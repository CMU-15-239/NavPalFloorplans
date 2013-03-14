//graphNode.js
//a node in the graph structure

function importGraphNode(simpleGraphNode) {
  if(util.exists(simpleGraphNode)) {
    var graphNode = new GraphNode(simpleGraphNode.type, simpleGraphNode.edges, simpleGraphNode.type);
    graphNode.id = simpleGraphNode.id;
    return graphNode;
  }
  
  return null;
}

/**
 * Summary: Constructor for the GraphNode object.
 * Parameters: type: String, type of node (space vs psw)
				edges: List of Strings (GraphNodes ids)
				idPrefix: String, prefix for id
 * Returns: undefined
**/
function GraphNode(type, edges, idPrefix) {
	this.type = type;
	if(util.exists(edges)) {this.edges = edges;}
	else {this.edges = [];}
	this.id;
	this.newId(idPrefix);
}

/**
 * Summary: Converts the GraphNode object to a simple JSON object (for export)
 * Parameters: undefined
 * Returns: Simple JSON object.
**/
GraphNode.prototype.toOutput = function() {
	return {
		type: this.type,
		edges: this.edges,
		id: this.id
	};
};

/**
 * Summary: Creates a GraphNode object based of the prefix and the graph data
 * Parameters: idPrefix: String
 * Returns: undefined
**/
GraphNode.prototype.newId = function(idPrefix) {
	this.id = idPrefix+"_"+JSON.stringify(this).hashCode();
};

GraphNode.prototype.equals = function(otherGraphNode) {
  if(util.exists(otherGraphNode) && otherGraphNode.id === this.id
      && otherGraphNode.type === this.type && util.exists(otherGraphNode.edges)
      && this.edges.length === otherGraphNode.edges.length) {
    
    for(var e = 0; e < this.edges.length; e++) {
      if(this.edges[e] !== otherGraphNode.edges[e]) {
        return false;
      }
    }
    
    return true;
  }
  
  return false;
};
