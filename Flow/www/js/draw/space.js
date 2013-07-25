//space.js
//Object for categorizing an open space (rooms and hallways)

function importSpace(simpleSpace, globalsContainer)
{
  var newSpace = null;
  if(util.exists(simpleSpace))
  {
    newSpace = new Space();
    newSpace.type = simpleSpace.type;
    newSpace.label = simpleSpace.label;
    
    if(util.exists(simpleSpace.walls))
    {
      for(var w = 0; w < simpleSpace.walls.length; w++)
      {
        var wall = simpleSpace.walls[w];
        var isDoor = wall.isDoor;
        var wall = globalsContainer.importWall(wall);
        
        //console.log(wall);
        //console.log("isDoor: " + isDoor);
        
        if(util.exists(wall))
        {
          wall = newSpace.addWall(wall);
          if(wall.isDoor)
          {
            newSpace.doors.push(wall);
          }
        }
      }
    }
    
    //newSpace.selectPoly = new Polygon($.extend([], newSpace.walls));
  }
  
  return newSpace;
}


/**
 * Summary: Create a new space when we have an enclosed area.
 * Parameters: walls: The walls that define the space.
 * Returns: undefined.
**/
function Space(walls)
{
	// TODO: The following array are not being populated
	//
	// 		- doors
	//		- points
	//		- 
	//
	
	//The doors in the space (if any)
	this.doors = [];

	//The walls that define the space
	this.walls = $.extend([], walls);

	this.points = [];

	//The classification of the space ("room", "hallway", "obstacle", etc.)
	this.type = "";

	//The space's room number (if any)
	this.label = "";

	//Is the space closed?
	this.isClosed = false;

	//A polygon that appears when the user selects the space.
	this.selectPoly = null;

	if(util.exists(walls))
	{
		this.selectPoly = new Polygon(walls);
	}
  
	this.drawPoly = false;
};

Space.prototype.toOutput = function() {
   console.log("**generating door outputs**");
   var outDoors = [];
   for(var d = 0; d < this.doors.length; d++) {
      outDoors[d] = this.doors[d].toOutput();
   }
   
   console.log("**generating wall outputs**");
   var outWalls = [];
   for(var w = 0; w < this.walls.length; w++) {
      outWalls[w] = this.walls[w].toOutput();
   }
   
   console.log("**generating point outputs**");
   var outPoints = [];
   for(var p = 0; p < this.points.length; p++) {
      outPoints[p] = this.points[p].toOutput();
   }
   
   return {
      doors: outDoors,
      walls: outWalls,
      points: outPoints,
      type: this.type,
      label: this.label,
      isClosed: this.isClosed
   }
};

/**
 * Summary: Return whether the classification of the space is "room"
 * Parameters: this
 * Returns: true iff the classification of the space is "room"
**/
Space.prototype.isRoom = function() {
	return this.type === "room";
};

/**
 * Summary: Return whether the classification of the space is "hallway"
 * Parameters: this
 * Returns: true iff the classification of the space is "hallway"
**/
Space.prototype.isHallway = function() {
	return this.type === "hallway";
};

/**
 * Summary: Add a line to the array of walls, which acts as a new wall.
 * Paremters: l: The line to add.
 * Returns: undefined
**/
Space.prototype.addWall = function(wallToAdd) {
	//Check to make sure that the wall being added isn't a duplicate.
	for (var i = 0; i < this.walls.length; i++) {
		if (this.walls[i].equals(wallToAdd)) return this.walls[i];
	}
  
  wallToAdd.p1 = this.addPoint(wallToAdd.p1);
  wallToAdd.p2 = this.addPoint(wallToAdd.p2);
	this.walls.push(wallToAdd);
  
  return wallToAdd;
};

Space.prototype.addPoint = function(pointToAdd) {
	//Check to make sure that the point being added isn't a duplicate
	for (var i = 0; i < this.points.length; i++) {
		if (this.points[i].equals(pointToAdd)) return this.points[i];
	}
	this.points.push(pointToAdd);
  return pointToAdd;
};

