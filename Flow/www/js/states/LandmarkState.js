/**
	LandmarkState.js
	Written by Paul Davis
	pjbdavis@gmail.com
	Spring 2013

	This file is responsible for the landmark state. It specifies the behavior
	when the user clicks to place a landmark, and when the user hits the submit
	or cancel button on the landmark box. It allows the user to create, remove,
	and edit landmarks.
	
*/

var LandmarkState = function(stateMan) {
	this.stateManager = stateMan;
	this.pointAtCursor; 
	this.currentMarkIndex;
}

// Keep this at the top
LandmarkState.prototype = new BaseState();

/**
 * Summary: This function is called when the user presses the sumbit
			button on the landmark box. It created a new landmark based
			on the information submitted.
 * Parameters: n/a
 * Returns: n/a
**/
$("#landmark_submit").click(function(event) {
	// Don't reload the whole page
	event.preventDefault();
	
	// Hide the landmark box
	$("#landmark_pop").toggleClass("hidden", true);
	var name = $("#name").val();
	var description = $("#description").val();

	// Add a new landmark
	var point = stateManager.currentState.pointAtCursor;
	landmark = new Landmark(name, description, point);
	landmark.draw();
	stateManager.currentFloor.landmarks.push(landmark);
	
	// Redraw everything
	stateManager.redraw();
	
	// Clear fields
	$("#description").val("");
	$("#name").val("");
});

/**
 * Summary: This function is called when the user presses the cancel
			button on the landmark box. It closes the landmark box and
			resets the fields inside the box.
 * Parameters: n/a
 * Returns: n/a
**/
$("#landmark_cancel").click(function(event) {
	event.preventDefault();
	$("#landmark_pop").toggleClass("hidden", true);
	
	// Clear fields
	$("#description").val("");
	$("#name").val("");
	
});

$("#landmark_update").click(function(event) {
	event.preventDefault();
	$("#landmark_update_pop").toggleClass("hidden", true);
	
	var mark = stateManager.currentFloor.landmarks[stateManager.currentState.currentMarkIndex];
	mark.label = $("#nameUpdateLandmark").val();
	mark.description = $("#descriptionUpdateLandmark").val();
	
	stateManager.currentState.noLandmarkEdit();
	
	// Redraw everything
	stateManager.redraw();
});

$("#landmark_delete").click(function(event) {
	event.preventDefault();
	$("#landmark_update_pop").toggleClass("hidden", true);
	
	stateManager.currentFloor.landmarks.splice(stateManager.currentState.currentMarkIndex, 1);
	
	stateManager.currentState.noLandmarkEdit();
	
	// Redraw everything
	stateManager.redraw();
});

/**
 * Summary: This function is called when the user clicks the mouse.
			It opens a landmark box and stores where the user clicked.
 * Parameters: n/a
 * Returns: undefined
**/
LandmarkState.prototype.click = function(event) {
	$("#landmark_pop").toggleClass("hidden", true);
	$("#landmark_update_pop").toggleClass("hidden", true);
	
	this.noLandmarkEdit();
	
	// Store where the user clicked to add a landmark
	this.pointAtCursor = stateManager.currentFloor.globals.view.toRealWorld(
		new Point(event.pageX - stateManager.currentFloor.globals.canvas.x, 
		event.pageY - stateManager.currentFloor.globals.canvas.y));
	
	var nearIndex = this.isNearLandmark(this.pointAtCursor);
	
	if (nearIndex === -1) {
		$("#landmark_pop").css({
			top: event.pageY + "px",
			left: event.pageX + "px"					 
		}).toggleClass("hidden", false);
		
		this.stateManager.redraw();
	}
	// Clicked near another landmark
	else {
		var mark = stateManager.currentFloor.landmarks[nearIndex];
		mark.edit = true;
		this.currentMarkIndex = nearIndex;
		$("#nameUpdateLandmark").val(mark.label);
		$("#descriptionUpdateLandmark").val(mark.description);
		
		$("#landmark_update_pop").css({
			top: event.pageY + "px",
			left: event.pageX + "px"					 
		}).toggleClass("hidden", false);
	}
	stateManager.redraw();
}

LandmarkState.prototype.noLandmarkEdit = function() {
	for (var i = 0; i < stateManager.currentFloor.landmarks.length; i ++) {
		stateManager.currentFloor.landmarks[i].edit = false;
	}
}

LandmarkState.prototype.isNearLandmark = function(point) {
	var zoom = stateManager.currentFloor.globals.view.scale;
	if (zoom == 0)  {
		return -1;
	}
	var radius = 30 / zoom;
	for (var i = 0; i < stateManager.currentFloor.landmarks.length; i ++) {
		var mark = stateManager.currentFloor.landmarks[i];
		console.log(mark.pointRep.distance(point));
		console.log(radius + " rad");
		if (mark.pointRep.distance(point) < radius) {
			return i;
		}
	}
	
	return -1;
}

// Required by State 'interface'
LandmarkState.prototype.enter = function() {
}

// Required by State 'interface'
LandmarkState.prototype.exit = function() {
	$("#landmark_pop").toggleClass("hidden", true);
	$("#landmark_update_pop").toggleClass("hidden", true);
}

// Required by State 'interface'
LandmarkState.prototype.draw = function() {
}

LandmarkState.prototype.mouseMove = function(event) {
}