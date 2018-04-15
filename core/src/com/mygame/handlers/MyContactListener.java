package com.mygame.handlers;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.mygame.entities.*;
import com.mygame.game.MyGame;
import com.mygame.interfaces.Attackable;

public class MyContactListener implements ContactListener {
    @Override
    public void beginContact(Contact contact) {
        Fixture fa = contact.getFixtureA();
        Fixture fb = contact.getFixtureB();
        WorldManifold wm = contact.getWorldManifold();

        if (fa.getUserData() == null || fb.getUserData() == null) return;

        playerEnemyCollision(fa, fb, wm);

        swordEnemyCollision(fa, fb);

        playerLootCollision(fa, fb);

        arrowEnemyCollision(fa, fb);
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

    private void playerEnemyCollision(Fixture fa, Fixture fb, WorldManifold wm) {
        if ((fa.getFilterData().categoryBits == Constants.BIT_PLAYER && fb.getFilterData().categoryBits == Constants.BIT_ENEMY) ||
                (fb.getFilterData().categoryBits == Constants.BIT_PLAYER && fa.getFilterData().categoryBits == Constants.BIT_ENEMY)) {
            Sprite player;
            Sprite enemy;

            if (fa.getUserData() instanceof Player) {
                player = (Sprite) fa.getUserData();
                enemy = (Sprite) fb.getUserData();
            } else {
                player = (Sprite) fb.getUserData();
                enemy = (Sprite) fa.getUserData();
            }

            if (((Attackable) player).getAttackableState() == Attackable.AttackableState.ALIVE &&
                    ((Attackable) enemy).getAttackableState() == Attackable.AttackableState.ALIVE) {
                float impulsePower = 200.f;
                Vector2 n = wm.getNormal();

                player.getBody().applyLinearImpulse(new Vector2(n.x * impulsePower,
                        n.y * impulsePower), player.getBody().getPosition(), true);
                enemy.getBody().applyLinearImpulse(new Vector2(-n.x * impulsePower,
                        -n.y * impulsePower), enemy.getBody().getPosition(), true);

                ((Attackable) player).hit(10);
                ((Attackable) enemy).hit(20);

                MyGame.assets.getSound("hurt01").stop();
                MyGame.assets.getSound("hurt01").play(0.2f);
            }
        }
    }

    private void swordEnemyCollision(Fixture fa, Fixture fb) {
        if ((fa.getFilterData().categoryBits == Constants.BIT_WEAPON && fb.getFilterData().categoryBits == Constants.BIT_ENEMY) ||
                (fb.getFilterData().categoryBits == Constants.BIT_WEAPON && fa.getFilterData().categoryBits == Constants.BIT_ENEMY)) {
            Sprite player;
            Sprite enemy;

            if (fa.getUserData() instanceof Player) {
                player = (Sprite) fa.getUserData();
                enemy = (Sprite) fb.getUserData();
            } else {
                player = (Sprite) fb.getUserData();
                enemy = (Sprite) fa.getUserData();
            }

            if (((Attackable) enemy).getAttackableState() == Attackable.AttackableState.ALIVE &&
                    ((Attackable) player).getAttackableState() == Attackable.AttackableState.ALIVE) {
                float impulsePower = 800.f;
                Vector2 n = new Vector2(player.getPosition().x - enemy.getPosition().x, player.getPosition().y - enemy.getPosition().y);

                enemy.getBody().applyLinearImpulse(new Vector2(-n.x * impulsePower,
                        -n.y * impulsePower), enemy.getBody().getPosition(), true);

                ((Attackable) enemy).hit(30);

                MyGame.assets.getSound("sword01").play();
            }
        }
    }

    private void playerLootCollision(Fixture fa, Fixture fb) {
        if ((fa.getFilterData().categoryBits == Constants.BIT_PLAYER && fb.getFilterData().categoryBits == Constants.BIT_LOOT) ||
                (fb.getFilterData().categoryBits == Constants.BIT_PLAYER && fa.getFilterData().categoryBits == Constants.BIT_LOOT)) {
            Player player;
            Loot loot;

            if (fa.getUserData() instanceof Player) {
                player = (Player) fa.getUserData();
                loot = (Loot) fb.getUserData();
            } else {
                player = (Player) fb.getUserData();
                loot = (Loot) fa.getUserData();
            }

            player.lootGold(loot.getGold());

            MyGame.assets.getSound("gold").play();
        }
    }

    private void arrowEnemyCollision(Fixture fa, Fixture fb) {
        if ((fa.getFilterData().categoryBits == Constants.BIT_ARROW && fb.getFilterData().categoryBits == Constants.BIT_ENEMY) ||
                (fb.getFilterData().categoryBits == Constants.BIT_ARROW && fa.getFilterData().categoryBits == Constants.BIT_ENEMY)) {
            Arrow arrow;
            Sprite enemy;

            if (fa.getUserData() instanceof Arrow) {
                arrow = (Arrow) fa.getUserData();
                enemy = (Sprite) fb.getUserData();
            } else {
                arrow = (Arrow) fb.getUserData();
                enemy = (Sprite) fa.getUserData();
            }

            if (((Attackable) enemy).getAttackableState() == Attackable.AttackableState.ALIVE &&
                    arrow.isActive()) {
                float impulsePower = 800.f;

                Vector2 n = new Vector2(arrow.getPosition().x - enemy.getPosition().x, arrow.getPosition().y - enemy.getPosition().y);
                enemy.getBody().applyLinearImpulse(new Vector2(-n.x * impulsePower,
                        -n.y * impulsePower), enemy.getBody().getPosition(), true);

                ((Attackable) enemy).hit(20);
                arrow.getBody().setLinearVelocity(0, 0);
                arrow.getFixture().setSensor(true);
                arrow.setTarget(enemy);

                MyGame.assets.getSound("arrowImpact01").play();
            }
        }
    }
}
