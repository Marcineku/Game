package com.mygame.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.mygame.game.MyGame;
import com.mygame.handlers.Constants;
import com.mygame.interfaces.Attackable;

public class Slime extends Sprite implements Attackable {
    private int hp;

    public Slime(float positionX, float positionY, World world) {
        super(BodyDef.BodyType.DynamicBody, positionX, positionY, 4.f, world, 0.f, 15.f, 0.12f);

        id = "slime";
        hp = 100;

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(8.f / Constants.PPM, 6.f / Constants.PPM, new Vector2(0, -2.f / Constants.PPM), 0.f);
        fixtureDef.shape = shape;
        fixtureDef.filter.categoryBits = Constants.BIT_ENEMY;
        fixture = body.createFixture(fixtureDef);
        fixture.setUserData(this);
        shape.dispose();

        Texture tex = MyGame.assets.getTexture("characters");
        TextureRegion[][] frames = TextureRegion.split(tex, 16, 16);

        float frameDuration = 1/12.f;

        TextureRegion[] walkDown = new TextureRegion[3];
        for(int i = 0; i < 3; ++i) {
            walkDown[i] = frames[4][i];
        }
        addAnimation("walkDown", walkDown, frameDuration);
    }

    @Override
    public void hit(int damage) {
        hp -= damage;
    }

    @Override
    public int getHp() {
        return hp;
    }
}
