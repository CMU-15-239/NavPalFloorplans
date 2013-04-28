//line.js

function importLine(simpleLine) {
  
  if(util.exists(simpleLine)) {
    var p1 = importPoint(simpleLine.p1);
    var p2 = importPoint(simpleLine.p2);
    
    return new Line(p1, p2, simpleLine.isDoor);
  }
  
  return null;
}

function importLineFromPoints(p1, p2, isDoor) {
  if(util.exists(p1) && util.exists(p2)) {
    var line = new Line(p1, p2, isDoor);
    return line;
  }
  
  return null;
}



/**
 * Summary: Constructor for the Line object.
 * Parameters: p1, p2: The points that comprise the start and end of the line.
 * Returns: undefined.
**/
function Line(p1, p2, isDoor) {
	this.p1 = p1;
	this.p2 = p2;
	this.isSelected = false;
	this.isDoor = (isDoor === true);
	this.isExit = false;
	this.definesRoom = false;
	
	this.calculateForm(p1, p2);
}

Line.prototype.toOutput = function() {
	return {
		p1: this.p1.toOutput(),
		p2: this.p2.toOutput(),
    isDoor: this.isDoor
	};
};

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
};

/**
 * Summary: Get the string form of the line.
 * Parameters: this
 * Returns: The string form of this.
**/
Line.prototype.toString = function() {
	return "<" + this.p1.toString() + "," + this.p2.toString() + ">";
};

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
Line.prototype.draw = function (lineColor) {	
	canvasP1 = stateManager.currentFloor.globals.view.toCanvasWorld(this.p1);
	canvasP2 = stateManager.currentFloor.globals.view.toCanvasWorld(this.p2);
	
	//Save the old stroke, so that we can restore it when we're done
	var oldStroke = stateManager.currentFloor.globals.canvas.strokeStyle;
	stateManager.currentFloor.globals.canvas.strokeStyle = 'rgba(0,180,0,0.6)';
	if (this.isDoor === true) {
		stateManager.currentFloor.globals.canvas.strokeStyle = 'rgba(188,0,255,0.8)';
	}
	if (this.isExit === true) {
		stateManager.currentFloor.globals.canvas.strokeStyle = 'rgba(100,100,100,0.8)';
	}
	if (this.isSelected === true) {
		stateManager.currentFloor.globals.canvas.strokeStyle = 'rgba(0,132,240,1)'; // Yellow
	}
	if (lineColor !== undefined) stateManager.currentFloor.globals.canvas.strokeStyle = lineColor;
	stateManager.currentFloor.globals.canvas.lineWidth = WALL_WIDTH;
	stateManager.currentFloor.globals.canvas.beginPath();
	stateManager.currentFloor.globals.canvas.moveTo(canvasP1.x, canvasP1.y);
	stateManager.currentFloor.globals.canvas.lineTo(canvasP2.x, canvasP2.y);
	stateManager.currentFloor.globals.canvas.stroke();
	//Reset the stroke style
	stateManager.currentFloor.globals.canvas.strokeStyle = oldStroke;
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
};

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
	//Don't want to split up line on its endpoints.
	if (this.p1.equals(p) || this.p2.equals(p)) return;
	var newLine1 = new Line(this.p1, p);
	var newLine2 = new Line(this.p2, p);
	newLine1.calculateForm(this.p1, p);
	newLine2.calculateForm(this.p2, p);
	this.calculateForm(this.p1, this.p2);
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

