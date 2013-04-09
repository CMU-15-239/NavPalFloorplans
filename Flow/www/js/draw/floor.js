//floor.js

function importFloor(simpleFloor) {
  var newFloor = null;
  if(util.exists(simpleFloor)) {
    newFloor = new Floor(simpleFloor.name, simpleFloor.imageId, simpleFloor.width, null);
    newFloor.imageScale = simpleFloor.imageScale;
    
    newFloor.globals = importGlobalsContainer(simpleFloor.globals);
    //console.log(newFloor.globals);
    
    if(util.exists(simpleFloor.spaces)) {
      for(var s = 0; s < simpleFloor.spaces.length; s++) {
        newFloor.spaces.push(importSpace(simpleFloor.spaces[s], newFloor.globals));
      }
    }
    
    if(util.exists(simpleFloor.obstacles)) {
      for(var o = 0; o < simpleFloor.obstacles.length; o++) {
        newFloor.obstacles.push(importSpace(simpleFloor.obstacles[o], newFloor.globals));
      }
    }
    
    if(util.exists(simpleFloor.landmarks)) {
      for(var l = 0; l < simpleFloor.landmarks.length; l++) {
        newFloor.landmarks.push(importLandmark(simpleFloor.landmarks[l]));
      }
    }
    
    if(util.exists(simpleFloor.floorConnections)) {
      for(var fc = 0; fc < simpleFloor.floorConnections.length; fc++) {
        newFloor.floorConnections.push(importFloorConnection(simpleFloor.floorConnections[fc]));
      }
    }
    
  }
  
  return newFloor;
}

function Floor(name, imageId, width, canvas) {
  this.name = name;
  this.imageId = imageId;
  this.width = width;
  this.imageScale = 1.0;

  //Space objects
  this.spaces = [];
  this.obstacles = [];
  
  this.landmarks = [];
  this.floorConnections = [];
  
  //Unsorted lines, walls, etc.
  this.globals = new GlobalsContainer(canvas);
}

Floor.prototype.calcScale = function(space, userSqFt) {
  this.imageScale = 1.0;
};

Floor.prototype.drawLandmarks = function() {
	for (var i = 0; i < this.landmarks.length; i+=1 ) {
		this.landmarks[i].draw();
	}
}

Floor.prototype.toOutput = function() {
   var outSpaces = [];
   for(var s = 0; s < this.spaces.length; s++) {
      outSpaces.push(this.spaces[s].toOutput());
   }
   
   var outObstacles = [];
   for(var o = 0; o < this.obstacles.length; o++) {
      outObstacles.push(this.obstacles[o].toOutput());
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
    obstacles: outObstacles,
    landmarks: outLandmarks,
    floorConnections: outFloorConnections,
	  globals: this.globals.toOutput()
   };
};