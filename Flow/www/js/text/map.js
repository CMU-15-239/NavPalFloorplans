/**
 * Summary: Computes the scaled form of a line/wall given the points that defined the wall.
 *			This function is used when 
 **/
function calculateScaledForm(p1, p2, scale)
{
	if (p1 === undefined || p2 === undefined)
	{
		return;
	}

	// Scale the x-y values for each point
	var x1 = p1.x / scale;
	var y1 = p1.y / scale;
	var x2 = p2.x / scale;
	var y2 = p2.y / scale;

	// Put line in form ax + by + c = 0
	var a = y1 - y2;
	var b = x2 - x1;
	var c = x1 * (y2 - y1) - y1 * (x2 - x1);
	var distConst = Math.sqrt(a * a + b * b);

	var obj = new Object();
	obj.x1 =  x1;
	obj.y1 = y1;
	obj.x2 = x2;
	obj.y2 = y2;
	obj.a =  a;
	obj.b = b;
	obj.c = c;
	obj.distConst = distConst;
	 
	return obj;
}

/******************************************************************************/

/*
 * 
 * name: initializeMapArray
 * @param width - Width of the map to be created
 * 		  height - Height of the map to be created
 * @return An intialized map array with dimensions [width x height]
 * 
 */
function initializeMapArray(width, height, doorChar)
{
	var map = [];
	
	for (var row=0; row < height; row++)
	{
		mapRow = [];
		for (var col=0; col < width; col++)
		{
			mapRow.push(doorChar);
		}
		map.push(mapRow);
	}

	return map;
}

/******************************************************************************/

/**
 * Summary: Adds walls from Space object to map string.
 * Parameters: space: Space object.
				map: 2d array of strings
				wallChar: String, how to represent a wall in map
 * Returns: map (2d array of strings)
 * 
 * NOTE: Gary, I added try catch blocks to this function since it was accessing elements that were ot of bounds for the map array
 * NOTE: Gary, I also added functionality to skip the drawing of any lines if they are labeled as doors.
 * arguments
**/
function addScaledLines(space, map, wallChar, scaleFactor)
{
	// console.log(map);
	for (var w=0; w<space.walls.length; w++)
	{
		var wall = space.walls[w];
		
		// If a wall is labeled as a door, skip it and resume drawing the other lines
		if (wall.isDoor == true)
		{
			continue;
		}

		scaledValues = calculateScaledForm(wall.p1, wall.p2, scaleFactor);

		var x1 = scaledValues.x1;
		var x2 = scaledValues.x2;
		var y1 = scaledValues.y1;
		var y2 = scaledValues.y2;

		console.log("Line Normal (" + wall.p1.x + ", " + wall.p1.y +  ") to (" + wall.p2.x + ", " + wall.p2.y + ") Scaled (" + x1.toFixed(1)  + ", " + y1.toFixed(1) + ") to (" + x2.toFixed(1) + ", " + y2.toFixed(1) + ")");

		var minX = Math.floor(Math.min(x1, x2))
		var maxX = Math.ceil(Math.max(x1, x2))
		var minY = Math.floor(Math.min(y1, y2))
		var maxY = Math.ceil(Math.max(y1, y2))		// Put line in form y = mx + b
		var m = -scaledValues.a / scaledValues.b
		var b = -scaledValues.c / scaledValues.b
		for (var x=minX; x<maxX; x++)
		{
			var y = m*x + b;
			var epsilon = Math.abs(Math.round(y) - y);
			if (epsilon >= 0)
			{	
				try
				{
					map[Math.floor(y)][x] = wallChar;
					map[Math.ceil(y)][x] = wallChar;
				}
				catch(err)
				{
					console.log("ERROR: Element Written to location [" + Math.floor(y) + " or " + Math.ceil(y) + ", " + x + "] in map array.");
					console.log("Map array only has dimensions [" + map.length + " by " + map[0].length + "]");
					console.log(err);
				}
			}
			else
			{

				try
				{
					map[Math.round(y)][x] = wallChar;
				}
				catch(err)
				{
					console.log("ERROR: Element Written to location [" + Math.round(y) + ", " + x + "] in map array.");
					console.log("Map array only has dimensions [" + map.length + " by " + map[0].length + "]");
					console.log(err);
				}
			}
		}
		var m = -scaledValues.b / scaledValues.a;
		var b = -scaledValues.c / scaledValues.a;
		for (var y=minY; y<maxY; y++)
		{
			var x = m*y + b;
			var epsilon = Math.abs(Math.round(y) - y);
			if (epsilon >= 0)
			{
				try
				{
					map[y][Math.floor(x)] = wallChar;
					map[y][Math.ceil(x)] = wallChar;
				}
				catch(err)
				{
					console.log("ERROR: Element Written to location [" + y + ", " + Math.floor(x) + " or " + Math.ceil(x) + "] in map array.");
					console.log("Map array only has dimensions [" + map.length + " by " + map[0].length + "]");
					console.log(err);
				}
			}
			else
			{
				try
				{
					map[y][Math.round(x)] = wallChar;
				}
				catch(err)
				{
					console.log("ERROR: Element Written to location [" + y + ", " + Math.round(x) + "] in map array.");
					console.log("Map array only has dimensions [" + map.length + " by " + map[0].length + "]");
					console.log(err);
				}
			}
		}
	}
	return map;
}
/* END SCALED ADD LINES */

