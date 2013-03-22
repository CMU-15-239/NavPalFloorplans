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
var mongoose = require('mongoose');
var passport = require('passport');
var LocalStrategy = require('passport-local').Strategy;

var Util = require('./util.js');
var FlowDB = require('./controllers/flowDB.js');

var Sector = require('./sector.js');


// set up express server
var app = express();
var child;
var flowDB;

/**
 * Summary: Initiazes the express server
 * Parameters: undefined
 * Returns: undefined
**/
function init(){
    configureExpress(app);
    
    flowDB = new FlowDB('mongodb://test:test@69.195.199.181:27017/flow'); //change this
    //flowDB.clearData();
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
        
        app.use(passport.initialize());
        app.use(passport.session());
        
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
//errorCode: success 0, preprocessingFailed 1, unauthorized 401
app.post('/preprocess', function (request, response) {
   flowDB.getUserById(request.session.userId, function(user) {
      if(Util.exists(user)) {
         var base64Data = req.body.image;
         var imagePath = '../www/floorplans/floorPlan.jpg';
         var index = base64Data.indexOf('base64,') + 'base64,'.length;
         base64Data = base64Data.substring(index, base64Data.length);
         fs.writeFile(imagePath, new Buffer(base64Data, "base64"));
         preprocessor(imagePath, function(lines) {
            if(Util.exists(lines)) {
               lines.errorCode = 0;
               response.send(lines);
            } else {
               response.send({errorCode: 1});
            }
         });
      } else {
         response.status(401);
         return response.send();
      }
   });
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

//errorCodes: success 0, fail 1
app.post('/login', passport.authenticate('local'), function(request, response) { //success case
   request.user.lastLoginTimestamp = new Date();
   request.session.userId = request.user._id;
   request.user.save(function(err) {
      var responseData = {
         buildings: request.user.getBuildingRefs(),
         errorCode: 0
      };
      if(err) {responseData.errorCode = 1;}
      return response.send(responseData);
   });
});

app.post('/logout', function(request, response) {
   request.logout();
   request.session.userId = null;
   return response.redirect('/home.html');
});

//errorCodes: success 0, invalid data 1, user already exists 2, failed to auto login 3
app.post('/register', function(request, response) {
   var responseData = {error: 1};
   if(Util.exists(request) && Util.isValidUsername(request.body.username) 
      && Util.isValidPassword(request.body.password)) {
     
      flowDB.register(request.body.username, request.body.password, function(newUser) {
         if(Util.exists(newUser)) {
            responseData.error = 0;
            request.login(newUser, function(err) {
               if (err) {
                  console.log("server.js 134");
                  responseData.error = 3;
               }
               request.session.userId = newUser._id;
               return response.send(responseData);
            });
         } else {
            responseData.error = 2;
            return response.send(responseData);
         }
     });
   } else {
     return response.send(responseData);
   }
});

// success 0, failedToSave 1, failedToExport 2, unauthorized 401
app.post('/saveExport', function(request, response) { //success case
   flowDB.getUserById(request.session.userId, function(user) {
      var responseData = {errorCode: 1, buildingId: null};
      if(Util.exists(user)) {
         user.saveBuilding(request.body.building, function(buildingObj) {
               if(Util.exists(buildingObj)) {
                  if(Util.exists(request.body.exportData) && request.body.exportData) {
                     console.log("----EXPORT----");
                  }
                  responseData.errorCode = 0;
                  responseData.buildingId = buildingObj.userBuildingId;
               }
               
               return response.send(responseData);
         });
      } else {
         response.status(401);
         return response.send();
      }
   });
});

//errorCodes: success 0, invalid data 1, failed to change password 2, unauthorized 401
app.post('/changePassword', function(request, response) {
   flowDB.getUserById(request.session.userId, function(user) {
      if(Util.exists(user)) {
         console.log("---user exists");
         if(Util.isValidPassword(request.body.newPassword)) {
            console.log("---valid new password");
            user.changePassword(request.body.newPassword, function(user) {
               if(Util.exists(user)) {
                  return response.send({errorCode: 0});
               } else {
                  return response.send({errorCode: 2});
               }
            });
         } else {
            return response.send({errorCode: 1});
         }
      } else {
         console.log("---unable to find user");
         response.status(401);
         return response.send();
      }
   });
});

//erroCode: success 0, invalid data 1, buildingNotFound 404, unauthorized 401
app.post('/getBuilding', function(request, response) {
   flowDB.getUserById(request.session.userId, function(user) {
      if(Util.exists(user)) {
         if(Util.exists(request.body.buildingId)) {
            user.getBuilding(request.body.buildingId, function(buildingObj) {
               if(Util.exists(buildingObj)) {
                  return response.send({building: buildingObj.toOutput(), errorCode: 0});
               } else {
                  response.status(404);
                  return response.send();
               }
            });
         } else {
            return response.send({errorCode: 1});
         }
      } else {
         response.status(401);
         return response.send();
      }
   });
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
function preprocessor(imagePath, callback) {
    var linesJSON;
    child = exec('python preprocessing.py ' + imagePath, 
        function (error, stdout, stderr) {
        // First I want to read the file
        fs.readFile('json.txt', function read(err, data) {
            if (err) {
               console.log("failed to preprocess: "+err);
               if(Util.exists(callback)) {return callback(null)}
            }
            linesJSON = data.toString('utf8');
            var lines = JSON.parse(linesJSON);
            // start at character 9 to remove ../../www
            lines['image'] = imagePath.substring(9, imagePath.length);
            if(Util.exists(callback)) {return callback(lines)}
        });
    });
}

// Launch server
app.listen(8080);
console.log("Express server listening on port 8080");