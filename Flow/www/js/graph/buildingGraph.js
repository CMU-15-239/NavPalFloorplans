//graphConstructor.js

function BuildingGraph(building) {
  this.name;
  this.id;
  this.floors = [];
  this.floorConnectionRefs = [];
  
  this.typeforFloorConnectionNodeRef = "floorConnection";
  
  for(var f = 0; f < building.floors.length; f++) {
    this.addFloor(building.floors[f]);
  }
};

BuildingGraph.prototype.addFloor = function(floor) {
  var floorGraph = new FloorGraph(floor);
  this.floors.push(floorGraph);
  if(util.exists(floor.floorConnections)) {
    for(var fc = 0; fc < floor.floorConnections.length; fc++) {
      var floorConnectionRef = this.addFloorConnectionRef(floor.floorConnections[fc], floorGraph.id);
      floorGraph.addFloorConnectionNode(floor.floorConnections[fc], floorConnectionRef.id);
    }
  }
};

BuildingGraph.prototype.addFloorConnectionRef = function(floorConnection, floorGraphId) {
  var floorConnectionRef;
  if(util.exists(floorConnection)) {
    var index = this.indexOfFloorConnectionRef(floorConnection);
    if(index != -1) {
      floorConnectionRef = this.floorConnectionRefs[index];
      floorConnectionRef.edges.push(floorGraphId);
    } else {
      //TODO: fix ugliness of this.floorConnectionType and floorConnection.floorConnectionType inconsistent code
      floorConnectionRef = new FloorConnectionNodeRef(this.typeforFloorConnectionNodeRef,
                                                floorConnection.floorConnectionType,
                                                floorConnection.label, [floorGraphId]);
      this.floorConnectionRefs.push(floorConnectionRef);
    }
  }
  
  return floorConnectionRef;
};

BuildingGraph.prototype.indexOfFloorConnectionRef = function(otherFloorConnection) {
  for(var fc = 0; fc < this.floorConnectionRefs.length; fc++) {
    if(this.floorConnectionRefs[fc].equalsFloorConnection(otherFloorConnection)) {
      return fc;
    }
  }
  
  return -1;
};

