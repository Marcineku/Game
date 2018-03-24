package com.mygame.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import javafx.animation.Animation;

public class Box extends GameObject {
    private PolygonShape shape;

    private Animation anim;

    Box(float positionX, float positionY, World world, float colliderWidth, float colliderHeight) {
        super(BodyDef.BodyType.DynamicBody, positionX, positionY, 4.f, world, 0.12f, 0.12f, 0.12f);

        shape = new PolygonShape();
        shape.setAsBox(colliderWidth, colliderHeight);
        createFixture();

        animInit();
    }

    private void animInit() {
        Texture texture = new Texture("characters.png");
        TextureRegion[][] tmpFrames = TextureRegion.split(texture, 16, 16);

        currentFrame = tmpFrames[4][1];
    }

    @Override
    public void update(float elapsedTime) {

    }

    @Override
    public Vector2 getPosition() {
        return new Vector2(body.getPosition().x - 8, body.getPosition().y - 4);
    }

    @Override
    public void createFixture() {
        fixtureDef.shape = shape;
        fixture = body.createFixture(fixtureDef);
    }

    @Override
    public void dispose() {
        shape.dispose();
    }
}
