//var rooms = [];
var targetPoint;
var visitedPoints = {};

// Given a set of lines, return a set of rooms
function detectRooms(lines) {
	//alert();
	var rooms = [];
	
	for (var i = 0; i < lines.length; i++) {
        //console.log("Is line : " + i + " part of a room");
		searchRoom(lines[i], rooms);
	}
	
	ALL_CLOSED_ROOMS = rooms;
	//console.log(rooms.length + " rooms found!");
}

function distance(lines) {
	sum = 0;
	
	for (var i = 0; i < lines.length; i ++) {
		sum += lines[i].magnitutde();
	}
	
	return sum;
}
//var theta = 0;

function sumAngles(lines, counterClock) {
	sum = 0;
	
	for (var i = 0; i < lines.length; i ++) {
		var line1 = lines[i];
		var line0;
		if (i == 0) {
			line0 = lines[lines.length-1];
		}
		else {
			line0 = lines[i-1];
		}
		
		var p;
		if (line0.p1.equals(line1.p1) || 
			line0.p1.equals(line1.p2)) {
			p = line0.p1;
		}
		else {
			p = line0.p2;
		}
		
		sum += angleBetween(counterClock, p, line0, line1);
	}
	
	return sum;
}

function searchRoom(line, rooms) {
	var validRoute = false;
	var validRevRoute = false;
	targetPoint = line.p2;
	
	//theta = 0;
	var route = new Array();
	visitedPoints = {}
	if (followWalls(true, line.p1, line, route)) {
		route.push(line);
		takeRoute = true;
		//console.log("THETA:  " + 180.0*sumAngles(route, true)/Math.PI);
		//console.log("THETA: " + 180* theta/ Math.PI);
	}
	
	
	visitedPoints = {}
	var revRoute = new Array();
	//targetPoint = line.p1
	if (followWalls(false, line.p1, line, revRoute)) {
		revRoute.push(line);
		validRevRoute = true;
	}
	
	// If the ccw traversal is slower, to cw
	//console.log("original: "+distance(route) + "  , rev " + distance(revRoute)); 
	if (distance(route) > distance(revRoute)) {
		route = revRoute;
	}
	
	if (validRoute || validRevRoute) {
		
		var newRoom = new Space(route);
		
		//console.log("FOUND ROOM");
		
		var found = false;
		for (var i = 0; i < rooms.length; i ++) {
			//console.log("Room " + i);
			for( var j = 0; j < rooms[i].walls.length; j ++) {
				//console.log(rooms[i].walls[j].toString());
			}
			
			if (rooms[i].sameRoomWalls(newRoom)) {
				found = true;
				//console.log("Not unique");
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
	if (point == false) {
		//console.log("point is false??");
	}
	//console.log(point);
	if (point.equals(targetPoint)) {
		return true;
	}	
	
	
	var edges = getEdgeNeighbors(point, line);
	//console.log("Edge count " + edges.length);
	edges = sortEdges(counterClock, edges, point, line);
	
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
			//theta += angleBetween(counterClock, point, line, newLine);
			
			//console.log("Traversing a line...");
			
			// We found a route back to our target
			if (followWalls(counterClock, newPoint, newLine, route)) {
				return true;
			}
			
			// We didn't find a route, let's backtrack
			route.pop();
			//theta -= angleBetween(counterClock, point, line, newLine);
		}
	}
	
	// If we make it back to the start
	if (route.length == 0) {
		return false;
	}
	
}

function angleBetween(counterClock, point, line0, line1) {
	
	//console.log("line 0 " + line0.toString());
	//console.log("line 1 " + line1.toString());
	
	var pc = point;
	var p0 = line0.otherPoint(point);
	var p1 = line1.otherPoint(point);
	
	var q0 = determineQuad(pc, p0);
	var q1 = determineQuad(pc, p1);
	
	var vec0X = p0.x - pc.x;
	var vec0Y = p0.y - pc.y;
	var vec1X = p1.x - pc.x;	
	var vec1Y = p1.y - pc.y;
	
	if (vec0X == 0) vec0X = epsilon;
	if (vec1X == 0) vec1X = epsilon;
	
	var theta0 = Math.atan(1.0 * vec0Y / vec0X);
	var theta1 = Math.atan(1.0 * vec1Y / vec1X);
	
	if (q0 == 2 || q0 == 3) {
		theta0 += Math.PI;
	}
	if (q1 == 2 || q1 == 3) {
		theta1 += Math.PI;
	}
	
	
	
	if (theta0 < 0) {
		theta0 += 2 * Math.PI;
	}
	if (theta1 < 0) {
		theta1 += 2 * Math.PI;
	}
	
	if (theta1 < theta0) {
		//theta1 += 2 * Math.PI;
	}
	
	//console.log(Math.PI);
	//console.log(theta0);
	//console.log(theta1);
	
	var angle = 0;
	if (counterClock) {
		if (theta1 > theta0) {
			angle = theta1 - theta0; 
		}
		else {
			angle = 2*Math.PI - (theta0 - theta1);
		}
		console.log("ANGLE: " + 180.0*angle/Math.PI);
		return angle
	}
	/*
		console.log("ANGLE: " + 180.0*(theta1 - theta0)/Math.PI);
		return (theta1 - theta0); */
	
	
	return 2 * Math.PI - (theta1 - theta0);
	
	
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
	//console.log("enter " + edges.length);
/*
	for (var count = 0; count < edges.length; count++) {
		console.log("Edges enter: " + edges[count].toString() + counterClock);
	}
	*/
	/*
	if (edges.length == 0) {
		return;
	}*/
	
	var orderedEdges = [];
	// Selection sort
	
	for (var count = 0; count < edges.length; count++) {
		var closestLine = edges[count];
		var lowIndex = count;
		for (var i = count; i < edges.length; i ++) {
			var edge = edges[i];
			
			// True if edge < closestLine
			if (shorterRotation(counterClock, point, line, edge, closestLine)) {
				//console.log("SWITCH");
				closestLine = edge;
				lowIndex = i;
			}
		}
		
		//orderedEdges.push(closestLine);
		// at position index, remove 1 element
		var temp = edges[lowIndex];
		edges[lowIndex] = edges[count];
		edges[count] = temp;
		
	}
	/*
	for (var count = 0; count < edges.length; count++) {
		console.log("Edges exit: " + edges[count].toString() + counterClock);
	}
	*/
	//console.log("exit " + edges.length);
	return edges;
	
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
	
	if (thetaA < 0) thetaA += 2 * Math.PI;
	if (thetaB < 0) thetaB += 2 * Math.PI;
	if (thetaLine < 0) thetaLine += 2 * Math.PI;
	
	if (thetaA < thetaLine) {
		thetaA += 2 * Math.PI;
	}
	if (thetaB < thetaLine) {
		thetaB += 2 * Math.PI;
	}
	//console.log(Math.PI);
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

function testAngleBetween() {
	console.log("START TESTING...");
	
	var p00 = new Point(0,0);
	var p01 = new Point(0,1);
	var p02 = new Point(0,2);
	var p10 = new Point(1,0);
	var p11 = new Point(1,1);
	var p12 = new Point(1,2);
	var p20 = new Point(2,0);
	var p21 = new Point(2,1);
	var p22 = new Point(2,2);
	
	var line00 = new Line(p00, p11);
	var line10
	var line20
	var line
	
	console.log("ALL TESTS PASSED!");
}

