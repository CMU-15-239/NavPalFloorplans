var mongoose = require('mongoose');
var Util = require('../util.js');
var Image = require('../models/imageModel.js');

var ImageController = {
   /**
    * Summary: Creates and initializes a new Image.
    * Parameters: creatorId: String
                  image: String
    * Returns: undefined. Calls callback with created Image or null if it fails.
   **/   
	newImage: function(creatorId, imageStr, callback) {
		var IC = this; //the image controller
		var imageId = this.generateImageId();
		console.log("imageController line10: "+imageId);
    
		this.findOne({_creatorId: creatorId, imageId: imageId}, function(imageObj) {
			if(!Util.exists(imageObj)) {
            imageObj = new Image({
               _creatorId: creatorId,
               imageStr: imageStr,
               imageId: imageId
            });
				
            imageObj.save(function(err) {
               if(err) {
                  console.log("\n--imageController.js 32 ERR: "+err+"--\n");
                  if(Util.exists(callback)) {return callback(null);}
               }
               else if(Util.exists(callback)) {return callback(imageObj);}
            });
			}
			else {
				return IC.newImage(creatorId, imageStr, callback);
			}
		});
	},
   
   idCounter: 0,

   /**
    * Summary: Generates an id based off counter.
    * Parameters: undefined
    * Returns: String
   **/
	generateImageId: function() {
      var id = 'image_' + this.idCounter;
      this.idCounter++;
      return id;
	},
	
   /**
    * Summary: Find all Images based off searchJSON.
    * Parameters: searchJSON: obj
    * Returns: undefined. Calls callback with found Images or null if none found.
   **/
	find: function(searchJSON, callback) {
		Image.find(searchJSON, function(err, imageObjs) {
			if(err) {
				console.log("\n--imageController.js 61 ERR: "+err+"--\n");
				if(Util.exists(callback)) {return callback(null);}
			}
			else if(Util.exists(callback)) {return callback(imageObjs);}
		});
	},
	
   /**
    * Summary: Find one Image based off searchJSON.
    * Parameters: searchJSON: obj
    * Returns: undefined. Calls callback with found Image or null if none found.
   **/
	findOne: function(searchJSON, callback) {
		//console.log('ImageController.js line73 :'+JSON.stringify(searchJSON));
		Image.findOne(searchJSON, function(err, imageObj) {
			//console.log("BC findone: "+JSON.stringify(imageObj));
			if(err) {
				console.log("\n--imageController.js 72 ERR: "+err+"--\n");
				if(Util.exists(callback)) {return callback(null);}
			}
			else if(Util.exists(callback)) {return callback(imageObj);}
		});
	}
};

module.exports = ImageController;