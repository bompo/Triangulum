package de.swagner.triangulum;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import de.swagner.triangulum.controls.SinglePlayerControls;
import de.swagner.triangulum.render.RenderScene;
import de.swagner.triangulum.units.Bullet;
import de.swagner.triangulum.units.Ship;

public class SinglePlayerGameScreen extends DefaultScreen {

	OrthographicCamera cam;

	SpriteBatch batch;
	SpriteBatch fadeBatch;
	Sprite blackFade;
	
	Music tune;
	Sound effect1;
	Sound baseSound;
	
	BitmapFont font;
	
	boolean gameFinished = false;
	float finishCountDown = 5;
	int points = 0;
	boolean won = false;
	
	int nextActionFromAI = 0;
	
	RenderScene renderScene;
	
	ShapeRenderer shapeRenderer = new ShapeRenderer();

	float fade = 1.0f;
	boolean finished = false;

	public SinglePlayerGameScreen(Game game) {
		super(game);
		
		tune = Gdx.audio.newMusic(Gdx.files.internal("data/tune1.ogg"));
		tune.setLooping(true);
		tune.setVolume(1.0f);
		tune.play();		
		
		effect1 = Gdx.audio.newSound(Gdx.files.internal("data/effect1.ogg"));
		baseSound = Gdx.audio.newSound(Gdx.files.internal("data/base.ogg"));
		
		GameSession.getInstance().newSinglePlayerGame(0);
		
		Gdx.input.setInputProcessor(new SinglePlayerControls(GameSession.getInstance().player));
		
		renderScene = new RenderScene();

		batch = new SpriteBatch();
		batch.getProjectionMatrix().setToOrtho2D(0, 0, 800, 480);
		
		blackFade = new Sprite(new Texture(Gdx.files.internal("data/black.png")));
		fadeBatch = new SpriteBatch();
		fadeBatch.getProjectionMatrix().setToOrtho2D(0, 0, 1, 1);
		
		font = Resources.getInstance().font;
		
		cam = GameSession.getInstance().cam;

		initRender();
	}

