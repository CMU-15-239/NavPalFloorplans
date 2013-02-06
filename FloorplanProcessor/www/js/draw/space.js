//space.js
//Object for categorizing an open space (rooms and hallways)

//create once you have an enclosed space and have all the data for it
//px and py be the pixel point where the user clicks to classify the room
//and this is probably needed for graphNode
function Space(px, py) {
	this.anchor = {x: px, y: py};
	this.doors = [];
	this.walls = [];
	this.type = ""; //"room" or "hallway"
	this.label = ""; //room number
}

Space.prototype.isRoom = function() {
	return this.classification === "room";
};

Space.prototype.isHallway = function() {
	return this.classification === "hallway";
};


