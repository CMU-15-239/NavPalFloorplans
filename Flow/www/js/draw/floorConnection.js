//floorInterconnection.js

function importFloorConnection(simpleFloorConnection) {
  var newFloorConnection = null;
  if(util.exists(simpleFloorConnection)) {
    newFloorConnection = new FloorConnection(simpleFloorConnection.label, 
                            importPoint(simpleFloorConnection.pointRep),
                            simpleFloorConnection.floorConnectionType);
  }
  
  return newFloorConnection;
}

function FloorConnection(label, pointRep, floorConnectionType) {
   this.label = label; //String
   this.pointRep = pointRep; //Point
   this.floorConnectionType = floorConnectionType; //String
}

FloorConnection.prototype.toOutput = function() {
   return {
      label: this.label,
      floorConnectionType: this.floorConnectionType,
      pointRep: this.pointRep.toOutput()
   };
};

FloorConnection.prototype.draw = function() {
	if (this.pointRep === undefined) return;
	canvasPoint = stateManager.currentFloor.globals.view.toCanvasWorld(this.pointRep);

	
	width = 50
	height = 50
	GLOBALS.canvas.drawImage(stateManager.landmarkImage, 0, 0, stateManager.landmarkImage.width, stateManager.landmarkImage.height - 30,
	canvasPoint.x - width/2, canvasPoint.y - height, width, height);
}