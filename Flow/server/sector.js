//sector.js

/**
 * Summary: Module to create the Sector represtation
**/
var Sector = {
	/**
	 * Summary: Tells if object exists (!= null && != undefined)
	 * Parameters: Object
	 * Returns: bool
	**/
	exists: function(obj) {
		return obj !== null && obj !== undefined;
	},
	
	/**
	 * Summary: 
	 * Parameters: line: Line object (two Point objects)
					point: Point object (x, y coordinate)
					radius: Number, how far point can be from line
	 * Returns: bool
	**/
	pointNearLine : function(line, point, radius) {
		
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
	},

	/**
	 * Summary: Checks if point with in the radius for any of the lines in lines
	 * Parameters: lines : list of Line objects (two Point objects)
					point: Point object (x, y coordinate)
	 * Returns: bool
	**/
	pointOnLines : function(lines, point, radius) {
		for(var l = 0; l < lines.length; l++) {
			if(this.pointNearLine(lines[l], point, radius)) {
				return lines[l];
			}
		}
		return null;
	},
	
	/**
	 * Summary: Checks if two points equal each other
	 * Parameters: p1: Point object (x, y coordinate)
					p2: Point object (x, y coordinate)
	 * Returns: bool
	**/
	pointEquals : function(p1, p2) {
		if(this.exists(p1) && this.exists(p2)) {
			return p1.x === p2.x && p1.y === p2.y
		}
		return false;
	},
	
	/**
	 * Summary: Checks if two lines equal each other
	 * Parameters: line1: Line object (two Point objects)
					line2: Line object (two Point objects)
	 * Returns: bool
	**/
	lineEquals : function(line1, line2) {
		if(this.exists(line1) && this.exists(line2)) {
			var isSameLine = (this.pointEquals(line1.p1, line2.p1) && this.pointEquals(line1.p2, line2.p2));
			
			var isFlippedLine = (this.pointEquals(line1.p2, line2.p1) && this.pointEquals(line1.p1, line2.p2));
			return isSameLine || isFlippedLine;
		}
		return false;
	},

	/**
	 * Summary: Checks if point is with in the space with ray casting
	 * Parameters: space: Space object, use walls (list of Line objects)
					point: Point object (x, y coordinate)
					width: Number, bounding width of space
					includeLine: bool, weither the point can be on the walls
	 * Returns: bool
	**/
	pointInSpace : function(space, point, width, includeLine) {
		//If point is very close to a line, then it's only in the space if we should include the walls' lines.
		if (this.exists(this.pointOnLines(space.walls, point, 0.5))) {
			return (includeLine === true);
		}
		else {
			var inShapeSegments = [];
			var lastLineIntersected = null;
			var currP1 = null;
			for(var xr = 0; xr < width; xr++) {
				var currRayPt = {x: xr, y: point.y};
				var intersectLine = this.pointOnLines(space.walls, currRayPt, 0.5);
				if(this.exists(intersectLine)) {
					if(!this.lineEquals(intersectLine, lastLineIntersected)) {
						lastLineIntersected = intersectLine;
						if(this.exists(currP1)) {
							//deep copy
							var inShapeSegment = {p1: {x: currP1.x, y: currP1.y}, p2: {x: currRayPt.x, y: currRayPt.y}}
							inShapeSegments.push(inShapeSegment);
							if(this.pointNearLine(inShapeSegment, point, 0.5)) {
								return true;
							}
							currP1 = null;
						}
						else {
							currP1 = {x:currRayPt.x, y:currRayPt.y};
						}
					}
				}
			}
			
			return false;
		}
	},
	
	/**
	 * Summary: Computes the fill value for the point based of space's id.
	 * Parameters: spaces: List of Space objects
					point: Point object (x, y coordinate)
					width: Number, bounding width for spaces
					emptyVal: String, fill value if point is not in any spaces
	 * Returns: bool
	**/
	findFillVal : function(spaces, point, width, emptyVal) {
		for(var s = 0; s < spaces.length; s++) {
			if(this.pointInSpace(spaces[s], point, width, true)) {
				return ""+s;
			}
		}
		return emptyVal;
	},

	/**
	 * Summary: Generate a sector representation (2d string array)
	 * Parameters: spaces: List of Space objects
					width: Number, bounding width to fill
					height: Number, bounding height to fill
	 * Returns: 2d string array
	**/
	generateSector : function(spaces, width, height) {
		var sector = [];
		var emptyVal = '-1';
		//x = col, y = row
		
		for(var row = 0; row < height; row++) {
			var sectorRow = [];
			for(var col = 0; col < width; col++) {
				sectorRow.push(this.findFillVal(spaces, {x:col, y:row}, width, emptyVal));
			}
			sector.push(sectorRow);
			console.log("complete with row: "+row);
		}
		
		return sector;
	},
	
	/**
	 * Summary: Generate a sector representation (String)
	 * Parameters: spaces: List of Space objects
					width: Number, bounding width to fill
					height: Number, bounding height to fill
	 * Returns: String
	**/
	generateSectorStr : function(spaces, width, height) {
		console.log("generating sector str...");
		var sector = this.generateSector(spaces, width, height);
		var sectorStr = ""
		for(var y = 0; y < height; y++) {
			for(var x = 0; x < width; x++) {
				sectorStr += sector[y][x]+" ";
			}
			sectorStr += "\n";
		}
		
		return sectorStr;
	}
};

module.exports = Sector;