package de.swagner.triangulum.units;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import de.swagner.triangulum.GameSession;

public class Ship {

	public int id;
	
	public enum TYPE {
		FIGHTER, TOWER, BASE;
	}
	
	public enum STATE {
		IDLE, THRUST, APPROACH, AVOID;
	}
	
	public STATE state = STATE.IDLE;
	
	public TYPE type;
	
	protected float shotCooldownTime = 6f;
	protected float shotCapacity = 1f;
	protected float shotReloadRate = 1f;
	protected float shotAimingTime = 4f;

	protected float shots = shotCapacity;
	protected float cooldown = 0;
	
	protected float turnSpeed = 100.0f;
	protected float accel = 5.0f;
	protected float hitPoints = 2;
	
	protected Vector2 targetPos = new Vector2();

	private float delta = 0.0f;
	
	public float directionAngle = 0;

	public float aliveTime = 0.0f;

	public Vector2 position = new Vector2();
	public Vector2 velocity = new Vector2();
	public Vector2 facing = new Vector2();
	
	public DefaultAI ai = new DefaultAI(this);
	
	public boolean alive = true;

	public Ship(int id, Vector2 position, Vector2 facing) {
		this.id = id;

		this.position.set(position);
		this.facing.set(facing);
	}
	
	public void update(float delta) {
		
		this.delta = delta;
		
		aliveTime = aliveTime + delta;
		
		//death check
		if(!alive){
			return;
		}
		
		if(state == STATE.APPROACH) {
			goTowards(targetPos);
		} else if(state == STATE.AVOID) {
			goAway(targetPos);
		} else if(state == STATE.THRUST) {
			thrust();
		}
		
		cooldown = Math.max(0, cooldown - delta);
		shots = Math.min(shots + (shotReloadRate * delta), shotCapacity);
		
		velocity.scl( (float) Math.pow(0.97f, delta * 30.f));
		position.add(velocity.x * delta, velocity.y * delta);	
	}
	
	public void logic() {
		ai.logic();
	}
	
	public void turn(float direction) {
		delta = Math.min(0.06f, delta);		
		facing.rotate(direction * turnSpeed * delta).nor();
	}

	private void thrust() {
		delta = Math.min(0.06f, delta);		
		velocity.add(facing.x * accel * delta, facing.y * accel * delta);
	}

	private void goTowardsOrAway(Vector2 targetPos, boolean isAway) {
		Vector2 targetDirection = targetPos.cpy().sub(position);
		if (isAway) {
			targetDirection.scl(-1);
		}

		if (facing.crs(targetDirection) > 0) {
			turn(1);
		} else {
			turn(-1);
		}

		if (facing.dot(targetDirection) > 0) {
			thrust();
		}
	}
	
	// automatically thrusts and turns according to the target
	private void goTowards(Vector2 targetPos) {
		goTowardsOrAway(targetPos, false);
	}

	private void goAway(Vector2 targetPos) {
		goTowardsOrAway(targetPos, true);
	}
	
	public void move() {
		state = STATE.THRUST;
	}
	
	public void approach(Vector2 targetPos) {
		state = STATE.APPROACH;
		this.targetPos.set(targetPos);
	}

	public void avoid(Vector2 targetPos) {
		state = STATE.AVOID;
		this.targetPos.set(targetPos);
	}
	
	public boolean isEmpty() {
		return shots < 1;
	}
	
	public boolean isReloaded() {
		return shots == shotCapacity;
	}

	public boolean isCooledDown() {
		return cooldown == 0;
	}
	
	public boolean isReadyToShoot() {
		return isReloaded() && isCooledDown() && !isEmpty();
	}

	public void shoot() {
		if (cooldown == 0 && shots >= 1) {
			shots -= 1;
			cooldown = shotCooldownTime;
			
			//bullets always hit the enemy
			ai.target.hit();
		}
	}
	
	public void hit() {
		hitPoints = hitPoints - 1;
		GameSession.getInstance().grid.setValue(MathUtils.floor(position.x),MathUtils.floor(position.y), 1.5f + id);
		GameSession.getInstance().grid.setValue(MathUtils.floor(position.x),MathUtils.floor(position.y)+1, 0.5f + id);
		GameSession.getInstance().grid.setValue(MathUtils.floor(position.x),MathUtils.floor(position.y)-1, 0.5f + id);
		GameSession.getInstance().grid.setValue(MathUtils.floor(position.x)+1,MathUtils.floor(position.y), 0.5f + id);
		GameSession.getInstance().grid.setValue(MathUtils.floor(position.x)-1,MathUtils.floor(position.y), 0.5f + id);
		if(hitPoints <= 0) {
			alive = false;
		}
	}

}
