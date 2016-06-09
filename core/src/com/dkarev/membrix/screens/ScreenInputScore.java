package com.dkarev.membrix.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.dkarev.membrix.MemoryBrixMain;
import com.dkarev.membrix.gameLogic.scoreEntry;
import com.dkarev.membrix.gameLogic.scoring;

/**
 * Created by Dobi on 8.6.2016 г..
 */
public class ScreenInputScore implements Screen, InputProcessor {

    OrthographicCamera camera;
    SpriteBatch batch;

    private final float worldWidth = 500;
    private float worldHeight = 0;
    private float textVerticalPadding = 50;

    private scoreEntry entry;
    private Game game;

    FreeTypeFontGenerator generator;
    BitmapFont font;
    GlyphLayout glyphLayout = new GlyphLayout();

    private final static String strYouFinished = "You finished the game!";
    private final static String strYourTime = "Your time: ";
    private final static String strYourClicks = "Clicks: ";
    private final static String strFinalScore = "Final score: ";

    private Texture background;
    private float backgroundDimension = 0;

    public ScreenInputScore(Game game, scoreEntry entry) {
        Gdx.app.log(MemoryBrixMain.NAME, "Entering score screen");

        this.game = game;

        this.entry = entry;

        float aspectRatio = (float)Gdx.graphics.getHeight() / (float)Gdx.graphics.getWidth();

        worldHeight = aspectRatio * worldWidth;
        backgroundDimension = worldWidth > worldHeight ? worldWidth : worldHeight;

        camera = new OrthographicCamera(worldWidth, worldHeight);
        camera.position.set(worldWidth / 2, worldHeight / 2, 0);

        generator = new FreeTypeFontGenerator(Gdx.files.internal("OpenSans-Semibold.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 36;
        parameter.color = Color.BLACK;

        font = generator.generateFont(parameter);

        background = new Texture("background.png");

        batch = new SpriteBatch();
        Gdx.gl.glClearColor(0, 1, 0, 1);

        Gdx.input.setCatchBackKey(true);
        Gdx.input.setInputProcessor(this);
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

        String text = strYouFinished;
        glyphLayout.setText(font, text);
        float w = glyphLayout.width;
        font.draw(batch, text, worldWidth / 2 - w / 2, worldHeight / 2 + 100);

        text = strYourClicks + entry.getClicks();
        glyphLayout.setText(font, text);
        w = glyphLayout.width;
        font.draw(batch, text, worldWidth / 2 - w / 2, worldHeight / 2 + 50);

        text = strYourTime + (entry.getElapsedTime() / 1000) + "s";
        glyphLayout.setText(font, text);
        w = glyphLayout.width;
        font.draw(batch, text, worldWidth / 2 - w / 2, worldHeight / 2);

        text = strFinalScore + Math.round(scoring.calculateScore(entry));
        glyphLayout.setText(font, text);
        w = glyphLayout.width;
        font.draw(batch, text, worldWidth / 2 - w / 2, worldHeight / 2 - 50);

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
        dispose();
    }

    @Override
    public void dispose() {
        Gdx.app.log(MemoryBrixMain.NAME, "Disposing score screen!");
        batch.dispose();
        font.dispose();
        generator.dispose();
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
