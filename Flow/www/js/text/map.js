function addLines(space, map) {
	for (var d=0; d<space.doors.length; d++) {
		var door = doors[d];
		var x1 = door.p1.x;
		var x2 = door.p2.x;
		var minX = Math.floor(Math.min(x1, x2))
		var maxX = Math.ceil(Math.max(x1, x2))
		// Put line in form y = mx + b
		var m = -door.a / door.b
		var b = -door.c / door.b
		for (var x=minX; x<maxX; x++) {
			var y = m*x + b;
			var epsilon = Math.abs(Math.round(y) - y);
			if (epsilon > 0.2) {
				map[Math.floor(y)][x] = 'O';
				map[Math.ceil(y)][x] = 'O';
			}
			else {
				map[Math.round(y)][x] = 'O';
			}
		}
	}
	return map;
}

function addLines(space, map) {
	for (var w=0; w<space.walls.length; w++) {
		var wall = walls[w];
		var x1 = wall.p1.x
		var x2 = wall.p2.x;
		var minX = Math.floor(Math.min(x1, x2))
		var maxX = Math.ceil(Math.max(x1, x2))
		// Put line in form y = mx + b
		var m = -wall.a / wall.b
		var b = -wall.c / wall.b
		for (var x=minX; x<maxX; x++) {
			var y = m*x + b;
			var epsilon = Math.abs(Math.round(y) - y);
			if (epsilon > 0.2) {
				map[Math.floor(y)][x] = 'X';
				map[Math.ceil(y)][x] = 'X';
			}
			else {
				map[Math.round(y)][x] = 'X';
			}
		}
	}
	return map;
}

function map(spaces) {
	var rows = 15;
	var cols = 15;
	var map = [];
	for (var row=0; row<rows; row++) {
		mapRow = [];
		for (var col=0; col<cols; col++) {
			mapRow.push('O');
		}
		map.push(mapRow);
	}
	for(var s = 0; s < spaces.length; s++) {
		addLines(spaces[s], map)
	}
	for(var s = 0; s < spaces.length; s++) {
		removeEntrances(spaces[s], map)
	}
	return map;
}