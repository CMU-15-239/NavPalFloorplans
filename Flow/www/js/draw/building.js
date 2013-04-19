//building.js

function importBuilding(simpleBuilding) {
  var newBuilding = null;
  if(util.exists(simpleBuilding)) {
    newBuilding = new Building(simpleBuilding.name);
    
    if(util.exists(simpleBuilding.floors)) {
      for(var f = 0; f < simpleBuilding.floors.length; f++) {
        newBuilding.floors.push(importFloor(simpleBuilding.floors[f]));
      }
    }
  }
  
  return newBuilding;
};

function Building(name) {
  this.name = name;
  this.floors = [];
}

Building.prototype.toOutput = function() {
   var outFloors = [];
   for(var f = 0; f < this.floors.length; f++) {
      outFloors.push(this.floors[f].toOutput());
   }
   
   return {
      name: this.name,
      floors: outFloors
   };
};