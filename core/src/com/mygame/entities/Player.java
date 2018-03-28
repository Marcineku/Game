package com.mygame.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.World;
import com.mygame.game.MyGame;
import com.mygame.handlers.Constants;
import com.mygame.handlers.MyInput;
import com.mygame.interfaces.Attackable;

public class Player extends Sprite implements Attackable {

    private int hp;
    private int maxHp;
    private PlayerState state;
    private float       movementSpeed;

    public Player(World world, float positionX, float positionY) {
        super(BodyDef.BodyType.DynamicBody, positionX, positionY, 5.f, world, 0.f, 15.f, 0.25f);

        id = "player";
        state = PlayerState.FACING_DOWN;
        maxHp = 100;
        hp = maxHp;
        movementSpeed = 25.f;

        CircleShape shape = new CircleShape();
        shape.setRadius(5.f / Constants.PPM);
        shape.setPosition(new Vector2(0, -6.f / Constants.PPM));
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
    }

    @Override
    public void update(float dt) {
        super.update(dt);

        if(hp <= 0.0) {
            movementSpeed = 0.f;
            state = PlayerState.DEAD;
            body.setAwake(false);
        }

        if(state != PlayerState.DEAD) {
            if (MyInput.isDown(MyInput.UP)) {
                this.body.applyLinearImpulse(0, movementSpeed, getPosition().x, getPosition().y, true);
                currentAnimation = animations.get("walkUp");
                state = PlayerState.FACING_UP;
            }
            if (MyInput.isDown(MyInput.DOWN)) {
                this.body.applyLinearImpulse(0, -movementSpeed, getPosition().x, getPosition().y, true);
                currentAnimation = animations.get("walkDown");
                state = PlayerState.FACING_DOWN;
            }
            if (MyInput.isDown(MyInput.LEFT)) {
                this.body.applyLinearImpulse(-movementSpeed, 0, getPosition().x, getPosition().y, true);
                currentAnimation = animations.get("walkLeft");
                state = PlayerState.FACING_LEFT;
            }
            if (MyInput.isDown(MyInput.RIGHT)) {
                this.body.applyLinearImpulse(movementSpeed, 0, getPosition().x, getPosition().y, true);
                currentAnimation = animations.get("walkRight");
                state = PlayerState.FACING_RIGHT;
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
            currentAnimation = animations.get("standDown");
            hp = 0;
        }
    }

    @Override
    public void hit(int damage) {
        hp -= damage;
    }

    @Override
    public int getHp() {
        return hp;
    }

    public enum PlayerState {
        FACING_UP, FACING_DOWN, FACING_LEFT, FACING_RIGHT, DEAD
    }
}
