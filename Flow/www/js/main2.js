$(".navigationImage").click(function() {
		console.log(this);
		var domImage = this;
		GLOBALS.canvas.image = image;
		var image = new Image(); 
		image.src = domImage.src;
		console.log(image.width);
		GLOBALS.canvas.drawImage(image,0,0,GLOBALS.canvas.width,GLOBALS.canvas.height);
		GLOBALS.canvas.image = image;
	}
)



/**
 * Summary: Initialize everything, but only when the document is fully loaded.
**/
$(document).ready(function () 
{
	/* Initialize the canvas */
    var can = document.getElementById("canvas");
	can.width = window.innerWidth - 95;
	can.height = window.innerHeight - 240;
    var canvas = can.getContext("2d");
	canvas.width = can.width;
	canvas.height = can.height;
	canvas.x = can.offsetLeft;
	canvas.y = can.offsetTop;
	
	GLOBALS = new GlobalsContainer(canvas);
	
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
	// Add tab index to ensure the canvas retains focus (needed for keydown)
	$("#canvas").attr("tabindex", "0");

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
	
	//$("#canvas").on('mousewheel', function(event){console.log(event)});
	$("#canvas").on('mousewheel', function(event){stateManager.scroll(event)});
	
}