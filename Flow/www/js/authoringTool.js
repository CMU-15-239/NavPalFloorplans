/**
 * Initializes layout of authoring tool page
 * Written by: Daniel Muller
*/

$(document).ready(function () 
    { $('#toolIcon').tooltip();}
);

/**
 * Summary: Connects preview pane to floorplan carousel by linking two carousels
            together and only showing 1 image of one carousel to create our
            preview pane
 * Parameters:  n/a
 * Returns: n/a
**/
(function($) {
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
})(jQuery);
