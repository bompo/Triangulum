package de.swagner;

import com.badlogic.gdx.backends.lwjgl.LwjglApplet;

import de.swagner.triangulum.ld26;

public class AppletMain extends LwjglApplet {
	private static final long serialVersionUID = 1L;

	public AppletMain() {
		super(new ld26(), true);
	}
}
