var ClassifyState = function(stateMan) {
	this.stateManager = stateMan;
	this.activeSpace = undefined;
	this.isBlocking = false;
}

$("#classification_submit").click(function(event) {
	event.preventDefault();
	var label = $("#classifyLabel").val();
	var type = $('input[name=type]:checked', '#classification_pop').val().toLowerCase();
	//Update the active space's information.
	if (stateManager.currentState.activeSpace !== undefined) {
		stateManager.currentState.activeSpace.type = type;
		stateManager.currentState.activeSpace.label = label;
	}
	$("#classifyLabel").val("");
	$("#classify_room").prop('checked', true);
	$("#classification_pop").toggleClass("hidden", true);
	stateManager.currentState.isBlocking = false;
	stateManager.currentState.mouseMove(event);
});

//Cancel button clicked	
$("#classification_cancel").click(function(event) {
	event.preventDefault();
	$("#classifyLabel").val("");
	$("#classify_room").prop('checked', true);
	$("#classification_pop").toggleClass("hidden", true);
	stateManager.currentState.isBlocking = false;
	stateManager.currentState.mouseMove(event);
});

ClassifyState.prototype = new BaseState();

ClassifyState.prototype.enter = function() {
	stateManager.updateSpaces();
}

ClassifyState.prototype.exit = function() {
	this.activeRoom = undefined;
	var allSpaces = stateManager.currentFloor.spaces;
	for (var i = 0; i < allSpaces.length; i++) {
		var curSpace = allSpaces[i];
		curSpace.drawPoly = false;
	}
	$("#classification_pop").toggleClass("hidden", true);
	this.isBlocking = false;
}

ClassifyState.prototype.mouseMove = function(event) {
	var pointAtCursor = stateManager.currentFloor.globals.view.toRealWorld(new Point(event.pageX - stateManager.currentFloor.globals.canvas.x, 
															event.pageY - stateManager.currentFloor.globals.canvas.y));
	stateManager.hoverRoomLabel(new Point(event.pageX, event.pageY), pointAtCursor);
	this.roomSelect(pointAtCursor);
	this.stateManager.redraw();
}

ClassifyState.prototype.click = function(event) {
	if (this.activeSpace !== undefined && !this.isBlocking) {
		this.isBlocking = true;
		var label = this.activeSpace.label;
		if (label === undefined) {
			label = "";
		}
		$("#classifyLabel").val(label);
		var type = this.activeSpace.type;
		if (type !== "") {
			type = "#classify_" + type;
			$(type).prop('checked', true);
		}
		$("#classification_pop").css({
			top: event.pageY + "px",
			left: event.pageX + "px"
		}).toggleClass("hidden", false);
	}
	this.stateManager.redraw();
}

//Will get called even if it doesn't do anything.
ClassifyState.prototype.draw = function() {
}

ClassifyState.prototype.roomSelect = function(point) {
	var allSpaces = stateManager.currentFloor.spaces;
	for (var i = 0; i < allSpaces.length; i++) {
		var curSpace = allSpaces[i];
		if (this.activeSpace !== curSpace) curSpace.drawPoly = false;
	}
	if (this.isBlocking === false) {
		for (var i = 0; i < allSpaces.length; i++) {
			var curSpace = allSpaces[i];
			var width = 5000;
			if (curSpace.pointInSpace(point, width, false)) {
				curSpace.drawPoly = true;
				this.activeSpace = curSpace;
				return; // only select one room
			}
		}
		this.activeSpace = undefined;
	}
}
