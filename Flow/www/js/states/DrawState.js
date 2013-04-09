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
	
	
	if (recentPoint !== undefined) {
		//Prevent the user from adding the same point multiple times.
		if (!recentPoint.equals(this.pointAtCursor)) {
			var newWall = new Line(this.pointAtCursor, recentPoint);
			this.mergeIntersectingLines(newWall);
		}
	}
	//if (!GLOBALS.pointExists(this.pointAtCursor)) 
	this.addPoint(this.pointAtCursor);
	//console.log("Number of points: " + GLOBALS.points.length);
	//console.log("Number of walls: " + GLOBALS.walls.length);
	this.stateManager.redraw();
}

DrawState.prototype.mergeIntersectingLines = function(line) {
	var intersectionPoints = [];
	var newLines = [];
	var newPoints = [];
	var deletedLines = [];
	var i = 0;
	while (i < GLOBALS.walls.length) {
		curWall = GLOBALS.walls[i];
		//Check if the newest point is on a line
		if (curWall.pointNearLine(line.p1, .01)) {
			console.log("Clicked near line!");
			var twoNewLines = curWall.breakIntoTwo(line.p1);
			newLines.push(twoNewLines.l1);
			newLines.push(twoNewLines.l2);
			deletedLines.push(curWall);
			GLOBALS.removeWall(curWall, false);
			continue;
		}
		var pointOfIntersect = curWall.pointOfLineIntersection(line);
		if (pointOfIntersect !== null) {
			console.log(pointOfIntersect.toString());
			this.addPoint(pointOfIntersect);
			intersectionPoints.push(pointOfIntersect);
			newPoints.push(pointOfIntersect);
			var twoNewLines = curWall.breakIntoTwo(pointOfIntersect);
			newLines.push(twoNewLines.l1);
			newLines.push(twoNewLines.l2);
			deletedLines.push(curWall);
			GLOBALS.removeWall(curWall, false);
		}
		else {
			i += 1;
		}
	}
	if (intersectionPoints.length === 0) this.addWall(line);
	else {
		//Now split up the line we just drew
		var splitUpLineSegs = line.splitUpLine(intersectionPoints);
		for (var i = 0; i < splitUpLineSegs.length; i++) {
			newLines.push(splitUpLineSegs[i]);
		}
	}
	for (var i = 0; i < GLOBALS.walls.length; i++) {
		var p1 = GLOBALS.walls[i].p1;
		var p2 = GLOBALS.walls[i].p2;
		GLOBALS.walls[i].calculateForm(p1, p2);
	}
	//Now add in all the new lines
	for (var k = 0; k < newLines.length; k++) {
		this.addWall(newLines[k]);
	}
	
	if (newLines.length === 0) this.addActionSetToStack({newlyAddedLines: new Array(line), deletedLines: deletedLines, newlyAddedPoints: newPoints});
	else this.addActionSetToStack({newlyAddedLines: newLines, deletedLines: deletedLines, newlyAddedPoints: newPoints});
}

DrawState.prototype.addActionSetToStack = function(recentAction) {
	this.actionStack[this.actionStack.length] = recentAction;
	//this.actionStack.push(recentlyAddedWalls);
}

DrawState.prototype.addPoint = function(pointToAdd) {
	this.pointsAddedInSession.push(pointToAdd);
	GLOBALS.addPoint(pointToAdd);
}

DrawState.prototype.addWall = function(wallToAdd) {
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
	//space
	if (keyCode === 32) {
		this.disengage();
	}
	//b
	else if (keyCode === 66) {
		this.undo();
	}
	//n
	else if (keyCode === 78) {
		this.redo();
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
	var actionToUndo = this.actionStack[this.actionStack.length - 1];
	this.actionStack = this.actionStack.splice(0, this.actionStack.length - 1);
	this.redoStack.push(actionToUndo);
	var numLinesRemoved = 0;
	while (numLinesRemoved < actionToUndo.newlyAddedLines.length) {
		var wallToRemove = actionToUndo.newlyAddedLines[numLinesRemoved];
		GLOBALS.removeWall(wallToRemove, true);
		numLinesRemoved += 1;
	}
	var numPointsRemoved = 0;
	while (numPointsRemoved < actionToUndo.newlyAddedPoints.length) {
		var pointToRemove = actionToUndo.newlyAddedPoints[numPointsRemoved];
		GLOBALS.removePoint(pointToRemove);
		numPointsRemoved += 1;
	}
	var numAddedBack = 0;
	while (numAddedBack < actionToUndo.deletedLines.length) {
		this.addWall(actionToUndo.deletedLines[numAddedBack]);
		numAddedBack += 1;
	}
	
	this.stateManager.redraw();
}

DrawState.prototype.redo = function() {
	if (this.redoStack.length <= 0) return undefined;
	var actionToRedo = this.redoStack[this.redoStack.length - 1];
	this.redoStack = this.redoStack.splice(0, this.redoStack.length - 1);
	this.actionStack.push(actionToRedo);
	var numLinesAdded = 0;
	while (numLinesAdded < actionToRedo.newlyAddedLines.length) {
		var wallToAdd = actionToRedo.newlyAddedLines[numLinesAdded];
		this.addWall(wallToAdd);
		numLinesAdded += 1;
	}
	/*var numPoints = 0;
	while (numPointsRemoved < actionToUndo.newlyAddedPoints.length) {
		var pointToRemove = actionToUndo.newlyAddedPoints[numPointsRemoved];
		GLOBALS.removePoint(pointToRemove);
		numPointsRemoved += 1;
	}*/
	var numLinesRemoved = 0;
	while (numLinesRemoved < actionToRedo.deletedLines.length) {
		var wallToRemove = actionToRedo.deletedLines[numLinesRemoved];
		GLOBALS.removeWall(wallToRemove, true);
		numLinesRemoved += 1;
	}
	
	this.stateManager.redraw();
}