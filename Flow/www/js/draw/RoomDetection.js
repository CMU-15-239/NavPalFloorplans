/**
	RoomDetection.js
	Written by Paul Davis
	pjbdavis@gmail.com
	Spring 2013
	
	This file takes a list of lines and determines which areas constitute
	different rooms. The basic algorithm uses depth first search 
	
	NEED TO FINISH WRITING...
	
*/

var targetPoint;
var visitedPoints = {};
var epsilon = .0001;

/**
 * Summary: Automatically detects the tightest bound around all 
			closed off rooms.
 * Parameters: lines: a list of all the lines that define walls
			   spaces: a list of all spaces (on this floor)
 * Returns: A new list of spaces
**/
function detectRooms(lines, spaces) {
	var rooms = [];
	
	// Check if this line is part of a closed off room
	for (var i = 0; i < lines.length; i++) {
		searchRoom(lines[i], rooms);
	}
	
	var newRooms = [];
	// Update the global list of rooms, don't overwrite existing rooms
	for (var i = 0; i < rooms.length; i ++) {
		var room = rooms[i];
		
		var alreadyExist = false;
		for (var j = 0; j < spaces.length; j ++) {
			var prevRoom = spaces[j];
			
			// The room already exists, don't overwrite it
			if (prevRoom.sameRoomWalls(room)) {
				alreadyExist = true;
				newRooms.push(prevRoom);
			}
		}
		
		// Add new rooms
		if (alreadyExist == false) {
			newRooms.push(room);
		}
	}
	console.log(newRooms);
	return newRooms;
}

/**
 * Summary: Sums the magnitude of all lines
 * Parameters: lines: a list lines
 * Returns: Sum of magnitude of each line
**/
function distance(lines) {
	sum = 0;
	
	for (var i = 0; i < lines.length; i ++) {
		sum += lines[i].magnitutde();
	}
	
	return sum;
}

/**
 * Summary: Traverse path along the lines, and sum the angles 
 *			moving in a cw or ccw direction
 * Parameters: lines: A set of lines that represent a path
 *			   counterClock: true if path follows the lines ccw 
 * Returns: The sum of the angles
**/
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
		
		// Find the point that the lines share
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

/**
 * Summary: Given the number of edges in a polygon return the sum
 * 			of the internal angles
 * Parameters: n: number of sides of polygon
 * Returns: Sum of internal angles
**/
function sumInternalAngle(n) {
	return (n - 2) * Math.PI;
}

/**
 * Summary: For a given line, see if that line is part of a room.
 *			Check the traversing both ccw and cw around to find a room.
 * Parameters: line: a wall. rooms: a list of all rooms found so far
 * Returns: undefined
**/
function searchRoom(line, rooms) {

	var validRoute = false;
	var validRevRoute = false;
	targetPoint = line.p2;
	
	// Starting at one of the points, DFS to get to the other, going ccw
	var route = new Array();
	visitedPoints = {}
	if (followWalls(true, line.p1, line, route)) { // DFS
		route.push(line);
		takeRoute = true;
		var angles = sumAngles(route, true);
		var expectedInternalAngle = sumInternalAngle(route.length);
		
		// Must take other route, this is not valid, 
		// path did not take innermost angles
		if (!almostEqual(expectedInternalAngle, angles)) {
			validRoute = false;
		}
		else {
			validRoute = true;
		}
	}
	
	// Let's also try clockwise
	if (true) {
		visitedPoints = {}
		var revRoute = new Array();
		if (followWalls(false, line.p1, line, revRoute)) { // DFS
			revRoute.push(line);
			validRevRoute = true;
			//route = revRoute;
			
			var angles = sumAngles(revRoute, false);
			var expectedInternalAngle = sumInternalAngle(revRoute.length);

			if (!almostEqual(expectedInternalAngle, angles)) {
				validRevRoute = false;
			}
			else {
				validRevRoute = true;
			}
		}
	}
	
	// If a valid room was found
	if (validRoute) {
		//console.log(route);
		addRouteAsRoom(route, rooms);
	}
	
	if (validRevRoute) {
		//console.log(revRoute);
		addRouteAsRoom(revRoute, rooms);
	}
	
}

function addRouteAsRoom(route, rooms) {

	var newRoom = new Space(route);
	var found = false;
	for (var i = 0; i < rooms.length; i ++) {
		if (rooms[i].sameRoomWalls(newRoom)) {
			found = true;
		}
	}
	
	// If a room with the same walls has not been found, this room is new
	if (!found) {
		rooms.push(newRoom);
	}
	
}


