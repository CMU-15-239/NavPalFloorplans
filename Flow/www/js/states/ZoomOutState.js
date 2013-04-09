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
	this.pointAtCursor = GLOBALS.view.toRealWorld(new Point(event.pageX - GLOBALS.canvas.x, 
															event.pageY - GLOBALS.canvas.y));
}

ZoomOutState.prototype.click = function(event) {
	GLOBALS.view.zoomCanvasPoint(false, new Point(event.originalEvent.layerX, 
												 event.originalEvent.layerY));
	this.stateManager.redraw();
}

ZoomOutState.prototype.draw = function() {
}

// DEPRICATED - moved to state manager
ZoomOutState.prototype.scroll = function(event) {
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