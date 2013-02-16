var rooms = [];
var targetPoint;
var visitedPoints = {};

// Given a set of lines, return a set of rooms
function detectRooms(lines) {
	//alert();
	for (i = 0; i < lines.length; i++) {
		console.log("Is line : " + i + " part of a room");
		searchRoom(lines[i]);
		
	}

}

function searchRoom(line) {
	targetPoint = line.p2;
	var route = new Array();
	visitedPoints = {}
	if (followWalls(true, line.p1, line, route)) {
		console.log("FOUND ROOM");
		//var room = makeRoom(route);
	}
	
	//followWalls(false, line.p1, line, route);
}


// A simple depth first search, proiritizing clockwise,
// or counter clockwise traversal
function followWalls(counterClock, point, line, route) {

	// Add this most recent line to our route
	//route.push(line);
	visitedPoints[point.toString()] = true;
	
	// Base Case
	if (point.equals(targetPoint)) {
		return true;
	}	
	
	
	var edges = getEdgeNeighbors(point, line);
	//console.log("Edge count " + edges.length);
	sortEdges(counterClock, edges, point, line);
	
	for (var i = 0; i < edges.length; i ++) {
		var newLine = edges[i];
		
		// See if this point is unexplored
		var p1 = visitedPoints[newLine.p1.toString()];
		var p2 = visitedPoints[newLine.p2.toString()];
		//console.log(p1);
		//console.log(p2);
		if (!(p1 == true) || !(p2 == true)) {
			var newPoint;
			if (!p1) {
				newPoint = newLine.p1;
			}
			else {
				newPoint = newLine.p2;
			}
			route.push(newLine);
			
			//console.log("Traversing a line...");
			
			// We found a route back to our target
			if (followWalls(counterClock, newPoint, newLine, route)) {
				return true;
			}
			
			// We didn't find a route, let's backtrack
			route.pop();
		}
	}
	
	// If we make it back to the start
	if (route.length == 0) {
		return false;
	}
	
}

// This can be more efficient some day...
function getEdgeNeighbors(point, includedLine) {
	var edges = [];
	
	for (var i = 0; i < ALL_WALLS.length; i++) {
		var line = ALL_WALLS[i];
		if (line.p1 == point || line.p2 == point) {
			if (line != includedLine) {
				edges.push(line);
			}
		}
	}
	
	return edges;
}

function sortEdges(counterClock, edges, point, line) {
	if (edges.length == 0) {
		return;
	}
	
	var orderedEdges = [];
	// Selection sort
	for (var count = 0; count < edges.length; count++) {
		var closestLine = undefined;
		var index = 0;
		for (var i = 0; i < edges.length; i ++) {
			var edge = edges[i];
			
			// True if edge < closestLine
			if (shorterRotation(counterClock, point, line, edge, closestLine)) {
				closestLine = edge;
				index = count;
			}
		}
		
		orderedEdges.push(closestLine);
		// at position index, remove 1 element
		edges = edges.splice(index, 1);
		
	}
	
	
	return orderedEdges;
}

// returns true if a is closer to line than b, moving cw, or ccw
function shorterRotation(counterClock, point, line, a, b) {

	// Assume we are going ccw, and return counterClock if a is closer	
	var pc = point;
	var p0 = line.otherPoint(point);
	var pa = a.otherPoint(point);
	var pb = b.otherPoint(point);
	
	// Determine which quadrant each line is in
	var q0 = determineQuad(pc, p0);
	var qa = determineQuad(pc, pa);
	var qb = determineQuad(pc, pb);
	
	// Case 1: all three same quad
	
	// Case 2: all three all diff quad
	
	// Case 3: a and b same quad
	
	// Case 4: a and line same quad
	
	// Case 5: b and line same quad
	
}

// Return which quadrant the line lies in 
// (round to lower quad, if lines on axis)
function determineQuad(pc, p) {
	
	// Quad 1 or 4
	if (pc.x < p.x) {
		if (pc.y <= p.y) { // Tie goes to q1
			return 1;
		}
		else { 
			return 4;
		}
	}
	// Quad 2 or 3
	else if (pc.x > p.x) {
		if (pc.y <= p.y) { // Tie goes to q2
			return 1;
		}
		else {
			return 3;
		}	
	}
	// Y axis
	else { 
		if (pc.y < p.y) { // round towards lower quad
			return 1;
		}
		return 3;
	}
}

function closerToZeroAngle(quad, a, b) {
	
}

