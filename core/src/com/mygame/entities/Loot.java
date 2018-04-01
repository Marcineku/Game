package com.mygame.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.World;
import com.mygame.game.MyGame;
import com.mygame.handlers.Constants;

public class Loot extends Sprite {
    private int gold;
    private boolean looted;

    public Loot(World world, float positionX, float positionY, int gold) {
        super(BodyDef.BodyType.DynamicBody, positionX, positionY, 5.f, world, 0.f, 15.f, 0.25f);

        id = "loot";
        layer = 1;
        this.gold = gold;
        looted = false;

        CircleShape shape = new CircleShape();
        shape.setRadius(8.f / Constants.PPM);
        fixtureDef.shape = shape;
        fixtureDef.filter.categoryBits = Constants.BIT_LOOT;
        fixtureDef.isSensor = true;
        fixture = body.createFixture(fixtureDef);
        fixture.setUserData(this);
        shape.dispose();

        Texture tex = MyGame.assets.getTexture("treasures");
        TextureRegion[][] frames = TextureRegion.split(tex, 16, 16);

        TextureRegion[] gld = new TextureRegion[1];
        gld[0] = frames[2][0];
        addAnimation("gold", gld, 0);
        currentAnimation = animations.get("gold");
    }

    public int getGold() {
        looted = true;
        return gold;
    }

    public boolean isLooted() {
        return looted;
    }
}