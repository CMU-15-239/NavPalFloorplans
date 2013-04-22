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


FloorConnection.STAIR = "STAIR";
FloorConnection.ELEVATOR = "ELEVATOR";

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

	imgSize = 30;
	
	if (this.floorConnectionType == FloorConnection.STAIR) {
		stateManager.currentFloor.globals.canvas.drawImage(stateManager.stairImage, 
		0, 0, stateManager.stairImage.width, stateManager.stairImage.height,
		canvasPoint.x - imgSize/2, canvasPoint.y - imgSize/2, imgSize, imgSize);
	}
	
	
}