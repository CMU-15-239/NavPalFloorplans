//graphConstructor.js

function BuildingGraph(building) {
  this.name;
  this.id;
  this.floors = [];
  this.floorConnections = [];
  Building.call(this, building.name);
  
  for(var f = 0; f < building.floors.length; f++) {
    this.addFloor(building.floors[f]);
  }
};

BuildingGraph.prototype = new Building();
BuildingGraph.prototype.constructor = BuildingGraph;

BuildingGraph.prototype.addFloor = function(floor) {
  this.floors.push(new FloorGraph(floor));
  this.addFloorConnections(this.floors[f].floorConnections);
};

BuildingGraph.prototype.addFloorConnections = function(floorConnections) {
  for(var fc = 0; fc < this.floorConnections.length; fc++) {
    
  }
};
