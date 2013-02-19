function importLines(jsonObj) {
	for (var i = 0; i < jsonObj.lines.length; i++) {
		var curLine = jsonObj.lines[i];
		var p1 = curLine.line[0];
		var p2 = curLine.line[1];
		p1 = new Point(p1.p1[0], p1.p1[1]);
		p2 = new Point(p2.p2[0], p2.p2[1]);
		ALL_WALLS.push(new Line(p1, p2));
		//console.log(ALL_WALLS);
		//Only add points that aren't already in the array
		if (!pointAlreadyExists(p1)) ALL_POINTS.push(p1);
		if (!pointAlreadyExists(p2)) ALL_POINTS.push(p2);
	}
	//console.log(ALL_POINTS);
	redraw();
}

function pointAlreadyExists(p) {
	for (var i = 0; i < ALL_POINTS.length; i++) {
		if (ALL_POINTS[i].equals(p)) return true;
	}
	return false;
}