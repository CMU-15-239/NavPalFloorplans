//floorNode.js
//a node in the floor structure

function importFloorNode(simpleFloorNode) {
  if(util.exists(simpleFloorNode)) {
    var floorNode = new FloorNode(simpleFloorNode.type, simpleFloorNode.edges, simpleFloorNode.type);
    floorNode.id = simpleFloorNode.id;
    return floorNode;
  }
  
  return null;
}

/**
 * Summary: Constructor for the FloorNode object.
 * Parameters: type: String, type of node (space vs psw)
				edges: List of Strings (FloorNodes ids)
				idPrefix: String, prefix for id
 * Returns: undefined
**/
function FloorNode(type, edges, idPrefix) {
	this.type = type;
	if(util.exists(edges)) {this.edges = edges;}
	else {this.edges = [];}
	this.id;
	this.newId(idPrefix);
}

/**
 * Summary: Converts the FloorNode object to a simple JSON object (for export)
 * Parameters: undefined
 * Returns: Simple JSON object.
**/
FloorNode.prototype.toOutput = function() {
	return {
		type: this.type,
		edges: this.edges,
		id: this.id
	};
};

/**
 * Summary: Creates a FloorNode object based of the prefix and the floor data
 * Parameters: idPrefix: String
 * Returns: undefined
**/
FloorNode.prototype.newId = function(idPrefix) {
	this.id = idPrefix+"_"+JSON.stringify(this).hashCode();
};

FloorNode.prototype.equals = function(otherFloorNode) {
  if(util.exists(otherFloorNode) && otherFloorNode.id === this.id
      && otherFloorNode.type === this.type && util.exists(otherFloorNode.edges)
      && this.edges.length === otherFloorNode.edges.length) {
    
    for(var e = 0; e < this.edges.length; e++) {
      if(this.edges[e] !== otherFloorNode.edges[e]) {
        return false;
      }
    }
    
    return true;
  }
  
  return false;
};
