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
	this.name = floor.name;
  this.imageId = floor.imageId;
  this.imageScale = floor.imageScale;
  
  this.spaces = [];
	this.psws = [];
	this.floorConnections = [];
  this.landmarks = [];
  
  this.typeForSpaceNode = "space";
  this.typeForPswNode = "psw";
  this.typeforFloorConnectionNode = "floorConnection";
  this.typeforLandmarkNode = "landmark";
	
  if(util.exists(floor)) {
    this.imageId = floor.imageId;
    this.imageScale = floor.imageScale;
    this.width = floor.width;
    if(util.exists(floor.spaces)) {
      for(var s = 0; s < floor.spaces.length; s++) {
        this.addSpaceNode(floor.spaces[s]);
      }
    }
    
    if(util.exists(floor.landmarks)) {
      for(var l = 0; l < floor.landmarks.length; l++) {
        this.addLandmarkNode(floor.landmarks[l]);
      }
    }
  }
	
  this.id = "floorGraph_"+JSON.stringify(this).hashCode();
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
    name: this.name,
    imageId: this.imageId,
    imageScale: this.imageScale,
    
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
  if(space.type === "room" || space.type === "hallway") {
    //first check and add doors
    //var psws = [];
    //var pswIds = [];
    var spaceNode = new SpaceNode(this.typeForSpaceNode, space.type, space.label, [], space.walls);
    for(var d = 0; d < space.doors.length; d++) {
      var lineRep = space.doors[d];
      var door = this.getPswNodeByLine(lineRep);
      if(!util.exists(door)) {
        //TODO: check and make sure the newId function returns in time for adding to pswIds
        door = new PswNode(this.typeForPswNode, "door", null, lineRep);
        this.psws.push(door);
      }
      
      //psws.push(newDoor);
      //pswIds.push(newDoor.id);
      door.addEdge(spaceNode);
    }
    
    this.spaces.push(spaceNode);
  }
};

/**
 * Summary: Adds a new LandmarkNode.
 * Parameters: landmark: Landmark object.
 * Returns: undefined
**/
FloorGraph.prototype.addLandmarkNode = function(landmark) {
  if(util.exists(landmark)) {
    var spaceNode = null;
    for(var s = 0; s < this.spaces.length; s++) {
      if(this.spaces[s].pointInSpaceNode(landmark.pointRep, this.width, true)) {
        spaceNode = this.spaces[s];
        break;
      }
    }
  
    var landmarkNode = new LandmarkNode(this.typeforLandmarkNode, landmark.label, 
                            landmark.description, [], landmark.pointRep);
    
    if(util.exists(spaceNode)) {spaceNode.addEdge(landmarkNode);}
    
    this.landmarks.push(landmarkNode);
  }
};

FloorGraph.prototype.addFloorConnectionNode = function(floorConnection, nodeId) {
  if(util.exists(floorConnection)) {
    var spaceNode = null;
    for(var s = 0; !util.exists(spaceNode) && s < this.spaces.length; s++) {
      if(this.spaces[s].pointInSpaceNode(floorConnection.pointRep, this.width, true)) {
        spaceNode = this.spaces[s];
        break;
      }
    }
    
    var floorConnectionNode = new FloorConnectionNode(this.typeforFloorConnectionNode,
                                    floorConnection.floorConnectionType, floorConnection.label,
                                    [], floorConnection.pointRep);
    if(util.exists(nodeId)) {floorConnectionNode.id = nodeId;}
    if(util.exists(spaceNode)) {
      
      spaceNode.addEdge(floorConnectionNode);
      if(this.name === "1") {
        var sIdx = spaceNode.edges.indexOf(floorConnectionNode.id);
        var fIdx = floorConnectionNode.edges.indexOf(spaceNode.id);
        console.log("floorConnection edge success: " + (sIdx !== -1)
          + " " + (fIdx !== -1));
      }
    }
    
    this.floorConnections.push(floorConnectionNode);
  }
};

/**
 * Summary: Gets a FloorNode from an id
 * Parameters: id: String.
 * Returns: undefined
**/
FloorGraph.prototype.getFloorNodeById = function(id) {
	var searchArr = [];
	if(id.indexOf(this.typeForPswNode + "_") === 0) {
		searchArr = this.psws;
	}	else if(id.indexOf(this.typeForSpaceNode + "_") === 0) {
		searchArr = this.spaces;
	} else if(id.indexOf(this.typeOfFloorConnectionNode + "_") === 0) {
    searchArr = this.floorConnections;
  }else if(id.indexOf(this.typeForLandmarkNode + "_") === 0) {
    searchArr = this.landmarks;
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
      && otherFloorGraph.name === this.name
      && otherFloorGraph.spaces.length === this.spaces.length
      && util.exists(otherFloorGraph.psws) 
      && otherFloorGraph.psws.length === this.psws.length
      && util.exists(otherFloorGraph.floorConnections)
      && otherFloorGraph.floorConnections.length === this.floorConnections.length
      && util.exists(otherFloorGraph.landmarks) 
      && otherFloorGraph.landmarks.length === this.landmarks.length) {
    
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
    
    for(var fc = 0; fc < this.floorConnections.length; fc++) {
      var thisFloorConnectionNode = this.floorConnections[l];
      var otherFloorConnectionNode = otherFloorGraph.getFloorNodeById(thisFloorConnectionNode.id);
      if(!util.exists(otherFloorConnectionNode) 
          || !thisFloorConnectionNode.equals(otherFloorConnectionNode)) {
        return false;
      }
    }
    
    for(var l = 0; l < this.psws.length; l++) {
      var thisLandmarkNode = this.spaces[p];
      var otherLandmarkNode = otherFloorGraph.getFloorNodeById(thisLandmarkNode.id);
      if(!util.exists(otherLandmarkNode) || !thisLandmarkNode.equals(otherLandmarkNode)) {
        return false;
      }
    }
    
    return true;
  }
  
  return false;
};
