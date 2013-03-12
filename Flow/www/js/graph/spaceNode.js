//spaceNode.js

/**
 * Summary: Constructor for the SpaceNode object.
 * Parameters: spaceType: String, type of space (room, hallway, etc...)
				label: String, label for room (e.g. room number)
				edges: List of Strings (GraphNodes ids)
				walls: List of Line objects, walls of SpaceNode (unordered)
 * Returns: undefined
**/
function SpaceNode(spaceType, label, edges, walls) {
	this.spaceType = spaceType;
	this.label = label;
	
	if(util.exists(walls)) {this.walls = walls;}
	else {this.walls = [];}
	
	GraphNode.call(this, "space", edges, "space");
}

SpaceNode.prototype = new GraphNode();
SpaceNode.prototype.constructor = SpaceNode;

/**
 * Summary: Converts the SpaceNode object to a simple JSON object (for export)
 * Parameters: undefined
 * Returns: Simple JSON object.
**/
SpaceNode.prototype.toOutput = function() {
	var outWalls = [];
	for(var w = 0; w < this.walls.length; w++) {
		outWalls.push(this.walls[w].toOutput());
	}
	
	return {
		type: this.type,
		edges: this.edges,
		id: this.id,
		spaceType: this.spaceType,
		label: this.label,
		walls: outWalls
	};
};


