package com.mygame.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygame.handlers.*;

public class MyGame extends ApplicationAdapter {
	public static final String TITLE    = "Game";
	public static final int    V_WIDTH  = 640;
	public static final int    V_HEIGHT = 450;
	public static final int    SCALE    = 2;
	public static final int    FPS      = 60;
	public static final float  STEP     = 1.f/FPS;

	private float accumulator;

	private SpriteBatch sb;
	private OrthographicCamera cam;
	private OrthographicCamera hudCam;

	private GameStateManager gsm;

	public static Content assets;

	@Override
	public void create () {
		accumulator = 0;

		Gdx.input.setInputProcessor(new MyInputProcessor());

		assets = new Content();

		assets.loadTexture("images\\characters.png", "characters");
		assets.loadTexture("images\\sword.png", "sword");
		assets.loadTexture("images\\grass.jpg", "background");
		assets.loadTexture("images\\treasures.png", "treasures");
		assets.loadTexture("images\\coin.png", "coin");
		assets.loadTexture("images\\hud.png", "hud");

		assets.loadSound("sfx\\sword02.wav", "sword01");
		assets.loadSound("sfx\\hurt01.wav", "hurt01");
		assets.loadSound("sfx\\gold02.wav", "gold");

		sb = new SpriteBatch();
		cam = new OrthographicCamera();
		cam.setToOrtho(false, V_WIDTH, V_HEIGHT);
		hudCam = new OrthographicCamera();
		hudCam.setToOrtho(false, V_WIDTH, V_HEIGHT);

		gsm = new GameStateManager(this);
	}

	@Override
	public void render () {
		accumulator += Gdx.graphics.getDeltaTime();
		while(accumulator >= STEP) {
			accumulator -= STEP;
			MyInput.update();
			gsm.update(STEP);
			gsm.render();
		}
	}

	@Override
	public void dispose () {
		sb.dispose();

		while(!gsm.isEmpty()) {
			gsm.popState();
		}

		assets.disposeAll();
	}

	@Override
	public void resize(int width, int height) {
		cam.setToOrtho(false, width / SCALE, height / SCALE);
	}

	public SpriteBatch getSpriteBatch() {
		return sb;
	}

	public OrthographicCamera getCam() {
		return cam;
	}

	public OrthographicCamera getHudCam() {
		return hudCam;
	}
}
