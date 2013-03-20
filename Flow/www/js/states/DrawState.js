var DrawState = function(stateMan) {
	this.stateManager = stateMan;
	
	this.pointAtCursor;
	this.pointsAddedInSession = [];
}

DrawState.prototype = new BaseState();

DrawState.prototype.enter = function() {
}

DrawState.prototype.exit = function() {
	this.pointAtCursor = undefined;
	this.pointsAddedInSession = [];
}

DrawState.prototype.mouseMove = function(event) {
	this.pointAtCursor = GLOBALS.view.toRealWorld(new Point(event.pageX, event.pageY));
	
	this.stateManager.redraw();
}

DrawState.prototype.addPoint = function(pointToAdd) {
	GLOBALS.addPoint(pointToAdd);
}

DrawState.prototype.addWall = function(wallToAdd) {
	GLOBALS.addWall(wallToAdd);
}

DrawState.prototype.draw = function() {
	//Draw the point that follows the cursor around
	if (this.pointAtCursor !== undefined) {
		this.pointAtCursor.draw();
	}

	//Draw the line that goes from the most recently drawn point to the user's cursor
	if (this.pointsAtCursor !== undefined && pointBeenDrawnInSession()) {
		var numPointsDrawn = this.pointsAddedInSession.length;
		var newestPointAdded = this.pointsAddedInSession[numPointsDrawn - 1];
		var trackingLine = new Line(this.pointAtCursor, newestPointDrawn);
		trackingLine.draw();
	}
}

DrawState.prototype.pointBeenDrawnInSession = function() {
	return (this.pointsAddedInSession.length != 0);
}

DrawState.prototype.undo = function() {
}

DrawState.prototype.redo = function() {
}