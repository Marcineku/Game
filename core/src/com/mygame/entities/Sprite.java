package com.mygame.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.mygame.handlers.Animation;
import com.mygame.handlers.Constants;

import java.util.HashMap;

/**
 * All displayable objects should inherit from this class,
 * contains all elements needed for creating physics object in box2d's world,
 * also contains hash map for animations and current animation that will be displayed
 * during render
 */
public abstract class Sprite implements Comparable<Sprite> {
    protected Body                       body;
    protected FixtureDef                 fixtureDef;
    protected Fixture                    fixture;
    protected HashMap<String, Animation> animations;
    protected Animation                  currentAnimation;
    protected float                      width;
    protected float                      height;
    protected int                        layer;

    /**
     * @param type type of box2d's body (kinematic, dynamic or static)
     * @param positionX x coordinate of spawning position
     * @param positionY y coordinate of spawning position
     * @param linearDamping used for reducing linear speed
     * @param world box2d world object in which collider of an object will be spawned
     * @param restitution "bounciness" of a collider
     * @param density how heavy collider will be compared to its size
     * @param friction how much velocity will be subtract after collision
     */
    public Sprite(BodyDef.BodyType type,
                  float positionX,
                  float positionY,
                  float linearDamping,
                  World world,
                  float restitution,
                  float density,
                  float friction) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = type;
        bodyDef.fixedRotation = true;
        bodyDef.active = true;
        bodyDef.allowSleep = true;
        bodyDef.awake = true;
        bodyDef.position.set(positionX / Constants.PPM, positionY / Constants.PPM);
        bodyDef.linearDamping = linearDamping;
        body = world.createBody(bodyDef);

        fixtureDef = new FixtureDef();
        fixtureDef.restitution = restitution;
        fixtureDef.density = density;
        fixtureDef.friction = friction;

