package com.mygame.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class Event {
    private SpriteBatch sb;
    private BitmapFont font;
    private String text;
    private float fadingTime;
    private float time;
    private Vector2 position;
    private boolean active;
    private float speed;
    private float fadingOffset;

    public Event(SpriteBatch sb, BitmapFont font, String text, float fadingTime, Vector2 position, Color color, float speed, float fadingOffset) {
        this.sb = sb;
        this.font = new BitmapFont(font.getData(), font.getRegion(), font.usesIntegerPositions());
        this.font.setColor(color);
        this.text = text;
        this.fadingTime = fadingTime;
        this.speed = speed;
        this.fadingOffset = fadingOffset;
        time = 0;
        active = true;
        this.position = new Vector2(position);
    }

    public void update(float dt) {
        time += dt;
        font.setColor(font.getColor().r, font.getColor().g, font.getColor().b, 1/time - fadingOffset);
        position.add(0, time * speed);

        if(time >= fadingTime) {
            active = false;
        }
    }

    public void render() {
        if(active) {
            sb.begin();
            font.draw(sb, text, position.x, position.y);
            sb.end();
        }
    }

    public boolean isActive() {
        return active;
    }
}
