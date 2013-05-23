package de.swagner.triangulum;

import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import de.swagner.triangulum.units.Base;
import de.swagner.triangulum.units.Bullet;
import de.swagner.triangulum.units.Fighter;
import de.swagner.triangulum.units.Ship;
import de.swagner.triangulum.units.Tower;

public class GameSession {
	
	public static GameSession instance;
	
	public Player player = new Player(Player.SIDE.LEFT);
	public Opponent opponent = new Opponent(Player.SIDE.RIGHT);
	
	public Sound effect2;
	public Sound missileSound;
	
	public OrthographicCamera cam;
	
	public Grid grid = new Grid();
	public Array<Ship> ships = new Array<Ship>();
	public Array<Ship> bases = new Array<Ship>();
	public Array<Ship> towers = new Array<Ship>();
	
	public Array<Bullet> bullets = new Array<Bullet>();
	
	public Random randomizer;

	public static GameSession getInstance() {
		if (instance == null) {
			instance = new GameSession();
		}
		return instance;
	}
	
	public void newSinglePlayerGame(long seed) {
		grid = new Grid();
		
		randomizer = new Random(seed);
		
		cam = new OrthographicCamera(800, 480);
		cam.position.set(800 * 0.5f, -460 * 0.5f, 0);  
		cam.zoom = .95f;
		
		effect2 = Gdx.audio.newSound(Gdx.files.internal("data/effect1.ogg"));
		missileSound = Gdx.audio.newSound(Gdx.files.internal("data/missile.ogg"));
		
		ships.clear();
		bases.clear();
		towers.clear();
		bullets.clear();
		
		player.hitPoints = 100;
		player.energy = 50;
		
		opponent.hitPoints = 100;
		opponent.energy = 50;
		
		// 10 bases on players side
		for (int i = 0; i < 10; i++) {
			bases.add(new Base(1, new Vector2(5, 5 + (i * 4))));
		}
		// and 10 on enemies side
		for (int i = 0; i < 10; i++) {
			bases.add(new Base(2, new Vector2(150, 5 + (i * 4))));
		}

	}
	
	public void addPlayerShip(Vector2 pos) {
		
		if(player.side == Player.SIDE.LEFT) {
			if(pos.x < 13) {
				if(player.energy >= 5) {
					
					long id = effect2.play();
					effect2.setPan(id, 1 - ((pos.x/80.f)*2.f), 1);
					effect2.setPitch(id, 0.5f + pos.y/48.f);
					
					player.energy -= 5;
					ships.add(new Fighter(1, pos, new Vector2(0, 1)));
				}
			} else if(pos.x > 13 && pos.x < 80) {
				if(player.energy >= 20) {
					
					long id = effect2.play();
					effect2.setPan(id, 1 - ((pos.x/80.f)*2.f), 1);
					effect2.setPitch(id, 0.5f + pos.y/48.f);
					
					player.energy -= 20;
					towers.add(new Tower(1, pos, new Vector2(0, 1)));
				}
			}
		} else {
			if(pos.x > 115) {
				if(player.energy >= 5) {

					long id = effect2.play();
					effect2.setPan(id, 1 - ((pos.x/80.f)*2.f), 1);
					effect2.setPitch(id, 0.5f + pos.y/48.f);
					
					player.energy -= 5;
					ships.add(new Fighter(1, pos, new Vector2(0, -1)));
				}
			} else if(pos.x > 80 && pos.x < 115) {
				if(player.energy >= 20) {
					
					long id = effect2.play();
					effect2.setPan(id, 1 - ((pos.x/80.f)*2.f), 1);
					effect2.setPitch(id, 0.5f + pos.y/48.f);
					
					player.energy -= 20;
					towers.add(new Tower(1, pos, new Vector2(0, -1)));
				}
			}
		}
	}
	
	public void addEnemyShip(Vector2 pos) {
		if(opponent.side == Player.SIDE.LEFT) {
			if(pos.x < 13) {
				if(opponent.energy >= 5) {
					
					long id = effect2.play();
					effect2.setPan(id, 1 - ((pos.x/80.f)*2.f), 1);
					effect2.setPitch(id, 0.5f + pos.y/48.f);
					
					opponent.energy -= 5;
					ships.add(new Fighter(2, pos, new Vector2(0, 1)));
				}
			} else if(pos.x > 13 && pos.x < 80) {
				if(opponent.energy >= 20) {
					
					long id = effect2.play();
					effect2.setPan(id, 1 - ((pos.x/80.f)*2.f), 1);
					effect2.setPitch(id, 0.5f + pos.y/48.f);
					
					opponent.energy -= 20;
					towers.add(new Tower(2, pos, new Vector2(0, 1)));
				}
			}
		} else {
			if(pos.x > 115) {
				if(opponent.energy >= 5) {

					long id = effect2.play();
					effect2.setPan(id, 1 - ((pos.x/80.f)*2.f), 1);
					effect2.setPitch(id, 0.5f + pos.y/48.f);
					
					opponent.energy -= 5;
					ships.add(new Fighter(2, pos, new Vector2(0, -1)));
				}
			} else if(pos.x > 80 && pos.x < 115) {
				if(opponent.energy >= 20) {
					
					long id = effect2.play();
					effect2.setPan(id, 1 - ((pos.x/80.f)*2.f), 1);
					effect2.setPitch(id, 0.5f + pos.y/48.f);
					
					opponent.energy -= 20;
					towers.add(new Tower(2, pos, new Vector2(0, -1)));
				}
			}
		}
	}
	
}
