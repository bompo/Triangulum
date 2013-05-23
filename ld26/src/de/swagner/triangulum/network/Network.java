package de.swagner.triangulum.network;

import io.socket.IOAcknowledge;
import io.socket.IOCallback;
import io.socket.SocketIO;
import io.socket.SocketIOException;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;

import org.json.JSONException;
import org.json.JSONObject;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;

import de.swagner.triangulum.GameSession;
import de.swagner.triangulum.Player;

public class Network {

	private SocketIO socket;
	
	private Json json;

	public Array<String> messageList = new Array<String>();
	public Set<Room> rooms;
	
	public Array<UpdatePackage> networkUpdates = new Array<UpdatePackage>();

	public boolean connected = false;
	public float timeToConnect = 5;
	
	public boolean startGame = false;
	public boolean error = false;

	// network vars
	public String id;
	public HashMap<String, Integer> connectedIDs = new HashMap<String, Integer>();
	
	Player.SIDE currentSide = Player.SIDE.LEFT;
	
	static Network instance;

	private Network() {		
		json = new Json();
		rooms = new TreeSet<Room>();
		Collections.synchronizedSet(rooms);
		connectToServer();
	}

	private void connectToServer() {
		
		try {
//			socket = new SocketIO("http://badminton.no-ip.org:19834");
			socket = new SocketIO("http://localhost:19834");
//			socket = new SocketIO("http://superturbobadminton.nodester.com:80");

			socket.connect(new IOCallback() {
				
		        @Override
		        public void onMessage(JSONObject json, IOAcknowledge ack) {
		            try {
		                System.out.println("Server said:" + json.toString(2));
		            } catch (JSONException e) {
		                e.printStackTrace();
		            }
		        }

		        @Override
		        public void onMessage(String data, IOAcknowledge ack) {
		        	json.prettyPrint(data);
		        	String test = new String();
		        	
		        	System.out.println(test);
		            System.out.println("Server said: " + data);
		        }

		        @Override
		        public void onError(SocketIOException socketIOException) {
		            System.out.println("an Error occured");
		            socketIOException.printStackTrace();
		        }

		        @Override
		        public void onDisconnect() {
		        	addMessage("connection terminated");
		            System.out.println("connection terminated.");
		            startGame = false;
		        }

		        @Override
		        public void onConnect() {
		        	connected = true;
		        	addMessage("connected");
		            System.out.println("Connection established");
		        }

		        @Override
		        public void on(String event, IOAcknowledge ack, Object... data) {
		        	System.out.println("Server triggered event '" + event + "'");
		        			        	
		            try {
			        	JSONObject obj  = (JSONObject) data[0];
			        	
		                if (event.equals("init")) {
		                	id = obj.getString("player");
		                	addMessage("player id " + id);
		                	System.out.println("player id " + id);
		                }
		                if (event.equals("roomconnect")) {
		                	rooms.add(new Room(obj.getString("room"),obj.getString("name"),obj.getBoolean("hasPass"),obj.getInt("playerCnt")));
		                	System.out.println("Room " + obj.getString("room") + " added");
				        	addMessage("Room " + obj.getString("room") + " added");
		                }
		                
		                if (event.equals("startgame")) {
		                	System.out.println("startgame");
				        	addMessage("startgame");
				        	startGame = true;
		                }
		                
		                if (event.equals("errorjoiningroom")) {
		                	String id = obj.getString("roomId");
		                	System.out.println("Room " + id + " full, removed or password wrong");
				        	addMessage("Room " + id + " full, removed or password wrong");      
				        	error = true;
		                }
		                
		                if (event.equals("roomdisconnect")) {
		                	String id = obj.getString("room");
		                	Room roomToRemove = null;
		                	for(Room room:rooms) {
		                		if(room.id.equals(id)) {
		                			roomToRemove = room;
		                			break;
		                		}
		                	}
		                	if(roomToRemove!=null) {
		                		rooms.remove(roomToRemove);
			                	System.out.println("Room " + obj.getString("room") + " removed");
					        	addMessage("Room " + obj.getString("room") + " removed");
		                	}
		                }
		                
		                if (event.equals("playerconnect")) {
		                	connectedIDs.put(obj.getString("player"), obj.getInt("count"));
		                	System.out.println("Player " + obj.getString("player") + ", " + obj.getInt("count") + " connected");

				        	addMessage("player " + obj.getString("player") + ", " + obj.getInt("count") + " connected");
				        	if(connectedIDs.keySet().size() == 1) {
				        		System.out.println("reinit");
				        	}
		                }
		                if (event.equals("ready")) {
		                	System.out.println("Player " + obj.getString("player") + " ready");

				        	addMessage("player " + obj.getString("player") + " ready");
		                }
		                if (event.equals("notready")) {
		                	System.out.println("Player " + obj.getString("player") + " not ready");

				        	addMessage("player " + obj.getString("player") + " not ready");
		                }
		                if (event.equals("death")) {
		                	System.out.println("Player " + obj.getString("player") + " death");

				        	addMessage("player " + obj.getString("player") + " death");
		                }
		                if (event.equals("playerdisconnect")) {
		                	connectedIDs.remove(obj.getString("player"));
		                	connectedIDs.clear();
		                	System.out.println("Player " + obj.getString("player") + " disconnected");
		                	addMessage("player " + obj.getString("player") + " disconnected");
		                }
		                
		                if (event.equals("getInput")) {		             
                			System.out.println("getInput");	
                			networkUpdates.add(new UpdatePackage(new Vector2((float) obj.getDouble("x"),(float) obj.getDouble("y"))));		                	
		                }
		                
		                if (event.equals("startround")) {		                	
		                	if( obj.getInt("count") == 0) {
		                		GameSession.getInstance().player.side = Player.SIDE.LEFT; 
		                		GameSession.getInstance().opponent.side = Player.SIDE.RIGHT;
		                	} else {
		                		GameSession.getInstance().player.side = Player.SIDE.RIGHT; 
		                		GameSession.getInstance().opponent.side = Player.SIDE.LEFT;
		                	}
		                }
		            } catch (Exception ex) {
		                ex.printStackTrace();
		            }
		        	

		        }
		    });

		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	    
	}
	
	public void update() {
		//TODO send input positions here
	}

	public void sendMessage(String message) {
		// This line is cached until the connection is established.
		socket.send(message);
	}
	
	public void sendCreateRoom(String roomName) {
		error = false;
		System.out.println("create room " + roomName);

		JSONObject json = new JSONObject();
		try {
			json.putOpt("player", id);
			json.putOpt("roomname", roomName);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		socket.emit("createroom", json);
	}
	
	public void sendCreatePrivateRoom(String roomName, String password) {
		error = false;
		System.out.println("create private room " + roomName + " (" + password + ")");

		JSONObject json = new JSONObject();
		try {
			json.putOpt("player", id);
			json.putOpt("roomname", roomName);
			json.putOpt("password", password);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		socket.emit("createprivateroom", json);
	}
	
	public void sendJoinRoom(String roomId) {
		error = false;
		System.out.println("join room " + roomId);

		JSONObject json = new JSONObject();
		try {
			json.putOpt("player", id);
			json.putOpt("roomId", roomId);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		socket.emit("joinroom", json);
	}
	
	public void sendJoinPrivateRoom(String roomId, String password) {
		error = false;
		System.out.println("join room " + roomId + " with pass:" + password);

		JSONObject json = new JSONObject();
		try {
			json.putOpt("player", id);
			json.putOpt("roomId", roomId);
			json.putOpt("password", password);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		socket.emit("joinprivateroom", json);
	}
	
	
	public void sendLeaveRoom() {
		System.out.println("leave current room ");

		JSONObject json = new JSONObject();
		try {
			json.putOpt("player", id);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		socket.emit("leaveroom", json);
	}

	public void sendReady() {
		System.out.println("send ready");

		JSONObject json = new JSONObject();
		try {
			json.putOpt("player", id);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		socket.emit("ready", json);
	}

	public void sendNotReady() {
		System.out.println("send not ready");

		JSONObject json = new JSONObject();
		try {
			json.putOpt("player", id);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		socket.emit("notready", json);
	}

	public void sendInput(Vector2 input) {
		System.out.println("send sentInput");

		JSONObject json = new JSONObject();
		try {
			json.putOpt("x", input.x);
			json.putOpt("y", input.y);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		socket.emit("sendInput", json);
	}
	
//	public void getInput() {
//		System.out.println("send getInput");
//
//		JSONObject json = new JSONObject();
//		
//		try {
//			json.putOpt("dummy", "dummy");
//		} catch (JSONException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		socket.emit("getInputFromPlayers", json);
//	}

	public static Network getInstance() {
		if (instance != null)
			return instance;
		instance = new Network();
		return instance;
	}

	public void addMessage(String m) {
		if (messageList.size > 5) {
			messageList.removeIndex(0);
		}
		messageList.add(m);
	}
	
	public void sortRooms() {
//		ArrayList<Room> temp = new ArrayList<Room>();
//		try {
//		temp.add(rooms2.first());
//		rooms2.remove(rooms2.first());
//		for(Room r : temp) {
//			
//			if(r.compareTo(temp.get(0)) < 0){
//				if(!r.equals(temp.get(0)))
//					temp.add(0, r);
//			}
//			if(r.compareTo(temp.get(0)) >= 0){
//				if(!r.equals(temp.get(0)))
//					temp.add(r);
//			}
//			
//		}
//		} catch(Exception e) {
//			java.util.Collections.so
//		}
//		
//		sortRooms(rooms2);
//		
//		rooms.clear();
//		rooms.addAll(temp);
		
		ArrayList<Room> chambers = new ArrayList<Room>();
		
		for(Room r : rooms) {
			chambers.add(r);
		}
		
		java.util.Collections.sort(chambers);
		
		
		
		rooms.clear();
		
		
		for(Room ro : chambers) {
			rooms.add(ro);
		}
	}


}
