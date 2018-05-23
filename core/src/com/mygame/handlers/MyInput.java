package com.mygame.handlers;

/**
 * Used to store which keys are pressed in current frame and which were pressed in previous
 */
public class MyInput {
    public static boolean[] keys;
    public static boolean[] pkeys;

    public static final int NUM_KEYS = 12;
    public static final int UP       = 0;
    public static final int DOWN     = 1;
    public static final int LEFT     = 2;
    public static final int RIGHT    = 3;
    public static final int SLIME    = 4;
    public static final int STRIKE   = 5;
    public static final int RESET    = 6;
    public static final int JUMP     = 7;
    public static final int PICK     = 8;
    public static final int EQ       = 9;
    public static final int DRAW     = 10;
    public static final int STRIKE2  = 11;

    static {
        keys = new boolean[NUM_KEYS];
        pkeys = new boolean[NUM_KEYS];
    }

    public static void update() {
        for(int i = 0; i < NUM_KEYS; ++i) {
            pkeys[i] = keys[i];
        }
    }

    public static void setKey(int i, boolean b) {
        keys[i] = b;
    }

    public static boolean isDown(int i) {
        return keys[i];
    }

    public static boolean isPressed(int i) {
        return keys[i] && !pkeys[i];
    }

    public static boolean isReleased(int i) { return !keys[i] && pkeys[i]; }
}