/**
 * Summary: A simple DFS that prioritizes ccw (or cw) traversal through
			neighboring edges
 * Parameters: counterClock: true for ccw traversal
			   point: the current point we are at
			   line: the last line traveled
			   route: stack of lines taken so far
 * Returns: undefined
**/
function followWalls(counterClock, point, line, route) {

	visitedPoints[point.toString()] = true;

	if (point == false) {
		// Why would this ever happen?
		//alert() 
	}
	
	// Base Case, we're at our target point
	if (point.equals(targetPoint)) {
		return true;
	}	
	
	// Find all outgoing lines and sort them in a ccw (or cw) order
	var edges = getEdgeNeighbors(point, line);
	edges = sortEdges(counterClock, edges, point, line);
	
	for (var i = 0; i < edges.length; i ++) {
		var newLine = edges[i];
		
		// See if this point is unexplored
		var p1 = visitedPoints[newLine.p1.toString()];
		var p2 = visitedPoints[newLine.p2.toString()];

		if (!(p1 == true) || !(p2 == true)) {
			var newPoint;
			if (!p1) {
				newPoint = newLine.p1;
			}
			else {
				newPoint = newLine.p2;
			}
			route.push(newLine);

			// We found a route back to our target
			if (followWalls(counterClock, newPoint, newLine, route)) {
				return true;
			}
			
			// We didn't find a route, let's backtrack
			route.pop();
		}
	}
	
	// If we make it back to the start, no path to target
	if (route.length == 0) {
		return false;
	}
	
}

/**
 * Summary: Find the angle between two lines
 * Parameters: counterClock: the direction to explore from line0
 *			   point: the center point that the lines share
 *			   line0: starting line
 *			   line1: target line
 * Returns: The angle between the lines, in radians
**/
function angleBetween(counterClock, point, line0, line1) {
	//console.log("ENTER ANGLE BETWEEN ------------------");
	//console.log("line 0 " + line0.toString());
	//console.log("line 1 " + line1.toString());
	
	var pc = point;
	var p0 = line0.otherPoint(point);
	var p1 = line1.otherPoint(point);
	
	var q0 = determineQuad(pc, p0);
	var q1 = determineQuad(pc, p1);
	
	var vec0X = p0.x - pc.x;
	var vec0Y = - p0.y + pc.y;
	var vec1X = p1.x - pc.x;	
	var vec1Y = - p1.y + pc.y;
	
	if (vec0X == 0) vec0X = epsilon;
	if (vec1X == 0) vec1X = epsilon;
	
	var theta0 = Math.atan(1.0 * vec0Y / vec0X);
	var theta1 = Math.atan(1.0 * vec1Y / vec1X);
	
	//console.log("theta0 original: " + toDegree(theta0));
	//console.log("theta1 original: " + toDegree(theta1));
	
	if (q0 == 2 || q0 == 3) {
		theta0 += Math.PI;
	}
	if (q1 == 2 || q1 == 3) {
		theta1 += Math.PI;
	}
	
	//console.log("theta0 quad: " + toDegree(theta0));
	//console.log("theta1 quad: " + toDegree(theta1));
	
	if (theta0 < 0) {
		theta0 += 2 * Math.PI;
	}
	if (theta1 < 0) {
		theta1 += 2 * Math.PI;
	}
	
	//console.log("theta0 relative: " + toDegree(theta0));
	//console.log("theta1 relative: " + toDegree(theta1));

	var angle = 0;

	if (theta1 > theta0) {
		angle = theta1 - theta0; 
	}
	else {
		angle = 2*Math.PI - (theta0 - theta1);
	}
	
	// If we are going clockwise, find the opposing angle
	if (counterClock == false) {
		angle = 2*Math.PI - angle;
	}
	return angle
}


/**
 * Summary: Find all the edges along the given point, excluded the given line
 * Parameters: point: the current point
 *			   includedLine: don't include this line
 * Returns: A list of neighbors of the point
**/
// TODO: improve efficiency with hash-map
function getEdgeNeighbors(point, includedLine) {
	var edges = [];
	var allWalls = stateManager.currentFloor.globals.walls;
	for (var i = 0; i < allWalls.length; i++) {
		var line = allWalls[i];
		if (line.p1 == point || line.p2 == point) {
			if (line != includedLine) {
				edges.push(line);
			}
		}
	}
	
	return edges;
}

