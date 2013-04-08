//floor.js

function Floor(name, imageId, width) {
  this.name = name;
  this.imageId = imageId;
  this.width = width;
  this.imageScale = 1.0;

  //Space objects
  this.spaces = [];
  this.obstacles = [];
  
  this.landmarks = [];
  this.floorConnections = [];
  
  StateManager.call(this);
}

Floor.prototype = new StateManager();
Floor.prototype.constructor = Floor;

Floor.prototype.calcScale = function(space, userSqFt) {
  this.imageScale = 1.0;
};

Floor.prototype.toOutput = function() {
   var outSpaces = [];
   for(var s = 0; s < this.spaces.length; s++) {
      outSpaces.push(this.spaces[s].toOutput());
   }
   
   var outObsticals = [];
   for(var o = 0; o < this.obsticals.length; o++) {
      outObsticals.push(this.obsticals[o].toOutput());
   }
   
   var outLandmarks = [];
   for(var l = 0; l < this.landmarks.length; l++) {
      outLandmarks.push(this.landmarks[l].toOutput());
   }
   
   var outFloorConnections = [];
   for(var f = 0; f < this.floorConnections.length; f++) {
      outFloorConnections.push(this.floorConnections[f].toOutput());
   }
   
   return {
      name: this.name,
      imageId: this.imageId,
      imageScale: this.imageScale,
      spaces: outSpaces,
      obsticals: outObsticals,
      landmarks: outLandmarks,
      floorConnections: outFloorConnections      
   };
};