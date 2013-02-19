
/**
 * Summary: Initialize everything, but only when the document is fully loaded.
**/
$(document).ready(function () 
{
	var generateData = window.generateData = function(spaces, width, height) {
		return {
			graph: (new Graph(spaces)).toOutput(),
			map: generateMap(spaces, width, height),
			sector: generateSector(spaces, width, height),
			room: generateRoom(spaces, '\n')
		};
	};
	
	var sendDataToServer = window.sendDataToServer = function(spaces, width, height) {
		var genData = generateData(spaces, width, height);
		var genDataId = JSON.stringify(genData).hashCode();
		$.ajax({
			type: "POST",
			url: "/graph",
			data: {id: genDataId, graph: genData.graph},
			success: function() {console.log("posted graph to server!");}
		});
		
		if(util.exists(genData.map) && util.exists(genData.sector)) {
			var mapStr = ""; var sectorStr = "";
			for(var y = 0; y < height; y++) {
				for(var x = 0; x < width; x++) {
					mapStr += genData.map[y][x]+" ";
					sectorStr += genData.sector[y][x]+" ";
				}
				mapStr += "\n";
				sectorStr += "\n";
			}
		}
		
		$.ajax({
			type: 'POST',
			url: "/text",
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
		return sendDataToServer(ALL_CLOSED_ROOMS, CANVAS.width, CANVAS.height);
	});
	
	$("#label_submit").click(function(event) {
		event.preventDefault();
		BLOCK_CHANGE_ROOM = false;
		var label = $("#label").val();
		console.log(label);
		var type = $('input[name=type]:checked', '#classification_pop').val().toLowerCase();
		console.log(type);
		ACTIVE_ROOM = undefined;
		$("#classification_pop").css("display", "none");
	});
	
	/*var jsonObj = '{"lines":[{"line":[{"p1":[0,0]},{"p2":[100,100]}]}, {"line":[{"p1":[100,100]},{"p2":[200,500]}]}]}';
	var obj = $.parseJSON(jsonObj);
	console.log(obj);
	importLines(obj);*/
});