/**
 * Summary: Sort the edges in a ccw (or cw) order start at line
 * Parameters:  counterClock: sort according to ccw if true
 *				edges: a list of edges to sort
 *				point: center point for comparison of angles
 *				line: starting line for comparison of angles
 * Returns: A list of sorted edges
**/
function sortEdges(counterClock, edges, point, line) {
	/*
	for (var count = 0; count < edges.length; count++) {
		console.log("Edges enter: " + edges[count].toString() + counterClock);
	}
	*/
	
	// Selection sort, put closest edges to line 
	for (var count = 0; count < edges.length; count++) {
		var closestLine = edges[count];
		var lowIndex = count;
		for (var i = count; i < edges.length; i ++) {
			var edge = edges[i];
			
			// True if edge < closestLine
			if (shorterRotation(counterClock, point, line, edge, closestLine)) {
				closestLine = edge;
				lowIndex = i;
			}
		}
		
		// Swap the min to the front
		var temp = edges[lowIndex];
		edges[lowIndex] = edges[count];
		edges[count] = temp;	
	}
	
	/*
	for (var count = 0; count < edges.length; count++) {
		console.log("Edges exit: " + edges[count].toString() + counterClock);
	}
	*/
	
	return edges;
}

/**
 * Summary: Compares two lines to see which is closer to a reference line
 *			moving in a ccw (or cw) direction.
 * Parameters:  counterClock: true if ccw direction
 *				point: shared point between the lines
 *				line: the reference line
 *				a: one of the lines to compare
 *				b: the other line to compare
 * Returns: True if line 'a' is cloer than line 'b'.
**/
function shorterRotation(counterClock, point, line, a, b) {

	var angleA = angleBetween(counterClock, point, line, a);
	var angleB = angleBetween(counterClock, point, line, b);
	
	return angleA < angleB;
}

/**
 * Summary: Returns which quadrant a point is in, with respect to a 
			center point.
 * Parameters:  pc: Center point (reference)
				p: Other point
 * Returns: Returns the quadrant the other point is in (I, II, III, or IV)
**/
function determineQuad(pc, p) {
	
	// Quad 1 or 4
	if (pc.x <= p.x) {// assume that epsilon has been added to dx
		if (pc.y > p.y) { // Tie goes to q1
			return 1;
		}
		else { 
			return 4;
		}
	}
	// Quad 2 or 3
	else if (pc.x > p.x) {
		if (pc.y > p.y) { // Tie goes to q2
			return 2;
		}
		else {
			return 3;
		}	
	}
}


// Testing for angleBetween function
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
	var line10 = new Line(p10, p11);
	var line20 = new Line(p20, p11);
	var line01 = new Line(p01, p11);
	var line21 = new Line(p21, p11);
	var line02 = new Line(p02, p11);
	var line12 = new Line(p12, p11);
	var line22 = new Line(p22, p11);
	
	var lines = [];
	lines.push(line21);
	lines.push(line20);
	lines.push(line10);
	lines.push(line00);
	lines.push(line01);
	lines.push(line02);
	lines.push(line12);
	lines.push(line22);
	
	// Create 8 lines, and test the angles between any pair, both
	// in the cw and ccw directions
	var eigth = 2 * Math.PI / 8;
	for (var zeroIndex = 0; zeroIndex < lines.length; zeroIndex ++) {
		for (var i = 1; i < lines.length; i ++) {
			var angle = angleBetween(true, p11, lines[zeroIndex], 
									 lines[(i + zeroIndex) % lines.length]);
			var revAngle = angleBetween(false, p11, lines[zeroIndex], 
									    lines[(i + zeroIndex) % lines.length]);
										
			var angle2 = angleBetween(false, p11, 
									  lines[(i + zeroIndex) % lines.length],
									  lines[zeroIndex]);
									  
			var revAngle2 = angleBetween(true, p11, 
									     lines[(i + zeroIndex) % lines.length],
									     lines[zeroIndex]);			

			if (!almostEqual(angle, angle2)) {
				alert();
			}
			if (!almostEqual(revAngle, revAngle2)) {
				alert();
			}
			if (!almostEqual(revAngle, 2*Math.PI - (eigth * i))) {
				alert();
			}
			if (!almostEqual(angle, eigth * i)) {
				alert();
			}
			else {
				
			}
		}
	}
	
	console.log("ALL TESTS PASSED!");
}

/**
 * Summary: Converts an angle in radians to degrees
 * Parameters: angle: Angle in radians
 * Returns: The angle in degrees
**/
function toDegree(angle) {
	return angle * 180.0 / Math.PI;
}


function almostEqual(a,b) {
	return Math.abs(a - b) < 0.001;
}
