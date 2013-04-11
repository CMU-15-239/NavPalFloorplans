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
};

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

function addFloorImages() {
    console.log('addFloorImages');
    var currentImages = $('#currentImages');
    var navigationImages = $('#navigationImages');
    var floorNames = Object.keys(buildingFloors);
    floorNames.alphanumSort(true);
    console.log(floorImages);
    
    for (var i = 0; i < floorNames.length; i++) {
        var floorName = floorNames[i];
        var image = floorImages[floorName];
        var dataURL = image.dataURL;
        var imageStr = image.imageStr;
        var stageImage = $('<li><img class="currentImage" src="' + dataURL + imageStr + '"></li>');
        var navImage = $('<li><img class="navigationImage" src="' + dataURL + imageStr + '"></li>');
        currentImages.append(stageImage);
        navigationImages.append(navImage);
    }
    setTimeout(function() {
        initCarousels();
        $('#loading').css('display', 'none');
    }, 0) 
}    


function init() {
    var buildingJSON = localStorage.getItem('building');
    if (buildingJSON !== null) {
        
        var building = $.parseJSON(buildingJSON);
        var floors = building.floors;
        for (var i = 0; i < floors.length; i++) {
            buildingFloors[floors[i].name] = floors[i];
            getFloorImage(floors[i]);
        };
    
    


        /* Initialize the canvas */
        var canvas = resizeCanvas();
        
        GLOBALS = new GlobalsContainer(canvas);
                
        stateManager = initStateManager(building, canvas);//new StateManager();
        
        /* The event handler for when a new state is clicked */
        $(".tool").click(function() {
            $(".tool").removeClass("active");
            $(this).addClass("active");
            
            var newState = $(this).attr("id");
            stateManager.changeState(newState);
        });
        
        initCanvasEventHandlers(stateManager);
        //testImport();
    }
}

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