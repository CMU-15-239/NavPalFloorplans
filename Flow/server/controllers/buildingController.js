var mongoose = require('mongoose');
var Util = require('../util.js');
var Building = require('../models/buildingModel.js');

var BuildingController = {
   /**
    * Summary: Creates and initializes a new Building.
    * Parameters: creatorId: String
                  userBuildingName: String
                  graph: Object
                  authoData: Object
                  callback: function
    * Returns: undefined. Calls callback with created Building or null if it fails.
   **/   
	newBuilding: function(creatorId, userBuildingName, graph, authoData, callback) {
		var BC = this; //the building controller
		var userBuildingId = this.generateBuildingId(creatorId);
		console.log("buildingController line10: "+userBuildingId);
    
		this.findOne({userBuildingId: userBuildingId}, function(buildingObj) {
			if(!Util.exists(buildingObj)) {
            buildingObj = new Building({
               _creatorId: creatorId,
               userBuildingId: userBuildingId,
               userBuildingName: userBuildingName,
               graph: graph,
               authoData: authoData
            });
				
            buildingObj.save(function(err) {
               if(err) {
                  console.log("\n--buildingController.js 32 ERR: "+err+"--\n");
                  if(Util.exists(callback)) {return callback(null);}
               }
               else if(Util.exists(callback)) {return callback(buildingObj);}
            });
			}
			else {
				return BC.newBuilding(creatorId, userBuildingName, graph, authoData, callback);
			}
		});
	},

   /**
    * Summary: Generates an id based off counter.
    * Parameters: undefined
    * Returns: String
   **/
	generateBuildingId: function(creatorId) {
    return 'building_' + GLOBAL.flowDB.getAndIncBuildingCounter();
	},
	
   /**
    * Summary: Find all Buildings based off searchJSON.
    * Parameters: searchJSON: obj
    * Returns: undefined. Calls callback with found Buildings or null if none found.
   **/
	find: function(searchJSON, callback) {
		Building.find(searchJSON, function(err, buildingObjs) {
			if(err) {
				console.log("\n--buildingController.js 61 ERR: "+err+"--\n");
				if(Util.exists(callback)) {return callback(null);}
			}
			else if(Util.exists(callback)) {return callback(buildingObjs);}
		});
	},
	
   /**
    * Summary: Find one Building based off searchJSON.
    * Parameters: searchJSON: obj
    * Returns: undefined. Calls callback with found Building or null if none found.
   **/
	findOne: function(searchJSON, callback) {
		//console.log('BuildingController.js line78 :'+JSON.stringify(searchJSON));
		Building.findOne(searchJSON, function(err, buildingObj) {
			//console.log("BC findone: "+JSON.stringify(buildingObj));
			if(err) {
				console.log("\n--buildingController.js 72 ERR: "+err+"--\n");
				if(Util.exists(callback)) {return callback(null);}
			}
			else if(Util.exists(callback)) {return callback(buildingObj);}
		});
	}
};

module.exports = BuildingController;