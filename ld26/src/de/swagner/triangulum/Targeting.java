package de.swagner.triangulum;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;

import de.swagner.triangulum.units.Ship;

public class Targeting {

	/*
	 * returns the closest target of the given type 0 = Soldier
	 */
	public static Ship getNearestOfType(Ship source, int type) {
		if (type == 0)
			return getNearestOfType(source, GameSession.getInstance().ships);
		else if (type == 1)
			return getNearestOfType(source, GameSession.getInstance().towers);
		else if (type == 4)
			return getNearestOfType(source, GameSession.getInstance().bases);
		else
			return null;
	}
	
	private static Ship getNearestOfType(Ship source, Array<Ship> ships) {
		// find the closest one!
		Ship closed = null;
		float closestDistanze = Float.MAX_VALUE;

		for (int i = 0; i < ships.size; i++) {
			Ship ship = ships.get(i);
			float currentDistance = source.position.dst2(ship.position);

			if (ship.alive && source.id != ship.id && (currentDistance < closestDistanze)) {
				closed = ship;
				closestDistanze = currentDistance;
			}
		}

		return closed;
	}

	/*
	 * return a random enemy of the desired type that's in range
	 * 0 = Soldier
	 */
	public static Ship getTypeInRange(Ship source, int type, float range) {
		if (type == 0)
			return getTypeInRange(source, GameSession.getInstance().ships, range);
		else if (type == 1)
			return getTypeInRange(source, GameSession.getInstance().towers, range);
		else
			return null;
	}

	/**
	 * return a random ship of the desired type that's in range
	 * @param source
	 * @param ships
	 * @param range
	 * @return
	 */
	private static Ship getTypeInRange(Ship source, Array<Ship> ships, float range) {
		Array<Ship> inRange = new Array<Ship>();
		float range_squared = range * range;

		for (int i = 0; i < ships.size; i++) {
			Ship ship = ships.get(i);
			float currentDistance = source.position.dst(ship.position);

			if (ship.alive && source.id != ship.id && (currentDistance < range_squared)) {
				inRange.add(ship);
			}
		}

		if (inRange.size > 0) {
			return inRange.get(MathUtils.random(0, inRange.size - 1));
		} else {
			return null;
		}
	}
}
