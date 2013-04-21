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
    //var buildingJSON = localStorage.getItem('building');
    var buildingJSON = '{"name":"buiding1","floors":[{"name":"1","imageId":"safdsa","width":30,"imageScale":1,"spaces":[{"doors":[{"p1":{"x":10,"y":1,"isSnap":false,"isSelected":false,"degree":0},"p2":{"x":10,"y":5,"isSnap":false,"isSelected":false,"degree":0},"isSelected":false,"isDoor":true,"definesRoom":false,"a":-4,"b":0,"c":40,"distConst":4}],"walls":[{"p1":{"x":0,"y":0,"isSnap":false,"isSelected":false,"degree":0},"p2":{"x":0,"y":10,"isSnap":false,"isSelected":false,"degree":0},"isSelected":false,"isDoor":false,"definesRoom":false,"a":-10,"b":0,"c":0,"distConst":10},{"p1":{"x":0,"y":10,"isSnap":false,"isSelected":false,"degree":0},"p2":{"x":10,"y":10,"isSnap":false,"isSelected":false,"degree":0},"isSelected":false,"isDoor":false,"definesRoom":false,"a":0,"b":10,"c":-100,"distConst":10},{"p1":{"x":10,"y":0,"isSnap":false,"isSelected":false,"degree":0},"p2":{"x":10,"y":1,"isSnap":false,"isSelected":false,"degree":0},"isSelected":false,"isDoor":false,"definesRoom":false,"a":-1,"b":0,"c":10,"distConst":1},{"p1":{"x":10,"y":1,"isSnap":false,"isSelected":false,"degree":0},"p2":{"x":10,"y":5,"isSnap":false,"isSelected":false,"degree":0},"isSelected":false,"isDoor":true,"definesRoom":false,"a":-4,"b":0,"c":40,"distConst":4},{"p1":{"x":10,"y":5,"isSnap":false,"isSelected":false,"degree":0},"p2":{"x":10,"y":10,"isSnap":false,"isSelected":false,"degree":0},"isSelected":false,"isDoor":false,"definesRoom":false,"a":-5,"b":0,"c":50,"distConst":5},{"p1":{"x":0,"y":0,"isSnap":false,"isSelected":false,"degree":0},"p2":{"x":10,"y":0,"isSnap":false,"isSelected":false,"degree":0},"isSelected":false,"isDoor":false,"definesRoom":false,"a":0,"b":10,"c":0,"distConst":10}],"points":[],"type":"room","label":"s1","isClosed":false,"selectPoly":{"points":[{"x":0,"y":10,"isSnap":false,"isSelected":false,"degree":0},{"x":10,"y":10,"isSnap":false,"isSelected":false,"degree":0},{"x":10,"y":5,"isSnap":false,"isSelected":false,"degree":0},{"x":10,"y":1,"isSnap":false,"isSelected":false,"degree":0},{"x":10,"y":0,"isSnap":false,"isSelected":false,"degree":0},{"x":0,"y":0,"isSnap":false,"isSelected":false,"degree":0}]},"drawPoly":false},{"doors":[{"p1":{"x":10,"y":1,"isSnap":false,"isSelected":false,"degree":0},"p2":{"x":10,"y":5,"isSnap":false,"isSelected":false,"degree":0},"isSelected":false,"isDoor":true,"definesRoom":false,"a":-4,"b":0,"c":40,"distConst":4}],"walls":[{"p1":{"x":10,"y":0,"isSnap":false,"isSelected":false,"degree":0},"p2":{"x":20,"y":0,"isSnap":false,"isSelected":false,"degree":0},"isSelected":false,"isDoor":false,"definesRoom":false,"a":0,"b":10,"c":0,"distConst":10},{"p1":{"x":20,"y":0,"isSnap":false,"isSelected":false,"degree":0},"p2":{"x":20,"y":25,"isSnap":false,"isSelected":false,"degree":0},"isSelected":false,"isDoor":false,"definesRoom":false,"a":-25,"b":0,"c":500,"distConst":25},{"p1":{"x":10,"y":10,"isSnap":false,"isSelected":false,"degree":0},"p2":{"x":20,"y":25,"isSnap":false,"isSelected":false,"degree":0},"isSelected":false,"isDoor":false,"definesRoom":false,"a":-15,"b":10,"c":50,"distConst":18.027756377319946},{"p1":{"x":10,"y":5,"isSnap":false,"isSelected":false,"degree":0},"p2":{"x":10,"y":10,"isSnap":false,"isSelected":false,"degree":0},"isSelected":false,"isDoor":false,"definesRoom":false,"a":-5,"b":0,"c":50,"distConst":5},{"p1":{"x":10,"y":1,"isSnap":false,"isSelected":false,"degree":0},"p2":{"x":10,"y":5,"isSnap":false,"isSelected":false,"degree":0},"isSelected":false,"isDoor":true,"definesRoom":false,"a":-4,"b":0,"c":40,"distConst":4},{"p1":{"x":10,"y":0,"isSnap":false,"isSelected":false,"degree":0},"p2":{"x":10,"y":1,"isSnap":false,"isSelected":false,"degree":0},"isSelected":false,"isDoor":false,"definesRoom":false,"a":-1,"b":0,"c":10,"distConst":1}],"points":[],"type":"room","label":"s2","isClosed":false,"selectPoly":{"points":[{"x":20,"y":0,"isSnap":false,"isSelected":false,"degree":0},{"x":20,"y":25,"isSnap":false,"isSelected":false,"degree":0},{"x":10,"y":10,"isSnap":false,"isSelected":false,"degree":0},{"x":10,"y":5,"isSnap":false,"isSelected":false,"degree":0},{"x":10,"y":1,"isSnap":false,"isSelected":false,"degree":0},{"x":10,"y":0,"isSnap":false,"isSelected":false,"degree":0}]},"drawPoly":false}],"obstacles":[],"landmarks":[{"label":"candyman","description":"Asdfafa","pointRep":{"x":3,"y":3,"isSnap":false,"isSelected":false,"degree":0}},{"label":"42","description":"1as","pointRep":{"x":20,"y":25,"isSnap":false,"isSelected":false,"degree":0}}],"floorConnections":[{"label":"evil stairs","pointRep":{"x":10,"y":10,"isSnap":false,"isSelected":false,"degree":0},"floorConnectionType":"stairs"},{"label":"good stairs","pointRep":{"x":20,"y":20,"isSnap":false,"isSelected":false,"degree":0},"floorConnectionType":"stairs"}],"globals":{"canvas":null,"walls":[],"points":[],"view":null,"preprocessedText":[],"snapRadius":15}},{"name":"2","imageId":"safdsa2","width":30,"imageScale":1,"spaces":[{"doors":[{"p1":{"x":10,"y":1,"isSnap":false,"isSelected":false,"degree":0},"p2":{"x":10,"y":5,"isSnap":false,"isSelected":false,"degree":0},"isSelected":false,"isDoor":true,"definesRoom":false,"a":-4,"b":0,"c":40,"distConst":4}],"walls":[{"p1":{"x":0,"y":0,"isSnap":false,"isSelected":false,"degree":0},"p2":{"x":0,"y":10,"isSnap":false,"isSelected":false,"degree":0},"isSelected":false,"isDoor":false,"definesRoom":false,"a":-10,"b":0,"c":0,"distConst":10},{"p1":{"x":0,"y":10,"isSnap":false,"isSelected":false,"degree":0},"p2":{"x":10,"y":10,"isSnap":false,"isSelected":false,"degree":0},"isSelected":false,"isDoor":false,"definesRoom":false,"a":0,"b":10,"c":-100,"distConst":10},{"p1":{"x":10,"y":0,"isSnap":false,"isSelected":false,"degree":0},"p2":{"x":10,"y":1,"isSnap":false,"isSelected":false,"degree":0},"isSelected":false,"isDoor":false,"definesRoom":false,"a":-1,"b":0,"c":10,"distConst":1},{"p1":{"x":10,"y":1,"isSnap":false,"isSelected":false,"degree":0},"p2":{"x":10,"y":5,"isSnap":false,"isSelected":false,"degree":0},"isSelected":false,"isDoor":true,"definesRoom":false,"a":-4,"b":0,"c":40,"distConst":4},{"p1":{"x":10,"y":5,"isSnap":false,"isSelected":false,"degree":0},"p2":{"x":10,"y":10,"isSnap":false,"isSelected":false,"degree":0},"isSelected":false,"isDoor":false,"definesRoom":false,"a":-5,"b":0,"c":50,"distConst":5},{"p1":{"x":0,"y":0,"isSnap":false,"isSelected":false,"degree":0},"p2":{"x":10,"y":0,"isSnap":false,"isSelected":false,"degree":0},"isSelected":false,"isDoor":false,"definesRoom":false,"a":0,"b":10,"c":0,"distConst":10}],"points":[],"type":"room","label":"s3","isClosed":false,"selectPoly":{"points":[{"x":0,"y":10,"isSnap":false,"isSelected":false,"degree":0},{"x":10,"y":10,"isSnap":false,"isSelected":false,"degree":0},{"x":10,"y":5,"isSnap":false,"isSelected":false,"degree":0},{"x":10,"y":1,"isSnap":false,"isSelected":false,"degree":0},{"x":10,"y":0,"isSnap":false,"isSelected":false,"degree":0},{"x":0,"y":0,"isSnap":false,"isSelected":false,"degree":0}]},"drawPoly":false},{"doors":[{"p1":{"x":10,"y":1,"isSnap":false,"isSelected":false,"degree":0},"p2":{"x":10,"y":5,"isSnap":false,"isSelected":false,"degree":0},"isSelected":false,"isDoor":true,"definesRoom":false,"a":-4,"b":0,"c":40,"distConst":4}],"walls":[{"p1":{"x":10,"y":0,"isSnap":false,"isSelected":false,"degree":0},"p2":{"x":20,"y":0,"isSnap":false,"isSelected":false,"degree":0},"isSelected":false,"isDoor":false,"definesRoom":false,"a":0,"b":10,"c":0,"distConst":10},{"p1":{"x":20,"y":0,"isSnap":false,"isSelected":false,"degree":0},"p2":{"x":20,"y":25,"isSnap":false,"isSelected":false,"degree":0},"isSelected":false,"isDoor":false,"definesRoom":false,"a":-25,"b":0,"c":500,"distConst":25},{"p1":{"x":10,"y":10,"isSnap":false,"isSelected":false,"degree":0},"p2":{"x":20,"y":25,"isSnap":false,"isSelected":false,"degree":0},"isSelected":false,"isDoor":false,"definesRoom":false,"a":-15,"b":10,"c":50,"distConst":18.027756377319946},{"p1":{"x":10,"y":5,"isSnap":false,"isSelected":false,"degree":0},"p2":{"x":10,"y":10,"isSnap":false,"isSelected":false,"degree":0},"isSelected":false,"isDoor":false,"definesRoom":false,"a":-5,"b":0,"c":50,"distConst":5},{"p1":{"x":10,"y":1,"isSnap":false,"isSelected":false,"degree":0},"p2":{"x":10,"y":5,"isSnap":false,"isSelected":false,"degree":0},"isSelected":false,"isDoor":true,"definesRoom":false,"a":-4,"b":0,"c":40,"distConst":4},{"p1":{"x":10,"y":0,"isSnap":false,"isSelected":false,"degree":0},"p2":{"x":10,"y":1,"isSnap":false,"isSelected":false,"degree":0},"isSelected":false,"isDoor":false,"definesRoom":false,"a":-1,"b":0,"c":10,"distConst":1}],"points":[],"type":"room","label":"s4","isClosed":false,"selectPoly":{"points":[{"x":20,"y":0,"isSnap":false,"isSelected":false,"degree":0},{"x":20,"y":25,"isSnap":false,"isSelected":false,"degree":0},{"x":10,"y":10,"isSnap":false,"isSelected":false,"degree":0},{"x":10,"y":5,"isSnap":false,"isSelected":false,"degree":0},{"x":10,"y":1,"isSnap":false,"isSelected":false,"degree":0},{"x":10,"y":0,"isSnap":false,"isSelected":false,"degree":0}]},"drawPoly":false}],"obstacles":[],"landmarks":[{"label":"candyman","description":"Asdfafa","pointRep":{"x":3,"y":3,"isSnap":false,"isSelected":false,"degree":0}},{"label":"42","description":"1as","pointRep":{"x":20,"y":25,"isSnap":false,"isSelected":false,"degree":0}}],"floorConnections":[{"label":"evil stairs","pointRep":{"x":10,"y":15,"isSnap":false,"isSelected":false,"degree":0},"floorConnectionType":"stairs"},{"label":"good stairs","pointRep":{"x":10,"y":10,"isSnap":false,"isSelected":false,"degree":0},"floorConnectionType":"stairs"}],"globals":{"canvas":null,"walls":[],"points":[],"view":null,"preprocessedText":[],"snapRadius":15}}],"floorConnections":[{"label":"evil stairs","pointRep":{"x":10,"y":10,"isSnap":false,"isSelected":false,"degree":0},"floorConnectionType":"stairs"},{"label":"good stairs","pointRep":{"x":20,"y":20,"isSnap":false,"isSelected":false,"degree":0},"floorConnectionType":"stairs"},{"label":"evil stairs","pointRep":{"x":10,"y":15,"isSnap":false,"isSelected":false,"degree":0},"floorConnectionType":"stairs"},{"label":"good stairs","pointRep":{"x":10,"y":10,"isSnap":false,"isSelected":false,"degree":0},"floorConnectionType":"stairs"}]}'
    // var buildingJSON = "{}";
    if (buildingJSON !== null) {
        
        // grab building object from local storage and initialize
        building = $.parseJSON(buildingJSON);
        var floors = building.floors;
        // for (var i = 0; i < floors.length; i++) {
        //     buildingFloors[floors[i].name] = floors[i];
        //     getFloorImage(floors[i]);
        // };        
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
        'e': function() { $('#Elevator').click(); }
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
                addFloorImages();
            }
        }
    });
}

/**
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

/**
 * Summary: Saves building to database and local storage
 * Parameters: none
 * Returns: none
**/
$('#save').click(function() {
    $(this).spin('small', '#fff');
    var building = stateManager.building;
    var buildingOut = building.toOutput();
    $.ajax({
        type: "POST",
        url: '/savePublish',
        data: {
            building: {
                name: building.name,
                authoData: buildingOut,
                graph: null
            },
            publishData: false
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
$('#publish').click(function() {
    $(this).spin('small', '#fff');
    var building = stateManager.building;
    var buildingOut = building.toOutput();
    var graph = new BuildingGraph(building);
    var graphOut = graph.toOutput();
    $.ajax({
        type: "POST",
        url: '/savePublish',
        data: {
            building: {
                name: building.name,
                authoData: buildingOut,
                graph: graphOut
            },
            publishData: true
        },
        success: function(response) {
            //save in local storage and redirect
            $('#publish').spin(false);
            localStorage.setItem('building', JSON.stringify(this));
            alert('Building has been published successfully!')
        }.bind(buildingOut),
        error: function(response) {
            // remove loading spinner and alert user of error
            $('#publish').spin(false);
            alert('An error occurred, please try again.');
        }
    })
});
