//space.js
//Object for categorizing an open space (rooms and hallways)

//create once you have an enclosed space and have all the data for it
//px and py be the pixel point where the user clicks to classify the room
//and this is probably needed for graphNode
function Space(walls) {
	//this.anchor = {x: px, y: py};
	this.doors = [];
	this.walls = walls;
	this.points = [];
	this.type = ""; //"room" or "hallway"
	this.label = ""; //room number
	this.isClosed = false;
	//A polygon that appears when the user selects the space.
	this.selectPoly = new Polygon(this.walls);
	this.drawPoly = false;
}

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
}

Space.prototype.addPoint = function(p) {
	this.points.push(p);
}

Space.prototype.draw = function() {
	if (this.drawPoly) this.selectPoly.draw();
}


Space.prototype.pointOnLines = function(point, lines) {
	for(var l = 0; l < lines.length; l++) {
		if(lines[l].pointNearLine(point, 0)) {
			return true;
		}
	}
	return false;
}

Space.prototype.pointInShape = function(point, lines, width, height) {
	if(!pointOnLines(point, lines)) {
		var inShapeSegments = [];
		var currP1 = null;
		for(var rx = 0; rx < width; rx++) {
			var currRayPt = {x: rx, y: point.y};
			console.log("checkingPt: "+JSON.stringify(currRayPt));
			if(pointOnLines(currRayPt, lines)) {
				console.log("found intersection pt: "+JSON.stringify(currRayPt));
				if(util.exists(currP1)) {
					inShapeSegments.push(new Line(new Point(currP1.x, currP1.y), new Point(currRayPt.x, currRayPt.y)));
					currP1 = null;
				}
				else {
					currP1 = currRayPt;
					console.log("p1: "+JSON.stringify(currP1));
				}
			}
		}
		console.log(inShapeSegments);
		return pointOnLines(point, inShapeSegments);
	}
	console.log("pt on wall");
	return false;
}

Space.prototype.sameLines = function(lines) {
	var seen = {}
	if (lines.length != this.walls.length) {
		return false;
	}
	
	
	for (var i = 0; i < this.walls.length; i++) {
		var line = lines[i];
		seen[line.toString()] = 0;
	}
	
	for (var i = 0; i < lines.length; i++) {
		var line = lines[i];
		seen[line.toString()] = 1;
	}
	
	for (var i = 0; i < this.walls.length; i++) {
		var line = lines[i];
		seen[line.toString()] -= 1;
	}
	
	for (line in seen) {
		if (seen[line] != 0) {
			console.log(seen[line]);
			return false;
		}
	}
	
	return true;
}

Space.prototype.sameRoomWalls = function(room) {
	return this.sameLines(room.walls);
}

