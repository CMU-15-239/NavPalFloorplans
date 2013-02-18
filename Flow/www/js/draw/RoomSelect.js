function roomSelectMouseMoved(x, y) {
	var point = new Point(x,y);
	//console.log(point.toString());
	for (var i = 0; i < ALL_CLOSED_ROOMS.length; i ++) {
		//console.log("Chcking room " + i);
		var room =  ALL_CLOSED_ROOMS[i];
		if (room.pointInSpace(point, CANVAS.width, CANVAS.height, false)) {
			//console.log("INSIDE A ROOM");
			room.drawPoly = true;
			return; // only select one room
		}
		else {
			room.drawPoly = false;
		}
		
	}
	
	//console.log("OUTSIDE A ROOM");
}