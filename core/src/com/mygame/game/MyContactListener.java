package com.mygame.game;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

public class MyContactListener implements ContactListener{
    @Override
    public void beginContact(Contact contact) {
        Fixture fa = contact.getFixtureA();
        Fixture fb = contact.getFixtureB();

        Body playerBody;

        Player player;
        Slime slime;

        //player and slime collision
        if((fa.getUserData().toString() == Constants.PLAYER_ID && fb.getUserData().toString() == Constants.SLIME_ID) || (fb.getUserData().toString() == Constants.PLAYER_ID && fa.getUserData().toString() == Constants.SLIME_ID)) {
            if(fa.getUserData().toString() == Constants.PLAYER_ID) {
                playerBody = fa.getBody();
                player = (Player) fa.getUserData();
                slime = (Slime) fb.getUserData();
            }
            else {
                playerBody = fb.getBody();
                player = (Player) fb.getUserData();
                slime = (Slime) fa.getUserData();
            }

            float impulsePower = 500.f;
            WorldManifold wm = contact.getWorldManifold();
            Vector2 n = wm.getNormal();
            playerBody.applyLinearImpulse(new Vector2(n.x * impulsePower, n.y * impulsePower), playerBody.getPosition(), true);

            player.hit(10.f);
        }
    }

    @Override
    public void endContact(Contact contact) {

    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
