package com.mygame.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.World;

public class Player extends GameObject{
    private Texture texture;

    private CircleShape shape;
    private float movementSpeed;
    private PlayerState state;

    private Animation walkDownAnim;
    private Animation walkUpAnim;
    private Animation walkLeftAnim;
    private Animation walkRightAnim;

    private TextureRegion standingDown;
    private TextureRegion standingUp;
    private TextureRegion standingLeft;
    private TextureRegion standingRight;

    Player(float positionX, float positionY, World world, float movementSpeed, float colliderRadius) {
        super(BodyDef.BodyType.DynamicBody, positionX, positionY, 4.f, world, 0.f, 0.12f, 0.12f);

        shape = new CircleShape();
        shape.setRadius(colliderRadius);
        createFixture();

        this.movementSpeed = movementSpeed;

        animInit();
        state = PlayerState.FACING_DOWN;
    }

    private void animInit() {
        texture = new Texture("characters.png");
        TextureRegion[][] tmpFrames = TextureRegion.split(texture, 16, 16);

        float frameDuration = 0.15f;

        TextureRegion[] tmpWalkDown = new TextureRegion[3];
        System.arraycopy(tmpFrames[0], 0, tmpWalkDown, 0, 3);
        walkDownAnim = new Animation(frameDuration, tmpWalkDown);
        standingDown = tmpWalkDown[1];

        TextureRegion[] tmpWalkUp = new TextureRegion[3];
        System.arraycopy(tmpFrames[3], 0, tmpWalkUp, 0, 3);
        walkUpAnim = new Animation(frameDuration, tmpWalkUp);
        standingUp = tmpWalkUp[1];

        TextureRegion[] tmpWalkLeft = new TextureRegion[3];
        System.arraycopy(tmpFrames[1], 0, tmpWalkLeft, 0, 3);
        walkLeftAnim = new Animation(frameDuration, tmpWalkLeft);
        standingLeft = tmpWalkLeft[1];

        TextureRegion[] tmpWalkRight = new TextureRegion[3];
        System.arraycopy(tmpFrames[2], 0, tmpWalkRight, 0, 3);
        walkRightAnim = new Animation(frameDuration, tmpWalkRight);
        standingRight = tmpWalkRight[1];

        currentFrame = tmpFrames[0][1];
    }

    public void move(float elapsedTime) {
        if(Gdx.input.isKeyPressed(Input.Keys.W)) {
            this.body.applyLinearImpulse(0, movementSpeed, getPosition().x, getPosition().y, true);
            currentFrame = (TextureRegion) walkUpAnim.getKeyFrame(elapsedTime, true);
            state = PlayerState.FACING_UP;
        }
        if(Gdx.input.isKeyPressed(Input.Keys.S)) {
            this.body.applyLinearImpulse(0, -movementSpeed, getPosition().x, getPosition().y, true);
            currentFrame = (TextureRegion) walkDownAnim.getKeyFrame(elapsedTime, true);
            state = PlayerState.FACING_DOWN;
        }
        if(Gdx.input.isKeyPressed(Input.Keys.A)) {
            this.body.applyLinearImpulse(-movementSpeed, 0, getPosition().x, getPosition().y, true);
            currentFrame = (TextureRegion) walkLeftAnim.getKeyFrame(elapsedTime, true);
            state = PlayerState.FACING_LEFT;
        }
        if(Gdx.input.isKeyPressed(Input.Keys.D)) {
            this.body.applyLinearImpulse(movementSpeed, 0, getPosition().x, getPosition().y, true);
            currentFrame = (TextureRegion) walkRightAnim.getKeyFrame(elapsedTime, true);
            state = PlayerState.FACING_RIGHT;
        }

        float padding = 50.f;
        if(body.getLinearVelocity().x < padding && body.getLinearVelocity().x > -padding &&
           body.getLinearVelocity().y < padding && body.getLinearVelocity().y > -padding) {
            switch (state) {
                case FACING_UP:
                    currentFrame = standingUp;
                    break;
                case FACING_DOWN:
                    currentFrame = standingDown;
                    break;
                case FACING_LEFT:
                    currentFrame = standingLeft;
                    break;
                case FACING_RIGHT:
                    currentFrame = standingRight;
                    break;
            }
        }
    }

    @Override
    public void update(float elapsedTime) {

    }

    @Override
    public Vector2 getPosition() {
        return new Vector2(body.getPosition().x - 8, body.getPosition().y - 2);
    }

    @Override
    public void createFixture() {
        fixtureDef.shape = shape;
        fixture = body.createFixture(fixtureDef);
    }

    @Override
    public void dispose() {
        shape.dispose();
        texture.dispose();
    }

    public enum PlayerState {
        FACING_UP, FACING_DOWN, FACING_LEFT, FACING_RIGHT
    }
}
