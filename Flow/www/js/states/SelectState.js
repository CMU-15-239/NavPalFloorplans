var SelectState = function(stateMan) {
	this.stateManager = stateMan;
	this.isCntrlDown = false;
	this.isMouseDown = false;
	this.isSelectBox = false;
	this.selectBox = {p1: undefined, p2: undefined};
	this.selectedLines = [];
	this.selectedPoints = [];
	this.hoverObjects = [];
	this.lastReferencePoint = undefined;
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
	this.lastReferencePoint = undefined;
}

SelectState.prototype.mouseDown = function(event) {
	var pointAtCursor = stateManager.currentFloor.globals.view.toRealWorld(new Point(event.pageX - stateManager.currentFloor.globals.canvas.x, event.pageY - stateManager.currentFloor.globals.canvas.y));
	this.lastReferencePoint = stateManager.currentFloor.globals.view.toCanvasWorld(pointAtCursor);
	this.isMouseDown = true;
	//Snapping to a point takes precedence over snapping to a line
	var snapPoint = this.stateManager.aboutToSnapToPoint(pointAtCursor);
	var snapLine = this.stateManager.aboutToSnapToLine(pointAtCursor);
	/* 3 cases:
		1) The mouse is close enough to snap to a point
		2) The mouse is close enough to snap to a line
		3) If neither 1 or 2, then start drawing the select box
	*/
	if (snapPoint !== null) {
		if (this.isPointSelected(snapPoint)) {
		}
		else {
			this.selectedPoints = [];
			this.selectedLines = [];
			this.addSelectedPoint(snapPoint);
		}
	}
	else if (snapLine !== null) {
		if (this.isLineSelected(snapLine)) {
		}
		else {
			this.selectedPoints = [];
			this.selectedLines = [];
			this.addSelectedLine(snapLine);
		}
	}
	else {
		//Start select box
		this.isSelectBox = true;
		this.selectBox.p1 = pointAtCursor;
		this.selectedPoints = [];
		this.selectedLines = [];
	}
	
	this.stateManager.redraw();
}

SelectState.prototype.mouseUp = function(event) {
	if (this.isSelectBox) {
		this.isSelectBox = false;
		this.selectBox = {p1: undefined, p2: undefined};
	}
	//Auto merge everything that was selected, while keeping selection semantics the same
	else {
		var numSeen = 0;
		//Try to snap all the selected points to other points
		while (numSeen < this.selectedPoints.length) {
			var curPoint = this.selectedPoints[numSeen];
			var snapPoint = this.stateManager.aboutToSnapToPoint(curPoint);
			if (snapPoint != null && snapPoint !== curPoint) {
				this.changeLineEndpoints(curPoint, snapPoint);
				this.removeFromSelectPoints(curPoint);
				stateManager.currentFloor.globals.removePoint(curPoint);
			}
			else {
				numSeen += 1;
			}
		}
		//Remove duplicate lines
		var numWallsSeen = 0;
		while (numWallsSeen < stateManager.currentFloor.globals.walls.length) {
			var curWall = stateManager.currentFloor.globals.walls[numWallsSeen];
			if (stateManager.currentFloor.globals.isWallDuplicate(curWall)) {
				console.log("HERE");
				stateManager.currentFloor.globals.removeWall(curWall);
			}
			else numWallsSeen += 1;
		}
		
		for (var i = 0; i < stateManager.currentFloor.globals.walls.length; i++) {
			var p1 = stateManager.currentFloor.globals.walls[i].p1;
			var p2 = stateManager.currentFloor.globals.walls[i].p2;
			stateManager.currentFloor.globals.walls[i].calculateForm(p1, p2);
		}
	}
	this.isMouseDown = false;
	this.stateManager.redraw();
}

