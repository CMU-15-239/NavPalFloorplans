
// Naive room closed algorithm. Requires that all the edges are drawn each
// time for every room.
function isClosedRoom(lines) {

	// Check for duplicate lines
	for (var i = 0; i < lines.length; i += 1) {
		var line = lines[i];
		for (var j = i + 1; j < lines.length; j += 1) {
			if (line.equals(lines[j])) { // Duplicate found
				return new Exception("Duplicate Wall");
			}
		}
	}
	
	console.log("entered");
	
	// A room must have at least two lines
	if (lines.length <= 2) {
		console.log(lines.length + " not enough lines");
		return false;
	}
	//console.log("at least two lines");
	
	var pointCount = {};
	
	// Initialize all points to 0
	for (var i = 0; i < lines.length; i += 1) {
		line = lines[i];
		console.log(lines.length);
		pointCount[line.p1.toString()] = 0;
		pointCount[line.p2.toString()] = 0;
	}
	
	// Count the number of times each point appears
	for (var i = 0; i < lines.length; i += 1) {
		line = lines[i];
		pointCount[line.p1.toString()] += 1;
		pointCount[line.p2.toString()] += 1;
	}
	
	// Check if every point appeared twice
	for (point in pointCount) {
		// If the user created something that is not a room, give exception
		if (pointCount[point] > 2) {
			//console.log("point: " + point.toString);
			return new Exception("Degree too high"); 
		}
	}
	//console.log("no degree too high");
	for (point in pointCount) {
		if (pointCount[point] != 2) {
			return false;
		}
	}
	
	return true;
}


