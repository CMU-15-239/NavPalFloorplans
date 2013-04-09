//floorInterconnection.js

function FloorConnection(label, pointRep, floorConnectionType) {
   this.label = label; //String
   this.pointRep = pointRep; //Point
   this.floorConnectionType = floorConnectionType; //String
}

FloorConnection.prototype.toOutput = function() {
   return {
      label: this.label,
      floorConnectionType: this.floorConnectionType,
      pointRep: this.pointRep.toOutput()
   };
};