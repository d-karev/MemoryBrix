package com.dkarev.membrix.gameLogic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.TimeUtils;
import com.dkarev.membrix.MemoryBrixMain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Dobi on 6.6.2016 г..
 */
public class brickGrid implements Disposable, Json.Serializable {
    private List<brick> brickList;
    private Map<UUID, TextureRegion> regionIdMap;
    private gridCalculator calculator;

    private int occurence = 0;
    private int diversity = 0;

    private int brickRegionWidth = 0;
    private int brickRegionHeight = 0;

    private float worldHeight = 0;
    private float worldWidth = 0;

    private float gridWorldHeight = 0;
    private float gridWorldWidth = 0;

    private float gridWorldX = 0;
    private float gridWorldY = 0;

    private String texBricksPath = "";
    private String texFlippedPath = "";

    private Texture texBricks;
    private Texture texFlipped;
    private TextureRegion regFlipped;

    private int currentBrickIdx = 0;
    private int nextBrickIdx = 0;

    private boolean clickable = true;

    private int clicks = 0;

    private List<UUID> currentUUID = new ArrayList<UUID>();

    private long startTime = 0; //vremeto na suzdavane na tazi sesiq
    private long prevTime = 0; //akumulirano vreme preminalo v predishni sesii

    private boolean gameCompleted = false;

    public boolean gameIsCompleted() {
        return gameCompleted;
    }

    private scoreEntry gameResult = null;

    public scoreEntry getGameResult() {
        return gameResult;
    }

    private static final String gameStateFileName = "gridstate.brg";

    public brickGrid(
            String texBricksPath,
            String texFlippedPath,
            int occurence, int diversity,
            int brickRegHeight, int brickRegWidth,
            float wldHeight, float wldWidth,
            float gridWldHeight, float gridWldWidth,
            float gridWldX, float gridWldY) {

        this.occurence = occurence;
        this.diversity = diversity;

        brickRegionHeight = brickRegHeight;
        brickRegionWidth = brickRegWidth;

        worldHeight = wldHeight;
        worldWidth = wldWidth;

        gridWorldHeight = gridWldHeight;
        gridWorldWidth = gridWldWidth;

        gridWorldX = gridWldX;
        gridWorldY = gridWldY;

        this.texBricksPath = texBricksPath;
        this.texFlippedPath = texFlippedPath;

        initTextures();

        initRegionIdList();

        initCalculator();

        initBricksMap();

        initStartTime();

        Gdx.app.log(MemoryBrixMain.NAME, "brickGrid has been initialized!");
    }

    public brickGrid() {
        //constructor za json
    }

    private void initCalculator() {
        calculator = new gridCalculator(
                brickRegionHeight, brickRegionWidth,
                worldHeight, worldWidth,
                gridWorldHeight, gridWorldWidth,
                gridWorldX, gridWorldY,
                false, false);
    }

    private void initTextures() {
        texBricks = new Texture(texBricksPath);
        texFlipped = new Texture(texFlippedPath);
        regFlipped = TextureRegion.split(
                texFlipped,
                texFlipped.getWidth(),
                texFlipped.getHeight())[0][0];
    }

    private void initStartTime() {
        startTime = TimeUtils.millis();
    }

    public long getAllTime() {
        return prevTime + TimeUtils.millis() - startTime;
    }

    public static boolean hasSavedState() {
        FileHandle fh = Gdx.files.local(gameStateFileName);
        return fh.exists();
    }

    public void saveState() {
        if (gameCompleted) {
            Gdx.app.log(MemoryBrixMain.NAME, "Game has completed, nothing to save");
            return;
        }

        Gdx.app.log(MemoryBrixMain.NAME, "Saving grid state");

        prevTime += TimeUtils.millis() - startTime;
        startTime = 0;
        flipBricksDown();

        Json json = new Json();
        String text = json.toJson(this, brickGrid.class);

        FileHandle fh = Gdx.files.local(gameStateFileName);
        fh.writeString(text, false);
    }

    public static brickGrid getOldState() {
        brickGrid result = null;

        FileHandle fh = Gdx.files.local(gameStateFileName);
        Json json = new Json();
        result = json.fromJson(brickGrid.class, fh);

        result.initCalculator();
        result.initStartTime();

        return result;
    }

    public static void deleteOldState() {
        FileHandle fh = Gdx.files.local(gameStateFileName);
        if (fh.exists())
            fh.delete();
    }

