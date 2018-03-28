package com.mygame.handlers;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.mygame.entities.Player;
import com.mygame.entities.Slime;

public class MyContactListener implements ContactListener{
    @Override
    public void beginContact(Contact contact) {
        Fixture fa = contact.getFixtureA();
        Fixture fb = contact.getFixtureB();

        Player player;
        Slime slime;

        //player and slime collision
        if((fa.getUserData().toString() == "player" && fb.getUserData().toString() == "slime") || (fb.getUserData().toString() == "player" && fa.getUserData().toString() == "slime")) {
            if(fa.getUserData().toString() == "player") {
                player = (Player) fa.getUserData();
                slime = (Slime) fb.getUserData();
            }
            else {
                player = (Player) fb.getUserData();
                slime = (Slime) fa.getUserData();
            }

            float impulsePower = 500.f;
            WorldManifold wm = contact.getWorldManifold();
            Vector2 n = wm.getNormal();
            player.getBody().applyLinearImpulse(new Vector2(n.x * impulsePower, n.y * impulsePower), player.getBody().getPosition(), true);
            slime.getBody().applyLinearImpulse(new Vector2(-n.x * impulsePower, -n.y * impulsePower), slime.getBody().getPosition(), true);

            player.hit(10);
            slime.hit(20);
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
