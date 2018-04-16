package com.mygame.states;

import box2dLight.PointLight;
import box2dLight.RayHandler;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
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
    private RayHandler rayHandler;
    private PointLight pointLight;
    private ParticleEffect fire;
    private Sound fireSound;
    private long fireSoundID;
    private Cursor cursor;

    private ArrayList<Sprite> gameObjects;

    private boolean click;
    private boolean spawn;

    private BitmapFont hpBarFont;
    private BitmapFont itemNameFont;

    private Player player;

    private Hud hud;

    private TiledMap tileMap;
    private OrthogonalTiledMapRenderer tmr;
    private float tileSize;

    public static boolean debug = true;

    public Play(GameStateManager gsm) {
        super(gsm);

        click = false;
        spawn = false;

        world = new World(new Vector2(0, 0), true);
        world.setContactListener(new MyContactListener());
        b2dr = new Box2DDebugRenderer();
        rayHandler = new RayHandler(world);
        cursor = new Cursor(world);

        rayHandler.setAmbientLight(0.2f);
        rayHandler.setBlur(true);

        pointLight = new PointLight(rayHandler, 200, Color.RED, 20.f, 95, 100);
        pointLight.setSoftnessLength(0.f);
        Filter filter = new Filter();
        filter.maskBits = Constants.BIT_PLAYER | Constants.BIT_ENEMY;
        pointLight.setContactFilter(filter);

        fire = MyGame.assets.getParticleEffect("fire");
        fire.scaleEffect(0.4f);
        fire.getEmitters().first().setPosition(95 * Constants.PPM, 100 * Constants.PPM);
        fire.start();

        gameObjects = new ArrayList<Sprite>();
        player = new Player(world, 100 * Constants.PPM, 100 * Constants.PPM);
        gameObjects.add(player);

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts\\PressStart2P.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 5;
        parameter.color = Color.GREEN;
        hpBarFont = generator.generateFont(parameter);
        hpBarFont.setUseIntegerPositions(false);

        parameter.size = 8;
        parameter.color = Color.WHITE;
        itemNameFont = generator.generateFont(parameter);
        itemNameFont.setUseIntegerPositions(false);

        generator.dispose();

        hud = new Hud(player);

        tileMap = new TmxMapLoader().load("maps\\test.tmx");
        tmr = new OrthogonalTiledMapRenderer(tileMap);

        TiledMapTileLayer layer = (TiledMapTileLayer) tileMap.getLayers().get("coast");
        tileSize = layer.getTileWidth();

        for(int row = 0; row < layer.getHeight(); ++row) {
            for(int col = 0; col < layer.getWidth(); ++col) {
                TiledMapTileLayer.Cell cell = layer.getCell(col, row);

                if(cell == null) continue;
                if(cell.getTile() == null) continue;

                BodyDef bd = new BodyDef();
                bd.type = BodyDef.BodyType.StaticBody;
                bd.position.set(
                        (col + 0.5f) * tileSize / Constants.PPM,
                        (row + 0.5f) * tileSize / Constants.PPM
                );

                PolygonShape s = new PolygonShape();
                s.setAsBox(16 / Constants.PPM, 16 / Constants.PPM);
                FixtureDef fd = new FixtureDef();
                fd.shape = s;
                fd.density = 0;
                fd.isSensor = false;
                fd.friction = 0;
                fd.restitution = 0;
                world.createBody(bd).createFixture(fd);

                s.dispose();
            }
        }

        layer = (TiledMapTileLayer) tileMap.getLayers().get("things");
        tileSize = layer.getTileWidth();

        for(int row = 0; row < layer.getHeight(); ++row) {
            for(int col = 0; col < layer.getWidth(); ++col) {
                TiledMapTileLayer.Cell cell = layer.getCell(col, row);

                if(cell == null) continue;
                if(cell.getTile() == null) continue;

                BodyDef bd = new BodyDef();
                bd.type = BodyDef.BodyType.StaticBody;
                bd.position.set(
                        (col + 0.5f) * tileSize / Constants.PPM,
                        (row + 0.5f) * tileSize / Constants.PPM
                );

                PolygonShape s = new PolygonShape();
                s.setAsBox(16 / Constants.PPM, 16 / Constants.PPM);
                FixtureDef fd = new FixtureDef();
                fd.shape = s;
                fd.density = 0;
                fd.isSensor = false;
                fd.friction = 0;
                fd.restitution = 0;
                world.createBody(bd).createFixture(fd);

                s.dispose();
            }
        }

        MyGame.assets.getSound("sea02").loop(0.5f);
        fireSound = MyGame.assets.getSound("fire01");
        fireSoundID = fireSound.loop();

        gameObjects.add(new Item(world, 105 * Constants.PPM, 100 * Constants.PPM, Constants.ITEM_BOW));
    }

    @Override
    public void update(float dt) {
        Gdx.graphics.setTitle("FPS: " + Gdx.graphics.getFramesPerSecond() + " OBJ: " + gameObjects.size());

        handleInput();

        fire.update(dt);

        ArrayList<Loot> lootToDrop = new ArrayList<Loot>();
        for(Iterator<Sprite> i = gameObjects.iterator(); i.hasNext();) {
            Sprite s = i.next();

            //moving slimes towards the player
            if(s instanceof Slime) {
                if(((Slime) s).getAttackableState() == Attackable.AttackableState.ALIVE) {
                    if(player.getAttackableState() == Attackable.AttackableState.ALIVE) {
                        s.getBody().setLinearVelocity(player.getBody().getPosition().x - s.getBody().getPosition().x, player.getBody().getPosition().y - s.getBody().getPosition().y);
                    }
                }
                //removing dead slimes
                if(((Slime) s).getAttackableState() == Attackable.AttackableState.DEAD && s.getBody().getPosition().dst(player.getPosition()) > MyGame.V_WIDTH / Constants.PPM) {
                    world.destroyBody(s.getBody());
                    i.remove();
                }
            }
            if(s instanceof Attackable) {
                //Removing dead slimes
                if(((Attackable) s).getAttackableState() == Attackable.AttackableState.DEAD) {
                    if(s.getCurrentAnimation().getTimesPlayed() > 20) {
                        world.destroyBody(s.getBody());
                        i.remove();
                    }
                    //Dropping loot
                    if(s instanceof Lootable) {
                        if(!((Lootable) s).isLooted()) {
                            lootToDrop.add(new Loot(world, s.getPosition().x * Constants.PPM, s.getPosition().y * Constants.PPM, ((Lootable) s).getGold()));
                            ((Lootable) s).setLooted(true);
                        }
                    }
                }
            }
            //Removing looted loot
            if(s instanceof Loot) {
                Loot loot = (Loot) s;
                if(loot.isLooted()) {
                    world.destroyBody(loot.getBody());
                    i.remove();
                }
            }
            if(s instanceof Arrow) {
                if(s.getFixture().testPoint(cursor.getBox2DPosition()) && s.getPosition().dst(player.getPosition()) < 2.f && MyInput.isDown(MyInput.PICK)) {
                    player.lootArrow();
                    ((Arrow) s).setLooted(true);

                    MyGame.assets.getSound("arrowPickup").play(0.5f);
                }
                //Removing looted arrows
                if(((Arrow) s).isLooted()) {
                    world.destroyBody(s.getBody());
                    i.remove();
                }
            }
            if(s instanceof Item) {
                //Picking up items
                if(s.getFixture().testPoint(cursor.getBox2DPosition()) && s.getPosition().dst(player.getPosition()) < 2.f && MyInput.isDown(MyInput.PICK)) {
                    ((Item) s).setLooted(true);
                    player.setWeaponEquipped(((Item) s).getItemName());

                    MyGame.assets.getSound("pickup").play(0.5f);
                }
                //Removing looted items
                if(((Item) s).isLooted()) {
                    world.destroyBody(s.getBody());
                    i.remove();
                }
            }
        }
        gameObjects.addAll(lootToDrop);

        //resetting player after death and when he pressed reset button
        if(player.getAttackableState() == Attackable.AttackableState.DEAD && MyInput.isDown(MyInput.RESET)) {
            player.reset();
        }

        //updating all game objects
        for(Sprite i : gameObjects) {
            i.update(dt);
        }

        //stepping physics simulation
        world.step(dt, 6, 2);

        //updating lights
        rayHandler.update();

        //updating camera
        cameraUpdate();

        float dst = player.getBody().getPosition().dst(new Vector2(95, 100));
        dst = (float) Math.pow(dst, -1);
        fireSound.setVolume(fireSoundID, dst);
    }

    @Override
    public void render() {
        //clearing screen
        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

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

        sb.begin();
        for(Sprite i : gameObjects) {
            //Rendering Attackable hp bars
            if(i instanceof Attackable) {
                String hp = Integer.toString(((Attackable) i).getHp());
                Vector2 position = new Vector2(((Attackable) i).getHpBarPosition().x, ((Attackable) i).getHpBarPosition().y);
                hpBarFont.draw(sb, hp, position.x, position.y);
            }
            //Rendering Item names if mouse cursor is over Item
            if(i instanceof Item ) {
                if(i.getFixture().testPoint(cursor.getBox2DPosition())) {
                String tmp = ((Item) i).getItemName();
                String name = tmp.substring(0, 1).toUpperCase() + tmp.substring(1);
                Vector2 position = new Vector2(((Item) i).getNamePosition().x, ((Item) i).getNamePosition().y);
                itemNameFont.draw(sb, "- " + name + " -", position.x, position.y);

                String damage = Integer.toString(((Item) i).getDamage());
                position = new Vector2(((Item) i).getNamePosition().x, ((Item) i).getNamePosition().y - 10);
                itemNameFont.draw(sb, "Damage: " + damage, position.x, position.y);

                String price = Integer.toString(((Item) i).getPrice());
                position = new Vector2(((Item) i).getNamePosition().x, ((Item) i).getNamePosition().y - 20);
                itemNameFont.draw(sb, "Price:  " + price, position.x, position.y);

                ((Item) i).setHighlighted(true);
                }
                else {
                    ((Item) i).setHighlighted(false);
                }
            }
            if(i instanceof Arrow) {
                if(i.getFixture().testPoint(cursor.getBox2DPosition()) && !((Arrow) i).isActive()) {
                    ((Arrow) i).setHighlighted(true);
                }
                else {
                    ((Arrow) i).setHighlighted(false);
                }
            }
        }
        sb.end();

        //rendering lights
        rayHandler.render();

        sb.begin();
        fire.draw(sb);
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
            if(player.getAttackableState() == Attackable.AttackableState.ALIVE && !player.isArrowsEmpty() && !player.getWeaponEquipped().equals("none")) {
                MyGame.assets.getSound("bow").play();
                player.shoot();
                Arrow arrow = new Arrow(world, player.getPosition().x, player.getPosition().y);
                arrow.getBody().setTransform(
                        player.getBody().getPosition().x,
                        player.getBody().getPosition().y,
                        player.getBody().getAngle() + (float) Math.toRadians(100f)
                );
                arrow.getBody().setLinearVelocity(
                        -arrow.getBody().getPosition().x * Constants.PPM + cursor.getPosition().x, -arrow.getBody().getPosition().y * Constants.PPM + cursor.getPosition().y
                );
                gameObjects.add(arrow);
            }
        }

        //spawning slimes
        if(!MyInput.isDown(MyInput.SLIME) && spawn) {
            spawn = false;
        }
        if(MyInput.isDown(MyInput.SLIME) && !spawn) {
            spawn = true;
            gameObjects.add(new Slime(cursor.getPosition().x, cursor.getPosition().y, world));
        }

        //rotating player's body towards mouse cursor
        if(player.getAttackableState() == Attackable.AttackableState.ALIVE) {
            Body body = player.getBody();
            Vector2 toTarget = new Vector2(cursor.getPosition().x / Constants.PPM - body.getPosition().x, cursor.getPosition().y / Constants.PPM - body.getPosition().y);
            float desiredAngle = (float) Math.atan2(-toTarget.x, toTarget.y) + (float) Math.toRadians(45) + (float) Math.toRadians(37.5);
            body.setTransform(body.getPosition(), desiredAngle);
        }
    }

    private void cameraUpdate() {
        cam.update();
        cam.position.set(player.getBody().getPosition().x * Constants.PPM, player.getBody().getPosition().y * Constants.PPM, 0);
        sb.setProjectionMatrix(cam.combined);

        rayHandler.setCombinedMatrix(cam.combined.cpy().scl(Constants.PPM), 0, 0, MyGame.V_WIDTH, MyGame.V_HEIGHT);

        cursor.Update(cam);

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
        rayHandler.dispose();
        hpBarFont.dispose();
        hud.dispose();
    }
}
