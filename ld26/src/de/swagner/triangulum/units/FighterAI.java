package de.swagner.triangulum.units;


public class FighterAI extends DefaultAI {

	public FighterAI(Ship soldier) {
		super(soldier);
		retarget();
	}
}
