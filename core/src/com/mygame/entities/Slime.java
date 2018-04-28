package com.mygame.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.mygame.game.MyGame;
import com.mygame.handlers.Constants;
import com.mygame.handlers.Timer;
import com.mygame.interfaces.Attackable;
import com.mygame.interfaces.Lootable;

import java.util.Random;

public class Slime extends Sprite implements Attackable, Lootable {
    private int hp;
    private int maxHp;
    private Direction direction;
    private AttackableState attackableState;
    private float movementSpeed;
    private float maxMovementSpeed;
    private boolean looted;
    private int minGold;
    private int maxGold;
    private Sprite target;
    private Timer timer;
    private int exp;
    private boolean hit;
    private int damage;

    public Slime(float positionX, float positionY, World world, Sprite target) {
        super(BodyDef.BodyType.DynamicBody, positionX, positionY, 4.f, world, 0.f, 15.f, 0.12f);

        layer = 3;
        maxHp = 100;
        hp = maxHp;
        maxMovementSpeed = 10.f;
        movementSpeed = maxMovementSpeed;
        direction = Direction.DOWN;
        attackableState = AttackableState.ALIVE;
        looted = false;
        minGold = 1;
        maxGold = 3;
        this.target = target;
        timer = new Timer();
        exp = 10;
        hit = false;
        damage = 0;

        defineMainCollider(8.f, 6.f, Constants.BIT_ENEMY, this);

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

        Texture deadTex = MyGame.assets.getTexture("dead");
        TextureRegion[][] deadFrames = TextureRegion.split(deadTex, 32, 32);
        TextureRegion[] dead = new TextureRegion[1];
        dead[0] = deadFrames[2][1];
        addAnimation("dead", dead, 0);

        width = 16;
        height = 16;
    }

    @Override
    public void update(float dt) {
        super.update(dt);

        timer.update(dt);

        if(hp <= 0) {
            hp = 0;
            attackableState = AttackableState.DEAD;
            fixture.setSensor(true);
            layer = 1;
            width = 32;
            height = 32;
            Filter filter = fixture.getFilterData();
            filter.categoryBits = 0;
            fixture.setFilterData(filter);
        }

        if(attackableState == AttackableState.ALIVE) {
            if(target instanceof Attackable && ((Attackable) target).getAttackableState() == Attackable.AttackableState.ALIVE) {
                Vector2 dir = new Vector2(target.body.getPosition()).sub(body.getPosition()).nor().scl(movementSpeed * 20);

                if(!timer.isRunning()) {
                    timer.start();
                }

                if(timer.getTime() >= 0.4f) {
                    getBody().applyLinearImpulse(dir, getPosition(), true);
                    timer.reset();
                }
            }

            Vector2 v = body.getLinearVelocity();
            if(v.x > 0 && v.y > 0) {
                if(v.x > v.y) {
                    direction = Direction.RIGHT;
                }
                else {
                    direction = Direction.UP;
                }
            }
            else {
                if(v.x < v.y) {
                    direction = Direction.LEFT;
                }
                else {
                    direction = Direction.DOWN;
                }
            }
            currentAnimation = animations.get("walk" + direction);
        }
        else {
            currentAnimation = animations.get("dead");
        }
    }

    @Override
    public void hit(int damage) {
        hp -= damage;
        this.damage = damage;
        hit = true;
    }

    @Override
    public int getHp() {
        return hp;
    }

    public int getExp() {
        return exp;
    }

    public Direction getDirection() {
        return direction;
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
        Random generator = new Random();
        return generator.nextInt(maxGold) + minGold;
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

    public boolean isHit() {
        return hit;
    }

    public void setHit(boolean hit) {
        this.hit = hit;
    }

    public int getDamage() {
        return damage;
    }
}
