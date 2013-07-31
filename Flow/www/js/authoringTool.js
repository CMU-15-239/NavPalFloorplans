/**
 * Initializes layout of authoring tool page
 * Written by: Daniel Muller
*/

/**
 * Summary: Connects preview pane to floorplan carousel by linking two carousels
            together and only showing 1 image of one carousel to create our
            preview pane
 * Parameters:  n/a
 * Returns: n/a
**/
var activeAJAX = 0;
var buildingFloors = {};
var floorImages = {};

//var Sector = require('./text/sector2.js');

/**
 * Summary: Initiailize icon tooltips and loading spinner while carousels load images
 * Parameters: n/a
 * Returns: n/a
**/
$(document).ready(function () {
    $('#toolIcon').tooltip();
    var opts = {
      lines: 13, // The number of lines to draw
      length: 30, // The length of each line
      width: 20, // The line thickness
      radius: 60, // The radius of the inner circle
      corners: 1, // Corner roundness (0..1)
      rotate: 0, // The rotation offset
      direction: 1, // 1: clockwise, -1: counterclockwise
      color: '#fff', // #rgb or #rrggbb
      speed: 0.75, // Rounds per second
      trail: 60, // Afterglow percentage
      shadow: false, // Whether to render a shadow
      hwaccel: false, // Whether to use hardware acceleration
      className: 'spinner', // The CSS class to assign to the spinner
      zIndex: 2e9, // The z-index (defaults to 2000000000)
      top: 'auto', // Top position relative to parent in px
      left: 'auto' // Left position relative to parent in px
    };
    var target = document.getElementById('loading');
    var spinner = new Spinner(opts).spin(target);
    setTimeout(init, 0);
});

/**
 * Summary: initializes authoringTool data structures
 * Parameters: building: localStorage building object json string
 * Returns: initialized authoring tool
**/
function init() {
    var buildingJSON = localStorage.getItem('building');
    console.log("Gary Client: " + JSON.stringify(buildingJSON));
    if (buildingJSON !== null) {
        // grab building object from local storage and initialize
        building = $.parseJSON(buildingJSON);
        var floors = building.floors;
        for (var i = 0; i < floors.length; i++) {
            buildingFloors[floors[i].name] = floors[i];
            getFloorImage(floors[i]);
        };        
        /* Initialize the canvas */
        var canvas = resizeCanvas();
        GLOBALS = new GlobalsContainer();
        GLOBALS.setCanvas(canvas);
        stateManager = initStateManager(building, canvas);//new StateManager();
        /* The event handler for when a new state is clicked */
        $(".tool").click(function() {
            $(".tool").removeClass("active");
            $(this).addClass("active");
            
            var newState = $(this).attr("id");
            stateManager.changeState(newState);
        });
        initCanvasEventHandlers(stateManager);
        initKeyboardShortcuts();
    }
}

