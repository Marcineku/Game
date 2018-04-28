package com.mygame.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.World;
import com.mygame.game.MyGame;
import com.mygame.handlers.Animation;
import com.mygame.handlers.Constants;

public class Loot extends Sprite {
    private int gold;
    private boolean looted;
    private boolean highlighted;
    private Animation highlightedAnimation;

    public Loot(World world, float positionX, float positionY, int gold) {
        super(BodyDef.BodyType.DynamicBody, positionX, positionY, 5.f, world, 0.f, 15.f, 0.25f);

        layer = 1;
        this.gold = gold;
        looted = false;
        highlighted = false;

        //Defining main collider
        defineMainCollider(8.f, 8.f, Constants.BIT_LOOT, this, true);

        Texture goldTex = MyGame.assets.getTexture("gold");
        TextureRegion[][] frames = TextureRegion.split(goldTex, 52, 52);

        TextureRegion[] goldFrames = new TextureRegion[6];
        for(int i = 0; i < goldFrames.length; ++i) {
            goldFrames[i] = frames[0][i];
        }
        addAnimation("gold", goldFrames, 0.1f);
        animations.get("gold").setLoop(false);
        TextureRegion[] goldHighlighted = new TextureRegion[1];
        goldHighlighted[0] = frames[0][6];
        addAnimation("goldHighlighted", goldHighlighted, 0);

        highlightedAnimation = animations.get("goldHighlighted");
        currentAnimation = animations.get("gold");

    }

    @Override
    public void render(SpriteBatch sb) {
        sb.begin();
        sb.draw(currentAnimation.getFrame(),
                body.getPosition().x * Constants.PPM - width / 2,
                body.getPosition().y * Constants.PPM - height / 2
        );
        if(highlighted) {
            sb.draw(highlightedAnimation.getFrame(), body.getPosition().x * Constants.PPM - width / 2, body.getPosition().y * Constants.PPM - height / 2);
        }
        sb.end();
    }

    public void setHighlighted(boolean highlighted) {
        this.highlighted = highlighted;
    }

    public int getGold() {
        looted = true;
        return gold;
    }

    public boolean isLooted() {
        return looted;
    }
}
