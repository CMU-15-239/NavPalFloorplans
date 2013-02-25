var wrapper = $('<div/>').css({height:0,width:0,'overflow':'hidden'});
var fileInput = $(':file').wrap(wrapper);
var THUMBWIDTH = 280.0;
var THUMBHEIGHT = 240.0;
var clickedAway = true
var isVisible = false

// options for boostrap popover a.k.a hoverzoom
function popoverOptions(imgSrc, width, height) { 
	return {
		html: true,
		animation: false,
		placement: function (context, source) {
		    var position = $(source).position();
		    if (position.left > 515) {
		        return "left";
		    }
		    return "right";
		},
	  trigger: 'hover',
	  content: function (width, height) {
	    return $('<img class="hoverzoom" src="'+ imgSrc + '" />').height(height*2 + 'px').width(width*2 + 'px');
	  }
	}
}

// template for dyanmicly added floorplan from upload
function labelTemplate(fileName) {
	return $(
		'<form class="form-horizontal">\
			<div class="control-group">\
				<label class="control-label" for="fileName">File Name:</label>\
				<div class="controls">\
					<p style="vertical-align:middle">'+ fileName +'</p>\
				</div>\
			</div>\
			<div class="control-group">\
				<label class="control-label" for="floorLabel">Floor Number:</label>\
				<div class="controls">\
					<input type="text" id="inputPassword" placeholder="###">\
				</div>\
			</div>\
		</form>');
}

// return jquery object of floorplan with correct dimensions
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

// creates a thumbnail for a given floorplan file
function createThumb(file) {
	var reader = new FileReader();
    reader.onload = function(event){
        var floorPlanImg = new Image();
        floorPlanImg.onload = function() {

        	var li = $('<li></li>').addClass('span4')
        	var thumb = $('<div></div>').addClass('thumbnail');
        	var holder = $('<div></div>').addClass('holder');
        	var caption = $('<div></div>').addClass('caption');
        	var label = labelTemplate(file.name);
        	var floorPlan = formatFloorPlan(floorPlanImg);

        	holder.append(floorPlan);
        	caption.append(label);
        	thumb.append(holder);
        	thumb.append(caption);
            li.append(thumb);

            $('#gallary').append(li);
        }
        floorPlanImg.src = event.target.result;
    }
    reader.readAsDataURL(file);
}

// html hack that allows styling of upload button #genius
$('#file').click(function(){
    fileInput.click();
}).show();

// goes through all selected files and creates thumbnails
fileInput.change( function(e){
	var files = e.target.files
    for (var i=0; i < files.length; i++) {
    	createThumb(files[i]);
    }
});