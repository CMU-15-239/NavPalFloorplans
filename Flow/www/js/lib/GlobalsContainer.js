var GlobalsContainer = function(canvas) {
	this.canvas = canvas;
	this.walls = [];
	//Points stored as real-world coordinates
	this.points = [];
	this.view = new ManipulationAreaView(this.canvas.x, this.canvas.y, 2);
	this.snapRadius = 15;
}

GlobalsContainer.prototype.drawPoints = function() {
	for (var i = 0; i < this.points.length; i++) {
		this.points[i].draw();
	}
}

GlobalsContainer.prototype.drawWalls = function() {
	for (var i = 0; i < this.walls.length; i++) {
		this.walls[i].draw();
	}
}

GlobalsContainer.prototype.addPoint = function(pointToAdd) {
	//Check to make sure that the point being added isn't a duplicate
	for (var i = 0; i < this.points.length; i++) {
		if (this.points[i].equals(pointToAdd)) return;
	}
	this.points.push(pointToAdd);
}

GlobalsContainer.prototype.addWall = function(wallToAdd) {
	//Check to make sure that the wall being added isn't a duplicate.
	for (var i = 0; i < this.walls.length; i++) {
		if (this.walls[i].equals(wallToAdd)) return;
	}
	this.walls.push(wallToAdd);
}