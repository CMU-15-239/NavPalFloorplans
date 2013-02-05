function f_DrawPolygon(P, Color)
%
% Draws the specified polygon to the current figure. 
%
% Parameters:
%
%   P - The points representing the polygon that have the form listed
%       below. Note that the list must start with and end with the first
%       waypoint. This is to ensure that the polygon is drawn correctly.
%
%           pointList = [10, 10;
%                        10, 20;
%                        20, 20;
%                        20, 10;
%                        10, 10];
%
%   Color - The color of the polygon
%
% Returns:
%
%   (none)
%
    hold on

    xValuesForDrawingPolygon = [P(:, 1); P(1, 1)];
    yValuesForDrawingPolygon = [P(:, 2); P(1, 2)];
    line(xValuesForDrawingPolygon, yValuesForDrawingPolygon, 'Color', Color);
    
    hold off
