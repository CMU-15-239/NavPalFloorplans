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
	var labelMap = {};
	var labels = [];
	var floors = [];
	for (var i = 0; i < processedFloors.length; i++) {
		var floor = processedFloors[i];
		var id = floor[0];
		var data = floor[1];
		var label = $("input.span1."+id).val();
		data.label = label;
		labelMap[label] = data;
		labels.push(label);
	};
	console.log(labelMap);
	labels.alphanumSort(true);
	console.log(labels);
	for (var i = 0; i < labels.length; i++) {
		var floor = labelMap[labels[i]];
		floors.push({'floor': floor});
	};
	return floors;
}

/**
 * Summary: Checks if an array has duplicate items
 * Parameters: array
 * Returns: bool
**/
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

/**
 * Summary: Checks if user has filled out all required
 			fields in order to initialize building 
 * Parameters: none
 * Returns: alert if requirements not met,
 			else calls initializeBuilding
**/
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
	// if all requirements met 
	else initializeBuilding()
})

/**
 * Summary: Contructs building object, saves building to database
 			saves building to local storage then redirects
 			to authoringTool page
 * Parameters: none
 * Returns: redirect
**/
function initializeBuilding() {
	var floors = getFloorLabels(PROCESSEDFLOORS);
	var building = constructBuildingFromPreprocess(buildingName, floors);
	// start a loading spinner to indicate processing
	$(this).spin('small').addClass('disabled');
	$.ajax({
		type: "POST",
		url: '/savePublish',
		data: {
			building: {
				name: building.name,
				authoData: building.toOutput(),
				graph: null
			},
			publishData: false
		},
		success: function(response) {
			//save in local storage and redirect
			localStorage.setItem('building', JSON.stringify(this));
			window.location = "/authoringTool.html";
		}.bind(building),
		error: function(response) {
			// remove loading spinner and alert user of error
			$('#done').spin(false).removeClass('disabled');
			alert('An error occurred, please try again.');
		}.bind(this)
	})
}

function constructBuildingFromPreprocess(buildingName, buildingData) {
	var buildingObject = new Building(buildingName);
	for (var i = 0; i < buildingData.length; i++) {
		var curFloor = buildingData[i];
		var curFloorName = curFloor.floor.label;
		var curFloorImageID = curFloor.floor.imageId;
		var curFloorLines = curFloor.floor.lines;
		var floorObject = new Floor(curFloorName, curFloorImageID);
		for (var j = 0; j < curFloorLines.length; j++) {
			var curLine = curFloorLines[j];
			var p1 = curLine.line[0];
			p1 = new Point(p1.p1[1], p1.p1[0]);
			var p1Duplicate = floorObject.globals.duplicatePoint(p1);
			if (p1Duplicate !== null) {
				p1 = p1Duplicate;
			}
			var p2 = curLine.line[1];
			p2 = new Point(p2.p2[1], p2.p2[0]);
			var p2Duplicate = floorObject.globals.duplicatePoint(p2);
			if (p2Duplicate !== null) {
				p2 = p2Duplicate;
			}
			var newLine = new Line(p1, p2);
			floorObject.globals.addWall(newLine);
			floorObject.globals.addPoint(p1);
			floorObject.globals.addPoint(p2);
			
		}
		var curFloorText = curFloor.floor.text;
		for (var k = 0; k < curFloorText.length; k++) {
			var curText = curFloorText[k];
			var curValue = curText.value;
			var x = curText.point[1];
			var y = curText.point[0];
			floorObject.globals.preprocessedText.push({value: curValue, location: new Point(x,y)});
		}
		buildingObject.floors.push(floorObject);
	}
	return buildingObject;
}
