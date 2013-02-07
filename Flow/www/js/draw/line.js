//line.js

/**
 * Summary: Constructor for the Line object.
 * Parameters: p1, p2: The points that comprise the start and end of the line.
 * Returns: undefined.
**/
function Line(Point p1, Point p2) {
	this.p1 = p1;
	this.p2 = p2;
	
	// Put line in form ax + by + c = 0
	this.a = p2.x - p1.x;
	this.b = p1.y - p2.y;
	this.c = p1.x * (p2.y - p1.y) - p1.y * (p2.x - p1.x);
	this.distConst = Math.sqrt(a * a + b * b);
}

/**
 * Summary: Checks whether the given line is equal to the current object.
 * Parameters: l: The line to check against.
 * Returns: true iff l and this are the same line, or are flipped versions 
 * 		of each other.
**/
Line.prototype.equals(Line l) {
	var isSameLine = (this.p1 == l.p1 && this.p2 == this.p2);
	var isFlippedLine = (this.p2 == l.p1 && this.p1 == l.p2);
	return isSameLine || isFlippedLine;
}

function distanceToPoint(point) {
	return Math.abs(this.a * point.x + this.b * point.y + this.c) / this.distConst;
}