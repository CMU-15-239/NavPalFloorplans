//floorGraph.js

function importFloorGraph(simpleFloorGraph) {
  if(util.exists(simpleFloorGraph)) {
    var floorGraph = new FloorGraph();
    if(util.exists(simpleFloorGraph.spaceNodes)) {
      for(var s = 0; s < simpleFloorGraph.spaceNodes.length; s++) {
        floorGraph.spaceNodes.push(importSpaceNode(simpleFloorGraph.spaceNodes[s]));
      }
    }
    
    if(util.exists(simpleFloorGraph.pswNodes)) {
      for(var p = 0; p < simpleFloorGraph.pswNodes.length; p++) {
        floorGraph.pswNodes.push(importPswNode(simpleFloorGraph.pswNodes[p])); 
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
function FloorGraph(spaces, callback, callbackVars) {
	this.spaceNodes = [];
	this.pswNodes = [];
  this.spaceType = "space";
  this.pswType = "psw";
	
  if(util.exists(spaces)) {
    for(var s = 0; s < spaces.length; s++) {
      this.addFloorNode(spaces[s]);
    }
  }
	
	if(util.exists(callback)) {callback.apply(callbackVars);}
}


/**
 * Summary: Constructs a list of spaces to represent the floorGraph.
 * Parameters: none
 * Returns: list of spaces
**/
FloorGraph.prototype.toSpaces = function() {
  var result = [];
  for(var s = 0; s < this.spaceNodes.length; s++) {
    var spaceNode = this.spaceNodes[s];
    //console.log(JSON.stringify(spaceNode));
    var space = new Space(spaceNode.walls);
    space.type = spaceNode.spaceType;
    space.label = spaceNode.label;
    for(var d = 0; d < spaceNode.edges.length; d++) {
      var edge = this.getFloorNodeById(spaceNode.edges[d]);
      if(util.exists(edge) && edge.type === this.pswType) {
        space.doors.push(edge.lineRep);
      }
    }
    
    result.push(space);
  }
  
  return result;
};

/**
 * Summary: Converts the FloorGraph object to a simple JSON object (for export)
 * Parameters: undefined
 * Returns: Simple JSON object.
**/
FloorGraph.prototype.toOutput = function() {
	var outSpaceNodes = [];
	for(var s = 0; s < this.spaceNodes.length; s++) {
		outSpaceNodes.push(this.spaceNodes[s].toOutput());
	}
	
	var outPswNodes = [];
	for(var p = 0; p < this.pswNodes.length; p++) {
		outPswNodes.push(this.pswNodes[p].toOutput());
	}
	
	return {
		spaceNodes: outSpaceNodes,
		pswNodes: outPswNodes
	};
};

/**
 * Summary: Gets the PswNode with the 2d line represenation of line.
 * Parameters: line: Line object.
 * Returns: PswNode found or null if none found.
**/
FloorGraph.prototype.getPswNodeByLine = function(line) {
	for(var p = 0; p < this.pswNodes.length; p++) {
		if(this.pswNodes[p].lineRep.equals(line)) {
			return this.pswNodes[p]
		}
	}
	return null;
};

/**
 * Summary: Adds a new FloorNode (SpaceNode) and establishes links to (and creates) PswNodes as necessary.
 * Parameters: space: Space object.
 * Returns: undefined
**/
FloorGraph.prototype.addFloorNode = function(space) {
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
			this.pswNodes.push(newDoor);
		}
	}
	
	
	var spaceNode = new SpaceNode(this.spaceType, space.type, space.label, pswIds, space.walls);
	//TODO: check and make sure the newId function returns in time for adding to psws
	for(var p = 0; p < psws.length; p++) {
		psws[p].edges.push(spaceNode.id);
	}
	this.spaceNodes.push(spaceNode);
};

/**
 * Summary: Gets a FloorNode from an id
 * Parameters: id: String.
 * Returns: undefined
**/
FloorGraph.prototype.getFloorNodeById = function(id) {
	var searchArr = [];
	if(id.indexOf(this.pswType+"_") === 0) {
		searchArr = this.pswNodes;
	}
	else if(id.indexOf(this.spaceType+"_") === 0) {
		searchArr = this.spaceNodes;
	}
	
	for(var n = 0; n < searchArr.length; n++) {
		if(searchArr[n].id === id) {
			return searchArr[n];
		}
	}
	
	return null;
};


FloorGraph.prototype.equals = function(otherFloorGraph) {
  if(util.exists(otherFloorGraph) && util.exists(otherFloorGraph.spaceNodes)
      && otherFloorGraph.spaceNodes.length === this.spaceNodes.length
      && util.exists(otherFloorGraph.pswNodes) 
      && otherFloorGraph.pswNodes.length === this.pswNodes.length) {
    
    for(var s = 0; s < this.spaceNodes.length; s++) {
      var thisSpaceNode = this.spaceNodes[s];
      var otherSpaceNode = otherFloorGraph.getFloorNodeById(thisSpaceNode.id);
      if(!util.exists(otherSpaceNode) || !thisSpaceNode.equals(otherSpaceNode)) {
        console.log("false for: "+s+" "+this.spaceNodes[s].id+" "+otherSpaceNode.id);
        return false;
      }
    }
    
    for(var p = 0; p < this.pswNodes.length; p++) {
     var thisPswNode = this.spaceNodes[p];
      var otherPswNode = otherFloorGraph.getFloorNodeById(thisPswNode.id);
      if(!util.exists(otherPswNode) || !thisPswNode.equals(otherPswNode)) {
        return false;
      }
    }
    
    return true;
  }
  
  return false;
};
