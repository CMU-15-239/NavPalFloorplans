var StateManager = function() {
	this.avaliableStates = {
		"Preprocess": new PreprocessState(this),
		"Draw": new DrawState(this),
		"Select": new SelectState(this),
		"Move": new MoveState(this)
	};
	
	this.currentState = this.avaliableStates["Draw"];
	this.currentState.enter();
}

StateManager.prototype.changeState = function(newState) {
	if (this.currentState !== newState) {
		this.currentState.exit();
		this.currentState = this.avaliableStates[newState];
		this.currentState.enter();
	}
}

StateManager.prototype.redraw = function() {
	//First, delete everything from the canvas.
    GLOBALS.canvas.clearRect(0, 0, GLOBALS.canvas.width, GLOBALS.canvas.height);
	GLOBALS.drawWalls();
	GLOBALS.drawPoints();
	//Let the state draw itself
	this.currentState.draw();
}

StateManager.prototype.aboutToSnapToPoint = function(testPoint, recentlyAddedPoints) {
	var curPoint;
	var numPoints = recentlyAddedPoints.length;
	for (var i = 0; i < GLOBALS.points.length; i++) {
		curPoint = GLOBALS.points[i];
		if (testPoint.distance(curPoint) <= GLOBALS.snapRadius) {
			//Make sure that we're not snapping to the point we just added, if it exists.
			if (numPoints === 0 || (numPoints > 0 && !recentlyAddedPoints[numPoints - 1].equals(curPoint))) {
				return curPoint;
			}
		}
	}
	return null;
}

StateManager.prototype.aboutToSnapToLine = function(testPoint) {
	var curWall;
	for (var i = 0; i < GLOBALS.walls.length; i++) {
		curWall = GLOBALS.walls[i];
		if (curWall.pointNearLine(testPoint, GLOBALS.snapRadius)) {
			return curWall;
		}
	}
	return null;
}


