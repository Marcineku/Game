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
    private Timer           timer;
    private int             arrows;
    private int             maxArrows;
    private Sound           walkingSound;
    private boolean         walking;
    private Body            shadow;

    public Player(World world, float positionX, float positionY) {
        super(BodyDef.BodyType.DynamicBody, positionX, positionY, 6.f, world, 0.f, 25.f, 0.25f);

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
        walkingSound = MyGame.assets.getSound("walking02");
        walking = false;

        //Collider
        CircleShape shape = new CircleShape();
        shape.setRadius(6.f / Constants.PPM);
        fixtureDef.shape = shape;
        fixtureDef.filter.categoryBits = Constants.BIT_PLAYER;
        fixture = body.createFixture(fixtureDef);
        fixture.setUserData(this);
        shape.dispose();

        //Idle animations
        Texture bodyManIdleTex = MyGame.assets.getTexture("bodyManIdle");
        TextureRegion[][] bodyManIdle = TextureRegion.split(bodyManIdleTex, 52, 52);
        float idleFrameDuration = 0.25f;

        TextureRegion[] idleUp        = new TextureRegion[5];
        TextureRegion[] idleUpRight   = new TextureRegion[5];
        TextureRegion[] idleRight     = new TextureRegion[5];
        TextureRegion[] idleRightDown = new TextureRegion[5];
        TextureRegion[] idleDown      = new TextureRegion[5];
        TextureRegion[] idleDownLeft  = new TextureRegion[5];
        TextureRegion[] idleLeft      = new TextureRegion[5];
        TextureRegion[] idleLeftUp    = new TextureRegion[5];
        for(int i = 0; i < idleUp.length; ++i) {
            idleUp[i]        = bodyManIdle[0][i];
            idleUpRight[i]   = bodyManIdle[1][i];
            idleRight[i]     = bodyManIdle[2][i];
            idleRightDown[i] = bodyManIdle[3][i];
            idleDown[i]      = bodyManIdle[4][i];
            idleDownLeft[i]  = bodyManIdle[5][i];
            idleLeft[i]      = bodyManIdle[6][i];
            idleLeftUp[i]    = bodyManIdle[7][i];
        }
        addAnimation("idleUp",        idleUp,        idleFrameDuration);
        addAnimation("idleUpRight",   idleUpRight,   idleFrameDuration);
        addAnimation("idleRight",     idleRight,     idleFrameDuration);
        addAnimation("idleRightDown", idleRightDown, idleFrameDuration);
        addAnimation("idleDown",      idleDown,      idleFrameDuration);
        addAnimation("idleDownLeft",  idleDownLeft,  idleFrameDuration);
        addAnimation("idleLeft",      idleLeft,      idleFrameDuration);
        addAnimation("idleLeftUp",    idleLeftUp,    idleFrameDuration);

        //Running animations
        Texture bodyManRunTex = MyGame.assets.getTexture("bodyManRun");
        TextureRegion[][] bodyManRun = TextureRegion.split(bodyManRunTex, 52, 52);
        float runFrameDuration = 0.09f;

        TextureRegion[] runUp        = new TextureRegion[8];
        TextureRegion[] runUpRight   = new TextureRegion[8];
        TextureRegion[] runRight     = new TextureRegion[8];
        TextureRegion[] runRightDown = new TextureRegion[8];
        TextureRegion[] runDown      = new TextureRegion[8];
        TextureRegion[] runDownLeft  = new TextureRegion[8];
        TextureRegion[] runLeft      = new TextureRegion[8];
        TextureRegion[] runLeftUp    = new TextureRegion[8];
        for(int i = 0; i < runUp.length; ++i) {
            runUp[i]        = bodyManRun[0][i];
            runUpRight[i]   = bodyManRun[1][i];
            runRight[i]     = bodyManRun[2][i];
            runRightDown[i] = bodyManRun[3][i];
            runDown[i]      = bodyManRun[4][i];
            runDownLeft[i]  = bodyManRun[5][i];
            runLeft[i]      = bodyManRun[6][i];
            runLeftUp[i]    = bodyManRun[7][i];
        }
        addAnimation("runUp",        runUp,        runFrameDuration);
        addAnimation("runUpRight",   runUpRight,   runFrameDuration);
        addAnimation("runRight",     runRight,     runFrameDuration);
        addAnimation("runRightDown", runRightDown, runFrameDuration);
        addAnimation("runDown",      runDown,      runFrameDuration);
        addAnimation("runDownLeft",  runDownLeft,  runFrameDuration);
        addAnimation("runLeft",      runLeft,      runFrameDuration);
        addAnimation("runLeftUp",    runLeftUp,    runFrameDuration);

        //Death animation
        TextureRegion[] dead = new TextureRegion[1];
        dead[0] = idleDown[0];
        addAnimation("dead", dead, 0);

        //Shadows
        BodyDef bd = new BodyDef();
        bd.type = BodyDef.BodyType.KinematicBody;
        bd.linearDamping = 0.f;
        bd.position.set(body.getPosition());
        bd.fixedRotation = false;
        shadow = world.createBody(bd);

        PolygonShape shadowShape = new PolygonShape();
        shadowShape.setAsBox(1.f / Constants.PPM, 15 / Constants.PPM, new Vector2(0, 15 / Constants.PPM), 0);
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
            //up right
            if(MyInput.isDown(MyInput.UP) && MyInput.isDown(MyInput.RIGHT)) {
                this.body.applyLinearImpulse(movementSpeed / (float) Math.sqrt(2), movementSpeed / (float) Math.sqrt(2), body.getPosition().x, body.getPosition().y, true);
                currentAnimation = animations.get("runUpRight");
                playerState = PlayerStates.FACING_UP_RIGHT;
            }
            //right down
            else if(MyInput.isDown(MyInput.RIGHT) && MyInput.isDown(MyInput.DOWN)) {
                this.body.applyLinearImpulse(movementSpeed / (float) Math.sqrt(2),  -movementSpeed / (float) Math.sqrt(2), body.getPosition().x, body.getPosition().y, true);
                currentAnimation = animations.get("runRightDown");
                playerState = PlayerStates.FACING_RIGHT_DOWN;
            }
            //down left
            else if (MyInput.isDown(MyInput.DOWN) && MyInput.isDown(MyInput.LEFT)) {
                this.body.applyLinearImpulse(-movementSpeed / (float) Math.sqrt(2), -movementSpeed / (float) Math.sqrt(2), body.getPosition().x, body.getPosition().y, true);
                currentAnimation = animations.get("runDownLeft");
                playerState = PlayerStates.FACING_DOWN_LEFT;
            }
            //left up
            else if(MyInput.isDown(MyInput.LEFT) && MyInput.isDown(MyInput.UP)) {
                this.body.applyLinearImpulse(-movementSpeed / (float) Math.sqrt(2), movementSpeed / (float) Math.sqrt(2), body.getPosition().x, body.getPosition().y, true);
                currentAnimation = animations.get("runLeftUp");
                playerState = PlayerStates.FACING_LEFT_UP;
            }
            //up
            else if (MyInput.isDown(MyInput.UP)) {
                this.body.applyLinearImpulse(0, movementSpeed, body.getPosition().x, body.getPosition().y, true);
                currentAnimation = animations.get("runUp");
                playerState = PlayerStates.FACING_UP;
            }
            //right
            else if (MyInput.isDown(MyInput.RIGHT)) {
                this.body.applyLinearImpulse(movementSpeed, 0, body.getPosition().x, body.getPosition().y, true);
                currentAnimation = animations.get("runRight");
                playerState = PlayerStates.FACING_RIGHT;
            }
            //down
            else if (MyInput.isDown(MyInput.DOWN)) {
                this.body.applyLinearImpulse(0, -movementSpeed, body.getPosition().x, body.getPosition().y, true);
                currentAnimation = animations.get("runDown");
                playerState = PlayerStates.FACING_DOWN;
            }
            //left
            else if (MyInput.isDown(MyInput.LEFT)) {
                this.body.applyLinearImpulse(-movementSpeed, 0, body.getPosition().x, body.getPosition().y, true);
                currentAnimation = animations.get("runLeft");
                playerState = PlayerStates.FACING_LEFT;
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

            float padding = 10.f;
            if (body.getLinearVelocity().x < padding && body.getLinearVelocity().x > -padding &&
                    body.getLinearVelocity().y < padding && body.getLinearVelocity().y > -padding) {
                switch (playerState) {
                    case FACING_UP:
                        currentAnimation = animations.get("idleUp");
                        break;
                    case FACING_UP_RIGHT:
                        currentAnimation = animations.get("idleUpRight");
                        break;
                    case FACING_RIGHT:
                        currentAnimation = animations.get("idleRight");
                        break;
                    case FACING_RIGHT_DOWN:
                        currentAnimation = animations.get("idleRightDown");
                        break;
                    case FACING_DOWN:
                        currentAnimation = animations.get("idleDown");
                        break;
                    case FACING_DOWN_LEFT:
                        currentAnimation = animations.get("idleDownLeft");
                        break;
                    case FACING_LEFT:
                        currentAnimation = animations.get("idleLeft");
                        break;
                    case FACING_LEFT_UP:
                        currentAnimation = animations.get("idleLeftUp");
                }
                walkingSound.stop();
                walking = true;
            }
            else if(walking){
                long id = walkingSound.loop(0.4f);
                walkingSound.setPitch(id, 3.0f);
                walking = false;
            }
        }
        else {
            currentAnimation = animations.get("dead");
            hp = 0;
        }

        //updating shadow's position
        shadow.setTransform(body.getPosition().x + body.getLinearVelocity().x * dt, body.getPosition().y + body.getLinearVelocity().y * dt, 0.f);
    }

    @Override
    public void render(SpriteBatch sb) {
        float x = getPosition().x * Constants.PPM - width / 2;
        float y = getPosition().y * Constants.PPM - height / 2 + 16;
        sb.begin();
        sb.draw(currentAnimation.getFrame(), x, y);
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
        return new Vector2(body.getPosition().x * Constants.PPM - 8.f, body.getPosition().y * Constants.PPM + 42.f);
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
        FACING_UP, FACING_UP_RIGHT, FACING_RIGHT, FACING_RIGHT_DOWN, FACING_DOWN, FACING_DOWN_LEFT, FACING_LEFT, FACING_LEFT_UP
    }
}