        animations = new HashMap<String, Animation>();
    }

    /**
     * @param name string identifier for animation
     * @param reg array of frames which animations will be made of
     * @param delay delay between frames
     */
    public void addAnimation(String name, TextureRegion[] reg, float delay) {
        animations.put(name, new Animation(reg, delay));
        currentAnimation = animations.get(name);

        width = reg[0].getRegionWidth();
        height = reg[0].getRegionHeight();
    }

    /**
     * @param name string identifier for animation
     * @param texture texture that contains 8-direction animation
     * @param framesPerDirection how much frames one direction contains
     * @param delay delay between frames
     */
    public void addDirectionalAnimation(String name, Texture texture, int framesPerDirection, float delay) {
        TextureRegion[][] frames = TextureRegion.split(texture, 52, 52);

        TextureRegion[] up        = new TextureRegion[framesPerDirection];
        TextureRegion[] upRight   = new TextureRegion[framesPerDirection];
        TextureRegion[] right     = new TextureRegion[framesPerDirection];
        TextureRegion[] rightDown = new TextureRegion[framesPerDirection];
        TextureRegion[] down      = new TextureRegion[framesPerDirection];
        TextureRegion[] downLeft  = new TextureRegion[framesPerDirection];
        TextureRegion[] left      = new TextureRegion[framesPerDirection];
        TextureRegion[] leftUp    = new TextureRegion[framesPerDirection];
        copyFrames(up, upRight, right, rightDown, down, downLeft, left, leftUp, frames);
        addAnimation(name + "Up",        up,        delay);
        addAnimation(name + "UpRight",   upRight,   delay);
        addAnimation(name + "Right",     right,     delay);
        addAnimation(name + "RightDown", rightDown, delay);
        addAnimation(name + "Down",      down,      delay);
        addAnimation(name + "DownLeft",  downLeft,  delay);
        addAnimation(name + "Left",      left,      delay);
        addAnimation(name + "LeftUp",    leftUp,    delay);
    }

    /**
     * @param name string identifier for animation
     * @param texture texture that contains 8-direction animation
     * @param framesPerDirection how much frames one direction contains
     * @param delay delay between frames
     * @param loop is animation going to loop
     */
    public void addDirectionalAnimation(String name, Texture texture, int framesPerDirection, float delay, boolean loop) {
        addDirectionalAnimation(name, texture, framesPerDirection, delay);

        if(!loop) {
            animations.get(name + "Up").setLoop(false);
            animations.get(name + "UpRight").setLoop(false);
            animations.get(name + "Right").setLoop(false);
            animations.get(name + "RightDown").setLoop(false);
            animations.get(name + "Down").setLoop(false);
            animations.get(name + "DownLeft").setLoop(false);
            animations.get(name + "Left").setLoop(false);
            animations.get(name + "LeftUp").setLoop(false);
        }
    }

    /**
     * @param name string identifier for animation
     * @param texture texture that contains 8-direction animation
     * @param framesPerDirection how much frames one direction contains
     * @param delay delay between frames
     * @param loop is animation going to loop
     * @param reverse are frames going to be displayed in opposite order
     */
    public void addDirectionalAnimation(String name, Texture texture, int framesPerDirection, float delay, boolean loop, boolean reverse) {
        if(reverse) {
            TextureRegion[][] frames = TextureRegion.split(texture, 52, 52);

            TextureRegion[] up        = new TextureRegion[framesPerDirection];
            TextureRegion[] upRight   = new TextureRegion[framesPerDirection];
            TextureRegion[] right     = new TextureRegion[framesPerDirection];
            TextureRegion[] rightDown = new TextureRegion[framesPerDirection];
            TextureRegion[] down      = new TextureRegion[framesPerDirection];
            TextureRegion[] downLeft  = new TextureRegion[framesPerDirection];
            TextureRegion[] left      = new TextureRegion[framesPerDirection];
            TextureRegion[] leftUp    = new TextureRegion[framesPerDirection];
            copyFrames(up, upRight, right, rightDown, down, downLeft, left, leftUp, frames);
            addAnimation(name + "UpReverse",        up,        delay);
            addAnimation(name + "UpRightReverse",   upRight,   delay);
            addAnimation(name + "RightReverse",     right,     delay);
            addAnimation(name + "RightDownReverse", rightDown, delay);
            addAnimation(name + "DownReverse",      down,      delay);
            addAnimation(name + "DownLeftReverse",  downLeft,  delay);
            addAnimation(name + "LeftReverse",      left,      delay);
            addAnimation(name + "LeftUpReverse",    leftUp,    delay);

            if(!loop) {
                animations.get(name + "UpReverse").setLoop(false);
                animations.get(name + "UpRightReverse").setLoop(false);
                animations.get(name + "RightReverse").setLoop(false);
                animations.get(name + "RightDownReverse").setLoop(false);
                animations.get(name + "DownReverse").setLoop(false);
                animations.get(name + "DownLeftReverse").setLoop(false);
                animations.get(name + "LeftReverse").setLoop(false);
                animations.get(name + "LeftUpReverse").setLoop(false);
            }

            animations.get(name + "UpReverse").reverse();
            animations.get(name + "UpRightReverse").reverse();
            animations.get(name + "RightReverse").reverse();
            animations.get(name + "RightDownReverse").reverse();
            animations.get(name + "DownReverse").reverse();
            animations.get(name + "DownLeftReverse").reverse();
            animations.get(name + "LeftReverse").reverse();
            animations.get(name + "LeftUpReverse").reverse();
        }
        else {
            addDirectionalAnimation(name, texture, framesPerDirection, delay, loop);
        }
    }

    /**
     * Updates current's animation frame
     * @param dt time since last update
     */
    public void update(float dt) {
        currentAnimation.update(dt);
    }

    public void render(SpriteBatch sb) {
        sb.begin();
        sb.draw(currentAnimation.getFrame(),
                body.getPosition().x * Constants.PPM - width / 2,
                body.getPosition().y * Constants.PPM - height / 2
        );
        sb.end();
    }

    /**
     * @return box2d body
     */
    public Body getBody() {
        return body;
    }

    public Vector2 getPosition() {
        return body.getPosition();
    }

    public Animation getCurrentAnimation() {
        return currentAnimation;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public Fixture getFixture() {
        return fixture;
    }

    @Override
    public int compareTo(Sprite o) {
        if(this.layer > o.layer) {
            return 1;
        }
        else if(this.layer == o.layer) {
            return 0;
        }
        else if(this.layer < o.layer) {
            return -1;
        }

        return 0;
    }

    public int getLayer() {
        return layer;
    }

    private void copyFrames(TextureRegion[] up, TextureRegion[] upRight, TextureRegion[] right, TextureRegion[] rightDown, TextureRegion[] down, TextureRegion[] downLeft, TextureRegion[] left, TextureRegion[] leftUp, TextureRegion[][] frames) {
        for(int i = 0; i < up.length; ++i) {
            up[i]        = frames[0][i];
            upRight[i]   = frames[1][i];
            right[i]     = frames[2][i];
            rightDown[i] = frames[3][i];
            down[i]      = frames[4][i];
            downLeft[i]  = frames[5][i];
            left[i]      = frames[6][i];
            leftUp[i]    = frames[7][i];
        }
    }

    /**
     * Must be called once from inheriting class to complete the process of creating box2d body
     * @param radius radius of a circle collider
     * @param categoryBits collision mask, should consist of collision's
     *                     mask bits that are defined in Constants class
     *                     (few collision bits can be connected with OR operator)
     * @param userData user data that can be accessed in contact listener
     *                     (usually "this" that allows to store whole object that it concerns)
     */
    protected void defineMainCollider(float radius, short categoryBits, Object userData) {
        CircleShape shape = new CircleShape();
        shape.setRadius(radius / Constants.PPM);
        fixtureDef.shape = shape;
        fixtureDef.filter.categoryBits = categoryBits;
        fixture = body.createFixture(fixtureDef);
        fixture.setUserData(userData);
        shape.dispose();
    }

    /**
     * Must be called once from inheriting class to complete the process of creating box2d body
     * @param radius radius of a circle collider
     * @param categoryBits collision mask, should consist of collision's
     *                     mask bits that are defined in Constants class
     *                     (few collision bits can be connected with OR operator)
     * @param userData user data that can be accessed in contact listener
     *                     (usually "this" that allows to store whole object that it concerns)
     * @param isSensor is collider going to be a sensor (box2d will just
     *                      listen for contacts but body itself wont collide with another body)
     */
    protected void defineMainCollider(float radius, short categoryBits, Object userData, boolean isSensor) {
        CircleShape shape = new CircleShape();
        shape.setRadius(radius / Constants.PPM);
        fixtureDef.shape = shape;
        fixtureDef.filter.categoryBits = categoryBits;
        fixture = body.createFixture(fixtureDef);
        fixture.setUserData(userData);
        fixture.setSensor(isSensor);
        shape.dispose();
    }

    /**
     * Must be called once from inheriting class to complete the process of creating box2d body
     * @param width width of a box collider
     * @param height height of a box collider
     * @param categoryBits collision mask, should consist of collision's
     *                  mask bits that are defined in Constants class
     *                  (few collision bits can be connected with OR operator)
     * @param userData user data that can be accessed in contact listener
     *                  (usually "this" that allows to store whole object that it concerns)
     */
    protected void defineMainCollider(float width, float height, short categoryBits, Object userData) {
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(width / Constants.PPM, height / Constants.PPM);
        fixtureDef.shape = shape;
        fixtureDef.filter.categoryBits = categoryBits;
        fixture = body.createFixture(fixtureDef);
        fixture.setUserData(userData);
        shape.dispose();
    }

    /**
     * Must be called once from inheriting class to complete the process of creating box2d body
     * @param width width of a box collider
     * @param height height of a box collider
     * @param categoryBits collision mask, should consist of collision's
     *                  mask bits that are defined in Constants class
     *                  (few collision bits can be connected with OR operator)
     * @param userData user data that can be accessed in contact listener
     *                  (usually "this" that allows to store whole object that it concerns)
     * @param isSensor is collider going to be a sensor (box2d will just
     *                  listen for contacts but body itself wont collide with another body)
     */
    protected void defineMainCollider(float width, float height, short categoryBits, Object userData, boolean isSensor) {
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(width / Constants.PPM, height / Constants.PPM);
        fixtureDef.shape = shape;
        fixtureDef.filter.categoryBits = categoryBits;
        fixture = body.createFixture(fixtureDef);
        fixture.setUserData(userData);
        fixture.setSensor(isSensor);
        shape.dispose();
    }

    /**
     * Procedure will synchronize all 8 directions of given directional animation, providing that all
     * of them will be in same state
     * @param name name of the animation to synchronize
     * @param animation animation that "name" will be synchronized with
     */
    public void synchronizeDirectionalAnimation(String name, Animation animation) {
        animations.get(name + Direction.UP).synchronize(animation);
        animations.get(name + Direction.UP_RIGHT).synchronize(animation);
        animations.get(name + Direction.RIGHT).synchronize(animation);
        animations.get(name + Direction.RIGHT_DOWN).synchronize(animation);
        animations.get(name + Direction.DOWN).synchronize(animation);
        animations.get(name + Direction.DOWN_LEFT).synchronize(animation);
        animations.get(name + Direction.LEFT).synchronize(animation);
        animations.get(name + Direction.LEFT_UP).synchronize(animation);
    }

    /**
     * Resets all 8 direction's of given animation
     * @param name name of the animation to reset
     */
    public void resetDirectionalAnimation(String name) {
        animations.get(name + Direction.UP).reset();
        animations.get(name + Direction.UP_RIGHT).reset();
        animations.get(name + Direction.RIGHT).reset();
        animations.get(name + Direction.RIGHT_DOWN).reset();
        animations.get(name + Direction.DOWN).reset();
        animations.get(name + Direction.DOWN_LEFT).reset();
        animations.get(name + Direction.LEFT).reset();
        animations.get(name + Direction.LEFT_UP).reset();
    }

    public HashMap<String, Animation> getAnimations() {
        return animations;
    }

    /**
     * Contains string name of 8 directions
     */
    public enum Direction {
        UP("Up"), UP_RIGHT("UpRight"), RIGHT("Right"), RIGHT_DOWN("RightDown"), DOWN("Down"), DOWN_LEFT("DownLeft"), LEFT("Left"), LEFT_UP("LeftUp");

        private String value;
        Direction(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return this.value;
        }
    }
}
