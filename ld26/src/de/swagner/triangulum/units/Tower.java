package de.swagner.triangulum.units;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import de.swagner.triangulum.GameSession;

public class Tower extends Ship {

	public Tower(int id, Vector2 position, Vector2 facing) {
		super(id, position, facing);
		
		shotCooldownTime = 4f;
		shotCapacity = 5f;
		shotReloadRate = 1f;
		
		turnSpeed = 0.0f;
		accel = 0.0f;
		hitPoints = 2;
		
		this.type = TYPE.TOWER;
		
		this.ai = new TowerAI(this);
	}
	
	@Override
	public void shoot() {
		if (cooldown == 0 && shots >= 1) {
			shots -= 1;
			cooldown = shotCooldownTime;

			long soundID = GameSession.getInstance().missileSound.play();
			GameSession.getInstance().missileSound.setPan(soundID, 1 - ((position.x/80.f)*2.f), 1);
			GameSession.getInstance().missileSound.setPitch(soundID, 0.5f + position.y/48.f);
			
			GameSession.getInstance().bullets.add(new Missile(id, position.cpy(), new Vector2(ai.target.position.x - position.x, ai.target.position.y - position.y).nor()));
		}
	}
	
	@Override
	public void update(float delta) {
				
		//death check
		if(!alive){
			return;
		}
		
		cooldown = Math.max(0, cooldown - delta);
		shots = Math.min(shots + (shotReloadRate * delta), shotCapacity);
	}
	
	@Override
	public void logic() {
		ai.logic();
	}
		
	@Override
	public void hit() {
		hitPoints = hitPoints - 1;
		GameSession.getInstance().grid.setValue(MathUtils.floor(position.x),MathUtils.floor(position.y), 1 + id);
		GameSession.getInstance().grid.setValue(MathUtils.floor(position.x),MathUtils.floor(position.y)+1, 1 + id);
		GameSession.getInstance().grid.setValue(MathUtils.floor(position.x),MathUtils.floor(position.y)-1, 1 + id);
		GameSession.getInstance().grid.setValue(MathUtils.floor(position.x)+1,MathUtils.floor(position.y), 1 + id);
		GameSession.getInstance().grid.setValue(MathUtils.floor(position.x)-1,MathUtils.floor(position.y), 1 + id);
		if(hitPoints <= 0) {
			alive = false;
		}
	}

}
