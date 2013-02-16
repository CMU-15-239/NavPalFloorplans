function fillSector(space, sector) {
	var vertices = space.points;
	var hit = false;
	while (!hit) {

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