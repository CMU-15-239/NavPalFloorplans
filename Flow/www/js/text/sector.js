//sector.js


function pointOnLines(point, lines) {
	for(var l = 0; l < lines.length; l++) {
		if(lines[l].pointNearLine(point)) {
			return true;
		}
	}
	return false;
}

function pointInShape(point, lines, width, height, linesInShape) {
	if(pointOnLines(point, lines)) {return linesInShape;}
	else{
		var inShapeSegments = [];
		var currP1 = null;
		for(var rx = 0; rx < width; rx++) {
			var currRayPt = {x: rx, y: point.y};
			//console.log("checkingPt: "+JSON.stringify(currRayPt));
			if(pointOnLines(currRayPt, lines)) {
				//console.log("found intersection pt: "+JSON.stringify(currRayPt));
				if(util.exists(currP1)) {
					inShapeSegments.push(new Line(new Point(currP1.x, currP1.y), new Point(currRayPt.x, currRayPt.y)));
					currP1 = null;
				}
				else {
					currP1 = currRayPt;
					//console.log("p1: "+JSON.stringify(currP1));
				}
			}
		}
		console.log(inShapeSegments);
		return pointOnLines(point, inShapeSegments);
	}
	//console.log("pt on wall");
	return false;
}

function findPointInShape(space, width, height) {
	var checkedVertices = [];
	
	for(var l = 0; l < space.walls.length; l++) {
		var endPts = [space.walls[l].p1, space.walls[l].p2];
		for(var ep = 0; ep < endPts.length; ep++) {
			var vertex = endPts[ep];
			var vertexString = JSON.stringify(vertex);
			if(checkedVertices.indexOf(vertexString) == -1) {
				for(var dy = -1; dy <= 1; dy++) {
					for(var dx = -1; dx <= 1; dx++) {
						//if(dy != 0 && dx != 0) {
							var point = {x: vertex.x+dx, y: vertex.y+dy};
							if(0 <= point.x && point.x < width
								&& 0 <= point.y && point.y < height
								&& space.pointInSpace(point, width, false)) {
								return point;
							}
						//}
					}
				}
				checkedVertices.push(vertexString);
			}
		}
	}
	//console.log(checkedVertices);
	return null;
}

function floodFillShape(sector, space, point, fillVal, emptyVal) {
	var checkPts = [{x: point.x-1, y: point.y}, {x: point.x+1, y: point.y},
					{x: point.x, y: point.y-1}, {x: point.x, y: point.y+1}];
	
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

function fillVertices(sector, lines, fillVal, emptyVal) {
	for(var l = 0; l < lines.length; l++) {
		var endPts = [lines[l].p1, lines[l].p2];
		for(var ep = 0; ep < endPts.length; ep++) {
			if(0 <= endPts[ep].x && endPts[ep].x < sector[0].length
			&& 0 <= endPts[ep].y && endPts[ep].y < sector.length
			&& sector[endPts[ep].y][endPts[ep].x] === emptyVal) {
				sector[endPts[ep].y][endPts[ep].x] = fillVal;
			}
		}
	}
	return sector;
}

function fillSector(sector, space, fillVal, emptyVal) {
	//sector = fillVertices(sector, lines, fillVal, emptyVal); //we need to do this because we do fill with 4pt connectivity
	var point = findPointInShape(space, sector[0].length, sector.length);
	sector[point.y][point.x] = fillVal;
	if(util.exists(point) && util.exists(point.x) && util.exists(point.y)) {
		return floodFillShape(sector, space, point, fillVal, emptyVal);
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