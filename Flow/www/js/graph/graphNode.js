//graphNode.js
//a node in the graph structure

function GraphNode(px, py, type, label, vertices, connections) {
	this.anchor = {x: px, y: py};
	this.type = type;
	this.label = label;
	this.vertices = [];
	this.connections = []; //refernces to other GraphNodes
	if(Util.exists(vertices)) {this.vertices = vertices;}
	if(Util.exists(connections)) {this.connections = connections;}
}

GraphNode.prototype.addVertice = function(px, py) {
	this.vertices.push({x: px, y: py});
};

GraphNode.prototype.addConnection = function(conn) {
	this.connection.push(conn);
};