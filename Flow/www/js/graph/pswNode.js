// PswNode.js

/**
 * Summary: Constructor for the PswNode object.
 * Parameters: pswType: String, type of passageway (door, sliding, opening, etc...)
				edges: List of Strings (GraphNodes ids)
				lineRep: Line object, 2d line represenation of passageway
 * Returns: undefined
**/
function PswNode(pswType, edges, lineRep) {
	this.pswType = pswType;
	this.lineRep = lineRep;

	GraphNode.call(this, "psw", edges, "psw");
}

PswNode.prototype = new GraphNode();
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