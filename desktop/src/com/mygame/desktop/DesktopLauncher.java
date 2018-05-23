package com.mygame.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.mygame.game.MyGame;

/**
 * Contains main procedure only.
 * Initializes all necessary LWJGL variables
 * and creates instantion of class that consists of main game loop
 */
public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title         = MyGame.TITLE;
		config.width         = MyGame.V_WIDTH  * MyGame.SCALE;
		config.height        = MyGame.V_HEIGHT * MyGame.SCALE;
		config.foregroundFPS = MyGame.FPS;
		config.backgroundFPS = MyGame.FPS;
		config.useGL30       = false;
		config.resizable     = true;

		new LwjglApplication(new MyGame(), config);
	}
}
