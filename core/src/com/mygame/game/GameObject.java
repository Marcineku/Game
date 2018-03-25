package com.mygame.game;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

public abstract class GameObject {
    String ID;
    Body body;
    FixtureDef fixtureDef;
    Fixture fixture;
    TextureRegion currentFrame;

    GameObject(BodyDef.BodyType type,
                      float positionX,
                      float positionY,
                      float linearDamping,
                      World world,
                      float restitution,
                      float density,
                      float friction) {
        BodyDef bodyDef;
        bodyDef = new BodyDef();
        bodyDef.type = type;
        bodyDef.fixedRotation = true;
        bodyDef.active = true;
        bodyDef.allowSleep = true;
        bodyDef.awake = true;
        bodyDef.position.set(positionX, positionY);
        bodyDef.linearDamping = linearDamping;
        body = world.createBody(bodyDef);

        fixtureDef = new FixtureDef();
        fixtureDef.restitution = restitution;
        fixtureDef.density = density;
        fixtureDef.friction = friction;
    }

    public TextureRegion getCurrentFrame() {
        return currentFrame;
    }

    public abstract Vector2 getPosition();

    public abstract void update(float elapsedTime);

    public abstract void dispose();
}
