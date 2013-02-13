var LAST_POINT;

var CUR_POINT;

var CUR_LINE;

var SNAPPED_TO_LINE;

var CAN_SNAP_TO_LAST = false;

var ABOUT_TO_SNAP_TO_POINT = false;

var DISPLAY_DOT = true;

var MOUSEDOWN = false;
var CNTRL_DOWN = false;
var PREVPOINT = new Point(0,0);
var SELECT_RECT = {shouldDraw : false, p1: undefined, p2: undefined}

function mouseDown(event) {
	var newlySelectedPoints = [];
	if (STATE === "select_tool") {
		MOUSEDOWN = true;
		PREVPOINT.x = event.pageX - CANVAS.x;
		PREVPOINT.y = event.pageY - CANVAS.y;
		
		var pointsClicked = 0;
		var linesClicked = 0;
		var mouseNearSelectedPoint = false;
		var mouseNearSelectedLine = false;
		//Select points
		for (var i = 0; i < ALL_POINTS.length; i ++) {
			var p = ALL_POINTS[i];
			if (PREVPOINT.distance(p) <= SNAP_RADIUS) {
				pointsClicked += 1;
				if (p.isSelected === false) {
					p.isSelected = true;
					newlySelectedPoints.push(p);
				}
				else {
					if (CNTRL_DOWN) {
						p.isSelected = false;
					}
					else {
						//If the user sets the mouse down near a selected point, just move
						//around the selected points when they move.
						mouseNearSelectedPoint = true;
					}
				}
			}
		}
		//Select lines
		for (var i = 0; i < ALL_WALLS.length; i++) {
			var line = ALL_WALLS[i];
			if (line.pointNearLine(PREVPOINT, SNAP_RADIUS)) {
				linesClicked += 1;
				if (CNTRL_DOWN && line.isSelected) line.isSelected = false;
				else if (!CNTRL_DOWN && line.isSelected) mouseNearSelectedLine = true;
				else line.isSelected = true;
				//Only select the line if neither of its vertices were clicked immediately before it.
				for (var j = 0; j < newlySelectedPoints.length; j++) {
					var curP = newlySelectedPoints[j];
					if (line.p1.equals(curP) || line.p2.equals(curP)) {
						line.isSelected = false;
						break;
					}
				}
			}
			else {
				if (!CNTRL_DOWN) line.isSelected = false;
			}
		}
		//We need to loop through them all again because we can't be sure when 
		//mouseNearSelectedPoint was set. We want to enable the user to move all the selected
		//points if they click and drag near a selected point.
		for (var i = 0; i < ALL_POINTS.length; i++) {
			var p = ALL_POINTS[i];
			if (PREVPOINT.distance(p) > SNAP_RADIUS) {
				if (!CNTRL_DOWN && !mouseNearSelectedPoint) {
					p.isSelected = false;
				}
			}
		}
		for (var i = 0; i < ALL_WALLS.length; i++) {
			var l = ALL_WALLS[i];
			if (!l.pointNearLine(PREVPOINT, SNAP_RADIUS)) {
				if (!CNTRL_DOWN && !mouseNearSelectedLine) {
					l.isSelected = false;
				}
			}
		}
		//We can start drawing a selection rectangle.
		if (pointsClicked === 0 && linesClicked === 0) {
			SELECT_RECT.shouldDraw = true;
			SELECT_RECT.p1 = new Point(event.pageX - CANVAS.x, event.pageY - CANVAS.y);
			SELECT_RECT.p2 = SELECT_RECT.p1;
		}
	}
}

function mouseUp(event) {
	if (STATE === "select_tool") {
		MOUSEDOWN = false;
		//Update the ax + by = c form for each line.
		for (var i = 0; i < ALL_WALLS.length; i++) {
			var line = ALL_WALLS[i];
			line.calculateForm(line.p1, line.p2);
		}
		
		SELECT_RECT.shouldDraw = false;
		
		var selectedLines = [];
		// Figure out if the selected lines define a valid room
		for (var i = 0; i < ALL_WALLS.length; i++) {
			var line = ALL_WALLS[i];
			if (line.isSelected) {
				console.log("line selected " + i);
				selectedLines.push(line);
			}
		}
		console.log(selectedLines.length + " entered lines");
		if (isClosedRoom(selectedLines) == true) {
			console.log("VALID room");
			enableAddRoom();
		}
		else {
			console.log("INVALID room");
			disableAddRoom();
		}
	}
}

