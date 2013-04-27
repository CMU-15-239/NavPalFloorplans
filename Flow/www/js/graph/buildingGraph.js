/*
BuildingGraph.js
By Vansi Vallabhaneni
*/

/**
  * Temporary globals for constructing a graph.
**/
var graphGlobals = {
  edgeWeights: null
};

function BuildingGraph(building) {
  this.name = building.name;
  this.id;
  this.floors = [];
  this.floorConnectionRefs = [];
  this.edgeWeights = graphGlobals.edgeWeights = new EdgeWeights();
  
  
  this.typeforFloorConnectionNodeRef = "floorConnectionRef";
  
  for(var f = 0; f < building.floors.length; f++) {
    this.addFloor(building.floors[f]);
  }
  
  this.id = "buildingGraph_"+JSON.stringify(this).hashCode();
};

/**
  * Summary: Constructs a floor graph and floor connection nodes, adding it to this.
  * Parameter: floor: Floor
  * Returns: undefined
**/
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

/**
  * Summary: Adds or updates an existing floor connection reference with the floor graph id.
  * Parameter: floorConnection: FloorConnection,
                floorGraphId: String
  * Returns: undefined
**/
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

/**
  * Summary: Finds the index of the floor connection reference which corresponds to the given floor connection.
  * Parameter: otherFloorConnection: FloorConnection
  * Returns: int, -1 if none exists
**/
BuildingGraph.prototype.indexOfFloorConnectionRef = function(otherFloorConnection) {
  for(var fc = 0; fc < this.floorConnectionRefs.length; fc++) {
    if(this.floorConnectionRefs[fc].equalsFloorConnection(otherFloorConnection)) {
      return fc;
    }
  }
  
  return -1;
};

/**
  * Summary: Constructs a JSON object from this.
  * Parameters: void
  * Returns: Object
**/
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

