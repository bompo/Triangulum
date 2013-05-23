package de.swagner.triangulum.controls;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import de.swagner.triangulum.Configuration;
import de.swagner.triangulum.GameSession;
import de.swagner.triangulum.Player;

public class GameControls implements InputProcessor {

	final Vector2 last = new Vector2(0, 0);
	final Vector2 delta = new Vector2();
	
	public void keyDownOptions(int keycode) {
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
		}
	    if (keycode == Input.Keys.ESCAPE) {
			Gdx.app.exit();
		} 
		
		if (keycode == Input.Keys.F1) {
			Configuration.getInstance().setDebug(!Configuration.getInstance().debug);
		}
		
		if (keycode == Input.Keys.F2) {
			Configuration.getInstance().setMusic(!Configuration.getInstance().music);
		}
	}

	public void keyDownPlayer(Player player, int keycode) {
		if (keycode == Input.Keys.R) {
			GameSession.getInstance().newSinglePlayerGame(0);
		}
	}

	public void keyUpPlayer(Player player, int keycode) {
	}

	@Override
	public boolean keyDown(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDown(int x, int y, int pointer, int button) {
		x = (int) (x / (float) Gdx.graphics.getWidth() * 800);
		y = (int) (y / (float) Gdx.graphics.getHeight() * 480);
	
		Vector3 vec = new Vector3(x,y,0);
				
		GameSession.getInstance().player.inputList.add(new Vector2(vec.x/5.f, 48 - vec.y/10.f));
		
		last.set(x,y);
		
		return false;
	}

	protected int lastTouchX;
	protected int lastTouchY;

	@Override
	public boolean touchUp(int x, int y, int pointer, int button) {
		x = (int) (x / (float) Gdx.graphics.getWidth() * 800);
		y = (int) (y / (float) Gdx.graphics.getHeight() * 480);
		return false;
	}

	@Override
	public boolean touchDragged(int x, int y, int pointer) {
		x = (int) (x / (float) Gdx.graphics.getWidth() * 800);
		y = (int) (y / (float) Gdx.graphics.getHeight() * 480);
		
//		delta.set(x, y).sub(last);
//		delta.mul(1f);
//		Vector3 temp = new Vector3(delta.x, delta.y, 0);
//		Quaternion rotation = new Quaternion();
//		GameSession.getInstance().cam.combined.getRotation(rotation);
//		rotation.transform(temp);
//		GameSession.getInstance().cam.translate(temp);
//		GameSession.getInstance().cam.update();
//		last.set(x, y);
//		
//		Vector3 temp1 = new Vector3(x, y * GameSession.getInstance().cam.zoom, 0);
//		
//		System.out.println(temp1);
//		
//		if(GameSession.getInstance().cam.position.y < -310) {
//			GameSession.getInstance().cam.position.y = -310;
//		}
//		
//		if(GameSession.getInstance().cam.position.y > -115) {
//			
//			
//		}
		
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
//		GameSession.getInstance().cam.zoom = GameSession.getInstance().cam.zoom + amount/30.f;
//		if(GameSession.getInstance().cam.zoom > 0.6f) {
//			GameSession.getInstance().cam.zoom = 0.6f;
//		}
//		
//		if(GameSession.getInstance().cam.zoom < 0.3f) {
//			GameSession.getInstance().cam.zoom = 0.3f;
//		}
//		System.out.println(GameSession.getInstance().cam.zoom);
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		// TODO Auto-generated method stub
		return false;
	}

}
