var ClassifyState = function(stateMan) {
	this.stateManager = stateMan;
	this.activeSpace;
}

$("#classification_submit").click(function(event) {
	event.preventDefault();
	var label = $("#label").val();
	var type = $('input[name=type]:checked', '#classification_pop').val().toLowerCase();
	this.type = type;
	$("#classification_pop").toggleClass("hidden", true);
});

	
$("#classification_cancel").click(function(event) {
	event.preventDefault();
	$("#classification_pop").toggleClass("hidden", true);;
});

ClassifyState.prototype = new BaseState();

ClassifyState.prototype.enter = function() {
	var curSpaces = stateManager.currentFloor.spaces;
	console.log(curSpaces);
	var allSpaces = detectRooms(stateManager.currentFloor.globals.walls, curSpaces);
	console.log(allSpaces);
	stateManager.currentFloor.spaces = allSpaces;
}

ClassifyState.prototype.exit = function() {
	this.activeRoom = undefined;
	$("#classification_pop").toggleClass("hidden", true);;
}

ClassifyState.prototype.mouseMove = function(event) {
	var pointAtCursor = stateManager.currentFloor.globals.view.toRealWorld(new Point(event.pageX - stateManager.currentFloor.globals.canvas.x, 
															event.pageY - stateManager.currentFloor.globals.canvas.y));
	this.roomSelect(pointAtCursor);
	
	
}

ClassifyState.prototype.click = function(event) {
	$("#classification_pop").css({
		top: event.pageY + "px",
		left: event.pageX + "px"
	}).toggleClass("hidden", false);;
	var pointAtCursor = stateManager.currentFloor.globals.view.toRealWorld(new Point(event.pageX - stateManager.currentFloor.globals.canvas.x, 
															event.pageY - stateManager.currentFloor.globals.canvas.y));
	this.stateManager.redraw();
}

ClassifyState.prototype.draw = function() {
}

ClassifyState.prototype.roomSelect = function(point) {
	var point = new Point(x,y);
	var allSpaces = stateManager.currentFloor.spaces;
	for (var i = 0; i < allSpaces.length; i++) {
		var curSpace = allSpaces[i];
		if (this.activeSpace !== curSpace) drawPoly = false;
	}
	for (var i = 0; i < allSpaces.length; i ++) {
		var curSpace = allSpaces[i];
		//var width = stateManager.currentFloor.globals.canvas.width;
		if (curSpace.pointInSpace(point, 5000, false)) {
			console.log("point in space!");
			curSpace.drawPoly = true;
			this.activeSpace = curSpace;
			console.log(this.activeSpace);
			return; // only select one room
		}
	}
	
	this.activeSpace = undefined;
}
