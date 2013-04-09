// FloorConnectionNodeRef.js

function importFloorConnectionNodeRef(simpleFloorConnectionNodeRef) {
  if(util.exists(simpleFloorConnectionNodeRef)) {
    var floorConnectionNodeRef = new FloorConnectionNodeRef(simpleFloorConnectionNodeRef.type,
                    simpleFloorConnectionNodeRef.floorConnectionType, 
                    simpleFloorConnectionNodeRef.edges,
                    importLine(simpleFloorConnectionNodeRef.lineRep, true));
    floorConnectionNodeRef.id = simpleFloorConnectionNodeRef.id;
    return floorConnectionNodeRef;
  }
  
  return null;
}

/**
 * Summary: Constructor for the FloorConnectionNodeRef object.
 * Parameters: type: String, type of floor node (e.g. space, psw, floorConnection)
        floorConnectionType: String, type of floor connection (e.g. stairs, elevator, etc...)
				label: String
        edges: List of Strings (FloorGraph ids)
 * Returns: undefined
**/
function FloorConnectionNodeRef(type, floorConnectionType, label, edges) {
  this.label = label; //String
  this.floorConnectionType = floorConnectionType; //String
   
	FloorNode.call(this, type, edges, type);
}

FloorConnectionNodeRef.prototype = new FloorNode();
FloorConnectionNodeRef.prototype.constructor = FloorConnectionNodeRef;

/**
 * Summary: Converts the FloorConnectionNodeRef object to a simple JSON object (for export)
 * Parameters: undefined
 * Returns: Simple JSON object.
**/
FloorConnectionNodeRef.prototype.toOutput = function() {
	return {
		type: this.type,
		edges: this.edges,
		id: this.id,
    label: this.label,
		floorConnectionType: this.floorConnectionType
	};
};

FloorConnectionNodeRef.prototype.equalsFloorConnection = function(floorConnectionObj) {
  return util.exists(floorConnectionObj)
      && floorConnectionObj.label === this.label
      && floorConnectionObj.floorConnectionType === this.floorConnectionType;
};

FloorConnectionNodeRef.prototype.equals = function(otherFloorConnectionNodeRef) {
  if(util.exists(otherFloorConnectionNodeRef) && otherFloorConnectionNodeRef.id === this.id
      && floorConnectionObj.label === this.label
      && otherFloorConnectionNodeRef.type === this.type && util.exists(otherFloorConnectionNodeRef.edges)
      && this.edges.length === otherFloorConnectionNodeRef.edges.length
      && this.floorConnectionType === otherFloorConnectionNodeRef.floorConnectionType
      && this.pointRep.equals(otherFloorConnectionNodeRef.pointRep)) {
    
    for(var e = 0; e < this.edges.length; e++) {
      if(this.edges[e] !== otherFloorConnectionNodeRef.edges[e]) {
        return false;
      }
    }
    
    return true;
  }
  
  return false;
};

