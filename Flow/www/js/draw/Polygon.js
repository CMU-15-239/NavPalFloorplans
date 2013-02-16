var POLYGON_COLOR = 'rgba(51,153,255,.5)';

/**
 * Summary: Constructor for the Polygon object.
 * Parameters: lines: An array of Line objects
 * Returns: undefined.
**/
function Polygon(lines) {
	this.points = polySort(lines);
	this.color = POLYGON_COLOR;
}

Polygon.prototype.draw = function() {
	var oldStyle = CANVAS.fillStyle;
	CANVAS.fillStyle = this.color;
	CANVAS.beginPath();
	CANVAS.moveTo(this.points[0].x, this.points[0].y);
	for (var i = 0; i < this.points.length; i++) {
		var curPoint = this.points[i];
		CANVAS.lineTo(curPoint.x, curPoint.y);
	}
	CANVAS.closePath();
	CANVAS.fill();
	CANVAS.fillStyle = oldStyle;
}

//Sort the lines (i.e. put them in order of appearance) so that they'll be drawn properly.
function polySort(lines) {
	var orderedPoints = [];
	var numSorted = 0;
	var initLinesLength = lines.length;
	var curLine = lines[0];
	var matchedPoint;
	while (numSorted < (initLinesLength - 1)) {
		for (var i = 0; i < lines.length; i++) {
			var lineToCheck = lines[i];
			if ((lineToCheck !== undefined) && !curLine.equals(lineToCheck)) { 
				if (curLine.p2.equals(lineToCheck.p1) || curLine.p2.equals(lineToCheck.p2)) {
					matchedPoint = curLine.p2;
				}
				else if(curLine.p1.equals(lineToCheck.p1) || curLine.p1.equals(lineToCheck.p2)) {
					matchedPoint = curLine.p1;
				}
				else continue;
				//It will only get here if something matched.
				orderedPoints.push(matchedPoint);
				lines[lines.indexOf(curLine)] = undefined;
				curLine = lineToCheck;
				numSorted += 1;
				break;
			}
		}
	}
	var lastPoint = orderedPoints[initLinesLength - 2];
	if (curLine.p1.equals(lastPoint)) orderedPoints.push(curLine.p2);
	else orderedPoints.push(curLine.p1);
	for (var j = 0; j < orderedPoints.length; j++) {
		var cur = orderedPoints[j];
		console.log("Point " + (j+1) + ": (" + cur.x + ", " + cur.y + ")");
	}
	return orderedPoints;
}