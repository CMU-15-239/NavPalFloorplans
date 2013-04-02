//graphConstructor.js

function BuildingGraph(building) {
  BuildingGraph.call(this, building.name);
  this.id = null;
  
  for(var f = 0; f < building.floors.length; f++) {
    this.floors[f] = new floorGraph(building.floors[f]);
    this.addFloorConnections(this.floors[f].floorConnections);
  }
  
};

BuildingGraph.prototype = new Building();
BuildingGraph.prototype.constructor = BuildingGraph;

BuildingGraph.prototype.addFloorConnections = function(floorConnections) {
  for(var fc = 0; fc < this.floorConnections.length; fc++) {
    
  }
};
