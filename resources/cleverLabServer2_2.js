var express = require('express');
var app = require('express')();
var server = require('http').Server(app);
var io = require('socket.io')(server);
var readlineSync = require('readline-sync');
var SerialPort= require('serial-node'), serial = new SerialPort(); //only Windows
var md5 = require('md5');
var bodyParser = require('body-parser');
var MongoClient = require('mongodb').MongoClient;
var url = "mongodb://localhost:27017/";
var listener;
var mySocket;
var list=serial.list(); // list of com ports
console.log('ports num=',list.length);

if (list.length == 0 || list[0]==0) console.log("No serial port available!");
else {
	for(i=0;i<list.length;i++) 
	{
		console.log(list[i]); 
	}
	var myPort = readlineSync.question("What ComPort to use? ");
	
	function checkPort(port){ //function for list.find
		return port === myPort; 
	} 
	
	while (!list.find(checkPort)) {  //while the selected com port is not in list of available ports
		console.log("Wrong Port!!!");
		myPort = readlineSync.question("What ComPort to use? ");  //re-enter com port
	}
	console.log("myPort = ",myPort);
	
}


var curOS = process.platform;
console.log("OS is ",curOS);
app.use('/assets', express.static('assets'));
app.use(express.static(__dirname + '/FromRoboPortal'));
// app.get('/jquery.js', function(res, req){
// res.sendFile(__dirname + '/jquery.js');
// });
var clientsCount = 0;
app.get('/', function(req, res){
	res.sendFile(__dirname + '/FromRoboPortal/index.html');
});

var urlencodedParser = bodyParser.urlencoded({ extended: false });

var adminConnected = false;
var myResult;
var myDevices;
var userConnected = false;

listener = io.listen(server);
listener.sockets.on('connection', function(socket){
	mySocket = socket;
	if (adminConnected) {
		socket.emit('users', myResult);
		socket.emit('devices', myDevices);
	}
	if (userConnected){
		socket.emit('devicesForUser', myDevices);
	}
	socket.on('hello', function(data){
		console.log(data);
	});
	socket.on('deviceActive', data=>{
		console.log('deviceName = ',data.deviceName,' deviceActive=',data.deviceActive);
		setDeviceActive(data.deviceName, data.deviceActive);
	});
});


function getAllUsers(dbo){
	dbo.collection("user").find().toArray(function(err, result) {
		if (err) throw err;
		console.log(result);
		adminConnected = true;
		myResult = result;
	});
}

function getAllDevices(dbo){
	dbo.collection("device").find().toArray(function(err, result) {
		if (err) throw err;
		console.log(result);
		myDevices = result;
	});
}

function setAdminOnline(user, online, res1){ 
	console.log("User ",user," online is ",online);
	MongoClient.connect(url, function(err, db) {
		if (err) throw err;
		if (curOS === "linux") var dbo = db.db("local"); //linux
		else var dbo = db.db("DB"); //windows
		var myquery = { login: user };
		var newvalues = { $set: {online: online } };
		dbo.collection("user").updateOne(myquery, newvalues, function(err, res) {
			if (err) throw err;
			console.log("1 document updated");
			if (online === true) {
				console.log("!!Pass correct!!");
				getAllUsers(dbo);
				getAllDevices(dbo);
				res1.sendFile(__dirname + '/FromRoboPortal/admin.html');
			}
			//db.close();
		});
	});
}

function setUserOnline(user, online, res1){
	console.log("User ",user," online is ",online);
	MongoClient.connect(url, function(err, db) {
		if (err) throw err;
		if (curOS === "linux") var dbo = db.db("local"); //linux
		else var dbo = db.db("DB"); //windows
		var myquery = { login: user };
		var newvalues = { $set: {online: online } };
		dbo.collection("user").updateOne(myquery, newvalues, function(err, res) {
			if (err) throw err;
			console.log("1 document updated");
			if (online === true) {
				getAllDevices(dbo);
				userConnected = true;
				mySocket.emit('devices', myDevices);
				res1.sendFile(__dirname + '/FromRoboPortal/userPage.html');
			}	
		});
	});	
}

app.get('/Logout', function(req, res){
	//	res.sendFile(__dirname + '/FromRoboPortal/index.html');
	//if (!req.body) return res.sendStatus(400)
	//res.send('welcome, ' + req.body.username)
	console.log("Login from logout: ", req.query.login);
	res.sendFile(__dirname + '/FromRoboPortal/index.html');
	setAdminOnline(req.query.login, false, res); //add if for admin and user logout separately
	//console.log("Pass", req.body.password);
});

