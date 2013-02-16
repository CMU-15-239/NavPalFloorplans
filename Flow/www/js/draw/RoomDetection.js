var rooms = [];
var targetPoint;
var visitedPoints = {};

// Given a set of lines, return a set of rooms
function detectRooms(lines) {
	
	for (i = 0; i < lines.length; i++) {
		searchRoom(lines[i]);
		console.log(i);
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
	visitedPoints[point.toString] = true;
	
	// Base Case
	if (point.equals(targetPoint)) {
		return true;
	}	
	
	
	var edges = getNeighbors(point, line);
	sortEdges(counterClock, edges); // does nothing currently
	
	for (var i = 0; i < edges.length; i ++) {
		var newLine = edges[i];
		
		// See if this point is unexplored
		var p1 = visitedPoints[newLine.p1.toString];
		var p2 = visitedPoints[newLine.p2.toString];
		if (!p1 || !p2) {
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
		if (line != includedLine) {
			edges.push(line);
		}
	}
	
	return edges;
}

function sortEdges(counterClock, edges) {
	return;
}
	