var rooms = [];
var targetPoint;
var visitedPoints = {};

// Given a set of lines, return a set of rooms
function detectRooms(lines) {
	
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
	
	
	var edges = getNeighbors(point, line);
	//console.log("Edge count " + edges.length);
	sortEdges(counterClock, edges); // does nothing currently
	
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
function getNeighbors(point, includedLine) {
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

function sortEdges(counterClock, edges) {
	return;
}
	