/**
 * Summary: Initializes the linked carousels for floor switching
 * Parameters: n/a
 * Returns: initialized preview pane and navigation carousel
**/
function initCarousels() {
    // This is the connector function.
    // It connects one item from the navigation carousel to one item from the
    // stage carousel.
    // The default behaviour is, to connect items with the same index from both
    // carousels. This might _not_ work with circular carousels!
    var connector = function(itemNavigation, carouselStage) {
        return carouselStage.jcarousel('items').eq(itemNavigation.index());
    };

    $(function() {
        // Setup the carousels. Adjust the options for both carousels here.
        var carouselStage      = $('.carousel-stage').jcarousel();
        var carouselNavigation = $('.carousel-navigation').jcarousel();
        // We loop through the items of the navigation carousel and set it up
        // as a control for an item from the stage carousel.
        carouselNavigation.jcarousel('items').each(function() {
            var item = $(this);
            // This is where we actually connect to items.
            var target = connector(item, carouselStage);
            item
                .on('active.jcarouselcontrol', function() {
                    carouselNavigation.jcarousel('scrollIntoView', this);
                    item.addClass('active');
                })
                .on('inactive.jcarouselcontrol', function() {
                    item.removeClass('active');
                })
                .jcarouselControl({
                    target: target,
                    carousel: carouselStage
                });
        });
        // Setup controls for the navigation carousel
        $('.prev-navigation')
            .on('inactive.jcarouselcontrol', function() {
                $(this).addClass('inactive');
            })
            .on('active.jcarouselcontrol', function() {
                $(this).removeClass('inactive');
            })
            .jcarouselControl({
                target: '-=2'
            });
        $('.next-navigation')
            .on('inactive.jcarouselcontrol', function() {
                $(this).addClass('inactive');
            })
            .on('active.jcarouselcontrol', function() {
                $(this).removeClass('inactive');
            })
            .jcarouselControl({
                target: '+=2'
            });
    });
    // Set up floor switching event handler
    $(".navigationImage").click(function() {
        var domImage = $(this);
        var floorName = domImage.data().internalid;
        var image = new Image();
        var floors = building.floors;
        var floorIndex = ArrayIndexOf(floors, function(floor) {
                    return floor.name == floorName;
                });
        if (floorIndex !== -1) {
            image.src = domImage.attr('src');
            var newFloor = floors[floorIndex];
            stateManager.changeFloor(stateManager.floors[floorIndex]);
            var currentFloor = stateManager.getCurrentFloor();
            currentFloor.globals.canvas.image = image;
            stateManager.redraw();
        }
    });
};

function initKeyboardShortcuts() {
    Mousetrap.bind({
        'v': function() { $('#Select').click(); },
        'h': function() { $('#Pan').click(); },
        '/': function() { $('#Draw').click(); },
        'd': function() { $('#Door').click(); },
        'c': function() { $('#Classify').click(); },
        'l': function() { $('#Landmark').click(); },
        's': function() { $('#Stair').click(); },
        'e': function() { $('#Elevator').click(); },
        'x': function() { $('#Exit').click(); }
    });
}

/**
 * Summary: Grabs floor plan image from database
 * Parameters:  floor: floor object
 * Returns: adds image to carousels
**/
function getFloorImage(floor) {
    activeAJAX++;
    $.ajax({ 
        type: "GET",
        url: '/image',
        async: true, 
        data:{
            imageId: floor.imageId,
        }, 
        success: function(response) {
            floorImages[this] = response;
            if (--activeAJAX == 0) {
                addFloorImages();
            }
        }.bind(floor.name),
        error: function() {
            if (--activeAJAX == 0) {
            }
        }
    });
}

/**I amup fro wht
 * Summary: Adds fetched floor images to preview pane and navigation carousel
 * Parameters:  n/a
 * Globals: floorNames: list of names, floorImages: mapping of names to images
 * Returns: boolean if element exists in array
**/
function addFloorImages() {
    // images from preview pane
    var currentImages = $('#currentImages');
    // images from navigation carousel
    var navigationImages = $('#navigationImages');
    var floorNames = Object.keys(buildingFloors);
    floorNames.alphanumSort(true);
    // add floor images to carousels in alphanumeric order of floor names
    for (var i = 0; i < floorNames.length; i++) {
        var floorName = floorNames[i];
        var image = floorImages[floorName];
        var dataURL = image.dataURL;
        var imageStr = image.imageStr;
        var stageImage = $('<li><img class="currentImage" src="' + dataURL + imageStr + '"></li>');
        var navImage = $('<li><img data-internalid='+floorName+' class="navigationImage" src="' + dataURL + imageStr + '"></li>');
        currentImages.append(stageImage);
        navigationImages.append(navImage);
        // initialize canvas with lowest floor
        if (i === 0) {
            var currentFloor = stateManager.getCurrentFloor();
            currentFloor.globals.canvas.image = $('<img class="currentImage" src="' + dataURL + imageStr + '">')[0];
        }
    }
    setTimeout(function() {
        initCarousels();
        $('#loading').css('display', 'none');
    }, 0) 
}

