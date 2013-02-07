//line.js

/**
 * Summary: Constructor for the Line object.
 * Parameters: p1, p2: The points that comprise the start and end of the line.
 * Returns: undefined.
**/
function Line(Point p1, Point p2) {
	this.p1 = p1;
	this.p2 = p2;
}

/**
 * Summary: Checks whether the given line is equal to the current object.
 * Parameters: l: The line to check against.
 * Returns: true iff l and this are the same line, or are flipped versions 
 * 		of each other.
**/
Line.prototype.equals(Line l) {
	var isSameLine = (this.p1 == l.p1 && this.p2 == 1.p2);
	var isFlippedLine = (this.p2 == l.p1 && this.p1 == l.p2);
	return isSameLine || isFlippedLine;
}