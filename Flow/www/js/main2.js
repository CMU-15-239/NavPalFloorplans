
/**
 * Summary: Initialize everything, but only when the document is fully loaded.
**/
$(document).ready(function () 
{
	/* Initialize the canvas */
    can = document.getElementById("canvas");
	can.width = 500;
	can.height = 500;
    CANVAS = can.getContext("2d");
	CANVAS.width = 500;
	CANVAS.height = 500;
	CANVAS.x = can.offsetLeft;
	CANVAS.y = can.offsetTop;
	
	var stateManager = new StateManager();
	
	/* The event handler for when a new state is clicked */
	$(".tool").click(function() {
		$(".tool").removeClass("active");
		$(this).addClass("active");
		
		var newState = $(this).attr("id");
		stateManager.changeState(newState);
	});
	
	initCanvasEventHandlers(stateManager);
});

function initCanvasEventHandlers(stateManager) {
	$("#canvas").click(function(event) {
		stateManager.currentState.click(event);
	});
	
	$("#canvas").mousemove(function(event) {
		stateManager.currentState.mouseMove(event);
	});
	
	$("#canvas").mousedown(function(event) {
		stateManager.currentState.mouseDown(event);
	});
	
	$("#canvas").mouseup(function(event) {
		stateManager.currentState.mouseUp(event);
	});
	
	$("#canvas").keypress(function(event) {
		stateManager.currentState.keyPress(event);
	});
	
	$("#canvas").keydown(function(event) {
		stateManager.currentState.keyDown(event);
	});
	
	$("#canvas").keyup(function(event) {
		stateManager.currentState.keyUp(event);
	});	
}