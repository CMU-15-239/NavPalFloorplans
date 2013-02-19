function importLines(jsonObj) {
	for (var i = 0; i < jsonObj.lines.length; i++) {
		var curLine = jsonObj.lines[i];
		var p1 = curLine.line[0];
		var p2 = curLine.line[1];
		p1 = new Point(p1.p1[1], p1.p1[0]);
		var p1Exists = pointAlreadyExists(p1);
		if (p1Exists !== false) p1 = p1Exists;
		p2 = new Point(p2.p2[1], p2.p2[0]);
		var p2Exists = pointAlreadyExists(p2);
		if (p2Exists !== false) p2 = p2Exists;
		ALL_WALLS.push(new Line(p1, p2));
		//console.log(ALL_WALLS);
		//Only add points that aren't already in the array
		if (p1Exists === false) ALL_POINTS.push(p1);
		if (p2Exists === false) ALL_POINTS.push(p2);
	}
	//console.log(ALL_POINTS);
	redraw();
}

function pointAlreadyExists(p) {
	for (var i = 0; i < ALL_POINTS.length; i++) {
		if (ALL_POINTS[i].equals(p)) return ALL_POINTS[i];
	}
	return false;
}