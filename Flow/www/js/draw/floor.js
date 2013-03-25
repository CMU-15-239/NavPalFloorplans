//floor.js

function Floor(name, imageId) {
  this.name = name;
  this.imageId = imageId;
  this.imageScale = 1.0;
  this.spaces = [];
  this.landmarks = [];
  this.obsticals = [];
}

Floor.prototype.calcScale = function(space, userSqFt) {
  this.imageScale = 1.0;
};