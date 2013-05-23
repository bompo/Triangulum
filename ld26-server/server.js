/**
simple server with lobby (public and private rooms) 
which just forwards packages for the usage
in triangulum
2012, RedLion (Tino Helmig, Stefan Wagner)
**/

var app = require('http').createServer(handler)
 , io = require('socket.io').listen(app);

app.listen(19834);

var rooms = [];
var maxPerRoom = 2;
var debug = false;

//create dummy data
if(debug) {
    var dummyRoom1 = new Room(0,"dummy room 1 ");
    var dummyRoom2 = new Room(1,"dummy room 2 pass: test", "test");
    var dummyClient1 = {};
    dummyClient1.id = 1;
    var dummyPlayer1 = new Player(dummyClient1);
    var dummyClient2 = {};
    dummyClient2.id = 2;
    var dummyPlayer2 = new Player(dummyClient2);
    var dummyClient3 = {};
    dummyClient3.id = 3;
    var dummyPlayer3 = new Player(dummyClient3);
    dummyRoom1.players.push(dummyPlayer1);
    dummyRoom1.players.push(dummyPlayer2);
    dummyRoom2.players.push(dummyPlayer3);
    rooms.push(dummyRoom1);
    rooms.push(dummyRoom2);
}


var replacer = function(key, value) {
    if (key==="currentRoom" || key==="client") {
        if(value.id && value.room) {
            return value.id + " in " + value.room.id;
        } else if(value.id) {
            return value.id;
        } else {
            return "";
        }
    }
    return value;
}

function handler (req, res) {
    if(debug) {
        res.writeHead(200, {"Content-Type": "application/json"});
        res.write(JSON.stringify(rooms, replacer, 2)); 
        res.end();
    }
}
 
io.sockets.on('connection', function(client){

    initClient(client); 
    
    // player not ready message
    client.on('getrooms', function(message){
        getAllRooms(client);      
    });
    
    // create public room
    client.on('createroom', function(message){
        createRoom(client, message.roomname);
    });
    
    // create private room with password
    client.on('createprivateroom', function(message){
        createRoom(client, message.roomname, message.password);
    });
    
    
    // join existing room
    client.on('joinroom', function(message){
        if(!joinRoom(client, message.roomId)) {
            //error notify client
            client.room = undefined;
            client.emit('errorjoiningroom',{ roomId: message.roomId }); 
        }    
    });
    
    // join existing private room
    client.on('joinprivateroom', function(message){
        if(!joinRoom(client, message.roomId, message.password)) {
            //error notify client
            client.room = undefined;
            client.emit('errorjoiningroom',{ roomId: message.roomId }); 
        } 
    
    });
    
    
    // leave current room
    client.on('leaveroom', function(message){
        var n = 0;
        for(var room in rooms ) {
            //remove player from current room
            var i = 0;
            for( var player in rooms[n].players ) { 
                if(rooms[n].players[i].client.id === client.id) {        
                    rooms[n].players.splice(i, 1);
                    break;
                }
                i = i + 1;
            }
            if(rooms[n].players.length === 0) {
                console.log("delete room", rooms[n].id);  
                client.emit('roomdisconnect',{ room: rooms[n].id, name: rooms[n].roomName});                      
                client.broadcast.emit('roomdisconnect',{ room: rooms[n].id, name: rooms[n].roomName});        
                rooms.splice(n, 1);
            }            
            n = n + 1;
        }            
        client.room = undefined;
    });
    
    
    // player ready message
    client.on('ready', function(message){
        if(client.room === undefined) return;
    
        for( var player in client.room.players ) { 
            // set current player to ready
            if(client.room.players[player].client.id === client.id) {
                client.room.players[player].ready = true;
            } else {
                // send everybody else in the room the ready message
                // TODO maybe we don't need this...
                client.room.players[player].client.emit('ready', {player: client.id, message: message});
            }
        }        
        
        // check if all players are ready
        // if so then start a new round
        ready = 0;
        for( var player in client.room.players ) { 
            if(client.room.players[player].ready === true) {
                ready = ready + 1;
            }
        }
        console.log("players ready:", ready, client.room.players.length);
        if(ready === client.room.players.length && ready >= 2 && client.room.gameInProgress === false) {
            // all ready so start a new round
            var cnt = 0
            for( var player in client.room.players ) { 
                client.room.players[player].client.emit('startround',{ player: client.room.players[player].client.id, count: cnt }); 
                cnt++;
            }
            client.room.gameInProgress = true;
        }
    });
    
    // player not ready message
    client.on('notready', function(message){
        for( var player in client.room.players ) { 
            // set current player to ready
            if(client.room.players[player].client.id === client.id) {
                client.room.players[player].ready = false;
            } else {
                // send everybody else in the room the ready message
                // TODO maybe we don't need this...
                client.room.players[player].client.emit('notready', {player: client.id, message: message});
            }
        }        
    });

    // update message
    client.on('update', function(message){
        if(client.room === undefined) return;
    
        // broadcast event to other players in the same room
        for( var player in client.room.players ) { 
            if(client.room.players[player].client.id !== client.id) {
                client.room.players[player].client.emit('update', {player: client.id, message: message});
            }
        }
    });
    
    // update message
    client.on('sendInput', function(message) {
        //client.room.inputList.push({x: message.x, y: message.y});
        //send this input to all players in this room
        for( var player in client.room.players ) { 
            client.room.players[player].client.emit('getInput', {x: message.x, y: message.y});
        }
    });
    
    // synchronize message
    client.on('synchronize', function(message){
        // broadcast event to other players in the same room
        for( var player in client.room.players ) { 
            if(client.room.players[player].client.id !== client.id) {
                client.room.players[player].client.volatile.emit('synchronize', {player: client.id, message: message});
            }
        }
    });
    
    // disconnect message
    client.on('disconnect', function() {
        if(client.room === undefined) {
            return;
        }
       
        // broadcast event to other players in the same room
        for( var player in client.room.players ) { 
            if(client.room.players[player].client.id !== client.id) {
                client.room.players[player].client.emit('playerdisconnect',{ player: client.id });
            }
        }

        console.log("players active", client.room.players.length);    
        //remove player from current room
        var n = 0;
        for( var player in client.room.players ) { 
            if(client.room.players[player].client.id === client.id) {
                console.log("found player", client.id);
                break;
            }
            n = n + 1;
        }
        client.room.players.splice(n, 1);
        console.log("players active", client.room.players.length);
    
        // if room is empty then delete room    
        if(client.room.players.length === 0) {
            var n = 0;
            for(var room in rooms ) {
                if(rooms[n].id === client.room.id) {
                    break;
                }
                n = n + 1;
            }
            if(rooms[n].players.length === 0) {
                console.log("delete room", client.id);
                rooms.splice(n, 1);
            }            
        } else if (client.room.players.length === 1) {
          //check if only one player remains...
          for( var player in client.room.players ) { 
                client.room.players[player].client.emit('endRound', {winner: player.id} );                
            }     
        }
    });
});
 
