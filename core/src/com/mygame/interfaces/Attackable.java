package com.mygame.interfaces;

import com.badlogic.gdx.math.Vector2;

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
