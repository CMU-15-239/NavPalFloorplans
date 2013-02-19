//sector.js

function findFillVal(spaces, point, width, emptyVal) {
	for(var s = 0; s < spaces.length; s++) {
		if(spaces[s].pointInSpace(point, width, true)) {
			return ""+s;
		}
	}
	return emptyVal;
}


function generateSector(spaces, width, height) {
	var sector = [];
	var emptyVal = '-1';
	//x = col, y = row
	
	for(var row = 0; row < height; row++) {
		var sectorRow = [];
		for(var col = 0; col < width; col++) {
			sectorRow.push(findFillVal(spaces, {x:col, y:row}, width, emptyVal));
		}
		sector.push(sectorRow);
	}
	
	return sector;
}