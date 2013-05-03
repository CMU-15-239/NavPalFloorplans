/**
	ElevatorState.js
	Written by Paul Davis
	pjbdavis@gmail.com
	Spring 2013

	This file is responsible for the elevator state. It specifies the behavior
	when the user clicks to place a elevator, and when the user hits the submit
	or cancel button on the elevator box. It allows the user to create, remove,
	and edit stairs.
	
*/

var ElevatorState = function(stateMan) {
	this.stateManager = stateMan;
	this.pointAtCursor; 
}

//NEED TO HAVE
ElevatorState.prototype = new BaseState();

/**
 * Summary: This function is called when the user presses the sumbit
			button on the elevator box. It created a new interfloor connection based
			on the information submitted.
 * Parameters: n/a
 * Returns: n/a
**/
$("#saveElevator").click(function(event) {

	// Don't reload the whole page
	event.preventDefault();
	
	// Hide the elevator box, clear the fields in the box
	$("#elevator_pop").toggleClass("hidden", true);
	var newElevator = $("#newElevator").val();
	var existingElevator = $("#selectmultiple").val();
	
	
	// Use an existing elevator name
	if (newElevator === "") {
		// Nothing choosen
		if (existingElevator === null) {
			return;
		}
		var connection = new FloorConnection(existingElevator, stateManager.currentState.pointAtCursor, FloorConnection.ELEVATOR);
		stateManager.currentFloor.floorConnections.push(connection);
	}
	
	// Add a new elevator
	else {
		var connection = new FloorConnection(newElevator, stateManager.currentState.pointAtCursor, FloorConnection.ELEVATOR);
		stateManager.currentFloor.floorConnections.push(connection);
	}
	
	// Reset field
	$("#newElevator").val("")
	stateManager.redraw();
});

/**
 * Summary: This function is called when the user presses the cancel
			button on the stair box. It closes the stair box and
			resets the fields inside the box.
 * Parameters: n/a
 * Returns: n/a
**/	
$("#cancelElevator").click(function(event) {
	event.preventDefault();
	$("#elevator_pop").toggleClass("hidden", true);
	$("#newElevator").val("")
});


/**
 * Summary: Checks if a given element is in a list.
 * Parameters: list: The list
			   element: The target element
 * Returns: True if 'element' is in 'list'
			False otherwise
**/	
ElevatorState.prototype.inList = function(list, element) {
	for (var i = 0; i < list.length; i ++) {
		if (list[i] == element) {
			return true;
		}
	}
	return false;
}

/**
 * Summary: Looks at all interfloor connections that are elevators
			to generate a list of unique elevator names.
 * Parameters: n/a
 * Returns: A unique list of the names of all elevators
**/	
ElevatorState.prototype.getExistingElevators = function() {
	var elevators = [];
	for (var i = 0; i < stateManager.building.floors.length; i ++) {
		for (var j = 0; j < stateManager.building.floors[i].floorConnections.length; j ++) {
			var connection = stateManager.building.floors[i].floorConnections[j];
			// We found an existing elevator
			if (connection.floorConnectionType === FloorConnection.ELEVATOR) {
				if (!this.inList(elevators, connection.label)) {
					elevators.push(connection.label);
				}
			}
		}
	}
	var uniqueElevators = []
	$.each(elevators, function(i, el){
		if($.inArray(el, uniqueElevators) === -1) uniqueElevators.push(el);
	});
	console.log(uniqueElevators)
	return uniqueElevators;
}

/**
 * Summary:	When the user clicks, open a stair box and populate possible stair case names.
 * Parameters: n/a
 * Returns: n/a
**/	
ElevatorState.prototype.click = function(event) {
	this.pointAtCursor = stateManager.currentFloor.globals.view.toRealWorld(
		new Point(event.pageX - stateManager.currentFloor.globals.canvas.x, 
		event.pageY - stateManager.currentFloor.globals.canvas.y));
	
	// Populate a list of existing stair names
	var elevatorList = $('#selectmultipleelevator');
	elevatorList.empty();
	var existingElevators = this.getExistingElevators();
	for (var i = 0; i < existingElevators.length; i ++) {
		elevatorList.append("<option>" + existingElevators[i] + "</option>");
	}

	// Required after modifiying the list of stair names
	setTimeout(function() {
		$("#elevator_pop").css({
			top: event.pageY + "px",
			left: event.pageX + "px"
		}).toggleClass("hidden", false);
							 
		this.stateManager.redraw();
	}, 0)
}


//NEED TO HAVE
ElevatorState.prototype.enter = function() {
}

//NEED TO HAVE
ElevatorState.prototype.exit = function() {
	$("#elevator_pop").toggleClass("hidden", true);
}

ElevatorState.prototype.mouseMove = function(event) {	
}

ElevatorState.prototype.draw = function() {
}
