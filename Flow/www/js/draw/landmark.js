//landmark.js

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