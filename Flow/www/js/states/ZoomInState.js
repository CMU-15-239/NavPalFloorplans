var ZoomInState = function(stateMan) {
	this.stateManager = stateMan;
	
	this.pointAtCursor;
}

//NEED TO HAVE
ZoomInState.prototype = new BaseState();

//NEED TO HAVE
ZoomInState.prototype.enter = function() {
}

//NEED TO HAVE
ZoomInState.prototype.exit = function() {
}


ZoomInState.prototype.mouseMove = function(event) {
	this.pointAtCursor = GLOBALS.view.toRealWorld(new Point(event.pageX - GLOBALS.canvas.x, 
															event.pageY - GLOBALS.canvas.y));
}

ZoomInState.prototype.click = function(event) {
	GLOBALS.view.zoomCanvasPoint(true, new Point(event.originalEvent.layerX, 
												 event.originalEvent.layerY));
	this.stateManager.redraw();
}

ZoomInState.prototype.draw = function() {
}

// DEPRICATED - moved to state manager
ZoomInState.prototype.scroll = function(event) {
	if (event.originalEvent.wheelDeltaY > 0) {
		GLOBALS.view.zoomCanvasPoint(true, new Point(event.originalEvent.layerX, 
													 event.originalEvent.layerY));
	}
	// Consider: GLOBALS.canvas.width - event.originalEvent.layerX [WLOG Y]
	// It provices a different feel for zoom out
	else {
		GLOBALS.view.zoomCanvasPoint(false, new Point(event.originalEvent.layerX, 
													  event.originalEvent.layerY));
	}
	this.stateManager.redraw();
}