var ACTIVE_SPACE = new Space();

var LAST_POINT;

var CUR_POINT;

var CUR_LINE;

function mouseMoved(event) {
	redraw();
	CUR_POINT = new Point(event.pageX, event.pageY);
	
	if (LAST_POINT !== undefined) {
		CUR_LINE = new Line(LAST_POINT, CUR_POINT);
		CUR_LINE.draw(true);
	}
	
	//console.log(ACTIVE_SPACE.points[0]);
	for (var i = 0; i < ACTIVE_SPACE.points.length - 1; i++) {
		var p = ACTIVE_SPACE.points[i];
		p.setSnap(false);
		// If a point is close enough, SNAP it
		if (CUR_POINT.distance(p) <= SNAP_RADIUS) {
			CUR_POINT.x = p.x;
			CUR_POINT.y = p.y;
			p.setSnap(true);
		}
	}
	
	CUR_POINT.draw();
	
	ACTIVE_SPACE.draw();
}

function mouseClicked(event) {
	redraw();
	ACTIVE_SPACE.addPoint(CUR_POINT);
	if (LAST_POINT !== undefined) {
		//console.log("p1: (" + CUR_LINE.p1.x + ", " + CUR_LINE.p1.y + ")    p2: (" + CUR_LINE.p2.x + ", " + CUR_LINE.p2.y + ")");
		ACTIVE_SPACE.addWall(CUR_LINE);
	}
	LAST_POINT = new Point(CUR_POINT.x, CUR_POINT.y);
	
	ACTIVE_SPACE.draw();
}

function redraw() {
    CANVAS.clearRect(0, 0, CANVAS.width, CANVAS.height);
}