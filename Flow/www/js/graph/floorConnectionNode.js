// FloorConnectionNode.js

function importFloorConnectionNode(simpleFloorConnectionNode) {
  if(util.exists(simpleFloorConnectionNode)) {
    var floorConnectionNode = new FloorConnectionNode(simpleFloorConnectionNode.type,
                    simpleFloorConnectionNode.floorConnectionType, 
                    simpleFloorConnectionNode.edges,
                    importLine(simpleFloorConnectionNode.lineRep, true));
    floorConnectionNode.id = simpleFloorConnectionNode.id;
    return floorConnectionNode;
  }
  
  return null;
}

/**
 * Summary: Constructor for the FloorConnectionNode object.
 * Parameters: type: String, type of floor node (e.g. space, psw, floorConnection)
        floorConnectionType: String, type of floor connection (e.g. stairs, elevator, etc...)
				label: String
        edges: List of Strings (GraphNodes ids)
				pointRep: Point object, 2d point representation of floor connection
 * Returns: undefined
**/
function FloorConnectionNode(type, floorConnectionType, label, edges, pointRep) {
  FloorConnection.call(this, label, pointRep, floorConnectionType);
	FloorNode.call(this, type, edges, type);
}

FloorConnectionNode.prototype = new FloorConnection();
FloorConnectionNode.prototype = new FloorNode();
FloorConnectionNode.prototype.constructor = FloorConnectionNode;

/**
 * Summary: Converts the FloorConnectionNode object to a simple JSON object (for export)
 * Parameters: undefined
 * Returns: Simple JSON object.
**/
FloorConnectionNode.prototype.toOutput = function() {
	return {
		type: this.type,
		edges: this.edges,
		id: this.id,
    label: this.label,
		pointRep: this.pointRep.toOutput(),
		floorConnectionType: this.floorConnectionType
	};
};

FloorConnectionNode.prototype.equals = function(otherFloorConnectionNode) {
  if(util.exists(otherFloorConnectionNode) && otherFloorConnectionNode.id === this.id
      && otherFloorConnectionNode.type === this.type && util.exists(otherFloorConnectionNode.edges)
      && this.edges.length === otherFloorConnectionNode.edges.length
      && this.floorConnectionType === otherFloorConnectionNode.floorConnectionType
      && this.lineRep.equals(otherFloorConnectionNode.lineRep)) {
    
    for(var e = 0; e < this.edges.length; e++) {
      if(this.edges[e] !== otherFloorConnectionNode.edges[e]) {
        return false;
      }
    }
    
    return true;
  }
  
  return false;
};

