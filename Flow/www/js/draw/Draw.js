/* The following are globals used exclusively in this file. */

//The most recently drawn point (not the one that moves with the cursor)
var LAST_POINT;

//The point that follows the user's cursor in the draw tool
var CUR_POINT;

//The line that is generated from the last point to current point
var CUR_LINE;

//The line the user is about to snap to, if it exists
var SNAPPED_TO_LINE;

//Can the user snap to the most recently drawn point?
var CAN_SNAP_TO_LAST = false;

//True iff the user is in snapping range of a point
var ABOUT_TO_SNAP_TO_POINT = false;

var DISPLAY_DOT = true;

//An array of the lines that are currently selected.
var SELECTED_LINES = [];

//Is the mouse down?
var MOUSEDOWN = false;

//Is the control button currently depressed?
var CNTRL_DOWN = false;

var PREVPOINT = new Point(0,0);

//The selection rectangle
var SELECT_RECT = {shouldDraw : false, p1: undefined, p2: undefined}

//Float equality!
var epsilon = 0.0000001;

/**
 * Summary: The event handler for mouse down.
 * Parameters: event: The object describing the event that occured.
 * Returns: undefined
**/
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
		
		SELECTED_LINES = [];
		// Figure out if the selected lines define a valid room
		for (var i = 0; i < ALL_WALLS.length; i++) {
			var line = ALL_WALLS[i];
			if (line.isSelected) {
				//console.log("line selected " + i);
				SELECTED_LINES.push(line);
			}
		}
		//console.log(selectedLines.length + " entered lines");
		if (isClosedRoom(SELECTED_LINES) == true) {
			//console.log("VALID room");
			//enableAddRoom();
		}
		else {
			//console.log("INVALID room");
			//disableAddRoom();
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
		roomSelectMouseMoved(event.pageX - CANVAS.x, event.pageY - CANVAS.y);
	}
	if (STATE === "room_detection_tool") {
		//roomSelectMouseMoved(event.pageX - CANVAS.x, event.pageY - CANVAS.y);
	}
	drawWalls();
}
	
