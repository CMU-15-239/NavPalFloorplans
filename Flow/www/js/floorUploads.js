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
var POPOVERWIDTH = 400;
var POPOVERHEIGHT = 250;
var PROCESSEDFLOORS = [];

/**
 * Summary: hides origonal file input button because it is ugly
 * Parameters: n/a-
 * Returns: n/a
**/
var wrapper = $('<div/>').css({height:0,width:0,'overflow':'hidden'});
var fileInput = $(':file').wrap(wrapper);

/**
 * Summary: logic to place popover such that it never appears off screen
 * Parameters: element - element that popover is being applied to
 * Returns: n/a
**/


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
		placement: "top",
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
function labelTemplate(fileName, id) {
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
					<input class="span1 ' + id + '" type="text">\
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

/**
 * Summary: creates a hash for a string
 * Parameters: none
 * Returns: hash value
**/
String.prototype.hashCode = function() {
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
    reader.onload = function(event) {
        var floorPlanImg = new Image();
        floorPlanImg.onload = function() {
        	// construct list element
        	var li = $('<li></li>').addClass('span4').addClass('thumb-li');
        	var thumb = $('<div></div>').addClass('thumbnail');
        	var imgHolder = $('<div></div>').addClass('imgHolder').addClass(id);
        	var caption = $('<div></div>').addClass('caption');
        	// constructs form template for labeling
        	var label = labelTemplate(file.name, id);
        	// directly holds image
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

/**
 * Summary: goes through all selected files and creates thumbnail
 * Parameters: n/a
 * Returns: appends images into image gallary
**/
function processFiles(files) {
	for (var i=0; i < files.length; i++) {
		var file = files[i]
		var reader = new FileReader();
		// get unique id for spinner
		var id = file.name.hashCode();
		// start a loading spinner
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
					// save preprocessor data into array for future use
					PROCESSEDFLOORS.push([id, response])
					$("."+ id).removeClass('loading').spin(false);
				}.bind(this),
				error: function(response) {
					var id = this.name.hashCode();
					// stop spinner
					$("."+ id).removeClass('loading').spin(false);
				}.bind(this)
			})}.bind(file)
		reader.readAsDataURL(file);
	}
	$('#done').toggleClass('disabled');
}

/**
 * Summary: checks if files are valid, if so calls createThumb
 * Parameters: event - change event on file input button
 * Returns: n/a
**/
fileInput.change( 
	function(e) {
		var files = e.target.files
		$('#done').toggleClass('disabled');
	    for (var i=0; i < files.length; i++) {
	    	if (files[i].type === "image/jpeg" || files[i].type === "image/png") {
	    		createThumb(files[i]);
	    	}
	    }
	    setTimeout(function() { 
	    	processFiles(files); }, 100 * files.length);
});

/**
 * Summary: html hack that allows styling of upload button #genius
 			hides input button (ugly) and paris clicking of pretty button
 			to click event of old button 
 * Parameters: none
 * Returns: n/a
**/
$('#file').click(function(){
    fileInput.click();
}).show();

function getFloorLabels(processedFloors) {
	var building = []
	for (var i = 0; i < processedFloors.length; i++) {
		var floor = processedFloors[i];
		var id = floor[0];
		var data = floor[1];
		var label = $("input.span1."+id).val();
		data.label = label;
		building.push({'floor': data});
	};
	return building;
}

function hasDuplicates(array) {
    var valuesSoFar = {};
    for (var i = 0; i < array.length; ++i) {
        var value = array[i];
        if (Object.prototype.hasOwnProperty.call(valuesSoFar, value)) {
            return true;
        }
        valuesSoFar[value] = true;
    }
    return false;
}

$('#done').click(function() {
	var buildingName = $('#buildingNameInput').val();
	var labelInputs = $("input.span1");
	var labels = [];
	var valid = true;
	labelInputs.each(function(index){ 
		var label = $(this).val();
		if (label === "") valid = false;
		labels.push(label);
	})
	if (PROCESSEDFLOORS.length === 0) {
		alert('You must upload floorplans before pressing done.')
	}
	else if (buildingName === "") {
		alert('You must give your building a name before pressing done.')
	}
	else if (!valid) {
		alert('You must give your each of your floors a label.')
	}
	else if (hasDuplicates(labels)) {
		alert('Floor labels must be unique.')
	}
	else {
		var floors = getFloorLabels(PROCESSEDFLOORS);
		var building = constructBuildingFromPreprocess(buildingName, floors);
		// start a loading spinner to indicate processing
		$(this).spin('small').addClass('disabled');
		$.ajax({
			type: "POST",
			url: '/savePublish',
			data: {
				building: building,
				publishData: false
			},
			success: function(response) {
				//save in local storage and redirect
				localStorage.setItem('building', JSON.stringify(building));
				window.location = "/authoringTool.html";
			}.bind(this),
			error: function(response) {
				// remove loading spinner
				$('#done').spin(false).removeClass('disabled');
				//alert user to their error
				alert('An error occurred, please try again.');
			}.bind(this)
		})
	}
})

function constructBuildingFromPreprocess(buildingName, buildingData) {
	console.log("Building name: " + buildingName);
	window.bitch = buildingData;
}