/******************************************************************************/

/**
 * Summary: Adds walls from Space object to map string.
 * Parameters: space: Space object.
				map: 2d array of strings
				wallChar: String, how to represent a wall in map
 * Returns: map (2d array of strings)
 * 
 * NOTE: Gary, I added try catch blocks to this function since it was accessing elements that were ot of bounds for the map array
 * NOTE: Gary, I also added functionality to skip the drawing of any lines if they are labeled as doors.
 * 
**/

function addLines(space, map, wallChar)
{
	for (var w=0; w<space.walls.length; w++)
	{
		var wall = space.walls[w];
		
		// If a wall is labeled as a door, skip it and resume drawing the other lines
		if (wall.isDoor == true)
		{
			continue;
		}

		var x1 = wall.p1.x
		var x2 = wall.p2.x;
		var y1 = wall.p1.y;
		var y2 = wall.p2.y;
		var minX = Math.floor(Math.min(x1, x2))
		var maxX = Math.ceil(Math.max(x1, x2))
		var minY = Math.floor(Math.min(y1, y2))
		var maxY = Math.ceil(Math.max(y1, y2))		// Put line in form y = mx + b
		var m = -wall.a / wall.b
		var b = -wall.c / wall.b
		for (var x=minX; x<maxX; x++)
		{
			var y = m*x + b;
			var epsilon = Math.abs(Math.round(y) - y);
			if (epsilon >= 0)
			{	
				try
				{
					map[Math.floor(y)][x] = wallChar;
					map[Math.ceil(y)][x] = wallChar;
				}
				catch(err)
				{
					console.log("ERROR: Element Written to location [" + Math.floor(y) + " or " + Math.ceil(y) + ", " + x + "] in map array.");
					console.log("Map array only has dimensions [" + map.length + " by " + map[0].length + "]");
					console.log(err);
				}
			}
			else
			{
				try
				{
					map[Math.round(y)][x] = wallChar;
				}
				catch(err)
				{
					console.log("ERROR: Element Written to location [" + Math.round(y) + ", " + x + "] in map array.");
					console.log("Map array only has dimensions [" + map.length + " by " + map[0].length + "]");
					console.log(err);
				}
			}
		}
		var m = -wall.b / wall.a;
		var b = -wall.c / wall.a;
		for (var y=minY; y<maxY; y++)
		{
			var x = m*y + b;
			var epsilon = Math.abs(Math.round(y) - y);
			if (epsilon >= 0)
			{
				try
				{
					map[y][Math.floor(x)] = wallChar;
					map[y][Math.ceil(x)] = wallChar;
				}
				catch(err)
				{
					console.log("ERROR: Element Written to location [" + y + ", " + Math.floor(x) + " or " + Math.ceil(x) + "] in map array.");
					console.log("Map array only has dimensions [" + map.length + " by " + map[0].length + "]");
					console.log(err);
				}
			}
			else
			{
				try
				{
					map[y][Math.round(x)] = wallChar;
				}
				catch(err)
				{
					console.log("ERROR: Element Written to location [" + y + ", " + Math.round(x) + "] in map array.");
					console.log("Map array only has dimensions [" + map.length + " by " + map[0].length + "]");
					console.log(err);
				}
			}
		}
	}
	return map;
}

