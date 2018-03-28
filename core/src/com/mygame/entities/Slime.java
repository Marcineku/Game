package com.mygame.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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
    private SlimeStates state;

    public Slime(float positionX, float positionY, World world) {
        super(BodyDef.BodyType.DynamicBody, positionX, positionY, 4.f, world, 0.f, 15.f, 0.12f);

        id = "slime";
        hp = 100;
        state = SlimeStates.ALIVE;

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(8.f / Constants.PPM, 6.f / Constants.PPM);
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

        TextureRegion[] dead = new TextureRegion[3];
        for(int i = 0; i < 3; ++i) {
            dead[i] = frames[4][6 + i];
        }
        addAnimation("dead", dead, frameDuration);
    }

    @Override
    public void update(float dt) {
        super.update(dt);

        if(hp <= 0) {
            hp = 0;
            state = SlimeStates.DEAD;
        }

        if(state == SlimeStates.DEAD) currentAnimation = animations.get("dead");
        else currentAnimation = animations.get("walkDown");
    }

    @Override
    public void hit(int damage) {
        hp -= damage;
    }

    @Override
    public int getHp() {
        return hp;
    }

    public SlimeStates getState() {
        return state;
    }

    @Override
    public void render(SpriteBatch sb) {
        sb.begin();
        sb.draw(currentAnimation.getFrame(),
                body.getPosition().x * Constants.PPM - width / 2,
                body.getPosition().y * Constants.PPM - height / 2 + 2
        );
        sb.end();
    }

    @Override
    public Vector2 getHpBarPosition() {
        return new Vector2(body.getPosition().x * Constants.PPM - 8.f, body.getPosition().y * Constants.PPM + 12.f);
    }

    public enum SlimeStates {
        ALIVE, DEAD
    }
}
