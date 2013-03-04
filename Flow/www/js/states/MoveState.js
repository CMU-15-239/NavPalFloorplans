var MoveState = function(stateMan) {
	this.stateManager = stateMan;
}

MoveState.prototype = new BaseState();

MoveState.prototype.enter = function() {
	console.log("enter move state");
}

MoveState.prototype.exit = function() {
	console.log("exit move state");
}