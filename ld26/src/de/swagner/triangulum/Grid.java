package de.swagner.triangulum;

import com.badlogic.gdx.math.MathUtils;

public class Grid {
	
	final int GRIDSIZEWIDTH = 800;
	final int GRIDSIZEHEIGHT = 480;
	
	public float[][] grid = new float[800][480];
	public float[][] gridOriginal = new float[800][480];

	public Grid() {
		
		for(int x = 0; x < GRIDSIZEWIDTH; x++) {
			for(int y = 0; y < GRIDSIZEHEIGHT; y++) {
				float rnd = MathUtils.random();
				grid[x][y] = rnd;
				gridOriginal[x][y] = rnd;
			}
		}		
	}
	
	public float getValue(int x, int y) {
		if(x < 0 || x > 800 || y < 0 || y > 480) return 0;
		return grid[x][y];
	}
	
	public float getOriginalValue(int x, int y) {
		if(x < 0 || x > 800 || y < 0 || y > 480) return 0;
		return gridOriginal[x][y];
	}
	
	public void setValue(int x, int y, float value) {
		if(x < 0 || x > 800 || y < 0 || y > 480) return;
		grid[x][y] = value;
	}
	
}
