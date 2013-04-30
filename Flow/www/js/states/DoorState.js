var DoorState = function(stateMan) {
	this.stateManager = stateMan; 
	this.hoverDoor = undefined;
	this.needToMoveMouse = false;
}

DoorState.prototype = new BaseState();

DoorState.prototype.enter = function() {
	console.log(stateManager.currentFloor.globals.walls);
	console.log(stateManager.currentFloor.globals.points);
}

DoorState.prototype.exit = function() {
	this.hoverDoor = undefined;
	this.needToMoveMouse = false;
}

DoorState.prototype.mouseMove = function(event) {
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

DoorState.prototype.click = function(event) {
	var pointAtCursor = stateManager.currentFloor.globals.view.toRealWorld(new Point(event.pageX - stateManager.currentFloor.globals.canvas.x, event.pageY - stateManager.currentFloor.globals.canvas.y));
	var snapLine = this.stateManager.aboutToSnapToLine(pointAtCursor);
	if (snapLine !== null) {
		snapLine.isDoor = !snapLine.isDoor;
		if (snapLine.isExit) snapLine.isExit = false;
		this.needToMoveMouse = true;
	}
	this.stateManager.redraw();
}

DoorState.prototype.draw = function() {
	if (this.hoverDoor !== undefined) {
		var color = "orange";
		this.hoverDoor.draw(color);
		this.hoverDoor.p1.draw(color);
		this.hoverDoor.p2.draw(color);
	}
	this.hoverDoor = undefined;
}
