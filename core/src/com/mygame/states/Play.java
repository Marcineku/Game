package com.mygame.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.mygame.entities.Player;
import com.mygame.entities.Slime;
import com.mygame.entities.Sprite;
import com.mygame.game.MyGame;
import com.mygame.handlers.Constants;
import com.mygame.handlers.GameStateManager;
import com.mygame.handlers.MyContactListener;
import com.mygame.handlers.MyInput;
import com.mygame.interfaces.Attackable;

import java.util.ArrayList;
import java.util.Iterator;

public class Play extends GameState {
    private World world;
    private Box2DDebugRenderer b2dr;

    private ArrayList<Sprite> gameObjects;

    private Vector2 mousePosition;

    private boolean click = false;

    private BitmapFont font;

    private Player player;

    public static boolean debug = false;

    public Play(GameStateManager gsm) {
        super(gsm);

        mousePosition = new Vector2();

        world = new World(new Vector2(0, 0), true);
        world.setContactListener(new MyContactListener());
        b2dr = new Box2DDebugRenderer();

        gameObjects = new ArrayList<Sprite>();
        player = new Player(world, 0, 0);
        gameObjects.add(player);

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
        Gdx.graphics.setTitle("FPS: " + Gdx.graphics.getFramesPerSecond() + " OBJ: " + gameObjects.size());

        handleInput();

        //moving slimes towards the player
        for(Sprite i : gameObjects) {
            if(i.toString().equals("slime")) {
                Slime s = (Slime) i;
                if(s.getState() != Slime.SlimeStates.DEAD) {
                    if(player.getState() != Player.PlayerStates.DEAD) {
                        i.getBody().setLinearVelocity(player.getBody().getPosition().x - i.getBody().getPosition().x, player.getBody().getPosition().y - i.getBody().getPosition().y);
                    }
                }
                else {
                    i.getBody().setLinearVelocity(i.getBody().getPosition().x - player.getBody().getPosition().x, i.getBody().getPosition().y - player.getBody().getPosition().y);
                }
            }
        }

        //removing dead slimes
        for(Iterator<Sprite> i = gameObjects.iterator(); i.hasNext();) {
            Sprite s = i.next();
            if(s.toString().equals("slime")) {
                Slime slime = (Slime) s;
                if(slime.getState() == Slime.SlimeStates.DEAD) {
                    if(slime.getCurrentAnimation().getTimesPlayed() > 20) {
                        world.destroyBody(slime.getBody());
                        i.remove();
                    }
                }
            }
        }

        for(Iterator<Sprite> i = gameObjects.iterator(); i.hasNext();) {
            Sprite s = i.next();
            if(s.toString().equals("player")) {
                Player p = (Player) s;
                if(p.getState() == Player.PlayerStates.DEAD && MyInput.isDown(MyInput.RESET)) {
                    player.reset();
                }
            }
        }

        world.step(dt, 6, 2);

        //updating all game objects
        for(Sprite i : gameObjects) {
            i.update(dt);
        }
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        cameraUpdate();

        sb.begin();
        Texture bg = MyGame.assets.getTexture("background");
        sb.draw(bg, -bg.getWidth() / 2, -bg.getHeight() / 2);
        sb.end();

        if(debug) {
            b2dr.render(world, cam.combined.scl(Constants.PPM));
        }

        for(Sprite i : gameObjects) {
            i.render(sb);
        }

        sb.begin();
        for(Sprite i : gameObjects) {
            if(i instanceof Attackable) {
                String hp = Integer.toString(((Attackable) i).getHp());
                Vector2 position = new Vector2(((Attackable) i).getHpBarPosition().x, ((Attackable) i).getHpBarPosition().y);
                font.draw(sb, hp, position.x, position.y);
            }
        }
        sb.end();
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

        //on button pressed
        if(MyInput.isDown(MyInput.SLIME)) {
            gameObjects.add(new Slime(mousePosition.x, mousePosition.y, world));
        }

        //rotating player's body towards mouse cursor
        Body body = gameObjects.get(0).getBody();
        Vector2 toTarget = new Vector2(mousePosition.x / Constants.PPM - body.getPosition().x, mousePosition.y / Constants.PPM - body.getPosition().y);
        float desiredAngle = (float) Math.atan2(-toTarget.x, toTarget.y) + (float) Math.toRadians(45) + (float) Math.toRadians(37.5);
        body.setTransform(body.getPosition(), desiredAngle);
    }

    private void cameraUpdate() {
        cam.update();
        cam.position.set(gameObjects.get(0).getBody().getPosition().x * Constants.PPM, gameObjects.get(0).getBody().getPosition().y * Constants.PPM, 0);
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
