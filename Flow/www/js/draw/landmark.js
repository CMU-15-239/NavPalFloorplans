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
	GLOBALS.canvas.beginPath();
    GLOBALS.canvas.arc(this.pointRep.x, this.pointRep.y, 20, 0, 2*Math.PI, false);
    GLOBALS.canvas.fill();
}