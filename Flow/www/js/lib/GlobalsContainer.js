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
	if (!this.pointExists(wallToAdd.p1)) this.points.push(wallToAdd.p1);
	if (!this.pointExists(wallToAdd.p2)) this.points.push(wallToAdd.p2);
	this.walls.push(wallToAdd);
}

GlobalsContainer.prototype.pointExists = function(point) {
	for (var i = 0; i < this.points.length; i++) {
		var curPoint = this.points[i];
		if (curPoint.equals(point)) return true;
	}
	return false;
}

GlobalsContainer.prototype.removeWall = function(wallToRemove, shouldRemoveIsolatedPoints) {
	var index = this.walls.indexOf(wallToRemove);
	if (index >= 0) {
		if (shouldRemoveIsolatedPoints) {
			if (this.degree(wallToRemove.p1) === 1) this.removePoint(wallToRemove.p1);
			if (this.degree(wallToRemove.p2) === 1) this.removePoint(wallToRemove.p2);
		}
		this.walls.splice(index, 1);
	}
}

GlobalsContainer.prototype.removePoint = function(pointToRemove) {
	var index = this.points.indexOf(pointToRemove);
	if (index >= 0) this.points.splice(index, 1);
}

GlobalsContainer.prototype.degree = function(point) {
	var degree = 0;
	for (var i = 0; i < this.walls.length; i++) {
		var curWall = this.walls[i];
		if (curWall.p1.equals(point)) degree += 1;
		if (curWall.p2.equals(point)) degree += 1;
	}
	return degree;
}