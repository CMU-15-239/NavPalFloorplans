var DoorState = function(stateMan) {
	this.stateManager = stateMan; 
}

//NEED TO HAVE
DoorState.prototype = new BaseState();

//NEED TO HAVE
DoorState.prototype.enter = function() {
}

//NEED TO HAVE
DoorState.prototype.exit = function() {
}


DoorState.prototype.mouseMove = function(event) {
	
}

DoorState.prototype.click = function(event) {
	var pointAtCursor = stateManager.currentFloor.globals.view.toRealWorld(new Point(event.pageX - stateManager.currentFloor.globals.canvas.x, event.pageY - stateManager.currentFloor.globals.canvas.y));
	var snapLine = this.stateManager.aboutToSnapToLine(pointAtCursor);
	if (snapLine != null) {
		snapLine.isDoor = !snapLine.isDoor;
	}
	this.stateManager.redraw();
}

DoorState.prototype.draw = function() {
}
