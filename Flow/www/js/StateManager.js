var StateManager = function(building, canvas) {
	this.avaliableStates = {
		"Preprocess": new PreprocessState(this),
		"Draw": new DrawState(this),
		"Select": new SelectState(this),
		"Move": new MoveState(this),
		"ZoomIn": new ZoomInState(this),
		"ZoomOut": new ZoomOutState(this),
		"Pan": new PanState(this),
		"Landmark": new LandmarkState(this),
		"Door": new DoorState(this),
		"Stair": new StairState(this),
		"Classify": new ClassifyState(this)
	};
	
	if (building !== undefined) {
		this.building = building;
		this.floors = building.floors;
		this.changeFloor(this.floors[0]);
		for (var i = 0; i < this.floors.length; i++) {
			var curFloor = this.floors[i];
			curFloor.globals.setCanvas(canvas);
		}
	}
	this.currentState = this.avaliableStates["Draw"];
	this.currentState.enter();
	if (canvas !== undefined) this.canvas = canvas;
	
	// Global images
	this.landmarkImage = new Image();
	this.landmarkImage.src = "./img/landmark.png";
	
	this.stairImage = new Image();
	this.elevatorImage = new Image();
}

StateManager.prototype.changeState = function(newState) {
	if (this.currentState !== newState) {
		this.currentState.exit();
		this.currentState = this.avaliableStates[newState];
		this.currentState.enter();
		this.redraw();
	}
}

StateManager.prototype.changeFloor = function(newFloor) {
	console.log(this.currentFloor);
	console.log(this.newFloor);
	if (this.currentFloor !== newFloor) {
		this.currentFloor = newFloor;
		this.currentFloor.canvas = this.canvas;
	}
}

StateManager.prototype.changeBuilding = function(newBuilding) {
	if (this.currentBuilding !== newBuilding) {
	}
}

StateManager.prototype.getCurrentFloor = function() {
	return this.currentFloor;
}

StateManager.prototype.redraw = function() {
	this.updateSpaces();
	//First, delete everything from the canvas.
	//console.log(this.currentFloor.globals.view.offsetX);
    this.currentFloor.globals.canvas.clearRect(0, 0, this.currentFloor.globals.canvas.width, this.currentFloor.globals.canvas.height);
	
	sx = this.currentFloor.globals.view.offsetX;
	sy = this.currentFloor.globals.view.offsetY;
	dx = 0;
	dy = 0;
	
	zoom = this.currentFloor.globals.view.scale;
	
	if (this.currentFloor.globals.view.offsetX < 0) {
		dx = -1 * this.currentFloor.globals.view.offsetX;
		sx = 0
	}
	if (this.currentFloor.globals.view.offsetY < 0) {
		dy = -1 * this.currentFloor.globals.view.offsetY;
		sy = 0
	}

	if (this.currentFloor.globals.canvas.image === undefined) {
		;
	}
	else {
		this.currentFloor.globals.canvas.drawImage(this.currentFloor.globals.canvas.image,
		sx, sy,
		this.currentFloor.globals.canvas.image.width - sx, this.currentFloor.globals.canvas.image.height - sy,
		dx * zoom,dy * zoom,
		zoom * (this.currentFloor.globals.canvas.image.width -  sx), zoom * (this.currentFloor.globals.canvas.image.height -  sy));
	}
	this.currentFloor.globals.drawWalls();
	this.currentFloor.globals.drawPoints();
	this.currentFloor.globals.drawLandmarks();
	this.currentFloor.drawSpaces();
	//Let the state draw itself
	this.currentState.draw();
}

StateManager.prototype.aboutToSnapToPoint = function(testPoint, recentlyAddedPoints) {
	if (recentlyAddedPoints !== undefined) {
		var curPoint;
		var numPoints = recentlyAddedPoints.length;
		for (var i = 0; i < this.currentFloor.globals.points.length; i++) {
			curPoint = this.currentFloor.globals.points[i];
			// Snap radius depends on the scale
			if (testPoint.distance(curPoint) <= this.currentFloor.globals.snapRadius / this.currentFloor.globals.view.scale) {
				//Make sure that we're not snapping to the point we just added, if it exists.
				if (numPoints === 0 || (numPoints > 0 && !recentlyAddedPoints[numPoints - 1].equals(curPoint))) {
					return curPoint;
				}
			}
		}
		return null;
	}
	else {
		var curPoint;
		for (var i = 0; i < this.currentFloor.globals.points.length; i++) {
			curPoint = this.currentFloor.globals.points[i];
			// Snap radius depends on the scale
			if (testPoint.distance(curPoint) <= this.currentFloor.globals.snapRadius / this.currentFloor.globals.view.scale) {
				if (curPoint !== testPoint) return curPoint;
			}
		}
		return null;
	}
}

StateManager.prototype.aboutToSnapToLine = function(testPoint) {
	var curWall;
	for (var i = 0; i < this.currentFloor.globals.walls.length; i++) {
		curWall = this.currentFloor.globals.walls[i];
		// Snap radius depends on scale
		if (curWall.pointNearLine(testPoint, this.currentFloor.globals.snapRadius / this.currentFloor.globals.view.scale)) {
			return curWall;
		}
	}
	return null;
}

// Allow for mousewheel scrolling in ANY state
StateManager.prototype.scroll = function(event) {

	// Consider: this.currentFloor.globals.canvas.width - event.originalEvent.layerX [WLOG Y]
	// It provices a different feel for zoom out
	this.currentFloor.globals.view.zoomCanvasPoint(event.originalEvent.wheelDeltaY > 0, 
								 new Point(event.originalEvent.layerX, 
										   event.originalEvent.layerY));
	
	this.redraw();
}

StateManager.prototype.updateSpaces = function () {
	var curSpaces = stateManager.currentFloor.spaces;
	var curWalls = stateManager.currentFloor.globals.walls;
	var allSpaces = detectRooms(curWalls, curSpaces);
	stateManager.currentFloor.spaces = allSpaces;
}

StateManager.prototype.hoverRoomLabel = function(realWorldPoint, canvasPoint) {
	var xOffset = 15;
	var yOffset = 10;
	var allSpaces = stateManager.currentFloor.spaces;
	for (var i = 0; i < allSpaces.length; i++) {
		var curSpace = allSpaces[i];
		//var width = stateManager.currentFloor.globals.canvas.width; TOO SMALL WHEN ZOOMING
		var width = 5000;
		if (curSpace.pointInSpace(canvasPoint, width, false)) {
			var label = curSpace.label;
			$("#room_label").html(label);
			$("#room_label").css({
				top: (realWorldPoint.y + yOffset) + "px",
				left: (realWorldPoint.x + xOffset) + "px",
				display: "inline-block"
			});
			return;
		}
	}
	$("#room_label").css("display", "none");
}

