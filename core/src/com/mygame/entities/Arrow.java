package com.mygame.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.mygame.game.MyGame;
import com.mygame.handlers.Animation;
import com.mygame.handlers.Constants;

public class Arrow extends Sprite {
    private boolean active;
    private boolean looted;
    private boolean highlighted;
    private Animation highlightedAnimation;
    private Sprite target;
    private boolean flipped;
    private int damage;
    private float random;

    public Arrow(World world, float positionX, float positionY, int damage) {
        super(BodyDef.BodyType.DynamicBody, positionX, positionY, 5.f, world, 0.f, 0.25f, 0.f);

        layer       = 2;
        active      = true;
        looted      = false;
        highlighted = false;
        flipped     = false;
        this.damage = damage;

        //Generating random radius from a center of a hit body in pixels
        float min = 4;
        float max = 11;
        random = (float) Math.random() * (max - min) + min;

        defineMainCollider(2.5f, 6.5f, Constants.BIT_ARROW, this, true);

        //Arrow animation
        Texture arrowTex = MyGame.assets.getTexture("arrow");
        TextureRegion[][] arrowFrames = TextureRegion.split(arrowTex, 52, 52);
        TextureRegion[] arrow = new TextureRegion[1];
        arrow[0] = arrowFrames[0][0];
        addAnimation("arrow", arrow, 0);

        TextureRegion[] arrowHighlighted = new TextureRegion[1];
        arrowHighlighted[0] = arrowFrames[0][1];
        addAnimation("arrowHighlighted", arrowHighlighted, 0);

        TextureRegion[] arrowThrusted = new TextureRegion[1];
        arrowThrusted[0] = arrowFrames[0][2];
        addAnimation("arrowThrusted", arrowThrusted, 0);

        TextureRegion[] arrowGrounded = new TextureRegion[4];
        for(int i = 0; i < arrowGrounded.length; ++i) {
            arrowGrounded[i] = arrowFrames[0][i+3];
        }
        addAnimation("arrowGrounded", arrowGrounded, 0.12f);
        animations.get("arrowGrounded").setLoop(false);

        TextureRegion[] arrowGroundedHighlighted = new TextureRegion[1];
        arrowGroundedHighlighted[0] = arrowFrames[0][7];
        addAnimation("arrowGroundedHighlighted", arrowGroundedHighlighted, 0);

        currentAnimation     = animations.get("arrow");
        highlightedAnimation = animations.get("arrowHighlighted");
    }

    @Override
    public void update(float dt) {
        super.update(dt);
        highlightedAnimation.update(dt);

        float padding = 1.5f;
        Vector2 v = body.getLinearVelocity();
        if((v.x < padding && v.x > -padding) && (v.y < padding && v.y > -padding) && target == null) {
            active = false;
            currentAnimation = animations.get("arrowGrounded");
            highlightedAnimation = animations.get("arrowGroundedHighlighted");

            if(!flipped) {
                flipped = true;
                currentAnimation.flip(false, true);
                highlightedAnimation.flip(false, true);

                if(Math.toDegrees(body.getAngle()) > 180) {
                    float x = body.getPosition().x - 5.5f / Constants.PPM;
                    float y = body.getPosition().y - 1.5f / Constants.PPM;
                    float angle = body.getAngle() + (float) Math.toRadians(45);
                    body.setTransform(x, y, angle);
                }
                else {
                    float x = body.getPosition().x + 5.5f / Constants.PPM;
                    float y = body.getPosition().y - 1.5f / Constants.PPM;
                    float angle = body.getAngle() - (float) Math.toRadians(45);
                    body.setTransform(x, y, angle);
                }
            }
        }

        if(target != null && target.getBody().isActive()) {
            active = false;
            currentAnimation = animations.get("arrowThrusted");
            layer = target.getLayer() + 1;

            float radius = random / Constants.PPM;
            float x = target.getPosition().x + radius * (float) Math.cos(body.getAngle() + Math.toRadians(90));
            float y = target.getPosition().y + radius * (float) Math.sin(body.getAngle() + Math.toRadians(90));
            body.setTransform(x, y, body.getAngle());
        }
    }

    @Override
    public void render(SpriteBatch sb) {
        float xOffset = 26.5f;
        float yOffset = 26.5f;
        float x = body.getPosition().x * Constants.PPM - xOffset;
        float y = body.getPosition().y * Constants.PPM - yOffset;
        float rot = (float) Math.toDegrees(body.getAngle()) - 90;
        if(!active && currentAnimation == animations.get("arrowGrounded")) {
            xOffset = 32.f;
            yOffset = 28.f;
            x = body.getPosition().x * Constants.PPM - xOffset;
            y = body.getPosition().y * Constants.PPM - yOffset;

            if(flipped) {
                rot -= 45;
            }
            else {
                rot += 45;
            }
        }
        sb.begin();
        sb.draw(currentAnimation.getFrame(), x, y, xOffset, yOffset, width, height, 1.f, 1.f, rot);
        if(highlighted) {
            sb.draw(highlightedAnimation.getFrame(), x, y, xOffset, yOffset, width, height, 1.f, 1.f, rot);
        }
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

    public void setHighlighted(boolean highlighted) {
        this.highlighted = highlighted;
    }

    public boolean isHighlighted() {
        return highlighted;
    }

    public void setTarget(Sprite target) {
        this.target = target;
    }

    public int getDamage() {
        return damage;
    }
}
