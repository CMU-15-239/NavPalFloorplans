//line.js

/**
 * Summary: Constructor for the Line object.
 * Parameters: p1, p2: The points that comprise the start and end of the line.
 * Returns: undefined.
**/
function Line(p1, p2) {
	this.p1 = p1;
	this.p2 = p2;
	
	// Put line in form ax + by + c = 0
	this.a = p1.y - p2.y;
	this.b = p2.x - p1.x;
	this.c = p1.x * (p2.y - p1.y) - p1.y * (p2.x - p1.x);
	this.distConst = Math.sqrt(this.a * this.a + this.b * this.b);
}

/**
 * Summary: Checks whether the given line is equal to the current object.
 * Parameters: l: The line to check against.
 * Returns: true iff l and this are the same line, or are flipped versions 
 * 		of each other.
**/
Line.prototype.equals = function (l) {
	var isSameLine = (this.p1.equals(l.p1) && this.p2.equals(l.p2));
	var isFlippedLine = (this.p2.equals(l.p1) && this.p1.equals(l.p2));
	return isSameLine || isFlippedLine;
}

Line.prototype.draw = function (drawPoints) {
	if (drawPoints === true) {
		//this.p1.draw();
		//this.p2.draw();
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
	
Line.prototype.signPointToLine = function(point) {
	return this.a * point.x + this.b * point.y + this.c;
}

Line.prototype.distanceToPoint = function(point) {
	return Math.abs(this.signPointToLine(point)) / this.distConst;
}

Line.prototype.snapToLine = function(point) {
	var d = this.distanceToPoint(point);
	var ratio = d / this.distConst;
	var dx = ratio * this.a;
	var dy = ratio * this.b;
	if (this.signPointToLine(point) > 0) {
		dx *= -1;
		dy *= -1;
	} 
	
	var newX = point.x + dx;
	var newY = point.y + dy;
	
	//point.x = newX;
	//point.y = newY;
	
	//console.log("new " + newX + "," + newY + "  and points:" + this.
	// Check that the new x,y coordinates are along the line segment
	if (((this.p1.x >= newX && newX >= this.p2.x) ||
		 (this.p1.x <= newX && newX <= this.p2.x)) &&
		 ((this.p1.y >= newY && newY >= this.p2.y) ||
		 (this.p1.y <= newY && newY <= this.p2.y))) {
			point.x = newX;
			point.y = newY;
		 }
	
}