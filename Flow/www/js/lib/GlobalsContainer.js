function importGlobalsContainer(simpleGlobalsContainer) {
  var newGlobalsContainer = null;
  if(util.exists(simpleGlobalsContainer)) {
    newGlobalsContainer = new GlobalsContainer(null);
    if(util.exists(simpleGlobalsContainer.points)) {
      for(var p = 0; p < simpleGlobalsContainer.points.length; p++) {
        newGlobalsContainer.addPoint(importPoint(simpleGlobalsContainer.points[p]));
      }
    }
    
    if(util.exists(simpleGlobalsContainer.walls)) {
      for(var w = 0; w < simpleGlobalsContainer.walls.length; w++) {
        var wall = simpleGlobalsContainer.walls[w];
        newGlobalsContainer.importWall(wall);
      }
    }
  }
  
  return newGlobalsContainer;
};

var GlobalsContainer = function() {
	this.canvas = null;
	this.walls = [];
	//Points stored as real-world coordinates
	this.points = [];
	this.view = null;
	//console.log("HERE");
	//if(util.exists(stateManager)) {
	//	this.view = new ManipulationAreaView(stateManager.canvas.x, stateManager.canvas.y, 1.05);
	//}
	this.preprocessedText = [];
	this.snapRadius = 15;
	this.image;
}

GlobalsContainer.prototype.setCanvas = function(canvas) {
	if (this.view === null && canvas !== undefined) {
		this.canvas = canvas;
		this.view = new ManipulationAreaView(this.canvas.x, this.canvas.y, 1.05);
	}
}

GlobalsContainer.prototype.toOutput = function() {
  var outWalls = [];
  for(var w = 0; w < this.walls.length; w++) {
    outWalls.push(this.walls[w].toOutput());
  }
  
  var outPoints = [];
  for(var p = 0; p < this.points.length; p++) {
    outPoints.push(this.points[p].toOutput());
  }
  
  return {
    walls: outWalls,
    points: outPoints
  };
};

GlobalsContainer.prototype.drawPoints = function() {
	for (var i = 0; i < this.points.length; i++) {
		this.points[i].draw();
	}
}

GlobalsContainer.prototype.drawWalls = function() {
	for (var i = 0; i < this.walls.length; i++) {
		this.walls[i].draw();
	}
}

GlobalsContainer.prototype.drawLandmarks = function() {
	for (var i = 0; i < stateManager.currentFloor.landmarks.length; i += 1) {
		stateManager.currentFloor.landmarks[i].draw();
	}
}

GlobalsContainer.prototype.addPoint = function(pointToAdd) {
	//Check to make sure that the point being added isn't a duplicate
	for (var i = 0; i < this.points.length; i++) {
		if (this.points[i].equals(pointToAdd)) return;
	}
	this.points.push(pointToAdd);
};

GlobalsContainer.prototype.importWall = function(wall) {
  if(util.exists(wall)) {
    return this.addWall(importLine(wall));
  }
  
  return null;
};

GlobalsContainer.prototype.addWall = function(wallToAdd) {
	//Check to make sure that the wall being added isn't a duplicate.
	for (var i = 0; i < this.walls.length; i++) {
		if (this.walls[i].equals(wallToAdd)) {
      //console.log("Tried to add a wall that already exists, no problem though");
      if(wallToAdd.isDoor) {
        this.walls[i].isDoor = true;
      }
      return this.walls[i];
    }
	}
  
  var p1Idx = this.indexOfPoint(wallToAdd.p1);
	if (p1Idx === -1) {
    this.points.push(wallToAdd.p1);
  } else {
    wallToAdd.p1 = this.points[p1Idx];
  }
  
	var p2Idx = this.indexOfPoint(wallToAdd.p2);
	if (p2Idx === -1) {
    this.points.push(wallToAdd.p2);
  } else {
    wallToAdd.p2 = this.points[p2Idx];
  }
  
	this.walls.push(wallToAdd);
  
  return wallToAdd;
};

GlobalsContainer.prototype.pointExists = function(point) {
	return this.indexOfPoint(point) !== -1;
};

GlobalsContainer.prototype.indexOfPoint = function(point) {
  for (var i = 0; i < this.points.length; i++) {
		var curPoint = this.points[i];
		if (curPoint.equals(point)) {return i;}
	}
	return -1;
};

GlobalsContainer.prototype.duplicatePoint = function(point) {
	for (var i = 0; i < this.points.length; i++) {
		var curPoint = this.points[i];
		if (curPoint.equals(point)) return curPoint;
	}
	return null;
}

GlobalsContainer.prototype.isWallDuplicate = function(wallToCheck) {
	return this.indexOfWall(wallToCheck);
};

GlobalsContainer.prototype.indexOfWall = function(wallToCheck) {
  var index = this.walls.indexOf(wallToCheck);
	for (var i = 0; i < this.walls.length; i++) {
		if (i == index) continue;
		if (this.walls[i].equals(wallToCheck)) return true;
	}
	return false;
};

GlobalsContainer.prototype.removeWall = function(wallToRemove, shouldRemoveIsolatedPoints) {
	var index = this.walls.indexOf(wallToRemove);
	if (index >= 0) {
		if (shouldRemoveIsolatedPoints) {
			if (this.degree(wallToRemove.p1) === 1) this.removePoint(wallToRemove.p1);
			if (this.degree(wallToRemove.p2) === 1) this.removePoint(wallToRemove.p2);
		}
		this.walls.splice(index, 1);
	}
}

GlobalsContainer.prototype.removePoint = function(pointToRemove) {
	var index = this.points.indexOf(pointToRemove);
	if (index >= 0) this.points.splice(index, 1);
}

GlobalsContainer.prototype.degree = function(point) {
	var degree = 0;
	for (var i = 0; i < this.walls.length; i++) {
		var curWall = this.walls[i];
		if (curWall.p1.equals(point)) degree += 1;
		if (curWall.p2.equals(point)) degree += 1;
	}
	return degree;
}