//floorInterconnection.js

function FloorConnection(name, location) {
   this.name = name; //String
   this.location = location; //Point
}

FloorConnection.prototype.toOutput = function() {
   return {
      name: this.name,
      location: this.location.toOutput()
   };
};