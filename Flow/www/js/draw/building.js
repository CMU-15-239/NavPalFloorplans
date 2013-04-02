//building.js

function Building(name) {
  this.name = name;
  this.id;
  this.floors = [];
  this.floorConnections = [];
}

Building.prototype.toOutput = function() {
   var outFloors = [];
   for(var f = 0; f < this.floors.length; f++) {
      outFloors.push(this.floor[f].toOutput());
   }
   
   var outFloorConnections = [];
   for(var f = 0; f < this.floorConnections.length; f++) {
      outFloorConnections.push(this.floorConnections[f].toOutput());
   }
   
   return {
      name: this.name,
      id: this.id,
      floors: outFloors,
      floorConnections: outFloorConnections
   };
};