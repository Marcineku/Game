package com.mygame.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.mygame.game.MyGame;
import com.mygame.handlers.Constants;

public class Arrow extends Sprite {
    private boolean active;
    private boolean looted;

    public Arrow(World world, float positionX, float positionY) {
        super(BodyDef.BodyType.DynamicBody, positionX, positionY, 5.f, world, 0.f, 0.25f, 0.f);
        body.setFixedRotation(true);
        body.setBullet(true);

        id = "arrow";
        layer = 1;
        active = true;
        looted = false;

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(4.f / Constants.PPM, 3.f / Constants.PPM);
        fixtureDef.shape = shape;
        fixtureDef.filter.categoryBits = Constants.BIT_ARROW;
        fixtureDef.isSensor = true;
        fixture = body.createFixture(fixtureDef);
        fixture.setUserData(this);
        shape.dispose();

        Texture tex = MyGame.assets.getTexture("arrow");
        TextureRegion[][] frames = TextureRegion.split(tex, 32, 32);

        float frameDuration = 1/12f;

        TextureRegion[] movingArrow = new TextureRegion[4];
        for(int i = 0; i < 2; ++i) {
            for(int j = 0; j < 2; ++j) {
                movingArrow[2*i+j] = frames[i][j];
            }
        }
        addAnimation("movingArrow", movingArrow, frameDuration);

        TextureRegion[] standingArrow = new TextureRegion[1];
        standingArrow[0] = frames[0][0];
        addAnimation("standingArrow", standingArrow, 0.f);

        currentAnimation = animations.get("movingArrow");
    }

    @Override
    public void update(float dt) {
        super.update(dt);

        float padding = 0.1f;
        if(body.getLinearVelocity().x < padding && body.getLinearVelocity().y < padding) {
            active = false;
            currentAnimation = animations.get("standingArrow");
        }
    }

    @Override
    public void render(SpriteBatch sb) {
        float x = body.getPosition().x * Constants.PPM - width / 2;
        float y = body.getPosition().y * Constants.PPM - 3;
        float rot = (float) Math.toDegrees(body.getAngle());
        sb.begin();
        sb.draw(currentAnimation.getFrame(), x, y, width/2, 3, width, height, 1.f, 1.f, rot);
        sb.end();
    }

    public boolean isLooted() {
        return looted;
    }

    public void setLooted(boolean looted) {
        this.looted = looted;
    }

    public boolean isActive() {
        return active;
    }
}
