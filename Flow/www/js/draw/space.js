//space.js
//Object for categorizing an open space (rooms and hallways)

//create once you have an enclosed space and have all the data for it
//px and py be the pixel point where the user clicks to classify the room
//and this is probably needed for graphNode
function Space(walls) {
	//this.anchor = {x: px, y: py};
	this.doors = [];
	this.walls = $.extend([], walls);
	this.points = [];
	this.type = ""; //"room" or "hallway"
	this.label = ""; //room number
	this.isClosed = false;
	
	//A polygon that appears when the user selects the space.
	this.selectPoly = new Polygon(this.walls);
	this.walls = walls;
	this.drawPoly = false;
};

Space.prototype.isRoom = function() {
	return this.type === "room";
};

Space.prototype.isHallway = function() {
	return this.type === "hallway";
};

/**
 * Summary: Add a line to the array of walls, which acts as a new wall.
 * Paremters: l: The line to add.
 * Returns: undefined
**/
Space.prototype.addWall = function(l) {
	this.walls.push(l);
};

Space.prototype.addPoint = function(p) {
	this.points.push(p);
};

Space.prototype.draw = function() {
	if (this.drawPoly) {
		this.selectPoly = new Polygon(this.walls);
		this.selectPoly.draw();
	}
}

Space.prototype.pointOnWalls = function(point, radius) {
	for(var w = 0; w < this.walls.length; w++) {
		if(this.walls[w].pointNearLine(point, radius)) {
			return this.walls[w];
		}
	}
	return null;
};

Space.prototype.pointInSpace = function(point, width, includeLine) {
	//console.log("params: "+JSON.stringify(point)+" width: "+width+" includeLine: "+includeLine);
	if (util.exists(this.pointOnWalls(point, 0.5))) {
		return (includeLine === true);
	}
	else {
		//console.log("jere");
		//compile intersecting lines
		var inShapeSegments = [];
		var lastLineIntersected = null;
		var currP1 = null;
		for(var xr = 0; xr < width; xr++) {
			var currRayPt = {x: xr, y: point.y};
			//console.log("checkingPt: "+JSON.stringify(currRayPt));
			var intersectLine = this.pointOnWalls(currRayPt, 0.5);
			if(util.exists(intersectLine)) {
				//console.log("found intersection pt: "+JSON.stringify(currRayPt));
				if(!intersectLine.equals(lastLineIntersected)) {
					lastLineIntersected = intersectLine;
					if(util.exists(currP1)) {
						//deep cpy
						inShapeSegments.push(new Line(new Point(currP1.x, currP1.y) , new Point(currRayPt.x, currRayPt.y)));
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
		for(var s = 0; s < inShapeSegments.length; s++) {
			if(inShapeSegments[s].pointNearLine(point, 0)) {
				return true;
			}
		}
		return false;
	}
};

Space.prototype.sameLines = function(lines) {
	var seen = {}
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

Space.prototype.sameRoomWalls = function(room) {
	return this.sameLines(room.walls);
};