/**
 * Summary: Finds index of element inside an array
 * Parameters:  a: list of elements, fnc: returns true if element is found
 * Returns: boolean if element exists in array
**/
function ArrayIndexOf(a, fnc) {
    if (!fnc || typeof (fnc) != 'function') {
        return -1;
    }
    if (!a || !a.length || a.length < 1) return -1;
    for (var i = 0; i < a.length; i++) {
        if (fnc(a[i])) return i;
    }
    return -1;
}

/********************************************************************************/

/*
 * 
 * name: scaleSpace
 * @param spaces - List of current spaces defined for the current floor plan
 * 		  scaleFactor - The scale by which to reduce the lines and points that make up the space
 * @return scaledSpaces - A scaled down version of the spaces
 * 
 * Note: This function uses jquery to clone the spaces object
 * 
 */
function scaleSpace(spaces, scaleFactor)
{
	var scaledSpaces = jQuery.extend(scaledSpaces, spaces);

	// Loop through each space and scale down the lines segments representing a wall
	for(var s = 0; s < spaces.length; s++)
	{
		var currentSpace = spaces[s];

		for(var w = 0; w < currentSpace.walls.length; w++)
		{
			var currentWall = currentSpace.walls[w];

			// Call function to scale the line since 
		}
	}
	
	return scaledSpaces;
}

/********************************************************************************/

function findMaxXYComponentsforPoints(floors)
{
	var minX = Number.MAX_VALUE;
	var minY = Number.MAX_VALUE;
	var maxX = 0;
	var maxY = 0;

	// For each floor
	for (var f=0; f < floors.length; f++)
	{
		var currentFloor = floors[f];
		
		for (var s=0; s < currentFloor.spaces.length; s++)
		{
			var currentSpace = currentFloor.spaces[s];
			
			for(var p=0; p < currentSpace.selectPoly.points.length; p++)
			{
				var currentPoint = currentSpace.selectPoly.points[p];
				
				// Find the max points
				if (currentPoint.x > maxX)
				{
					maxX = currentPoint.x;
				}
				
				if (currentPoint.y > maxY)
				{
					maxY = currentPoint.y;
				}
				
				// Find the min points
				if (currentPoint.x < minX)
				{
					minX = currentPoint.x;
				}
				
				if (currentPoint.y < minY)
				{
					minY = currentPoint.y;
				}
			}
		}
	}

	pointLimits=new Object();
	pointLimits.minX = minX;
	pointLimits.minY = minY;
	pointLimits.maxX = maxX;
	pointLimits.maxY = maxY;
 
	return pointLimits;
}

/**
 * Summary: Saves building to database and local storage
 * Parameters: none
 * Returns: none
**/
$('#save').click(function() {
    $(this).spin('small', '#fff');
    var building = stateManager.building;
    var buildingOut = building.toOutput();
    var canvasWidth = CANVAS_WIDTH;
    var canvasHeight = CANVAS_HEIGHT;
    $.ajax({
        type: "POST",
        url: '/savePublish',
        data: {
            building: {
                name: building.name,
                authoData: buildingOut,
                graph: null
            },
            publishData: false,
            width: canvasWidth,
            height: canvasHeight,
        },
        success: function(response) {
            //save in local storage and redirect
            $('#save').spin(false);
            localStorage.setItem('building', JSON.stringify(this));
        }.bind(buildingOut),
        error: function(response) {
            // remove loading spinner and alert user of error
            $('#save').spin(false);
            alert('An error occurred, please try again.');
        }
    })
});

