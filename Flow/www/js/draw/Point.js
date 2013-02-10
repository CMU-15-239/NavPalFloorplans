//Point.js

/**
 * Summary: The constructor for a Point object.
 * Parameters: x,y: The x- and y-location of the point (in pixels).
 * Returns: Undefined.
**/
function Point(x, y) {
	this.x = x;
	this.y = y;
	this.isSnap = false;
	this.isSelected = false;
}

/**
 * Summary: Checks whether the given Point is equal to the current object.
 * Parameters: p: The Point to check against.
 * Returns: true iff p and this have the same x and y coordinates.
**/
Point.prototype.equals = function(p) {
	return (this.x == p.x && this.y = p.y); 
}

Point.prototype.draw = function(isSnapped) {
	CANVAS.fillStyle = POINT_COLOR;
	if (this.isSnap) {
		CANVAS.beginPath();
		CANVAS.arc(this.x, this.y, SNAP_RADIUS, 0, 2*Math.PI, false);
		CANVAS.lineWidth = WALL_WIDTH;
		CANVAS.stroke();
		CANVAS.fillStyle = "rgb(0,255,0)";
		this.isSnap = false;
	}
	CANVAS.beginPath();
    CANVAS.arc(this.x, this.y, POINT_SIZE, 0, 2*Math.PI, false);
    CANVAS.fill();
	CANVAS.fillStyle = POINT_COLOR;
	
	/*
	if (this.isSnap == true) {
		CANVAS.beginPath();
		CANVAS.arc(this.x, this.y, SNAP_RADIUS, 0, 2*Math.PI, false);
		CANVAS.lineWidth = WALL_WIDTH;
		CANVAS.stroke();
		this.isSnap = false; // Only snap to a single point at a time
	}*/
	
}

Point.prototype.setSnap = function(snap) {
	this.isSnap = snap;
}

Point.prototype.toString = function() {
	return this.x + "," + this.y;
}

Point.prototype.setXY = function(x,y) {
	this.x = x;
	this.y = y;
}

Point.prototype.equals = function(p) {
	return (this.x == p.x && this.y == p.y);
}

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
