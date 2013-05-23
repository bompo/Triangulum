package de.swagner.triangulum.render;

import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;

import de.swagner.triangulum.GameSession;
import de.swagner.triangulum.units.Bullet;
import de.swagner.triangulum.units.Ship;

public class RenderScene {
	static final String TAG = "de.swagner.ld26";

	float timer;
	float delta;
	float alpha = 1;
	
	OrthographicCamera cam;

	ShaderProgram shader;
	
	ShapeRenderer shapeRenderer = new ShapeRenderer();
	
	Matrix4 tmp = new Matrix4().idt();
	Matrix4 model = new Matrix4().idt();
	
	Preferences prefs;
	
	Bloom bloom;
	
	public RenderScene() {
		prefs = Gdx.app.getPreferences(TAG);
		
		bloom = new Bloom();		
		bloom.setBloomIntesity(5.0f);
		bloom.setClearColor(1, 1, 1, 1);
	}
	
	public void reInit() {
		bloom = new Bloom();		
		bloom.setBloomIntesity(5.0f);
		bloom.setClearColor(1, 1, 1, 1);
	}
	
	public void updateCamera(OrthographicCamera cam) {
		this.cam = cam;
	}

	public void render(float delta) {
		this.delta = delta;
		
		timer = timer + delta;
		
		Gdx.gl.glDisable(GL20.GL_CULL_FACE);
		Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);
		Gdx.gl.glEnable(GL20.GL_BLEND);
		
		cam.update();
	
		shapeRenderer.setProjectionMatrix(cam.combined);
		
