/*  Node.js server for User Authoring tool
    Written by: Daniel Muller and Vansi Vallabhaneni (2013)
*/

// initialize middleware and global variables
var path = require('path');
var express = require('express');
var http = require('http');
var util = require('util');
var exec = require('child_process').exec;
var fs = require('fs');
var child;
var Sector = require('./sector.js');
var app = express();
var port = process.env.PORT || 3000;

/**
 * Summary: Initiazes the express server
 * Parameters: undefined
 * Returns: undefined
**/
function init(){
    configureExpress(app);
    http.createServer(app).listen(port, function() {
        console.log("Express server listening on port %d", port);
    });
}

init();


/**
 * Summary: Setup express with middleware and Access-Control workarounds
 * Parameters: undefined
 * Returns: undefined
**/
function configureExpress(app) {
    app.configure(function() {

    	app.use(express.logger());
    	app.use(express.cookieParser());
    	app.use(express.bodyParser());
    	app.use(express.methodOverride());
    	app.use(express.session({secret:"keyboard cat"}));
    	app.use(express.static(path.join(__dirname, '../www')));

        app.use(function(request, response, next) {
            response.header('Access-Control-Allow-Origin', 'http://localhost:8080');
            response.header('Access-Control-Allow-Methods', 'PUT,GET,POST,DELETE,OPTIONS');
            response.header('Access-Control-Allow-Headers', 'Content-Type,X-Requested-With');

            next();
        });

        app.use(app.router);
        app.use(express.errorHandler({ dumpExceptions: true, showStack: true }));
    });
}

// =========== ROUTES ==========

/**
 * Summary: Upload an image for preprocessing.
 * Parameters:  req.body.image: base64 encoding of
                request.user: passport userId   
 * Returns: list of lines is returned in JSON as well as link to greyscale image
**/
app.post('/upload', function (request, response) {
	var base64Data = req.body.image;
    var imagePath = '../www/floorplans/floorPlan.jpg';
    var index = base64Data.indexOf('base64,') + 'base64,'.length;
    base64Data = base64Data.substring(index, base64Data.length);
    fs.writeFile(imagePath, new Buffer(base64Data, "base64"));
    preprocessor(imagePath, response);
});

// creates three text files required by navPal android app
app.post('/text', function (request, response) {
    var map = request.body.map;
    var room = request.body.room;
    var id = request.body.id;
    if (map !== undefined && map !== null) {
        fs.writeFile('../www/text/' + id + '_map.txt', map);
    }
    if (room !== undefined && room !== null) {
        fs.writeFile('../www/text/' + id + '_room.txt', room);
    }
    if (sector !== undefined && sector !== null) {
        fs.writeFile('./www/text/' + id + '_sector.txt', sector);
    }
    return response.send('sucess!'); 
});

// creates json of graph representation of floorplan
app.post('/graph', function (request, response) {
    var graph = request.body.graph;
    var width = request.body.width;
    var height = request.body.height;
    var id = request.body.id;
    if (graph !== undefined && graph !== null) {
		console.log("************************");
		console.log(graph.spaceNodes.length);
        fs.writeFile('../www/text/'+ id + '_graph.txt', JSON.stringify(graph));
		if(width !== undefined && width !== null && height !== undefined && height !== null
			&& graph.spaceNodes !== undefined && graph.spaceNodes !== null) {
			
			var sector = Sector.generateSectorStr(graph.spaceNodes, width, height);
			fs.writeFile('../www/text/' + id + '_sector.txt', sector);
		}
    }
    return response.send('sucess!');
});

// =========== PREPROCESSOR ==========

/*  TODO:
        temp id for 

*/


/**
 * Summary: calls python script to extract lines from image and convert to gray scale
 * Parameters:  imagePath: path to image to be processsed
                response: responseponse to be sent to user 
 * Returns: response with json of identified lines and src of thresholded image
**/
function preprocessor(imagePath, response) {
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
            // start at character 6 to remove ../www
            lines['image'] = imagePath.substring(6, imagePath.length);
            return response.json(lines);
        });
    });
}

// Launch server
app.listen(8080);