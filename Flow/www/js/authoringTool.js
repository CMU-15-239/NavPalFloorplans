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
function carousels() {
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

function addFloorToCarousel(floor) {
    $.ajax({ 
        type: "GET",
        url: '/image',
        async: true, 
        data:{
            imageId: floor.imageId,
        }, 
        success: function(response) {
            var carouselStage = $('.carousel-stage');
            var carouselNavigation = $('.carousel-navigation');
            var dataURL = response.dataURL;
            var imageStr = response.imageStr;
            var stageImage = '<li><img class="currentImage" src="' + dataURL + imageStr + '"></li>';
            var navImage = '<li><img class="navigationImage" src="' + dataURL + imageStr + '"></li>';
            console.log(stageImage);

            carouselStage.append(stageImage);
            carouselNavigation.append(navImage);
        }
    }).done(carousels);
}

$(document).ready(function () { 

    $('#toolIcon').tooltip();
    var buildingJSON = localStorage.getItem('building');
    var building = $.parseJSON(buildingJSON);
    var labelToFloor = {};
    var labels = [];
    console.log(building);
    for (var i = 0; i < building.floors.length; i++) {
        labels.push(building.floors[i].name);
        labelToFloor[building.floors[i].name] = building.floors[i];
    };
    console.log(labels);
    labels.alphanumSort(true);
    for (var i = 0; i < labels.length; i++) {
        var label = labels[i];
        var floor = labelToFloor[label];
        console.log(floor);
        addFloorToCarousel(floor);
		
    };





    /* Initialize the canvas */
    var canvas = resizeCanvas();
    
    //GLOBALS = new GlobalsContainer(canvas);
    
    //JSON.parse(simpleBuiildingfromlocal data)
    
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
});