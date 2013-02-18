//sector.js

function floodFillShape(sector, space, startPoint, fillVal, emptyVal) {
	/*var fPoints = [startPoint];
	while(fPoints.length > 0) {
		var fPoint = fPoints.splice(0,1);
		if(0 <= fPoint.x && fPoint.x < sector[0].length
			&& 0 <= fPoint.y && fPoint.y < sector.length
			&& space.pointInSpace(fPoint, sector[0].length, true)
			&& sector[fPoint.y][fPoint.x] === emptyVal) {
			sector[fPoint.y][fPoint.x] = fillVal;
			checkPts = checkPts.concat([{x: fPoint.x-1, y: fPoint.y},
					{x: fPoint.x+1, y: fPoint.y}, {x: fPoint.x, y: fPoint.y-1},
					{x: fPoint.x, y: fPoint.y+1}]);
		}
	}
	console.log(sector);
	return sector;
	*/
	
	var checkPts = [{x: startPoint.x-1, y: startPoint.y}, {x: startPoint.x+1, y: startPoint.y},
					{x: startPoint.x, y: startPoint.y-1}, {x: startPoint.x, y: startPoint.y+1}];
	
	//console.log("flood filling: "+JSON.stringify(point));
	for(var cp = 0; cp < checkPts.length; cp++) {
		var fPoint = checkPts[cp];
		//console.log("checkingPt: "+JSON.stringify(fPoint)+" sectorVal: "+sector[fPoint.y][fPoint.x]
		//			+"pointOnLines: "+pointOnLines(fPoint, lines));
		if(space.pointInSpace(fPoint, sector[0].length, true)
			&& sector[fPoint.y][fPoint.x] === emptyVal) {
			sector[fPoint.y][fPoint.x] = fillVal;
			floodFillShape(sector, space, fPoint, fillVal, emptyVal);
		}
	}
	return sector;
}

function fillSector(sector, space, fillVal, emptyVal) {
	var startPoint = space.walls[0].p1;
	if(util.exists(startPoint) && util.exists(startPoint.x) && util.exists(startPoint.y)) {
		return floodFillShape(sector, space, startPoint, fillVal, emptyVal);
	}
	else {
		//alert("did not draw shape with lines: "+JSON.stringify(lines));
		return sector;
	}
}


function generateSector(spaces, width, height) {
	var sector = [];
	var emptyChar = '-1';
	//x = col, y = row
	for (var row=0; row < height; row++) {
		sectorRow = [];
		for (var col=0; col < width; col++) {
			sectorRow.push(emptyChar);
		}
		sector.push(sectorRow);
	}
	
	for(var s = 0; s < spaces.length; s++) {
		//console.log(spaces[s]);
		sector = fillSector(sector, spaces[s], ""+s, emptyChar)
	}
	
	//console.log(JSON.stringify(sector));
	return sector;
}