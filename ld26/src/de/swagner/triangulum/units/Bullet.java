package de.swagner.triangulum.units;

import com.badlogic.gdx.math.Vector2;

public class Bullet extends Ship {

	public float damage = 0;
	public float bulletSpeed = 0f;

	public Bullet(int id, Vector2 position, Vector2 facing) {
		super(id, position, facing);
	}
	
	

}