// Unselect everything when the user switches to the select tool
function unselectAll() {
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

/**
 * Summary: Check whether the given point is inside the point that define the 
 *		top-left and bottom-right of a rectangle.
 * Parameters: pointToCheck: The point to check,
		boundingP1: The top-left point of the rectangle to check. 
		boundingP2: The bottom-right point of the rectangle to check.
 * Returns: true iff the given point is inside the defined rectangle.
**/
function pointInRect(pointToCheck, boundingP1, boundingP2) {
	return (((pointToCheck.x >= boundingP1.x && pointToCheck.x <= boundingP2.x) || (pointToCheck.x <= boundingP1.x && pointToCheck.x >= boundingP2.x)) &&
	((pointToCheck.y >= boundingP1.y && pointToCheck.y <= boundingP2.y) || (pointToCheck.y <= boundingP1.y && pointToCheck.y >= boundingP2.y)));
}

/**
 * Summary: The event handler when the mosue is moved in the line_tool state
 * Parameters: cursorX: The x-coordinate of the mouse,
		cursorY: The y-coordinate of the mouse
 * Returns: undefined
**/
function lineToolAction(cursorX, cursorY) {
	//Update the current point to be where the cursor is.
	CUR_POINT = new Point(cursorX, cursorY);

	var snapFound = false;
	//Check if the current point is in snapping distance to a line.
	for (var i = 0; i < ALL_WALLS.length; i++) {
		var line = ALL_WALLS[i];
		if (line.distanceToPoint(CUR_POINT) <= SNAP_RADIUS) {
			//Now actually snap to the line.
			if (line.snapToLine(CUR_POINT)) {
				SNAPPED_TO_LINE = line;
				snapFound = true;
			}
		}
	}
	//If we don't snap to a line, then leave the SNAPPED_TO_LINE undefined.
	if (!snapFound) {
		SNAPPED_TO_LINE = undefined;
	}
	
	//Keep track of the index of the most recently-drawn point
	var lastSnapIndex = ALL_POINTS.length - 1;
	//If the user is allowed to snap to the most recently drawn point, accommodate that
	//in the last index we check before snapping.
	if (CAN_SNAP_TO_LAST) lastSnapIndex += 1;
	//Check if the current point is in snapping distance to a point.
	for (var i = 0; i < lastSnapIndex; i++) {
		var p = ALL_POINTS[i];
		// If a point is close enough, snap to it
		if (CUR_POINT.distance(p) <= SNAP_RADIUS) {
			CUR_POINT.x = p.x;
			CUR_POINT.y = p.y;
			p.setSnap(true);
			ABOUT_TO_SNAP_TO_POINT = p;
			break; // Only snap to a single point
		}
		ABOUT_TO_SNAP_TO_POINT = false;
	}
	
	//The first time through, if the line to the user's cursor isn't drawn, draw it.
	if (LAST_POINT !== undefined) {
		CUR_LINE = new Line(LAST_POINT, CUR_POINT);
		CUR_LINE.draw(true);
	}
	
	CUR_POINT.draw();
}

/**
 * Summary: The event handler for key press.
 * Parameters: event: The object describing the event that occured.
 * Returns: undefined
**/
function mouseClicked(event) {
	redraw();
	if (STATE === "line_tool") {
		//If the user clicked and is not about to snap to a point, draw a new point.
		if (ABOUT_TO_SNAP_TO_POINT === false && CUR_POINT !== undefined) {
			//Now we know that the current point will be permanent on the drawn floor plan.
			ALL_POINTS.push(CUR_POINT);
		}
		else {
			//The user is about to snap to an existing point
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
		//This structure seriously needs to be changed.
		if (SNAPPED_TO_LINE !== undefined && SNAPPED_TO_LINE !== true && ABOUT_TO_SNAP_TO_POINT === false) {
			//break line into two based on current point
			//Don't break up if the one of the new lines we'd create has length of 0.
			if (!SNAPPED_TO_LINE.p1.equals(CUR_POINT) && !SNAPPED_TO_LINE.p2.equals(CUR_POINT)) {
				var twoNewLines = SNAPPED_TO_LINE.breakIntoTwo(CUR_POINT);
				CUR_POINT.degree += 2;
				ALL_WALLS.splice(ALL_WALLS.indexOf(SNAPPED_TO_LINE), 1);
				newLine1Duplicate = false;
				newLine2Duplicate = false;
				for (var i = 0; i < ALL_WALLS.length; i++) {
					if (ALL_WALLS[i].equals(twoNewLines.l1)) newLine1Duplicate = true;
					if (ALL_WALLS[i].equals(twoNewLines.l2)) newLine2Duplicate = true;
				}
				if (CUR_LINE != undefined) {
					if (CUR_LINE.equals(twoNewLines.l1)) newLine1Duplicate = true;
					if (CUR_LINE.equals(twoNewLines.l2)) newLine2Duplicate = true;
				}
				if (!newLine1Duplicate) ALL_WALLS.push(twoNewLines.l1);
				if (!newLine2Duplicate) ALL_WALLS.push(twoNewLines.l2);
			}
		}
		//The user just made a new line.
		if (LAST_POINT !== undefined && CUR_LINE !== undefined) {
			//console.log("p1: (" + CUR_LINE.p1.x + ", " + CUR_LINE.p1.y + ")    p2: (" + CUR_LINE.p2.x + ", " + CUR_LINE.p2.y + ")");
			ALL_WALLS.push(CUR_LINE);
			CUR_LINE.p1.degree += 1;
			CUR_LINE.p2.degree += 1;
		}
		//Ensure the labeling form doesn't appear.
		$("#done").attr("disabled", true);
		//Update the drawing variables.
		LAST_POINT = CUR_POINT;
		CAN_SNAP_TO_LAST = false;
		SNAPPED_TO_LINE = true;
	}
	else if (STATE === "select_tool") {
		//If the user clicks in a room, have the classification form appear where they click.
		if (ACTIVE_ROOM !== undefined && !BLOCK_CHANGE_ROOM && !inSnapRange(event)) {
			$("#classification_pop").css({
				display: "block",
				top: event.pageY + "px",
				left: event.pageX + "px"
			});
			//Populate the fields of the form with the room's information, if it exists.
			$("#label").val(ACTIVE_ROOM.label);
			var roomType = ACTIVE_ROOM.type;
			if ($("#" + roomType).length) $("#" + roomType).prop('checked', true);
			//Ensure the user can't change the active room.
			BLOCK_CHANGE_ROOM = true;
		}
	}
	drawWalls();
}

/**
 * Summary: Check if the event location is within snapping distance of 
 *		any points or walls.
 * Parameters: event: The event to check the location of.
 * Returns: true iff the event took place within snapping distance of a point or wall..
**/
function inSnapRange(event) {
	//Get the point of the event relative to the canvas.
	var curP = new Point(event.pageX - CANVAS.x, event.pageY - CANVAS.y);
	//First check if the point is within snapping distance of any wall.
	for (var i = 0; i < ALL_WALLS.length; i++) {
		var curWall = ALL_WALLS[i];
		if(curWall.pointNearLine(curP, SNAP_RADIUS)) {
			return true;
		}
	}
	//Check if the point is within snapping distance of any point.
	for (var i = 0; i < ALL_POINTS.length; i++) {
		var curPoint = ALL_POINTS[i];
		if (curPoint.distance(curP) <= SNAP_RADIUS) return true;
	}
	//The point isn't close to any point or wall.
	return false;
}

/**
 * Summary: Check whether all the closed spaces have been given a type
 *		by the user.
 * Parameters: none
 * Returns: true iff all rooms have a non-empty type classification.
**/
function allSpacesClassified() {
	for (var i = 0; i < ALL_CLOSED_ROOMS.length; i++) {
		var curRoom = ALL_CLOSED_ROOMS[i];
		//If the type hasn't yet been assigned, return false.
		if (curRoom.type === "") return false;
	}
	//All the spaces have a type that isn't empty.
	return true;
}

/**
 * Summary: Draw the walls and their defining points.
 * Parameters: none
 * Returns: undefined
**/
function drawWalls() {
	for (var i = 0; i < ALL_WALLS.length; i++) {
		ALL_WALLS[i].draw();
	}
	for (var j = 0; j < ALL_POINTS.length; j++) {
		ALL_POINTS[j].draw();
	}
}

/**
 * Summary: Draw the background image that is the original floorplan.
 * Parameters: none
 * Returns: undefined
**/
function drawFloorPlan() {
	console.log('here');
	if (window.FLOOR_PLAN != null) {
		CANVAS.drawImage(FLOOR_PLAN,0,0, CANVAS.width, CANVAS.height);
	}
}

/**
 * Summary: Draw the polygons that define closed spaces, if appropriate.
 * Parameters: none
 * Returns: undefined
**/
function drawSpaces() {
	for (var i = 0; i < ALL_CLOSED_ROOMS.length; i++) {
		ALL_CLOSED_ROOMS[i].draw();
	}
}

/**
 * Summary: Redraw the volatile elements on the canvas.
 * Parameters: none
 * Returns: undefined
**/
function redraw() {
  	var width = window.innerWidth;
  	var height = window.innerHeight;
  	console.log(width, height);
  	can.width = width;
	can.height = height;
	CANVAS.width = width;
	CANVAS.height = height;
	CANVAS.x = can.offsetLeft;
	CANVAS.y = can.offsetTop;
	console.log
    CANVAS.clearRect(0, 0, CANVAS.width, CANVAS.height);
	//Draw the background floorplan image
    drawFloorPlan();
	//Draw all the walls and the points that define them.
	drawWalls();
	//Draw all the spaces (the polygons that define them)
	drawSpaces();
	//Draw the selection rectangle if appropriate.
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

/**
 * Summary: The event handler for key press.
 * Parameters: event: The object describing the event that occured.
 * Returns: undefined
**/
function keyPressed(event) {
	//Get the code of the key that was pressed.
	var keyCode = event.keyCode;
	//space or enter
	if (keyCode === 13 || keyCode === 32) {
		redraw();
		//Break the current line (i.e. stop the drawing tool)
		if (STATE === "line_tool") {
			//STATE = "select_tool";
			CUR_POINT = undefined;
			resetLineGlobals();
			CAN_SNAP_TO_LAST = true;
		}
		//Unselect everything.
		if (STATE === "select_tool") {
			unselectAll();
		}
	}
	//'d'
	if (keyCode === 100) {
		//Toggle whether the selected lines are doors.
		if (STATE === "select_tool") {
			for (var i = 0; i < ALL_WALLS.length; i++) {
				var curWall = ALL_WALLS[i];
				if (curWall.isSelected) curWall.isDoor = !curWall.isDoor;
			}
			redraw();
		}
		//Toggle whether the most recently-drawn line is a door.
		else if (STATE === "line_tool") {
			if (ALL_WALLS.length >= 1) {
				var mostRecentWall = ALL_WALLS[ALL_WALLS.length - 1];
				mostRecentWall.isDoor = !mostRecentWall.isDoor;
			}
			redraw();
			if (CUR_LINE !== undefined) CUR_LINE.draw();
			if (CUR_POINT !== undefined) CUR_POINT.draw();
		}
	}
}

/**
 * Summary: The event handler for key down.
 * Parameters: event: The object describing the event that occured.
 * Returns: undefined
**/
function keyDown(event) {
	//Get the code of the key that was pressed.
	var keyCode = event.keyCode;
	//Ctrl key
	if (keyCode === 17) {
		CNTRL_DOWN = true;
	}
}

/**
 * Summary: The event handler for key up.
 * Parameters: event: The object describing the event that occured.
 * Returns: undefined
**/
function keyUp(event) {
	//Get the code of the key that was released.
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
				//If the point is currently selected, then it should be deleted.
				if (p.isSelected === true) {
					pointsToDelete.push(p);
					//Remove the point from the points array.
					ALL_POINTS.splice(i, 1);
					//Note that the point array size just changed
					i -= 1;
				}
			}
			//Now delete all the walls that are either selected or contain a 
			//point to delete, as a wall shouldn't exist without two defined endpoints.
			var numDeletedWalls = 0;
			for (var j = 0; j < ALL_WALLS.length; j++) {
				var l = ALL_WALLS[j];
				if (l.isSelected) {
					//Set the position in the array to false to indicate that it should be removed.
					ALL_WALLS[j] = false;
					numDeletedWalls += 1;
					//Decrement the degree of each of its endpoints.
					l.p1.degree -= 1;
					l.p2.degree -= 1;
					continue;
				}
				//Now go through and delete the walls with a deleted endpoint.
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
					//Note that the size of ALL_WALLS just changed.
					index -= 1;
				}
				index += 1;
			}
			
			//Delete points that have degree 0 (i.e. aren't connected to anything)
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
		
/**
 * Summary: Deselect all the points and walls.
 * Parameters: none
 * Returns: undefined
**/
function unselectAll() {
	for (var i = 0; i < ALL_WALLS.length; i++) {
		ALL_WALLS[i].isSelected = false;
	}
	for (var j = 0; j < ALL_POINTS.length; j++) {
		ALL_POINTS[j].isSelected = false;
	}
	redraw();
}

/**
 * Summary: Reset the globals used by the drawing tool.
 * Parameters: none
 * Returns: undefined
**/
function resetLineGlobals() {
	LAST_POINT = undefined;
	CUR_LINE = undefined;
}

/**
 * Summary: The method called right before sending data to the post-processor that
 *		populates the door array in each room.
 * Parameters: none
 * Returns: undefined
**/
function addDoorsToRooms() {
	for (var i = 0; i < ALL_CLOSED_ROOMS.length; i++) {
		var curSpace = ALL_CLOSED_ROOMS[i];
		//Iterate through all the walls for the room and push those that
		//are doors
		for (var j = 0; j < curSpace.walls.length; j++) {
			var curWall = curSpace.walls[j];
			if (curWall.isDoor) curSpace.doors.push(curWall);
		}
	}
}