package com.mygame.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.mygame.handlers.Animation;
import com.mygame.handlers.Constants;

import java.util.HashMap;

public abstract class Sprite {
    protected String                     id;
    protected Body                       body;
    protected FixtureDef                 fixtureDef;
    protected Fixture                    fixture;
    protected HashMap<String, Animation> animations;
    protected Animation                  currentAnimation;
    protected float                      width;
    protected float                      height;

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

    @Override
    public String toString() {
        return id;
    }
}
