//Point.js

function importPoint(simplePoint) {
  if(util.exists(simplePoint)) {
    return new Point(simplePoint.x, simplePoint.y);
  }
  
  return null;
}

/**
 * Summary: The constructor for a Point object.
 * Parameters: x,y: The x- and y-location of the point (in pixels).
 * Returns: undefined
**/
function Point(x, y) {
	this.x = x;
	this.y = y;
	this.isSnap = false;
	this.isSelected = false;
	this.degree = 0;
}

/**
 * Summary: Checks whether the given Point is equal to the current object.
 * Parameters: p: The Point to check against.
 * Returns: true iff p and this have the same x and y coordinates.
**/
Point.prototype.toOutput = function() {
	return {x: this.x, y: this.y};
}

/**
 * Summary: Check whether the given point refers to the same pojnt as this.
 * Parameters: p: The point to check against for equality.
 * Returns: true iff the given point and this have the same x and y.
**/
Point.prototype.equals = function(p) {
	if (p === undefined) return false;
	else return (this.x === p.x && this.y === p.y); 
}

/**
 * Summary: Draw the point on the canvas.
 * Parameters: this
 * Returns: undefined
**/
Point.prototype.draw = function(pointColor) {
	if (pointColor === undefined) stateManager.currentFloor.globals.canvas.fillStyle = 'rgba(255,255,255,.6)';
	else stateManager.currentFloor.globals.canvas.fillStyle = pointColor;
	
	canvasPoint = stateManager.currentFloor.globals.view.toCanvasWorld(this);

	//If the point is being snapped to, draw a larger circle around it to make this fact clear to the user.
	/*if (this.isSnap) {
		stateManager.currentFloor.globals.canvas.beginPath();
		stateManager.currentFloor.globals.canvas.strokeStyle = "black";
		stateManager.currentFloor.globals.canvas.arc(canvasPoint.x, canvasPoint.y, SNAP_RADIUS, 0, 2*Math.PI, false);
		stateManager.currentFloor.globals.canvas.lineWidth = WALL_WIDTH;
		stateManager.currentFloor.globals.canvas.stroke();
		stateManager.currentFloor.globals.canvas.fillStyle = "rgb(255,0,255)";
		this.isSnap = false;
	}*/
	//Color the point yellow if it's currently selected.
	/*if (this.isSelected) {
		stateManager.currentFloor.globals.canvas.fillStyle = "rgb(255,0,0)";
	}*/
	stateManager.currentFloor.globals.canvas.beginPath();
    stateManager.currentFloor.globals.canvas.arc(canvasPoint.x, canvasPoint.y, 5, 0, 2*Math.PI, false);
    stateManager.currentFloor.globals.canvas.fill();
}

/**
 * Summary: Set whether this point is being snapped to.
 * Parameters: snap: true if the point is being snapped to, false otherwise
 * Returns: undefined (changes object variable)
**/
Point.prototype.setSnap = function(snap) {
	this.isSnap = snap;
}

/**
 * Summary: Get the string version of this point, which is comprised of its x- and y-values.
 * Parameters: this
 * Returns: The string version of this point.
**/
Point.prototype.toString = function() {
	return this.x + "," + this.y;
}

/**
 * Summary: Set the x- and y-coordinates of this point.
 * Parameters: x: the new x-coordinate, y: the new y-coordinate
 * Returns: undefined (changes object variables)
**/
Point.prototype.setXY = function(x,y) {
	this.x = x;
	this.y = y;
}

/**
 * Summary: Check whether this point and the given point have the same coordinates.
 * Parameters: p: the point to check against for equality
 * Returns: true iff this point and the given point have the same x- and y-coordinates.
**/
Point.prototype.equals = function(p) {
	return (this.x == p.x && this.y == p.y);
}

/**
 * Summary: Get the distance between the point and the given point.
 * Parameters: p: the point to check for distance against
 * Returns: The distance between this point and the given point.
**/
Point.prototype.distance = function(p) {
	var dx = Math.abs(this.x - p.x);
	var dy = Math.abs(this.y - p.y);
	return Math.sqrt(dx*dx + dy*dy);
}

function distance(point) {
	var dx = Math.abs(this.x - point.x);
	var dy = Math.abs(this.y - point.y);
	return Math.sqrt(dx * dx + dy * dy);
}
