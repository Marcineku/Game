package com.mygame.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.mygame.handlers.Constants;

/**
 * Consists of current cursor position
 */
public class Cursor {
    private Vector2 position;

    public Cursor() {
        position = new Vector2(0, 0);
    }

    /**
     * Updates the cursor's position in relation to camera
     * @param cam the camera in relation to which cursor's position will be calculated
     */
    public void update(OrthographicCamera cam) {
        Vector3 mouseInWorld3D = new Vector3();
        mouseInWorld3D.x = Gdx.input.getX();
        mouseInWorld3D.y = Gdx.input.getY();
        mouseInWorld3D.z = 0;
        cam.unproject(mouseInWorld3D);
        position.x = mouseInWorld3D.x;
        position.y = mouseInWorld3D.y;
    }

    public Vector2 getPosition() {
        return position;
    }

    public Vector2 getBox2DPosition() {
        return new Vector2(position.x / Constants.PPM, position.y / Constants.PPM);
    }
}
