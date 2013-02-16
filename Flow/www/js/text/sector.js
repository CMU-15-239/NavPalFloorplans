//sector.js


function pointOnLines(point, lines) {
	for(var l = 0; l < lines.length; l++) {
		if(lines[l].pointNearLine(point, 0)) {
			return true;
		}
	}
	return false;
}

function pointInShape(point, lines, width, height) {
	var inShapeSegments = [];
	if(!pointOnLines(point, lines)) {
		var currP1 = null;
		for(var rx = 0; rx < width; rx++) {
			var currRayPt = {x: rx, y: point.y};
			if(pointOnLines(currRayPt, lines)) {
				if(util.exists(currP1)) {
					inShapeSegments.push(new Line(currP1, new Point(currRayPt.x, currRayPt.y)));
					currP1 = null;
				}
				else {
					currP1 = new Point(currRayPt.x, currRayPt.y);
				}
			}
		}
	}
	
	return pointOnLines(point, inShapeSegments);
}

function findPointInShape(lines, width, height) {
	var checkedVertices = [];
	
	for(var l = 0; l < lines.length; l++) {
		var endPts = [lines[l].p1, lines[l].p2];
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
								&& pointInShape(point, lines, width, height)) {
								return point;
							}
						//}
					}
				}
				checkedVertices.push(vertexString);
			}
		}
	}
	console.log(checkedVertices);
}

function floodFillShape(sector, lines, point, fillVal, emptyVal) {
	var checkPts = [{x: point.x-1, y: point.y}, {x: point.x+1, y: point.y},
					{x: point.x, y: point.y-1}, {x: point.x, y: point.y+1}];
	
	for(var cp = 0; cp < checkPts.length; cp++) {
		var fPoint = checkPts[cp];
		if(0 <= fPoint.x && fPoint.x <= sector[0].length
			&& 0 <= fPoint.y && fPoint.y <= sector.length
			&& sector[fPoint.y][fPoint.x] === emptyVal) {
			sector[fPoint.y][fPoint.x] = fillVal;
			if(!pointOnLines(fPoint, lines)) {
				floodFillShape(sector, lines, fPoint, fillVal, emptyVal);
			}
		}
	}
					
}

function fillSector(sector, lines, fillVal, emptyVal) {
	var point = findPointInShape(lines, sector[0].length, sector.length);
	floodFillShape(sector, lines, point, fillVal, emptyVal);
}


function sector(spaces, width, height) {
	var sector = [];
	var emptyChar = '-1';
	for (var row=0; row < width; row++) {
		sectorRow = [];
		for (var col=0; col < height; col++) {
			sectorRow.push(emptyChar);
		}
		sector.push(sectorRow);
	}
	
	for(var s = 0; s < spaces.length; s++) {
		fillSector(sector, spaces[s].walls, sector, ""+s, emptyChar)
	}
	
	return sector;
}