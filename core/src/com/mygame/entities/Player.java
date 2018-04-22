package com.mygame.entities;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.mygame.game.MyGame;
import com.mygame.handlers.Animation;
import com.mygame.handlers.Constants;
import com.mygame.handlers.MyInput;
import com.mygame.handlers.Timer;
import com.mygame.interfaces.Attackable;

import java.util.ArrayList;

public class Player extends Sprite implements Attackable {
    private Cursor          cursor;
    private int             hp;
    private int             maxHp;
    private Direction       playerState;
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
    private Animation       currentWeaponAnim;
    private Item            weaponEquipped;
    private ArrayList<Item> items;
    private State           state;
    private boolean         weaponDrawn;

    public Player(World world, float positionX, float positionY, Cursor cursor) {
        super(BodyDef.BodyType.DynamicBody, positionX, positionY, 6.f, world, 0.f, 25.f, 0.25f);

        this.cursor = cursor;

        layer = 5;
        playerState = Direction.FACING_DOWN;
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
        weaponEquipped = null;
        items = new ArrayList<Item>();
        state = State.IDLE;
        weaponDrawn = false;

        defineMainCollider(6.f, Constants.BIT_PLAYER, this);

        //Idle animation
        addDirectionalAnimation("idle", MyGame.assets.getTexture("bodyManIdle"), 5, 0.25f);
        //Run animation
        addDirectionalAnimation("run", MyGame.assets.getTexture("bodyManRun"), 8, 0.09f);

        //Animation of not drawn bow while idling
        addDirectionalAnimation("bowBackIdle", MyGame.assets.getTexture("bowBackManIdle"), 5, 0.25f);
        //Animation of not drawn bow while running
        addDirectionalAnimation("bowBackRun", MyGame.assets.getTexture("bowBackManRun"), 8, 0.09f);

        //Animation of drawing a bow
        addDirectionalAnimation("bodyManBowDraw", MyGame.assets.getTexture("bodyManBowDraw"), 5, 0.20f, false);
        //Animation of hiding a bow
        addDirectionalAnimation("bodyManBowDraw", MyGame.assets.getTexture("bodyManBowDraw"), 5, 0.20f, false, true);

        //Animation of bow being drawn
        addDirectionalAnimation("bowDrawManIdle", MyGame.assets.getTexture("bowDrawManIdle"), 5, 0.20f, false);
        //Animation of bow being hidden
        addDirectionalAnimation("bowDrawManIdle", MyGame.assets.getTexture("bowDrawManIdle"), 5, 0.20f, false, true);

        TextureRegion[] dead = new TextureRegion[1];
        dead[0] = getCurrentAnimation().getFrame();
        addAnimation("dead", dead, 0);

        currentAnimation = animations.get("idleDown");
        currentWeaponAnim = animations.get("bowBackIdleDown");
    }

