package com.mygame.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygame.entities.Sprite;
import com.mygame.handlers.*;

import java.util.ArrayList;

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

		Pixmap pm = new Pixmap(Gdx.files.internal("images\\cursor.png"));
		Gdx.graphics.setCursor(Gdx.graphics.newCursor(pm, 2, 0));
		pm.dispose();

		assets = new Content();

		assets.loadTexture("images\\characters.png", "characters");
		assets.loadTexture("images\\treasures.png", "treasures");
		assets.loadTexture("images\\coin.png", "coin");
		assets.loadTexture("images\\arrow.png" , "arrow");
		assets.loadTexture("images\\dead.png", "dead");

		assets.loadTexture("images\\bodyManIdle.png", "bodyManIdle");
		assets.loadTexture("images\\bodyManRun.png", "bodyManRun");
		assets.loadTexture("images\\bowBackManIdle.png", "bowBackManIdle");
		assets.loadTexture("images\\bowBackManRun.png", "bowBackManRun");
		assets.loadTexture("images\\bowItem.png", "bow");
		assets.loadTexture("images\\bodyManBowDraw.png", "bodyManBowDraw");
		assets.loadTexture("images\\bowDrawManIdle.png", "bowDrawManIdle");
		assets.loadTexture("images\\bowDrawnManIdle.png", "bowDrawnManIdle");
		assets.loadTexture("images\\bowDrawnManRun.png", "bowDrawnManRun");
		assets.loadTexture("images\\bodyManBowPull.png", "bodyManBowPull");
		assets.loadTexture("images\\bowPullMan.png", "bowPullMan");
		assets.loadTexture("images\\gold.png", "gold");

		assets.loadSound("sfx\\hurt01.wav", "hurt01");
		assets.loadSound("sfx\\gold02.wav", "gold");
		assets.loadSound("sfx\\arrowImpact01.wav", "arrowImpact01");
		assets.loadSound("sfx\\bow01.mp3", "bow");
		assets.loadSound("sfx\\sword03.wav", "swordSwing");
		assets.loadSound("sfx\\arrowPickup.wav", "arrowPickup");
		assets.loadSound("sfx\\sea02.ogg", "sea02");
		assets.loadSound("sfx\\fire01.wav", "fire01");
		assets.loadSound("sfx\\walking.ogg", "walking");
		assets.loadSound("sfx\\walking02.ogg", "walking02");
		assets.loadSound("sfx\\pickup.wav", "pickup");
		assets.loadSound("sfx\\bowPull.wav", "bowPull");
		assets.loadSound("sfx\\bowDraw.ogg", "bowDraw");
		assets.loadSound("sfx\\goldDrop.ogg", "goldDrop");

		assets.loadParticleEffect("particles\\fire", "fire");

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
			gsm.update(STEP);
			gsm.render();
			MyInput.update();
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
		hudCam.setToOrtho(false, width, height);
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