/**
 * Summary: Publishes building to database and saves to local storage
 * Parameters: none
 * Returns: none
**/
$('#publish').click(function()
{
    $(this).spin('small', '#fff');
    var building = stateManager.building;
    var buildingOut = building.toOutput();
    var graph = new BuildingGraph(building);
    var graphOut = graph.toOutput();

	// Required for text file generation
	var originalMapString = "";
	var scaledMapString = "";
	var roomString = "";
	var sectorString = "";
	var floorName = "unidentified";

	// Compute the buffer size for the current floor

	// Given the spaces object, build the string representation of the map
	// TODO: The floors should be in a loop when we move to multi floor planning
	if (stateManager.floors[0].spaces != undefined)
	{
		// Build the floor name that will be used to name the text files
		floorName = building.name + "_" + stateManager.floors[0].name;

		var spaces = stateManager.floors[0].spaces;

		// This function returns the following array [minX, minY, maxX, maxY]
		// Get the dimensions of the actual image.
		var canvasHeight = stateManager.floors[0].globals.canvas.image.height;
		var canvasWidth  = stateManager.floors[0].globals.canvas.image.width;
		var pointLimits = findMaxXYComponentsforPoints(stateManager.building.floors);

		// Perform sanity check to make sure no points exist outside of the bounded image area
		if ((pointLimits.minX < 0) || (pointLimits.minY < 0) || (pointLimits.maxX > canvasWidth) || (pointLimits.maxY > canvasHeight))
		{
			alert("Some points are outside of the image bounds of [" + canvasWidth + " by " + canvasHeight + "]");
			return;
		}

		if (spaces.length > 0)
		{
			// TODO: I may need to add the ability to scale down the array given the image dimensions and the defined res below.
			// 		 The current array that is produced is full size when compared to the image. Code exists to scale down this array
			//		 as defined in the file image convert lines 515 to 551. I need to use this same code in this function to scale down
			//		 both the maps and the sector arrays.

			var imageScaleForDisplay = 13;	// Value defined in the Android app regarding the 
			var mapScale 			 = 6;	// Values defined in the Android app

			console.log("MAP GENERATION: Generating Map text file.");
			var mapArray = generateMap(spaces, canvasWidth, canvasHeight, true, mapScale);	// "true" indicates to make a scale representation of the map

			originalMapString = convertMapArrayToString(mapArray.originalSizeMap, canvasWidth, canvasHeight, imageScaleForDisplay, mapScale);
			scaledMapString = convertMapArrayToString(mapArray.scaledSizeMap, canvasWidth/mapScale, canvasHeight/mapScale, imageScaleForDisplay, mapScale);

			console.log("ROOM GENERATION: Generating Room text file.");
			roomString = generateRoom(spaces, '\n');

			var scaledSpaces = scaleSpace(spaces, mapScale);

			// Commented out until map text file generation is correct since sector generation can take up to several minutes
			//console.log("SECTOR GENERATION: Generating Sector text file.");

			//var startDate = Date.now();
			//sectorString = generateSectorStr(spaces, canvasWidth, canvasHeight, imageScaleForDisplay, mapScale);
			//var endDate = Date.now();
			//var totalTime = endDate - startDate;			
			//console.log("Sector generation took " + (totalTime/1000) + " seconds.");			
		}
		else
		{
			console.log("TEXT FILE GENERATION: There are no spaces defined in session scope");
		}			
	}
	else
	{
		console.log("TEXT FILE GENERATION: stateManager.floors[0].spaces NOT defined in session scope");
	}

    $.ajax({
        type: "POST",
        url: '/savePublish',
        data: {
            building: {
                name: building.name,
                authoData: buildingOut,
                graph: graphOut
            },
            publishData: true,
            width: canvasWidth,	   // Required for text file generaiton on server side
            height: canvasHeight,  // Requried for text file generation on server side
            map: originalMapString,
            scaledMap: scaledMapString,
            room: roomString,
            sector: sectorString,
            floorName: floorName,
        },
        success: function(response)
        {
            //save in local storage and redirect
            $('#publish').spin(false);
            localStorage.setItem('building', JSON.stringify(this));
            alert('Building has been published successfully!');
        }.bind(buildingOut),
        error: function(response) {
            // remove loading spinner and alert user of error
            $('#publish').spin(false);
            alert('An error occurred, please try again.');
        }
    })
});
