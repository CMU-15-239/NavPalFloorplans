function isClosedRoom(lines) {
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
		if (pointCount[point] != 2) {
			return false;
		}
	}
	
	// A room must have at least two lines
	if (lines.length <= 2) {
		return false;
	}
	
	// TODO:
	
	return true;
}

