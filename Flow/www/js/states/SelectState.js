var SelectState = function(stateMan) {
	this.stateManager = stateMan;
	this.isCntrlDown = false;
	this.isMouseDown = false;
	this.isSelectBox = false;
	this.selectBox = {p1: undefined, p2: undefined};
	this.selectedLines = [];
	this.selectedPoints = [];
}

SelectState.prototype = new BaseState();

SelectState.prototype.enter = function() {
}

SelectState.prototype.exit = function() {
	this.isCntrlDown = false;
	this.isMouseDown = false;
	this.isSelectBox = false;
	this.selectedLines = [];
	this.selectedPoints = [];
}

SelectState.prototype.mouseDown = function(event) {
	var pointAtCursor = GLOBALS.view.toRealWorld(new Point(event.pageX - GLOBALS.canvas.x, event.pageY - GLOBALS.canvas.y));
	this.isMouseDown = true;
	//Snapping to a point takes precedence over snapping to a line
	var snapPoint = this.stateManager.aboutToSnapToPoint(pointAtCursor, GLOBALS.points);
	var snapLine = this.stateManager.aboutToSnapToLine(pointAtCursor);
	if (snapPoint !== null) {
		this.selectedPoints.push(snapPoint);
	}
	else if (snapLine !== null) {
		this.selectedLines.push(snapLine);
	}
	else {
		//Start select box
		this.isSelectBox = true;
		this.selectBox.p1 = pointAtCursor;
	}
	
	this.stateManager.redraw();
}

SelectState.prototype.mouseUp = function(event) {
	this.isMouseDown = false;
}

SelectState.prototype.mouseMove = function(event) {
	var pointAtCursor = GLOBALS.view.toRealWorld(new Point(event.pageX - GLOBALS.canvas.x, event.pageY - GLOBALS.canvas.y));
	if (this.isSelectBox) {
		this.selectBox.p2 = pointAtCursor;
		var p1 = this.selectBox.p1;
		var p2 = this.selectBox.p2;
		//Select points
		for (var i = 0; i < GLOBALS.points.length; i++) {
			var p = GLOBALS.points[i];
			if (this.pointInRect(p, p1, p2)) {
				this.selectedPoints.push(p);
			}
			else {
				this.removeFromSelectPoints(p);
			}
		}
		//Select lines
		for (var i = 0; i < GLOBALS.walls.length; i++) {
			var l = GLOBALS.walls[i];
			if (this.pointInRect(l.p1, p1, p2) && this.pointInRect(l.p2, p1, p2)) {
				this.selectedLines.push(l);
			}
			else {
				this.removeFromSelectLines(l);
			}
		}
	}
	
	else {
	}
	
	this.stateManager.redraw();
}

/**
 * Summary: Check whether the given point is inside the point that define the 
 *		top-left and bottom-right of a rectangle.
 * Parameters: pointToCheck: The point to check,
		boundingP1: The top-left point of the rectangle to check. 
		boundingP2: The bottom-right point of the rectangle to check.
 * Returns: true iff the given point is inside the defined rectangle.
**/
SelectState.prototype.pointInRect = function(pointToCheck, boundingP1, boundingP2) {
	return (((pointToCheck.x >= boundingP1.x && pointToCheck.x <= boundingP2.x) || (pointToCheck.x <= boundingP1.x && pointToCheck.x >= boundingP2.x)) &&
	((pointToCheck.y >= boundingP1.y && pointToCheck.y <= boundingP2.y) || (pointToCheck.y <= boundingP1.y && pointToCheck.y >= boundingP2.y)));
}

SelectState.prototype.keyDown = function(event) {
	var keyCode = event.keyCode;
	//Ctrl key pressed
	if (keyCode === 17) {
		this.isCntrlDown = true;
	}
}

SelectState.prototype.keyUp = function(event) {
	var keyCode = event.keyCode;
	//Ctrl key released
	if (keyCode === 17) {
		this.isCntrlDown = false;
	}
}

SelectState.prototype.draw = function() {
	//Draw the selected lines in red
	for (var i = 0; i < this.selectedLines.length; i++) {
		this.selectedLines[i].draw("red");
	}
	//Draw the selected points in red
	for (var j = 0; j < this.selectedPoints.length; j++) {
		this.selectedPoints[j].draw("red");
	}
	
	//Draw the selection rectangle if appropriate.
	if (this.isSelectBox && !this.selectBoxUndefined()) {
		var p1 = GLOBALS.view.toCanvasWorld(this.selectBox.p1);
		var p2 = GLOBALS.view.toCanvasWorld(this.selectBox.p2);
		var width = p2.x - p1.x;
		var height = p2.y - p1.y;
		GLOBALS.canvas.beginPath();
		GLOBALS.canvas.rect(p1.x, p1.y, width, height);
		GLOBALS.canvas.fillStyle = 'rgba(51,153,255,.5)';
		GLOBALS.canvas.fill();
	}
}

SelectState.prototype.selectBoxUndefined = function() {
	return (this.selectBox.p1 === undefined || this.selectBox.p2 === undefined);
}

SelectState.prototype.removeFromSelectPoints = function(pointToRemove) {
	var index = this.selectedPoints.indexOf(pointToRemove);
	if (index >= 0) {
		this.selectedPoints.splice(index, 1);
	}
}

SelectState.prototype.removeFromSelectLines = function(lineToRemove) {
	var index = this.selectedLines.indexOf(lineToRemove);
	if (index >= 0) {
		this.selectedLines.splice(index, 1);
	}
}