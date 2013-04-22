var StairState = function(stateMan) {
	this.stateManager = stateMan;
	this.pointAtCursor; 
}

$("#saveStair").click(function(event) {

	event.preventDefault();
	$("#stair_pop").toggleClass("hidden", true);
	var newStair = $("#newStair").val();
	var existingStair = $("#selectmultiple").val();
	
	
	// Use an existing stair
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
	
	$("#newStair").val("")
	//var description = $("#description").val();
	stateManager.redraw();
/*
	console.log("Added new landmark! name:" + name + ", description " + description + stateManager.currentState.pointAtCursor.toString());
	stateManager.currentFloor.landmarks.push(landmark);
	stateManager.redraw();
	
	// Clear values for next time
	$("#name").val("");
	$("#description").val("");
	*/
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

	
$("#cancelStair").click(function(event) {
	event.preventDefault();
	$("#stair_pop").toggleClass("hidden", true);
});

//NEED TO HAVE
StairState.prototype = new BaseState();

//NEED TO HAVE
StairState.prototype.enter = function() {
}

//NEED TO HAVE
StairState.prototype.exit = function() {
	$("#stair_pop").toggleClass("hidden", true);
}


StairState.prototype.mouseMove = function(event) {
	
}

StairState.prototype.inList = function(list, element) {
	for (var i = 0; i < list.length; i ++) {
		if (list[i] == element) {
			return true;
		}
	}
	return false;
}

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

StairState.prototype.click = function(event) {
	this.pointAtCursor = stateManager.currentFloor.globals.view.toRealWorld(new Point(event.pageX - stateManager.currentFloor.globals.canvas.x, 
						 event.pageY - stateManager.currentFloor.globals.canvas.y));
						 
	var stairList = $('#selectmultiple');
	stairList.empty();
	var existingStairs = this.getExistingStairs();
	for (var i = 0; i < existingStairs.length; i ++) {
		stairList.append("<option>" + existingStairs[i] + "</option>");
	}
	
	setTimeout(function() {
		$("#stair_pop").css({
			top: event.pageY + "px",
			left: event.pageX + "px"
		}).toggleClass("hidden", false);
							 
		this.stateManager.redraw();
	}, 0)
}

StairState.prototype.draw = function() {

}
