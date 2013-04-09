$(".navigationImage").click(function() {
		console.log(this);
		var domImage = this;
		GLOBALS.canvas.image = image;
		var image = new Image(); 
		image.src = domImage.src;
		console.log(image.width);
		//GLOBALS.canvas.drawImage(image,0,0,GLOBALS.canvas.width,GLOBALS.canvas.height);
		GLOBALS.canvas.image = image;
	}
)

function resizeCanvas() {
	var can = document.getElementById("canvas");
	can.width = window.innerWidth - 95;
	can.height = window.innerHeight - 230;
    var canvas = can.getContext("2d");
	canvas.width = can.width;
	canvas.height = can.height;
	canvas.x = can.offsetLeft;
	canvas.y = can.offsetTop;
	return canvas;
}

/**
 * Summary: Initialize everything, but only when the document is fully loaded.
**/
$(window).ready(function () 
{
	/* Initialize the canvas */
    var canvas = resizeCanvas();
	
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

$(window).resize(function() {
	resizeCanvas();
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
		event.preventDefault();
		stateManager.currentState.keyPress(event);
	});
	
	$("#canvas").keydown(function(event) {
		var keyCode = event.keyCode;
		//Prevent default browser behavior on space.
		if (keyCode === 32) {
			event.preventDefault();
		}
		stateManager.currentState.keyDown(event);
	});
	
	$("#canvas").keyup(function(event) {
		stateManager.currentState.keyUp(event);
	});
	
	//$("#canvas").on('mousewheel', function(event){console.log(event)});
	$("#canvas").on('mousewheel', function(event){
		event.preventDefault();
		stateManager.scroll(event);
	});
	
}