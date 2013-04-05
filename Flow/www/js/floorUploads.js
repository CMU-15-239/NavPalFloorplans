
/**
 * Handles clicking events and creating of dynamic content for
   floor upload page
 * Written by: Daniel Muller
*/

/**
 * Summary: size of the thumbnails that we create
 * Parameters: n/a-
 * Returns: n/a
**/
var THUMBWIDTH = 280.0;
var THUMBHEIGHT = 200.0;

/**
 * Summary: hides origonal file input button because it is ugly
 * Parameters: n/a-
 * Returns: n/a
**/
var wrapper = $('<div/>').css({height:0,width:0,'overflow':'hidden'});
var fileInput = $(':file').wrap(wrapper);

/**
 * Summary: options for boostrap popover a.k.a hoverzoom
 			uses postion of image to decide where popup should appear
 * Parameters: imgSrc-location of image, width-image width, height-image height
 * Returns: html template of popover
**/

function popoverOptions(imgSrc, width, height) { 
	return {
		html: true,
		animation: false,
		placement: function(tip, element) {
		    var $element, above, actualHeight, actualWidth, below, boundBottom, boundLeft, boundRight, boundTop, elementAbove, elementBelow, elementLeft, elementRight, isWithinBounds, left, pos, right;
		    isWithinBounds = function(elementPosition) {
		      return boundTop < elementPosition.top && boundLeft < elementPosition.left && boundRight > (elementPosition.left + actualWidth) && boundBottom > (elementPosition.top + actualHeight);
		    };
		    $element = $(element);
		    pos = $.extend({}, $element.offset(), {
		      width: element.offsetWidth,
		      height: element.offsetHeight
		    });
		    actualWidth = 550;
		    actualHeight = 315;
		    boundTop = $(document).scrollTop();
		    boundLeft = $(document).scrollLeft();
		    boundRight = boundLeft + $(window).width();
		    boundBottom = boundTop + $(window).height();
		    elementAbove = {
		      top: pos.top - actualHeight,
		      left: pos.left + pos.width / 2 - actualWidth / 2
		    };
		    elementBelow = {
		      top: pos.top + pos.height,
		      left: pos.left + pos.width / 2 - actualWidth / 2
		    };
		    elementLeft = {
		      top: pos.top + pos.height / 2 - actualHeight / 2,
		      left: pos.left - actualWidth
		    };
		    elementRight = {
		      top: pos.top + pos.height / 2 - actualHeight / 2,
		      left: pos.left + pos.width
		    };
		    above = isWithinBounds(elementAbove);
		    below = isWithinBounds(elementBelow);
		    left = isWithinBounds(elementLeft);
		    right = isWithinBounds(elementRight);
		    if (above) return "top";
		    else if (below) return "bottom";
		    else if (left) return "left";
		    else if (right) return "right";
		    else return "right";
		},
	  trigger: 'hover',
	  content: function (width, height) {
	    return $('<img class="hoverzoom" src="'+ imgSrc + '" />').height(height*2 + 'px').width(width*2 + 'px');
	  }
	}
}


/**
 * Summary: template for dyanmicly added floorplan from upload
 * Parameters: fileName-name of uploaded floorplan 
 * Returns: html string of form feilds for a floorplan
**/
function labelTemplate(fileName) {
	return $(
		'<form class="form-horizontal well form-inline">\
			<div class="control-group">\
				<label class="control-label" for="fileName">File Name:</label>\
				<div class="controls">\
					<p>'+ fileName +'</p>\
				</div>\
			</div>\
			<div class="control-group">\
				<label class="control-label" for="floorLabel">Floor Number:</label>\
				<div class="controls">\
					<input class="span1" type="text">\
				</div>\
			</div>\
		</form>');
}


/**
 * Summary: return jquery object of floorplan with correct dimensions and centered
 			vertically within its container
 * Parameters: floorPlanImg-img object of uploaded floorplan
 * Returns: formatted image of floorplan
**/

function formatFloorPlan(floorPlanImg) {
	var widthRatio = THUMBWIDTH / floorPlanImg.width;
	var heightRatio = THUMBHEIGHT / floorPlanImg.height;
	var ratio = Math.min(widthRatio, heightRatio);
	var width = floorPlanImg.width * ratio;
	var height = floorPlanImg.height * ratio;
	var floorPlan = $(floorPlanImg).width(width + 'px').height(height + 'px')
	floorPlan.css('vertical-align', 'middle').addClass('floorPlan');
	
	floorPlan.popover(popoverOptions(floorPlanImg.src, width, height));

	return floorPlan
}

String.prototype.hashCode = function(){
    var hash = 0, i, char;
    if (this.length == 0) return hash;
    for (i = 0; i < this.length; i++) {
        char = this.charCodeAt(i);
        hash = ((hash<<5)-hash)+char;
        hash = hash & hash; // Convert to 32bit integer
    }
    return "h" + hash;
};

/**
 * Summary: creates a thumbnail for a given floorplan file
 * Parameters: file-uploaded image of floorplan
 * Returns: n/a, appends element directly to image gallary
**/

function createThumb(file) {
	var id = file.name.hashCode();
	var reader = new FileReader();
    reader.onload = function(event){
        var floorPlanImg = new Image();
        floorPlanImg.onload = function() {
        	var li = $('<li></li>').addClass('span4').addClass('thumb-li')
        	var thumb = $('<div></div>').addClass('thumbnail');
        	var imgHolder = $('<div></div>').addClass('imgHolder').addClass(id);
        	var caption = $('<div></div>').addClass('caption');
        	var label = labelTemplate(file.name);
        	var floorPlan = formatFloorPlan(floorPlanImg).addClass('loading').addClass(id);
        	imgHolder.append(floorPlan);
        	caption.append(label);
        	thumb.append(imgHolder);

        	thumb.append(caption);
            li.append(thumb);

            $('#gallary').append(li);
        }
        floorPlanImg.src = event.target.result;
    }
    reader.readAsDataURL(file);
}

function processFiles(files) {
	for (var i=0; i < files.length; i++) {
		var file = files[i]
		var reader = new FileReader();
		var id = file.name.hashCode();
		$('.imgHolder.'+id).spin();
	    reader.onload = function(event) {
			$.ajax({
				type: "POST",
				url: '/preprocess',
				async: true,
				data: {
					image: event.target.result,
				},
				success: function(response) {
					var id = this.name.hashCode();
					$("."+ id).removeClass('loading').spin(false);
				}.bind(this),
				error: function(response) {
					var id = this.name.hashCode();
					$("."+ id).removeClass('loading').spin(false);
				}.bind(this)
			})}.bind(file)
		reader.readAsDataURL(file);
	}
	$('#done').toggleClass('disabled');
}



/**
 * Summary: goes through all selected files and creates thumbnail
 * Parameters: n/a
 * Returns: appends images into image gallary
**/
fileInput.change( 
	function(e) {
		var files = e.target.files
		$('#done').toggleClass('disabled');
	    for (var i=0; i < files.length; i++) {
	    	createThumb(files[i]);
	    }
	    setTimeout(function() { 
	    	processFiles(files); }, 100 * files.length);
});

/**
 * Summary: html hack that allows styling of upload button #genius
 			hides input button (ugly) and paris clicking of pretty button
 			to click event of old button 
 * Parameters: none
 * Returns:
**/

$('#file').click(function(){
    fileInput.click();
}).show();
