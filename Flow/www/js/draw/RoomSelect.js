function roomSelectMouseMoved(x, y) {
	var point = new Point(x,y);
	for (var i = 0; i < ALL_CLOSED_ROOMS.length; i ++) {
		var room = ALL_CLOSED_ROOMS[i];
		for (var j = 0; j < room.walls.length; j ++) {
			room.walls[j].definesRoom = false;
			room.drawPoly = false;
		}
	}
	
	//console.log(point.toString());
	for (var i = 0; i < ALL_CLOSED_ROOMS.length; i ++) {
		//console.log("Chcking room " + i);
		var room =  ALL_CLOSED_ROOMS[i];

		
		if (room.pointInSpace(point, CANVAS.width, false)) {
			console.log("INSIDE A ROOM " + i);
			room.drawPoly = true;
			// Color the lines!!
			for (var j = 0; j < room.walls.length; j ++) {
				room.walls[j].definesRoom = true;
			}
			
			return i; // only select one room
		}
		else {
			
		}
		
	}
	

	return -1;
	console.log("OUTSIDE A ROOM");

}