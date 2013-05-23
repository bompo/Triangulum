package de.swagner.triangulum;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

public class Resources {
	public BitmapFont font;
	
	public static Resources instance;
	
	BitmapFont decorateFont;

	public static Resources getInstance() {
		if (instance == null) {
			instance = new Resources();
		}
		return instance;
	}

	public Resources() {		
		reInit();	
	}
	

	public void reInit() {
		font = new BitmapFont();
//		font = new BitmapFont(Gdx.files.internal("data/arial.fnt"), false);
//		font.setScale(0.4f);
		decorateFont = new BitmapFont(Gdx.files.internal("data/edge.fnt"), false);
		
//		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("data/edge.ttf"));
//		decorateFont = generator.generateFont(80);
//		generator.dispose();
	}

	public void dispose() {
		font.dispose();
	}
}
