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
	var xCoordinate = event.pageX;
	var yCoordinate = event.pageY;
	this.pointAtCursor = new Point(xCoordinate, yCoordinate);
	
	this.stateManager.redraw();
}

DrawState.prototype.addPoint = function(pointToAdd) {
	ALL_POINTS.push(pointToAdd);
}

DrawState.prototype.addWall = function(wallToAdd) {
	ALL_WALLS.push(wallToAdd);
}

DrawState.prototype.draw = function() {
	if (this.pointAtCursor !== undefined) {
		this.pointAtCursor.draw();
	}
	if (this.pointBeenDrawnInSession()) {
		var numPointsDrawn = this.pointsAddedInSession.length;
		this.pointsAddedInSession[numPointsDrawn - 1].draw();
	}
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