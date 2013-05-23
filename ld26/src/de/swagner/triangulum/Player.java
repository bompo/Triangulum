package de.swagner.triangulum;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import de.swagner.triangulum.controls.ControlMappings;
import de.swagner.triangulum.controls.PlayerOneControlMappings;

public class Player {

	public enum SIDE {
		LEFT, RIGHT;
	}
	
	public ControlMappings input;

	public int hitPoints = 100;
	public float energy = 50;
	
	public Array<Vector2> inputList;
	
	public SIDE side = SIDE.LEFT;
	
	public Player(SIDE side) {		
		this.side = side;
		inputList = new Array<Vector2>();
		input = new PlayerOneControlMappings();
	}

	public void update() {
	}

}
