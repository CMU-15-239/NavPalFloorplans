//floor.js

function Floor(name, imageId) {
  this.name = name;
  this.imageId = imageId;
  this.imageScale = 1.0;
  
  //Space objects
  this.spaces = [];
  this.obsticals = [];
  
  this.landmarks = [];
  this.floorInterconnections = [];
}

Floor.prototype.calcScale = function(space, userSqFt) {
  this.imageScale = 1.0;
};