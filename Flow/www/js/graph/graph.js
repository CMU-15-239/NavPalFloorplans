//graph.js

/**
 * Summary: Constructor for the Graph object.
 * Parameters: spaces: The list of space objects created in the drawing tool,
				callback: Function to callback after the constructor executes. 
				(To notify something when the graph has been created (e.g load wheel))
				callbackVars: Inputs for callback.
 * Returns: undefined
**/
function Graph(spaces, callback, callbackVars) {
	this.spaceNodes = [];
	this.pswNodes = [];
	
	for(var s = 0; s < spaces.length; s++) {
		this.addGraphNode(spaces[s]);
	}
	
	if(util.exists(callback)) {callback.apply(callbackVars);}
}

/**
 * Summary: Converts the Graph object to a simple JSON object (for export)
 * Parameters: undefined
 * Returns: Simple JSON object.
**/
Graph.prototype.toOutput = function() {
	var outSpaceNodes = [];
	for(var s = 0; s < this.spaceNodes.length; s++) {
		outSpaceNodes.push(this.spaceNodes[s].toOutput());
	}
	
	var outPswNodes = [];
	for(var p = 0; p < this.pswNodes.length; p++) {
		outPswNodes.push(this.pswNodes[p].toOutput());
	}
	
	return {
		spaceNodes: outSpaceNodes,
		pswNodes: outPswNodes
	};
};

/**
 * Summary: Gets the PswNode with the 2d line represenation of line.
 * Parameters: line: Line object.
 * Returns: PswNode found or null if none found.
**/
Graph.prototype.getPswNodeByLine = function(line) {
	for(var p = 0; p < this.pswNodes.length; p++) {
		if(this.pswNodes[p].lineRep.equals(line)) {
			return this.pswNodes[p]
		}
	}
	return null;
};

/**
 * Summary: Adds a new GraphNode (SpaceNode) and establishes links to (and creates) PswNodes as necessary.
 * Parameters: space: Space object.
 * Returns: undefined
**/
Graph.prototype.addGraphNode = function(space) {
	//first check and add doors
	var psws = [];
	var pswIds = [];
	for(var d = 0; d < space.doors.length; d++) {
		var lineRep = space.doors[d];
		var existingDoor = this.getPswNodeByLine(lineRep);
		if(util.exists(existingDoor)) {
			psws.push(existingDoor);
			pswIds.push(existingDoor.id);
		}
		else {
			//TODO: check and make sure the newId function returns in time for adding to pswIds
			var newDoor = new PswNode("door", null, lineRep);
			psws.push(newDoor);
			pswIds.push(newDoor.id);
			this.pswNodes.push(newDoor);
		}
	}
	
	
	var spaceNode = new SpaceNode(space.type, space.label, pswIds, space.walls);
	//TODO: check and make sure the newId function returns in time for adding to psws
	for(var p = 0; p < psws.length; p++) {
		psws[p].edges.push(spaceNode.id);
	}
	this.spaceNodes.push(spaceNode);
};

/**
 * Summary: Gets a GraphNode from an id
 * Parameters: id: String.
 * Returns: undefined
**/
Graph.prototype.getGraphNodeById = function(id) {
	var searchArr = [];
	if(id.indexOf("psw_") === 0) {
		searchArr = this.pswNodes;
	}
	else if(id.indexOf("space_") === 0) {
		searchArr = this.spaceNodes;
	}
	
	for(var n = 0; n < searchArr.length; n++) {
		if(searchArr[n].id === id) {
			return searchArr[n];
		}
	}
	
	return null;
};
