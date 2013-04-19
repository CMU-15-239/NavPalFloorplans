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
	//if (!stateManager.currentFloor.globals.pointExists(this.pointAtCursor)) 
	this.addPoint(this.pointAtCursor);
	//console.log("Number of points: " + stateManager.currentFloor.globals.points.length);
	//console.log(stateManager.currentFloor.globals.walls);
	this.stateManager.redraw();
}

DrawState.prototype.mergeIntersectingLines = function(line) {
	var intersectionPoints = [];
	var newLines = [];
	var deletedLines = [];
	var i = 0;
	while (i < stateManager.currentFloor.globals.walls.length) {
		curWall = stateManager.currentFloor.globals.walls[i];
		var pointOfIntersect = curWall.pointOfLineIntersection(line);
		//if (pointOfIntersect !== null) console.log(stateManager.currentFloor.globals.pointExists(pointOfIntersect));
		if (pointOfIntersect !== null) {
			//We now know that the point should be added to the canvas
			this.addPoint(pointOfIntersect);
			intersectionPoints.push(pointOfIntersect);
			var twoNewLines = curWall.breakIntoTwo(pointOfIntersect);
			newLines.push(twoNewLines.l1);
			newLines.push(twoNewLines.l2);
			deletedLines.push(curWall);
			stateManager.currentFloor.globals.removeWall(curWall, false);
			//console.log("Number of new lines: " + newLines.length);
		}
		else {
			i += 1;
		}
	}
	//this.stateManager.redraw();
	//console.log(stateManager.currentFloor.globals.walls);
	if (intersectionPoints.length === 0) {
		this.addWall(line);
	}
	else {
		//Now split up the line we just drew
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
	
	//if (newLines.length === 0) this.addActionSetToStack({newlyAddedLines: new Array(line), deletedLines: deletedLines, newlyAddedPoints: newPoints});
	//else this.addActionSetToStack({newlyAddedLines: newLines, deletedLines: deletedLines, newlyAddedPoints: newPoints});
}

DrawState.prototype.addActionSetToStack = function(recentAction) {
	this.actionStack[this.actionStack.length] = recentAction;
	//this.actionStack.push(recentlyAddedWalls);
}

DrawState.prototype.addPoint = function(pointToAdd) {
	this.pointsAddedInSession.push(pointToAdd);
	stateManager.currentFloor.globals.addPoint(pointToAdd);
}

DrawState.prototype.addWall = function(wallToAdd) {
	stateManager.currentFloor.globals.addWall(wallToAdd);
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
		stateManager.currentFloor.globals.removeWall(wallToRemove, true);
		numLinesRemoved += 1;
	}
	var numPointsRemoved = 0;
	while (numPointsRemoved < actionToUndo.newlyAddedPoints.length) {
		var pointToRemove = actionToUndo.newlyAddedPoints[numPointsRemoved];
		stateManager.currentFloor.globals.removePoint(pointToRemove);
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
		stateManager.currentFloor.globals.removePoint(pointToRemove);
		numPointsRemoved += 1;
	}*/
	var numLinesRemoved = 0;
	while (numLinesRemoved < actionToRedo.deletedLines.length) {
		var wallToRemove = actionToRedo.deletedLines[numLinesRemoved];
		stateManager.currentFloor.globals.removeWall(wallToRemove, true);
		numLinesRemoved += 1;
	}
	
	this.stateManager.redraw();
}