var ZoomOutState = function(stateMan) {
	this.stateManager = stateMan;
	
	this.pointAtCursor;
}

//NEED TO HAVE
ZoomOutState.prototype = new BaseState();

//NEED TO HAVE
ZoomOutState.prototype.enter = function() {
}

//NEED TO HAVE
ZoomOutState.prototype.exit = function() {
}


ZoomOutState.prototype.mouseMove = function(event) {
	this.pointAtCursor = stateManager.currentFloor.globals.view.toRealWorld(new Point(event.pageX - stateManager.currentFloor.globals.canvas.x, 
															event.pageY - stateManager.currentFloor.globals.canvas.y));
}

ZoomOutState.prototype.click = function(event) {
	stateManager.currentFloor.globals.view.zoomCanvasPoint(false, new Point(event.originalEvent.layerX, 
												 event.originalEvent.layerY));
	this.stateManager.redraw();
}

ZoomOutState.prototype.draw = function() {
}

// DEPRICATED - moved to state manager
ZoomOutState.prototype.scroll = function(event) {
	if (event.originalEvent.wheelDeltaY > 0) {
		stateManager.currentFloor.globals.view.zoomCanvasPoint(true, new Point(event.originalEvent.layerX, 
													 event.originalEvent.layerY));
	}
	// Consider: stateManager.currentFloor.globals.canvas.width - event.originalEvent.layerX [WLOG Y]
	// It provices a different feel for zoom out
	else {
		stateManager.currentFloor.globals.view.zoomCanvasPoint(false, new Point(event.originalEvent.layerX, 
													  event.originalEvent.layerY));
	}
	this.stateManager.redraw();
}