var LAST_POINT;

var CUR_POINT;

var CUR_LINE;

var SNAPPED_TO_LINE;

var CAN_SNAP_TO_LAST = false;

var ABOUT_TO_SNAP_TO_POINT = false;

var DISPLAY_DOT = true;

var MOUSEDOWN = false;
var PREVPOINT = new Point(0,0);

function mouseDown(event) {
	if (STATE === "select_tool") {
		MOUSEDOWN = true;
		PREVPOINT.x = event.pageX;
		PREVPOINT.y = event.pageY;
		
		for (var i = 0; i < ALL_POINTS.length; i ++) {
			var p = ALL_POINTS[i];
			if (PREVPOINT.distance(p) <= SNAP_RADIUS) {
				console.log("In range");
				if (p.isSelected) p.isSelected = false;
				else p.isSelected = true;
			}
		}
	}
}

function mouseUp(event) {
	if (STATE === "select_tool") {
		MOUSEDOWN = false;
		for (var i = 0; i < ALL_WALLS.length; i++) {
			var line = ALL_WALLS[i];
			line.calculateForm(line.p1, line.p2);
		}
	}
}

function mouseMoved(event) {
	redraw();
	if (STATE === "line_tool") {
		lineToolAction(event.pageX, event.pageY);
	}
	if (STATE === "select_tool") {
		selectToolMouseMoved(event.pageX, event.pageY);
	}
	drawWalls();
}
	
// Unselect everything when the user switches to the select tool
function selectToolInit() {
	for (var i = 0; i < ALL_POINTS.length; i ++) {
		ALL_POINTS[i].isSelected = false;
	}
	
	for (var i = 0; i < ALL_WALLS.length; i ++) {
		ALL_WALLS[i].isSelected = false;
	}
}

function selectToolMouseMoved(cursorX, cursorY) {
	
	if (MOUSEDOWN) {
		var dx = PREVPOINT.x - cursorX;
		var dy = PREVPOINT.y - cursorY;
		for (var i = 0; i < ALL_POINTS.length; i ++) {
			var p = ALL_POINTS[i];
			if (p.isSelected) {
				p.x -= dx;
				p.y -= dy;
			}
		}
		
		PREVPOINT.x = cursorX;
		PREVPOINT.y = cursorY;
	}
	
}
	
function lineToolAction(cursorX, cursorY) {
	CUR_POINT = new Point(cursorX, cursorY);

	for (var i = 0; i < ALL_WALLS.length; i++) {
		var line = ALL_WALLS[i];
		//console.log(line.distanceToPoint(CUR_POINT));
		if (line.distanceToPoint(CUR_POINT) < SNAP_RADIUS) {
			line.snapToLine(CUR_POINT);
			SNAPPED_TO_LINE = line;
			//break;
		}
	}
	SNAPPED_TO_LINE = undefined;
	
	var lastSnapIndex = ALL_POINTS.length - 1;
	//If the user is allowed to snap to the most recently drawn point, accommodate that
	//in the last index we check before snapping.
	if (CAN_SNAP_TO_LAST) lastSnapIndex += 1;
	//console.log(ACTIVE_SPACE.points[0]);
	for (var i = 0; i < lastSnapIndex; i++) {
		var p = ALL_POINTS[i];
		//p.setSnap(false); // TODO: this does not suffice, must set ALL snaps to false
		// If a point is close enough, SNAP it
		if (CUR_POINT.distance(p) <= SNAP_RADIUS) {
			CUR_POINT.x = p.x;
			CUR_POINT.y = p.y;
			p.setSnap(true);
			ABOUT_TO_SNAP_TO_POINT = p;
			break; // Only snap to a single point
		}
		ABOUT_TO_SNAP_TO_POINT = false;
	}
	
	if (LAST_POINT !== undefined) {
		CUR_LINE = new Line(LAST_POINT, CUR_POINT);
		CUR_LINE.draw(true);
	}
	
	CUR_POINT.draw();
}

function mouseClicked(event) {
	redraw();
	if (STATE === "line_tool") {
		if (ABOUT_TO_SNAP_TO_POINT === false && CUR_POINT !== undefined) {
			//Now we know that the current point will be permanent on the drawn floor plan.
			ALL_POINTS.push(CUR_POINT);
		}
		else {
			if (CUR_LINE !== undefined) {
				CUR_LINE.p2 = ABOUT_TO_SNAP_TO_POINT;
				//CUR_POINT = new Point(ABOUT_TO_SNAP_TO_POINT.x, ABOUT_TO_SNAP_TO.y);
				CUR_POINT = ABOUT_TO_SNAP_TO_POINT;
				ABOUT_TO_SNAP_TO_POINT = false;
			}
			// This solves a bug where right after switching to the
			// line tool, you select the most recent point placed on the map
			else {
				CUR_POINT = ABOUT_TO_SNAP_TO_POINT;
			}
			
		} /*
		if (SNAPPED_TO_LINE !== undefined) {
			//break line into two based on current point
			var twoNewLines = SNAPPED_TO_LINE.breakIntoTwo(CUR_POINT);
			ALL_WALLS.splice(ALL_WALLS.indexOf(SNAPPED_TO_LINE), 1);
			ALL_WALLS.push(twoNewLines.l1);
			ALL_WALLS.push(twoNewLines.l2);
		} */
		if (LAST_POINT !== undefined && CUR_LINE !== undefined) {
			//console.log("p1: (" + CUR_LINE.p1.x + ", " + CUR_LINE.p1.y + ")    p2: (" + CUR_LINE.p2.x + ", " + CUR_LINE.p2.y + ")");
			ALL_WALLS.push(CUR_LINE);
		}
		
		//console.log(isClosedRoom(ACTIVE_SPACE.walls));
		LAST_POINT = CUR_POINT;//new Point(CUR_POINT.x, CUR_POINT.y);
		CAN_SNAP_TO_LAST = false;
		
		
	}
	
	drawWalls();
}

function drawWalls() {
	for (var i = 0; i < ALL_WALLS.length; i++) {
		ALL_WALLS[i].draw();
	}
	for (var j = 0; j < ALL_POINTS.length; j++) {
		ALL_POINTS[j].draw();
	}
}

function drawFloorPlan() {
	if (window.FLOOR_PLAN != null) {
		ctx.drawImage(FLOOR_PLAN,0,0, CANVAS.width, CANVAS.height);
	}
}


function redraw() {
    CANVAS.clearRect(0, 0, CANVAS.width, CANVAS.height);
    drawFloorPlan();
	drawWalls();
}

function keyPressed(event) {
	var keyCode = event.keyCode;
	//Break the current line (i.e. stop the drawing tool
	if (keyCode === 13 || keyCode === 32) {
		redraw();
		if (STATE === "line_tool") {
			//STATE = "select_tool";
			CUR_POINT = undefined;
			resetLineGlobals();
			CAN_SNAP_TO_LAST = true;
		}
	}
}

function resetLineGlobals() {
	LAST_POINT = undefined;
	//CUR_POINT = undefined;
	CUR_LINE = undefined;
}