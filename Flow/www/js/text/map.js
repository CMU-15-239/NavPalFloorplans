function removeDoors(space, map, doorChar) {
	for (var d=0; d<space.doors.length; d++) {
		var door = space.doors[d];
		var x1 = door.p1.x;
		var x2 = door.p2.x;
		var y1 = door.p1.y;
		var y2 = door.p2.y;
		var minX = Math.floor(Math.min(x1, x2))
		var maxX = Math.ceil(Math.max(x1, x2))
		var minY = Math.floor(Math.min(y1, y2))
		var maxY = Math.ceil(Math.max(y1, y2))
		// Put line in form y = mx + b
		var m = -door.a / door.b
		var b = -door.c / door.b
		for (var x=minX; x<maxX; x++) {
			var y = m*x + b;
			var epsilon = Math.abs(Math.round(y) - y);
			if (epsilon >= 0) {
				map[Math.floor(y)][x] = doorChar;
				map[Math.ceil(y)][x] = doorChar;
			}
			else {
				map[Math.round(y)][x] = doorChar;
			}
		}
		var m = -door.b / door.a;
		var b = -door.c / door.a;
		for (var y=minY; y<maxY; y++) {
			var x = m*y + b;
			var epsilon = Math.abs(Math.round(y) - y);
			if (epsilon >= 0) {
				map[y][Math.floor(x)] = doorChar;
				map[y][Math.ceil(x)] = doorChar;
			}
			else {
				map[y][Math.round(x)] = doorChar;
			}
		}
	}
	return map;
}

function addLines(space, map, wallChar) {
	//console.log(map);
	for (var w=0; w<space.walls.length; w++) {
		var wall = space.walls[w];
		var x1 = wall.p1.x
		var x2 = wall.p2.x;
		var y1 = wall.p1.y;
		var y2 = wall.p2.y;
		var minX = Math.floor(Math.min(x1, x2))
		var maxX = Math.ceil(Math.max(x1, x2))
		var minY = Math.floor(Math.min(y1, y2))
		var maxY = Math.ceil(Math.max(y1, y2))		// Put line in form y = mx + b
		var m = -wall.a / wall.b
		var b = -wall.c / wall.b
		for (var x=minX; x<maxX; x++) {
			var y = m*x + b;
			var epsilon = Math.abs(Math.round(y) - y);
			if (epsilon >= 0) {
				map[Math.floor(y)][x] = wallChar;
				map[Math.ceil(y)][x] = wallChar;
			}
			else {
				map[Math.round(y)][x] = wallChar;
			}
		}
		var m = -wall.b / wall.a;
		var b = -wall.c / wall.a;
		for (var y=minY; y<maxY; y++) {
			var x = m*y + b;
			var epsilon = Math.abs(Math.round(y) - y);
			if (epsilon >= 0) {
				map[y][Math.floor(x)] = wallChar;
				map[y][Math.ceil(x)] = wallChar;
			}
			else {
				map[y][Math.round(x)] = wallChar;
			}
		}
	}
	return map;
}

function generateMap(spaces, width, height) {
	var map = [];
	var doorChar = '0';
	var wallChar = 'X';
	for (var row=0; row < height; row++) {
		mapRow = [];
		for (var col=0; col < width; col++) {
			mapRow.push(doorChar);
		}
		map.push(mapRow);
	}
	for(var s = 0; s < spaces.length; s++) {
		addLines(spaces[s], map, wallChar)
	}
	for(var s = 0; s < spaces.length; s++) {
		removeDoors(spaces[s], map, doorChar)
	}
	return map;
}