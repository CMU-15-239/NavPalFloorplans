//floorGraph.js

function importFloorGraph(simpleFloorGraph) {
  if(util.exists(simpleFloorGraph)) {
    var floorGraph = new FloorGraph();
    if(util.exists(simpleFloorGraph.spaces)) {
      for(var s = 0; s < simpleFloorGraph.spaces.length; s++) {
        floorGraph.spaces.push(importSpaceNode(simpleFloorGraph.spaces[s]));
      }
    }
    
    if(util.exists(simpleFloorGraph.psws)) {
      for(var p = 0; p < simpleFloorGraph.psws.length; p++) {
        floorGraph.psws.push(importPswNode(simpleFloorGraph.psws[p])); 
      }
    }
    
    return floorGraph;
  }
  
  return null;
}

/**
 * Summary: Constructor for the FloorGraph object.
 * Parameters: spaces: The list of space objects created in the drawing tool,
				callback: Function to callback after the constructor executes. 
				(To notify something when the floorGraph has been created (e.g load wheel))
				callbackVars: Inputs for callback.
 * Returns: undefined
**/
function FloorGraph(floor, callback, callbackVars) {
	this.name;
  this.imageId;
  this.imageScale = 1.0;
  
  this.spaces = [];
	this.psws = [];
	this.floorConnections = [];
  this.landmarks = [];
  
  this.spaceType = "space";
  this.pswType = "psw";
  this.floorConnectionType = "floorConnection";
  this.landmarkType = "landmark";
	
  if(util.exists(floor)) {
    this.imageId = floor.imageId;
    this.imageScale = floor.imageScale;
    if(util.exists(floor.spaces)) {
      for(var s = 0; s < floor.spaces.length; s++) {
        this.addSpaceNode(floor.spaces[s]);
      }
    }
  }
	
	if(util.exists(callback)) {callback.apply(callbackVars);}
}


/**
 * Summary: Converts the FloorGraph object to a simple JSON object (for export)
 * Parameters: undefined
 * Returns: Simple JSON object.
**/
FloorGraph.prototype.toOutput = function() {
	var outSpaces = [];
	for(var s = 0; s < this.spaces.length; s++) {
		outSpaces.push(this.spaces[s].toOutput());
	}
	
	var outPsws = [];
	for(var p = 0; p < this.psws.length; p++) {
		outPsws.push(this.psws[p].toOutput());
	}
  
  var outFloorConnections = [];
  for(var fc = 0; fc < this.floorConnections.length; fc++) {
    outFloorConnections.push(this.floorConnections[fc].toOutput());
  }
  
  var outLandmarks = [];
  for(var l = 0; l < this.landmarks.length; l++) {
    outLandmarks.push(this.landmarks[l].toOutput());
  }
	
	return {
		spaces: outSpaces,
		psws: outPsws,
    floorConnections: outFloorConnections,
    landmarks: outLandmarks
	};
};

/**
 * Summary: Gets the PswNode with the 2d line represenation of line.
 * Parameters: line: Line object.
 * Returns: PswNode found or null if none found.
**/
FloorGraph.prototype.getPswNodeByLine = function(line) {
	for(var p = 0; p < this.psws.length; p++) {
		if(this.psws[p].lineRep.equals(line)) {
			return this.psws[p]
		}
	}
	return null;
};

/**
 * Summary: Adds a new SpaceNode and establishes links to (and creates) PswNodes as necessary.
 * Parameters: space: Space object.
 * Returns: undefined
**/
FloorGraph.prototype.addSpaceNode = function(space) {
	//first check and add doors
	var psws = [];
	var pswIds = [];
	for(var d = 0; d < space.doors.length; d++) {
		var lineRep = space.doors[d];
		var existingDoor = this.getPswNodeByLine(lineRep);
		if(util.exists(existingDoor)) {
			psws.push(existingDoor);
			pswIds.push(existingDoor.id);
		}
		else {
			//TODO: check and make sure the newId function returns in time for adding to pswIds
			var newDoor = new PswNode("psw", "door", null, lineRep);
			psws.push(newDoor);
			pswIds.push(newDoor.id);
			this.psws.push(newDoor);
		}
	}
	
	
	var spaceNode = new SpaceNode(this.spaceType, space.type, space.label, pswIds, space.walls);
	//TODO: check and make sure the newId function returns in time for adding to psws
	for(var p = 0; p < psws.length; p++) {
		psws[p].edges.push(spaceNode.id);
	}
	this.spaces.push(spaceNode);
};

/**
 * Summary: Gets a FloorNode from an id
 * Parameters: id: String.
 * Returns: undefined
**/
FloorGraph.prototype.getFloorNodeById = function(id) {
	var searchArr = [];
	if(id.indexOf(this.pswType+"_") === 0) {
		searchArr = this.psws;
	}
	else if(id.indexOf(this.spaceType+"_") === 0) {
		searchArr = this.spaces;
	}
	
	for(var n = 0; n < searchArr.length; n++) {
		if(searchArr[n].id === id) {
			return searchArr[n];
		}
	}
	
	return null;
};


FloorGraph.prototype.equals = function(otherFloorGraph) {
  if(util.exists(otherFloorGraph) && util.exists(otherFloorGraph.spaces)
      && otherFloorGraph.spaces.length === this.spaces.length
      && util.exists(otherFloorGraph.psws) 
      && otherFloorGraph.psws.length === this.psws.length) {
    
    for(var s = 0; s < this.spaces.length; s++) {
      var thisSpaceNode = this.spaces[s];
      var otherSpaceNode = otherFloorGraph.getFloorNodeById(thisSpaceNode.id);
      if(!util.exists(otherSpaceNode) || !thisSpaceNode.equals(otherSpaceNode)) {
        console.log("false for: "+s+" "+this.spaces[s].id+" "+otherSpaceNode.id);
        return false;
      }
    }
    
    for(var p = 0; p < this.psws.length; p++) {
     var thisPswNode = this.spaces[p];
      var otherPswNode = otherFloorGraph.getFloorNodeById(thisPswNode.id);
      if(!util.exists(otherPswNode) || !thisPswNode.equals(otherPswNode)) {
        return false;
      }
    }
    
    return true;
  }
  
  return false;
};
