
/**
 * Summary: Initialize everything, but only when the document is fully loaded.
**/
$(document).ready(function () 
{
	var generateData = window.generateData = function(spaces, width, height) {
		return {
			graph: (new Floor(spaces)).toOutput(),
			map: generateMap(spaces, width, height),
			//sector: generateSector(spaces, width, height),
			room: generateRoom(spaces, '\n')
		};
	};
	
	var sendDataToServer = window.sendDataToServer = function(spaces, width, height) {
		var genData = generateData(spaces, width, height);
		var genDataId = JSON.stringify(genData).hashCode();
		
		var mapStr = ""; 
		if(util.exists(genData.map) /*&& util.exists(genData.sector)*/) {
			//var sectorStr = "";
			for(var y = 0; y < height; y++) {
				for(var x = 0; x < width; x++) {
					mapStr += genData.map[y][x]+" ";
					//sectorStr += genData.sector[y][x]+" ";
				}
				mapStr += "\n";
				//sectorStr += "\n";
			}
		}
		
		$.ajax({
			type: 'POST',
			url: "/text",
			data: {id: genDataId, map: mapStr, /*sector: sectorStr,*/ room: genData.room},
			success: function() {console.log("posted text data to server!");}
		});
		
		$.ajax({
			type: "POST",
			url: "/graph",
			data: {id: genDataId, graph: genData.graph, width: width, height: height},
			success: function() {console.log("posted graph to server!");}
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
			CUR_POINT = undefined;
			resetLineGlobals();
			CAN_SNAP_TO_LAST = true;
			detectRooms(ALL_WALLS); 
			$("#add_room").css("display", "block");
		}
		else if (STATE === "line_tool") {
			$("#add_room").css("display", "none");
		}
		unselectAll();
	});
	
	$('#detectRooms').click(function() {
		$(".tool").removeClass("active");
		STATE = "room_detection_tool";
		unselectAll();
		CUR_POINT = undefined;
		resetLineGlobals();
		CAN_SNAP_TO_LAST = true;
		//detectRooms(ALL_WALLS); // find closed off rooms
	});
	
	$('#add_room').click(function() {
		addRoomClicked();
	});
	
	$('#done').click(function() {
		addDoorsToRooms();
		return sendDataToServer(ALL_CLOSED_ROOMS, CANVAS.width, CANVAS.height);
	});
	
	$("#label_submit").click(function(event) {
		event.preventDefault();
		BLOCK_CHANGE_ROOM = false;
		var label = $("#label").val();
		ACTIVE_ROOM.label = label;
		var type = $('input[name=type]:checked', '#classification_pop').val().toLowerCase();
		ACTIVE_ROOM.type = type;
		ACTIVE_ROOM = undefined;
		$("#classification_pop").css("display", "none");
		if (allSpacesClassified()) $("#done").removeAttr("disabled");
	});
});