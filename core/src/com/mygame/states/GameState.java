package com.mygame.states;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygame.handlers.GameStateManager;
import com.mygame.game.MyGame;

/**
 * Is for passing application-level objects like game state manager,
 * sprite batch or cams to objects that will extend it. Examples of game states are
 * different main menu screens or different game stages, only game state that is going
 * to be updated and rendered is the game state on top of the game state stack in game state manager
 */
public abstract class GameState {
    protected GameStateManager gsm;
    protected MyGame game;

    protected SpriteBatch sb;
    protected OrthographicCamera cam;
    protected OrthographicCamera hudCam;

    protected GameState(GameStateManager gsm) {
        this.gsm = gsm;
        game = gsm.getGame();
        sb = game.getSpriteBatch();
        cam = game.getCam();
        hudCam = game.getHudCam();
    }

    public abstract void handleInput();
    public abstract void update(float dt);
    public abstract void render();
    public abstract void dispose();
}
