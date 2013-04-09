//edgeWeight.js

function EdgeWeight(node1Id, node2Id, weight) {
  if(node1Id < node2Id) {
    this.node1Id = node1Id;
    this.node2Id = node2Id;
  } else {
    this.node1Id = node2Id;
    this.node2Id = node1Id;
  }
  
  this.weight = weight;
}

EdgeWeight.prototype.equals = function(edgeWeight) {
  return util.exists(edgeWeight) && this.equalsById(edgeWeight.nodeId1, edgeWeight.nodeId2);
};

EdgeWeight.prototype.equalsById = function(node1Id, node2Id) {
  return (this.node1Id === node1Id && this.node2Id === node2Id)
        || (this.node1Id === node2Id && this.node2Id === node1Id);
};

EdgeWeight.prototype.toOutput = function() {
  return {
    node1Id: this.node1Id,
    node2Id: this.node2Id,
    weight: this.weight
  };
};