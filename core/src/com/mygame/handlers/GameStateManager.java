package com.mygame.handlers;

import com.mygame.game.MyGame;
import com.mygame.states.GameState;
import com.mygame.states.Play;

import java.util.Stack;

public class GameStateManager {
    private MyGame game;

    private Stack<GameState> gameStates;

    public static final int PLAY = 1;

    public GameStateManager(MyGame game) {
        this.game = game;
        gameStates = new Stack<GameState>();
        pushState(PLAY);
    }

    public MyGame getGame() {
        return game;
    }

    public void update(float dt) {
        gameStates.peek().update(dt);
    }

    public void render() {
        gameStates.peek().render();
    }

    private GameState getState(int state) {
        if(state == PLAY) return new Play(this);
        return null;
    }

    public void setState(int state) {
        popState();
        pushState(state);
    }

    public void pushState(int state) {
        gameStates.push(getState(state));
    }

    public void popState() {
        GameState g = gameStates.pop();
        g.dispose();
    }

    public boolean isEmpty() {
        return gameStates.isEmpty();
    }
}
