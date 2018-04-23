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
    private Direction       direction;
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
    private int             exp;
    private int             damage;
    private boolean         hit;

    public Player(World world, float positionX, float positionY, Cursor cursor) {
        super(BodyDef.BodyType.DynamicBody, positionX, positionY, 6.f, world, 0.f, 25.f, 0.25f);

        this.cursor = cursor;

        layer = 5;
        direction = Direction.DOWN;
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
        exp = 0;
        damage = 0;
        hit = false;

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
        addDirectionalAnimation("bodyManBowDraw", MyGame.assets.getTexture("bodyManBowDraw"), 5, 0.1f, false);
        //Animation of hiding a bow
        addDirectionalAnimation("bodyManBowDraw", MyGame.assets.getTexture("bodyManBowDraw"), 5, 0.1f, false, true);

        //Animation of bow being drawn
        addDirectionalAnimation("bowDrawManIdle", MyGame.assets.getTexture("bowDrawManIdle"), 5, 0.1f, false);
        //Animation of bow being hidden
        addDirectionalAnimation("bowDrawManIdle", MyGame.assets.getTexture("bowDrawManIdle"), 5, 0.1f, false, true);

        //Animation of drawn bow while idling
        addDirectionalAnimation("bowDrawnIdle", MyGame.assets.getTexture("bowDrawnManIdle"), 5, 0.25f);

        //Animation of drawn bow while running
        addDirectionalAnimation("bowDrawnRun", MyGame.assets.getTexture("bowDrawnManRun"), 8, 0.09f);

        TextureRegion[] dead = new TextureRegion[1];
        dead[0] = getCurrentAnimation().getFrame();
        addAnimation("dead", dead, 0);

        direction = Direction.DOWN;
        currentAnimation = animations.get("idle" + direction);
        currentWeaponAnim = animations.get("bowBackIdle" + direction);
    }

    @Override
    public void update(float dt) {
        super.update(dt);

        if(weaponEquipped != null)
            currentWeaponAnim.update(dt);

        timer.update(dt);

        if(hp <= 0) {
            hp = 0;
            attackableState = AttackableState.DEAD;
            body.setAwake(false);
            walkingSound.stop();
            currentAnimation = animations.get("dead");
        }

        //Rotating player's body towards mouse cursor
        if(getAttackableState() == Attackable.AttackableState.ALIVE && state == State.IDLE) {
            Vector2 toTarget = new Vector2(cursor.getPosition().x / Constants.PPM, cursor.getPosition().y / Constants.PPM).sub(body.getPosition());
            float desiredAngle = (float) Math.atan2(-toTarget.x, toTarget.y) + (float) Math.toRadians(90);
            body.setTransform(body.getPosition(), desiredAngle);
        }

        //Drawing or hiding weapon
        if(MyInput.isPressed(MyInput.DRAW) && attackableState == AttackableState.ALIVE && state == State.IDLE && weaponEquipped != null && weaponEquipped.getItemName().equals(Constants.ITEM_BOW)) {
            state = State.DRAWING_BOW;
            walkingSound.stop();
            MyGame.assets.getSound("bowDraw").play(0.2f);

            String reverse;
            if(weaponDrawn) {
                reverse = "Reverse";
            }
            else {
                reverse = "";
            }

            currentAnimation = animations.get("bodyManBowDraw"+ direction + reverse);

            if(weaponEquipped != null) {
                currentWeaponAnim = animations.get("bowDrawManIdle" + direction + reverse);
                currentWeaponAnim.Synchronize(currentAnimation);
            }
        }

        if(state == State.DRAWING_BOW) {
            if(currentAnimation.hasEnded()) {
                state = State.IDLE;

                if(weaponDrawn) {
                    weaponDrawn = false;
                    movementSpeed = maxMovementSpeed;
                }
                else {
                    weaponDrawn = true;
                    movementSpeed = maxMovementSpeed - 5.f;
                }
            }
        }

        if(attackableState == AttackableState.ALIVE && state == State.IDLE) {
            move();

            if(weaponDrawn) {
                float angle = (float) Math.toDegrees(body.getAngle());

                if(angle < 22.5f && angle >= -22.5f) {
                    direction = Direction.RIGHT;
                }
                else if(angle < 67.5f && angle >= 22.5f) {
                    direction = Direction.UP_RIGHT;
                }
                else if(angle < 112.5f && angle >= 67.5f) {
                    direction = Direction.UP;
                }
                else if(angle < 157.5f && angle >= 112.5f) {
                    direction = Direction.LEFT_UP;
                }
                else if(angle < 202.5f && angle >= 157.5f) {
                    direction = Direction.LEFT;
                }
                else if(angle < 247.5f && angle >= 202.5f) {
                    direction = Direction.DOWN_LEFT;
                }
                else if((angle >= 247.5f && angle <= 270.f) || (angle >= -90.f && angle < -67.5f)) {
                    direction = Direction.DOWN;
                }
                else if(angle < -22.5f && angle >= -67.5f) {
                    direction = Direction.RIGHT_DOWN;
                }
            }

            currentAnimation = animations.get("run" + direction);

            if(weaponEquipped != null) {
                if(weaponDrawn) {
                    currentWeaponAnim = animations.get(weaponEquipped + "DrawnRun" + direction);
                }
                else {
                    currentWeaponAnim = animations.get(weaponEquipped + "BackRun" + direction);
                }

                currentWeaponAnim.Synchronize(currentAnimation);
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
                currentAnimation = animations.get("idle" + direction);

                if(weaponEquipped != null) {
                    if(weaponDrawn) {
                        currentWeaponAnim = animations.get(weaponEquipped + "DrawnIdle" + direction);
                    }
                    else {
                        currentWeaponAnim = animations.get(weaponEquipped + "BackIdle" + direction);
                    }

                    currentWeaponAnim.Synchronize(currentAnimation);
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
        this.damage = damage;
        hit = true;
    }

    @Override
    public int getHp() {
        return hp;
    }

    @Override
    public Vector2 getHpBarPosition() {
        return new Vector2(body.getPosition().x * Constants.PPM - 8.f, body.getPosition().y * Constants.PPM + 42.f);
    }

    public Direction getDirection() {
        return direction;
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
        direction = Direction.DOWN;
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

    private void move() {
        //up right
        if(MyInput.isDown(MyInput.UP) && MyInput.isDown(MyInput.RIGHT)) {
            this.body.applyLinearImpulse(movementSpeed / (float) Math.sqrt(2), movementSpeed / (float) Math.sqrt(2), body.getPosition().x, body.getPosition().y, true);
            direction = Direction.UP_RIGHT;
        }
        //right down
        else if(MyInput.isDown(MyInput.RIGHT) && MyInput.isDown(MyInput.DOWN)) {
            this.body.applyLinearImpulse(movementSpeed / (float) Math.sqrt(2),  -movementSpeed / (float) Math.sqrt(2), body.getPosition().x, body.getPosition().y, true);
            direction = Direction.RIGHT_DOWN;
        }
        //down left
        else if (MyInput.isDown(MyInput.DOWN) && MyInput.isDown(MyInput.LEFT)) {
            this.body.applyLinearImpulse(-movementSpeed / (float) Math.sqrt(2), -movementSpeed / (float) Math.sqrt(2), body.getPosition().x, body.getPosition().y, true);
            direction = Direction.DOWN_LEFT;
        }
        //left up
        else if(MyInput.isDown(MyInput.LEFT) && MyInput.isDown(MyInput.UP)) {
            this.body.applyLinearImpulse(-movementSpeed / (float) Math.sqrt(2), movementSpeed / (float) Math.sqrt(2), body.getPosition().x, body.getPosition().y, true);
            direction = Direction.LEFT_UP;
        }
        //up
        else if (MyInput.isDown(MyInput.UP)) {
            this.body.applyLinearImpulse(0, movementSpeed, body.getPosition().x, body.getPosition().y, true);
            direction = Direction.UP;
        }
        //right
        else if (MyInput.isDown(MyInput.RIGHT)) {
            this.body.applyLinearImpulse(movementSpeed, 0, body.getPosition().x, body.getPosition().y, true);
            direction = Direction.RIGHT;
        }
        //down
        else if (MyInput.isDown(MyInput.DOWN)) {
            this.body.applyLinearImpulse(0, -movementSpeed, body.getPosition().x, body.getPosition().y, true);
            direction = Direction.DOWN;
        }
        //left
        else if (MyInput.isDown(MyInput.LEFT)) {
            this.body.applyLinearImpulse(-movementSpeed, 0, body.getPosition().x, body.getPosition().y, true);
            direction = Direction.LEFT;
        }
    }

    public int getExp() {
        return exp;
    }

    public void addExp(int exp) {
        this.exp += exp;
    }

    public Timer getTimer() {
        return timer;
    }

    @Override
    public boolean isHit() {
        return hit;
    }

    @Override
    public void setHit(boolean hit) {
        this.hit = hit;
    }

    @Override
    public int getDamage() {
        return damage;
    }
}
