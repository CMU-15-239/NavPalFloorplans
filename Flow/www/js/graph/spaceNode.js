/*
spaceNode.js
By Vansi Vallabhaneni
*/

/**
  * Imports a space node, for debugging, is outdated
**/
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
 * Parameters: type: String, type of floor node (e.g. space, psw, floorConnection)
        spaceType: String, type of space (room, hallway, etc...)
				label: String, label for room (e.g. room number)
				edges: List of Strings (GraphNodes ids)
				walls: List of Line objects, walls of SpaceNode (unordered)
 * Returns: undefined
**/
function SpaceNode(type, spaceType, label, edges, walls, width) {
	this.spaceType = spaceType;
	this.label = label;
  this.grid = null;
	
	if(util.exists(walls)) {
    this.walls = walls;
    this.grid = new Grid(this, width);
  }
	else {this.walls = [];}
	
	FloorNode.call(this, type, edges, type);
}

SpaceNode.prototype = new FloorNode();
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

/**
  * Summary: Adds an edge to this space node.
  * Parameters: otherFloorNode: FloorNode
  * Returns: undefined
**/
SpaceNode.prototype.addEdge = function(otherFloorNode) {
  FloorNode.prototype.addEdge.call(this, otherFloorNode);
  this.grid.addEdge(otherFloorNode);
};

/**
 * Summary: Find the first wall that is within radius distance of the given point.
 * Parameters: point: the point to find a match for, 
 *	radius: the maximum distance the wall can be away from the point
 * Returns: The first wall that is close to the point if there is one, and null otherwise.
**/
SpaceNode.prototype.pointOnWalls = function(point, radius) {
	for(var w = 0; w < this.walls.length; w++) {
		if(this.walls[w].pointNearLine(point, radius)) {
			return this.walls[w];
		}
	}
	return null;
};

/**
 * Summary: Check whether the given point is within the defining walls of the space.
 * Parameters: point: the point we're checking, 
 * 		width: int,
 * 		includeLine: Should we include the lines that define the walls in our check? 
 * Returns: true iff the given point is within the defining walls of the space.
**/
SpaceNode.prototype.pointInSpaceNode = function(point, width, includeLine) {
	//console.log("params: "+JSON.stringify(point)+" width: "+width+" includeLine: "+includeLine);
	//If point is very close to a line, then it's only in the space if we should include the walls' lines.
	if (util.exists(this.pointOnWalls(point, 0.5))) {
		return (includeLine === true);
	}	else {
		var inShapeSegments = [];
		var lastLineIntersected = null;
		var currP1 = null;
		for(var xr = 0; xr < width; xr++) {
			var currRayPt = {x: xr, y: point.y};
			var intersectLine = this.pointOnWalls(currRayPt, 0.5);
			if(util.exists(intersectLine)) {
				if(!intersectLine.equals(lastLineIntersected)) {
					lastLineIntersected = intersectLine;
					if(util.exists(currP1)) {
						//deep cpy
						var inShapeSegment = new Line(new Point(currP1.x, currP1.y) , new Point(currRayPt.x, currRayPt.y));
						inShapeSegments.push(inShapeSegment);
						if(inShapeSegment.pointNearLine(point, 0.5)) {
							return true;
						}
						currP1 = null;
					}
					else {
						currP1 = {x:currRayPt.x, y:currRayPt.y};
					}
				}
			}
		}
		
		return false;
	}
};

/**
  * Checks Equality, for debugging, is outdated
**/
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
