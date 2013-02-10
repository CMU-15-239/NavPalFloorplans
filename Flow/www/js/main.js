
/**
 * Summary: Initialize everything, but only when the document is fully loaded.
**/
$(document).ready(function () 
{
	/* Initialize the canvas */
    can = document.getElementById("canvas");
	can.width = CANVAS_WIDTH;
	can.height = CANVAS_HEIGHT;
    CANVAS = can.getContext("2d");

	CANVAS.width = CANVAS_WIDTH;
	CANVAS.height = CANVAS_HEIGHT;
    can.addEventListener("mousemove", mouseMoved);
    /*CANVAS.addEventListener("mouseout", mouseOut);
    CANVAS.addEventListener("mouseover", mouseIn); */
    can.addEventListener("click", mouseClicked); 
	
    can.addEventListener('mousedown', mouseDown);
    can.addEventListener('mouseup', mouseUp);	
	//can.addEventListener("keypress", keyPressed);
	
	// Add tab index to ensure the canvas retains focus
	$("#canvas").attr("tabindex", "0");
	$("#canvas").keypress(function(event) {
		keyPressed(event);
	});
    // Mouse down override to prevent default browser controls from appearing
    $("canvas").mousedown(function(){
		$(this).focus(); 
		return false;
	}); 
	
	$(".tool").click(function() {
		$(".tool").removeClass("active");
		$(this).addClass("active");
		
		STATE = $(this).attr("id");
		if (STATE === "select_tool") {
			CUR_POINT = undefined;
			LAST_POINT = undefined;
			//resetLineGlobals();
		}
	});
	
});