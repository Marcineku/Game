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
import com.badlogic.gdx.math.MathUtils;
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

import java.lang.reflect.Array;
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
    private Vector2 clickPosition;

    private ArrayList<Sprite> gameObjects;
    private ArrayList<Event> events;

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

        clickPosition = new Vector2(0, 0);

        world = new World(new Vector2(0, 0), true);
        world.setContactListener(new MyContactListener());
        b2dr = new Box2DDebugRenderer();
        rayHandler = new RayHandler(world);
        cursor = new Cursor();

        rayHandler.setAmbientLight(0.2f);
        rayHandler.setBlur(true);

        pointLight = new PointLight(rayHandler, 200, Color.RED, 20.f, 95, 100);
        pointLight.setSoftnessLength(0.f);
        Filter filter = new Filter();
        filter.maskBits = Constants.BIT_PLAYER | Constants.BIT_ENEMY;
        pointLight.setContactFilter(filter);

        fire = MyGame.assets.getParticleEffect("fire");
        fire.scaleEffect(0.1f);
        fire.getEmitters().first().setPosition(94 * Constants.PPM, 100 * Constants.PPM);
        fire.start();

        gameObjects = new ArrayList<Sprite>();
        player = new Player(world, 100 * Constants.PPM, 100 * Constants.PPM, cursor);
        gameObjects.add(player);
        cam.position.set(new Vector3(player.getPosition().x * Constants.PPM, player.getPosition().y * Constants.PPM, 0));

        events = new ArrayList<Event>();

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
        Gdx.graphics.setTitle("FPS: " + Gdx.graphics.getFramesPerSecond() + " OBJ: " + gameObjects.size() + " EVNT: " + events.size());

        handleInput();

        fire.update(dt);

        ArrayList<Loot> lootToDrop = new ArrayList<Loot>();
        for(Iterator<Sprite> i = gameObjects.iterator(); i.hasNext();) {
            Sprite s = i.next();

            if(s instanceof Slime) {
                //Removing dead slimes
                if(((Slime) s).getAttackableState() == Attackable.AttackableState.DEAD && s.getBody().getPosition().dst(player.getPosition()) > MyGame.V_WIDTH / Constants.PPM) {
                    world.destroyBody(s.getBody());
                    i.remove();
                }
            }
            if(s instanceof Attackable) {
                if(((Attackable) s).getAttackableState() == Attackable.AttackableState.DEAD) {
                    //Dropping loot
                    if(s instanceof Lootable) {
                        if(!((Lootable) s).isLooted()) {
                            lootToDrop.add(new Loot(world, s.getPosition().x * Constants.PPM, s.getPosition().y * Constants.PPM, ((Lootable) s).getGold()));
                            ((Lootable) s).setLooted(true);
                            MyGame.assets.getSound("goldDrop").play();
                            //Increasing player's exp
                            player.addExp(((Attackable) s).getExp());
                            String exp = Integer.toString(((Attackable) s).getExp());
                            events.add(new Event(sb, itemNameFont,"+" + exp, 3.f, s.getPosition().scl(Constants.PPM), Color.WHITE, 1/2.f, 0));
                        }
                    }
                }
                if(((Attackable) s).getAttackableState() == Attackable.AttackableState.ALIVE && ((Attackable) s).isHit()) {
                    ((Attackable) s).setHit(false);
                    String damage = Integer.toString(((Attackable) s).getDamage());
                    events.add(new Event(sb, itemNameFont, "-" + damage, 1.f, s.getPosition().scl(Constants.PPM), Color.RED, 1.f, 1));
                }
            }
            //Removing looted loot
            if(s instanceof Loot) {
                Loot loot = (Loot) s;
                if(loot.isLooted()) {
                    events.add(new Event(sb, itemNameFont, "+" + ((Loot) s).getGold(), 3.f, s.getPosition().scl(Constants.PPM), Color.GOLD, 1/2.f, 0));
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
                    player.pickItem(((Item) s));
                    player.setWeaponEquipped(((Item) s));

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

        //updating all game objects
        for(Sprite i : gameObjects) {
            i.update(dt);
        }

        //updating all events
        for(Event i : events) {
            i.update(dt);
        }

        for(Iterator<Event> i = events.iterator(); i.hasNext();) {
            Event e = i.next();

            if(!e.isActive()) {
                i.remove();
            }
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
                if(((Attackable) i).getAttackableState() == Attackable.AttackableState.ALIVE) {
                    String hp = Integer.toString(((Attackable) i).getHp());
                    Vector2 position = new Vector2(((Attackable) i).getHpBarPosition().x, ((Attackable) i).getHpBarPosition().y);
                    hpBarFont.draw(sb, hp, position.x, position.y);
                }
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
            if(i instanceof Loot) {
                if(i.getFixture().testPoint(cursor.getBox2DPosition())) {
                    ((Loot) i).setHighlighted(true);
                }
                else {
                    ((Loot) i).setHighlighted(false);
                }
            }
        }
        sb.end();

        //rendering lights
        rayHandler.render();

        //rendering all events
        for(Event i : events) {
            i.render();
        }

        sb.begin();
        fire.draw(sb);
        sb.end();

        //rendering hud
        sb.setProjectionMatrix(hudCam.combined);
        hud.render(sb);
    }

    @Override
    public void handleInput() {
        //shooting arrows on click
        if(MyInput.isDown(MyInput.STRIKE) && player.getAttackableState() == Attackable.AttackableState.ALIVE && !player.isArrowsEmpty() && player.getWeaponEquipped() != null && player.getWeaponEquipped().getItemName().equals(Constants.ITEM_BOW) && player.isWeaponDrawn()) {
            cam.zoom = MathUtils.lerp(cam.zoom, 0.8f, 0.1f);
        }
        if(MyInput.isPressed(MyInput.STRIKE) && player.getAttackableState() == Attackable.AttackableState.ALIVE && !player.isArrowsEmpty() && player.getWeaponEquipped() != null && player.getWeaponEquipped().getItemName().equals(Constants.ITEM_BOW) && player.isWeaponDrawn()) {
            player.setClickPoint(new Vector2(cursor.getPosition()));
            player.getTimer().start();
            MyGame.assets.getSound("bowPull").stop();
            MyGame.assets.getSound("bowPull").play();
            player.setState(Player.State.PULLING_BOWSTRING);
            clickPosition = cursor.getPosition().cpy();
        }

        if(MyInput.isReleased(MyInput.STRIKE) && player.getAttackableState() == Attackable.AttackableState.ALIVE && !player.isArrowsEmpty() && player.getWeaponEquipped() != null && player.getWeaponEquipped().getItemName().equals(Constants.ITEM_BOW) && player.isWeaponDrawn() && player.getState() == Player.State.PULLING_BOWSTRING) {
            player.getCurrentAnimation().reset();
            player.getCurrentWeaponAnim().reset();

            player.setState(Player.State.IDLE);
            MyGame.assets.getSound("bowPull").stop();
            MyGame.assets.getSound("bow").play();

            float velocity = 120.f * player.getTimer().getTime();

            int damage = MathUtils.clamp((int) velocity / 5 + 10,2, player.getWeaponEquipped().getDamage());

            if(velocity > 100.f) {
                velocity = 100.f;
            }

            player.shoot();

            Arrow arrow = new Arrow(world, player.getPosition().x, player.getPosition().y, damage, player);
            arrow.getBody().setTransform(
                    player.getBody().getPosition().x,
                    player.getBody().getPosition().y,
                    player.getBody().getAngle() + (float) Math.toRadians(90.f)
            );

            Vector2 dir = new Vector2(player.getClickPoint()).sub(arrow.getPosition().scl(Constants.PPM)).nor().scl(velocity);
            arrow.getBody().setLinearVelocity(dir);
            gameObjects.add(arrow);

            player.getTimer().reset();
        }

        //resetting player's state after he's not firing bow after pulling bowstring
        if(MyInput.isPressed(MyInput.STRIKE2) && player.getState() == Player.State.PULLING_BOWSTRING) {
            player.getCurrentAnimation().reset();
            player.getCurrentWeaponAnim().reset();

            player.setState(Player.State.IDLE);
        }

        //spawning slimes
        if(MyInput.isPressed(MyInput.SLIME)) {
            gameObjects.add(new Slime(cursor.getPosition().x, cursor.getPosition().y, world, player));
        }

        //resetting player after death and when he pressed reset button
        if(player.getAttackableState() == Attackable.AttackableState.DEAD && MyInput.isDown(MyInput.RESET)) {
            player.reset();
        }
    }

    private void cameraUpdate() {
        cam.update();

        if(player.isWeaponDrawn()) {
            if(cursor.getBox2DPosition().dst(player.getPosition()) > 2.f) {
                Vector2 dir2d = new Vector2(player.getPosition().scl(Constants.PPM).add(cursor.getPosition().sub(player.getPosition().scl(Constants.PPM)).nor().scl(60)));
                Vector3 dir = new Vector3(dir2d.x, dir2d.y, 0);
                cam.position.lerp(dir, 0.05f);
                cam.zoom = MathUtils.lerp(cam.zoom, 0.9f, 0.02f);
            }
            else {
                Vector2 dir2d = new Vector2(player.getPosition().scl(Constants.PPM).add(cursor.getPosition().sub(player.getPosition().scl(Constants.PPM)).nor().scl(60)));
                Vector3 dir = new Vector3(dir2d.x, dir2d.y, 0);
                cam.position.lerp(dir, 0.004f);
                cam.zoom = MathUtils.lerp(cam.zoom, 0.9f, 0.02f);
            }
        }
        else {
            Vector3 dir = new Vector3(player.getPosition().x, player.getPosition().y, 0).scl(Constants.PPM);
            cam.position.lerp(dir, 0.2f);
            cam.zoom = MathUtils.lerp(cam.zoom, 1f, 0.02f);
        }

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
