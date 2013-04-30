/**
	DrawState.js
	Written by Justin Greet
	justin.greet11@gmail.com
	Spring 2013
	
	The state that takes care of drawing new lines and points.
	
*/

var DrawState = function(stateMan) {
	this.stateManager = stateMan;
	//The location of the cursor.
	this.pointAtCursor;
	//Points added between the times this state is entered and exited.
	this.pointsAddedInSession = [];
}

//Inherit from base state.
DrawState.prototype = new BaseState();

/**
 * Summary: Called when the draw state it entered. This functionality
 *		is required, even if it does nothing.
 * Parameters: none.
 * Returns: undefined.
**/
DrawState.prototype.enter = function() {
}

/**
 * Summary: When we exit the state, reset any temporary variables.
 * Parameters: none.
 * Returns: undefined.
**/
DrawState.prototype.exit = function() {
	this.pointAtCursor = undefined;
	this.pointsAddedInSession = [];
}

/**
 * Summary: When the mouse moves, snap the new point if necessary.
 * Parameters: event: the event that encapsulates the mouse movement
 * Returns: undefined.
**/
DrawState.prototype.mouseMove = function(event) {
	//Get the location of the cursor in absolute coordinates.
	this.pointAtCursor = stateManager.currentFloor.globals.view.toRealWorld(new Point(event.pageX - stateManager.canvas.x, event.pageY - stateManager.canvas.y));
	//Snapping to a point takes precedence over snapping to a line
	var snapPoint = this.stateManager.aboutToSnapToPoint(this.pointAtCursor, this.pointsAddedInSession);
	var snapLine = this.stateManager.aboutToSnapToLine(this.pointAtCursor);
	if (snapPoint !== null) {
		this.pointAtCursor = snapPoint;
	}
	else if (snapLine !== null) {
		snapLine.snapToLine(this.pointAtCursor);
	}
	
	this.stateManager.redraw();
}

/**
 * Summary: On mouse click, add a new point/line if necessary.
 * Parameters: event: the event that encapsulates the click
 * Returns: undefined.
**/
DrawState.prototype.click = function(event) {
	//The number of points added since this state's been entered.
	var numPoints = this.pointsAddedInSession.length;
	var recentPoint = undefined;
	//Get the most recently drawn point in this session, if it exists.
	if (numPoints > 0) recentPoint = this.pointsAddedInSession[numPoints - 1];
	
	if (recentPoint !== undefined) {
		//First, check that the user isn't adding a point where one already exists.
		if (!recentPoint.equals(this.pointAtCursor)) {
			//Make a new line from the recent point to where the user clicked.
			var newLine = new Line(this.pointAtCursor, recentPoint);
			//If the new line intersects any others, add new points and split up the lines.
			this.mergeIntersectingLines(newLine);
		}
	} 
	//Add a point where the user clicked.
	this.addPoint(this.pointAtCursor);
	this.stateManager.redraw();
}

/**
 * Summary: Split up any lines that line intersects, and add new points as needed.
 * Parameters: line: the line to check for intersections
 * Returns: undefined.
**/
DrawState.prototype.mergeIntersectingLines = function(line) {
	//The points of intersection between line and other lines.
	var intersectionPoints = [];
	//New lines that are added 
	var newLines = [];
	//Lines that are split up
	var deletedLines = [];
	var i = 0;
	//Iterate through the existing walls, and check if they intersect with line
	while (i < stateManager.currentFloor.globals.walls.length) {
		curWall = stateManager.currentFloor.globals.walls[i];
		//Point of intersection between curWall and line
		var pointOfIntersect = curWall.pointOfLineIntersection(line);
		//Means there's a valid point of intersection.
		if (pointOfIntersect !== null) {
			//We now know that the point should be added to the canvas
			this.addPoint(pointOfIntersect);
			intersectionPoints.push(pointOfIntersect);
			//Split the curWall up along the point of intersection.
			var twoNewLines = curWall.breakIntoTwo(pointOfIntersect);
			newLines.push(twoNewLines.l1);
			newLines.push(twoNewLines.l2);
			deletedLines.push(curWall);
			//Delete the old, unified wall that's been split up.
			stateManager.currentFloor.globals.removeWall(curWall, false);
		}
		//If the lines don't intersect, keep traversing
		else {
			i += 1;
		}
	}
	//If line doesn't intersect anything, we can safely add the wall it created.
	if (intersectionPoints.length === 0) {
		this.addWall(line);
	}
	else {
		//Now split up the parameter line
		var splitUpLineSegs = line.splitUpLine(intersectionPoints);
		for (var i = 0; i < splitUpLineSegs.length; i++) {
			newLines.push(splitUpLineSegs[i]);
		}
	}
	//Now get the lines to register correctly by updating their forms.
	for (var i = 0; i < stateManager.currentFloor.globals.walls.length; i++) {
		var p1 = stateManager.currentFloor.globals.walls[i].p1;
		var p2 = stateManager.currentFloor.globals.walls[i].p2;
		stateManager.currentFloor.globals.walls[i].calculateForm(p1, p2);
	}
	//Now add in all the new lines
	for (var k = 0; k < newLines.length; k++) {
		this.addWall(newLines[k]);
	}
}

/**
 * Summary: Add a new point to the canvas.
 * Parameters: pointToAdd: the point to add
 * Returns: undefined.
**/
DrawState.prototype.addPoint = function(pointToAdd) {
	//Register that this point was drawn in the current session.
	this.pointsAddedInSession.push(pointToAdd);
	stateManager.currentFloor.globals.addPoint(pointToAdd);
}

/**
 * Summary: Add a new wall to the canvas.
 * Parameters: wallToAdd: the wall to add
 * Returns: undefined.
**/
DrawState.prototype.addWall = function(wallToAdd) {
	stateManager.currentFloor.globals.addWall(wallToAdd);
}

/**
 * Summary: Draw a point to track the user's cursor, and draw a line attached
 *		to it if necessart,
 * Parameters: none
 * Returns: undefined.
**/
DrawState.prototype.draw = function() {
	//Draw the line that goes from the most recently drawn point to the user's cursor
	if (this.pointAtCursor !== undefined && this.pointBeenDrawnInSession()) {
		var numPointsDrawn = this.pointsAddedInSession.length;
		var newestPointAdded = this.pointsAddedInSession[numPointsDrawn - 1];
		var trackingLine = new Line(this.pointAtCursor, newestPointAdded);
		trackingLine.draw();
	}

	//Draw the point that follows the cursor around
	if (this.pointAtCursor !== undefined) {
		this.pointAtCursor.draw();
	}
}

/**
 * Summary: If the user hits space, disengage the drawing tool.
 * Parameters: event: The event that encapsulates the key press.
 * Returns: undefined.
**/
DrawState.prototype.keyDown = function(event) {
	var keyCode = event.keyCode;
	//space
	if (keyCode === 32) {
		this.disengage();
	}
}

/**
 * Summary: Disengage the drawing tool.
 * Parameters: none
 * Returns: undefined.
**/
DrawState.prototype.disengage = function() {
	//Reset the points drawn in this session.
	this.pointsAddedInSession = [];
	this.stateManager.redraw();
}

/**
 * Summary: Check if any points have been added in this session.
 * Parameters: none
 * Returns: true iff at least one point has been added in this session.
**/
DrawState.prototype.pointBeenDrawnInSession = function() {
	return (this.pointsAddedInSession.length != 0);
}