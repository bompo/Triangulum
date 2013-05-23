package de.swagner.triangulum;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

import de.swagner.triangulum.render.RenderScene;

public class TutorialScreen extends DefaultScreen implements InputProcessor {

	float startTime = 0;
	OrthographicCamera cam;

	SpriteBatch batch;
	SpriteBatch fadeBatch;
	Sprite blackFade;
	
	Music tune;
	Sound effect1;
	Sound baseSound;
	
	BitmapFont font;
	BitmapFont decorateFont;
	
	RenderScene renderScene;

	float fade = 1.0f;
	boolean finished = false;

	float delta;
	
	ShapeRenderer shapeRenderer = new ShapeRenderer();

	public TutorialScreen(Game game) {
		super(game);
		
		if(Configuration.getInstance().music) {
			tune = Gdx.audio.newMusic(Gdx.files.internal("data/tune1.ogg"));
			tune.setLooping(true);
			tune.setVolume(0.7f);
			tune.play();
		}
		
		effect1 = Gdx.audio.newSound(Gdx.files.internal("data/effect1.ogg"));
		baseSound = Gdx.audio.newSound(Gdx.files.internal("data/base.ogg"));
		
		GameSession.getInstance().newSinglePlayerGame(0);
		
		Gdx.input.setInputProcessor(this);
		
		renderScene = new RenderScene();

		batch = new SpriteBatch();
		batch.getProjectionMatrix().setToOrtho2D(0, 0, 800, 480);
		
		blackFade = new Sprite(new Texture(Gdx.files.internal("data/black.png")));
		fadeBatch = new SpriteBatch();
		fadeBatch.getProjectionMatrix().setToOrtho2D(0, 0, 1, 1);
		
		font = Resources.getInstance().font;
		
		decorateFont = Resources.getInstance().decorateFont;
		
		cam = GameSession.getInstance().cam;

		shapeRenderer.setProjectionMatrix(cam.combined);
		
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
	
	private float deltaCount = 0;	
	@Override
	public void render(float deltaTime) {
		deltaCount += deltaTime;
		if(deltaCount > 0.01) {
			deltaCount = 0;
			renderFrame(0.02f);
		}
	}

	public void renderFrame(float deltaTime) {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		delta = Math.min(0.1f, deltaTime);

		startTime += delta;
		
		//update energy meter
		GameSession.getInstance().player.energy += delta * 2.f;
		if(GameSession.getInstance().player.energy >= 100) {
			GameSession.getInstance().player.energy = 100;	
		}
		GameSession.getInstance().opponent.energy += delta * 2.f;
		if(GameSession.getInstance().opponent.energy >= 100) {
			GameSession.getInstance().opponent.energy = 100;	
		}
		
		cam.update();
		
		renderScene.updateCamera(cam);
		renderScene.render(delta);
		
		Gdx.gl.glEnable(GL20.GL_BLEND);
		
		shapeRenderer.begin(ShapeType.Line);	
		shapeRenderer.setColor(new Color(1f, 1f, 1f, 1.0f));
		shapeRenderer.line(-150, -210, -110, -210);
		shapeRenderer.line(-150, -230, -110, -230);
		shapeRenderer.line(150, -210, 110, -210);
		shapeRenderer.line(150, -230, 110, -230);
		
		shapeRenderer.line(-340, 170, -300, 170);
		shapeRenderer.end();
		
		batch.begin();
		font.drawMultiLine(batch, "Hit points of your base", 100, 40);
		font.drawMultiLine(batch, "Energy of your base", 114, 20);
		font.drawMultiLine(batch, "Hit points of enemy base", 560, 40);
		font.drawMultiLine(batch, "Energy of enemy base", 560, 20);
		
		font.drawMultiLine(batch, "Click in this area\n to build fighters\n(cost 5 energy)", 110, 440);
		font.drawMultiLine(batch, "Click in your half\n to build towers\n(cost 20 energy)", 200, 280);
		batch.end();

		// FadeInOut
		if (!finished && fade > 0) {
			fade = Math.max(fade - (delta), 0);
			fadeBatch.begin();
			blackFade.setColor(blackFade.getColor().r, blackFade.getColor().g,
					blackFade.getColor().b, fade);
			blackFade.draw(fadeBatch);
			fadeBatch.end();
		}

		if (finished) {
			fade = Math.min(fade + (delta), 1);
			fadeBatch.begin();
			blackFade.setColor(blackFade.getColor().r, blackFade.getColor().g,
					blackFade.getColor().b, fade);
			blackFade.draw(fadeBatch);
			fadeBatch.end();
			if (fade >= 1) {
				if(Configuration.getInstance().music) {
					tune.stop();
				}
				game.setScreen(new SinglePlayerGameScreen(game));
			}
		}
	}
	
	@Override
	public void hide() {
	}

	@Override
	public void dispose() {
	}

	@Override
	public boolean keyDown(int keycode) {
		if (keycode == Input.Keys.F) {
			if (Gdx.app.getType() == ApplicationType.Desktop) {
				if (!Gdx.graphics.isFullscreen()) {
					Gdx.graphics.setDisplayMode(
							Gdx.graphics.getDesktopDisplayMode().width,
							Gdx.graphics.getDesktopDisplayMode().height, true);
					Configuration.getInstance().setFullscreen(true);
				} else {
					Gdx.graphics.setDisplayMode(800, 480, false);
					Configuration.getInstance().setFullscreen(false);
				}
			}
		} else if (keycode == Input.Keys.ESCAPE) {
//			Gdx.app.exit();
		} else {
			finished = true;
		}
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		finished = true;
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		// TODO Auto-generated method stub
		return false;
	}

}
