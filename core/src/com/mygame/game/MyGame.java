package com.mygame.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;

import java.util.ArrayList;

public class MyGame extends ApplicationAdapter {
	private final Vector2 mouseInWorld2D = new Vector2();
	private final Vector3 mouseInWorld3D = new Vector3();

	private SpriteBatch batch;
	private World world;
	private Box2DDebugRenderer debugRenderer;
	private OrthographicCamera camera;

	private Player player;
	private ArrayList<GameObject> gameObjects;

	private boolean click = false;

	private float accumulator = 0;
	private float elapsedTime = 0;

	@Override
	public void create () {
		Box2D.init();
		world = new World(new Vector2(0, 0), true);
		world.setContactListener(new MyContactListener());
		batch = new SpriteBatch();
		debugRenderer = new Box2DDebugRenderer();

		float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight();
		camera = new OrthographicCamera();
		camera.setToOrtho(false, w / 2, h / 2);

		gameObjects = new ArrayList<GameObject>();
		player = new Player(0, 0, world);
	}

	@Override
	public void render () {
		elapsedTime += Gdx.graphics.getDeltaTime();
		update();

		Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		debugRenderer.render(world, camera.combined.scl(Constants.PPM));

		batch.begin();

		for(GameObject i : gameObjects) {
			batch.draw(i.getCurrentFrame(), i.getPosition().x, i.getPosition().y);
		}
		batch.draw(player.getCurrentFrame(), player.getPosition().x, player.getPosition().y);

		player.drawUI(batch);

		batch.end();

		doPhysicsStep(world, Gdx.graphics.getDeltaTime());
	}

	private void update() {
		cameraUpdate();
		player.move(elapsedTime);

		mouseEvents();

		player.update(elapsedTime);
		for (GameObject i : gameObjects) {
			i.update(elapsedTime);
		}
	}

	private void cameraUpdate() {
		camera.update();
		camera.position.set(player.getPosition().x, player.getPosition().y, 0);
		batch.setProjectionMatrix(camera.combined);

		mouseInWorld3D.x = Gdx.input.getX();
		mouseInWorld3D.y = Gdx.input.getY();
		mouseInWorld3D.z = 0;
		camera.unproject(mouseInWorld3D);
		mouseInWorld2D.x = mouseInWorld3D.x / Constants.PPM;
		mouseInWorld2D.y = mouseInWorld3D.y / Constants.PPM;

		if(Gdx.input.isKeyPressed(Input.Keys.UP)) {
			camera.zoom -= 0.02f;
		}
		if(Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
			camera.zoom += 0.02f;
		}
	}

	private void mouseEvents() {
		if(click && !Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
			click = false;

			//on click release
		}
		if(Gdx.input.isButtonPressed(Input.Buttons.LEFT) && !click) {
			click = true;

			//on click
			gameObjects.add(new Slime(mouseInWorld2D.x, mouseInWorld2D.y, world));
		}
	}

	private void doPhysicsStep(World world, float deltaTime) {
		float frameTime = Math.min(deltaTime, 0.25f);
		accumulator += frameTime;
		while(accumulator >= 1/60f) {
			world.step(1/60f, 24, 8);
			accumulator -= 1/60f;
		}
	}

	@Override
	public void resize(int width, int height) {
		camera.setToOrtho(false, width / 2, height / 2);
	}

	@Override
	public void dispose () {
		batch.dispose();
		world.dispose();
		debugRenderer.dispose();

		player.dispose();
		for (GameObject i: gameObjects) {
			i.dispose();
		}
	}
}
