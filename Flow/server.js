var path = require('path');
var express = require('express');
var http = require('http');
var util = require('util');
var exec = require('child_process').exec;
var fs = require('fs');
var child;

// set up express server
var app = express();
var port = process.env.PORT || 3000;

function init(){
    configureExpress(app);
    http.createServer(app).listen(port, function() {
        console.log("Express server listening on port %d", port);
    });
}

init();

// setup express for serving files and Access-Control workarounds
function configureExpress(app){
    app.configure(function(){

    	app.use(express.logger());
    	app.use(express.cookieParser());
    	app.use(express.bodyParser());
    	app.use(express.methodOverride());
    	app.use(express.session({secret:"keyboard cat"}));
    	app.use(express.static(path.join(__dirname, 'www')));

        app.use(function(req, res, next) {
            res.header('Access-Control-Allow-Origin', 'http://localhost:8080');
            res.header('Access-Control-Allow-Methods', 'PUT,GET,POST,DELETE,OPTIONS');
            res.header('Access-Control-Allow-Headers', 'Content-Type,X-Requested-With');

            next();
        });

        app.use(app.router);
        app.use(express.errorHandler({ dumpExceptions: true, showStack: true }));
    });
}

// =========== ROUTES ==========

// upload an image for preprocessing.
// REQUIRES: base64 represenation of floorplan image
// ENSURES: list of lines is returned in JSON as well as link to greyscale image
app.post('/upload', function (req, res) {
	var base64Data = req.body.image;
    var id = req.body.id;
    var imagePath = './public/floorplans/fp' + id + '.jpg';
    var index = base64Data.indexOf('base64,') + 'base64,'.length;
    base64Data = base64Data.substring(index, base64Data.length);
    fs.writeFile(imagePath, new Buffer(base64Data, "base64"));
    var lines = extractLines(imagePath);
    lines['image'] = imagePath;
	return res.json(lines);
});

app.post('/text', function (req, res) {
    var map = req.body.map;
    var room = req.body.rooms;
    var sector = req.body.sector;
    var id = req.body.id;
    if (map !== undefined && map !== null) {
        fs.writeFile('./www/text/map-' + id + '.txt', map);
    }
    if (room !== undefined && room !== null) {
        fs.writeFile('./www/text/rooms-' + id + '.txt', rooms);
    }
    if (sector !== undefined && sector !== null) {
        fs.writeFile('./www/text/sector-' + id + '.txt', sector);
    }
    return res.send('sucess!'); 
});

app.post('/graph', function (req, res) {
    var graph = req.body.graph;
    var id = req.body.id;
<<<<<<< HEAD
    fs.writeFile('./www/text/graph-'+ id + '.txt', JSON.stringify(graph));
=======
    if (graph !== undefined) {
        fs.writeFile('./www/text/graph-'+ id + '.txt', graph);
    }
>>>>>>> ad5a3bc3dc1020f4575c207edd9f99dd22448c02
    return res.send('sucess!');
});

// =========== PREPROCESSOR ==========

function extractLines(imagePath) {
    child = exec('python extractLines.py ' + imagePath,
    function (error, stdout, stderr) {
        var lines = JSON.parse(stdout);
        return lines;
    });
}

// Launch server
app.listen(8080);
