package com.mygame.interfaces;

/**
 * Consists of looted flag getter and setter and gold getter. If attackable object will implement this
 * interface, it will drop gold on death
 */
public interface Lootable {
    void setLooted(boolean looted);
    boolean isLooted();
    int getGold();
}
