var mongoose = require('mongoose');
var User = require('./userModel.js');
var Util = require('../util.js');

var ImageSchema = new mongoose.Schema({
	_creatorId: String,
   imageId: String,
   imageStr: String,
   dataURL: String
});

/**
 * Summary: Extract the data to output. Removes sensitive data (e.g. this._id).
 * Parameters: undefined
 * Returns: {imageId : String, image: String}
**/
ImageSchema.methods.toOutput = function() {
   return {
      imageId: this.imageId,
      imageStr: this.imageStr,
      dataURL: this.dataURL
   };
};

module.exports = mongoose.model('Image', ImageSchema);