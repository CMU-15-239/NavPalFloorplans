var rooms = [];
var targetPoint;
var visitedPoints = {};

// Given a set of lines, return a set of rooms
function detectRooms(lines) {
	//alert();
	rooms = [];
	
	for (i = 0; i < lines.length; i++) {
        //console.log("Is line : " + i + " part of a room");
		searchRoom(lines[i]);
		
	}

}

function searchRoom(line) {
	targetPoint = line.p2;
	var route = new Array();
	visitedPoints = {}
	if (followWalls(true, line.p1, line, route)) {
		var newRoom = new Space(route);
		
		console.log("FOUND ROOM");
		
		var found = false;
		for (var i = 0; i < rooms.length; i ++) {
			if (rooms[i].sameRoomWalls(newRoom)) {
				found = true;
				console.log("Not unique");
			}
		}
		
		if (!found) {
			rooms.push(newRoom);
		}
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
	//sortEdges(counterClock, edges, point, line);
	
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
	for (var count = 0; count < edges.length; count++) {
		console.log("Edges enter: " + edges[count].toString());
	}
	
	/*
	if (edges.length == 0) {
		return;
	}*/
	
	var orderedEdges = [];
	// Selection sort
	for (var count = 0; count < edges.length; count++) {
		var closestLine = edges[0];
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
	
	/*
	for (var count = 0; count < orderedEdges.length; count++) {
		console.log("Edges exit: " + orderedEdges[count].toString());
	}*/
	edges = orderedEdges;
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
	
	var vecAX = pa.x - pc.x;
	var vecAY = pa.y - pc.y;
	var vecBX = pb.x - pc.x;
	var vecBY = pb.y - pc.y;
	var vecLX = p0.x - pc.x;
	var vecLY = p0.y - pc.y;
	
	if (vecAX == 0) {
		vecAX = epsilon;
	}
	if (vecBX == 0) {
		vecBX = epsilon;
	}
	if (vecLX == 0) {
		vecLX = epsilon;
	}
	
	var thetaA = Math.atan(vecAY / vecAX);
	var thetaB = Math.atan(vecBY / vecBX);
	var thetaLine = Math.atan(vecLY / vecLX);
	
	if (thetaA < thetaLine) {
		thetaA += 2 * Math.pi;
	}
	if (thetaB < thetaLine) {
		thetaB += 2 * Math.pi;
	}
	
	if (thetaA < thetaB) {
		return counterClock;
	}
	return !counterClock;
	
	
	
	/*
	// Case 1: all three same quad
	if (q0 == qa && q0 == qb) {
		// Line is closer to theta = 0
		if (closerToZeroAngle(q0, line, a)) {
			if (closerToZeroAngle(q0, a, b)) { // a closer than b
				return counterClock // TRUE
			}
			return !counterClock // FALSE
		}
		// a is closer to theta = 0
		else {
			if (
		}
	}
	
	// Case 2: all three all diff quad
	else if (q0 != qa && q0 != qb && qa != qb){

	}

	// Case 3: a and line same quad
	else if (q0 == qa){
	
	}
	
	// Case 4: b and line same quad
	else if (q0 == qb) {
	
	}
	
	// Case 5: a and b same quad
	else {
	
	}
	*/
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

