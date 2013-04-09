var LandmarkState = function(stateMan) {
	this.stateManager = stateMan;
	
	this.pointAtCursor;
}

$("#landmark_submit").click(function(event) {
	event.preventDefault();
	$("#landmark_pop").css("display", "none");
	var name = $("#name").val();
	var description = $("#description").val();
	console.log("Added new landmark! name:" + name + ", description " + description);
/*
		event.preventDefault();
		BLOCK_CHANGE_ROOM = false;
		var label = $("#label").val();
		ACTIVE_ROOM.label = label;
		var type = $('input[name=type]:checked', '#classification_pop').val().toLowerCase();
		ACTIVE_ROOM.type = type;
		ACTIVE_ROOM = undefined;
		$("#classification_pop").css("display", "none");
		if (allSpacesClassified()) $("#done").removeAttr("disabled");
		*/
});

	
$("#landmark_cancel	").click(function(event) {
	event.preventDefault();
	$("#landmark_pop").css("display", "none");
});

//NEED TO HAVE
LandmarkState.prototype = new BaseState();

//NEED TO HAVE
LandmarkState.prototype.enter = function() {
}

//NEED TO HAVE
LandmarkState.prototype.exit = function() {
}


LandmarkState.prototype.mouseMove = function(event) {
	this.pointAtCursor = GLOBALS.view.toRealWorld(new Point(event.pageX - GLOBALS.canvas.x, 
															event.pageY - GLOBALS.canvas.y));
}

LandmarkState.prototype.click = function(event) {
	$("#landmark_pop").css({
		display: "block",
		top: event.pageY + "px",
		left: event.pageX + "px"
	});
	this.stateManager.redraw();
}

LandmarkState.prototype.draw = function() {
}

// DEPRICATED - moved to state manager
LandmarkState.prototype.scroll = function(event) {
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