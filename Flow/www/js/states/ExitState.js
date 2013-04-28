var ExitState = function(stateMan) {
	this.stateManager = stateMan; 
	this.hoverDoor = undefined;
	this.needToMoveMouse = false;
}

ExitState.prototype = new BaseState();

ExitState.prototype.enter = function() {
}

ExitState.prototype.exit = function() {
	this.hoverDoor = undefined;
	this.needToMoveMouse = false;
}

ExitState.prototype.mouseMove = function(event) {
	var pointAtCursor = stateManager.currentFloor.globals.view.toRealWorld(new Point(event.pageX - stateManager.currentFloor.globals.canvas.x, event.pageY - stateManager.currentFloor.globals.canvas.y));
	//show hover color if near anything
	//Snapping to a point takes precedence over snapping to a line
	var snapLine = this.stateManager.aboutToSnapToLine(pointAtCursor);
	if (snapLine !== null && !this.needToMoveMouse) {
		this.hoverDoor = snapLine;
	}
	if (this.needToMoveMouse) this.needToMoveMouse = false;
	this.stateManager.redraw();
}

ExitState.prototype.click = function(event) {
	var pointAtCursor = stateManager.currentFloor.globals.view.toRealWorld(new Point(event.pageX - stateManager.currentFloor.globals.canvas.x, event.pageY - stateManager.currentFloor.globals.canvas.y));
	var snapLine = this.stateManager.aboutToSnapToLine(pointAtCursor);
	if (snapLine !== null) {
		snapLine.isExit = !snapLine.isExit;
		if (snapLine.isDoor) snapLine.isDoor = false;
		this.needToMoveMouse = true;
	}
	this.stateManager.redraw();
}

ExitState.prototype.draw = function() {
	if (this.hoverDoor !== undefined) {
		var color = "orange";
		this.hoverDoor.draw(color);
		this.hoverDoor.p1.draw(color);
		this.hoverDoor.p2.draw(color);
	}
	this.hoverDoor = undefined;
}
