package com.mygame.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

public class Box extends GameObject {
    private Texture texture;

    Box(float positionX, float positionY, World world) {
        super(BodyDef.BodyType.DynamicBody, positionX, positionY, 4.f, world, 0.f, 15.f, 0.12f);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(8.f / Constants.PPM, 6.f / Constants.PPM);
        fixtureDef.shape = shape;
        fixture = body.createFixture(fixtureDef);

        animInit();

        shape.dispose();
    }

    private void animInit() {
        texture = new Texture("images\\characters.png");
        TextureRegion[][] tmpFrames = TextureRegion.split(texture, 16, 16);

        currentFrame = tmpFrames[4][1];
    }

    @Override
    public void update(float elapsedTime) {

    }

    @Override
    public Vector2 getPosition() {
        return new Vector2(body.getPosition().x * Constants.PPM - 8, body.getPosition().y * Constants.PPM - 6);
    }

    @Override
    public void dispose() {
        texture.dispose();
    }
}
