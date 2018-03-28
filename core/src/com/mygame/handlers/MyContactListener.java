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

        if(fa.getUserData() == null  || fb.getUserData() == null) return;

        //player and slime collision
        if((fa.getFilterData().categoryBits == Constants.BIT_PLAYER && fb.getFilterData().categoryBits == Constants.BIT_ENEMY) ||
                (fb.getFilterData().categoryBits == Constants.BIT_PLAYER && fa.getFilterData().categoryBits == Constants.BIT_ENEMY)) {
            Player player;
            Slime slime;

            if(fa.getUserData().toString() == "player") {
                player = (Player) fa.getUserData();
                slime = (Slime) fb.getUserData();
            }
            else {
                player = (Player) fb.getUserData();
                slime = (Slime) fa.getUserData();
            }


            if(slime.getState() == Slime.SlimeStates.ALIVE && player.getState() != Player.PlayerStates.DEAD) {
                float impulsePower = 500.f;
                WorldManifold wm = contact.getWorldManifold();
                Vector2 n = wm.getNormal();
                player.getBody().applyLinearImpulse(new Vector2(n.x * impulsePower,
                        n.y * impulsePower), player.getBody().getPosition(), true);
                slime.getBody().applyLinearImpulse(new Vector2(-n.x * impulsePower,
                        -n.y * impulsePower), slime.getBody().getPosition(), true);

                player.hit(10);
                slime.hit(20);
            }
        }

        //player strikes a slime with a weapon
        if ((fa.getFilterData().categoryBits == Constants.BIT_WEAPON && fb.getFilterData().categoryBits == Constants.BIT_ENEMY) ||
                (fb.getFilterData().categoryBits == Constants.BIT_WEAPON && fa.getFilterData().categoryBits == Constants.BIT_ENEMY)) {
            Player player;
            Slime slime;

            if(fa.getUserData().toString() == "player") {
                player = (Player) fa.getUserData();
                slime = (Slime) fb.getUserData();
            }
            else {
                player = (Player) fb.getUserData();
                slime = (Slime) fa.getUserData();
            }

            if(slime.getState() == Slime.SlimeStates.ALIVE && player.getState() != Player.PlayerStates.DEAD) {
                float impulsePower = 300.f;
                WorldManifold wm = contact.getWorldManifold();
                Vector2[] points = wm.getPoints();
                Vector2 n = new Vector2(player.getPosition().x - points[0].x, player.getPosition().y - points[0].y);
                slime.getBody().applyLinearImpulse(new Vector2(-n.x * impulsePower,
                        -n.y * impulsePower), slime.getBody().getPosition(), true);

                slime.hit(10);
            }
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