SelectState.prototype.changeLineEndpoints = function(oldPoint, newPoint) {
	for (var i = 0; i < stateManager.currentFloor.globals.walls.length; i++) {
		var curWall = stateManager.currentFloor.globals.walls[i];
		if (curWall.p1.equals(oldPoint)) curWall.p1 = newPoint;
		else if (curWall.p2.equals(oldPoint)) curWall.p2 = newPoint;
	}
}

SelectState.prototype.mouseMove = function(event) {
	/* Cases:
		1) Select box is currently doing its thing
		2) Select box isn't around
			a) mouse is down, which implies we move everything that's selected
			b) mouse isn't down, which means we just highlight things we're near that
				are unselected
	*/
	var pointAtCursor = stateManager.currentFloor.globals.view.toRealWorld(new Point(event.pageX - stateManager.currentFloor.globals.canvas.x, event.pageY - stateManager.currentFloor.globals.canvas.y));
	if (this.isSelectBox) {
		this.selectBox.p2 = pointAtCursor;
		var p1 = this.selectBox.p1;
		var p2 = this.selectBox.p2;
		//Select points
		for (var i = 0; i < stateManager.currentFloor.globals.points.length; i++) {
			var p = stateManager.currentFloor.globals.points[i];
			if (this.pointInRect(p, p1, p2)) {
				this.addSelectedPoint(p);
			}
			else {
				this.removeFromSelectPoints(p);
			}
		}
		//Select lines
		for (var i = 0; i < stateManager.currentFloor.globals.walls.length; i++) {
			var l = stateManager.currentFloor.globals.walls[i];
			if (this.pointInRect(l.p1, p1, p2) && this.pointInRect(l.p2, p1, p2)) {
				this.addSelectedLine(l);
			}
			else {
				this.removeFromSelectLines(l);
			}
		}
	}
	
	else {
		if (this.isMouseDown) {
			//move selected stuff
			var deltaX = stateManager.currentFloor.globals.view.toCanvasWorld(pointAtCursor).x - this.lastReferencePoint.x;
			var deltaY = stateManager.currentFloor.globals.view.toCanvasWorld(pointAtCursor).y - this.lastReferencePoint.y;
			for (var i = 0; i < this.selectedPoints.length; i++) {
				var currentPoint = stateManager.currentFloor.globals.view.toCanvasWorld(this.selectedPoints[i]);
				currentPoint.x += deltaX;
				currentPoint.y += deltaY;
				var currentPointRealWorld = stateManager.currentFloor.globals.view.toRealWorld(currentPoint);
				this.selectedPoints[i].x = currentPointRealWorld.x;
				this.selectedPoints[i].y = currentPointRealWorld.y;
				//TO DO: Have setter methods for line instead of doing this.
			}
			for (var i = 0; i < stateManager.currentFloor.globals.walls.length; i++) {
				var p1 = stateManager.currentFloor.globals.walls[i].p1;
				var p2 = stateManager.currentFloor.globals.walls[i].p2;
				stateManager.currentFloor.globals.walls[i].calculateForm(p1, p2);
			}
		}
		else {
			//Display the room label
			stateManager.hoverRoomLabel(new Point(event.pageX, event.pageY), pointAtCursor);
			//show hover color if near anything
			//Snapping to a point takes precedence over snapping to a line
			var snapPoint = this.stateManager.aboutToSnapToPoint(pointAtCursor);
			var snapLine = this.stateManager.aboutToSnapToLine(pointAtCursor);
			if (snapPoint !== null) {
				this.hoverObjects.push(snapPoint);
			}
			else if (snapLine !== null) {
				this.hoverObjects.push(snapLine);
				this.hoverObjects.push(snapLine.p1);
				this.hoverObjects.push(snapLine.p2);
			}
		}
	}
	
	this.lastReferencePoint = stateManager.currentFloor.globals.view.toCanvasWorld(pointAtCursor);
	this.stateManager.redraw();
}

SelectState.prototype.isPointSelected = function(point) {
	var index = this.selectedPoints.indexOf(point);
	return (index >= 0);
}