app.post('/Login', urlencodedParser, function(req, res){
	//	res.sendFile(__dirname + '/FromRoboPortal/index.html');
	if (!req.body) return res.sendStatus(400)
	//res.send('welcome, ' + req.body.username)
	console.log("Login: ", req.body.login);
	//console.log("Pass", req.body.password);
	MongoClient.connect(url, function(err, db) {
		if (err) throw err;
		//	var dbo = db.db("DB"); //windows
		if (curOS === "linux") var dbo = db.db("local"); //linux
		else var dbo = db.db("DB"); //windows
		//console.log("DB = ", dbo1);
		var query = { login: req.body.login };
		dbo.collection("user").find(query).toArray(function(err, result) {
			if (err) throw err;
			//console.log("from db = ",result);
			if (result[0].pass===md5(req.body.password)) {
				if (result[0].login==="Admin") setAdminOnline(req.body.login, true, res);
				else {
					console.log('Simple user');
					//res.send('<p style="color:green; font-size:50px;" >Hi user </p>');
					setUserOnline(req.body.login, true, res);
				}
				//res.send('welcome, ' + result[0].fio);
				// dbo.collection("user").find().toArray(function(err, result) {
				// if (err) throw err;
				// console.log(result);
				// adminConnected = true;
				// myResult = result;
				//req.root = result;
			//});
		}
		else {
			console.log("!!Pass incorrect!!");
			res.send('<p style="color:red; font-size:50px;" >Pass error</p>');
		}
	}); //C:\Program Files\MongoDB\Server\3.6\bin>mongod.exe
});

});

//AddUser
app.post('/AddUser', urlencodedParser, function(req, res){
	if (!req.body) return res.sendStatus(400)
	console.log("From form: ")
	console.log(req.body.FIO, " ", req.body.email, " ", req.body.login, " ", req.body.password);
	var pass = md5(req.body.password);
	MongoClient.connect(url, function(err, db) {
		if (err) throw err;
		if (curOS === "linux") var dbo = db.db("local"); //linux
		else var dbo = db.db("DB"); //windows
		var myobj = { fio: req.body.FIO, email: req.body.email, login: req.body.login, pass: pass, status: "User", online: false, devices: []};
		dbo.collection("user").insertOne(myobj, function(err, res2) {
			if (err) throw err;
			console.log("1 document inserted");
			getAllUsers(dbo);
			mySocket.emit('addUser', myobj);
			//res.setHeader("Location", "admin.html");
			//res.end();
			//res.sendFile(__dirname + '/FromRoboPortal/admin.html');
			//db.close();
		});
	});
	res.sendFile(__dirname + '/FromRoboPortal/admin.html');
	
});

function setDeviceActive(deviceName, deviceActive){
	MongoClient.connect(url, function(err, db) {
		if (err) throw err;
		if (curOS === "linux") var dbo = db.db("local"); //linux
		else var dbo = db.db("DB"); //windows
		var myquery = { name: deviceName };
		var newvalues = { $set: {active: deviceActive } };
		dbo.collection("device").updateOne(myquery, newvalues, function(err, res) {
			if (err) throw err;
			console.log("1 document updated, res = ", res);
			//db.close();
		});
	});
	
}

var matrixArray = [ "000000","000000","000000","000000","000000","000000","000000","000000", 
	"000000","000000","000000","000000","000000","000000","000000","000000",
	"000000","000000","000000","000000","000000","000000","000000","000000",
	"000000","000000","000000","000000","000000","000000","000000","000000",
	"000000","000000","000000","000000","000000","000000","000000","000000",
	"000000","000000","000000","000000","000000","000000","000000","000000",
	"000000","000000","000000","000000","000000","000000","000000","000000",
	"000000","000000","000000","000000","000000","000000","000000","000000",
];
var cross = 'x';