    private void initBricksMap() {
        calculator.prepareToSetCoordinates(occurence * diversity);
        brickList = new ArrayList<brick>();

        List<UUID> idList = new ArrayList<UUID>(regionIdMap.keySet());
        Map<UUID, AtomicInteger> idOccurence = new HashMap<UUID, AtomicInteger>();

        for (UUID id : idList)
            idOccurence.put(id, new AtomicInteger());

        Random rndm = new Random();
        int currentIdx = 0;
        UUID currentId;

        while (calculator.prepareNextCoordinates()) {
            currentIdx = rndm.nextInt(idList.size());
            currentId = idList.get(currentIdx);

            brick b = new brick(
                    currentId,
                    calculator.getCurrentIdxX(), calculator.getCurrentIdxY(),
                    calculator.getCurrentX(), calculator.getCurrentY());

            brickList.add(b);

            if (idOccurence.get(currentId).incrementAndGet() == occurence) {
                idOccurence.remove(currentId);
                idList.remove(currentIdx);
            }
        }
    }

    private void initRegionIdList() {
        TextureRegion[][] regBricks = TextureRegion.split(
                texBricks,
                brickRegionWidth, brickRegionHeight);

        regionIdMap = new HashMap<UUID, TextureRegion>();
        int texRegCount = 0;

        for (int y = 0; y < regBricks.length; y++) {
            for (int x = 0; x < regBricks[y].length; x++) {

                regionIdMap.put(UUID.randomUUID(), regBricks[y][x]);

                texRegCount++;

                if (texRegCount == diversity)
                    return;
            }
        }
    }

    private void initRegionIdList(Map<UUID, IntPair> idMap) {
        TextureRegion[][] regBricks = TextureRegion.split(
                texBricks,
                brickRegionWidth, brickRegionHeight);

        regionIdMap = new HashMap<UUID, TextureRegion>();

        for (UUID id: idMap.keySet()) {
            IntPair pair = idMap.get(id);

            regionIdMap.put(
                    id,
                    regBricks[pair.y][pair.x]);
        }
    }

    public void prepareGridForRender() {
        currentBrickIdx = 0;
        nextBrickIdx = 0;
    }

    public boolean prepareNextGridElement() {
        currentBrickIdx = nextBrickIdx;
        nextBrickIdx++;
        return currentBrickIdx < brickList.size();
    }

    public TextureRegion getCurrentTextureRegion() {
        TextureRegion result = null;

        if (currentBrickIdx < brickList.size()) {
            brick b = brickList.get(currentBrickIdx);

            if (b.flipped)
                result = regFlipped;
            else
                result = regionIdMap.get(b.getTextureId());
        }

        return result;
    }

    public float getCurrentLocationX() {
        float result = 0;

        if (currentBrickIdx < brickList.size()) {
            brick b = brickList.get(currentBrickIdx);

            result = b.getPositionX();
        }

        return result;
    }

    public float getCurrentLocationY() {
        float result = 0;

        if (currentBrickIdx < brickList.size()) {
            brick b = brickList.get(currentBrickIdx);

            result = b.getPositionY();
        }

        return result;
    }

    public float getBrickHeight() {
        return calculator.getBrickWorldHeight();
    }

    public float getBrickWidth() {
        return calculator.getBrickWorldWidth();
    }

    public void handleTouchUp(float screenX, float screenY) {
        //Gdx.app.log(MemoryBrixMain.NAME, "Handling touch up event on grid");

        if (clickable) {
            for (brick b : brickList) {
                if (
                        b.clickable
                                && screenX >= b.getPositionX()
                                && screenX <= b.getPositionX() + getBrickWidth()
                                && screenY >= b.getPositionY()
                                && screenY <= b.getPositionY() + getBrickHeight()) {

                    clicks++;

                    if (currentUUID.size() == occurence) {
                        flipBricksDown();
                    }

                    b.flipped = false;
                    b.clickable = false;
                    currentUUID.add(b.getTextureId());

                    if (currentUUID.size() == occurence && equalIds()) {
                        deleteBricks(b.getTextureId());
                    }
                    break;
                }
            }

            if (brickList.size() == 0) {
                Gdx.app.log(MemoryBrixMain.NAME, "Game completed!");
                deleteOldState();

                gameResult = new scoreEntry(
                        getAllTime(),
                        clicks,
                        occurence,
                        diversity,
                        "");

                gameCompleted = true;
            }
        }
    }

    private void deleteBricks(UUID id) {
        for (int k = 0; k < occurence; k++) {
            for (brick b: brickList) {
                if (b.getTextureId().equals(id)) {
                    brickList.remove(b);
                    break;
                }
            }
        }
    }

