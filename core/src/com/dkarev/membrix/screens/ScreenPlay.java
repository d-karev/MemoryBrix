package com.dkarev.membrix.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.dkarev.membrix.MemoryBrixMain;
import com.dkarev.membrix.gameLogic.brickGrid;

/**
 * Created by Dobi on 6.6.2016 г..
 */
public class ScreenPlay implements Screen, InputProcessor {

    private Game game;

    private OrthographicCamera camera;
    private SpriteBatch batch;

    private int occurence = 0;
    private int diversity = 0;
    private String textureBrickPath = "";
    private String textureFlippedPath = "";

    private int brickRegionWidth = 0;
    private int brickRegionHeight = 0;

    private final float worldWidth = 100;
    private float worldHeight = 0;

    private float gridX = 0;
    private float gridY = 0;

    private float gridWidth = 0;
    private float gridHeight = 0;

    private brickGrid grid;

    private Texture background;
    private float backgroundDimension = 0;

    public ScreenPlay(
            Game game,
            int occurence,
            int diversity,
            String texBrPath,
            String texFlPath,
            int brickRegHeight,
            int brickRegWidth) {
        Gdx.app.log(MemoryBrixMain.NAME, "Entering play screen");

        this.game = game;

        this.occurence = occurence;
        this.diversity = diversity;
        textureBrickPath = texBrPath;
        textureFlippedPath = texFlPath;

        brickRegionHeight = brickRegHeight;
        brickRegionWidth = brickRegWidth;

        init();

        grid = new brickGrid(
                textureBrickPath,
                textureFlippedPath,
                occurence,
                diversity,
                brickRegionHeight, brickRegionWidth,
                worldHeight, worldWidth,
                gridHeight, gridWidth,
                gridX, gridY);

        Gdx.app.log(MemoryBrixMain.NAME, "Play screen has been initialized to a new game!");
    }

    public ScreenPlay(Game game) {
        this.game = game;
        init();
        grid = brickGrid.getOldState();
        Gdx.app.log(MemoryBrixMain.NAME, "Play screen has been initialized from a previous grid!");
    }

    private void init() {
        float aspectRatio = (float)Gdx.graphics.getHeight() / (float)Gdx.graphics.getWidth();

        worldHeight = aspectRatio * worldWidth;

        backgroundDimension = worldWidth > worldHeight ? worldWidth : worldHeight;

        gridWidth = worldWidth * (float)0.95;
        gridHeight = worldHeight * (float)1;

        gridX = (worldWidth - gridWidth) / 2;
        gridY = worldHeight - gridHeight - gridX;

        camera = new OrthographicCamera(worldWidth, worldHeight);
        camera.position.set(worldWidth / 2, worldHeight / 2, 0);

        background = new Texture("background.png");

        batch = new SpriteBatch();
        Gdx.gl.glClearColor(0, 1, 0, 1);

        Gdx.input.setInputProcessor(this);
        Gdx.input.setCatchBackKey(true);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();

        batch.begin();
        batch.setProjectionMatrix(camera.combined);

        batch.draw(background, 0, 0, backgroundDimension, backgroundDimension);

        grid.prepareGridForRender();
        while (grid.prepareNextGridElement()) {
            batch.draw(
                    grid.getCurrentTextureRegion(),
                    grid.getCurrentLocationX(), grid.getCurrentLocationY(),
                    grid.getBrickWidth(), grid.getBrickHeight());
        }

        batch.end();

        if (grid.gameIsCompleted()) {
            game.setScreen(new ScreenInputScore(game, grid.getGameResult()));
        }
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {
        grid.saveState();
        this.dispose();
    }

    @Override
    public void dispose() {
        Gdx.app.log(MemoryBrixMain.NAME, "Disposing Play screen!");
        grid.dispose();
        batch.dispose();
        background.dispose();
    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        boolean result = false;

        if (keycode == Input.Keys.BACK) {
            result = true;
            game.setScreen(new ScreenMain(game));
        }

        return result;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        boolean handled = false;

        Vector3 inputVector = new Vector3();
        Vector3 unprojected = camera.unproject(inputVector.set(screenX, screenY, 0));

        grid.handleTouchUp(unprojected.x, unprojected.y);

        return handled;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
