var MoveState = function(stateMan) {
	this.stateManager = stateMan;
}

MoveState.prototype = new BaseState();

MoveState.prototype.enter = function() {
	console.log("enter move state");
}

MoveState.prototype.exit = function() {
}

MoveState.prototype.mouseDown = function(event) {
}