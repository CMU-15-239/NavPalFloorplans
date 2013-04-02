//landmark.js

function Landmark(name, description, location) {
   this.name = name; //string
   this.description = description; //string
   this.location = location; //point
}

Landmark.prototype.toOutput = function() {
   return {
      name: this.name,
      description: this.description,
      location: this.location.toOutput()
   };
};