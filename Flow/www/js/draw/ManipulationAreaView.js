/**
	ManipulationAreaView.js
	Written by Paul Davis
	pjbdavis@gmail.com
	Spring 2013
	
	This class converts between the real-world coordinates of an image,
	as defined by the original image, and the user-perspective world, which
	are relative to the canvas.
	
	It takes into account the scale and offset for zooming and panning
	
*/

function ManipulationAreaView(htmlX, htmlY, scaleStep) {
	// Represents the coordinates of the canvas object on the HTML page
	this.htmlX = htmlX;
	this.htmlY = htmlY;
	
	this.scaleStep = scaleStep;
	
	// Offsets from the real-world (0,0)
	// Represents a vector from real-world (0,0) to canvas world (0,0)
	this.offsetX = 0;
	this.offsetY = 0;
	
	// scale is ratio-  real-world : canvas world
	this.scale = 1;
	this.maxScale = 16;
	this.minScale = 1.0/16;
}

/**
 * Summary: Converts canvas coordinates to real-world coordinates
 * Parameters: p: The point in the canvas world
 * Returns: The point as a real-world coordinate
**/
ManipulationAreaView.prototype.toRealWorld = function(p) {
	x = p.x;
	y = p.y;
	point = new Point((x / this.scale) + this.offsetX, 
					  (y / this.scale) + this.offsetY);
	return point;
}

/**
 * Summary: Converts real-world coordinates to canvas coordinates
 * Parameters: p: The point in the real-world world
 * Returns: The point as canvas world coordinates
**/
ManipulationAreaView.prototype.toCanvasWorld = function(p) {
	point = new Point((p.x - this.offsetX) * this.scale,
					  (p.y - this.offsetY) * this.scale);
	return point;
}

/**
 * Summary: Modifies the scale to zoom in or out
 * Parameters: zoomIn: true if zooming in, false if zooming out
			   scaleStep: the factor to zoom in
 * Returns: true if zoom was successful, and false if already at zoom bounds
**/
ManipulationAreaView.prototype.zoom = function(isZoomIn) {
	// Zoom in
	if (isZoomIn) {
		this.scale *= this.scaleStep;
		if (this.scale > this.maxScale) {
			this.scale = this.maxScale;
			return false;
		}
	}
	
	// Zoom out
	else {
		this.scale /= this.scaleStep;
		if (this.scale < this.minScale) {
			this.scale = this.minScale;
			return false;
		}
	}
	
	return true;
}

/**
 * Summary: Modifies the scale to zoom in or out, keeping a specific point
			in the same place.
 * Parameters: 	zoomIn: true if zooming in, false if zooming out
				point: the point to remain pinned, while zooming
					   the point is in CANVAS WOLRD coordinates!
 * Returns: true if zoom was successful, and false if already at zoom bounds
**/
ManipulationAreaView.prototype.zoomCanvasPoint = function(isZoomIn, point) {
	// Apply a zoom
	zoom = this.zoom(isZoomIn);
	
	// Modify the offset so it remains in the same place
	if (zoom) {
		dx = point.x - this.offsetX;
		dy = point.y - this.offsetY;
		if (isZoomIn) {
			dx /= this.scaleStep;
			dy /= this.scaleStep;
		}
		else {
			dx *= -1.0 * this.scaleStep;
			dy *= -1.0 * this.scaleStep;
		}
		this.offsetX += dx;
		this.offsetY += dy;
	}
	
	return zoom;
}

// TODO: write zoom canvas point CENTER

/**
 * Summary: Changes the offset of the manipulation area
 * Parameters: dx: the number of pixels to pan in the x direction
			   dy: the number of pixels to pan in the y direction
 * Returns: undefined
**/
ManipulationAreaView.prototype.pan = function(dx, dy) {
	this.offsetX += dx / this.scale;
	this.offsetY += dy / this.scale;
}