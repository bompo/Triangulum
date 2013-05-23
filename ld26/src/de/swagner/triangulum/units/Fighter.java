package de.swagner.triangulum.units;

import com.badlogic.gdx.math.Vector2;

public class Fighter extends Ship {

	public Fighter(int id, Vector2 position, Vector2 facing) {
		super(id, position, facing);
		this.type = TYPE.FIGHTER;
		
		this.ai = new FighterAI(this);	
	}
	
	public void update(float delta) {
		super.update(delta);
		if(!alive) return;
	}

}
