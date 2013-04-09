var PanState = function(stateMan) {
	this.stateManager = stateMan;
	
	this.pointAtCursor = new Point(0,0);
	this.drag = false;
	this.oldPoint;
}

//NEED TO HAVE
PanState.prototype = new BaseState();

//NEED TO HAVE
PanState.prototype.enter = function() {
}

//NEED TO HAVE
PanState.prototype.exit = function() {
}

PanState.prototype.mouseUp = function(event) {
	this.drag = false;
}

PanState.prototype.mouseDown = function(event) {
	this.drag = true;
	this.oldPoint = this.pointAtCursor;
}

PanState.prototype.mouseMove = function(event) {
	//this.pointAtCursor = stateManager.currentFloor.globals.view.toRealWorld(new Point(event.pageX - stateManager.currentFloor.globals.canvas.x, 
	//														event.pageY - stateManager.currentFloor.globals.canvas.y));
	
	this.pointAtCursor = new Point(event.pageX - stateManager.currentFloor.globals.canvas.x,
									event.pageY - stateManager.currentFloor.globals.canvas.y);
									
	// If the mouse is being dragged, pan
	if (this.drag) {
		dx = this.oldPoint.x - this.pointAtCursor.x;
		dy = this.oldPoint.y - this.pointAtCursor.y;
		
		stateManager.currentFloor.globals.view.pan(dx, dy);
		
		this.oldPoint = this.pointAtCursor;
		
		this.stateManager.redraw();
	}
}

PanState.prototype.click = function(event) {
}

PanState.prototype.draw = function() {
}

PanState.prototype.scroll = function(event) {
	
}