function mouseMoved(event) {
	redraw();
	if (STATE === "line_tool") {
		lineToolAction(event.pageX - CANVAS.x, event.pageY - CANVAS.y);
	}
	if (STATE === "select_tool") {
		selectToolMouseMoved(event.pageX - CANVAS.x, event.pageY - CANVAS.y);
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
	redraw();
}

function selectToolMouseMoved(cursorX, cursorY) {
	
	if (MOUSEDOWN) {
		var dx = PREVPOINT.x - cursorX;
		var dy = PREVPOINT.y - cursorY;
		if (SELECT_RECT.shouldDraw === false) {
			for (var i = 0; i < ALL_POINTS.length; i ++) {
				var p = ALL_POINTS[i];
				if (p.isSelected) {
					p.x -= dx;
					p.y -= dy;
				}
			}
			for (var i = 0; i < ALL_WALLS.length; i++) {
				var l = ALL_WALLS[i];
				if (l.isSelected) {
					l.p1.x -= dx;
					l.p2.x -= dx;
					l.p1.y -= dy;
					l.p2.y -= dy;
				}
			}
		}
		
		PREVPOINT.x = cursorX;
		PREVPOINT.y = cursorY;
		
		if (SELECT_RECT.shouldDraw) {
			SELECT_RECT.p2 = new Point(cursorX, cursorY);
			var p1 = SELECT_RECT.p1;
			var p2 = SELECT_RECT.p2;
			//Select points
			for (var i = 0; i < ALL_POINTS.length; i ++) {
				var p = ALL_POINTS[i];
				if (pointInRect(p, p1, p2)) {
					p.isSelected = true;
				}
				else {
					p.isSelected = false;
				}
			}
			//Select walls
			for (var i = 0; i < ALL_WALLS.length; i++) {
				var w = ALL_WALLS[i];
				if (pointInRect(w.p1, p1, p2) && pointInRect(w.p2, p1, p2)) {
					w.isSelected = true;
				}
				else w.isSelected = false;
			}
		}
	}
}

function pointInRect(pointToCheck, boundingP1, boundingP2) {
	return (((pointToCheck.x >= boundingP1.x && pointToCheck.x <= boundingP2.x) || (pointToCheck.x <= boundingP1.x && pointToCheck.x >= boundingP2.x)) &&
	((pointToCheck.y >= boundingP1.y && pointToCheck.y <= boundingP2.y) || (pointToCheck.y <= boundingP1.y && pointToCheck.y >= boundingP2.y)));
}
	
function lineToolAction(cursorX, cursorY) {
	CUR_POINT = new Point(cursorX, cursorY);

	var snapFound = false;
	for (var i = 0; i < ALL_WALLS.length; i++) {
		var line = ALL_WALLS[i];
		//console.log(line.distanceToPoint(CUR_POINT));
		if (line.distanceToPoint(CUR_POINT) <= SNAP_RADIUS) {
			if (line.snapToLine(CUR_POINT)) {
				SNAPPED_TO_LINE = line;
				snapFound = true;
			}
		}
	}
	if (!snapFound) {
		SNAPPED_TO_LINE = undefined;
	}
	
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
			
		} 
		if (SNAPPED_TO_LINE !== undefined && SNAPPED_TO_LINE !== true && ABOUT_TO_SNAP_TO_POINT === false) {
			//break line into two based on current point
			var twoNewLines = SNAPPED_TO_LINE.breakIntoTwo(CUR_POINT);
			CUR_POINT.degree += 2;
			ALL_WALLS.splice(ALL_WALLS.indexOf(SNAPPED_TO_LINE), 1);
			ALL_WALLS.push(twoNewLines.l1);
			ALL_WALLS.push(twoNewLines.l2);
		}
		if (LAST_POINT !== undefined && CUR_LINE !== undefined) {
			//console.log("p1: (" + CUR_LINE.p1.x + ", " + CUR_LINE.p1.y + ")    p2: (" + CUR_LINE.p2.x + ", " + CUR_LINE.p2.y + ")");
			ALL_WALLS.push(CUR_LINE);
			CUR_LINE.p1.degree += 1;
			CUR_LINE.p2.degree += 1;
		}
		//LAST_POINT = new Point(CUR_POINT.x, CUR_POINT.y);
		LAST_POINT = CUR_POINT;
		CAN_SNAP_TO_LAST = false;
		SNAPPED_TO_LINE = true;
	}
	
	drawWalls();
}

