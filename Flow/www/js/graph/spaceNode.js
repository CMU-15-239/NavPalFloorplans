//spaceNode.js

function importSpaceNode(simpleSpaceNode) {
  if(util.exists(simpleSpaceNode)) {
    var spaceNode = new SpaceNode(simpleSpaceNode.type, simpleSpaceNode.spaceType,
                            simpleSpaceNode.label, simpleSpaceNode.edges);
    spaceNode.id = simpleSpaceNode.id;
    
    if(util.exists(simpleSpaceNode.walls)) {
      for(var w = 0; w < simpleSpaceNode.walls.length; w++) {
        spaceNode.walls.push(importLine(simpleSpaceNode.walls[w], false));
      }
    }
    
    return spaceNode;
  }
  
  return null;
}

/**
 * Summary: Constructor for the SpaceNode object.
 * Parameters: spaceType: String, type of space (room, hallway, etc...)
				label: String, label for room (e.g. room number)
				edges: List of Strings (GraphNodes ids)
				walls: List of Line objects, walls of SpaceNode (unordered)
 * Returns: undefined
**/
function SpaceNode(type, spaceType, label, edges, walls) {
	this.spaceType = spaceType;
	this.label = label;
	
	if(util.exists(walls)) {this.walls = walls;}
	else {this.walls = [];}
	
	GraphNode.call(this, type, edges, type);
}

SpaceNode.prototype = new GraphNode();
SpaceNode.prototype.constructor = SpaceNode;

/**
 * Summary: Converts the SpaceNode object to a simple JSON object (for export)
 * Parameters: undefined
 * Returns: Simple JSON object.
**/
SpaceNode.prototype.toOutput = function() {
	var outWalls = [];
	for(var w = 0; w < this.walls.length; w++) {
		outWalls.push(this.walls[w].toOutput());
	}
	
	return {
		type: this.type,
		edges: this.edges,
		id: this.id,
		spaceType: this.spaceType,
		label: this.label,
		walls: outWalls
	};
};

SpaceNode.prototype.equals = function(otherSpaceNode) {
  if(util.exists(otherSpaceNode) && otherSpaceNode.id === this.id
      && otherSpaceNode.type === this.type && util.exists(otherSpaceNode.edges)
      && this.edges.length === otherSpaceNode.edges.length
      && this.spaceType === otherSpaceNode.spaceType 
      && util.exists(otherSpaceNode.walls)
      && this.walls.length === otherSpaceNode.walls.length){
    
    for(var e = 0; e < this.edges.length; e++) {
      if(this.edges[e] !== otherSpaceNode.edges[e]) {
        return false;
      }
    }
    
    for(var w = 0; w < this.walls.length; w++) {
      if(!this.walls[w].equals(otherSpaceNode.walls[w])) {
        return false;
      }
    }
    
    return true;
  }
  
  return false;
};


