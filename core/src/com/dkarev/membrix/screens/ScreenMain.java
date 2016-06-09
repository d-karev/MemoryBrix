package com.dkarev.membrix.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.dkarev.membrix.MemoryBrixMain;
import com.dkarev.membrix.gameLogic.brickGrid;

/**
 * Created by Dobi on 5.6.2016 г..
 */
public class ScreenMain implements Screen, InputProcessor {

    OrthographicCamera camera;
    SpriteBatch batch;

    private Game game;

    private Texture texButtons;
    private TextureRegion[][] texRegion;

    private final int buttonIndexNewGame = 0;
    private final int buttonIndexContinue = 1;
    private final int buttonIndexScore = 2;
    private final int buttonIndexOptions = 3;

    private int buttonRegionWidth = 888;
    private int buttonRegionHeight = 263;

    private final float worldWidth = 100;
    private float worldHeight = 0;

    private float buttonWidthToWorldWidth = (float)0.85;

    private float buttonWidth = 0;
    private float buttonHeight = 0;

    private float buttonPositionX = 0;
    private float[] buttonPositionY = new float[4];

    private float buttonVerticalPadding = (float)1.4;
    private float buttonVerticalPaddingFromBottom = 0;

    private boolean hasOldGame = false;

    private Texture background;
    private float backgroundDimension = 0;

    private Texture logo;
    private float logoWidth = 0;
    private float logoHeight = 0;

    public ScreenMain(Game game) {
        Gdx.app.log(MemoryBrixMain.NAME, "Entering main screen");

        this.game = game;

        init();
    }

    public ScreenMain(Game game, Screen previous) {
        Gdx.app.log(MemoryBrixMain.NAME, "Entering main screen");

        this.game = game;

        init();

        previous.dispose();
    }

    private void init() {
        float aspectRatio = (float)Gdx.graphics.getHeight() / (float)Gdx.graphics.getWidth();

        worldHeight = aspectRatio * worldWidth;
        backgroundDimension = worldWidth > worldHeight ? worldWidth : worldHeight;

        background = new Texture("background.png");
        logo = new Texture("logo.png");

        camera = new OrthographicCamera(worldWidth, worldHeight);
        camera.position.set(worldWidth / 2, worldHeight / 2, 0);

        texButtons = new Texture("buttons_main_menu_888x263.png");
        texRegion = TextureRegion.split(texButtons, buttonRegionWidth, buttonRegionHeight);

        buttonWidth = worldWidth * buttonWidthToWorldWidth;
        buttonHeight = buttonWidth * buttonRegionHeight / buttonRegionWidth;

        buttonPositionX = (worldWidth - buttonWidth) / 2;

        buttonVerticalPaddingFromBottom = worldHeight * (float)0.35;

        logoWidth = buttonWidth;
        logoHeight = (float)logo.getHeight() / (float)logo.getWidth() * logoWidth;

        batch = new SpriteBatch();
        Gdx.gl.glClearColor(0, 1, 0, 1);

        Gdx.input.setCatchBackKey(false);
        Gdx.input.setInputProcessor(this);
    }

    private void updateMenu() {
        hasOldGame = brickGrid.hasSavedState();

        buttonPositionY[buttonIndexNewGame] = buttonVerticalPaddingFromBottom;

        if (hasOldGame) {
            buttonPositionY[buttonIndexContinue] =
                    buttonVerticalPaddingFromBottom -
                            buttonHeight -
                            buttonVerticalPadding;
        }
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        updateMenu();
        camera.update();

        batch.begin();
        batch.setProjectionMatrix(camera.combined);

        batch.draw(background, 0, 0, backgroundDimension, backgroundDimension);
        batch.draw(
                logo,
                buttonPositionX,
                buttonPositionY[buttonIndexNewGame] + buttonHeight +
                    (worldHeight
                    - logoHeight
                    - buttonPositionY[buttonIndexNewGame]
                    - buttonHeight) / 2,
                logoWidth,
                logoHeight);

        batch.draw(
                texRegion[buttonIndexNewGame][0],
                buttonPositionX,
                buttonPositionY[buttonIndexNewGame],
                buttonWidth,
                buttonHeight);

        if (hasOldGame)
            batch.draw(
                    texRegion[buttonIndexContinue][0],
                    buttonPositionX,
                    buttonPositionY[buttonIndexContinue],
                    buttonWidth,
                    buttonHeight);

        batch.end();
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
        this.dispose();
    }

    @Override
    public void dispose() {
        Gdx.app.log(MemoryBrixMain.NAME, "Disposing Main screen!");
        batch.dispose();
        texButtons.dispose();
        background.dispose();
        logo.dispose();
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        boolean handled = false;

        Vector3 inputVector = new Vector3();
        Vector3 unprojected = camera.unproject(inputVector.set(screenX, screenY, 0));

        //proverka za shirina
        if (
                unprojected.x > buttonPositionX
                && unprojected.x < worldWidth - buttonPositionX) {

            // proverka za natisnati butoni
            if (
                    unprojected.y > buttonPositionY[buttonIndexNewGame]
                    && unprojected.y <= buttonPositionY[buttonIndexNewGame] + buttonHeight) {
                Gdx.app.log(MemoryBrixMain.NAME, "New game button - pressed!");
                game.setScreen(
                        new ScreenPlay(
                                game,
                                2, 20,
                                "bricks_food_x263.png", "bricks_flipped_x263.png",
                                263, 263));

                handled = true;

            } else if (
                    hasOldGame
                    && unprojected.y > buttonPositionY[buttonIndexContinue]
                    && unprojected.y <= buttonPositionY[buttonIndexContinue] + buttonHeight) {
                Gdx.app.log(MemoryBrixMain.NAME, "Continue button - pressed!");
                game.setScreen(new ScreenPlay(game));
                handled = true;
            }
        }

        return handled;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
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
