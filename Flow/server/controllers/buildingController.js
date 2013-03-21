var mongoose = require('mongoose');
var Util = require('../util.js');
var Building = require('../models/buildingModel.js');

var BuildingController = {
	newBuilding: function(creatorId, userBuildingName, graph, authoData, callback) {
		var BC = this;
		var userBuildingId = this.generateBuildingId(6); //11 mill possible canvases
		console.log("buildingController line10: "+userBuildingId);
    
		this.findOne({_creatorId: creatorId, userBuildingId: userBuildingId}, function(buildingObj) {
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
				return BC.newCanvas(creatorId, userBuildingName, graph, authoData, callback);
			}
		});
	},

	generateBuildingId: function(idLen) {
		var chars = 'abcdefghijklmnopqrstuvwxyz0123456789';
		var id = 'building_';
		for(var i = 0; i < idLen; i++) {
			id += chars[Math.round(Math.random()*(chars.length-1))];
		}
		//need to check if id already exists, but it is an async call :(
		return id;
	},
	
	find: function(searchJSON, callback) {
		Building.find(searchJSON, function(err, buildingObjs) {
			if(err) {
				console.log("\n--buildingController.js 61 ERR: "+err+"--\n");
				if(Util.exists(callback)) {return callback(null);}
			}
			else if(Util.exists(callback)) {return callback(buildingObjs);}
		});
	},
	
	findOne: function(searchJSON, callback) {
		console.log('line63 :'+JSON.stringify(searchJSON));
		Building.findOne(searchJSON, function(err, buildingObj) {
			console.log("BC findone: "+JSON.stringify(buildingObj));
			if(err) {
				console.log("\n--buildingController.js 72 ERR: "+err+"--\n");
				if(Util.exists(callback)) {return callback(null);}
			}
			else if(Util.exists(callback)) {return callback(buildingObj);}
		});
	}
};

module.exports = BuildingController;