# ChatApp_NodeJS_Socket.io
var express  = require("express");
var app  = express();
var fs = require("fs");
var server = require("http").createServer(app);
var io =  require("socket.io").listen(server);
server.listen(process.env.PORT || 3000);
console.log("connect successful !");
io.sockets.on('connection', function(socket){
	console.log("co thiet bi vua ket noi");
	socket.on("receiveDataFromServer", function(data){
		console.log("du lieu nhan duoc "+data);
	});
	var array=[];
	array.push("sdf");

	io.sockets.emit("emitDataFromServer",{thongtin:"fsdfdsf"}});
});
