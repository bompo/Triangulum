package de.swagner.triangulum.units;

import com.badlogic.gdx.math.Vector2;

import de.swagner.triangulum.Targeting;

public class DefaultAI {
	
	public enum STATE {
		IDLE, MOVING, RUNNING, SHOOTING;
	}
	
	public STATE state = STATE.IDLE;
	
	// shot range
	protected float shotRange = 7;

	// try to stay this far away when you're out of ammo
	protected float runDistance = 14f;

	public Ship target;
	
	//recycle vars
	protected Vector2 toTarget = new Vector2();

	protected Ship ship;

	public DefaultAI(Ship ship) {
		this.ship = ship;
		retarget();
	}

	public void retarget() {
		if(target != null && target.alive == false) {
			target = null;
		}
		
		if(ship.type == Ship.TYPE.FIGHTER) {
    		if (target == null || target.type == Ship.TYPE.BASE) {
    			target = Targeting.getTypeInRange(ship, 0, 4);
    		}
    		if (target == null || target.type == Ship.TYPE.BASE) {
    			target = Targeting.getTypeInRange(ship, 1, 4);
    		}
    		if (target == null) {
    			target = Targeting.getNearestOfType(ship, 4);
    		}
		}
		
		if(ship.type == Ship.TYPE.TOWER) {
    		if (target == null) {
    			target = Targeting.getNearestOfType(ship, 0);
    		}
		}
		
		//if nearby base then ignore anything and always attack the base
		if(ship.id == 1 && ship.position.x > 120) {
			target = Targeting.getNearestOfType(ship, 4);
		} else if(ship.id == 2 && ship.position.x < 30) {
			target = Targeting.getNearestOfType(ship, 4);
		}
		
		state = STATE.MOVING;
	}

	public void target(Ship ship) {
		target = ship;
		state = STATE.MOVING;
	}
	
	public void setState(STATE state) {
		this.state = state;
	}
	
	public void logic() {
		retarget();

		if (target != null) {			
			toTarget.set(target.position.x - ship.position.x, target.position.y - ship.position.y);
			float distSquared = toTarget.dot(toTarget);

			if (state.equals(STATE.RUNNING)) {
				// run away until you have full ammo and are far enough away
				boolean too_close = distSquared < Math.pow(runDistance, 2);
				// if you're too close to the target then turn away
				if (too_close) {
					ship.avoid(target.position);
				} else {
					ship.move();
				}

				if (!ship.isEmpty() && !too_close) {
					state = STATE.MOVING;
				}
			}
			if (state.equals(STATE.MOVING)) {
				// go towards the target and attack!
				ship.approach(target.position);

				// is target enemy in range?
				if (distSquared <= shotRange * shotRange && toTarget.dot(ship.facing) > 0 && Math.pow(toTarget.dot(ship.facing), 2) > 0.97 * distSquared) {
					if (ship.isReloaded()) {
						state = STATE.SHOOTING;
						ship.shoot();
					} else {
						state = STATE.RUNNING;
					}
				} 				

				// if out of shots then run away
				if (ship.isEmpty()) {
					state = STATE.RUNNING;
				}
			}
		}
	}
}
