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

/**
 * Summary: Getter for userBuildingId.
 * Parameters: undefined
 * Returns: String
**/
BuildingSchema.methods.getUserBuildingId = function() {
  return this.userBuildingId;
};

/**
 * Summary: Getter for userBuildingName.
 * Parameters: undefined
 * Returns: String
**/
BuildingSchema.methods.getUserBuildingName = function() {
  return this.userBuildingName;
};

/**
 * Summary: Getter for graph.
 * Parameters: undefined
 * Returns: Object
**/
BuildingSchema.methods.getGraph = function() {
  return this.graph;
};

/**
 * Summary: Setter for Graph.
 * Parameters: newGraph: Object
               callback: function
 * Returns: undefined. Calls callback with user or null if failes to save.
**/
BuildingSchema.methods.setGraph = function(newGraph, callback) {
  this.graph = newGraph;
  var user = this;
  this.save(function(err) {
    if(err) {
      console.log("\n--buildingModel.js 30 ERR: "+err+"--\n");
      if(Util.exists(callback)) {return callback(null);}
    }
    else if(Util.exists(callback)) {callback(user);}
	});
};

/**
 * Summary: Getter for authoData.
 * Parameters: undefined
 * Returns: Object
**/
BuildingSchema.methods.getAuthoData = function() {
  return this.authoData;
};

/**
 * Summary: Setter for authoData.
 * Parameters: newAuthoData: Object
               callback: function
 * Returns: undefined. Calls callback with user or null if failes to save.
**/
BuildingSchema.methods.setAuthoData = function(newAuthoData, callback) {
  this.authoData = newAuthoData;
  var user = this;
  this.save(function(err) {
    if(err) {
      console.log("\n--buildingModel.js 45 ERR: "+err+"--\n");
      if(Util.exists(callback)) {return callback(null);}
    }
    else if(Util.exists(callback)) {callback(user);}
	});
};

/**
 * Summary: Extract the data to output. Removes sensitive data (e.g. this._id).
 * Parameters: undefined
 * Returns: {id : String, name : String, graph : Object, authoData : Object}
**/
BuildingSchema.methods.toOutput = function() {
   return {
      id: this.getUserBuildingId(),
      name: this.getUserBuildingName(),
      graph: this.getGraph(),
      authoData: this.getAuthoData()
   };
};

module.exports = mongoose.model('Building', BuildingSchema);