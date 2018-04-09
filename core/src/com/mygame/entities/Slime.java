package com.mygame.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.mygame.game.MyGame;
import com.mygame.handlers.Constants;
import com.mygame.interfaces.Attackable;
import com.mygame.interfaces.Lootable;

public class Slime extends Sprite implements Attackable, Lootable {
    private int hp;
    private int maxHp;
    private SlimeStates slimeState;
    private AttackableState attackableState;
    private float movementSpeed;
    private float maxMovementSpeed;
    private boolean looted;
    private int gold;

    public Slime(float positionX, float positionY, World world) {
        super(BodyDef.BodyType.DynamicBody, positionX, positionY, 4.f, world, 0.f, 15.f, 0.12f);
        body.setBullet(true);

        id = "slime";
        layer = 2;
        maxHp = 100;
        hp = maxHp;
        maxMovementSpeed = 25.f;
        movementSpeed = maxMovementSpeed;
        slimeState = SlimeStates.FACING_DOWN;
        attackableState = AttackableState.ALIVE;
        looted = false;
        gold = 2;

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(8.f / Constants.PPM, 6.f / Constants.PPM);
        fixtureDef.shape = shape;
        fixtureDef.filter.categoryBits = Constants.BIT_ENEMY;
        fixture = body.createFixture(fixtureDef);
        fixture.setUserData(this);
        shape.dispose();

        Texture tex = MyGame.assets.getTexture("characters");
        TextureRegion[][] frames = TextureRegion.split(tex, 16, 16);

        float frameDuration = 1/12.f;

        TextureRegion[] walkUp = new TextureRegion[3];
        for(int i = 0; i < 3; ++i) {
            walkUp[i] = frames[7][i];
        }
        addAnimation("walkUp", walkUp, frameDuration);

        TextureRegion[] walkDown = new TextureRegion[3];
        for(int i = 0; i < 3; ++i) {
            walkDown[i] = frames[4][i];
        }
        addAnimation("walkDown", walkDown, frameDuration);

        TextureRegion[] walkLeft = new TextureRegion[3];
        for(int i = 0; i < 3; ++i) {
            walkLeft[i] = frames[5][i];
        }
        addAnimation("walkLeft", walkLeft, frameDuration);

        TextureRegion[] walkRight = new TextureRegion[3];
        for(int i = 0; i < 3; ++i) {
            walkRight[i] = frames[6][i];
        }
        addAnimation("walkRight", walkRight, frameDuration);

        TextureRegion[] dead = new TextureRegion[3];
        for(int i = 0; i < 3; ++i) {
            dead[i] = frames[4][6 + i];
        }
        addAnimation("dead", dead, frameDuration);
    }

    @Override
    public void update(float dt) {
        super.update(dt);

        if(hp <= 0) {
            hp = 0;
            attackableState = AttackableState.DEAD;
            fixture.setSensor(true);
        }

        if(attackableState == AttackableState.ALIVE) {
            Vector2 v = body.getLinearVelocity();
            if(v.x > 0 && v.y > 0) {
                if(v.x > v.y) {
                    currentAnimation = animations.get("walkRight");
                    slimeState = SlimeStates.FACING_RIGHT;
                }
                else {
                    currentAnimation = animations.get("walkUp");
                    slimeState = SlimeStates.FACING_UP;
                }
            }
            else {
                if(v.x < v.y) {
                    currentAnimation = animations.get("walkLeft");
                    slimeState = SlimeStates.FACING_LEFT;
                }
                else {
                    currentAnimation = animations.get("walkDown");
                    slimeState = SlimeStates.FACING_DOWN;
                }
            }
        }
        else {
            currentAnimation = animations.get("dead");
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

    public SlimeStates getSlimeState() {
        return slimeState;
    }

    public AttackableState getAttackableState() {
        return attackableState;
    }

    public void setLooted(boolean looted) {
        this.looted = looted;
    }

    public boolean isLooted() {
        return looted;
    }

    public int getGold() {
        return gold;
    }

    @Override
    public void render(SpriteBatch sb) {
        sb.begin();
        sb.draw(currentAnimation.getFrame(),
                body.getPosition().x * Constants.PPM - width / 2,
                body.getPosition().y * Constants.PPM - height / 2 + 2
        );
        sb.end();
    }

    @Override
    public Vector2 getHpBarPosition() {
        return new Vector2(body.getPosition().x * Constants.PPM - 8.f, body.getPosition().y * Constants.PPM + 12.f);
    }


    public enum SlimeStates {
        FACING_UP, FACING_DOWN, FACING_LEFT, FACING_RIGHT
    }
}
