package com.mygame.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;
import com.mygame.entities.*;
import com.mygame.game.MyGame;
import com.mygame.handlers.Constants;
import com.mygame.handlers.GameStateManager;
import com.mygame.handlers.MyContactListener;
import com.mygame.handlers.MyInput;
import com.mygame.interfaces.Attackable;
import com.mygame.interfaces.Lootable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

public class Play extends GameState {
    private World world;
    private Box2DDebugRenderer b2dr;

    private ArrayList<Sprite> gameObjects;

    private Vector2 mousePosition;
    private boolean click;

    private BitmapFont font;

    private Player player;

    private Hud hud;

    private TiledMap tileMap;
    private OrthogonalTiledMapRenderer tmr;
    float tileSize;

    public static boolean debug = true;

    public Play(GameStateManager gsm) {
        super(gsm);

        mousePosition = new Vector2();
        click = false;

        world = new World(new Vector2(0, 0), true);
        world.setContactListener(new MyContactListener());
        b2dr = new Box2DDebugRenderer();

        gameObjects = new ArrayList<Sprite>();
        player = new Player(world, 100 * Constants.PPM, 100 * Constants.PPM);
        gameObjects.add(player);

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts\\PressStart2P.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 5;
        parameter.color = Color.GREEN;
        font = generator.generateFont(parameter);
        font.setUseIntegerPositions(false);

        generator.dispose();

        hud = new Hud(player);

        tileMap = new TmxMapLoader().load("maps\\Test.tmx");
        tmr = new OrthogonalTiledMapRenderer(tileMap);

        TiledMapTileLayer layer = (TiledMapTileLayer) tileMap.getLayers().get("water");
        tileSize = layer.getTileWidth();

        for(int row = 29; row < layer.getHeight(); ++row) {
            for(int col = 23; col < layer.getWidth(); ++col) {
                TiledMapTileLayer.Cell cell = layer.getCell(col, row);

                if(cell == null) continue;
                if(cell.getTile() == null) continue;

                BodyDef bdef = new BodyDef();
                bdef.type = BodyDef.BodyType.StaticBody;
                bdef.position.set((col + 0.5f) * tileSize / Constants.PPM, (row + 0.5f) * tileSize / Constants.PPM);

                PolygonShape shape = new PolygonShape();
                shape.setAsBox(8 / Constants.PPM, 8 / Constants.PPM);

                FixtureDef fdef = new FixtureDef();
                fdef.friction = 0;
                fdef.shape = shape;
                fdef.isSensor = false;
                world.createBody(bdef).createFixture(fdef);
                shape.dispose();

                if(col > 71) break;
            }
            if(row > 72) break;
        }
    }

    @Override
    public void update(float dt) {
        Gdx.graphics.setTitle("FPS: " + Gdx.graphics.getFramesPerSecond() + " OBJ: " + gameObjects.size());

        handleInput();

        //moving slimes towards the player
        for(Sprite i : gameObjects) {
            if(i.toString().equals("slime")) {
                Slime s = (Slime) i;
                if(s.getAttackableState() == Attackable.AttackableState.ALIVE) {
                    if(player.getAttackableState() == Attackable.AttackableState.ALIVE) {
                        i.getBody().setLinearVelocity(player.getBody().getPosition().x - i.getBody().getPosition().x, player.getBody().getPosition().y - i.getBody().getPosition().y);
                    }
                }
                else {
                    i.getBody().setLinearVelocity(i.getBody().getPosition().x - player.getBody().getPosition().x, i.getBody().getPosition().y - player.getBody().getPosition().y);
                }
            }
        }

        //removing dead slimes, dropping loot and removing looted loot
        ArrayList<Loot> lootToDrop = new ArrayList<Loot>();
        for(Iterator<Sprite> i = gameObjects.iterator(); i.hasNext();) {
            Sprite s = i.next();
            if(s instanceof Attackable) {
                if(((Attackable) s).getAttackableState() == Attackable.AttackableState.DEAD) {
                    if(s.getCurrentAnimation().getTimesPlayed() > 20) {
                        world.destroyBody(s.getBody());
                        i.remove();
                    }
                    if(s instanceof Lootable) {
                        if(!((Lootable) s).isLooted()) {
                            lootToDrop.add(new Loot(world, s.getPosition().x * Constants.PPM, s.getPosition().y * Constants.PPM, ((Lootable) s).getGold()));
                            ((Lootable) s).setLooted(true);
                        }
                    }
                }
            }
            if(s.toString().equals("loot")) {
                Loot loot = (Loot) s;
                if(loot.isLooted()) {
                    world.destroyBody(loot.getBody());
                    i.remove();
                }
            }
            if(s.toString().equals("arrow")) {
                Arrow arrow = (Arrow) s;
                if(arrow.isLooted()) {
                    world.destroyBody(arrow.getBody());
                    i.remove();
                }
            }
        }
        gameObjects.addAll(lootToDrop);

        //resetting player after death and when he pressed reset button
        if(player.getAttackableState() == Attackable.AttackableState.DEAD && MyInput.isDown(MyInput.RESET)) {
            player.reset();
        }

        //stepping physics simulation
        world.step(dt, 6, 2);

        //updating all game objects
        for(Sprite i : gameObjects) {
            i.update(dt);
        }
    }

