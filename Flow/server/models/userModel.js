var mongoose = require('mongoose');
var BuildingController = require('../controllers/buildingController.js');
var Util = require('../util.js');
var passportLocalMongoose = require('passport-local-mongoose');

var UserSchema = new mongoose.Schema({
   registeredTimestamp: Date,
   lastLoginTimestamp: Date,
	//username: String,
	//password: String,
   buildingRefs: Array,
   userBuildingNames: Array,
   userBuildingIds: Array
});

UserSchema.plugin(passportLocalMongoose); //adds username, password to schema

UserSchema.methods.changePassword = function(newPassword, callback) {
   var user = this;
   this.setPassword(newPassword, function(err) {
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
};

UserSchema.methods.saveBuilding = function(buildingObj, callback) {
   if(Util.exists(buildingObj) && Util.exists(buildingObj.buildingName) 
      && Util.exists(buildingObj.buildingId) && Util.exists(buildingObj.graph)
      && Util.exists(buildingObj.authoData)) {
      if(!this.hasBuilding(buildingId)) {
         return this.createNewBuilding(buildingName, graph, authoData, callback);
      } else {
         return this.getBuilding(buildingId, function(buildingObj) {
            if(Util.exists(buildingObj)) {
               buildingObj.userBuildingName = buildingName;
               buildingObj.graph = graph;
               buildingObj.authoData = authoData;
               buildingObj.save(function(err) {
                  if(err) {
                     console.log("userModel.js 29 failed to save buildingObj");
                     if(Util.exists(callback)) {return callback(null);}
                  } else if(Util.exists(callback)) {
                     return callback(buildingObj);
                  }
               });
            } else if(Util.exists(callback)){
               return callback(null);
            }
         });
      }
   } else if(Util.exists(callback)) {
      return callback(null);
   }
};

UserSchema.methods.createNewBuilding = function(buildingName, graph, authoData, callback) {
	console.log("userModel line18");
	var user = this;
	BuildingController.newBuilding(this.username, buildingName, graph, authoData, function(buildingObj) {
		console.log("userModel line20: created canvas: "+JSON.stringify(buildingObj));
		user.userBuildingNames.push(buildingObj.getUserBuildingName());
      user.userBuildingIds.push(buildingObj.getUserBuildingId());
		user.buildingRefs.push({name: buildingObj.getUserBuildingName(), id: buildingObj.getUserBuildingId()});
      buildingObj.save(function(err) {
			if(err) {
				console.log("\n--userMode.js 26 ERR: "+err+"--\n");
				if(Util.exists(callback)) {return callback(null);}
			}
			else {
				user.save(function(err) {
					if(err) {
                  console.log("\n--userMode.js 31 ERR: "+err+"--\n");
                  if(Util.exists(callback)) {return callback(null);}
					}
					else if(Util.exists(callback)) {return callback(buildingObj);}
				});
			}
		});
	});
};

UserSchema.methods.addBuildingById = function(userBuildingId, callback) {
	if(!this.hasBuilding(userBuildingId)) {
		var user = this;
		BuildingController.findOne({userBuildingId: userBuildingId}, function(buildingObj) {
			if(Util.exists(buildingObj)) {
            user.userBuildingIds.push(buildingObj.getUserBuildingId());
            user.userBuildingName.push(buildingObj.getUserBuildingName());
            user.buildingRefs.push({name: buildingObj.getUserBuildingName(),
                                    id: buildingObj.getUserBuildingId()});
            user.save(function(err) {
               if(err) {
                  console.log("\n--userModel.js 48 ERR: "+err+"--\n");
                  if(Util.exists(callback)) {return callback(null);}
               }
               else if(Util.exists(callback)) {return callback(buildingObj);}
            });
			}
		});
	}
   else if(Util.exists(callback)) {return callback(null);}
};

UserSchema.methods.addBuildingByObj = function(buildingObj, callback) {
	if(!this.hasBuilding(buildingObj.userBuildingId)) {
		this.userBuildingIds.push(buildingObj.getUserBuildingId());
		this.userBuildingNames.push(buildingObj.getUserBuildingName());
		this.buildingRefs.push({name: buildingObj.getUserBuildingName(),
                              id: buildingObj.getUserBuildingId()});
		this.save(function(err) {
			if(err) {
				console.log("\n--userModel.js 66 ERR: "+err+"--\n");
				if(Util.exists(callback)) {return callback(null);}
			}
			else if(Util.exists(callback)) {return callback();}
		});
	}
};

UserSchema.methods.indexOfBuildingRef = function(userBuildingId) {
   for(var br = 0; br < this.buildingRefs.length; br++) {
      if(this.buildingRefs[br].id === userBuildingId) {return br;}
   }
   
   return -1;
};

UserSchema.methods.getBuildingRefs = function() {
   return this.buildingRefs;
};

UserSchema.methods.hasBuilding = function(userBuildingId) {
   return Util.exists(userBuildingId) && this.indexOfBuildingRef(userBuildingId) !== -1;
};

UserSchema.methods.getBuilding = function(userBuildingId, callback) {
	console.log("--getting Building--: "+userBuildingId);
	console.log("hasBuilding="+this.hasBuilding(userBuildingId));
	BuildingController.findOne({_creatorId: this.username, userBuildingId: userBuildingId}, callback);
};

module.exports = mongoose.model('User', UserSchema);