SelectState.prototype.isLineSelected = function(line) {
	var index = this.selectedLines.indexOf(line);
	return (index >= 0);
}

SelectState.prototype.addSelectedPoint = function(pointToAdd) {
	if (!this.isPointSelected(pointToAdd)) {
		this.selectedPoints.push(pointToAdd);
	}
}

SelectState.prototype.addSelectedLine = function(lineToAdd) {
	if (!this.isLineSelected(lineToAdd)) {
		this.selectedLines.push(lineToAdd);
		this.addSelectedPoint(lineToAdd.p1);
		this.addSelectedPoint(lineToAdd.p2);
	}
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
	//Del key
	else if (keyCode === 46) {
		this.deleteSelectedItems();
		this.stateManager.redraw();
	}
}

SelectState.prototype.deleteSelectedItems = function() {
	var pointsToDelete = [];
	for (var i = 0; i < this.selectedPoints.length; i++) {
		var curPoint = this.selectedPoints[i];
		if (!this.pointInSelectedLine(curPoint)) {
			stateManager.currentFloor.globals.removePoint(this.selectedPoints[i]);
		}
	}
	for (var j = 0; j < this.selectedLines.length; j++) {
		stateManager.currentFloor.globals.removeWall(this.selectedLines[j], true);
	}
	var numWallsSeen = 0;
	while (numWallsSeen < stateManager.currentFloor.globals.walls.length) {
		var l = stateManager.currentFloor.globals.walls[numWallsSeen];
		if (this.containsSelectedPoint(l)
			&& !this.pointInSelectedLine(l.p1)
			&& !this.pointInSelectedLine(l.p2)) {
			stateManager.currentFloor.globals.removeWall(l);
		}
		else numWallsSeen += 1;
	}
	this.selectedLines = [];
	this.selectedPoints = [];
}

SelectState.prototype.pointInSelectedLine = function(point) {
	for (var i = 0; i < this.selectedLines.length; i++) {
		var curLine = this.selectedLines[i];
		if (curLine.p1.equals(point) || curLine.p2.equals(point)) {
			return true;
		}
	}
	return false;
}

SelectState.prototype.containsSelectedPoint = function(line) {
	for (var i = 0; i < this.selectedPoints.length; i++) {
		var curPoint = this.selectedPoints[i];
		if (line.p1.equals(curPoint) || line.p2.equals(curPoint)) return true;
	}
	return false;
}

SelectState.prototype.keyUp = function(event) {
	var keyCode = event.keyCode;
	//Ctrl key released
	if (keyCode === 17) {
		this.isCntrlDown = false;
	}
}

SelectState.prototype.draw = function() {
	//Draw anything that's being hovered over in yellow
	for (var i = 0; i < this.hoverObjects.length; i++) {
		this.hoverObjects[i].draw("yellow");
	}
	this.hoverObjects = [];

	//Draw the selected lines in red
	for (var i = 0; i < this.selectedLines.length; i++) {
		this.selectedLines[i].draw("red");
		this.selectedLines[i].p1.draw("red");
		this.selectedLines[i].p2.draw("red");
	}
	//Draw the selected points in red
	for (var j = 0; j < this.selectedPoints.length; j++) {
		this.selectedPoints[j].draw("red");
	}
	
	//Draw the selection rectangle if appropriate.
	if (this.isSelectBox && !this.selectBoxUndefined()) {
		var p1 = stateManager.currentFloor.globals.view.toCanvasWorld(this.selectBox.p1);
		var p2 = stateManager.currentFloor.globals.view.toCanvasWorld(this.selectBox.p2);
		var width = p2.x - p1.x;
		var height = p2.y - p1.y;
		stateManager.currentFloor.globals.canvas.beginPath();
		stateManager.currentFloor.globals.canvas.rect(p1.x, p1.y, width, height);
		stateManager.currentFloor.globals.canvas.fillStyle = 'rgba(51,153,255,.5)';
		stateManager.currentFloor.globals.canvas.fill();
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