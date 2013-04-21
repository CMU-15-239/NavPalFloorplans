var LandmarkState = function(stateMan) {
	this.stateManager = stateMan;
	
	this.pointAtCursor; 
}

$("#landmark_submit").click(function(event) {
	event.preventDefault();
	$("#landmark_pop").toggleClass("hidden", true);
	var name = $("#name").val();
	var description = $("#description").val();

	var point = stateManager.currentState.pointAtCursor;
	landmark = new Landmark(name, description, point);
	landmark.draw();
	//console.log("Added new landmark! name:" + name + ", description " + description + point.toString());
	stateManager.currentFloor.landmarks.push(landmark);
	
	stateManager.redraw();
	// Clear fields
	$("#description").val("");
	$("#name").val("");

	
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

	
$("#landmark_cancel").click(function(event) {
	event.preventDefault();
	$("#landmark_pop").toggleClass("hidden", true);
	
	// Clear fields
	$("#description").val("");
	$("#name").val("");
	
});

//NEED TO HAVE
LandmarkState.prototype = new BaseState();

//NEED TO HAVE
LandmarkState.prototype.enter = function() {
}

//NEED TO HAVE
LandmarkState.prototype.exit = function() {
	$("#landmark_pop").toggleClass("hidden", true);
}


LandmarkState.prototype.mouseMove = function(event) {
	
}

LandmarkState.prototype.click = function(event) {


	this.pointAtCursor = stateManager.currentFloor.globals.view.toRealWorld(new Point(event.pageX - stateManager.currentFloor.globals.canvas.x, 
						 event.pageY - stateManager.currentFloor.globals.canvas.y));
						 

	$("#landmark_pop").css({
		top: event.pageY + "px",

		left: event.pageX + "px"					 
	}).toggleClass("hidden", false);
	
	this.pointAtCursor = stateManager.currentFloor.globals.view.toRealWorld(new Point(event.pageX - stateManager.currentFloor.globals.canvas.x, 
															event.pageY - stateManager.currentFloor.globals.canvas.y));

	this.stateManager.redraw();
}

LandmarkState.prototype.draw = function() {

}
