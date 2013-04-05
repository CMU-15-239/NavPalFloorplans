var StateManager = function() {
	this.avaliableStates = {
		"Preprocess": new PreprocessState(this),
		"Draw": new DrawState(this),
		"Select": new SelectState(this),
		"Move": new MoveState(this),
		"ZoomIn": new ZoomState(this),
		"ZoomOut": new ZoomState(this),
		"Pan": new PanState(this)
	};
	
	this.currentState = this.avaliableStates["Draw"];
	this.currentState.enter();
}

StateManager.prototype.changeState = function(newState) {
	if (this.currentState !== newState) {
		this.currentState.exit();
		this.currentState = this.avaliableStates[newState];
		this.currentState.enter();
		this.redraw();
	}
}

StateManager.prototype.redraw = function() {
	//First, delete everything from the canvas.
	//console.log(GLOBALS.view.offsetX);
    GLOBALS.canvas.clearRect(0, 0, GLOBALS.canvas.width, GLOBALS.canvas.height);
	/*GLOBALS.canvas.drawImage(GLOBALS.canvas.image,
	GLOBALS.view.offsetX, GLOBALS.view.offsetY,
	GLOBALS.canvas.image.width - GLOBALS.view.offsetX, GLOBALS.canvas.image.height - GLOBALS.view.offsetY,
	50,50,
	GLOBALS.canvas.image.width -  GLOBALS.view.offsetX, GLOBALS.canvas.image.height -  GLOBALS.view.offsetY);*/
	
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
		// Snap radius depends on the scale
		if (testPoint.distance(curPoint) <= GLOBALS.snapRadius / GLOBALS.view.scale) {
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
		// Snap radius depends on scale
		if (curWall.pointNearLine(testPoint, GLOBALS.snapRadius / GLOBALS.view.scale)) {
			return curWall;
		}
	}
	return null;
}

// Allow for mousewheel scrolling in ANY state
StateManager.prototype.scroll = function(event) {

	// Consider: GLOBALS.canvas.width - event.originalEvent.layerX [WLOG Y]
	// It provices a different feel for zoom out
	GLOBALS.view.zoomCanvasPoint(event.originalEvent.wheelDeltaY > 0, 
								 new Point(event.originalEvent.layerX, 
										   event.originalEvent.layerY));
	
	this.redraw();
}

