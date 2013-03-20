/**
 * Summary: Import and draw the lines from the image processing stage.
 * Parameters: jsonObj: The JSON object that describes the lines found in the
 * 		image processing.
 * Returns: undefined
**/
function importLines(jsonObj) {
	//Iterate through all the lines in the json object and add them to
	//the tool
	for (var i = 0; i < jsonObj.lines.length; i++) {
		var curLine = jsonObj.lines[i];
		var p1 = curLine.line[0];
		var p2 = curLine.line[1];
		
		p1 = new Point(p1.p1[1], p1.p1[0]);
		//Check if the point already exists
		var p1Exists = pointAlreadyExists(p1);
		//If the point already exists, make p1 point to it.
		if (p1Exists !== false) p1 = p1Exists;
		
		p2 = new Point(p2.p2[1], p2.p2[0]);
		//Check if the point already exists
		var p2Exists = pointAlreadyExists(p2);
		//If the point already exists, make p2 point to it.
		if (p2Exists !== false) p2 = p2Exists;
		p1.degree += 1;
		p2.degree += 1;
		//Add the new line to the drawing
		ALL_WALLS.push(new Line(p1, p2));
		//Only add points that aren't already in the array
		if (p1Exists === false) ALL_POINTS.push(p1);
		if (p2Exists === false) ALL_POINTS.push(p2);
	}
	redraw();
}

/**
 * Summary: Check if the given point already exists in the drawing.
 * Parameters: p: The point to check for existence.
 * Returns: false is the point does not already exist, and the point in the drawing
 *		that matches the given point otherwise.
**/
function pointAlreadyExists(p) {
	for (var i = 0; i < ALL_POINTS.length; i++) {
		if (ALL_POINTS[i].equals(p)) return ALL_POINTS[i];
	}
	return false;
}