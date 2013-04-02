/**
 * Node.js server for User Authoring tool
 * Written by: Daniel Muller and Vansi Vallabhaneni (2013)
 * Express Docs: expressjs.com
 * Passport Docs: passportjs.org
 * Mongoose (MongoDB middleware for node) Docs: mongoosejs.com
 * Some good example code (from 15-237 Fall 12): 
      kosbie.net/cmu/fall-12/15-237/handouts/notes-server-side-part1.html
      kosbie.net/cmu/fall-12/15-237/handouts/notes-server-side-part2.html
*/

// Initialize middleware and global variables.
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


// Set up express server.
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
        
    	app.use(express.session({userId: "", secret:"keyboard cat"}));
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

//-------------------------------
// User Account Management Routes
//-------------------------------

/**
 * Summary: Route to login an existing user.
 * request: {username : String, password : String}
 * response: {buildings : [{buildingId : String, buildingName : String}]}
 * httpCode: success 200, unauthorized 401
**/
app.post('/login', passport.authenticate('local'), function(request, response) {
   request.user.lastLoginTimestamp = new Date();
   
   // userId is used to reference this user from future requests
   request.session.userId = request.user._id;
   request.user.save(function(err) {
      response.status(200);
      
      return response.send({
         buildings: request.user.getBuildingRefs()
      });
   });
});

/**
 * Summary: Route to logout the current user.
 * request: undefined
 * response: redirects to home.html
 * errorCode: undefined
 * httpCode: undefined
**/
app.post('/logout', function(request, response) {
   request.logout();
   request.session.userId = null;
   return response.redirect('/home.html');
});

/**
 * Summary: Route to register a new user.
 * request: {username : String, password : String}
 * response: {errorCode : Number}
 * errorCode: success 0, invalid data 1, user already exists 2, failed to auto login 3
 * httpCode: success 200, invalid data 400, user already exists 200, failed to auto login 200
**/
app.post('/register', function(request, response) {
   var responseData = {errorCode: 1};
   if(Util.exists(request) && Util.isValidUsername(request.body.username) 
      && Util.isValidPassword(request.body.password)) {
     
      flowDB.register(request.body.username, request.body.password, function(newUser) {
         if(Util.exists(newUser)) {
            responseData.errorCode = 0;
            response.status(200);
            request.login(newUser, function(err) {
               if (err) {
                  console.log("server.js 134");
                  responseData.errorCode = 3;
               }
               request.session.userId = newUser._id;
               return response.send(responseData);
            });
         } else {
            responseData.errorCode = 2;
            return response.send(responseData);
         }
     });
   } else {
     response.status(400);
     return response.send(responseData);
   }
});

//-------------------
// Application Routes
//-------------------

/**
 * Summary: Route to change the current user's password.
 * request: {newPassword : String}
 * response: {errorCode : Number}
 * errorCode: success 0, invalid data 1, failed to change password 2, unauthorized 401
 * httpCode: success 200, invalid data 400, failed to change password 500, unauthorized 401
**/
app.post('/changePassword', function(request, response) {
   flowDB.getUserById(request.session.userId, function(user) {
      if(Util.exists(user)) {
         console.log("---user exists");
         if(Util.isValidPassword(request.body.newPassword)) {
            console.log("---valid new password");
            user.changePassword(request.body.newPassword, function(user) {
               if(Util.exists(user)) {
                  response.status(200);
                  return response.send({errorCode: 0});
               } else {
                  response.status(500);
                  return response.send({errorCode: 2});
               }
            });
         } else {
            response.status(400);
            return response.send({errorCode: 1});
         }
      } else {
         console.log("---unable to find user");
         response.status(401);
         return response.send({errorCode: 401});
      }
   });
});


