//globals.js

/* This file stores all of the globals used throughout the tool. */

//Store all of the closed rooms.
var ALL_CLOSED_ROOMS = [];

//The array that stores all the walls drawn in the floorplan.
var ALL_WALLS = []

//The array that stores all the points in the floorplans.
var ALL_POINTS = [];

//The canvas instantiation.
var CANVAS;

//The width of the canvas (in pixels).
var CANVAS_WIDTH = 780;

//The height of the canvas (in pixels).
var CANVAS_HEIGHT = 500;

//The color that the points at the ends of walls should be drawn.
var POINT_COLOR = 'rgba(0,100,255,.6)';

//The radius of a point, in pixels.
var POINT_SIZE = 5;

//The width to draw the walls (in pixels).
var WALL_WIDTH = 4;

//The radius of the snap circle (in pixels).
var SNAP_RADIUS = 15;

//The current state of the canvas.
var STATE = "line_tool";

//The room that's being hovered over
var ACTIVE_ROOM = undefined;

//If true, don't change the active room
var BLOCK_CHANGE_ROOM = false;


