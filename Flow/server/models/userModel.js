var mongoose = require('mongoose');
var BuidlingController = require('../controllers/buildingController.js');
var Util = require('../util.js');
var passportLocalMongoose = require('passport-local-mongoose');

var UserSchema = new mongoose.Schema({
    registeredTimestamp: Date,
    lastLoginTimestamp: Date,
	//username: String,
	//password: String,
    userBuildingNames: Array,
    userBuildingIds: Array
});

UserSchema.plugin(passportLocalMongoose); //adds username, password to schema

UserSchema.methods.createNewBuilding = function(buildingName, graph, authoData, callback) {
	console.log("userModel line18");
	var user = this;
	BuidlingController.newBuilding(this.username, buildingName, graph, authoData, function(buildingObj) {
		console.log("userModel line20: created canvas: "+JSON.stringify(buildingObj));
		user.userBuildingIds.push(buildingObj.getUserBuildingId());
		user.userBuildingNames.push(buildingObj.getUserBuildingName());
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
	if(!this.hasCanvas(userBuildingId)) {
		var user = this;
		BuidlingController.findOne(userBuildingId, function(buildingObj) {
			if(Util.exists(buildingObj)) {
				user.userBuildingIds.push(buildingObj.getUserBuildingId());
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

UserSchema.methods.addBuidlingByObj = function(buildingObj, callback) {
	if(!this.hasBuilding(buildingObj.userBuildingId)) {
		this.userBuildingIds.push(buildingObj.getUserBuildingId());
		this.userBuildingNames.push(buildingObj.getUserBuildingName());
		
		this.save(function(err) {
			if(err) {
				console.log("\n--userModel.js 66 ERR: "+err+"--\n");
				if(Util.exists(callback)) {return callback(null);}
			}
			else if(Util.exists(callback)) {return callback();}
		});
	}
};

UserSchema.methods.hasBuilding = function(userBuildingId) {
	return (this.userBuildingIds.indexOf(userBuildingId) !== -1);
};

UserSchema.methods.getBuilding = function(userBuildingId, callback) {
	console.log("--getting Building--: "+userBuildingId);
	console.log("hasBuilding="+this.hasBuilding(userBuildingId));
	if(this.hasCanvas(userBuildingId)) {
		BuildingController.findOne({'userBuildingId': userBuildingId}, callback);
	}
};

module.exports = mongoose.model('User', UserSchema);