    @Override
    public void update(float dt) {
        super.update(dt);

        //rotating player's body towards mouse cursor
        if(getAttackableState() == Attackable.AttackableState.ALIVE) {
            Vector2 toTarget = new Vector2(cursor.getPosition().x / Constants.PPM - body.getPosition().x, cursor.getPosition().y / Constants.PPM - body.getPosition().y);
            float desiredAngle = (float) Math.atan2(-toTarget.x, toTarget.y) + (float) Math.toRadians(45) + (float) Math.toRadians(37.5);
            body.setTransform(body.getPosition(), desiredAngle);
        }

        if(weaponEquipped != null)
            currentWeaponAnim.update(dt);

        timer.update(dt);

        if(MyInput.isPressed(MyInput.DRAW) && attackableState == AttackableState.ALIVE && weaponEquipped != null && state == State.IDLE && weaponEquipped.getItemName().equals(Constants.ITEM_BOW)) {
            state = State.DRAWING_BOW;
            walkingSound.stop();

            String reverse;
            if(weaponDrawn) {
                reverse = "Reverse";
            }
            else {
                reverse = "";
            }

            switch (playerState) {
                case FACING_UP:
                    currentAnimation = animations.get("bodyManBowDrawUp" + reverse);

                    if(weaponEquipped != null) {
                        currentWeaponAnim = animations.get("bowDrawManIdleUp" + reverse);
                        currentWeaponAnim.Synchronize(currentAnimation);
                    }
                    break;
                case FACING_UP_RIGHT:
                    currentAnimation = animations.get("bodyManBowDrawUpRight" + reverse);

                    if(weaponEquipped != null) {
                        currentWeaponAnim = animations.get("bowDrawManIdleUpRight" + reverse);
                        currentWeaponAnim.Synchronize(currentAnimation);
                    }
                    break;
                case FACING_RIGHT:
                    currentAnimation = animations.get("bodyManBowDrawRight" + reverse);

                    if(weaponEquipped != null) {
                        currentWeaponAnim = animations.get("bowDrawManIdleRight" + reverse);
                        currentWeaponAnim.Synchronize(currentAnimation);
                    }
                    break;
                case FACING_RIGHT_DOWN:
                    currentAnimation = animations.get("bodyManBowDrawRightDown" + reverse);

                    if(weaponEquipped != null) {
                        currentWeaponAnim = animations.get("bowDrawManIdleRightDown" + reverse);
                        currentWeaponAnim.Synchronize(currentAnimation);
                    }
                    break;
                case FACING_DOWN:
                    currentAnimation = animations.get("bodyManBowDrawDown" + reverse);

                    if(weaponEquipped != null) {
                        currentWeaponAnim = animations.get("bowDrawManIdleDown" + reverse);
                        currentWeaponAnim.Synchronize(currentAnimation);
                    }
                    break;
                case FACING_DOWN_LEFT:
                    currentAnimation = animations.get("bodyManBowDrawDownLeft" + reverse);

                    if(weaponEquipped != null) {
                        currentWeaponAnim = animations.get("bowDrawManIdleDownLeft" + reverse);
                        currentWeaponAnim.Synchronize(currentAnimation);
                    }
                    break;
                case FACING_LEFT:
                    currentAnimation = animations.get("bodyManBowDrawLeft" + reverse);

                    if(weaponEquipped != null) {
                        currentWeaponAnim = animations.get("bowDrawManIdleLeft" + reverse);
                        currentWeaponAnim.Synchronize(currentAnimation);
                    }
                    break;
                case FACING_LEFT_UP:
                    currentAnimation = animations.get("bodyManBowDrawLeftUp" + reverse);

                    if(weaponEquipped != null) {
                        currentWeaponAnim = animations.get("bowDrawManIdleLeftUp" + reverse);
                        currentWeaponAnim.Synchronize(currentAnimation);
                    }
                    break;
            }
        }

        if(state == State.DRAWING_BOW) {
            if(currentAnimation.hasEnded()) {
                state = State.IDLE;

                if(weaponDrawn) {
                    weaponDrawn = false;
                }
                else {
                    weaponDrawn = true;
                }
            }
        }

        if(hp <= 0.0) {
            movementSpeed = 0.f;
            attackableState = AttackableState.DEAD;
            body.setAwake(false);
            walkingSound.stop();
        }

        if(attackableState == AttackableState.ALIVE && state == State.IDLE) {
            //up right
            if(MyInput.isDown(MyInput.UP) && MyInput.isDown(MyInput.RIGHT)) {
                this.body.applyLinearImpulse(movementSpeed / (float) Math.sqrt(2), movementSpeed / (float) Math.sqrt(2), body.getPosition().x, body.getPosition().y, true);
                currentAnimation = animations.get("runUpRight");
                playerState = Direction.FACING_UP_RIGHT;

                if(weaponEquipped != null) {
                    currentWeaponAnim = animations.get(weaponEquipped + "BackRunUpRight");
                    currentWeaponAnim.Synchronize(currentAnimation);
                }
            }
            //right down
            else if(MyInput.isDown(MyInput.RIGHT) && MyInput.isDown(MyInput.DOWN)) {
                this.body.applyLinearImpulse(movementSpeed / (float) Math.sqrt(2),  -movementSpeed / (float) Math.sqrt(2), body.getPosition().x, body.getPosition().y, true);
                currentAnimation = animations.get("runRightDown");
                playerState = Direction.FACING_RIGHT_DOWN;

                if(weaponEquipped != null) {
                    currentWeaponAnim = animations.get(weaponEquipped + "BackRunRightDown");
                    currentWeaponAnim.Synchronize(currentAnimation);
                }
            }
            //down left
            else if (MyInput.isDown(MyInput.DOWN) && MyInput.isDown(MyInput.LEFT)) {
                this.body.applyLinearImpulse(-movementSpeed / (float) Math.sqrt(2), -movementSpeed / (float) Math.sqrt(2), body.getPosition().x, body.getPosition().y, true);
                currentAnimation = animations.get("runDownLeft");
                playerState = Direction.FACING_DOWN_LEFT;

                if(weaponEquipped != null) {
                    currentWeaponAnim = animations.get(weaponEquipped + "BackRunDownLeft");
                    currentWeaponAnim.Synchronize(currentAnimation);
                }
            }
            //left up
            else if(MyInput.isDown(MyInput.LEFT) && MyInput.isDown(MyInput.UP)) {
                this.body.applyLinearImpulse(-movementSpeed / (float) Math.sqrt(2), movementSpeed / (float) Math.sqrt(2), body.getPosition().x, body.getPosition().y, true);
                currentAnimation = animations.get("runLeftUp");
                playerState = Direction.FACING_LEFT_UP;

                if(weaponEquipped != null) {
                    currentWeaponAnim = animations.get(weaponEquipped + "BackRunLeftUp");
                    currentWeaponAnim.Synchronize(currentAnimation);
                }
            }
            //up
            else if (MyInput.isDown(MyInput.UP)) {
                this.body.applyLinearImpulse(0, movementSpeed, body.getPosition().x, body.getPosition().y, true);
                currentAnimation = animations.get("runUp");
                playerState = Direction.FACING_UP;

                if(weaponEquipped != null) {
                    currentWeaponAnim = animations.get(weaponEquipped + "BackRunUp");
                    currentWeaponAnim.Synchronize(currentAnimation);
                }
            }
            //right
            else if (MyInput.isDown(MyInput.RIGHT)) {
                this.body.applyLinearImpulse(movementSpeed, 0, body.getPosition().x, body.getPosition().y, true);
                currentAnimation = animations.get("runRight");
                playerState = Direction.FACING_RIGHT;

                if(weaponEquipped != null) {
                    currentWeaponAnim = animations.get(weaponEquipped + "BackRunRight");
                    currentWeaponAnim.Synchronize(currentAnimation);
                }
            }
            //down
            else if (MyInput.isDown(MyInput.DOWN)) {
                this.body.applyLinearImpulse(0, -movementSpeed, body.getPosition().x, body.getPosition().y, true);
                currentAnimation = animations.get("runDown");
                playerState = Direction.FACING_DOWN;

                if(weaponEquipped != null) {
                    currentWeaponAnim = animations.get(weaponEquipped + "BackRunDown");
                    currentWeaponAnim.Synchronize(currentAnimation);
                }
            }
            //left
            else if (MyInput.isDown(MyInput.LEFT)) {
                this.body.applyLinearImpulse(-movementSpeed, 0, body.getPosition().x, body.getPosition().y, true);
                currentAnimation = animations.get("runLeft");
                playerState = Direction.FACING_LEFT;

                if(weaponEquipped != null) {
                    currentWeaponAnim = animations.get(weaponEquipped + "BackRunLeft");
                    currentWeaponAnim.Synchronize(currentAnimation);
                }
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

                        if(weaponEquipped != null) {
                            currentWeaponAnim = animations.get(weaponEquipped + "BackIdleUp");
                            currentWeaponAnim.Synchronize(currentAnimation);
                        }
                        break;
                    case FACING_UP_RIGHT:
                        currentAnimation = animations.get("idleUpRight");

                        if(weaponEquipped != null) {
                            currentWeaponAnim = animations.get(weaponEquipped + "BackIdleUpRight");
                            currentWeaponAnim.Synchronize(currentAnimation);
                        }
                        break;
                    case FACING_RIGHT:
                        currentAnimation = animations.get("idleRight");

                        if(weaponEquipped != null) {
                            currentWeaponAnim = animations.get(weaponEquipped + "BackIdleRight");
                            currentWeaponAnim.Synchronize(currentAnimation);
                        }
                        break;
                    case FACING_RIGHT_DOWN:
                        currentAnimation = animations.get("idleRightDown");

                        if(weaponEquipped != null) {
                            currentWeaponAnim = animations.get(weaponEquipped + "BackIdleRightDown");
                            currentWeaponAnim.Synchronize(currentAnimation);
                        }
                        break;
                    case FACING_DOWN:
                        currentAnimation = animations.get("idleDown");

                        if(weaponEquipped != null) {
                            currentWeaponAnim = animations.get(weaponEquipped + "BackIdleDown");
                            currentWeaponAnim.Synchronize(currentAnimation);
                        }
                        break;
                    case FACING_DOWN_LEFT:
                        currentAnimation = animations.get("idleDownLeft");

                        if(weaponEquipped != null) {
                            currentWeaponAnim = animations.get(weaponEquipped + "BackIdleDownLeft");
                            currentWeaponAnim.Synchronize(currentAnimation);
                        }
                        break;
                    case FACING_LEFT:
                        currentAnimation = animations.get("idleLeft");

                        if(weaponEquipped != null) {
                            currentWeaponAnim = animations.get(weaponEquipped + "BackIdleLeft");
                            currentWeaponAnim.Synchronize(currentAnimation);
                        }
                        break;
                    case FACING_LEFT_UP:
                        currentAnimation = animations.get("idleLeftUp");

                        if(weaponEquipped != null) {
                            currentWeaponAnim = animations.get(weaponEquipped + "BackIdleLeftUp");
                            currentWeaponAnim.Synchronize(currentAnimation);
                        }
                        break;
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
        else if(attackableState == AttackableState.DEAD) {
            currentAnimation = animations.get("dead");
            hp = 0;
        }
    }

    @Override
    public void render(SpriteBatch sb) {
        float x = getPosition().x * Constants.PPM - width / 2;
        float y = getPosition().y * Constants.PPM - height / 2 + 16;

        sb.begin();
        sb.draw(currentAnimation.getFrame(), x, y);
        if(weaponEquipped != null)
            sb.draw(currentWeaponAnim.getFrame(), x, y);
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

    public Direction getPlayerState() {
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
        playerState = Direction.FACING_DOWN;
        attackableState = AttackableState.ALIVE;
        body.setTransform(100, 100, 0);
        movementSpeed = maxMovementSpeed;
        body.setAwake(true);
    }

    public boolean isArrowsEmpty() {
        return arrows <= 0;
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

    public enum Direction {
        FACING_UP, FACING_UP_RIGHT, FACING_RIGHT, FACING_RIGHT_DOWN, FACING_DOWN, FACING_DOWN_LEFT, FACING_LEFT, FACING_LEFT_UP
    }

    public enum State {
        DRAWING_BOW, IDLE
    }

    public void setWeaponEquipped(Item weapon) {
        this.weaponEquipped = weapon;
    }

    public Item getWeaponEquipped() {
        return weaponEquipped;
    }

    public void pickItem(Item item) {
        items.add(item);
    }

    public ArrayList<Item> getItems() {
        return items;
    }

    public boolean isWeaponDrawn() {
        return weaponDrawn;
    }
}
