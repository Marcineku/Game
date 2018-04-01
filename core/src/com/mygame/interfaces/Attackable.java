package com.mygame.interfaces;

import com.badlogic.gdx.math.Vector2;

public interface Attackable {
    void hit(int damage);
    int getHp();
    Vector2 getHpBarPosition();
    AttackableState getAttackableState();

    enum AttackableState {
        DEAD, ALIVE
    }
}
