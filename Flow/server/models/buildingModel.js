var mongoose = require('mongoose');
var User = require('./userModel.js');
var Util = require('../util.js');

var BuildingSchema = new mongoose.Schema({
	_creatorId: String,
  userBuildingId: String,
  userBuildingName: String,
	graph: Object,
  authoData: Object
});

BuildingSchema.methods.getUserBuildingId = function() {
  return this.userBuildingId;
};

BuildingSchema.methods.getUserBuildingName = function() {
  return this.userBuildingName;
};

BuildingSchema.methods.getGraph = function() {
  return this.graph;
};

BuildingSchema.methods.setGraph = function(newGraph, callback) {
  this.graph = newGraph;
  this.save(function(err) {
    if(err) {
      console.log("\n--buildingModel.js 30 ERR: "+err+"--\n");
      if(Util.exists(callback)) {return callback(false);}
    }
    else if(Util.exists(callback)) {callback(true);}
	});
};

BuildingSchema.methods.getAuthoData = function() {
  return this.authoData;
};

BuildingSchema.methods.setAuthoData = function(newAuthoData, callback) {
  this.authoData = newAuthoData;
  this.save(function(err) {
    if(err) {
      console.log("\n--buildingModel.js 45 ERR: "+err+"--\n");
      if(Util.exists(callback)) {return callback(false);}
    }
    else if(Util.exists(callback)) {callback(true);}
	});
};

module.exports = mongoose.model('Building', BuildingSchema);