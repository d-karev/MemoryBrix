package com.dkarev.membrix;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.dkarev.membrix.screens.ScreenMain;

public class MemoryBrixMain extends Game {

	public static final String NAME = "MemoryBrix";

	@Override
	public void create () {
		Gdx.app.log(NAME, "Entering create()");
		setScreen(new ScreenMain(this));
	}

	@Override
	public void render () {
		super.render();
	}

	@Override
	public void resize (int width, int height) {
		Gdx.app.log(NAME, "Resizing to " + width + "x" + height);
		super.resize(width, height);
	}

	@Override
	public void pause() {
		Gdx.app.log(NAME, "pausing");
		super.pause();
	}

	@Override
	public void resume () {
		Gdx.app.log(NAME, "resuming");
		super.resume();
		setScreen(new ScreenMain(this));
	}

	@Override
	public void dispose () {
		Gdx.app.log(NAME, "Disposing game content");
		super.dispose();
	}
}
