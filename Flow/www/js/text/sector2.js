//sector.js
//var Sector = {
	
	//pointNearLine : function(line, point, radius) {
function pointNearLine(line, point, radius)
{		
	var a = line.p1.y - line.p2.y;
	var b = line.p2.x - line.p1.x;
	var c = line.p1.x * (line.p2.y - line.p1.y) - line.p1.y * (line.p2.x - line.p1.x);
	var distConst = Math.sqrt(a*a + b*b);
	
	var signPointToLine = a * point.x + b * point.y + c;
	
	var close = (Math.abs(signPointToLine) / distConst) <= radius;
	//Make sure the point is actually within the endpoints of the line.
	var onLine = ((line.p1.x >= point.x && point.x >= line.p2.x) ||
		 (line.p1.x <= point.x && point.x <= line.p2.x)) &&
		 ((line.p1.y >= point.y && point.y >= line.p2.y) ||
		 (line.p1.y <= point.y && point.y <= line.p2.y));
		 
	return close && onLine;
}

/********************************************************************************/

	//pointOnLines : function(lines, point, radius) {
function pointOnLines(lines, point, radius)
{
	for(var l = 0; l < lines.length; l++)
	{
		//if(this.pointNearLine(lines[l], point, radius))
		if(pointNearLine(lines[l], point, radius))
		{
			return lines[l];
		}
	}
	return null;
}

/********************************************************************************/

	//pointInSpace : function(space, point, width, includeLine) {
function pointInSpace(space, point, width, includeLine)
{
	//console.log("params: "+JSON.stringify(point)+" width: "+width+" includeLine: "+includeLine);
	//If point is very close to a line, then it's only in the space if we should include the walls' lines.
	//if (util.exists(this.pointOnLines(space.walls, point, 0.5)))
	if (util.exists(pointOnLines(space.walls, point, 0.5)))
	{
		return (includeLine === true);
	}
	else {
		//console.log("jere");
		//compile intersecting lines
		var inShapeSegments = [];
		var lastLineIntersected = null;
		var currP1 = null;
		for(var xr = 0; xr < width; xr++) {
			var currRayPt = {x: xr, y: point.y};
			//console.log("checkingPt: "+JSON.stringify(currRayPt));
			//var intersectLine = this.pointOnLines(space.walls, currRayPt, 0.5);
			var intersectLine = pointOnLines(space.walls, currRayPt, 0.5);
			if(util.exists(intersectLine)) {
				//console.log("found intersection pt: "+JSON.stringify(currRayPt));
				if(!intersectLine.equals(lastLineIntersected)) {
					lastLineIntersected = intersectLine;
					if(util.exists(currP1)) {
						//deep cpy
						var inShapeSegment = {p1: {x: currP1.x, y: currP1.y}, p2: {x: currRayPt.x, y: currRayPt.y}}
						inShapeSegments.push(inShapeSegment);
						//if(this.pointNearLine(inShapeSegment, point, 0.5))
						if(pointNearLine(inShapeSegment, point, 0.5))
						{
							return true;
						}
						currP1 = null;
						//console.log("interLine: "+JSON.stringify(inShapeSegments[inShapeSegments.length-1]));
					}
					else {
						currP1 = {x:currRayPt.x, y:currRayPt.y};
						//console.log("p1: "+JSON.stringify(currP1))
					}
				}
			}
		}
		
		//console.log(inShapeSegments);
		//determine if point is on one of these intersected lines
		return false;
		/*
		for(var s = 0; s < inShapeSegments.length; s++) {
			if(inShapeSegments[s].pointNearLine(point, 0)) {
				return true;
			}
		}
		return false;
		*/
	}
}
	
/********************************************************************************/
	
	//findFillVal : function(spaces, point, width, emptyVal) {
function findFillVal (spaces, point, width, emptyVal) 
{
		for(var s = 0; s < spaces.length; s++)
		{
			//if(this.pointInSpace(spaces[s], point, width, true))
			if(pointInSpace(spaces[s], point, width, true))
			{
				return ""+s;
			}
		}
		return emptyVal;
}

/********************************************************************************/

	//generateSector : function(spaces, width, height) {
function generateSector(spaces, width, height)
{		
	var sector = [];
	var emptyVal = '-1';
	//x = col, y = row
	
	for(var row = 0; row < height; row++)
	{
		var sectorRow = [];
		for(var col = 0; col < width; col++)
		{
			//sectorRow.push(this.findFillVal(spaces, {x:col, y:row}, width, emptyVal));
			sectorRow.push(findFillVal(spaces, {x:col, y:row}, width, emptyVal));
		}
		sector.push(sectorRow);
	}
	
	return sector;
}

/********************************************************************************/

/*
 * 
 * name: unknown
 * @param
 * @return
 * 
 */
function scaleSpace(spaces, scaleFactor)
{
	var scaledSpaces = jQuery.extend(scaledSpaces, spaces);

	// Loop through each space and scale down the lines segments representing a wall
	for(var s = 0; s < spaces.length; s++)
	{
		var currentSpace = spaces[s];

		for(var w = 0; w < currentSpace.walls.length; w++)
		{
			var currentWall = currentSpace.walls[w];

			// Call function to scale the line since 
		}
	}
	
	return 
}
	
/********************************************************************************/
	
	//generateSectorStr : function(spaces, width, heigth) {
function generateSectorStr(spaces, width, height, scale, res)
{
	//var sector = this.generateSector(spaces, width, height);
	var sector = generateSector(spaces, width, height);
	var sectorStr = ""
	
	sectorStr += height + " " + width + " " + scale + " " + res + "\n";
	
	for(var y = 0; y < height; y++)
	{
		for(var x = 0; x < width; x++)
		{
			sectorStr += sector[y][x]+" ";
		}
		sectorStr += "\n";
	}
		
	return sectorStr;
}

//};

//module.exports = Sector;
