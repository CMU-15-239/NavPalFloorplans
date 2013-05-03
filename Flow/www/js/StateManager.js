/**
	StateManager.js
	Written by Justin Greet
	justin.greet11@gmail.com
	Spring 2013
	
	This file manages which tool is currently in control in the editing tool.
	It is based on the state design pattern. See http://en.wikipedia.org/wiki/State_pattern
	for more information on this design pattern. Thanks to 
	http://robdodson.me/blog/2012/06/02/take-control-of-your-app-with-the-javascript-state-patten/
	for giving me some inspiration for the design. 
	
*/

var StateManager = function(building, canvas) {
	//All of the avaliable tools. To add a new one, simply
	//add it below, modify the html in authoringTool.html,
	//and make a class to implement the behavior.
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
		"Elevator": new ElevatorState(this),
		"Classify": new ClassifyState(this),
		"Exit": new ExitState(this)
	};
	//If we're given a building object, make it a part of
	//the tool.
	if (building !== undefined) {
		this.building = building;
		this.floors = building.floors;
		this.changeFloor(this.floors[0]);
		for (var i = 0; i < this.floors.length; i++) {
			var curFloor = this.floors[i];
			curFloor.globals.setCanvas(canvas);
		}
	}
	//Initialize to the draw state.
	this.currentState = this.avaliableStates["Draw"];
	this.currentState.enter();
	if (canvas !== undefined) this.canvas = canvas;
	
	// Global images
	this.landmarkImage = new Image();
	this.landmarkImage.src = "./img/landmark.png";
	this.landmarkImageBlue = new Image();
	this.landmarkImageBlue.src = "./img/landmarkBlue.png";
	
	this.stairImage = new Image();
	this.stairImage.src = "./img/stairs.png";
	this.elevatorImage = new Image();
	this.elevatorImage.src = "./img/elevator.png";
}

/**
 * Summary: Change the state of the tool.
 * Parameters: newState: The state to enter.
 * Returns: undefined.
**/
StateManager.prototype.changeState = function(newState) {
	//Make sure we're not switching to the current state.
	if (this.currentState !== newState) {
		this.currentState.exit();
		this.currentState = this.avaliableStates[newState];
		this.currentState.enter();
		this.redraw();
	}
}

/**
 * Summary: Change the floor the user is editing.
 * Parameters: newFloor: The floor to edit next.
 * Returns: undefined.
**/
StateManager.prototype.changeFloor = function(newFloor) {
	//Make sure we're not switching to the current floor.
	if (this.currentFloor !== newFloor) {
		this.currentFloor = newFloor;
		this.currentFloor.canvas = this.canvas;
	}
}

/**
 * Summary: A getter for the current floor.
 * Parameters: none
 * Returns: The current floor.
**/
StateManager.prototype.getCurrentFloor = function() {
	return this.currentFloor;
}

/**
 * Summary: Reset then redraw the contents of the canvas.
 * Parameters: none
 * Returns: undefined.
**/
StateManager.prototype.redraw = function() {
	this.updateSpaces();
	//First, delete everything from the canvas.
    this.currentFloor.globals.canvas.clearRect(0, 0, this.currentFloor.globals.canvas.width, this.currentFloor.globals.canvas.height);
	
	// Draw the floorplan in the background
	var sx = this.currentFloor.globals.view.offsetX;
	var sy = this.currentFloor.globals.view.offsetY;
	var dx = 0;
	var dy = 0;
	
	var zoom = this.currentFloor.globals.view.scale;
	
	if (this.currentFloor.globals.view.offsetX < 0) {
		dx = -1 * this.currentFloor.globals.view.offsetX;
		sx = 0
	}
	if (this.currentFloor.globals.view.offsetY < 0) {
		dy = -1 * this.currentFloor.globals.view.offsetY;
		sy = 0
	}

	if (util.exists(this.currentFloor.globals.canvas.image)) {
		this.currentFloor.globals.canvas.drawImage(this.currentFloor.globals.canvas.image,
			sx, sy,
			this.currentFloor.globals.canvas.image.width - sx, this.currentFloor.globals.canvas.image.height - sy,
			dx * zoom,dy * zoom,
			zoom * (this.currentFloor.globals.canvas.image.width -  sx), zoom * (this.currentFloor.globals.canvas.image.height -  sy));
	}
	
	//Finally, draw all the components that are persistent across state.
	this.currentFloor.globals.drawWalls();
	this.currentFloor.globals.drawPoints();
	this.currentFloor.drawLandmarks();
	this.currentFloor.drawStairs();
	this.currentFloor.drawSpaces();
	//Let the current state draw itself
	this.currentState.draw();
}

