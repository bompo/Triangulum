package de.swagner.triangulum.units;

import com.badlogic.gdx.math.Vector2;

public class Base extends Ship {
	
	public Base(int id, Vector2 position) {
		super(id, position, new Vector2());
		hitPoints = 200;
		type = TYPE.BASE;
	}
	
	@Override
	public void update(float delta) {
	}
	
	@Override
	public void hit() {
		hitPoints = hitPoints - 1;
		if(hitPoints <= 0) {
			alive = false;
		}
	}
	
}
