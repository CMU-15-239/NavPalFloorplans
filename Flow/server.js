var path = require('path');
var express = require('express');
var http = require('http');
var util = require('util');
var exec = require('child_process').exec;
var fs = require('fs');
var child;
var Sector = require('./sector.js');

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
function configureExpress(app) {
    app.configure(function() {

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
    var imagePath = './www/floorplans/floorPlan.jpg';
    var index = base64Data.indexOf('base64,') + 'base64,'.length;
    base64Data = base64Data.substring(index, base64Data.length);
    fs.writeFile(imagePath, new Buffer(base64Data, "base64"));
    var lines = preprocessor(imagePath, res);
});

// creates three text files required by navPal android app
app.post('/text', function (req, res) {
    var map = req.body.map;
    var room = req.body.room;
    //var sector = req.body.sector;
    var id = req.body.id;
    if (map !== undefined && map !== null) {
        fs.writeFile('./www/text/' + id + '_map.txt', map);
    }
    if (room !== undefined && room !== null) {
        fs.writeFile('./www/text/' + id + '_room.txt', room);
    }
    //if (sector !== undefined && sector !== null) {
    //    fs.writeFile('./www/text/' + id + '_sector.txt', sector);
    //}
    return res.send('sucess!'); 
});

// creates json of graph representation of floorplan
app.post('/graph', function (req, res) {
    var graph = req.body.graph;
    var width = req.body.width;
    var height = req.body.height;
    var id = req.body.id;
    if (graph !== undefined && graph !== null) {
		console.log("************************");
		console.log(graph.spaceNodes.length);
        fs.writeFile('./www/text/'+ id + '_graph.txt', JSON.stringify(graph));
		if(width !== undefined && width !== null && height !== undefined && height !== null
			&& graph.spaceNodes !== undefined && graph.spaceNodes !== null) {
			
			var sector = Sector.generateSectorStr(graph.spaceNodes, width, height);
			fs.writeFile('./www/text/' + id + '_sector.txt', sector);
		}
    }
    return res.send('sucess!');
});

// =========== PREPROCESSOR ==========

// calls python script to extract lines from image and convert to gray scale
function preprocessor(imagePath, res) {
    var linesJSON;
    child = exec('python preprocessing.py ' + imagePath,
    function (error, stdout, stderr) {
        // First I want to read the file
        fs.readFile('json.txt', function read(err, data) {
            if (err) {
                throw err;
            }
            linesJSON = data.toString('utf8');
            var lines = JSON.parse(linesJSON);
            lines['image'] = imagePath.substring(5, imagePath.length);
            return res.json(lines);
        });
    });
}

// Launch server
app.listen(8080);