//graphNode.js
//a node in the graph structure

function GraphNode(type, edges, prefix) {
	this.type = type;
	if(util.exists(edges)) {this.edges = edges;}
	else {this.edges = [];}
	this.id;
	this.newId(prefix);
}

GraphNode.prototype.newId = function(prefix) {
	this.id = prefix+"_"+JSON.stringify(this).hashCode();
};

GraphNode.prototype.test = function() {
	console.log("in graphNode");
};