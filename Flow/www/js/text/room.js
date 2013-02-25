//room.js

/**
 * Summary: Constructs a room text string.
 * Parameters: spaces: List of Space objects
				EOL: String, line termination character
 * Returns: String
**/
function generateRoom(spaces, EOL) {
	if(!util.exists(EOL)) {EOL = "";}
	var info = "";
	for(var s = 0; s < spaces.length; s++) {
		var space = spaces[s];
		var line = "";
		if(space.isRoom()) {
			line += "Room ";
		}
		else if(space.isHallway()) {
			line += "Hallway ";
		}
		else {
			line += "Unknown ";
			alert("error");
		}
		
		line += "path"+s;
		
		//write vertices
		for(var v = 0; v < space.selectPoly.points.length; v++) {
			var point = space.selectPoly.points[v];
			line += " "+point.x+" "+point.y;
		}
		
		
		info += line + EOL;
	}
	
	return info;
}