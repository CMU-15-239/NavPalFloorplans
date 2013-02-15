//graph.js

function Graph(spaces, callback, callbackVars) {
	this.spaceNodes = [];
	this.pswNodes = [];
	
	for(var s = 0; s < spaces.length; s++) {
		this.addGraphNode(spaces[s]);
	}
	
	if(util.exists(callback)) {callback.apply(callbackVars);}
}

Graph.prototype.getPswNodeByLine = function(line) {
	for(var p = 0; p < this.pswNodes.length; p++) {
		if(this.pswNodes[p].lineRep.equals(line)) {
			return this.pswNodes[p]
		}
	}
	return null;
};

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