Line.prototype.pointOfLineIntersection = function(line) {
	//If they have a common endpoint, they don't intersect in any meaningful way.
	if (this.p1.equals(line.p1) || this.p1.equals(line.p2) || this.p2.equals(line.p1) || this.p2.equals(line.p2)){
		return null;
	}
	//Now check to see if one of the endpoints of one of the lines intersects the other line.
	var smallRadius = .0001;
	if (this.pointNearLine(line.p1, smallRadius)) return line.p1;
	else if (this.pointNearLine(line.p2, smallRadius)) return line.p2;
	else if (line.pointNearLine(this.p1, smallRadius)) return this.p1;
	else if (line.pointNearLine(this.p2, smallRadius)) return this.p2;
  
	var epsilon = .01;
	
	//The following can't be 0
	var checkOne = Math.abs(line.a*this.b - this.a*line.b) > epsilon;
	var checkTwo = Math.abs(line.b * this.b) > epsilon;
	
	if (!checkOne || !checkTwo) return null;
	
	//The equation for checking the x-value of the intersection of two lines in standard form.
	var xIntersect = -1*(line.b*this.c - this.b*line.c)/(this.a*line.b - line.a*this.b);
	
	//Now check that the x-value falls on both lines.
	var fallsOnThis = ((this.p1.x >= xIntersect && xIntersect >= this.p2.x) || 
		(this.p1.x <= xIntersect && xIntersect <= this.p2.x));
		
	var fallsOnLine = ((line.p1.x >= xIntersect && xIntersect >= line.p2.x) || 
		(line.p1.x <= xIntersect && xIntersect <= line.p2.x));
		
	if (fallsOnThis && fallsOnLine) {
		//Doesn't matter which line we use to calculate y-value, because they're equal at this x.
		var yIntersect = (-1*this.c - this.a*xIntersect)/(this.b);
		return new Point(xIntersect, yIntersect);
	}
	
	//There is no valid point of intersection.
	return null;
};

Line.prototype.splitUpLine = function(setOfPoints) {
	var sortedPoints = this.sortPoints(setOfPoints);
	var newLineSegments = [];
	var lineToSplit = this;
	for (var i = 0; i < sortedPoints.length; i++) {
		var newSegs = lineToSplit.breakIntoTwo(sortedPoints[i]);
		if (newSegs !== undefined) {
			//Since we sorted starting at p1, we want to split the segment that includes p2
			if (newSegs.l1.p1.equals(this.p2) || newSegs.l1.p2.equals(this.p2)) {
				newLineSegments.push(newSegs.l2);
				lineToSplit = newSegs.l1;
				//The last time through, we should add both sections
				if (i === sortedPoints.length - 1) newLineSegments.push(newSegs.l1);
			}
			else if (newSegs.l2.p1.equals(this.p2) || newSegs.l2.p2.equals(this.p2)) {
				newLineSegments.push(newSegs.l1);
				lineToSplit = newSegs.l2;
				//The last time through, we should add both sections
				if (i === sortedPoints.length - 1) newLineSegments.push(newSegs.l2);
			}
		}
		//Accounts for the case in which p2 of this line lies on an existing line.
		else if (i === sortedPoints.length - 1) newLineSegments.push(lineToSplit);
	}
	return newLineSegments;
};

Line.prototype.sortPoints = function(points) {
	//First find the closest point to p1 (could be p2 just as well) and put it at the front,
	//because it will act as our starting point for sorting
	var closestDistance = 100000000000;
	var startingPointIndex;
	for (var i = 0; i < points.length; i++) {
		var pointToCheck = points[i];
		var distanceTop1 = pointToCheck.distance(this.p1);
		if (distanceTop1 < closestDistance) {
			startingPointIndex = i;
			closestDistance = distanceTop1;
		}
	}
	//Now remove it and put it at the front. Splice returns an array.
	var startingPoint = points.splice(startingPointIndex, 1); 
	startingPoint = startingPoint[0];
	points.unshift(startingPoint);
	
	//Now go through each remaining point and sort it, stopping when all the points are sorted.
	var closestPoint;
	var numSorted = 1;
	var sortedPoints = [];
	sortedPoints.push(startingPoint);
	closestDistance = 100000000000;
	while (numSorted < points.length) {
		//We want to find the closest point to the most recent one that we've sorted.
		var pointToCheck = sortedPoints[numSorted - 1];
		for (var j = 0; j < points.length; j++) {
			var curPoint = points[j];
			if (!curPoint.equals(pointToCheck) &&
				!this.containsPoint(sortedPoints, curPoint) && 
				(pointToCheck.distance(curPoint) < closestDistance)) {
				closestPoint = curPoint;
				closestDistance = pointToCheck.distance(curPoint);
			}
		}
		sortedPoints.push(closestPoint);
		closestDistance = 10000000000000;
		numSorted += 1;
	}
	return sortedPoints;
};

Line.prototype.containsPoint = function(pointList, point) {
	for (var i = 0; i < pointList.length; i++) {
		if (pointList[i].equals(point)) return true;
	}
	return false;
};

