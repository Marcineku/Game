package com.mygame.entities;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.mygame.game.MyGame;
import com.mygame.handlers.Constants;
import com.mygame.handlers.MyInput;
import com.mygame.handlers.Timer;
import com.mygame.interfaces.Attackable;

public class Player extends Sprite implements Attackable {
    private int             hp;
    private int             maxHp;
    private PlayerStates    playerState;
    private AttackableState attackableState;
    private float           movementSpeed;
    private float           maxMovementSpeed;
    private boolean         strike;
    private int             gold;
    private Fixture         weapon;
    private Timer           timer;
    private int             arrows;
    private int             maxArrows;
    private Sound           walking;
    private boolean         isWalking;
    private Body            shadow;

    private TextureRegion sword;

    public Player(World world, float positionX, float positionY) {
        super(BodyDef.BodyType.DynamicBody, positionX, positionY, 5.f, world, 0.f, 15.f, 0.25f);

        id = "player";
        layer = 3;
        playerState = PlayerStates.FACING_DOWN;
        attackableState = AttackableState.ALIVE;
        maxHp = 100;
        hp = maxHp;
        maxMovementSpeed = 25.f;
        movementSpeed = maxMovementSpeed;
        strike = false;
        gold = 0;
        timer = new Timer();
        maxArrows = 100;
        arrows = maxArrows;
        walking = MyGame.assets.getSound("walking02");
        isWalking = false;

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
/*
        float radius = 6.f;
        float angle = 15;
        Vector2[] vertices = new Vector2[8];
        vertices[0] = new Vector2(0, 0);
        for(int i = 0; i < vertices.length - 1; ++i) {
            float a = (float) Math.toRadians(i / 6.f * angle);
            vertices[i+1] = new Vector2(radius * (float) Math.cos(a), radius * (float) Math.sin(a));
        }

        TextureRegion swordtmp[][] = TextureRegion.split(MyGame.assets.getTexture("sword"), 26, 58);
        sword = swordtmp[0][0];

        PolygonShape polygonShape = new PolygonShape();
        polygonShape.set(vertices);
        FixtureDef fd = new FixtureDef();
        fd.isSensor = true;
        fd.density = 0.f;
        fd.shape = polygonShape;
        fd.filter.categoryBits = Constants.BIT_WEAPON;
        weapon = body.createFixture(fd);
        weapon.setUserData(this);
        body.setSleepingAllowed(false);
        body.setBullet(false);

        polygonShape.dispose();
*/
        BodyDef bd = new BodyDef();
        bd.type = BodyDef.BodyType.KinematicBody;
        bd.linearDamping = 0.f;
        bd.position.set(body.getPosition());
        bd.fixedRotation = false;
        shadow = world.createBody(bd);

        PolygonShape shadowShape = new PolygonShape();
        shadowShape.setAsBox(2.f / Constants.PPM, 8 / Constants.PPM, new Vector2(0, 6 / Constants.PPM), 0);
        FixtureDef f = new FixtureDef();
        f.isSensor = true;
        f.density = 0.f;
        f.shape = shadowShape;
        f.filter.categoryBits = Constants.BIT_SHADOWS;
        shadow.createFixture(f);
        shadowShape.dispose();
    }

    @Override
    public void update(float dt) {
        super.update(dt);

        timer.update(dt);

        if(hp <= 0.0) {
            movementSpeed = 0.f;
            attackableState = AttackableState.DEAD;
            body.setAwake(false);
        }

        if(attackableState == AttackableState.ALIVE) {
            if (MyInput.isDown(MyInput.UP)) {
                this.body.applyLinearImpulse(0, movementSpeed, body.getPosition().x, body.getPosition().y, true);
                currentAnimation = animations.get("walkUp");
                playerState = PlayerStates.FACING_UP;

            }
            if (MyInput.isDown(MyInput.DOWN)) {
                this.body.applyLinearImpulse(0, -movementSpeed, body.getPosition().x, body.getPosition().y, true);
                currentAnimation = animations.get("walkDown");
                playerState = PlayerStates.FACING_DOWN;
            }
            if (MyInput.isDown(MyInput.LEFT)) {
                this.body.applyLinearImpulse(-movementSpeed, 0, body.getPosition().x, body.getPosition().y, true);
                currentAnimation = animations.get("walkLeft");
                playerState = PlayerStates.FACING_LEFT;
            }
            if (MyInput.isDown(MyInput.RIGHT)) {
                this.body.applyLinearImpulse(movementSpeed, 0, body.getPosition().x, body.getPosition().y, true);
                currentAnimation = animations.get("walkRight");
                playerState = PlayerStates.FACING_RIGHT;
            }

            if(!MyInput.isDown(MyInput.STRIKE) && strike && timer.getTime() >= 0.5f) {
                strike = false;
                timer.stop();
                timer.reset();
            }
            if(MyInput.isDown(MyInput.STRIKE) && !strike) {
                strike = true;
                timer.start();
            }

            if(strike) {
                //Filter f = weapon.getFilterData();
                //f.categoryBits = Constants.BIT_WEAPON;
                //weapon.setFilterData(f);
            }
            else {
                //Filter f = weapon.getFilterData();
                //f.categoryBits = 0;
                //weapon.setFilterData(f);
            }

            float padding = 10.f;
            if (body.getLinearVelocity().x < padding && body.getLinearVelocity().x > -padding &&
                    body.getLinearVelocity().y < padding && body.getLinearVelocity().y > -padding) {
                switch (playerState) {
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
                walking.stop();
                isWalking = true;
            }
            else if(isWalking){
                long id = walking.loop(0.4f);
                walking.setPitch(id, 3.0f);
                isWalking = false;
            }
        }
        else {
            currentAnimation = animations.get("dead");
            hp = 0;
        }

        shadow.setTransform(body.getPosition().x + body.getLinearVelocity().x * dt, body.getPosition().y + body.getLinearVelocity().y * dt, 0.f);
    }

    @Override
    public void render(SpriteBatch sb) {
        float x = getPosition().x * Constants.PPM - 13;
        float y = getPosition().y * Constants.PPM - 5;
        sb.begin();
        //sb.draw(sword, x, y + 15, 0 + 13, 0 + 5 - 15, 26, 58,0.8f,0.8f, (float) Math.toDegrees(body.getAngle()) - 82.5f);
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

    public PlayerStates getPlayerState() {
        return playerState;
    }

    public AttackableState getAttackableState() {
        return attackableState;
    }

    public boolean isStrike() {
        return strike;
    }

    public int getGold() {
        return gold;
    }

    public void lootGold(int gold) {
        this.gold += gold;
    }

    public void reset() {
        hp = maxHp;
        gold = 0;
        playerState = PlayerStates.FACING_DOWN;
        attackableState = AttackableState.ALIVE;
        body.setTransform(100, 100, 0);
        movementSpeed = maxMovementSpeed;
        body.setAwake(true);
    }

    public boolean isArrowsEmpty() {
        if(arrows <= 0) {
            return true;
        }
        else {
            return false;
        }
    }

    public void shoot() {
        arrows -= 1;
    }

    public int getArrows() {
        return arrows;
    }

    public void lootArrow() {
        arrows += 1;
    }

    public enum PlayerStates {
        FACING_UP, FACING_DOWN, FACING_LEFT, FACING_RIGHT, JUMPING
    }
}
