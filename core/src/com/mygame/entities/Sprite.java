package com.mygame.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.mygame.handlers.Animation;
import com.mygame.handlers.Constants;

import java.util.HashMap;

public abstract class Sprite implements Comparable<Sprite> {
    protected Body                       body;
    protected FixtureDef                 fixtureDef;
    protected Fixture                    fixture;
    protected HashMap<String, Animation> animations;
    protected Animation                  currentAnimation;
    protected float                      width;
    protected float                      height;
    protected int                        layer;

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

    public void addAnimation(String name, TextureRegion[] reg, float delay) {
        animations.put(name, new Animation(reg, delay));
        currentAnimation = animations.get(name);

        width = reg[0].getRegionWidth();
        height = reg[0].getRegionHeight();
    }

    public void addDirectionalAnimation(String name, Texture texture, int framesPerDirection, float frameDuration) {
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
        addAnimation(name + "Up",        up,        frameDuration);
        addAnimation(name + "UpRight",   upRight,   frameDuration);
        addAnimation(name + "Right",     right,     frameDuration);
        addAnimation(name + "RightDown", rightDown, frameDuration);
        addAnimation(name + "Down",      down,      frameDuration);
        addAnimation(name + "DownLeft",  downLeft,  frameDuration);
        addAnimation(name + "Left",      left,      frameDuration);
        addAnimation(name + "LeftUp",    leftUp,    frameDuration);
    }

    public void addDirectionalAnimation(String name, Texture texture, int framesPerDirection, float frameDuration, boolean loop) {
        addDirectionalAnimation(name, texture, framesPerDirection, frameDuration);

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

    public void addDirectionalAnimation(String name, Texture texture, int framesPerDirection, float frameDuration, boolean loop, boolean reverse) {
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
            addAnimation(name + "UpReverse",        up,        frameDuration);
            addAnimation(name + "UpRightReverse",   upRight,   frameDuration);
            addAnimation(name + "RightReverse",     right,     frameDuration);
            addAnimation(name + "RightDownReverse", rightDown, frameDuration);
            addAnimation(name + "DownReverse",      down,      frameDuration);
            addAnimation(name + "DownLeftReverse",  downLeft,  frameDuration);
            addAnimation(name + "LeftReverse",      left,      frameDuration);
            addAnimation(name + "LeftUpReverse",    leftUp,    frameDuration);

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
            addDirectionalAnimation(name, texture, framesPerDirection, frameDuration, loop);
        }
    }

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

    protected void defineMainCollider(float radius, short categoryBits, Object userData) {
        CircleShape shape = new CircleShape();
        shape.setRadius(radius / Constants.PPM);
        fixtureDef.shape = shape;
        fixtureDef.filter.categoryBits = categoryBits;
        fixture = body.createFixture(fixtureDef);
        fixture.setUserData(userData);
        shape.dispose();
    }

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

    protected void defineMainCollider(float width, float height, short categoryBits, Object userData) {
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(width / Constants.PPM, height / Constants.PPM);
        fixtureDef.shape = shape;
        fixtureDef.filter.categoryBits = categoryBits;
        fixture = body.createFixture(fixtureDef);
        fixture.setUserData(userData);
        shape.dispose();
    }

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
}
