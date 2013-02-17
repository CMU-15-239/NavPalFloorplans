
/**
 * Summary: Initialize everything, but only when the document is fully loaded.
**/
$(document).ready(function () 
{
	var generateData = window.generateData = function(spaces, width, height) {
		var graph = new Graph(spaces);
		return {
			graph: graph.toOutput(),
			mapArr: generateMap(spaces, width, height),
			sectorArr: generateSector(spaces, width, height),
			roomStr: generateRoom(spaces, '\n')
		};
	};

	/* Initialize the canvas */
    can = document.getElementById("canvas");
	can.width = CANVAS_WIDTH;
	can.height = CANVAS_HEIGHT;
    CANVAS = can.getContext("2d");

	CANVAS.width = CANVAS_WIDTH;
	CANVAS.height = CANVAS_HEIGHT;
	CANVAS.x = can.offsetLeft;
	CANVAS.y = can.offsetTop;
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
	$("canvas").keydown(function(event) {
		keyDown(event);
	});
	$("canvas").keyup(function(event) {
		keyUp(event);
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
			detectRooms(ALL_WALLS); // find closed off rooms
			CUR_POINT = undefined;
			resetLineGlobals();
			CAN_SNAP_TO_LAST = true;
			$("#add_room").css("display", "block");
		}
		else if (STATE === "line_tool") {
			$("#add_room").css("display", "none");
		}
		selectToolInit();
	});
	
	$('#add_room').click(function () {
		addRoomClicked();
	});
});