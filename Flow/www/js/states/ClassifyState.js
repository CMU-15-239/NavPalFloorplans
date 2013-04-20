var ClassifyState = function(stateMan) {
	this.stateManager = stateMan;
	this.activeSpace = undefined;
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
	stateManager.currentState.mouseMove(event);
});

	
$("#classification_cancel").click(function(event) {
	event.preventDefault();
	$("#classifyLabel").val("");
	$("#classify_room").prop('checked', true);
	$("#classification_pop").toggleClass("hidden", true);
	stateManager.currentState.mouseMove(event);
});

ClassifyState.prototype = new BaseState();

ClassifyState.prototype.enter = function() {
	console.log("Number of points: " + stateManager.currentFloor.globals.points.length);
	stateManager.updateSpaces();
}

ClassifyState.prototype.exit = function() {
	this.activeRoom = undefined;
	var allSpaces = stateManager.currentFloor.spaces;
	for (var i = 0; i < allSpaces.length; i++) {
		var curSpace = allSpaces[i];
		curSpace.drawPoly = false;
	}
	$("#classification_pop").toggleClass("hidden", true);;
}

ClassifyState.prototype.mouseMove = function(event) {
	var pointAtCursor = stateManager.currentFloor.globals.view.toRealWorld(new Point(event.pageX - stateManager.currentFloor.globals.canvas.x, 
															event.pageY - stateManager.currentFloor.globals.canvas.y));
	stateManager.hoverRoomLabel(new Point(event.pageX, event.pageY), pointAtCursor);
	//stateManager.hoverRoomLabel(pointAtCursor);
	this.roomSelect(pointAtCursor);
	this.stateManager.redraw();
}

ClassifyState.prototype.click = function(event) {
	if (this.activeSpace !== undefined) {
		var label = this.activeSpace.label;
		if (label === undefined) label = "";
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

ClassifyState.prototype.draw = function() {
}

ClassifyState.prototype.roomSelect = function(point) {
	//if (point.x < 0 || point.y < 0) console.log("negative point");
	var allSpaces = stateManager.currentFloor.spaces;
	for (var i = 0; i < allSpaces.length; i++) {
		var curSpace = allSpaces[i];
		if (this.activeSpace !== curSpace) curSpace.drawPoly = false;
	}
	for (var i = 0; i < allSpaces.length; i++) {
		var curSpace = allSpaces[i];
		//var width = stateManager.currentFloor.globals.canvas.width; TOO SMALL WHEN ZOOMING
		var width = 5000;
		if (curSpace.pointInSpace(point, width, false)) {
			curSpace.drawPoly = true;
			this.activeSpace = curSpace;
			return; // only select one room
		}
	}
	this.activeSpace = undefined;
}