/**
 * Summary: Draw the polygon that defines the room on the canvas, if appropriate
 * Parameters: this
 * Returns: undefined
**/
Space.prototype.draw = function() {
	if (this.drawPoly) {
		//Make a new polygon every time to eliminate the possibility of destructively
		//modifying its parameters.
		this.selectPoly = new Polygon(this.walls, 'rgba(51,153,255,.5)');
		this.selectPoly.draw();
	}
	else if (this.type !== "") {
		this.classifiedPoly = new Polygon(this.walls, 'rgba(255, 0, 0, .35)');
		this.classifiedPoly.draw();
	}
}

/**
 * Summary: Find the first wall that is within radius distance of the given point.
 * Parameters: point: the point to find a match for, 
 *	radius: the maximum distance the wall can be away from the point
 * Returns: The first wall that is close to the point if there is one, and null otherwise.
**/
Space.prototype.pointOnWalls = function(point, radius) {
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
 * 		width: ???,
 * 		includeLine: Should we include the lines that define the walls in our check? 
 * Returns: true iff the given point is within the defining walls of the space.
**/
Space.prototype.pointInSpace = function(point, width, includeLine) {
	//console.log("params: "+JSON.stringify(point)+" width: "+width+" includeLine: "+includeLine);
	//If point is very close to a line, then it's only in the space if we should include the walls' lines.
	if (util.exists(this.pointOnWalls(point, 0.5))) {
		return (includeLine === true);
	}
	else {
		//console.log("jere");
		//compile intersecting lines
		var inShapeSegments = [];
		var lastLineIntersected = null;
		var currP1 = null;
		for(var xr = (-1*width); xr < width; xr++) {
			var currRayPt = {x: xr, y: point.y};
			//console.log("checkingPt: "+JSON.stringify(currRayPt));
			var intersectLine = this.pointOnWalls(currRayPt, 0.5);
			if(util.exists(intersectLine)) {
				//console.log("found intersection pt: "+JSON.stringify(currRayPt));
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
						//console.log("interLine: "+JSON.stringify(inShapeSegments[inShapeSegments.length-1]));
					}
					else {
						currP1 = {x:currRayPt.x, y:currRayPt.y};
						//console.log("p1: "+JSON.stringify(currP1))
					}
				}
			}
		}
		
		//console.log(inShapeSegments);
		//determine if point is on one of these intersected lines
		return false;
		/*
		for(var s = 0; s < inShapeSegments.length; s++) {
			if(inShapeSegments[s].pointNearLine(point, 0)) {
				return true;
			}
		}
		return false;
		*/
	}
};

/**
 * Summary: Check whether the given set of lines contain the same walls as this space.
 * Parameters: lines: The set of lines we're checking against.
 * Returns: true iff the given set of lines and this space contain the same walls.
**/
Space.prototype.sameLines = function(lines) {
	var seen = {}
	//If the two sets contain a different number of walls, then it's trivially false.
	if (lines.length != this.walls.length) {
		return false;
	}
	
	
	for (var i = 0; i < this.walls.length; i++) {
		var line = this.walls[i];
		seen[line.toString()] = 0;
	}
	
	for (var i = 0; i < lines.length; i++) {
		var line = lines[i];
		seen[line.toString()] = 1;
	}
	
	for (var i = 0; i < this.walls.length; i++) {
		var line = this.walls[i];
		seen[line.toString()] -= 1;
	}
	
	for (line in seen) {
		if (seen[line] != 0) {
			//console.log("RETURNING FALSE these rooms are different");
			return false;
		}
	}
	
	return true;
};

/**
 * Summary: Check whether the given space and this space contain the same walls.
 * Parameters: room: The room to check against.
 * Returns: true iff the given room and this room contain the same walls.
**/
Space.prototype.sameRoomWalls = function(room) {
	return this.sameLines(room.walls);
};