		bloom.capture();
		drawFilledTriangles();
		drawBase();
		drawOutlineTriangles();
		drawUI();
		bloom.render();
	}
	
	
	private void drawBase() {
		shapeRenderer.begin(ShapeType.Filled);		
		int x = 0;
		int y = 0;
		//player base
		for(int i = 0; i < 6*51*2; i++) {
			Color color = new Color();
			Random random = new Random(x*y);
			random.nextFloat();
			float rnd = MathUtils.sin(timer * random.nextFloat()) * ((0.5f - GameSession.getInstance().grid.getOriginalValue(x/10, 470 + y)))/2.f;
			float gridValue = GameSession.getInstance().grid.getValue((int) MathUtils.floor(x/5), (int) MathUtils.floor((470 + y)/10));
			float intensity = (gridValue - 1) / 4.f;
			color = new Color(0.55f + rnd + intensity, 0.55f + rnd + intensity, 0.1f + rnd + intensity, 1.0f);
			
			shapeRenderer.setColor(color);
			
			if(x%10 == 0) {
				shapeRenderer.triangle(x, y, x + 5f, y + 10, x + 10, y);
			} else {
				shapeRenderer.triangle(x, y + 10, x + 10, y + 10, x + 5, y);				
			}
			x = x + 5;
			if(x > 60) {
				x = 0;
				y = y - 10;
			}
		}
		
		x = 0;
		y = 0;
		//enemy base
		for(int i = 0; i < 6*51*2; i++) {
			int offset = 730;
			
			Color color = new Color();
			Random random = new Random(x*y);
			random.nextFloat();
			float rnd = MathUtils.sin(timer * random.nextFloat()) * ((0.5f - GameSession.getInstance().grid.getOriginalValue((x+offset)/10, 470 + y)))/2.f;
			float gridValue = GameSession.getInstance().grid.getValue((int) MathUtils.floor((x + offset)/5), (int) MathUtils.floor((470 + y)/10));
			float intensity = (gridValue - 1) / 4.f;
			color = new Color(0.05f + rnd + intensity, 0.3f + rnd + intensity, 0.6f + rnd + intensity, 1.0f);
			
			shapeRenderer.setColor(color);
			
			if(x%10 == 0) {
				shapeRenderer.triangle(x + offset, y, x + 5f + offset, y + 10, x + 10 + offset, y);
			} else {
				shapeRenderer.triangle(x + offset, y + 10, x + 10 + offset, y + 10, x + 5 + offset, y);				
			}
			x = x + 5;
			if(x > 60) {
				x = 0;
				y = y - 10;
			}
		}		
		shapeRenderer.end();
	}
	
	private void drawUI() {
		shapeRenderer.begin(ShapeType.Filled);	
		
		int x = 0;
		int y = 0;
		//draw ground
		for(int i = 0; i < 210; i++) {
			int offsetX = 400 - 100;
			int offsetY = -420;
			
			Color color = new Color();
			Random random = new Random(x*y);
			random.nextFloat();
			float rnd = MathUtils.sin(timer * random.nextFloat()) * ((0.5f - GameSession.getInstance().grid.getOriginalValue((x+offsetX)/10, 470 + y + offsetY)))/2.f;
			color = new Color(0.01f + rnd, 0.01f + rnd, 0.07f + rnd, 1.0f);
					
			shapeRenderer.setColor(color);
			
			if(x%10 == 0) {
				shapeRenderer.triangle(x + offsetX, y + offsetY, x + 5f + offsetX, y + 10 + offsetY, x + 10 + offsetX, y + offsetY);
			} else {
				shapeRenderer.triangle(x + offsetX, y + 10 + offsetY, x + 10 + offsetX, y + 10 + offsetY, x + 5 + offsetX, y + offsetY);				
			}
			x = x + 5;
			if(x > 200) {
				x = 0;
				y = y - 10;
			}
		}
				
		x = 390;
		y = -430;
		//draw hitPoints meter player
		for(int i = 0; i < GameSession.getInstance().player.hitPoints/6.0f; i++) {
			int offsetX = 0;
			int offsetY = 0;
			
			Color color = new Color();
			Random random = new Random(x*y);
			random.nextFloat();
			float rnd = MathUtils.sin(timer * random.nextFloat()) * ((0.5f - GameSession.getInstance().grid.getOriginalValue((x+offsetX)/10, 470 + y + offsetY)))/2.f;
			color = new Color(0.2f + rnd, 0.55f + rnd, 0.2f + rnd, 1.0f);
			
			shapeRenderer.setColor(color);
			
			if(x%10 == 0) {
				shapeRenderer.triangle(x + offsetX, y + offsetY, x + 5f + offsetX, y + 10 + offsetY, x + 10 + offsetX, y + offsetY);
			} else {
				shapeRenderer.triangle(x + offsetX, y + 10 + offsetY, x + 10 + offsetX, y + 10 + offsetY, x + 5 + offsetX, y + offsetY);				
			}
			x = x - 5;
		}
		
		x = 390;
		y = -450;
		//draw energy meter player
		for(int i = 0; i < GameSession.getInstance().player.energy/6.0f; i++) {
			int offsetX = 0;
			int offsetY = 0;
			
			Color color = new Color();
			Random random = new Random(x*y);
			random.nextFloat();
			float rnd = MathUtils.sin(timer * random.nextFloat()) * ((0.5f - GameSession.getInstance().grid.getOriginalValue((x+offsetX)/10, 470 + y + offsetY)))/2.f;
			color = new Color(0.6f + rnd, 0.6f + rnd, 0.6f + rnd, 1.0f);
			
			shapeRenderer.setColor(color);
			
			if(x%10 == 0) {
				shapeRenderer.triangle(x + offsetX, y + offsetY, x + 5f + offsetX, y + 10 + offsetY, x + 10 + offsetX, y + offsetY);
			} else {
				shapeRenderer.triangle(x + offsetX, y + 10 + offsetY, x + 10 + offsetX, y + 10 + offsetY, x + 5 + offsetX, y + offsetY);				
			}
			x = x - 5;
		}
		
		x = 410;
		y = -430;
		//draw hitPoints meter opponent
		for(int i = 0; i < GameSession.getInstance().opponent.hitPoints/6.0f; i++) {
			int offsetX = 0;
			int offsetY = 0;
			
			Color color = new Color();
			Random random = new Random(x*y);
			random.nextFloat();
			float rnd = MathUtils.sin(timer * random.nextFloat()) * ((0.5f - GameSession.getInstance().grid.getOriginalValue((x+offsetX)/10, 470 + y + offsetY)))/2.f;
			color = new Color(0.2f + rnd, 0.55f + rnd, 0.2f + rnd, 1.0f);
			
			shapeRenderer.setColor(color);
			
			if(x%10 == 0) {
				shapeRenderer.triangle(x + offsetX, y + offsetY, x + 5f + offsetX, y + 10 + offsetY, x + 10 + offsetX, y + offsetY);
			} else {
				shapeRenderer.triangle(x + offsetX, y + 10 + offsetY, x + 10 + offsetX, y + 10 + offsetY, x + 5 + offsetX, y + offsetY);				
			}
			x = x + 5;
		}
		
		x = 410;
		y = -450;
		//draw energy meter opponent
		for(int i = 0; i < GameSession.getInstance().opponent.energy/6.0f; i++) {
			int offsetX = 0;
			int offsetY = 0;
			
			Color color = new Color();
			Random random = new Random(x*y);
			random.nextFloat();
			float rnd = MathUtils.sin(timer * random.nextFloat()) * ((0.5f - GameSession.getInstance().grid.getOriginalValue((x+offsetX)/10, 470 + y + offsetY)))/2.f;
			color = new Color(0.6f + rnd, 0.6f + rnd, 0.6f + rnd, 1.0f);
			
			shapeRenderer.setColor(color);
			
			if(x%10 == 0) {
				shapeRenderer.triangle(x + offsetX, y + offsetY, x + 5f + offsetX, y + 10 + offsetY, x + 10 + offsetX, y + offsetY);
			} else {
				shapeRenderer.triangle(x + offsetX, y + 10 + offsetY, x + 10 + offsetX, y + 10 + offsetY, x + 5 + offsetX, y + offsetY);				
			}
			x = x + 5;
		}
		
		shapeRenderer.end();
	}
	
	private void drawOutlineTriangles() {
		shapeRenderer.begin(ShapeType.Line);		
		int x = 0;
		int y = 0;
		for(int i = 0; i < 80*48*2; i++) {
			if(x%10 == 0) {
				shapeRenderer.setColor(0.0f, 0.0f, 0.0f, 0.05f);
				shapeRenderer.line(x, y, x + 5f, y + 10);
				shapeRenderer.line(x + 5f, y + 10, x + 10, y);
				shapeRenderer.line(x + 10, y, x, y);
			} else {
				shapeRenderer.setColor(0.1f, 0.1f, 0.1f, 0.1f);
				shapeRenderer.line(x, y + 10, x + 10, y + 10);
				shapeRenderer.line(x + 10, y + 10, x + 5, y);
				shapeRenderer.line(x + 5, y, x, y + 10);
				
			}
			x = x + 5;
			if(x > 800) {
				x = 0;
				y = y - 10;
			}
		}		
		shapeRenderer.end();
	}
	
	private void drawFilledTriangles() {
		shapeRenderer.begin(ShapeType.Filled);		
		int x = 0;
		int y = 0;
		for(int i = 0; i < 80*48*2; i++) {
			
			Color color = new Color();
			Random random = new Random(x*y);
			random.nextFloat();
			float rnd = MathUtils.sin(timer * random.nextFloat()) * ((0.5f - GameSession.getInstance().grid.getOriginalValue(x/10, 470 + y)))/2.f;
			Color gridColor = new Color(0.10f + rnd, 0.2f + rnd, 0.15f + rnd, 1.0f);
			
			float gridValue = GameSession.getInstance().grid.getValue((int) MathUtils.floor(x/5), (int) MathUtils.floor((470 + y)/10));
			if(gridValue > 2.0f) {
				//enemy color
				float intensity = (gridValue - 2) / 20.f;
				
				color = new Color(0.2f + rnd + intensity, 0.45f + rnd + intensity, 0.75f + rnd + intensity, 1.0f);
				color = gridColor.lerp(color, (gridValue - 2));
				
				if(gridValue - delta/10.f > 2.0f) {
					GameSession.getInstance().grid.setValue((int) MathUtils.floor(x/5), (int) MathUtils.floor((470 + y)/10), gridValue - delta/20.f);
				} else {
					GameSession.getInstance().grid.setValue((int) MathUtils.floor(x/5), (int) MathUtils.floor((470 + y)/10), MathUtils.random());
				}
				
			} else if(gridValue > 1.0f){
				//player color
				float intensity = (gridValue - 1) / 10.f;
				
				color = new Color(0.55f + rnd + intensity, 0.55f + rnd + intensity, 0.1f + rnd + intensity, 1.0f);
				color = gridColor.lerp(color, (gridValue - 1));
				
				if(gridValue - delta/10.f > 1.0f) {
					GameSession.getInstance().grid.setValue((int) MathUtils.floor(x/5), (int) MathUtils.floor((470 + y)/10), gridValue - delta/20.f);
				} else {
					GameSession.getInstance().grid.setValue((int) MathUtils.floor(x/5), (int) MathUtils.floor((470 + y)/10), MathUtils.random());
				}
			} else {
				//grid color
				color = gridColor;
			}
									
			for(int n = 0; n < GameSession.getInstance().ships.size; n++) {
				Ship ship = GameSession.getInstance().ships.get(n);
				if((int) MathUtils.floor(ship.position.x) == (int) MathUtils.floor(x/5) && (int) MathUtils.floor(ship.position.y)  == (int) MathUtils.floor((470 + y)/10)) {
					if(ship.id == 1) {
						color = new Color(0.9f + rnd, 0.9f + rnd, 0.4f + rnd, 1.0f);
						GameSession.getInstance().grid.setValue((int) MathUtils.floor(x/5), (int) MathUtils.floor((470 + y)/10), 2);
					} else {
						color = new Color(0.05f + rnd, 0.3f + rnd, 0.6f + rnd, 1.0f);
						GameSession.getInstance().grid.setValue((int) MathUtils.floor(x/5), (int) MathUtils.floor((470 + y)/10), 3);
					}
				}
			}
			
			for(int n = 0; n < GameSession.getInstance().towers.size; n++) {
				Ship tower = GameSession.getInstance().towers.get(n);
				if((int) MathUtils.floor(tower.position.x) == (int) MathUtils.floor(x/5) && (int) MathUtils.floor(tower.position.y)  == (int) MathUtils.floor((470 + y)/10)) {
					if(tower.id == 1) {
						color = new Color(1.0f + rnd, 1.0f + rnd, 0.5f + rnd, 1.0f);						
						int xPos = (int) MathUtils.floor(x / 5);
						int yPos = (int) MathUtils.floor((470 + y)/ 10);
						
						GameSession.getInstance().grid.setValue(xPos, yPos, 2.9f);
						GameSession.getInstance().grid.setValue(xPos, yPos + 1, 2);
						GameSession.getInstance().grid.setValue(xPos, yPos - 1, 2);
						GameSession.getInstance().grid.setValue(xPos + 1, yPos, 2);
						GameSession.getInstance().grid.setValue(xPos - 1, yPos, 2);
					} else {
						color = new Color(0.35f + rnd, 0.6f + rnd, 0.9f + rnd, 1.0f);
						int xPos = (int) MathUtils.floor(x / 5);
						int yPos = (int) MathUtils.floor((470 + y)/ 10);
						
						GameSession.getInstance().grid.setValue(xPos, yPos, 3.9f);
						GameSession.getInstance().grid.setValue(xPos, yPos + 1, 3);
						GameSession.getInstance().grid.setValue(xPos, yPos - 1, 3);
						GameSession.getInstance().grid.setValue(xPos + 1, yPos, 3);
						GameSession.getInstance().grid.setValue(xPos - 1, yPos, 3);
					}
				}
			}
			
			for(int n = 0; n < GameSession.getInstance().bullets.size; n++) {
				Bullet bullet = GameSession.getInstance().bullets.get(n);
				if((int) MathUtils.floor(bullet.position.x) == (int) MathUtils.floor(x/5) && (int) MathUtils.floor(bullet.position.y)  == (int) MathUtils.floor((470 + y)/10)) {
					if(bullet.id == 1) {
						color = new Color(0.9f + rnd, 0.9f + rnd, 0.4f + rnd, 1.0f);
						GameSession.getInstance().grid.setValue((int) MathUtils.floor(x/5), (int) MathUtils.floor((470 + y)/10), 1.5f);
					} else {
						color = new Color(0.25f + rnd, 0.5f + rnd, 0.8f + rnd, 1.0f);
						GameSession.getInstance().grid.setValue((int) MathUtils.floor(x/5), (int) MathUtils.floor((470 + y)/10), 2.5f);
					}
				}
			}
			
			shapeRenderer.setColor(color);
			
			if(x%10 == 0) {
				shapeRenderer.triangle(x, y, x + 5f, y + 10, x + 10, y);
			} else {
				shapeRenderer.triangle(x, y + 10, x + 10, y + 10, x + 5, y);				
			}
			x = x + 5;
			if(x > 800) {
				x = 0;
				y = y - 10;
			}
		}		
		shapeRenderer.end();
	}

}
