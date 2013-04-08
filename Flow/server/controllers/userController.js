var mongoose = require('mongoose');
var passport = require('passport');
var LocalStrategy = require('passport-local').Strategy;
var Util = require('../util.js');

/**
 * Initializes the authentication strategy to the default.
**/
function initPassportUser(){
    var User = require('../models/userModel.js');

    passport.use(new LocalStrategy(User.authenticate()));

    passport.serializeUser(User.serializeUser());
    passport.deserializeUser(User.deserializeUser());

    return User;
}
var User = initPassportUser();


var UserController = {
   /**
    * Summary: Creates and initializes a new User.
    * Parameters: username: String
                  password: String
                  callback: function
    * Returns: undefined. Calls callback with created User or null if it fails.
   **/
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
            
            var date = new Date();
				user.lastLoginTimestamp = date;
            user.registeredTimestamp = date;
				
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
	
   /**
    * Summary: Find all Users based off searchJSON.
    * Parameters: searchJSON: obj
    * Returns: undefined. Calls callback with found Users or null if none found.
   **/
	find: function(searchJSON, callback) {
		User.find(searchJSON, function(err, userObjs) {
			if(err) {
				console.log("\n--userController.js 63 ERR: "+err+"--\n");
				if(Util.exists(callback)) {return callback(null);}
			}
			else if(Util.exists(callback)) {return callback(userObjs);}
		});
	},
	
   /**
    * Summary: Find one User based off searchJSON.
    * Parameters: searchJSON: obj
    * Returns: undefined. Calls callback with found User or null if none found.
   **/
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