/**
 * Summary: Upload an image for preprocessing.
 * Parameters:  req.body.image: base64 encoding of
                request.user: passport userId   
 * Returns: list of lines is returned in JSON as well as link to greyscale image
**/
/**
 * Summary: Route to preprocess an image.
 * request: {image : String}
 * response: {errorCode : Number, lines : [], ????}
 * errorCode: success 0, preprocessing failed 1, unauthorized 401
 * httpCode: success 200, preprocessing failed 500, unauthorized 401
**/
app.post('/preprocess', function (request, response) {
   flowDB.getUserById(request.session.userId, function(user) {
      if(Util.exists(user)) {
         var base64Data = req.body.image;
         //var imagePath = '../www/floorplans/floorPlan.jpg';
         
         var imageDir = '/temp/images/';
         var imagePath = imageDir + 'image1.jpg';
         
         var index = base64Data.indexOf('base64,') + 'base64,'.length;
         base64Data = base64Data.substring(index, base64Data.length);
         fs.writeFile(imagePath, new Buffer(base64Data, "base64"));
         preprocessor(imagePath, function(lines) {
            if(Util.exists(lines)) {
               lines.errorCode = 0;
               lines.imageId = null;
               response.status(200);

               //TODO: read image from same file and add to db
                  //var imageObj = user.addImage(base64Data);
                  //imageObj.imageId
               fs.readFile(imagePath, function(err, data) {
                  
                  if(err) {
                     
                  } else {
                     user.saveImage(data, function(imageObj) {
                        if(util.exists(imageObj)) {
                           lines.imageId = imageObj.imageId;
                           return response.send(lines);
                        }
                     });
                  }
               });
            } else {
               response.status(500);
               return response.send({errorCode: 1});
            }
         });
      } else {
         response.status(401);
         return response.send();
      }
   });
});

/**
 * Summary: Route to get the preprocessed image.
 * request: {imageId : String}
 * response: {errorCode : Number, image : String}
 * errorCode: success 0, image not found 404, unauthorized 401
 * httpCode: success 200, image not found 404, unauthorized 401
**/
app.get('/getImage', function(request, response) {
   flowDB.getUserById(request.session.userId, function(user) {
      if(Util.exists(user)) {
         user.getImage(request.body.imageId, function(imageObj) {
            if(Util.exists(image)) {
               response.status(200);
               return response.send({
                  errorCode: 0,
                  image: imageObj.image
               });
            } else {
               response.status(404);
               return response.send({errorCode: 404});
            }
         });
         
      } else {
         response.status(401);
         return response.send({errorCode: 401});
      }
   });
});

/**
 * Summary: Route to save or publish building plans.
 * request: {
               building : {name : string, authoData : Object, graph : Object, id : String}
               publishData: boolean
            }
            id is undefined if this is a new building
 * response: {errorCode : Number, buildingId : String}
             buildingId is null if there is an error
 * errorCode: success 0, invalid data 1, failed to save 2, failedToExport 3, unauthorized 401
 * httpCode: success 200, invalid data 400, failed to save 500, failed to export 500, unauthorized 401
**/
app.post('/savePublish', function(request, response) {
   //var buildingData = JSON.parse(request.body.building);
   var buildingData = request.body.building;
   //console.log("---savePublish---");
   //console.log(request.body.building);
   flowDB.getUserById(request.session.userId, function(user) {      
      if(Util.exists(user)) {
         if(Util.exists(buildingData) 
            && Util.exists(buildingData.name)
            && Util.exists(buildingData.authoData) 
            && Util.exists(buildingData.graph)) {
            
            user.saveBuilding(buildingData, function(buildingObj) {
                  if(Util.exists(buildingObj)) {
                     if(request.body.publishData === true) {
                        console.log("----PUBLISHING----");
                     }                     
                     response.status(200);
                     return response.send({
                        errorCode: 0, 
                        buildingId: buildingObj.getUserBuildingId()
                     });
                  } else {
                     response.status(500);
                     return response.send({errorCode: 2, buildingId: null});
                  }
            });
         } else {
            response.status(400);
            return response.send({errorCode: 1, buildingId: null});
         }
      } else {
         response.status(401);
         return response.send({errorCode: 401, buildingId: null});
      }
   });
});

/**
 * Summary: Route to get the building object.
 * request: {buildingId : String}
 * response: building : {errorCode : Number, building: {name: String, id: String, authoData: Object, graph: Object}
 * errorCode: success 0, invalid data 1, building not found 404, unauthorized 401
 * httpCode: success 200, invalid data 400, building not found 404, unauthorized 401
**/
app.post('/getBuilding', function(request, response) {
   flowDB.getUserById(request.session.userId, function(user) {
      if(Util.exists(user)) {
         if(Util.exists(request.body.buildingId)) {
            user.getBuilding(request.body.buildingId, function(buildingObj) {
               if(Util.exists(buildingObj)) {
                  response.status(200);
                  return response.send({building: buildingObj.toOutput(), errorCode: 0});
               } else {
                  response.status(404);
                  return response.send({errorCode: 404});
               }
            });
         } else {
            response.status(400);
            return response.send({errorCode: 1});
         }
      } else {
         response.status(401);
         return response.send({errorCode: 401});
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