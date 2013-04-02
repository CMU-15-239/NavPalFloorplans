var PreprocessState = function(stateMan) {
	this.stateManager = stateMan;
}

PreprocessState.prototype = new BaseState();

PreprocessState.prototype.enter = function() {
	console.log("Enter preprocess state!");
}

PreprocessState.prototype.exit = function() {
	console.log("Exit preprocess state!");
}

PreprocessState.prototype.click = function() {
}