/**
 * Node.js server for User Authoring tool
 * Written by: Daniel Muller and Vansi Vallabhaneni (2013)
 * Express Docs: expressjs.com
 * Passport Docs: passportjs.org
 * Mongoose (MongoDB middleware for node) Docs: mongoosejs.com
 * Some good example code (from 15-237 Fall 12): 
      kosbie.net/cmu/fall-12/15-237/handouts/notes-server-side-part1.html
      kosbie.net/cmu/fall-12/15-237/handouts/notes-server-side-part2.html
**/

// Initialize middleware and global variables.
var path = require('path');
var express = require('express');
var http = require('http');
var util = require('util');
var exec = require('child_process').exec;
var fs = require('fs');
fs.exists = fs.exists || path.exists;
var mongoose = require('mongoose');
var passport = require('passport');
var LocalStrategy = require('passport-local').Strategy;

var Util = require('./util.js');
var FlowDB = require('./controllers/flowDB.js');

var Sector = require('./sector.js');


// Set up express server.
var app = express();
var port;
var child;
GLOBAL.flowDB;


/**
 * Summary: Initiazes the express server
 * Parameters: undefined
 * Returns: undefined
**/
function init(){
  configureExpress(app);
  
  port = 8080
  // server grove connection string: 'mongodb://test:test@69.195.199.181:27017/flow'
  flowDB = new FlowDB('mongodb://test:test@localhost:27017/flow'); //change this
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
    
    app.use(express.session({userId: "", secret: Util.generateConnKey(9)}));
    app.use(express.static(path.join(__dirname, '../www')));

    app.use(function(request, response, next) {
      response.header('Access-Control-Allow-Origin', '*');
      response.header('Access-Control-Allow-Methods', 'PUT,GET,POST,DELETE,OPTIONS');
      response.header('Access-Control-Allow-Headers', 'Content-Type,X-Requested-With');

      next();
    });

    app.use(app.router);
    app.use(express.errorHandler({ dumpExceptions: true, showStack: true }));
  });
}

//-----------
// API Routes
//-----------

/**
 * Summary: Public API to get the published building references.
 * Request: undefined
 * Response: buildingRefs: [{name: String, id: String}]
 * httpCode: undefined
**/
app.get('/allBuildingRefs', function(request, response) {
  response.send({buildingRefs: flowDB.getPublicBuildingRefs()});
});