//Draw the walls
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
		CANVAS.drawImage(FLOOR_PLAN,0,0, CANVAS.width, CANVAS.height);
	}
}


function redraw() {
    CANVAS.clearRect(0, 0, CANVAS.width, CANVAS.height);
    drawFloorPlan();
	drawWalls();
	if (SELECT_RECT.shouldDraw && !SELECT_RECT.p1.equals(SELECT_RECT.p2)) {
		var p1 = SELECT_RECT.p1;
		var p2 = SELECT_RECT.p2;
		var width = p2.x - p1.x;
		var height = p2.y - p1.y;
		CANVAS.beginPath();
		CANVAS.rect(p1.x, p1.y, width, height);
		CANVAS.fillStyle = 'rgba(51,153,255,.5)';
		CANVAS.fill();
	}
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
		if (STATE === "select_tool") {
			unselectAll();
		}
	}
}

function keyDown(event) {
	var keyCode = event.keyCode;
	//Ctrl key
	if (keyCode === 17) {
		CNTRL_DOWN = true;
	}
}

function keyUp(event) {
	var keyCode = event.keyCode;
	//Ctrl key
	if (keyCode === 17) {
		CNTRL_DOWN = false;
	}
	//Delete key
	else if (keyCode === 46) {
		//Delete all the selected points and walls
		if (STATE === "select_tool") {
			var pointsToDelete = [];
			for (var i = 0; i < ALL_POINTS.length; i++) {
				var p = ALL_POINTS[i];
				if (p.isSelected) {
					pointsToDelete.push(p);
					//Remove the point from the points array.
					ALL_POINTS.splice(i, 1);
					//Note that the point array size just changed
					i -= 1;
				}
			}
			//Now delete all the walls that contain a point to delete.
			var numDeletedWalls = 0;
			for (var j = 0; j < ALL_WALLS.length; j++) {
				var l = ALL_WALLS[j];
				if (l.isSelected) {
					ALL_WALLS[j] = false;
					numDeletedWalls += 1;
					l.p1.degree -= 1;
					l.p2.degree -= 1;
					continue;
				}
				for (var m = 0; m < pointsToDelete.length; m++) {
					var p = pointsToDelete[m];
					if (l.p1.equals(p) || l.p2.equals(p)) {
						ALL_WALLS[j] = false;
						numDeletedWalls += 1;
						l.p1.degree -= 1;
						l.p2.degree -= 1;
						break;
					}
				}
			}
			//Now we actually have to splice out the walls we want to remove.
			var index = 0;
			while (numDeletedWalls > 0) {
				var l = ALL_WALLS[index];
				if (l === false) {
					ALL_WALLS.splice(index, 1);
					numDeletedWalls -= 1;
					index -= 1;
				}
				index += 1;
			}
			
			//Delete the point because it's not connected to any line
			for (var i = 0; i < ALL_POINTS.length; i++) {
				var p = ALL_POINTS[i];
				if (p.degree <= 0) {
					//Remove the point from the points array.
					ALL_POINTS.splice(i, 1);
					i -= 1;
				}
			}
			redraw();
		}
	}
}
		
function unselectAll() {
	for (var i = 0; i < ALL_WALLS.length; i++) {
		ALL_WALLS[i].isSelected = false;
	}
	for (var j = 0; j < ALL_POINTS.length; j++) {
		ALL_POINTS[j].isSelected = false;
	}
	redraw();
}

function resetLineGlobals() {
	LAST_POINT = undefined;
	//CUR_POINT = undefined;
	CUR_LINE = undefined;
}

// Make the 'Add Room' button active.
function enableAddRoom() {
	$("#add_room").removeAttr("disabled");
}

// Make the 'Add Room' button inactive.
function disableAddRoom() {
	$("#add_room").attr("disabled", "true");
}

// This will be called when the 'Add Room' button is active and clicked.
function addRoomClicked() {
	console.log("CLICKED ADD ROOM");
}