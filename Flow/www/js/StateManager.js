var StateManager = function() {
	this.avaliableStates = {
		"Preprocess": new PreprocessState(this),
		"Draw": new DrawState(this),
		"Select": new SelectState(this),
		"Move": new MoveState(this)
	};
	
	this.currentState = this.avaliableStates["Draw"];
	this.currentState.enter();
}

StateManager.prototype.changeState = function(newState) {
	if (this.currentState !== newState) {
		this.currentState.exit();
		this.currentState = this.avaliableStates[newState];
		this.currentState.enter();
	}
}

StateManager.prototype.redraw = function() {
	//First, delete everything from the canvas.
    CANVAS.clearRect(0, 0, CANVAS.width, CANVAS.height);
	this.currentState.draw();
}


