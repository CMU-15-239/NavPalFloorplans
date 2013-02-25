//graphNode.js
//a node in the graph structure

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

GraphNode.prototype.test = function() {
	console.log("in graphNode");
};