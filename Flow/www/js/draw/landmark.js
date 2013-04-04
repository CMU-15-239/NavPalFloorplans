//landmark.js

function Landmark(name, description, pointRep) {
   this.name = name; //string
   this.description = description; //string
   this.pointRep = pointRep; //point
}

Landmark.prototype.toOutput = function() {
   return {
      name: this.name,
      description: this.description,
      pointRep: this.pointRep.toOutput()
   };
};