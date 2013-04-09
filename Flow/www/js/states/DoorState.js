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
	var pointAtCursor = GLOBALS.view.toRealWorld(new Point(event.pageX - GLOBALS.canvas.x, event.pageY - GLOBALS.canvas.y));
	var snapLine = this.stateManager.aboutToSnapToLine(pointAtCursor);
	if (snapLine != null) {
		snapLine.isDoor = !snapLine.isDoor;
	}
	this.stateManager.redraw();
}

DoorState.prototype.draw = function() {
}
