var mongoose = require('mongoose');
var UserController = require('./userController.js');
var User = require('../models/userModel.js');
var Building = require('../models/buildingModel.js');
var Util = require('../util.js');

/**
 * Summary: Constructor for FlowDB and connects to a MongoDB at connection.
 * Parameters: connection: String
 * Returns: undefined
**/
function FlowDB(connection) {
	mongoose.connect(connection);
}

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

/*
FlowDB.prototype.login = function(username, password, callback) {
	console.log("FBController line 39 loggin");
	UserController.authUser(username, password, function(validity, userObj) {
		if(UserController.isAuthorized(validity)){
			userObj.lastLoginTimestamp = new Date();
            userObj.save(function(err) {
                if(err) {
                    console.log("\n--flowDBController.js 22 ERR: "+err+"--\n");
                    if(Util.exists(callback)) {return callback({userObj: null, validity: true});}
                }
                else if(Util.exists(callback)) {return callback({userObj: userObj, validity: true});}
            });
		}
		else if(Util.exists(callback)) {
           return callback({userObj: null, validity: false});
        }
	}.bind(this));
};
*/

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