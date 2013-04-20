/**
 * Summary: Constructor for the Polygon object.
 * Parameters: lines: An array of Line objects
 * Returns: undefined.
**/
function Polygon(lines, color) {
	var copyLines = [];
	//Make a copy of the lines so that we don't disturb any aspect of the containing space.
	for (var i = 0; i < lines.length; i++) {
		var curLine = lines[i];
		copyLines.push(new Line(new Point(curLine.p1.x, curLine.p1.y), new Point(curLine.p2.x, curLine.p2.y)));
	}
	//Sort the point in the polygon to enable drawing.
	this.points = polySort(copyLines);
	//Set the color the polygon should be.
	this.color = color;
}

/**
 * Summary: Draw the polygon on the canvas.
 * Parameters: this
 * Returns: undefined
**/
Polygon.prototype.draw = function() {
	//Save the old fill style so that we can restore it later.
	var oldStyle = stateManager.currentFloor.globals.canvas.fillStyle;
	stateManager.currentFloor.globals.canvas.fillStyle = this.color;
	var canvas = stateManager.currentFloor.globals.canvas;
	canvas.beginPath();
	var startingPoint = stateManager.currentFloor.globals.view.toCanvasWorld(this.points[0]);
	//Move to the first point
	canvas.moveTo(startingPoint.x, startingPoint.y);
	//Draw a line to each point in succession
	for (var i = 0; i < this.points.length; i++) {
		var curPoint = this.points[i];
		curPoint = stateManager.currentFloor.globals.view.toCanvasWorld(curPoint);
		canvas.lineTo(curPoint.x, curPoint.y);
	}
	//Close off the polygon
	canvas.closePath();
	canvas.fill();
	//Restore the olf fill style
	stateManager.currentFloor.globals.canvas.fillStyle = oldStyle;
}

/**
 * Summary: Sort the points in the given set of lines (i.e. put them in order of appearance)
 * Parameters: lines: The set of lines whose points we should sort.
 * Returns: The ordered set of points.
**/
function polySort(lines) {
	if (lines.length === 0) return;
	else if (lines.length === 1) return [lines[0].p1, lines[0].p2];
	var orderedPoints = [];
	var numSorted = 0;
	//The initial number of lines.
	var initLinesLength = lines.length;
	var curLine = lines[0];
	var matchedPoint;
	//There are remaining points to sort only if all the points in the lines aren't sorted.
	while (numSorted < (initLinesLength - 1)) {
		//Now that we know we should still be sorting, pick a line whose point we should sort next.
		for (var i = 0; i < lines.length; i++) {
			var lineToCheck = lines[i];
			//Make sure we don't keep checking the same line over and over
			if ((lineToCheck !== undefined) && !curLine.equals(lineToCheck)) { 
				//If the two lines match on a point, then we know the next line we should look at
				if (curLine.p2.equals(lineToCheck.p1) || curLine.p2.equals(lineToCheck.p2)) {
					matchedPoint = curLine.p2;
				}
				else if(curLine.p1.equals(lineToCheck.p1) || curLine.p1.equals(lineToCheck.p2)) {
					matchedPoint = curLine.p1;
				}
				//The the two lines don't match on a single point, keep moving.
				else continue;
				//It will only get here if something matched.
				orderedPoints.push(matchedPoint);
				//Set the line that's been fully checked (i.e. both of its points sorted) to undefined.
				lines[lines.indexOf(curLine)] = undefined;
				curLine = lineToCheck;
				numSorted += 1;
				break;
			}
		}
	}
	//Now we simply have to put the last remaining point in the sorted set.
	var lastPoint = orderedPoints[initLinesLength - 2];
	if (curLine.p1.equals(lastPoint)) orderedPoints.push(curLine.p2);
	else orderedPoints.push(curLine.p1);
	return orderedPoints;
}