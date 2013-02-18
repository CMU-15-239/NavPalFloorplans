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
	
	this.calculateForm(p1, p2);
}

Line.prototype.toOutput = function() {
	return {
		p1: this.p1.toOutput(),
		p2: this.p2.toOutput()
	};
}

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

Line.prototype.draw = function (drawPoints) {
	if (drawPoints === true) {
		//this.p1.draw();
		//this.p2.draw();
	}
	
	var oldStroke = CANVAS.strokeStyle;
	if (this.isSelected === true) {
		CANVAS.strokeStyle = "yellow";
	}
	CANVAS.lineWidth = WALL_WIDTH;
	CANVAS.beginPath();
	CANVAS.moveTo(this.p1.x,this.p1.y);
	CANVAS.lineTo(this.p2.x,this.p2.y);
	CANVAS.stroke();
	//Reset the stroke style
	CANVAS.strokeStyle = oldStroke;
};

Line.prototype.setPoints = function(p1, p2) {
	this.p1 = p1;
	this.p2 = p2;
};
	
Line.prototype.signPointToLine = function(point) {
	return this.a * point.x + this.b * point.y + this.c;
};

Line.prototype.distanceToPoint = function(point) {
	return Math.abs(this.signPointToLine(point)) / this.distConst;
};

Line.prototype.pointOnLine = function(point) {
	var lineLenSq = util.distance(this.p1.x, this.p1.y, this.p2.x, this.p2.y);
	var p1ToPointSq = util.distance(this.p1.x, this.p1.y, point.x, point.y);
	var p2ToPointSq = util.distance(this.p2.x, this.p2.y, point.x, point.y);
	return lineLenSq === p1ToPointSq + p2ToPointSq;
};

Line.prototype.pointNearLine = function(point, radius) {
	if(radius <= 0) {return this.pointOnLine(point);}
	
	var close = (Math.abs(this.signPointToLine(point)) / this.distConst) <= radius;
	var onLine = ((this.p1.x >= point.x && point.x >= this.p2.x) ||
		 (this.p1.x <= point.x && point.x <= this.p2.x)) &&
		 ((this.p1.y >= point.y && point.y >= this.p2.y) ||
		 (this.p1.y <= point.y && point.y <= this.p2.y));
		 
	return close && onLine;
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
			return true;
		 }
	return false;
	
};

Line.prototype.breakIntoTwo = function(p) {
	var newLine1 = new Line(this.p1, p);
	var newLine2 = new Line(this.p2, p);
	return {l1: newLine1, l2: newLine2};
};

Line.prototype.getSlope = function() {
	if (this.b != 0) { // Avoid division by 0
		return -1.0 * this.a / this.b;
	}
	
	var b = epsilon
	return -1.0 * this.a / b;
};

// Assume that point is on this line, return the other point
Line.prototype.otherPoint = function(point) {
	if (point.equals(this.p1)) {
		return this.p2;
	}
	else {
		return this.p1;
	}
};

Line.prototype.magnitutde = function() {
	var dx = Math.abs(this.p1.x - this.p2.x);
	var dy = Math.abs(this.p1.y - this.p2.y);
	return Math.sqrt(dx * dx + dy * dy);
};

