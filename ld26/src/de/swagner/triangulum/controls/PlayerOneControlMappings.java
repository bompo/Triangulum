package de.swagner.triangulum.controls;

import com.badlogic.gdx.Input;

public class PlayerOneControlMappings extends ControlMappings {
	
	public PlayerOneControlMappings() {
		this.up = Input.Keys.W;
		this.down = Input.Keys.S;
		this.left = Input.Keys.A;
		this.right = Input.Keys.D;
	}

}
