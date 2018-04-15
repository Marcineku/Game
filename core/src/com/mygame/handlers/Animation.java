package com.mygame.handlers;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Animation {
    private TextureRegion[] frames;
    private float           time;
    private float           delay;
    private int             currentFrame;
    private int             timesPlayed;
    private boolean         loop;

    public Animation(TextureRegion[] frames, float delay) {
        this.frames  = frames;
        this.delay   = delay;
        time         = 0;
        currentFrame = 0;
        timesPlayed  = 0;
        loop = true;
    }

    public void update(float dt) {
        if(delay <= 0) return;

        time += dt;
        while(time >= delay) step();
    }

    private void step() {
        time -= delay;
        ++currentFrame;
        if(currentFrame == frames.length) {
            if(loop) {
                currentFrame = 0;
                ++timesPlayed;
            }
            else {
                currentFrame = frames.length - 1;
            }
        }
    }

    public TextureRegion getFrame() {
        return frames[currentFrame];
    }

    public int getTimesPlayed() {
        return timesPlayed;
    }

    public float getTime() {
        return time;
    }

    public int getCurrentFrame() {
        return currentFrame;
    }

    public void Synchronize(Animation anim) {
        this.time = anim.getTime();
        this.currentFrame = anim.getCurrentFrame();
    }

    public void setLoop(boolean loop) {
        this.loop = loop;
    }

    public boolean isLoop() {
        return loop;
    }

    public void flip(boolean x, boolean y) {
        for(TextureRegion i : frames) {
            i.flip(x, y);
        }
    }
}
