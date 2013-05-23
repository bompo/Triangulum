package de.swagner.triangulum;

import com.badlogic.gdx.Game;

public class ld26 extends Game {
	@Override 
	public void create () {
		setScreen(new StartScreen(this));
	}
}
