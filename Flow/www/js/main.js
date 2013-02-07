
/**
 * Summary: Initialize everything, but only when the document is fully loaded.
**/
$(document).ready(function () 
{
	/* Initialize the canvas */
    can = document.getElementById("canvas");
    CANVAS = can.getContext("2d");

    CANVAS.addEventListener("mousemove", mouseMoved);
    CANVAS.addEventListener("mouseout", mouseOut);
    CANVAS.addEventListener("mouseover", mouseIn);
    CANVAS.addEventListener("click", mouseClicked);           
    CANVAS.width = 500;
    CANVAS.height = 500;

});