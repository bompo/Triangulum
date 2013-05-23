package de.swagner.triangulum.units;


public class TowerAI extends DefaultAI {

	float shotRange = 25;
	
	public TowerAI(Ship ship) {
		super(ship);
		retarget();
	}

	@Override
	public void logic() {
		retarget();
	
		if (target != null) {		
			toTarget.set(target.position.x - ship.position.x, target.position.y - ship.position.y);
			float distSquared = toTarget.dot(toTarget);
	
			// is target enemy in range?
			if (distSquared <= shotRange * shotRange) {
				if(ship.isReadyToShoot()) {
					ship.shoot();
				}
			} 	
		}
		
	}
	
	
}
