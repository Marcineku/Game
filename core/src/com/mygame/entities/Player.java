package com.mygame.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.mygame.game.MyGame;
import com.mygame.handlers.Constants;
import com.mygame.handlers.MyInput;
import com.mygame.interfaces.Attackable;

public class Player extends Sprite implements Attackable {
    private int          hp;
    private int          maxHp;
    private PlayerStates state;
    private float        movementSpeed;
    private boolean      strike;

    public Player(World world, float positionX, float positionY) {
        super(BodyDef.BodyType.DynamicBody, positionX, positionY, 5.f, world, 0.f, 15.f, 0.25f);

        id = "player";
        state = PlayerStates.FACING_DOWN;
        maxHp = 100;
        hp = maxHp;
        movementSpeed = 25.f;
        strike = false;

        CircleShape shape = new CircleShape();
        shape.setRadius(5.f / Constants.PPM);
        fixtureDef.shape = shape;
        fixtureDef.filter.categoryBits = Constants.BIT_PLAYER;
        fixture = body.createFixture(fixtureDef);
        fixture.setUserData(this);

        shape.dispose();

        Texture tex = MyGame.assets.getTexture("characters");
        TextureRegion[][] frames = TextureRegion.split(tex, 16, 16);

        float frameDuration = 1/12.f;

        //walking up animation + standing up animation
        TextureRegion[] walkUp = new TextureRegion[3];
        TextureRegion[] standUp = new TextureRegion[1];
        for(int i = 0; i < 3; ++i) {
            walkUp[i] = frames[3][i];
        }
        standUp[0] = walkUp[1];
        addAnimation("walkUp", walkUp, frameDuration);
        addAnimation("standUp", standUp, 0);

        //walking down animation + standing down animation
        TextureRegion[] walkDown = new TextureRegion[3];
        TextureRegion[] standDown = new TextureRegion[1];
        for(int i = 0; i < 3; ++i) {
            walkDown[i] = frames[0][i];
        }
        standDown[0] = walkDown[1];
        addAnimation("walkDown", walkDown, frameDuration);
        addAnimation("standDown", standDown, 0);

        //walking left animation + standing left animation
        TextureRegion[] walkLeft = new TextureRegion[3];
        TextureRegion[] standLeft = new TextureRegion[1];
        for(int i = 0; i < 3; ++i) {
            walkLeft[i] = frames[1][i];
        }
        standLeft[0] = walkLeft[1];
        addAnimation("walkLeft", walkLeft, frameDuration);
        addAnimation("standLeft", standLeft, 0);

        //walking right animation + standing right animation
        TextureRegion[] walkRight = new TextureRegion[3];
        TextureRegion[] standRight = new TextureRegion[1];
        for(int i = 0; i < 3; ++i) {
            walkRight[i] = frames[2][i];
        }
        standRight[0] = walkRight[1];
        addAnimation("walkRight", walkRight, frameDuration);
        addAnimation("standRight", standRight, 0);

        //death animation
        TextureRegion[] dead = new TextureRegion[1];
        dead[0] = frames[0][10];
        addAnimation("dead", dead, 0);

        float radius = 6.f;
        float angle = 15;
        Vector2[] vertices = new Vector2[8];
        vertices[0] = new Vector2(0, 0);
        for(int i = 0; i < vertices.length - 1; ++i) {
            float a = (float) Math.toRadians(i / 6.f * angle);
            vertices[i+1] = new Vector2(radius * (float) Math.cos(a), radius * (float) Math.sin(a));
        }

        PolygonShape polygonShape = new PolygonShape();
        polygonShape.set(vertices);
        FixtureDef fd = new FixtureDef();
        fd.isSensor = false;
        fd.density = 0.f;
        fd.shape = polygonShape;
        fd.filter.categoryBits = Constants.BIT_WEAPON;
        Fixture fx = body.createFixture(fd);
        fx.setUserData(this);
        body.setSleepingAllowed(false);
        body.setBullet(true);

        polygonShape.dispose();
    }

    @Override
    public void update(float dt) {
        super.update(dt);

        if(hp <= 0.0) {
            movementSpeed = 0.f;
            state = PlayerStates.DEAD;
            body.setAwake(false);
        }

        if(state != PlayerStates.DEAD) {
            if (MyInput.isDown(MyInput.UP)) {
                this.body.applyLinearImpulse(0, movementSpeed, body.getPosition().x, body.getPosition().y, true);
                currentAnimation = animations.get("walkUp");
                state = PlayerStates.FACING_UP;
            }
            if (MyInput.isDown(MyInput.DOWN)) {
                this.body.applyLinearImpulse(0, -movementSpeed, body.getPosition().x, body.getPosition().y, true);
                currentAnimation = animations.get("walkDown");
                state = PlayerStates.FACING_DOWN;
            }
            if (MyInput.isDown(MyInput.LEFT)) {
                this.body.applyLinearImpulse(-movementSpeed, 0, body.getPosition().x, body.getPosition().y, true);
                currentAnimation = animations.get("walkLeft");
                state = PlayerStates.FACING_LEFT;
            }
            if (MyInput.isDown(MyInput.RIGHT)) {
                this.body.applyLinearImpulse(movementSpeed, 0, body.getPosition().x, body.getPosition().y, true);
                currentAnimation = animations.get("walkRight");
                state = PlayerStates.FACING_RIGHT;
            }

            if(MyInput.isDown(MyInput.STRIKE)) {
                strike = true;
            }
            else {
                strike = false;
            }

            float padding = 10.f;
            if (body.getLinearVelocity().x < padding && body.getLinearVelocity().x > -padding &&
                    body.getLinearVelocity().y < padding && body.getLinearVelocity().y > -padding) {
                switch (state) {
                    case FACING_UP:
                        currentAnimation = animations.get("standUp");
                        break;
                    case FACING_DOWN:
                        currentAnimation = animations.get("standDown");
                        break;
                    case FACING_LEFT:
                        currentAnimation = animations.get("standLeft");
                        break;
                    case FACING_RIGHT:
                        currentAnimation = animations.get("standRight");
                        break;
                }
            }
        }
        else {
            currentAnimation = animations.get("dead");
            hp = 0;
        }
    }

    @Override
    public void render(SpriteBatch sb) {
        sb.begin();
        sb.draw(currentAnimation.getFrame(),
                body.getPosition().x * Constants.PPM - width / 2,
                body.getPosition().y * Constants.PPM - height / 2 + 6
        );
        sb.end();
    }

    @Override
    public void hit(int damage) {
        hp -= damage;
    }

    @Override
    public int getHp() {
        return hp;
    }

    @Override
    public Vector2 getHpBarPosition() {
        return new Vector2(body.getPosition().x * Constants.PPM - 6.f, body.getPosition().y * Constants.PPM + 20.f);
    }

    public PlayerStates getState() {
        return state;
    }

    public boolean isStrike() {
        return strike;
    }

    public enum PlayerStates {
        FACING_UP, FACING_DOWN, FACING_LEFT, FACING_RIGHT, DEAD
    }
}
