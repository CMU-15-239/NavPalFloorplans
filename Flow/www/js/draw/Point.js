//Point.js

/**
 * Summary: The constructor for a Point object.
 * Parameters: x,y: The x- and y-location of the point (in pixels).
 * Returns: Undefined.
**/
function Point(x, y) {
	this.x = x;
	this.y = y;
}

/**
 * Summary: Checks whether the given Point is equal to the current object.
 * Parameters: p: The Point to check against.
 * Returns: true iff p and this have the same x and y coordinates.
**/
Point.prototype.equals = function(Point p) {
	return (this.x == p.x && this.y = p.y); 
};

function distance(point) {
	var dx = Math.abs(this.x - point.x);
	var dy = Math.abs(this.y - point.y);
	return Math.sqrt(dx * dx + dy * dy);
}
