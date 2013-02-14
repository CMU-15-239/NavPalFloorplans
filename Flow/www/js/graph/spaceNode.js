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

