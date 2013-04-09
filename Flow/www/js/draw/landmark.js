//landmark.js

function importLandmark(simpleLandmark) {
  var newLandmark = null;
  if(util.exists(simpleLandmark)) {
    newLandmark = new Landmark(simpleLandmark.label,
                            simpleLandmark.description,
                            importPoint(simpleLandmark.pointRep));
  }
  
  return newLandmark;
}

function Landmark(label, description, pointRep) {
   this.label = label; //string
   this.description = description; //string
   this.pointRep = pointRep; //point
}

Landmark.prototype.toOutput = function() {
   return {
      label: this.label,
      description: this.description,
      pointRep: this.pointRep.toOutput()
   };
};

Landmark.prototype.draw = function() {
	if (this.pointRep === undefined) return;
	stateManager.currentFloor.globals.canvas.fillStyle = "blue";
	stateManager.currentFloor.globals.canvas.beginPath();
    stateManager.currentFloor.globals.canvas.arc(this.pointRep.x, this.pointRep.y, 5, 0, 2*Math.PI, false);
    stateManager.currentFloor.globals.canvas.fill();
}