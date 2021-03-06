/**
	line.js
	Written by Justin Greet and Paul Davis.
	justin.greet11@gmail.com
	Spring 2013
	
	The data structure that represents a line in the canvas.
	
*/


function importLine(simpleLine)
{
  
  if(util.exists(simpleLine))
  {
    var p1 = importPoint(simpleLine.p1);
    var p2 = importPoint(simpleLine.p2);
    
    return new Line(p1, p2, simpleLine.isDoor);
  }
  
  return null;
}

function importLineFromPoints(p1, p2, isDoor)
{
  if(util.exists(p1) && util.exists(p2))
  {
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
function Line(p1, p2, isDoor)
{
	this.p1 = p1;
	this.p2 = p2;
	this.isSelected = false;
	this.isDoor = (isDoor === true);
	this.isExit = false;
	this.definesRoom = false;
	
	// Line in form ax + by + c = 0
	// sqrt(a*a + b*b) = distConst
	this.a;
	this.b;
	this.c;
	this.distConst;
	this.calculateForm(p1, p2);
}

/*
 * Added by Gary Giger
 * 
 * Returns a copy of the line by creating a new instance of the Line object.  
 * 
 * Note that I named the function cloneLine instead of clone because I did not 
 * want to override any clone object that is implicitly inherited and may break
 * other logic. I don't know Javascript that well and did not want to risk
 * breaking other logic.
 * 
 * name: cloneLine
 * @param (none)
 * @return A new instance of this Line
 * 
 */
Line.prototype.cloneLine = function()
{
	var clonedLine = new Line(this.p1.clonePoint(), this.p2.clonePoint(), this.isDoor);

	clonedLine.isSelected = this.isSelected;
	clonedLine.isExit = this.isExit;
	clonedLine.definesRoom = this.definesRoom;	

	return clonedLine; 
};

Line.prototype.cloneLineAndScale = function(scaleFactor)
{
	// Clone and scale the points
	var p1Scaled = this.p1.clonePointAndScale(scaleFactor);
	var p2Scaled = this.p2.clonePointAndScale(scaleFactor);

	// Create a new Line instance with the scaled points
	var clonedLine = new Line(p1Scaled, p2Scaled, this.isDoor);
	clonedLine.isSelected = this.isSelected;
	clonedLine.isExit = this.isExit;
	clonedLine.definesRoom = this.definesRoom;
	
	return clonedLine;
};

/**
  * Constructs a JSON object from this.
  **/
Line.prototype.toOutput = function() 
{
	return {
		p1: this.p1.toOutput(),
		p2: this.p2.toOutput(),
		isDoor: this.isDoor
	};
};

/******************************************************************************/

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
 * Summary: Get the slope of the line. (approximates vertical lines to large numbers)
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
  * Summary: Get the slope of the line.
  * Parameters: void
  * Returns: int (Infinity if it is a vertical line).
**/
Line.prototype.getSlope2 = function() {
  var leftPt = this.p1;
  var rightPt = this.p2;
  if(leftPt.x > rightPt.x) {
    leftPt = this.p2;
    rightPt = this.p1;
  }
  
  return (rightPt.y - leftPt.y)/(rightPt.x - leftPt.x);
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
  * Summary: Creates a sampleing of the line at regular intervals.
  * Parameters: void
  * Returns: [Point], Array of Points
**/
Line.prototype.getPointsRep = function() {
  var pts = [];
  
  if(this.getSlope2() === Infinity) {
    //is a vertical line
    var minY = this.p1.y;
    var maxY = this.p2.y;
    if(maxY < minY) {
      minY = this.p2.y;
      maxY = this.p1.y;
    }
    
    for(var yr = minY + 1; yr < maxY; yr++) {
      pts.push(new Point(this.p1.x, yr));
    }
  } else {
    var xr = Math.min(this.p1.x, this.p2.x);
    var pt;
    while((pt = this.getYAtX(xr)) !== null) {
      pts.push(pt);
      xr++;
    }
  }
  
  return pts;
};

/**
  * Summary: Finds the y values for the given x values.
        If it is a vertical line returns the top point.
  * Parameters: xr : int
  * Returns: Point
**/
Line.prototype.getYAtX = function(xr) {
  var minPt = this.p1;
  var maxPt = this.p2;
  if(maxPt.x < minPt.x) {
    minPt = this.p2;
    maxPt = this.p1;
  }
  
  if(minPt.x <= xr && xr <= maxPt.x) {
    var slope = this.getSlope();
    var dx = xr - minPt.x;
    var dy = slope * dx;
    return new Point(xr, minPt.y + dy);
  }
  
  return null;
};


/**
 * Summary: Get the magnitude of the line (i.e. its length).
 * Parameters: this
 * Returns: The magnitude of the line.
**/	
Line.prototype.magnitutde = function() { //TODO: fix spelling
	var dx = Math.abs(this.p1.x - this.p2.x);
	var dy = Math.abs(this.p1.y - this.p2.y);
	return Math.sqrt(dx * dx + dy * dy);
};

Line.prototype.magnitude = Line.prototype.magnitutde;

/**
  * Summary: Check if line and this intersect.
  * Parameters: line: the line to check for intersection.
  * Returns: The point of intersection, if it exists. Null otherwise.
**/
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

/**
  * Summary: Split up this along each of the points in setOfPoints.
  * Parameters: setOfPoints: The points at which we should split up this.
  * Returns: undefined
**/
Line.prototype.splitUpLine = function(setOfPoints) {
	//First sort the points, starting at this.p1
	var sortedPoints = this.sortPoints(setOfPoints);
	//The lines that result from splitting this up.
	var newLineSegments = [];
	var lineToSplit = this;
	//Go through each of the points and split up segments.
	for (var i = 0; i < sortedPoints.length; i++) {
		//Split what remains of this up.
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

/**
  * Summary: Sort a set of points that lie on this, starting at this.p1
  * Parameters: points: The unsorted set of points that lie along this.
  * Returns: A sorted set of the points that lie along this.
**/
Line.prototype.sortPoints = function(points) {
	//First find the closest point to p1 (could be p2 just as well) and put it at the front,
	//because it will act as our starting point for sorting
	var closestDistance = 100000000000;
	var startingPointIndex;
	//Get the index of the point that's closest to this.p1
	for (var i = 0; i < points.length; i++) {
		var pointToCheck = points[i];
		var distanceTop1 = pointToCheck.distance(this.p1);
		if (distanceTop1 < closestDistance) {
			startingPointIndex = i;
			closestDistance = distanceTop1;
		}
	}
	//Now remove that point and put it at the front. Splice returns an array.
	var startingPoint = points.splice(startingPointIndex, 1); 
	startingPoint = startingPoint[0];
	//Unshift inserts into the front of an array.
	points.unshift(startingPoint);
	
	//Now go through each remaining point and sort it, stopping when all the points are sorted.
	var closestPoint;
	var numSorted = 1;
	var sortedPoints = [];
	//We know the starting point is the first sorted point.
	sortedPoints.push(startingPoint);
	closestDistance = 100000000000;
	while (numSorted < points.length) {
		//We want to find the closest point to the most recent one that we've sorted.
		var pointToCheck = sortedPoints[numSorted - 1];
		//Go through the remaining points to find the closest one.
		for (var j = 0; j < points.length; j++) {
			var curPoint = points[j];
			//Make sure we skip any points that have already been sorted. 
			//Also skip the point that's equal to curPoint.
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

/**
  * Summary: Check if pointList contains point.
  * Parameters: pointList: A set of points
  *		point: The point to check for
  * Returns: true iff there exists a point in pointList that equals point.
**/
Line.prototype.containsPoint = function(pointList, point) {
	for (var i = 0; i < pointList.length; i++) {
		if (pointList[i].equals(point)) return true;
	}
	return false;
};

/**
  * Summary: Determines if this is parallel to the given line.
  * Parameters: otherLine: Line
  * Returns: boolean
**/
Line.prototype.isParallelTo = function(otherLine) {
  return this.getSlope2() === otherLine.getSlope2();
};

/**
  * Summary: Determines if this is parallel to one of the given lines.
  * Parameters: otherLines: [Line], Array of Lines
  * Returns: boolean
**/
Line.prototype.isParallelToOne = function(otherLines) {
  for(var l = 0; l < otherLines.length; l++) {
    if(this.isParallelTo(otherLines[l])) {
      return true;
    }
  }
  
  return false;
}

