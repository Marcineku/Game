package com.mygame.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;
import com.mygame.game.MyGame;
import com.mygame.handlers.Animation;
import com.mygame.handlers.Constants;

/**
 * Contains box2d physics object, all animations and information related to item
 */

public class Item extends Sprite{
    private String itemName;
    private int damage;
    private int price;
    private boolean looted;
    private boolean highlighted;
    private Animation highlightedAnimation;

    /**
     * @param world box2d world object in which item's collider will be spawned
     * @param positionX x coordinate of spawning position
     * @param positionY y coordinate of spawning position
     * @param itemName static name of an item from Constants class
     */
    public Item(World world, float positionX, float positionY, String itemName) {
        super(BodyDef.BodyType.KinematicBody, positionX, positionY, 0.f, world, 0.f, 0.f, 0.f);

        layer = 1;
        this.itemName = itemName;
        looted = false;
        highlighted = false;

        defineMainCollider(8.f, 12.f, Constants.BIT_ITEM, this, true);

        //Item animation
        Texture itemTex = MyGame.assets.getTexture(itemName);
        TextureRegion[][] itemFrames = TextureRegion.split(itemTex, 52, 52);
        TextureRegion[] item = new TextureRegion[1];
        item[0] = itemFrames[0][0];
        addAnimation(itemName, item, 0);

        TextureRegion[] itemHighlighted = new TextureRegion[1];
        itemHighlighted[0] = itemFrames[0][1];
        addAnimation(itemName + "Highlighted", itemHighlighted, 0);

        currentAnimation = animations.get(itemName);
        highlightedAnimation = animations.get(itemName + "Highlighted");

        if(itemName.equals(Constants.ITEM_BOW)) {
            damage = 30;
            price = 20;
        }
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

    public String getItemName() {
        return itemName;
    }

    /**
     * @return position at which item's name will be drawn
     */
    public Vector2 getNamePosition() {
        return new Vector2(body.getPosition().x * Constants.PPM - 8.f, body.getPosition().y * Constants.PPM + 40.f);
    }

    public int getDamage() {
        return damage;
    }

    public int getPrice() {
        return price;
    }

    public void setLooted(boolean looted) {
        this.looted = looted;
    }

    public boolean isLooted() {
        return looted;
    }

    public void setHighlighted(boolean highlighted) {
        this.highlighted = highlighted;
    }

    public boolean isHighlighted() {
        return highlighted;
    }

    @Override
    public String toString() {
        return itemName;
    }
}
