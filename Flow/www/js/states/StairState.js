var StairState = function(stateMan) {
	this.stateManager = stateMan;
	this.pointAtCursor; 
}

$("#saveStair").click(function(event) {

	event.preventDefault();
	$("#stair_pop").toggleClass("hidden", true);
	var newStair = $("#newStair").val();
	var existingStair = $("#selectmultiple").val();
	
	console.log("New : " + newStair);
	console.log("existing: " + existingStair);
	// Use an existing stair
	if (newStair === "") {
		// Nothing choosen
		if (existingStair === null) {
			return
			;
		}
	}
	
	// Add a new stair
	else {
			
	}
	
	//var description = $("#description").val();
	console.log("stair");
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
	$("#stair_pop").css("display", "none");
});

//NEED TO HAVE
StairState.prototype = new BaseState();

//NEED TO HAVE
StairState.prototype.enter = function() {
}

//NEED TO HAVE
StairState.prototype.exit = function() {
	$("#stair_pop").css("display", "none");
}


StairState.prototype.mouseMove = function(event) {
	
}

StairState.prototype.getExistingStairs = function() {

}

StairState.prototype.click = function(event) {
	this.pointAtCursor = stateManager.currentFloor.globals.view.toRealWorld(new Point(event.pageX - stateManager.currentFloor.globals.canvas.x, 
						 event.pageY - stateManager.currentFloor.globals.canvas.y));
						 
	var stairList = $('#selectmultiple');
	stairList.empty();
	var existingStairs = getExistingStairs();
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
