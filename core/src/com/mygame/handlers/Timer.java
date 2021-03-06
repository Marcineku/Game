package com.mygame.handlers;

/**
 * Simple time counter
 */
public class Timer {
    private float time;
    private boolean running;

    public Timer() {
        time = 0;
        running = false;
    }

    public float getTime() {
        return time;
    }

    public void update(float dt) {
        if(running) time += dt;
    }

    public void reset() {
        time = 0;
        running = false;
    }

    public void start() {
        running = true;
    }

    public void stop() {
        running = false;
    }

    public boolean isRunning() {
        return running;
    }
}