    private void flipBricksDown() {
        for (brick b: brickList) {
            b.flipped = true;
            b.clickable = true;
        }

        currentUUID.clear();
    }

    private boolean equalIds() {
        boolean result = true;

        UUID first = currentUUID.get(0);

        for (int k = 1; k < currentUUID.size(); k++)
            if (!currentUUID.get(k).equals(first)) {
                result = false;
                break;
            }

        return result;
    }

    @Override
    public void dispose() {
        Gdx.app.log(MemoryBrixMain.NAME, "Disposing grid");
        texBricks.dispose();
        texFlipped.dispose();
    }

    @Override
    public void write(Json json) {
        json.writeValue("occurence", occurence);
        json.writeValue("diversity", diversity);

        json.writeValue("brickRegionWidth", brickRegionWidth);
        json.writeValue("brickRegionHeight", brickRegionHeight);

        json.writeValue("worldHeight", worldHeight);
        json.writeValue("worldWidth", worldHeight);

        json.writeValue("gridWorldHeight", gridWorldHeight);
        json.writeValue("gridWorldWidth", gridWorldWidth);

        json.writeValue("gridWorldX", gridWorldX);
        json.writeValue("gridWorldY", gridWorldY);

        json.writeValue("texBricksPath", texBricksPath);
        json.writeValue("texFlippedPath", texFlippedPath);

        json.writeValue("prevTime", prevTime);

        json.writeValue("clicks", clicks);

        json.writeValue("brickList", brickList, ArrayList.class, brick.class);

        Map<UUID, IntPair> idMap = new HashMap<UUID, IntPair>();

        for (UUID id: regionIdMap.keySet()) {
            TextureRegion reg = regionIdMap.get(id);
            int x = reg.getRegionX() / brickRegionWidth;
            int y = reg.getRegionY() / brickRegionHeight;
            idMap.put(id, new IntPair(x, y));
        }

        json.writeValue("regionIdMap", idMap, HashMap.class, IntPair.class);
    }

    @Override
    public void read(Json json, JsonValue jsonData) {

        for (JsonValue entry = jsonData.child; entry != null; entry = entry.next) {
            if (entry.name.equals("occurence")) {
                occurence = entry.asInt();
            } else if (entry.name.equals("diversity")) {
                diversity = entry.asInt();
            } else if (entry.name.equals("brickRegionWidth")) {
                brickRegionWidth = entry.asInt();
            } else if (entry.name.equals("brickRegionHeight")) {
                brickRegionHeight = entry.asInt();
            } else if (entry.name.equals("worldHeight")) {
                worldHeight = entry.asFloat();
            } else if (entry.name.equals("worldWidth")) {
                worldWidth = entry.asFloat();
            } else if (entry.name.equals("gridWorldHeight")) {
                gridWorldHeight = entry.asFloat();
            } else if (entry.name.equals("gridWorldWidth")) {
                gridWorldWidth = entry.asFloat();
            } else if (entry.name.equals("gridWorldX")) {
                gridWorldX = entry.asFloat();
            } else if (entry.name.equals("gridWorldY")) {
                gridWorldY = entry.asFloat();
            } else if (entry.name.equals("texBricksPath")) {
                texBricksPath = entry.asString();
            } else if (entry.name.equals("texFlippedPath")) {
                texFlippedPath = entry.asString();
            } else if (entry.name.equals("clicks")) {
                clicks = entry.asInt();
            } else if (entry.name.equals("prevTime")) {
                prevTime = entry.asLong();
            } else if (entry.name.equals("brickList")) {
                brickList = new ArrayList<brick>();
                JsonValue bricks = entry.child;

                for (JsonValue b = bricks; b != null; b = b.next) {
                    brick br = json.fromJson(brick.class, b.toString());
                    brickList.add(br);
                }
            } else if (entry.name.equals("regionIdMap")) {
                Map<UUID, IntPair> map = new HashMap<UUID, IntPair>();
                for (JsonValue b = entry.child; b != null; b = b.next) {
                    UUID id = UUID.fromString(b.name);

                    int x = 0;
                    int y = 0;

                    for (JsonValue p = b.child; p != null; p = p.next) {
                        if (p.name.equals("x")) {
                            x = p.asInt();
                        } else if (p.name.equals("y")) {
                            y = p.asInt();
                        }
                    }

                    IntPair pair = new IntPair(x, y);
                    map.put(id, pair);
                }
                initTextures();
                initRegionIdList(map);
            }
        }
    }

    private class IntPair {
        int x = 0;
        int y = 0;

        public IntPair(int X, int Y) {
            x = X;
            y = Y;
        }
    }
}