    @Override
    public void render() {
        //clearing screen
        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        //updating camera
        cameraUpdate();

        //rendering tiled map
        tmr.setView(cam);
        tmr.render();

        //rendering physics engine hitboxes
        if(debug) {
            b2dr.render(world, cam.combined.scl(Constants.PPM));
        }

        //rendering all game objects
        Collections.sort(gameObjects);
        for(Sprite i : gameObjects) {
            i.render(sb);
        }

        //rendering Attackable hp bars
        sb.begin();
        for(Sprite i : gameObjects) {
            if(i instanceof Attackable) {
                String hp = Integer.toString(((Attackable) i).getHp());
                Vector2 position = new Vector2(((Attackable) i).getHpBarPosition().x, ((Attackable) i).getHpBarPosition().y);
                font.draw(sb, hp, position.x, position.y);
            }
        }
        sb.end();

        //rendering hud
        sb.setProjectionMatrix(hudCam.combined);
        hud.render(sb);
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
            //shooting arrows
            if(player.getAttackableState() == Attackable.AttackableState.ALIVE && !player.isArrowsEmpty()) {
                player.shoot();
                Arrow arrow = new Arrow(world, player.getPosition().x, player.getPosition().y);
                arrow.getBody().setTransform(
                        player.getBody().getPosition().x,
                        player.getBody().getPosition().y,
                        player.getBody().getAngle() + (float) Math.toRadians(100f)
                );
                arrow.getBody().setLinearVelocity(
                        -arrow.getBody().getPosition().x * Constants.PPM + mousePosition.x, -arrow.getBody().getPosition().y * Constants.PPM + mousePosition.y
                );
                gameObjects.add(arrow);
            }
        }

        //on button pressed
        //spawning slimes
        if(MyInput.isDown(MyInput.SLIME)) {
            gameObjects.add(new Slime(mousePosition.x, mousePosition.y, world));
        }

        //rotating player's body towards mouse cursor
        if(player.getAttackableState() == Attackable.AttackableState.ALIVE) {
            Body body = player.getBody();
            Vector2 toTarget = new Vector2(mousePosition.x / Constants.PPM - body.getPosition().x, mousePosition.y / Constants.PPM - body.getPosition().y);
            float desiredAngle = (float) Math.atan2(-toTarget.x, toTarget.y) + (float) Math.toRadians(45) + (float) Math.toRadians(37.5);
            body.setTransform(body.getPosition(), desiredAngle);
        }
    }

    private void cameraUpdate() {
        cam.update();
        cam.position.set(player.getBody().getPosition().x * Constants.PPM, player.getBody().getPosition().y * Constants.PPM, 0);
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
        font.dispose();
        hud.dispose();
    }
}
