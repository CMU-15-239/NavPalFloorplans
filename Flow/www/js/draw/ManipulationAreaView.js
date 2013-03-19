/**
	ManipulationAreaView.js
	Written by Paul Davis
	pjbdavis@gmail.com
	Spring 2013
	
	This class converts between the real-world coordinates of an image,
	as defined by the original image, and the user-perspective world, which
	are relative to the canvas.
	
*/

function ManipulationAreaView(maxWidth, maxHeight, canvasX, canvasY) {
	this.maxWidth = maxWidth;
	this.maxHeight = maxHeight;
	this.canvasX = canvasX;
	this.canvasY = canvasY;
	
	// Offsets from the real-world (0,0)
	// Represents a vector from real-world (0,0) to canvas world (0,0)
	offsetX = 0;
	offsetY = 0;
	
	scale = 1;
	maxScale = 16;
	minScale = 1.0/16;
}

/**
 * Summary: Converts canvas coordinates to real-world coordinates
 * Parameters: x,y: The x and y coordinates in the canvas wolrd
 * Returns: [x',y'] : The x and y coordinates in the real-world, 
					  as the 0th and 1st elements in a list
**/
ManipulationAreaView.prototype.toRealWorld = function(p) {
	x = p.x;
	y = p.y;
	x -= this.canvasX;
	y -= this.canvasY;
	point = new Point((x * scale) + offsetX, 
					  (y * scale) + offsetY);
	return point;
}

/**
 * Summary: Converts real-world coordinates to canvas coordinates
 * Parameters: x,y: The x and y coordinates in the real-world world
 * Returns: [x',y'] : The x and y coordinates in the canvas world, 
					  as the 0th and 1st elements in a list
**/
ManipulationAreaView.prototype.toCanvasWorld = function(p) {
	point = new Point((p.x - offsetX) * scale + canvasX,
					  (p.y - offsetY) * scale + canvasY);
	return point;
}

/**
 * Summary: Modifies the scale to zoom in or out
 * Parameters: zoomIn: true if zooming in, false if zooming out
 * Returns: true if zoom was successful, and false if already at zoom bounds
**/
ManipulationAreaView.prototype.zoom = function(isZoomIn) {
	// Zoom in
	if (isZoomIn) {
		scale *= 2;
		if (scale > maxScale) {
			scale = maxScale;
			return false;
		}
	}
	
	// Zoom out
	else {
		scale /= 2;
		if (scale < minScale) {
			scale = minScale;
			return false;
		}
	}
	
	return true;
}

/**
 * Summary: Modifies the scale to zoom in or out
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
			dx /= 2;
			dy /= 2;
		}
		else {
			dx *= -2;
			dy *= -2;
		}
		this.offsetX += dx;
		this.offsetY += dy;
	}
	
	return zoom;
}

/**
 * Summary: Changes the offset of the manipulation area
 * Parameters: dx: the number of pixels to pan in the x direction
			   dy: the number of pixels to pan in the y direction
 * Returns: undefined
**/
ManipulationAreaView.prototype.pan = function(dx, dy) {
	this.offsetX += dx * this.scale;
	this.offsetY += dy * this.scale;
}