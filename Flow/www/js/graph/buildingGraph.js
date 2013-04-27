//graphConstructor.js
var graphGlobals = {
  edgeWeights: null
};

function BuildingGraph(building) {
  this.name = building.name;
  this.id;
  this.floors = [];
  this.floorConnectionRefs = [];
  this.edgeWeights = graphGlobals.edgeWeights = new EdgeWeights();
  
  
  this.typeforFloorConnectionNodeRef = "floorConnection";
  
  for(var f = 0; f < building.floors.length; f++) {
    //console.log("Adding floor " + f + " " + building.floors[f].name + "...");
    this.addFloor(building.floors[f]);
  }
  
  this.id = "buildingGraph_"+JSON.stringify(this).hashCode();
  //console.log("constructing building complete");
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
    //console.log("Adding floor connection: ");
    //console.log(floorConnection);
    
    var index = this.indexOfFloorConnectionRef(floorConnection);
    //console.log("sadf");
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

BuildingGraph.prototype.toOutput = function() {
  var outFloors = [];
  for(var f = 0; f < this.floors.length; f++) {
    outFloors.push(this.floors[f].toOutput());
  }
  
  var outFloorConnectionRefs = [];
  for(var fc = 0; fc < this.floorConnectionRefs.length; fc++) {
    outFloorConnectionRefs.push(this.floorConnectionRefs[fc].toOutput());
  }
  
  this.edgeWeights = $.extend(graphGlobals.edgeWeights, {});
  
  return {
    name: this.name,
    id: this.id,
    floors: outFloors,
    floorConnectionRefs: outFloorConnectionRefs,
    edgeWeights: this.edgeWeights.toOutput()
  }
};

