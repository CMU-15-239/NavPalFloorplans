var DrawState = function(stateMan) {
	this.stateManager = stateMan;
	
	this.pointAtCursor;
	this.pointsAddedInSession = [];
	this.actionStack = [];
	this.redoStack = [];
}

//NEED TO HAVE
DrawState.prototype = new BaseState();

//NEED TO HAVE
DrawState.prototype.enter = function() {
console.log("entering draw state");
}

//NEED TO HAVE
DrawState.prototype.exit = function() {
	this.pointAtCursor = undefined;
	this.pointsAddedInSession = [];
}

DrawState.prototype.mouseMove = function(event) {
	this.pointAtCursor = GLOBALS.view.toRealWorld(new Point(event.pageX - GLOBALS.canvas.x, event.pageY - GLOBALS.canvas.y));
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

DrawState.prototype.click = function(event) {
	var numPoints = this.pointsAddedInSession.length;
	var recentPoint = undefined;
	if (numPoints > 0) recentPoint = this.pointsAddedInSession[numPoints - 1];
	
	this.addPoint(this.pointAtCursor);
	
	if (recentPoint !== undefined) {
		//TO-DO: undo/redo
		/* Keep track of all the things added in the merge (and the things that
		 * existed before a merge ever took place), and add them
		 * to the activity stack. On undo, just delete those lines and add
		 * back the old ones. On redo, add the new lines and delete the old
		 * ones.
		*/
		
		//Prevent the user from adding the same point multiple times.
		if (!recentPoint.equals(this.pointAtCursor)) {
			var newWall = new Line(this.pointAtCursor, recentPoint);
			this.mergeIntersectingLines(newWall);
		}
	}
}

DrawState.prototype.mergeIntersectingLines = function(line) {
	for (var i = 0; i < GLOBALS.walls.length; i++) {
		curWall = GLOBALS.walls[i];
		var pointOfIntersect = curWall.pointOfLineIntersection(line);
		if (pointOfIntersect !== null) console.log(pointOfIntersect);
	}
	
	this.addWall(line);
}

DrawState.prototype.addActionSetToStack = function(recentlyAddedWalls) {
	actionStack.push(recentlyAddedWalls);
}

DrawState.prototype.addPoint = function(pointToAdd) {
	this.pointsAddedInSession.push(pointToAdd);
	GLOBALS.addPoint(pointToAdd);
}

DrawState.prototype.addWall = function(wallToAdd) {
	var newAction = [];
	newAction.push(wallToAdd);
	this.actionStack.push(newAction);
	GLOBALS.addWall(wallToAdd);
}

//NEED TO HAVE
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

DrawState.prototype.keyDown = function(event) {
	var keyCode = event.keyCode;
	if (keyCode === 32) {
		this.disengage();
	}
	else if (keyCode === 66) {
		this.undo();
	}
}

DrawState.prototype.pointBeenDrawnInSession = function() {
	return (this.pointsAddedInSession.length != 0);
}

DrawState.prototype.disengage = function() {
	this.pointsAddedInSession = [];
	this.stateManager.redraw();
}

DrawState.prototype.undo = function() {
	if (this.actionStack.length <= 0) return undefined;
	
	var actionToUndo = this.actionStack.splice(this.actionStack.length - 1, 1);
	this.redoStack.push(actionToUndo);
	var numRemoved = 0;
	while (numRemoved < actionToUndo.length) {
		GLOBALS.walls.splice(GLOBALS.walls.indexOf(actionToUndo[numRemoved]), 1);
		numRemoved += 1;
	}
		
}

DrawState.prototype.redo = function() {
}