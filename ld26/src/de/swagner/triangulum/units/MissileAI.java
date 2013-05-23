package de.swagner.triangulum.units;

import com.badlogic.gdx.math.Vector2;

import de.swagner.triangulum.Targeting;

public class MissileAI {
	private float MAX_LIFETIME = 3; // 3 seconds to auto-destruct

	private Ship target;

	private Missile missile;
	
	Vector2 relativeVel = new Vector2();
	Vector2 toTarget = new Vector2();

	public MissileAI(Missile missile) {
		this.missile = missile;
		retarget();
	}

	public void retarget() {
		target = Targeting.getTypeInRange(missile, 0, 500);
	}

	public void selfDestruct() {
		// EXPLODE!
		missile.alive = false;
	}

	public Vector2 predict() {
		relativeVel.set(missile.velocity).sub(target.velocity);
		toTarget.set(target.position).sub(missile.position);
		if (missile.velocity.dot(toTarget) != 0) {
			float time_to_target = toTarget.dot(toTarget) / relativeVel.dot(toTarget);
			return new Vector2(target.position).sub(relativeVel.scl(Math.max(0, time_to_target)));
		} else {
			return target.position;
		}
	}

	public void update() {
		if (target == null || missile.aliveTime > MAX_LIFETIME) {
			selfDestruct();
		} else if (!target.alive) {
			retarget();
		} else {
			missile.approach(target.position);
		}
	}
}
