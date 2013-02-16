//spaceNode.js

function SpaceNode(spaceType, label, edges, walls) {
	this.spaceType = spaceType; //room vs hallway
	this.label = label;
	
	if(util.exists(walls)) {this.walls = walls;}
	else {this.walls = [];}
	
	GraphNode.call(this, "space", edges, "space");
}

SpaceNode.prototype = new GraphNode();
SpaceNode.prototype.constructor = SpaceNode;

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


