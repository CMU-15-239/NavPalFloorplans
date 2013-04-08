// PswNode.js

function importPswNode(simplePswNode) {
  if(util.exists(simplePswNode)) {
    var pswNode = new PswNode(simplePswNode.type, simplePswNode.pswType,
                    simplePswNode.edges, importLine(simplePswNode.lineRep, true));
    pswNode.id = simplePswNode.id;
    return pswNode;
  }
  
  return null;
}

/**
 * Summary: Constructor for the PswNode object.
 * Parameters: type: String, type of floor node (e.g. space, psw, floorConnection)
        pswType: String, type of passageway (door, sliding, opening, etc...)
				edges: List of Strings (GraphNodes ids)
				lineRep: Line object, 2d line represenation of passageway
 * Returns: undefined
**/
function PswNode(type, pswType, edges, lineRep) {
	this.pswType = pswType;
	this.lineRep = lineRep;

	FloorNode.call(this, type, edges, type);
}

PswNode.prototype = new FloorNode();
PswNode.prototype.constructor = PswNode;

/**
 * Summary: Converts the PswNode object to a simple JSON object (for export)
 * Parameters: undefined
 * Returns: Simple JSON object.
**/
PswNode.prototype.toOutput = function() {
	return {
		type: this.type,
		edges: this.edges,
		id: this.id,
		pswType: this.pswType,
		lineRep: this.lineRep.toOutput()
	};
};

PswNode.prototype.equals = function(otherPswNode) {
  if(util.exists(otherPswNode) && otherPswNode.id === this.id
      && otherPswNode.type === this.type && util.exists(otherPswNode.edges)
      && this.edges.length === otherPswNode.edges.length
      && this.pswType === otherPswNode.pswType
      && this.lineRep.equals(otherPswNode.lineRep)) {
    
    for(var e = 0; e < this.edges.length; e++) {
      if(this.edges[e] !== otherPswNode.edges[e]) {
        return false;
      }
    }
    
    return true;
  }
  
  return false;
};

