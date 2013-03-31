var mongoose = require('mongoose');
var User = require('./userModel.js');
var Util = require('../util.js');

var ImageSchema = new mongoose.Schema({
	_creatorId: String,
   imageId: String,
   image: String
});

/**
 * Summary: Extract the data to output. Removes sensitive data (e.g. this._id).
 * Parameters: undefined
 * Returns: {id : String, image: String}
**/
ImageSchema.methods.toOutput = function() {
   return {
      id: this.imageId,
      image: this.image
   };
};

module.exports = mongoose.model('Image', ImageSchema);