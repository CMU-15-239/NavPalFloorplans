
/**
 * Summary: Initialize everything, but only when the document is fully loaded.
**/
$(document).ready(function () 
{
	var generateData = window.generateData = function(spaces, width, height) {
		var graph = new Graph(spaces);
		return {
			graph: graph.toOutput(),
			map: generateMap(spaces, width, height),
			sector: generateSector(spaces, width, height),
			//room: generateRoom(spaces, '\n')
		};
	};
	
	var sendDataToServer = window.sendDataToServer = function(spaces, width, height) {
		var genData = generateData(spaces, width, height);
		var genDataId = JSON.stringify(genData).hashCode();
		$.ajax({
			type: "POST",
			url: "128.237.234.187:8080/graph",
			data: {id: genDataId, graph: genData.graph},
			success: function() {console.log("posted graph to server!");}
		});
		
		var mapStr = ""; var sectorStr = "";
		for(var y = 0; y < height; y++) {
			for(var x = 0; x < width; x++) {
				mapStr += genData.map[y][x]+" ";
				sectorStr += genData.sector[y][x]+" ";
			}
			mapStr += "\n";
			sectorStr += "\n";
		}
		
		$.ajax({
			type: 'POST',
			url: "128.237.234.187:8080/text",
			data: {id: genDataId, map: mapStr, sector: sectorStr, room: genData.room},
			success: function() {console.log("posted text data to server!");}
		});
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