package com.mygame.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.mygame.entities.Slime;
import com.mygame.entities.Sprite;
import com.mygame.handlers.Constants;
import com.mygame.handlers.GameStateManager;
import com.mygame.handlers.MyContactListener;
import com.mygame.interfaces.Attackable;

import java.util.ArrayList;

public class Play extends GameState {
    private World world;
    private Box2DDebugRenderer b2dr;

    private ArrayList<Sprite> gameObjects;

    private final Vector2 mousePosition = new Vector2();

    private boolean click = false;

    private BitmapFont font;

    public Play(GameStateManager gsm) {
        super(gsm);

        world = new World(new Vector2(0, 0), true);
        world.setContactListener(new MyContactListener());
        b2dr = new Box2DDebugRenderer();

        gameObjects = new ArrayList<Sprite>();
        gameObjects.add(new com.mygame.entities.Player(world, 0, 0));

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts\\PressStart2P.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 5;
        parameter.color = Color.GREEN;
        font = generator.generateFont(parameter);
        font.setUseIntegerPositions(false);

        generator.dispose();
    }

    @Override
    public void update(float dt) {
        handleInput();

        world.step(dt, 6, 2);

        for(Sprite i : gameObjects) {
            i.update(dt);
        }
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        cameraUpdate();

        b2dr.render(world, cam.combined.scl(Constants.PPM));

        for(Sprite i : gameObjects) {
            i.render(sb);
        }

        for(Sprite i : gameObjects) {
            if(i instanceof Attackable) {
                String hp = Integer.toString(((Attackable) i).getHp());
                Vector2 position = new Vector2(i.getPosition().x * Constants.PPM - 8.f, i.getPosition().y * Constants.PPM + 16.f);
                sb.begin();
                font.draw(sb, hp, position.x, position.y);
                sb.end();
            }
        }
    }

    @Override
    public void handleInput() {
        if(click && !Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
            click = false;

            //on click release
        }
        if(Gdx.input.isButtonPressed(Input.Buttons.LEFT) && !click) {
            click = true;

            //on click
        }

        if(Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
            gameObjects.add(new Slime(mousePosition.x, mousePosition.y, world));
        }
    }

    private void cameraUpdate() {
        cam.update();
        cam.position.set(gameObjects.get(0).getPosition().x * Constants.PPM, gameObjects.get(0).getPosition().y * Constants.PPM, 0);
        sb.setProjectionMatrix(cam.combined);

        Vector3 mouseInWorld3D = new Vector3();
        mouseInWorld3D.x = Gdx.input.getX();
        mouseInWorld3D.y = Gdx.input.getY();
        mouseInWorld3D.z = 0;
        cam.unproject(mouseInWorld3D);
        mousePosition.x = mouseInWorld3D.x;
        mousePosition.y = mouseInWorld3D.y;

        if(Gdx.input.isKeyPressed(Input.Keys.UP)) {
            cam.zoom -= 0.02f;
        }
        if(Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            cam.zoom += 0.02f;
        }
    }

    @Override
    public void dispose() {
        world.dispose();
        b2dr.dispose();
    }

}
