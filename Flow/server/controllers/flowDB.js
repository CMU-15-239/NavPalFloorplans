var mongoose = require('mongoose');
var UserController = require('./userController.js');
var User = require('../models/userModel.js');
var Building = require('../models/buildingModel.js');
var BuildingController = require('./buildingController.js');
var FlowDBPersistents = require('../models/flowDBPersistentsModel.js');
var Util = require('../util.js');

/**
 * Summary: Constructor for FlowDB and connects to a MongoDB at connection.
 * Parameters: connection: String
 * Returns: undefined
**/
function FlowDB(connection) {
	mongoose.connect(connection);
  this.persistents;
  FDB = this;
  FlowDBPersistents.findOne({}, function(err, persistents) {
    if(Util.exists(persistents)) {
      FDB.persistents = persistents;
    } else {
      FDB.persistents = new FlowDBPersistents({
        publicBuildingRefs: [],
        imageCounter: 0,
        buildingCounter: 0
      });
      FDB.persistents.save();
    }
  });
}

FlowDB.prototype.getAndIncImageCounter = function() {
  var imageCounter = this.persistents.imageCounter;
  this.persistents.imageCounter++
  
  if(this.persistents.imageCounter % 10 === 0) {
    this.persistents.save();
  }
  
  return imageCounter
};

FlowDB.prototype.getAndIncBuildingCounter = function() {
  var buildingCounter = this.persistents.buildingCounter;
  this.persistents.buildingCounter++
  
  if(this.persistents.buildingCounter % 10 === 0) {
    this.persistents.save();
  }
  
  return buildingCounter;
};

/**
  * Summary: Adds a building reference coressponding to the given building to the publicBuildingRefs
  * Parameters: building: Building
  * Returns: undefined
**/
FlowDB.prototype.publishBuilding = function(building) {
  if(this.indexOfPublicBuildingRef(building.getUserBuildingId()) === -1) {
    this.persistents.publicBuildingRefs.push({name: buildingObj.getUserBuildingName(),
                                    id: buildingObj.getUserBuildingId()});
    this.persistents.save();
  }
};

/**
  * Summary: Removes a building reference coressponding to the given building to the publicBuildingRefs
  * Parameters: buildingId: String
  * Returns: undefined
**/
FlowDB.prototype.unpublishBuilding = function(buildingId) {
  var idx = this.indexOfPublicBuildingRef(buildingId);
  if(idx !== -1) {
    this.persistents.publicBuildingRefs.splice(idx, 1);
    this.persistents.save();
  }
};

/**
  * Summary: Finds a building reference with an id of buildingId
  * Parameters: buildingId: String
  * Returns: int, -1 if none found
**/
FlowDB.prototype.indexOfPublicBuildingRef = function(buildingId) {
  for(var b = 0; b < this.publicBuildingRefs.length; b++) {
    if(this.persistents.publicBuildingRefs[b].id === buildingId) {
      return b;
    }
  }
  
  return -1;
};

/**
  * Summary: Finds the 
**/
FlowDB.prototype.getBuildingGraphById = function(buildingId, callback) {
  var FDB = this;
  if(this.indexOfPublicBuildingRef(buildingId) === -1) {
    callback(null);
  } else {
    BuildingController.findOne({userBuildingId: buildingId}, function(building) {
      if(!Util.exists(building)) {
        FDB.unpublishBuilding(buildingId);
        callback(null);
      } else {
        callback(building.getGraph());
      }
    });
  }
};

/**
 * Summary: Finds one user based off username.
 * Parameters: username: String
               callback: function
 * Returns: undefined
**/
FlowDB.prototype.getUserByUsername = function(username, callback) {
	UserController.findOne({username: username}, callback);
};

/**
 * Summary: Finds one user based off object id.
 * Parameters: id: String
               callback: function
 * Returns: undefined
**/
FlowDB.prototype.getUserById = function(id, callback) {
   if(Util.exists(id)) {
      return UserController.findOne({_id: id}, callback);
   } else if(Util.exists(callback)) {
      return callback(null);
   }
};

/**
 * Summary: Clears all data in the database.
 * Parameters: undefined
 * Returns: undefined
**/
FlowDB.prototype.clearData = function() {
	User.find({}).remove();
	Building.find({}).remove();
};

/**
 * Summary: Creates a new User with username and password.
 * Parameters: username: String
               password: String
               callback: function
 * Returns: undefined. Calls callback with created User or null if a User with username already exists.
**/
FlowDB.prototype.register = function(username, password, callback) {
	console.log("flowDB.register");
    this.getUserByUsername(username, function(userObj) {
        if(!Util.exists(userObj)) {
            UserController.newUser(username, password, callback);
        } else if(Util.exists(callback)){
            return callback(null);
        }
    });
};

module.exports = FlowDB;
