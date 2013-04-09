var StateManager = function() {
	this.avaliableStates = {
		"Preprocess": new PreprocessState(this),
		"Draw": new DrawState(this),
		"Select": new SelectState(this),
		"Move": new MoveState(this),
		"ZoomIn": new ZoomInState(this),
		"ZoomOut": new ZoomOutState(this),
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
	
	sx = GLOBALS.view.offsetX;
	sy = GLOBALS.view.offsetY;
	dx = 0;
	dy = 0;
	
	zoom = GLOBALS.view.scale;
	
	if (GLOBALS.view.offsetX < 0) {
		dx = -1 * GLOBALS.view.offsetX;
		sx = 0
	}
	if (GLOBALS.view.offsetY < 0) {
		dy = -1 * GLOBALS.view.offsetY;
		sy = 0
	}

	if (GLOBALS.canvas.image === undefined) {
		;
	}
	else {
		GLOBALS.canvas.drawImage(GLOBALS.canvas.image,
		sx, sy,
		GLOBALS.canvas.image.width - sx, GLOBALS.canvas.image.height - sy,
		dx * zoom,dy * zoom,
		zoom * (GLOBALS.canvas.image.width -  sx), zoom * (GLOBALS.canvas.image.height -  sy));
	}
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