var listener2 = io.listen(server);
listener2.sockets.on('connection', function(socket){
//for servo1
	socket.emit('servo1Hello', {'hello': 'hello, servo1'});
	socket.on('servo1Active', data=>{
		console.log('from Servo1=',data.Active);
	});
	socket.on('servo1Angle', data=>{
		console.log('Servo1 angle =',data);
		serial.use(myPort);
		serial.open();
		var data1 = "{\"angle1\":" +data.angle1+"}";
		// console.log('data1 length =',data1.length);
		for (var i = 0; i < data1.length; i++) { //in cycle send all data to Arduino by 1 char
			// console.log("sent to Arduino: ", data1.charAt(i));
			serial.write(data1.charAt(i));
		}
		serial.close();

	});
//for motor1
	socket.emit('motor1Hello', {'hello': 'hello, motor1'});
	socket.on('motor1Active', data=>{
		console.log('from motor1=',data.Active);
	});
	socket.on('motor1', data=>{
		console.log('motor1 =',data);
		serial.use(myPort);
		serial.open();
		// var data1 = "{\"motor1\":" +data.motor1+"}";
		var data1 = "{\"motor1\":" +data.motor1+", \"motor1Slider\":" +data.motor1Slider+"}";
		for (var i = 0; i < data1.length; i++) { //in cycle send all data to Arduino by 1 char
			serial.write(data1.charAt(i));
		}
		serial.close();
	});	
	socket.on('motor1Slider', data=>{
		console.log('motor1Slider =',data);
		serial.use(myPort);
		serial.open();
		var data1 = "{\"motor1Slider\":" +data.motor1Slider+"}";

		for (var i = 0; i < data1.length; i++) { //in cycle send all data to Arduino by 1 char
			serial.write(data1.charAt(i));
		}
		serial.close();
	});	


//for RGB_Matrix_1
	socket.on('clientNumber', function(data){ //recieve client number
		clientsCount++; // increase clientsCount
		console.log('clients=',clientsCount);
		socket.emit('cross', {'cross': cross});
		socket.emit('matrixArray',{'matrixArray' : matrixArray}); //send matrix
	});
	console.log('client connected!',clientsCount);
	socket.emit('clientNumber',{'clientNumber':clientsCount}); //send current number to the client
	socket.on('cross', function(data){  //if cross was pressed
		serial.use(myPort);
		serial.open();
		serial.write(data);
		console.log('showCross = ', data.cross);
		serial.close();
		socket.broadcast.emit('cross',data); // send cross to all clients
		cross = data.cross;
		if (cross === 'X'){
			for (var i = 0; i < 64; i += 9) {
				//strip.setPixelColor(i, strip.Color(r, g, b));
				matrixArray[i] = "ff0000";
			}
			for (var i = 7; i < 64; i += 7) {
				matrixArray[i] = "ff0000";
			}
		}
		else {
			for (var i = 0; i < 64; i += 9) {
				//strip.setPixelColor(i, strip.Color(r, g, b));
				matrixArray[i] = "000000";
			}
			for (var i = 7; i < 64; i += 7) {
				matrixArray[i] = "000000";
			}
		}
	});
	socket.on('pageRefresh', function(){ //if client reload page
		socket.emit('cross', {'cross': cross});
		socket.emit('matrixArray',{'matrixArray' : matrixArray}); //send matrix
		console.log("Page refresh!!");
	});
	socket.on('ledDot', function(data){ //receive single dot
		//		console.log(data.ledDot);
		console.log(data);
		matrixArray[data.cellNumber] = data.cellColor; //change dot in matrix
		//broadcast received led to all client sockets		
		socket.broadcast.emit('oneLed',{cellNumber:data.cellNumber, cellColor: data.cellColor});
		var red = data.cellColor[0] + data.cellColor[1];
		var green = data.cellColor[2] + data.cellColor[3];
		var blue = data.cellColor[4] + data.cellColor[5];
		// console.log(red," ", green," ", blue);
		// var row = parseInt(data.cellNumber / 8);
		// var col= data.cellNumber % 8;
		var red10 = parseInt(red, 16);
		var green10 = parseInt(green, 16);
		var blue10 = parseInt(blue, 16);
		// var toArduino = "L"+row+"/"+col+"/"+red10+"/"+green10+"/"+blue10+";";
		// console.log(row, col, red10, green10, blue10);
		// console.log(toArduino);
		serial.use(myPort);
		serial.open();
		// var data1 = "{\"cellNumber\":\"" +data.cellNumber+"\",\"red\":\""+red10+"\",\"green\":\""+green10+"\",\"blue\":\""+blue10+"\"}";
		var data1 = "{\"cellNumber\":" +data.cellNumber+",\"red\":"+red10+",\"green\":"+green10+",\"blue\":"+blue10+"}";
		console.log("data1: ", data1);
		for (var i = 0; i < data1.length; i++) { //in cycle send all data to Arduino by 1 char
			// console.log("sent to Arduino: ", data1.charAt(i));
			serial.write(data1.charAt(i));
		}
		serial.close();
	});
	}); 			

server.listen(8000, function(){
	console.log('listening on *:8000');
	// serial.use(myPort);
	// serial.open();
	// serial.write('0');
	// serial.close();
	
});
