var POLYGON_COLOR = 'rgba(51,153,255,.5)';

/**
 * Summary: Constructor for the Polygon object.
 * Parameters: lines: An array of Line objects
 * Returns: undefined.
**/
function Polygon(lines) {
	this.lines = lines;
	this.color = POLYGON_COLOR;
}

Polygon.prototype.draw = function() {
	var oldStyle = CANVAS.fillStyle;
	CANVAS.fillStyle = this.color;
	CANVAS.beginPath();
	CANVAS.moveTo(this.lines[0].p1.x, this.lines[0].p1.y);
	for (var i = 0; i < this.lines.length; i++) {
		var curLine = this.lines[i];
		CANVAS.lineTo(curLine.p2.x, curLine.p2.y);
	}
	CANVAS.closePath();
	CANVAS.fill();
	CANVAS.fillStyle = oldStyle;
}