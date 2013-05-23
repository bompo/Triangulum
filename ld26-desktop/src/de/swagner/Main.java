package de.swagner;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import de.swagner.triangulum.ld26;

public class Main {
	public static void main(String[] args) {
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "Triangulum - Ludum Dare 26 Entry from bompo";
		cfg.useGL20 = true;
		cfg.samples = 4;
		cfg.backgroundFPS = 60;
		cfg.vSyncEnabled = true;
		cfg.resizable = true;
		cfg.width = 800;
		cfg.height = 480;
		
		new LwjglApplication(new ld26(), cfg);
	}
}
