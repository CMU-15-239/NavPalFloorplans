var mongoose = require('mongoose');
var passport = require('passport');
var LocalStrategy = require('passport-local').Strategy;
var Util = require('../util.js');

function initPassportUser(){
    var User = require('../models/userModel.js');

    passport.use(new LocalStrategy(User.authenticate()));

    passport.serializeUser(User.serializeUser());
    passport.deserializeUser(User.deserializeUser());

    return User;
}
var User = initPassportUser();


var UserController = {
	newUser: function(username, password, callback) {
		User.findOne({username: username}, function(err, user) {
			if(err) {
				console.log("\n--userController.js 10 ERR: "+err+"--\n");
				if(Util.exists(callback)) {return callback(null);}
			}
			
			else if(!Util.exists(user)) {
				user = new User({
					username: username,
					buildingRefs: [],
					userBuildingNames: [],
					userBuildingIds: []
				});
				
				user.lastLoginTimestamp = user.registeredTimestamp = new Date();
				
               user.setPassword(password, function(err) {
                    if(err) {
                        console.log("\n--userController.js 22 ERR: "+err+"--\n");
                        if(Util.exists(callback)) {return callback(null);}
                    }
                    else {
                        user.save(function(err) {
                            if(err) {
                                console.log("\n--userController.js 22 ERR: "+err+"--\n");
                                if(Util.exists(callback)) {return callback(null);}
                            }
                            else if(Util.exists(callback)) {return callback(user);}
                        });
                    }
               });
				
			}
			else if(Util.exists(callback)) {return callback(null);}
		});
	},
    
    isAuthorized: function(validity) {return validity === 1;},
    isUserNotFound: function(validity) {return validity === 0;},
    isInvalidPassword: function(validity) {return validity === -1;},
    
	//validity is 1 if authorized
	//validity is 0 if username not found
	//validity is -1 if username found, but password doesnt match
	authUser: function(username, password, callback) {
		User.findOne({username: username}, function(err, result) {
			if(err) {
				console.log("\n--userController.js 38 ERR: "+err+"--\n");
				if(Util.exists(callback)) {return callback(false, null);}
				return;
			}
			console.log("userController.js 50 username: "+username+" result: ");
			console.log(JSON.stringify(result));
			console.log("-------------\n");
			
			var validity = 0;
			if(Util.exists(result)) {
				if(Util.exists(result.password) && result.password === password) 
				{validity = 1;}
				
				else {validity = -1;}
			}
			
			if(Util.exists(callback)) {
				return callback(validity, result);
			}
		});
	},
	
	find: function(searchJSON, callback) {
		User.find(searchJSON, function(err, userObjs) {
			if(err) {
				console.log("\n--userController.js 63 ERR: "+err+"--\n");
				if(Util.exists(callback)) {return callback(null);}
			}
			else if(Util.exists(callback)) {return callback(userObjs);}
		});
	},
	
	findOne: function(searchJSON, callback) {
		User.findOne(searchJSON, function(err, userObj) {
			if(err) {
				console.log("\n--userController.js 73 ERR: "+err+"--\n");
				if(Util.exists(callback)) {return callback(null);}
			}
			else if(Util.exists(callback)) {return callback(userObj);}
		});
	}
};

module.exports = UserController;