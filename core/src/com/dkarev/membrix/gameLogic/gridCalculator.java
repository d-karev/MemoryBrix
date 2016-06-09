package com.dkarev.membrix.gameLogic;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * Created by Dobi on 6.6.2016 г..
 */
public class gridCalculator {

    private boolean basedOnWidth = true;

    private int brickTextureHeight = 0;
    private int brickTextureWidth = 0;

    private float worldHeight = 0;
    private float worldWidth = 0;

    private float gridWorldHeight = 0;
    private float gridWorldWidth = 0;

    private float gridWorldX = 0;
    private float gridWorldY = 0;

    private static final float percentPadding = (float)0.02;
    private static final int minInline = 5;

    private float padding = 0;

    private float brickWorldHeight = 0;
    private float brickWorldWidth = 0;

    public float getBrickWorldHeight() {
        return brickWorldHeight;
    }

    public float getBrickWorldWidth() {
        return brickWorldWidth;
    }

    private int maxRows = 0;
    private int maxColumns = 0;

    private int occupiedRows = 0;
    private int occupiedColumns = 0;
    private int occupiedCells = 0;

    private float offsetHorizontal = 0;
    private float offsetVertical = 0;

    private boolean useOffsetHor = false;
    private boolean useOffsetVer = false;

    private float currentX = 0;
    private float currentY = 0;

    public float getCurrentX() {
        return currentX;
    }

    public float getCurrentY() {
        return currentY;
    }

    private int currentIdxX = 0;
    private int currentIdxY = 0;

    public int getCurrentIdxX() {
        return currentIdxX;
    }

    public int getCurrentIdxY() {
        return currentIdxY;
    }

    private int nextIdxX = 0;
    private int nextIdxY = 0;

    private int currentCellCount = 0;

    public int getMaxBricksOnScreen() {
        return maxColumns * maxRows;
    }

    public gridCalculator(
            int brickTexHeight, int brickTexWidth,
            float wldHeight, float wldWidth,
            float gridWldHeight, float gridWldWidth,
            float gridWldX, float gridWldY,
            boolean offsetHor, boolean offsetVer) {

        brickTextureHeight = brickTexHeight;
        brickTextureWidth = brickTexWidth;
        worldHeight = wldHeight;
        worldWidth = wldWidth;
        gridWorldHeight = gridWldHeight;
        gridWorldWidth = gridWldWidth;
        gridWorldX = gridWldX;
        gridWorldY = gridWldY;
        useOffsetHor = offsetHor;
        useOffsetVer = offsetVer;

        basedOnWidth = gridWldHeight >= gridWldWidth;

        if (basedOnWidth)
            initByWidth();
        else
            initByHeight();
    }

    private void initByHeight() {
        padding = worldHeight * percentPadding;
        // TODO init by height
        throw new NotImplementedException();
    }

    private void initByWidth() {
        padding = worldWidth * percentPadding;

        brickWorldWidth = (gridWorldWidth - (minInline - 1) * padding) / minInline;
        brickWorldHeight = brickTextureHeight / brickTextureWidth * brickWorldWidth;

        float temp = 0;
        int tempCount = 0;

        while (temp < gridWorldHeight) {
            if (temp <= gridWorldHeight - brickWorldHeight)
                tempCount++;

            temp += brickWorldHeight + padding;
        }

        maxRows = tempCount;
        maxColumns = minInline;
    }

    private void calculateOffset() {
        if (basedOnWidth) {
            offsetHorizontal = 0;
            offsetVertical =
                    (gridWorldHeight
                    - occupiedRows * brickWorldHeight
                    - (occupiedRows - 1) * padding)
                    / 2;
        } else {
            // TODO offset by width
            offsetVertical = 0;
            throw new NotImplementedException();
        }
    }

    public void prepareToSetCoordinates(int occupiedGridCells) {
        if (occupiedGridCells > getMaxBricksOnScreen())
            throw new IndexOutOfBoundsException();

        occupiedCells = occupiedGridCells;

        if (occupiedCells < maxColumns) {
            occupiedColumns = occupiedCells;
            occupiedRows = 1;
        } else {
            occupiedColumns = maxColumns;

            Double temp = Math.ceil(occupiedCells / occupiedColumns);
            occupiedRows =  temp.intValue();
        }

        calculateOffset();

        currentX = 0;
        currentY = 0;
        currentIdxX = 0;
        currentIdxY = 0;
        nextIdxX = 0;
        nextIdxY = 0;
        currentCellCount = 1;
    }

    public boolean prepareNextCoordinates() {
        boolean result = false;

        if (currentCellCount <= occupiedCells) {
            result = true;

            currentIdxX = nextIdxX;
            currentIdxY = nextIdxY;

            currentX =
                    (useOffsetHor ? offsetHorizontal : 0)
                    + gridWorldX
                    + currentIdxX * brickWorldWidth
                    + currentIdxX * padding;

            currentY =
                    gridWorldY
                    + gridWorldHeight
                    - (currentIdxY + 1) * brickWorldHeight
                    - currentIdxY * padding
                    - (useOffsetVer ? offsetVertical : 0);

            if (currentIdxX < occupiedColumns - 1)
                nextIdxX++;
            else {
                nextIdxX = 0;
                nextIdxY++;
            }

            currentCellCount++;
        }

        return result;
    }
}
