var SelectState = function(stateMan) {
	this.stateManager = stateMan;
}

SelectState.prototype = new BaseState();

SelectState.prototype.enter = function() {
	console.log("enter select state");
}

SelectState.prototype.exit = function() {
	console.log("exit select state");
}