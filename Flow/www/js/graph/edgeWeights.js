//edgeWeights.js

function EdgeWeights() {
  this.edges = [];
};

EdgeWeights.prototype.indexOf = function(node1Id, node2Id) {
  for(var e = 0; e < this.edges.length; e++) {
    var edgeWeight = this.edges[e];
    if(edgeWeight.equalsById(node1Id, node2Id)) {
      return e;
    }
  }
  
  return -1
};

EdgeWeights.prototype.add = function(node1Id, node2Id, edgeWeight) {
  var idx = this.indexOf(node1Id, node2Id);
  if(idx === -1) {
    this.edges.push(new EdgeWeight(node1Id, node2Id, edgeWeight));
  }
  
  return edgeWeight;
};

EdgeWeights.prototype.getWeight = function(node1Id, node2Id) {
  var idx = this.indexOf(node1Id, node2Id);
  if(idx !== -1) {
    return this.edges[idx].weight;
  }
  
  return -1;
};

EdgeWeights.prototype.toOutput = function() {
  var outEdgeWeights = [];
  for(var e = 0; e < this.edges.length; e++) {
    outEdgeWeights.push(this.edges[e].toOutput());
  };
  
  return outEdgeWeights;
};