package com.mygame.handlers;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.mygame.game.MyGame;

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

        if(keycode == Input.Buttons.LEFT) {
            MyInput.setKey(MyInput.STRIKE, true);
        }

        if(keycode == Input.Keys.F) {
            MyInput.setKey(MyInput.SLIME, true);
        }

        if(keycode == Input.Keys.R) {
            MyInput.setKey(MyInput.RESET, true);
        }

        if(keycode == Input.Keys.SPACE) {
            MyInput.setKey(MyInput.JUMP, true);
        }

        if(keycode == Input.Keys.E) {
            MyInput.setKey(MyInput.PICK, true);
        }

        if(keycode == Input.Keys.I) {
            if(!MyInput.isDown(MyInput.EQ)) {
                MyInput.setKey(MyInput.EQ, true);
            }
            else {
                MyInput.setKey(MyInput.EQ, false);
            }
        }

        if(keycode == Input.Keys.Q) {
            MyInput.setKey(MyInput.DRAW, true);
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

        if(keycode == Input.Buttons.LEFT) {
            MyInput.setKey(MyInput.STRIKE, false);
        }

        if(keycode == Input.Keys.F) {
            MyInput.setKey(MyInput.SLIME, false);
        }

        if(keycode == Input.Keys.R) {
            MyInput.setKey(MyInput.RESET, false);
        }

        if(keycode == Input.Keys.SPACE) {
            MyInput.setKey(MyInput.JUMP, false);
        }

        if(keycode == Input.Keys.E) {
            MyInput.setKey(MyInput.PICK, false);
        }

        if(keycode == Input.Keys.Q) {
            MyInput.setKey(MyInput.DRAW, false);
        }

        return super.keyUp(keycode);
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if(button == Input.Buttons.LEFT) {
            MyInput.setKey(MyInput.STRIKE, true);
        }

        return super.touchDown(screenX, screenY, pointer, button);
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if(button == Input.Buttons.LEFT) {
            MyInput.setKey(MyInput.STRIKE, false);
        }

        return super.touchUp(screenX, screenY, pointer, button);
    }
}
