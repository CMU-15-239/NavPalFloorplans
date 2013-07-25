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
	// console.log(map);
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

function convertMapArrayToString(map)
{
	var mapOutputString = "";

	for (var row=0; row<map.length; row++)
	{
		numberCols = map[row].length;

		for (var col=0; col<numberCols; col++)
		{
			mapOutputString += map[row][col];
		}
		mapOutputString += "\n";
	}

	return mapOutputString;
} 

/**
 * Summary: Constructs a map array (2d array of strings).
 * Parameters: spaces: List of Space objects
				width: Number
				height: Number
 * Returns: map (2d array of strings)
**/
function generateMap(spaces, width, height)
{
	var map = [];
	var doorChar = '0';
	var wallChar = 'X'; 
	for (var row=0; row < height; row++)
	{
		mapRow = [];
		for (var col=0; col < width; col++)
		{
			mapRow.push(doorChar);
		}
		map.push(mapRow);
	}

	console.log("map::generateMap - Number of Spaces" + spaces.length);
	console.log("map dimensions: Rows: " + map.length + " Cols: " + map[0].length);

	for(var s = 0; s < spaces.length; s++)
	{
		addLines(spaces[s], map, wallChar);
	}

	return map;
}
