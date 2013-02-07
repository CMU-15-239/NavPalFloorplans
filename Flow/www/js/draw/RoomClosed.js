
// Naive room closed algorithm. Requires that all the edges are drawn each
// time for every room.
function isClosedRoom(lines) {

	// Check for duplicate lines
	for (var i = 0; i < lines.length; i += 1) {
		var line = lines[i];
		for (var j = i + 1; j < lines.length; j += 1) {
			if (line == lines[j]) { // Duplicate found
				return new Exception("Duplicate Wall");
			}
		}
	}
	
	// A room must have at least two lines
	if (lines.length <= 2) {
		return false;
	}
	
	var pointCount = {};
	
	// Initialize all points to 0
	for line in lines {
		pointCount[line.p1.toString()] = 0;
		pointCount[line.p2.toString()] = 0;
	}
	
	// Count the number of times each point appears
	for line in lines {
		pointCount[line.p1.toString()] += 1;
		pointCount[line.p2.toString()] += 1;
	}
	
	// Check if every point appeared twice
	for point in pointCount {
		if (pointCount[point] > 2) {
			return new Exception("Degree too high");
		}
		if (pointCount[point] != 2) {
			return false;
		}
	}
	
	return true;
}


