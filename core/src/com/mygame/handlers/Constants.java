package com.mygame.handlers;

/**
 * Consists of game constants such as pixels per meter
 * (for unit conversion between physics engine
 * units which are meter/kilogram/second and screen units, which are pixels),
 * collision masks or item names
 */
public class Constants {
    public static final float PPM = 50.f/3.f;

    public static final short BIT_PLAYER  = 2;
    public static final short BIT_ENEMY   = 4;
    public static final short BIT_WEAPON  = 8;
    public static final short BIT_LOOT    = 16;
    public static final short BIT_ARROW   = 32;
    public static final short BIT_SHADOWS = 64;
    public static final short BIT_CURSOR  = 128;
    public static final short BIT_ITEM    = 256;

    public static final String ITEM_BOW = "bow";
}
