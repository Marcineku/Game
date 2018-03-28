package com.mygame.handlers;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;

public class MyInputProcessor extends InputAdapter {
    @Override
    public boolean keyDown(int keycode) {
        if(keycode == Input.Keys.W) {
            MyInput.setKey(MyInput.UP, true);
        }
        if(keycode == Input.Keys.S) {
            MyInput.setKey(MyInput.DOWN, true);
        }
        if(keycode == Input.Keys.A) {
            MyInput.setKey(MyInput.LEFT, true);
        }
        if(keycode == Input.Keys.D) {
            MyInput.setKey(MyInput.RIGHT, true);
        }

        return super.keyDown(keycode);
    }

    @Override
    public boolean keyUp(int keycode) {
        if(keycode == Input.Keys.W) {
            MyInput.setKey(MyInput.UP, false);
        }
        if(keycode == Input.Keys.S) {
            MyInput.setKey(MyInput.DOWN, false);
        }
        if(keycode == Input.Keys.A) {
            MyInput.setKey(MyInput.LEFT, false);
        }
        if(keycode == Input.Keys.D) {
            MyInput.setKey(MyInput.RIGHT, false);
        }

        return super.keyUp(keycode);
    }
}
