package com.mygame.interfaces;

import com.badlogic.gdx.math.Vector2;

/**
 * Consists of health and exp getters and also health modifiers like hit procedure,
 * that is for subtracting health from attackable object. It's having getter and setter for hit flag
 * which is useful for texts events after being hit. Moreover it stores position of hp bar and state
 * of an attackable object (which can be either dead or alive)
 */
public interface Attackable {
    void hit(int damage);
    int getHp();
    int getExp();
    int getDamage();
    boolean isHit();
    void setHit(boolean hit);
    Vector2 getHpBarPosition();
    AttackableState getAttackableState();

    enum AttackableState {
        DEAD, ALIVE
    }
}
