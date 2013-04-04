//floorInterconnection.js

function FloorConnection(label, pointRep, floorConnectionType) {
   this.label = label; //String
   this.pointRep = pointRep; //Point
   this.floorConnectionType = floorConnectionType; //String
}

FloorConnection.prototype.toOutput = function() {
   return {
      name: this.name,
      location: this.location.toOutput()
   };
};