	public void initRender() {
		Gdx.graphics.getGL20().glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
		Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0);
		Gdx.gl.glClearColor(0.2f ,0.2f ,0.2f ,1.0f);
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
		cam.viewportWidth = 800;
		cam.viewportHeight = 480;		
		initRender();
		renderScene.reInit();
	}

	@Override
	public void show() {
	}
	
	private int steps = 0;
	private float meassureSteps = 0;
	
	private final float FIXEDDELTA = 0.01f;
	private final float GAMESPEED = 0.02f;
	private float deltaCount = 0;	
	@Override
	public void render(float deltaTime) {
		deltaCount += deltaTime;
		meassureSteps += Gdx.graphics.getRawDeltaTime();
		
		if(steps < 60) {
			deltaCount -= FIXEDDELTA;
			logic(GAMESPEED);
			updateUnits(GAMESPEED);
			steps = steps + 1;
		}
				
		renderFrame(FIXEDDELTA);
		
		if(meassureSteps >= 1f) {
			while(steps < 60) {
				deltaCount -= FIXEDDELTA;
				logic(GAMESPEED);
				updateUnits(GAMESPEED);						
				steps = steps + 1;
			}
			processInput();
			System.out.println(steps + " steps");
			System.out.println(Gdx.graphics.getFramesPerSecond() + " fps");
			System.out.println(meassureSteps + " ms");
			meassureSteps -= meassureSteps;
			steps = 0;
		}
	}
	
	//makes new decisions for units
	public void logic(float delta) {
		logicUnits();
		logicAI();
		
		//check if win or losw
		if(GameSession.getInstance().player.hitPoints <= 0 && gameFinished == false) {
			gameFinished = true;
			won = false;
		}		
		if(GameSession.getInstance().opponent.hitPoints <= 0 && gameFinished == false) {
			gameFinished = true;
			won = true;
			points = GameSession.getInstance().player.hitPoints;
		}
		if(gameFinished) {
			finishCountDown -= delta;
			if(finishCountDown <= 0) {
				finished = true;
			}
		}
		
		// update energy meter
		GameSession.getInstance().player.energy += delta * 2.5f;
		if(GameSession.getInstance().player.energy >= 100) {
			GameSession.getInstance().player.energy = 100;	
		}
		GameSession.getInstance().opponent.energy += delta * 2.5f;
		if(GameSession.getInstance().opponent.energy >= 100) {
			GameSession.getInstance().opponent.energy = 100;	
		}
	}
	
	public void processInput() {
		for(int i = 0; i < GameSession.getInstance().player.inputList.size; i++) {
			GameSession.getInstance().addPlayerShip(GameSession.getInstance().player.inputList.get(i));
		}
		GameSession.getInstance().player.inputList.clear();
		
		for(int i = 0; i < GameSession.getInstance().opponent.inputList.size; i++) {
			GameSession.getInstance().addEnemyShip(GameSession.getInstance().opponent.inputList.get(i));
		}
		GameSession.getInstance().opponent.inputList.clear();
		
		
	}

	public void renderFrame(final float deltaTime) {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		
		cam.update();
		
		renderScene.updateCamera(cam);
		renderScene.render(deltaTime);
		
		batch.begin();
		if(gameFinished) {
			Resources.getInstance().decorateFont.setScale(1f);
			if(won) {
				Resources.getInstance().decorateFont.drawMultiLine(batch, "YOU WON\nPOINTS: " + points, 90, 340);
			} else {
				Resources.getInstance().decorateFont.drawMultiLine(batch, "YOU LOSE", 180, 340);
			}
		}
		batch.end();

		// FadeInOut
		if (!finished && fade > 0) {
			fade = Math.max(fade - (FIXEDDELTA), 0);
			fadeBatch.begin();
			blackFade.setColor(blackFade.getColor().r, blackFade.getColor().g,
					blackFade.getColor().b, fade);
			blackFade.draw(fadeBatch);
			fadeBatch.end();
		}

		if (finished) {
			fade = Math.min(fade + (FIXEDDELTA), 1);
			fadeBatch.begin();
			blackFade.setColor(blackFade.getColor().r, blackFade.getColor().g,
					blackFade.getColor().b, fade);
			blackFade.draw(fadeBatch);
			fadeBatch.end();
			if (fade >= 1) {
				if(Configuration.getInstance().music) {
					tune.stop();	
				}
				game.setScreen(new StartScreen(game));
			}
		}
	}
	
	//units follow their current orders
	private void updateUnits(float delta) {
		for (int i = 0; i < GameSession.getInstance().ships.size; i++) {
			Ship ship = GameSession.getInstance().ships.get(i);
			ship.update(delta);
			if(ship.alive == false) {
				long id = effect1.play();
				effect1.setPan(id, 1 - ((ship.position.x/80.f)*2.f), 1);
				effect1.setPitch(id, 0.5f + ship.position.y/48.f);
				
				GameSession.getInstance().ships.removeIndex(i);
			}			

			if(ship.position.x > 148 && ship.id == 1) {
				ship.alive = false;
				GameSession.getInstance().grid.setValue(MathUtils.floor(ship.position.x),MathUtils.floor(ship.position.y), 3); 
				GameSession.getInstance().opponent.hitPoints -= 10;
				
				long id = baseSound.play();
				baseSound.setPan(id, 1, 1);
			}
			
			if(ship.position.x < 10 && ship.id == 2) {
				ship.alive = false;
				GameSession.getInstance().player.hitPoints -= 10;
				
				long id = baseSound.play();
				baseSound.setPan(id, -1, 1);
			}
			
			if(ship.position.y < -1) {
				ship.alive = false;
			}
			
			if(ship.position.y > 49) {
				ship.alive = false;
			}
			
		}
		
		for (int i = 0; i < GameSession.getInstance().towers.size; i++) {
			Ship ship = GameSession.getInstance().towers.get(i);
			ship.update(delta);
			if(ship.alive == false) {
				long id = effect1.play();
				effect1.setPan(id, 1 - ((ship.position.x/80.f)*2.f), 1);
				effect1.setPitch(id, 0.5f + ship.position.y/48.f);
				
				GameSession.getInstance().towers.removeIndex(i);				
			}			
		}
		
		for (int i = 0; i < GameSession.getInstance().bullets.size; i++) {
			Bullet bullet = GameSession.getInstance().bullets.get(i);
			bullet.update(delta);
			if(bullet.alive == false) {
				GameSession.getInstance().bullets.removeIndex(i);
			}
			for (int n = 0; n < GameSession.getInstance().ships.size; n++) {
				Ship ship = GameSession.getInstance().ships.get(n);
				if(ship.id != bullet.id && ship.position.dst(bullet.position) < 2.f) {
					bullet.alive = false;
					ship.hit();
				}
			}
		}
	}
	
	private void logicUnits() {
		for (int i = 0; i < GameSession.getInstance().ships.size; i++) {
			Ship ship = GameSession.getInstance().ships.get(i);
			ship.logic();
		}
		
		for (int i = 0; i < GameSession.getInstance().towers.size; i++) {
			Ship ship = GameSession.getInstance().towers.get(i);
			ship.logic();		
		}
		
		for (int i = 0; i < GameSession.getInstance().bullets.size; i++) {
			Bullet bullet = GameSession.getInstance().bullets.get(i);
			bullet.logic();
		}
	}
	
	private void logicAI() {
		//mode 0 -> build towers
		//mode 1 -> build ships
		//mode 2 -> saver energy
		int mode = 2;
		
		nextActionFromAI -= 1;
		if(nextActionFromAI > 0) {
			return;
		}
		
		int numberOfPlayerShips = 0;
		int numberOfEnemyShips = 0;
		
		int numberOfPlayerTowers = 0;
		int numberOfEnemyTowers = 0;
		
		for (int i = 0; i < GameSession.getInstance().ships.size; i++) {
			Ship ship = GameSession.getInstance().ships.get(i);
			if(ship.id == 1) {
				numberOfPlayerShips += 1;
			} else {
				numberOfEnemyShips += 1;
			}			
		}
		
		for (int i = 0; i < GameSession.getInstance().towers.size; i++) {
			Ship ship = GameSession.getInstance().towers.get(i);
			if(ship.id == 1) {
				numberOfPlayerTowers += 1;
			} else {
				numberOfEnemyTowers += 1;
			}			
		}
		
		if(numberOfPlayerShips > 5 && numberOfEnemyShips > 5 && numberOfEnemyTowers < 1) {
			mode = 0;
		}
		if(numberOfPlayerShips > 5 && numberOfEnemyShips < 10) {
			mode = 1;
		}
		if(numberOfPlayerShips > 2 && numberOfEnemyShips < 2) {
			mode = 2;
		}
		if(numberOfEnemyShips < 7) {
			mode = 1;
		}
		if(GameSession.getInstance().opponent.energy > 65) {
			mode = 1;
		}
		if(numberOfPlayerTowers > 3 && numberOfEnemyShips < 7) {
			mode = 1;
		}

		if(mode == 0) {
			//search all ships and place tower in mean position
			float posY = 0;
			float posX = 0;
			for (int i = 0; i < GameSession.getInstance().ships.size; i++) {
				Ship ship = GameSession.getInstance().ships.get(i);
				if(ship.id == 1) {
					posX += ship.position.x;
					posY += ship.position.y;					
				} 		
			}
			posX /= numberOfPlayerShips;
			posY /= numberOfPlayerShips;
			
			posX = MathUtils.random(posX + 20, posX - 10);
			posY = MathUtils.random(posY + 10, posY - 10);
			
			if(posX < 80) {
				posX = 80;
			}
			posX += 20;
			if(posX > 120) {
				posX = 100;
			}			
			
			GameSession.getInstance().opponent.inputList.add(new Vector2(posX, posY));
		} else if(mode == 1) {
			GameSession.getInstance().opponent.inputList.add(new Vector2(155, MathUtils.random(10, 40)));
		}
		
		nextActionFromAI = 2;
	}

	@Override
	public void hide() {
	}

	@Override
	public void dispose() {
	}

}
