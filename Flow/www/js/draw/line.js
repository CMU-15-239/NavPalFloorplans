//line.js

/**
 * Summary: Constructor for the Line object.
 * Parameters: p1, p2: The points that comprise the start and end of the line.
 * Returns: undefined.
**/
function Line(p1, p2) {
	this.p1 = p1;
	this.p2 = p2;
}

/**
 * Summary: Checks whether the given line is equal to the current object.
 * Parameters: l: The line to check against.
 * Returns: true iff l and this are the same line, or are flipped versions 
 * 		of each other.
**/
Line.prototype.equals = function (l) {
	var isSameLine = (this.p1 == l.p1 && this.p2 == l.p2);
	var isFlippedLine = (this.p2 == l.p1 && this.p1 == l.p2);
	return isSameLine || isFlippedLine;
}

Line.prototype.draw = function (drawPoints) {
	if (drawPoints === true) {
		this.p1.draw();
		this.p2.draw();
	}
	
	//console.log("p1: (" + this.p1.x + ", " + this.p1.y + ")    p2: (" + this.p2.x + ", " + this.p2.y + ")");
	
	CANVAS.lineWidth = WALL_WIDTH;
	CANVAS.beginPath();
	CANVAS.moveTo(this.p1.x,this.p1.y);
	CANVAS.lineTo(this.p2.x,this.p2.y);
	CANVAS.stroke();
}

Line.prototype.setPoints = function(p1, p2) {
	this.p1 = p1;
	this.p2 = p2;
}