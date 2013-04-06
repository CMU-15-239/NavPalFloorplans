// LandmarkNode.js

function importLandmarkNode(simpleLandmarkNode) {
  if(util.exists(simpleLandmarkNode)) {
    var landmarkNode = new LandmarkNode(simpleLandmarkNode.type,
                    simpleLandmarkNode.landmarkType, 
                    simpleLandmarkNode.edges,
                    importLine(simpleLandmarkNode.lineRep, true));
    landmarkNode.id = simpleLandmarkNode.id;
    return landmarkNode;
  }
  
  return null;
}

/**
 * Summary: Constructor for the LandmarkNode object.
 * Parameters: type: String, type of floor node (e.g. space, psw, landmark)
        landmarkType: String, type of floor connection (e.g. stairs, elevator, etc...)
				label: String
        edges: List of Strings (GraphNodes ids)
				pointRep: Point object, 2d point representation of floor connection
 * Returns: undefined
**/
function LandmarkNode(type, label, description, edges, pointRep) {
  this.label = label; //string
  this.description = description; //string
  this.pointRep = pointRep; //point
  
	FloorNode.call(this, type, edges, type);
}

LandmarkNode.prototype = new FloorNode();
LandmarkNode.prototype.constructor = LandmarkNode;

/**
 * Summary: Converts the LandmarkNode object to a simple JSON object (for export)
 * Parameters: undefined
 * Returns: Simple JSON object.
**/
LandmarkNode.prototype.toOutput = function() {
	return {
		type: this.type,
		edges: this.edges,
		id: this.id,
    label: this.label,
		description: this.description,
		pointRep: this.pointRep.toOutput()
	};
};

LandmarkNode.prototype.equals = function(otherLandmarkNode) {
  if(util.exists(otherLandmarkNode) && otherLandmarkNode.id === this.id
      && otherLandmarkNode.type === this.type && util.exists(otherLandmarkNode.edges)
      && this.edges.length === otherLandmarkNode.edges.length
      && this.landmarkType === otherLandmarkNode.landmarkType
      && this.lineRep.equals(otherLandmarkNode.lineRep)) {
    
    for(var e = 0; e < this.edges.length; e++) {
      if(this.edges[e] !== otherLandmarkNode.edges[e]) {
        return false;
      }
    }
    
    return true;
  }
  
  return false;
};

