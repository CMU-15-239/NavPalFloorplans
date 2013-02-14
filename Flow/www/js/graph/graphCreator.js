//graphCreator.js

function Graph(spaces) {
	this.graphNodes = [];
	
	for(int s = 0; s < spaces.length; s++) {
		var space = spaces[s];
		var graphNode = new GraphNode(space.anchor, space.type, space.label,
			space.walls); //TODO: walls may be references in space, need to check
		
		//find links
		for(int d = 0; d < graphNode.doors.length; d++) {
			var door = this.graphNode.doors[d];
			for(int g = 0; g < graphNodes.length; g++) {
				var otherGNode = this.graphNodes[g];
				var otherIdx = otherGNode.getDoorIdx(door);
				if(otherIdx != -1) {
					var link = {
						nodes: [otherGNode.id, graphNode.id],
						position: door;
						type: "door" //default
					};
					
					otherGNode.links.push(link);
					graphNode.links.push(link);
					otherGNode.doors.splice(otherIdx, 1);
				}
			}
		}
		
		this.graphNodes.push(graphNode);
	}
}