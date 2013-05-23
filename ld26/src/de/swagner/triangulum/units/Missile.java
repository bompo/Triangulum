package de.swagner.triangulum.units;

import com.badlogic.gdx.math.Vector2;

public class Missile extends Bullet {

	private MissileAI ai = new MissileAI(this);
	
	public Missile(int id, Vector2 position, Vector2 facing) {
		super(id, position, facing);
		turnSpeed = 100f;
		accel = 12.0f;	
		bulletSpeed = 0;
		this.velocity = new Vector2().set(facing).scl(bulletSpeed);
		damage = 50;
	}
	
	@Override
	public void update(float delta) {
		ai.update();
		
		super.update(delta);
	}
	
}