/**
 * 
 * name: convertMapArrayToString
 * @param map - The 2D map array representing a floorplan
 * @return A string of the 2D map array floorplan
 * 
 * NOTE: This was added by Gary Giger as a helper method, which is used as part of the text file generation for the map.
 * NOTE: The output of this method is currently sent to the server and written to a text file.
 * 
 */

function convertMapArrayToString(map, width, height, scale, res)
{
	var mapOutputString = "";

	mapOutputString += height + " " + width + " " + scale + " " + res + "\n";

	for (var row=0; row<map.length; row++)
	{
		numberCols = map[row].length;

		for (var col=0; col<numberCols; col++)
		{
			var value = map[row][col];
			
			if (value != undefined)
			{
				mapOutputString += map[row][col] + " ";
			}
			else
			{
				console.log("Undefined value in array [" + width + "," + height + "] at location (" + col + "," + row + ")");
			}
		}
		mapOutputString += "\n";
	}

	return mapOutputString;
} 

/******************************************************************************/

/**
 * Summary: Constructs a map array (2d array of strings).
 * Parameters: spaces: List of Space objects
				width: Number
				height: Number
 * Returns: map (2d array of strings)
**/
function generateMap(spaces, width, height, generateScaledDownVersionOfMap, scaleFactor)
{
	var doorChar = '0';
	var wallChar = 'X';

	var maps = new Object();

	var map = initializeMapArray(width, height, doorChar);
	
	// Compute scaled values and remove any decimals.
	// This is already being computed in the authoring tool's publish method. What I need to do is refactor this
	// method so that the same method is used to generate the full size map and the scaled map and simply call it
	// twice, once for the full size map and respective parameters and one for the scaled parameters. I am
	// commiting th versions for now so they we have at least something working in the mean time.
	var scaledHeight = height/scaleFactor;
	var scaledWidth = width/scaleFactor;
	scaledHeight = Math.ceil(scaledHeight);
	scaledWidth = Math.ceil(scaledWidth);
			
	var scaledMap = initializeMapArray(scaledWidth, scaledHeight, doorChar);

	console.log("map::generateMap - Number of Spaces" + spaces.length);
	console.log();
	console.log("Originl Map Dimensions: Rows: " + map.length + " Cols: " + map[0].length);
	console.log("Scaled Map Dimensions: Rows: " + scaledMap.length + " Cols: " + scaledMap[0].length);
	console.log();
	console.log("Generating Full Size Map");

	for(var s = 0; s < spaces.length; s++)
	{
		addLines(spaces[s], map, wallChar);
	}

	maps.originalSizeMap = map;

	if (generateScaledDownVersionOfMap)
	{
		console.log("Generating Scaled Map");
		for(var s = 0; s < spaces.length; s++)
		{
			addScaledLines(spaces[s], scaledMap, wallChar, scaleFactor);
		}
		
		maps.scaledSizeMap   = scaledMap;
	}

	console.log("map::generateMap - Done!");

	return maps;
}
