package com.dkarev.membrix.gameLogic;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

import java.util.UUID;

/**
 * Created by Dobi on 6.6.2016 г..
 */
public class brick implements Json.Serializable {

    private UUID textureId;

    public UUID getTextureId() {
        return textureId;
    }

    private int indexX = 0;
    private int indexY = 0;

    public int getIndexX() {
        return indexX;
    }

    public int getIndexY() {
        return indexY;
    }

    private float positionX = 0;
    private float positionY = 0;

    public float getPositionX() {
        return positionX;
    }

    public float getPositionY() {
        return positionY;
    }

    //kogato e true, bricka e s gurba nagore
    public boolean flipped = true;

    public boolean clickable = true;

    public brick(UUID texId, int idxX, int idxY, float posX, float posY) {
        textureId = texId;
        indexX = idxX;
        indexY = idxY;
        positionX = posX;
        positionY = posY;
    }

    public brick() {
        //construktor za json
    }

    @Override
    public void write(Json json) {
        json.writeValue("textureId", textureId.toString());
        json.writeValue("indexX", indexX);
        json.writeValue("indexY", indexY);
        json.writeValue("positionX", positionX);
        json.writeValue("positionY", positionY);
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        for (JsonValue entry = jsonData.child; entry != null; entry = entry.next) {
            if (entry.name.equals("textureId")) {
                textureId = UUID.fromString(entry.asString());
            } else if (entry.name.equals("indexX")) {
                indexX = entry.asInt();
            } else if (entry.name.equals("indexY")) {
                indexY = entry.asInt();
            } else if (entry.name.equals("positionX")) {
                positionX = entry.asInt();
            } else if (entry.name.equals("positionY")) {
                positionY = entry.asInt();
            }
        }
    }
}
