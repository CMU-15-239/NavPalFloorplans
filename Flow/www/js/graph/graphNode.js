//graphNode.js
//a node in the graph structure

function GraphNode(anchor, type, label, walls, doors) {
	this.anchor = anchor;
	this.type = type; //room, 
	this.label = label;
	
	if(Util.exists(walls)) {this.walls = walls;}
	else {this.walls = [];}
	
	if(Util.exists(doors)) {this.doors = doors;}
	else {this.doors = [];}
	
	this.id = this.newID();
}

GraphNode.prototype.getDoorIdx = function(door) {
	for(int d = 0; d < doors.length; d++) {
		if(doors[d].equal(door)) {
			return d;
		}
	}
	return -1;
};

GraphNode.prototype.getWallsIdx = function(wall) {
	for(int w = 0; d < walls.length; w++) {
		if(walls[w].equal(wall)) {
			return w;
		}
	}
	return -1;
};

GraphNode.prototype.newId = function() {
	return JSON.stringify(this).hashCode();
};

GraphNode.prototype.toOutput = function() {
	var outWalls = [];
	for(var w = 0; w < this.walls.length; w++) {
		outWalls.push(walls[w].toOutput());
	}
	
	for(var w = 0; w < this.links.length; w++) {
		links[w].position = links[w].position.toOutput();
	}
	
	return {
		anchor: this.anchor,
		type: this.type,
		lable: this.label,
		walls: this.walls,
		links: this.links,
		id: this.id
	};
}
