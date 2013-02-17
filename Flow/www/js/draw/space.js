//space.js
//Object for categorizing an open space (rooms and hallways)

//create once you have an enclosed space and have all the data for it
//px and py be the pixel point where the user clicks to classify the room
//and this is probably needed for graphNode
function Space(walls) {
	//this.anchor = {x: px, y: py};
	this.doors = [];
	this.walls = walls;
	this.points = [];
	this.type = ""; //"room" or "hallway"
	this.label = ""; //room number
	this.isClosed = false;
	//A polygon that appears when the user selects the space.
	//this.selectPoly = new Polygon(this.walls);
	this.drawPoly = false;
}

Space.prototype.isRoom = function() {
	return this.type === "room";
};

Space.prototype.isHallway = function() {
	return this.type === "hallway";
};

/**
 * Summary: Add a line to the array of walls, which acts as a new wall.
 * Paremters: l: The line to add.
 * Returns: undefined
**/
Space.prototype.addWall = function(l) {
	this.walls.push(l);
}

Space.prototype.addPoint = function(p) {
	this.points.push(p);
}

Space.prototype.draw = function() {
	if (this.drawPoly) this.selectPoly.draw();
}




