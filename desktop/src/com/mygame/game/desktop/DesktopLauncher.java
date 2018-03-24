package com.mygame.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.mygame.game.MyGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Game";
		config.useGL30 = false;
		config.width = 1280;
		config.height = 900;
		config.resizable = false;

		new LwjglApplication(new MyGame(), config);
	}
}