function Player(client, roomId) {
        this.client = client;
        this.roomId = roomId;
        this.points = 0;
        this.ready = false;
};

function Room(id, roomName, password) {
        this.id = parseInt(id);
        this.roomName = roomName;
        this.password = password;
        this.gameInProgress = false;
        this.currentRound = 1;
        this.players = [];
        
        this.inputList = [];
};

function getAllRooms(client) {
    // send list of all rooms
    for(var room in rooms ) {  
        if(rooms[room].password === undefined) {
            client.emit('roomconnect',{ room: rooms[room].id, name: rooms[room].roomName, hasPass: false, playerCnt: rooms[room].players.length });
        } else {
            client.emit('roomconnect',{ room: rooms[room].id, name: rooms[room].roomName, hasPass: true, playerCnt: rooms[room].players.length });
        }
    }
};

function initClient(client) {
    getAllRooms(client);
    
    // send own id to client
    client.emit('init',{ player: client.id}); 
}

function createRoom(client, roomName, password) {
    // check if room with same name exists
    var postfix = "";
    var postfixN = 0;
    var found = true;
    while(found === true) {
        found = false;
        for(var room in rooms ) {
            //search room
            if(rooms[room].roomName === roomName+postfix) {
                postfixN = postfixN + 1;
                postfix = "_" + postfixN;
                found = true;
            }
        }
    }
    if(postfixN>0) {
        postfix = "_" + postfixN;
    }

    // ok add room
    var room = new Room(client.id, roomName+postfix, password)
    rooms.push(room);
    client.room = room;
    
    // add own id to current players in room
    var player = new Player(client);
    room.players.push(new Player(client));
    
    connectRoom(client, room);
    
    if(debug) console.log("room:",room.id, "players:", room.players.length); 
}

function connectRoom(client, room) {
    var hasPass = false;
    if(room.password !== undefined) {
        hasPass = true;
    }
    client.emit('roomconnect',{ room: room.id, name: room.roomName, hasPass: hasPass, playerCnt: room.players.length });
    client.broadcast.emit('roomconnect',{ room: room.id, name: room.roomName, hasPass: hasPass, playerCnt: room.players.length });
}

function disconnectRoom(client, room) {
    var hasPass = false;
    if(room.password !== undefined) {
        hasPass = true;
    }
    client.emit('roomdisconnect',{ room: room.id, name: room.roomName, hasPass: hasPass, playerCnt: room.players.length });
    client.broadcast.emit('roomdisconnect',{ room: room.id, name: room.roomName, hasPass: hasPass, playerCnt: room.players.length });
}

function refreshRoom(client, room) {
    disconnectRoom(client, room);
    connectRoom(client, room);
}

function joinRoom(client, roomId, password) {
    var joined = false;
    
    roomId = parseInt(roomId);
    
    for(var room in rooms ) {
    
        console.log(rooms[room], rooms[room].players.length < maxPerRoom, rooms[room].id === roomId, rooms[room].id, roomId);
        
        //search room
        if(rooms[room].players.length < maxPerRoom && rooms[room].id === roomId) {
            
            console.log("join", rooms[room]);
        
            if(password === undefined || rooms[room].password === password) {
        
                var player = new Player(client);
                rooms[room].players.push(new Player(client));
                client.room = rooms[room];
                
                //send all players in room that game can be started
                for( var player in rooms[room].players ) { 
                    rooms[room].players[player].client.emit('startgame',{ id: roomId }); 
                }
                
                //update room player number
                refreshRoom(client, rooms[room]);
                
                joined = true;
            }
        }
    }  
    return joined; 
}
