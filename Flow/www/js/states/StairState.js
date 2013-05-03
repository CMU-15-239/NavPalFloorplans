/**
	StairState.js
	Written by Paul Davis
	pjbdavis@gmail.com
	Spring 2013

	This file is responsible for the stair state. It specifies the behavior
	when the user clicks to place a stair, and when the user hits the submit
	or cancel button on the stair box. It allows the user to create, remove,
	and edit stairs.
	
*/

var StairState = function(stateMan) {
	this.stateManager = stateMan;
	this.pointAtCursor; 
}

//NEED TO HAVE
StairState.prototype = new BaseState();


/**
 * Summary: This function is called when the user presses the sumbit
			button on the stair box. It created a new interfloor connection based
			on the information submitted.
 * Parameters: n/a
 * Returns: n/a
**/
$("#saveStair").click(function(event) {

	// Don't reload the whole page
	event.preventDefault();
	
	// Hide the stair box, clear the fields in the box
	$("#stair_pop").toggleClass("hidden", true);
	var newStair = $("#newStair").val();
	var existingStair = $("#selectmultiple").val();
	
	
	// Use an existing stair name
	if (newStair === "") {
		// Nothing choosen
		if (existingStair === null) {
			return;
		}
		var connection = new FloorConnection(existingStair, stateManager.currentState.pointAtCursor, FloorConnection.STAIR);
		stateManager.currentFloor.floorConnections.push(connection);
	}
	
	// Add a new stair
	else {
		var connection = new FloorConnection(newStair, stateManager.currentState.pointAtCursor, FloorConnection.STAIR);
		stateManager.currentFloor.floorConnections.push(connection);
	}
	
	// Reset field
	$("#newStair").val("")
	stateManager.redraw();
});

/**
 * Summary: This function is called when the user presses the cancel
			button on the stair box. It closes the stair box and
			resets the fields inside the box.
 * Parameters: n/a
 * Returns: n/a
**/	
$("#cancelStair").click(function(event) {
	event.preventDefault();
	$("#stair_pop").toggleClass("hidden", true);
	$("#newStair").val("")
});


/**
 * Summary: Checks if a given element is in a list.
 * Parameters: list: The list
			   element: The target element
 * Returns: True if 'element' is in 'list'
			False otherwise
**/	
StairState.prototype.inList = function(list, element) {
	for (var i = 0; i < list.length; i ++) {
		if (list[i] == element) {
			return true;
		}
	}
	return false;
}

/**
 * Summary: Looks at all interfloor connections that are stairs
			to generate a list of unique stair names.
 * Parameters: n/a
 * Returns: A unique list of the names of all stairs
**/	
StairState.prototype.getExistingStairs = function() {
	var stairs = [];
	for (var i = 0; i < stateManager.building.floors.length; i ++) {
		for (var j = 0; j < stateManager.building.floors[i].floorConnections.length; j ++) {
			var connection = stateManager.building.floors[i].floorConnections[j];
			// We found an existing stair case
			if (connection.floorConnectionType === FloorConnection.STAIR) {
				if (!this.inList(stairs, connection.label)) {
					stairs.push(connection.label);
				}
			}
		}
	}
	var uniqueStairs = []
	$.each(stairs, function(i, el){
		if($.inArray(el, uniqueStairs) === -1) uniqueStairs.push(el);
	});
	return uniqueStairs;
}

/**
 * Summary:	When the user clicks, open a stair box and populate possible stair case names.
 * Parameters: n/a
 * Returns: n/a
**/	
StairState.prototype.click = function(event) {
	this.pointAtCursor = stateManager.currentFloor.globals.view.toRealWorld(
		new Point(event.pageX - stateManager.currentFloor.globals.canvas.x, 
		event.pageY - stateManager.currentFloor.globals.canvas.y));
	
	// Populate a list of existing stair names
	var stairList = $('#selectmultiple');
	stairList.empty();
	var existingStairs = this.getExistingStairs();
	for (var i = 0; i < existingStairs.length; i ++) {
		stairList.append("<option>" + existingStairs[i] + "</option>");
	}
	
	// Required after modifiying the list of stair names
	setTimeout(function() {
		$("#stair_pop").css({
			top: event.pageY + "px",
			left: event.pageX + "px"
		}).toggleClass("hidden", false);
							 
		this.stateManager.redraw();
	}, 0)
}


//NEED TO HAVE
StairState.prototype.enter = function() {
}

//NEED TO HAVE
StairState.prototype.exit = function() {
	$("#stair_pop").toggleClass("hidden", true);
}

StairState.prototype.mouseMove = function(event) {	
}

StairState.prototype.draw = function() {
}
