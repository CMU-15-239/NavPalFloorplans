function onWall(point, line) {
	((this.p1.x >= point.x && point.x >= this.p2.x) ||
	 (this.p1.x <= point.x && point.x <= this.p2.x)) &&
	 ((this.p1.y >= point.y && point.y >= this.p2.y) ||
	 (this.p1.y <= point.y && point.y <= this.p2.y));
}

function fillSector(space, sector) {
	var vertices = space.points;
	var hitPoint;
	var hit = false;
	var v = 0;
	while (!hit && v<vertices.length) {
		var vertex = vertices[v];
		var hitVertices = []
		var startRow = Math.max(0, vertex.x - 1)
		var startCol = Math.max(0, vertex.y - 1)
		var endRow = Math.min(sector.length, vertex.x + 1)
		var endCol = Math.min(sector[0].length, vertex.y + 1)
		for (var r=startRow; r<endRow; r++) {
			for (var c=startCol; c<endCol; c++) {
				for (var w=0; w<space.walls.length; w++) {
					if (c !== vertex.x && r !== vertex.y) {
						var point = new Point(r, c);
						if (onWall(point, space.walls[w])) {
							hitVertices.append(point)
						}
					}
				}
			}
		}
		if (hitVertices.length === 3) {
			hit = true;
		}
	}
	
}


function sector(spaces, map) {
	var rows = map.length;
	var cols = map[0].length;
	var sector = map;
	for(var s = 0; s < spaces.length; s++) {
		sector = fillSector(spaces[s], sector)
	}
	return sector;
}