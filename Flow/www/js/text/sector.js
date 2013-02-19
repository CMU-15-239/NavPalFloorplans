//sector.js

function floodFillShape(sector, space, startPoint, fillVal, emptyVal) {
	var checkPts = [startPoint];
	for(var cp = 0; cp < checkPts.length; cp++) {
		var fPoint = checkPts[cp];
		if(space.pointInSpace(fPoint, sector[0].length, true)
			&& sector[fPoint.y][fPoint.x] === emptyVal) {
			
			sector[fPoint.y][fPoint.x] = fillVal;
			checkPts.push({x:fPoint.x+1, y: fPoint.y});
			checkPts.push({x:fPoint.x-1, y: fPoint.y});
			checkPts.push({x:fPoint.x, y: fPoint.y+1});
			checkPts.push({x:fPoint.x, y: fPoint.y-1});
		}
	}
	return sector;
}

function findPointInSpace(space, width) {
	for(var w = 0; w < space.walls.length; w++) {
		var point = space.walls[w].p1;
		for(var dx = -1; dx <= 1; dx++) {
			for(var dy = -1; dy <= 1; dy++) {
				var checkPt = {x: point.x+dx, y: point.y+dy};
				if(space.pointInSpace(checkPt, width, false)) {
					return checkPt;
				}
			}
		}
	}
	return null;
}

function fillSector(sector, space, fillVal, emptyVal) {
	var startPoint = findPointInSpace(space, sector[0].length);
	//console.log(startPoint);
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
		sector = fillSector(sector, spaces[s], ""+s, emptyChar)
	}
	
	//console.log(JSON.stringify(sector));
	return sector;
}