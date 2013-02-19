//line.js

/**
 * Summary: Constructor for the Line object.
 * Parameters: p1, p2: The points that comprise the start and end of the line.
 * Returns: undefined.
**/
function Line(p1, p2) {
	this.p1 = p1;
	this.p2 = p2;
	this.isSelected = false;
	this.isDoor = false;
	this.definesRoom = false;
	
	this.calculateForm(p1, p2);
}

Line.prototype.toOutput = function() {
	return {
		p1: this.p1.toOutput(),
		p2: this.p2.toOutput()
	};
}

/**
 * Summary: Put the line in standard form (ax + by = c)
 * Parameters: p1, p2: The end points of the line.
 * Returns: undefined (changes object variables)
**/
Line.prototype.calculateForm = function(p1, p2) {

	if (p1 === undefined || p2 === undefined) {
		return;
	}
	// Put line in form ax + by + c = 0
	this.a = p1.y - p2.y;
	this.b = p2.x - p1.x;
	this.c = p1.x * (p2.y - p1.y) - p1.y * (p2.x - p1.x);
	this.distConst = Math.sqrt(this.a * this.a + this.b * this.b);
}

/**
 * Summary: Get the string form of the line.
 * Parameters: this
 * Returns: The string form of this.
**/
Line.prototype.toString = function() {
	return "<" + this.p1.toString() + "," + this.p2.toString() + ">";
}

/**
 * Summary: Checks whether the given line is equal to the current object.
 * Parameters: l: The line to check against.
 * Returns: true iff l and this are the same line, or are flipped versions 
 * 		of each other.
**/
Line.prototype.equals = function (l) {
	if(util.exists(l)) {
		var isSameLine = (this.p1.equals(l.p1) && this.p2.equals(l.p2));
		var isFlippedLine = (this.p2.equals(l.p1) && this.p1.equals(l.p2));
		return isSameLine || isFlippedLine;
	}
	return false;
};

/**
 * Summary: Draw the line on the canvas.
 * Parameters: this
 * Returns: undefined.
**/
Line.prototype.draw = function () {	
	//Save the old stroke, so that we can restore it when we're done
	var oldStroke = CANVAS.strokeStyle;
	CANVAS.strokeStyle = 'rgba(0,180,0,1)';
	if (this.isSelected === true) {
		CANVAS.strokeStyle = "yellow"; // Yellow
	}
	if (this.isDoor === true) {
		CANVAS.strokeStyle = "pink";
	}
	CANVAS.lineWidth = WALL_WIDTH;
	CANVAS.beginPath();
	CANVAS.moveTo(this.p1.x,this.p1.y);
	CANVAS.lineTo(this.p2.x,this.p2.y);
	CANVAS.stroke();
	//Reset the stroke style
	CANVAS.strokeStyle = oldStroke;
};

/**
 * Summary: Basic setter method for the points of the line.
 * Parameters: this
 * Returns: undefined.
**/
Line.prototype.setPoints = function(p1, p2) {
	this.p1 = p1;
	this.p2 = p2;
};

/**
 * Summary: Plug a point's coordinates into the line's standard form equation.
 * Parameters: point: The point to plug in.
 * Returns: The value of the line at the given point.
**/	
Line.prototype.signPointToLine = function(point) {
	return this.a * point.x + this.b * point.y + this.c;
};

/**
 * Summary: Find the distance from the line to the given point.
 * Parameters: point: The point whose distance we want to find.
 * Returns: The distance of this to the given point.
**/	
Line.prototype.distanceToPoint = function(point) {
	return Math.abs(this.signPointToLine(point)) / this.distConst;
};

/**
 * Summary: Check whether the the given point is within radius distance of the line.
 * Parameters: point: The point to check, radius: The maximum distance allowed.
 * Returns: true iff the given point is within radius distance of the line.
**/	
Line.prototype.pointNearLine = function(point, radius) {
	//if(radius <= 0) {return this.pointOnLine(point);} //why doesnt this work?
	
	var close = (Math.abs(this.signPointToLine(point)) / this.distConst) <= radius;
	//Make sure the point is actually within the endpoints of the line.
	var onLine = ((this.p1.x >= point.x-radius && point.x+radius >= this.p2.x) ||
		 (this.p1.x <= point.x+radius && point.x-radius <= this.p2.x)) &&
		 ((this.p1.y >= point.y-radius && point.y+radius >= this.p2.y) ||
		 (this.p1.y <= point.y+radius && point.y-radius <= this.p2.y));
		 
	return close && onLine;
}

/**
 * Summary: Snap the given point to the line.
 * Parameters: point: The point to snap.
 * Returns: true iff the point snaps to the line.
**/	
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

	// Check that the new x,y coordinates are along the line segment
	if (((this.p1.x >= newX && newX >= this.p2.x) ||
		 (this.p1.x <= newX && newX <= this.p2.x)) &&
		 ((this.p1.y >= newY && newY >= this.p2.y) ||
		 (this.p1.y <= newY && newY <= this.p2.y))) {
			point.x = newX;
			point.y = newY;
			return true;
		 }
	return false;
	
};

/**
 * Summary: Break the line into 2 separate lines at the given point.
 * Parameters: point: The point at which we should cut the line.
 * Returns: A data structure containing the two new lines.
**/	
Line.prototype.breakIntoTwo = function(p) {
	var newLine1 = new Line(this.p1, p);
	var newLine2 = new Line(this.p2, p);
	return {l1: newLine1, l2: newLine2};
};

/**
 * Summary: Get the slop of the line.
 * Parameters: this
 * Returns: The slope of the line.
**/	
Line.prototype.getSlope = function() {
	if (this.b != 0) { // Avoid division by 0
		return -1.0 * this.a / this.b;
	}
	
	var b = epsilon
	return -1.0 * this.a / b;
};

/**
 * Summary: Assuming that the given point is one endpoint of the line, return the other endpoint.
 * Parameters: point: One endpoint of the line.
 * Returns: The other endpoint of the line.
**/	
Line.prototype.otherPoint = function(point) {
	if (point.equals(this.p1)) {
		return this.p2;
	}
	else {
		return this.p1;
	}
};


/**
 * Summary: Get the magnitude of the line (i.e. its length).
 * Parameters: this
 * Returns: The magnitude of the line.
**/	
Line.prototype.magnitutde = function() {
	var dx = Math.abs(this.p1.x - this.p2.x);
	var dy = Math.abs(this.p1.y - this.p2.y);
	return Math.sqrt(dx * dx + dy * dy);
};