/**
 * Summary: Check if testPoint is close enough to snap to a point.
 * Parameters: testPoint: The point to check for snapping.
 * 		recentlyAddedPoints: Cycle through these points and check if testPoint is close.
 * Returns: The point that testPoint can snap to. Null if none exist.
**/
StateManager.prototype.aboutToSnapToPoint = function(testPoint, recentlyAddedPoints) {
	if (util.exists(recentlyAddedPoints)) {
		var curPoint;
		var numPoints = recentlyAddedPoints.length;
		//Cycle through the points and check if testPoint is close.
		for (var i = 0; i < this.currentFloor.globals.points.length; i++) {
			curPoint = this.currentFloor.globals.points[i];
			// Snap radius depends on the scale
			if (testPoint.distance(curPoint) <= this.currentFloor.globals.snapRadius / this.currentFloor.globals.view.scale) {
				//Make sure that we're not snapping to the point we just added, if it exists.
				if (numPoints === 0 || (numPoints > 0 && !recentlyAddedPoints[numPoints - 1].equals(curPoint))) {
					//Return so we can only snap to one point.
					return curPoint;
				}
			}
		}
		return null;
	}
	//If recentlyAddedPoints doesn't exist, check testPoint against all the points in the canvas.
	else {
		var curPoint;
		//Cycle through the points and check if testPoint is close.
		for (var i = 0; i < this.currentFloor.globals.points.length; i++) {
			curPoint = this.currentFloor.globals.points[i];
			// Snap radius depends on the scale
			if (testPoint.distance(curPoint) <= this.currentFloor.globals.snapRadius / this.currentFloor.globals.view.scale) {
				//Return so we can only snap to one point.
				if (curPoint !== testPoint) return curPoint;
			}
		}
		return null;
	}
}

/**
 * Summary: Check if testPoint is close enough to snap to any line on the canvas.
 * Parameters: testPoint: The point to check for snapping.
 * Returns: The line that testPoint can snap to. Null if none exist.
**/
StateManager.prototype.aboutToSnapToLine = function(testPoint) {
	var curWall;
	//Cycle through the lines and check if testPoint is close.
	for (var i = 0; i < this.currentFloor.globals.walls.length; i++) {
		curWall = this.currentFloor.globals.walls[i];
		// Snap radius depends on scale
		if (curWall.pointNearLine(testPoint, this.currentFloor.globals.snapRadius / this.currentFloor.globals.view.scale)) {
			//Return so we only ever snap to 1 line at a time.
			return curWall;
		}
	}
	return null;
}

/**
 * Summary: Have the mouse wheel zoom in and out across ANY state.
 * Parameters: event: the event that encapsulates the mouse wheel movement
 * Returns: undefined
**/
// Allow for mousewheel scrolling in ANY state
StateManager.prototype.scroll = function(event) {

	// Consider: this.currentFloor.globals.canvas.width - event.originalEvent.layerX [WLOG Y]
	// It provices a different feel for zoom out
	this.currentFloor.globals.view.zoomCanvasPoint(event.originalEvent.wheelDeltaY > 0, 
								 new Point(event.originalEvent.layerX, 
										   event.originalEvent.layerY));
	
	this.redraw();
}

/**
 * Summary: Update the spaces to reflect any new changes (i.e. new line drawn, movement, etc.)
 * Parameters: none
 * Returns: undefined
**/
StateManager.prototype.updateSpaces = function () {
	var curSpaces = stateManager.currentFloor.spaces;
	var curWalls = stateManager.currentFloor.globals.walls;
	//Get the updated space objects.
	var allSpaces = detectRooms(curWalls, curSpaces);
	stateManager.currentFloor.spaces = allSpaces;
}

/**
 * Summary: Display the room number of the space currently being hovered
 *		if it exists.
 * Parameters: realWorldPoint: the location of the cursor in real world terms
 *		canvasPoint: the location of the cursor in canvas terms
 * Returns: undefined
**/
StateManager.prototype.hoverRoomLabel = function(realWorldPoint, canvasPoint) {
	//The distance away from the cursor to display the label.
	var xOffset = 15;
	var yOffset = 10;
	var allSpaces = stateManager.currentFloor.spaces;
	//Cycle through the spaces to check if the point lies in one.
	for (var i = 0; i < allSpaces.length; i++) {
		var curSpace = allSpaces[i];
		//An arbitrarily large width.
		var width = 5000;
		if (curSpace.pointInSpace(canvasPoint, width, false)) {
			//If the point is in a space, display its label.
			var label = curSpace.label;
			$("#room_label").html(label);
			$("#room_label").css({
				top: (realWorldPoint.y + yOffset) + "px",
				left: (realWorldPoint.x + xOffset) + "px",
				display: "inline-block"
			});
			//Ensure we only ever display one label at a time.
			return;
		}
	}
	//If the point isn't in a space, don't display anything.
	$("#room_label").css("display", "none");
}