/**
 * Summary: Public API to get a published building graph by the building id.
 * Request: buildingId: String
 * Response: buildingGraph: Object
 * httpCode: success 200, building graph not found 404, bad request 400
**/
app.get('/buildingGraph', function(request, response) {
  var buildingId = response.query.buildingId;
  if(Util.exists(buildingId)) {
    flowDB.getBuildingGraphById(buildingId, function(buildingGraph) {
      if(!Util.exists(buildingGraph)) {
        response.status(404);
        response.send("Building Graph Not Found");
      } else {
        response.status(200);
        response.send({buildingGraph: buildingGraph});
      }
    });
  } else {
    response.status(400);
    response.send("Illegal request, send a buildingId");
  }
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
 * httpCode: success 200, invalid data 400, user already exists 400, failed to auto login 200
 * httpCodes (ie response.status) have been removed since client side can't see the errorCode when there is a non 2xx status
**/
app.post('/register', function(request, response) {
  if(Util.exists(request) && Util.isValidUsername(request.body.username) 
    && Util.isValidPassword(request.body.password)) {
   
    flowDB.register(request.body.username, request.body.password, function(newUser) {
      if(Util.exists(newUser)) {
        var responseData = {errorCode: 0}
        //response.status(200);
        
        request.login(newUser, function(err) {
          if (err) {
            console.log("Server.Register: failed to login");
            responseData.errorCode = 3;
          }
          request.session.userId = newUser._id;
          return response.send(responseData);
        });
      } else {
        //response.status(400);
        return response.send({errorCode: 2});
      }
   });
  } else {
    //response.status(400);
    return response.send({errorCode: 1});
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
      if(Util.isValidPassword(request.body.newPassword)) {
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
       response.status(401);
       return response.send({errorCode: 401});
    }
  });
});


/**
 * Summary: Route to preprocess an image.
 * request: {image : String}
 * response: {errorCode : Number, lines : [], imageId: String}
 * errorCode: success 0, invalid data 1, preprocessing failed 2, unauthorized 401
 * httpCode: success 200, invalid data 400, preprocessing failed 500, unauthorized 401
**/
app.post('/preprocess', function (request, response) {
  flowDB.getUserById(request.session.userId, function(user) {
    if(Util.exists(user)) {
      var imageData = request.body.image;       
      if(Util.exists(imageData)) {
        var imageDir = './temp/';
        
        // Generate random file names. - TODO: make a hash code, check on seed of .random
        var randFileNum = Math.floor(Math.random() * 90000) + 10000;
        var oldImagePath = imageDir + 'oldImage'+randFileNum+'.png';
        var newImagePath = imageDir + 'newImage'+randFileNum+'.png';
        var dataPath = imageDir + 'data'+randFileNum+'.json';
         
        // Extract the base64 incoded image string and create a buffer.
        var index = imageData.indexOf('base64,') + 'base64,'.length;
        var base64Data = imageData.substring(index, imageData.length);
        var base64DataBuffer = new Buffer(base64Data, "base64");
        
        fs.writeFile(oldImagePath, base64DataBuffer, function(err) {
          if(Util.exists(err)) {
            console.log("Server.preprocessor: failed to write inital image: "+err);
            response.status(500);
            return response.send({errorCode: 2});
          } else {
            // Call the preprocessor.
            preprocessor(oldImagePath, newImagePath, dataPath, function(preprocessData) {
              if(Util.exists(preprocessData)) {
              
                // Save the image.
                user.saveImage(null, preprocessData.image, preprocessData.dataURL, function(imageObj) {
                  if(Util.exists(imageObj)) {
                    preprocessData.result.errorCode = 0;
                    preprocessData.result.imageId = imageObj.imageId;
                    
                    response.status(200);          
                    return response.send(preprocessData.result);
                  } else {
                    response.status(500);
                    return response.send({errorCode: 2});
                  }
                });
                  
              } else {
                console.log("Server.preprocess: unable to preprocess data");
                response.status(500);
                return response.send({errorCode: 2});
              }
            });
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

/**
 * Summary: Route to get the preprocessed image.
 * request: {imageId : String}
 * response: {errorCode : Number, image : String, imageId : String}
 * errorCode: success 0, invalid data 1, image not found 404, unauthorized 401
 * httpCode: success 200, invalid data 400, image not found 404, unauthorized 401
**/
app.get('/image', function(request, response) {
  flowDB.getUserById(request.session.userId, function(user) {
    if(Util.exists(user)) {
      if(Util.exists(request.query.imageId)) {
        user.getImage(request.query.imageId, function(imageObj) {
          if(Util.exists(imageObj)) {
            response.status(200);
            var responseData = imageObj.toOutput(); // = {image: String, imageId: String}
            responseData.errorCode = 0;
            return response.send(responseData);
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

/**
 * Summary: Route to save or publish building plans.
 * request: {
              building : {name : string, authoData : Object, graph : Object, id : String}
              publishData: boolean
            }
            id is undefined if this is a new building
 * response: {errorCode : Number, buildingId : String}
             buildingId is null if there is an error
 * errorCode: success 0, invalid data 1, failed to save 2, failed to publish 3, unauthorized 401
 * httpCode: success 200, invalid data 400, failed to save 500, failed to publish 500, unauthorized 401
**/

// NOTE: Gary, it might be a good idea to call the routines to generate the three text files right here.

app.post('/savePublish', function(request, response) {
  var buildingData = request.body.building;
  console.log("GARY:" + JSON.stringify(buildingData));
  flowDB.getUserById(request.session.userId, function(user) {      
    if(Util.exists(user)) {
      if(Util.exists(buildingData) 
        && Util.exists(buildingData.name)
        && Util.exists(buildingData.authoData) 
        && Util.exists(buildingData.graph)) {
        
        user.saveBuilding(buildingData, function(buildingObj) {
          if(Util.exists(buildingObj)) {
            if(request.body.publishData === true) {
              flowDB.publishBuilding(buildingObj);
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
 * Summary: Route to unpublish building graph.
 * request: buildingId : String
 * response: {errorCode : Number}
 * errorCode: success 0, invalid data 1, unauthorized 401
 * httpCode: success 200, invalid data 400, unauthorized 401
**/
app.get('/unpublishBuilding', function(request, response) {
  var buildingId = request.query.buildingId;
  flowDB.getUserById(request.session.userId, function(user) {      
    if(Util.exists(user)) {
      if(Util.exists(buildingId)) {
        if(user.hasBuilding(buildingId)) {
          flowDB.unpublishBuilding(buildingId);
          response.status(200);
          return response.send({errorCode: 0});
        } else {
          response.status(401);
          return response.send({errorCode: 401});
        }
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

/**
 * Summary: Route to delete a building.
 * request: buildingId : String
 * response: {errorCode : Number}
 * errorCode: success 0, invalid data 1, unauthorized 401, building not found 404
 * httpCode: success 200, invalid data 400, unauthorized 401, building not found 404
**/
app.get('/deleteBuilding', function(request, response) {
  var buildingId = request.query.buildingId;
  flowDB.getUserById(request.session.userId, function(user) {      
    if(Util.exists(user)) {
      if(Util.exists(buildingId)) {
        if(user.hasBuilding(buildingId)) {
          flowDB.unpublishBuilding(buildingId);
          user.deleteBuilding(buildingId, function(errorCode) {
            if(errorCode === 0) {
              response.status(200);
              return response.send({errorCode: 0});
            } else {
              response.status(404);
              return response.send({errorCode: 404});
            }
          });
          
        } else {
          response.status(401);
          return response.send({errorCode: 401});
        }
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
app.get('/building', function(request, response) {
   flowDB.getUserById(request.session.userId, function(user) {
      if(Util.exists(user)) {
        if(Util.exists(request.query.buildingId)) {
          user.getBuilding(request.query.buildingId, function(buildingObj) {
            if(Util.exists(buildingObj)) {
              response.status(200);
	      
	      console.log("Gary/building" + JSON.stringify(buildingObj.toOutput()));
	      
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

/**
 * Summary: Route to get the buildingRefs.
 * request: void
 * response: buildings :  [{name: String, id: String}]
 * errorCode: success 0, unauthorized 401
 * httpCode: success 200, unauthorized 401
**/
app.get('/buildingsRefs', function(request, response) {
  flowDB.getUserById(request.session.userId, function(user) {
    if(Util.exists(user)) {
      response.status(200);
      return response.send({
         buildings: user.getBuildingRefs()
      });
    } else {
       response.status(401);
       return response.send({errorCode: 401});
    }
  });
});

// =========== PREPROCESSOR ==========
/**
 * Summary: calls python script to extract lines from image and convert to gray scale
 * Parameters:  imagePath: String, path to image to be processsed
                dataPath: String, path to store processed data
                callback: function
 * Returns: calls callback with preprocessed data and image
**/
function preprocessor(oldImagePath, newImagePath, dataPath, callback) {

  // For debugging without image preprocessing
  //callback({result: {}, image: '', dataURL: 'data:image/png;base64,'});
  //return;

  //dataPath = 'json.txt';

  var child = exec('python ./python/preprocessing.py ' + oldImagePath + ' ' 
                    + newImagePath + ' ' + dataPath, function (err, stdout, stderr) {
    console.log("+++Preprocess Error Log+++");
    console.log("err: " + err);
    console.log("stdout: " + stdout);
    console.log("stderr: " + stderr);
    console.log("+++Preprocess Logging Complete+++\n");
    
    fs.exists(oldImagePath, function(exists) {
      if(exists) {
        fs.unlink(oldImagePath);
      }
    });
    
    /* Since Node is asynchronous, we do not have control of the order in which the image and data files need to be read,
      so we just call the preprocessor's callback when the last fileIO read's callback gets called
     these booleans are used to help determine within the callback if it is the last one or not. */
    var readOtherFile = false;
    var returned = false;
    var data;
    var base64ImageStr;
    
    // Read the thresholded image.
    fs.exists(newImagePath, function (exists) {
      if(exists) {
        fs.readFile(newImagePath, function(err, imageStr) {
          if(err) {
            console.log("Server.preprocessor: failed to read processed image: "+err);
            if(Util.exists(callback)) {return callback(null)}
          } else {
            fs.unlink(newImagePath);
            base64ImageStr = new Buffer(imageStr, 'base64').toString('base64');
            
            if(!returned && readOtherFile && Util.exists(callback)) {
              returned = true;
              return callback({result: data, image: base64ImageStr, dataURL: 'data:image/png;base64,'});
            }
            readOtherFile = true;
          }
        });
      } else if(!returned && Util.exists(callback)) {
        returned = true;
        return callback(null);
      }
    });
    
    // Read the extracted data.
    fs.exists(dataPath, function(exists) {
      if(exists) {
        fs.readFile(dataPath, function read(err, dataStr) {
          if (err) {
            console.log("Server.preprocessor: failed to read data: "+err);
            if(Util.exists(callback)) {return callback(null)}
          } else {
            var dataStrUTF8 = dataStr.toString('utf8');
            
            try {
              data = JSON.parse(dataStrUTF8);
              fs.unlink(dataPath);
            } catch(e) {
              data = null;
              console.log("failed to parse data at: " + dataPath);
            }
            
            if(!returned && readOtherFile && Util.exists(callback)) {
              returned = true;
              return callback({result: data, image: base64ImageStr, dataURL: 'data:image/png;base64,'});
            }
            readOtherFile = true;
          }
        });
      } else if(!returned && Util.exists(callback)) {
        returned = true;
        return callback(null);
      }
    });
    
  });
}

// Launch server
app.listen(port);
console.log("Express server listening on port " + port);
