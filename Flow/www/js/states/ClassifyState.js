var ClassifyState = function(stateMan) {
	this.stateManager = stateMan;
	this.activeRoom;
}

$("#label_submit").click(function(event) {
	event.preventDefault();
	var label = $("#label").val();
	this.activeRoom.label = label;
	var type = $('input[name=type]:checked', '#classification_pop').val().toLowerCase();
	this..type = type;
	this.activeRoom = undefined;
	$("#classify_pop").css("display", "none");
});

	
$("#landmark_cancel	").click(function(event) {
	event.preventDefault();
	$("#classify_pop").css("display", "none");
});

ClassifyState.prototype = new BaseState();

ClassifyState.prototype.enter = function() {
	var curSpaces = stateManager.currentFloor.spaces;
	var newSpaces = detectRooms(stateManager.currentFloor.globals.walls, curSpaces);
	stateManager.currentFloor.spaces = rooms;
}

ClassifyState.prototype.exit = function() {
	this.activeRoom = undefined;
	$("#classify_pop").css("display", "none");
}

ClassifyState.prototype.mouseMove = function(event) {
	
}

ClassifyState.prototype.click = function(event) {
	$("#landmark_pop").css({
		display: "block",
		top: event.pageY + "px",
		left: event.pageX + "px"
	});
	this.pointAtCursor = stateManager.currentFloor.globals.view.toRealWorld(new Point(event.pageX - stateManager.currentFloor.globals.canvas.x, 
															event.pageY - stateManager.currentFloor.globals.canvas.y));
	this.stateManager.redraw();
}

ClassifyState.prototype.draw = function() {
}
