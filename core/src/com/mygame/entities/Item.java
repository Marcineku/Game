package com.mygame.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.mygame.game.MyGame;
import com.mygame.handlers.Animation;
import com.mygame.handlers.Constants;

public class Item extends Sprite{
    private String itemName;
    private int damage;
    private int price;
    private boolean looted;
    private boolean highlighted;
    private Animation highlightedAnimation;

    public Item(World world, float positionX, float positionY, String itemName) {
        super(BodyDef.BodyType.KinematicBody, positionX, positionY, 0.f, world, 0.f, 0.f, 0.f);

        layer = 1;
        this.itemName = itemName;
        looted = false;
        highlighted = false;

        //Defining main collider
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(8 / Constants.PPM, 12 / Constants.PPM);
        fixtureDef.filter.categoryBits = Constants.BIT_ITEM;
        fixtureDef.shape = shape;
        fixture = body.createFixture(fixtureDef);
        fixture.setUserData(this);
        fixture.setSensor(true);
        shape.dispose();

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
            damage = 10;
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
