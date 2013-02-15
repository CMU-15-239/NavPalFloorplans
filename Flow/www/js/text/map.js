function addLines(space, map) {
	for (var line=0; line<space.lines.length; line++) {
		var (x1, x2) = lines[line].p1.x, lines[line].p2.x;
		var minX = Math.floor(Math.min(x1, x2))
		var maxX = Math.ceil(Math.max(x1, x2))
		// Put line in form y = mx + b
		var m = -line.a / line.b
		var b = -line.c / line.b
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
	var rows = CANVAS.width;
	var cols = CANVAS.height;
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
	return map;
}