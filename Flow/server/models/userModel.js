var mongoose = require('mongoose');
var passportLocalMongoose = require('passport-local-mongoose');
var BuildingController = require('../controllers/buildingController.js');
var ImageController = require('../controllers/imageController.js');
var Util = require('../util.js');

var UserSchema = new mongoose.Schema({
   registeredTimestamp: Date,
   lastLoginTimestamp: Date,
   buildingRefs: Array,
   imageRefs: Array
});

//Plugs in passport to User, adds username and password to UserSchema
UserSchema.plugin(passportLocalMongoose); //adds username, password to schema

/**
 * Summary: Changes the password to newPassword.
 * Parameters: newPassword: String
               callback: function
 * Returns: calls callback with null or user.
**/
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

//-------------------
// User Image Methods
//-------------------

/**
 * Summary: Edits and saves imageStr and creates a new image if imageId is undefined/null.
 * Parameters: imageStr: String
               imageId: String
               callback: function
 * Returns: calls callback with null or building.
**/
UserSchema.methods.saveImage = function(imageId, imageStr, dataURL, callback) {
  if(Util.exists(imageStr)) {
    if(!Util.exists(imageId)) {
       return this.createNewImage(imageStr, dataURL, callback);
    } else {
       return this.getImage(imageId, function(imageObj) {
          if(Util.exists(imageObj)) {
             imageObj.imageStr = imageStr;
             imageObj.dataURL = dataURL;
             imageObj.save(function(err) {
                if(err) {
                   console.log("userModel.js 29 failed to save imageObj");
                   if(Util.exists(callback)) {return callback(null);}
                } else if(Util.exists(callback)) {
                   return callback(imageObj);
                }
             });
          } else if(Util.exists(callback)) {
            return callback(null);
          }
       });
    }
  } else if(Util.exists(callback)) {
    return callback(null);
  }
};

/**
 * Summary: Creates a new image from inputs.
 * Parameters: imageStr : String
               callback: function
 * Returns: calls callback with null or building.
**/
UserSchema.methods.createNewImage = function(imageStr, dataURL, callback) {
	console.log("userModel line18");
	var user = this;
	ImageController.newImage(this._id, imageStr, dataURL, function(imageObj) {
    //console.log("userModel line87: created image: "+JSON.stringify(imageObj));
    if(Util.exists(imageObj)) {
      user.imageRefs.push(imageObj.imageId);
      user.save(function(err) {
        if(err) {
          console.log("\n--userMode.js 31 ERR: "+err+"--\n");
          if(Util.exists(callback)) {return callback(null);}
        }
        else if(Util.exists(callback)) {return callback(imageObj);}
      });
    } else if(Util.exists(callback)) {return callback(null);}
	});
};

/**
 * Summary: Finds the image with imageId if the user has access to it.
 * Parameters: imageId: String
               callback: function
 * Returns: calls callback with building or null if none found.
**/
UserSchema.methods.getImage = function(imageId, callback) {
	console.log("--getting Image--: "+imageId);
	ImageController.findOne({_creatorId: this._id, imageId: imageId}, callback);
};


//----------------------
// User Building Methods
//----------------------

/**
 * Summary: Saves buildingData.
 * Parameters: buildingData: {name : string, authoData : obj, graph : obj, id : String}
                  id is undefined if this is a new building
               callback: function
 * Returns: calls callback with null or building.
**/
UserSchema.methods.saveBuilding = function(buildingData, callback) {
   if(Util.exists(buildingData) && Util.exists(buildingData.name)
      && Util.exists(buildingData.graph) && Util.exists(buildingData.authoData)) {
      if(!this.hasBuilding(buildingData.id)) {
         return this.createNewBuilding(buildingData.name, buildingData.graph, buildingData.authoData, callback);
      } else {
         return this.getBuilding(buildingData.id, function(buildingObj) {
            if(Util.exists(buildingObj)) {
               buildingObj.userBuildingName = buildingData.name;
               buildingObj.graph = buildingData.graph;
               buildingObj.authoData = buildingData.authoData;
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

/**
 * Summary: Creates a new building from inputs.
 * Parameters: buildingData: {name : string, authoData : obj, graph : obj, id : String}
                  id is undefined if this is a new building
               callback: function
 * Returns: calls callback with null or building.
**/
UserSchema.methods.createNewBuilding = function(buildingName, graph, authoData, callback) {
	console.log("userModel line18");
	var user = this;
	BuildingController.newBuilding(this._id, buildingName, graph, authoData, function(buildingObj) {
		if(Util.exists(buildingObj)) {
      //console.log("userModel line87: created building: "+JSON.stringify(buildingObj));
      user.buildingRefs.push({name: buildingObj.getUserBuildingName(),
                                id: buildingObj.getUserBuildingId()});
                                
      user.save(function(err) {
        if(err) {
          console.log("\n--userMode.js 31 ERR: "+err+"--\n");
          if(Util.exists(callback)) {return callback(null);}
        }
        else if(Util.exists(callback)) {return callback(buildingObj);}
      });
    } else if(Util.exists(callback)) {return callback(null);}
	});
};

/**
 * Summary: Finds and adds existing building by id to this User.
 * Parameters: userBuildingId: String
               callback: function
 * Returns: calls callback with null or building.
**/
UserSchema.methods.addBuildingById = function(userBuildingId, callback) {
	if(!this.hasBuilding(userBuildingId)) {
		var user = this;
		BuildingController.findOne({userBuildingId: userBuildingId}, function(buildingObj) {
			if(Util.exists(buildingObj)) {
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

/**
 * Summary: Adds existing building to this User.
 * Parameters: buildingObj: Building object
               callback: function
 * Returns: calls callback with null or building.
**/
UserSchema.methods.addBuildingByObj = function(buildingObj, callback) {
	if(!this.hasBuilding(buildingObj.userBuildingId)) {
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

/**
 * Summary: Getter for buildingRef.
 * Parameters: undefined.
 * Returns: list of building references.
**/
UserSchema.methods.getBuildingRefs = function() {
   return this.buildingRefs;
};

/**
 * Summary: Index of first building reference ({name : String, id : String})
            with id equal to userBuildingId
 * Parameters: userBuildingId: String
 * Returns: Number, index or -1 if building reference not found
**/
UserSchema.methods.indexOfBuildingRef = function(userBuildingId) {
   for(var br = 0; br < this.buildingRefs.length; br++) {
      if(this.buildingRefs[br].id === userBuildingId) {return br;}
   }
   
   return -1;
};

/**
 * Summary: Determines if the user has access to the building from userBuildingId.
 * Parameters: userBuildingId: String
 * Returns: boolean
**/
UserSchema.methods.hasBuilding = function(userBuildingId) {
   return Util.exists(userBuildingId) && this.indexOfBuildingRef(userBuildingId) !== -1;
};

/**
 * Summary: Finds the building with id of userBuildingId if the user has access to it.
 * Parameters: userBuildingId: String
               callback: function
 * Returns: calls callback with building or null if none found.
**/
UserSchema.methods.getBuilding = function(userBuildingId, callback) {
	console.log("--getting Building--: "+userBuildingId);
	console.log("hasBuilding="+this.hasBuilding(userBuildingId));
	BuildingController.findOne({_creatorId: this._id, userBuildingId: userBuildingId}, callback);
};

module.exports = mongoose.model('User', UserSchema);