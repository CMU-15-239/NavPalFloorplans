var mongoose = require('mongoose');
var UserController = require('./userController.js');
var User = require('../models/userModel.js');
var Building = require('../models/buildingModel.js');
var Util = require('../util.js');


function FlowDB(connection) {
	mongoose.connect(connection);
}

FlowDB.prototype.getUser = function(username, callback) {
	UserController.findOne({username: username}, callback);
};

FlowDB.prototype.clearData = function(callback) {
	User.find({}).remove();
	Building.find({}).remove();
	if(Util.exists(callback)) {callback();}
};

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

FlowDB.prototype.register = function(username, password, callback) {
	console.log("flowDB.register");
    this.getUser(username, function(userObj) {
        if(!Util.exists(userObj)) {
            UserController.newUser(username, password, callback);
        }
    });
};

module.exports = FlowDB;