function [perpendicularVector1, perpendicularVector2] = f_ComputePerpendicularVector(point1, point2)

%
% Given two points on a line, computes both perpendicular vectors to point 1.
% The points must be in the form [x, y, z].
% 
% Note that the z component is not currently used in the computations
%
% Inputs:
%
%   point1 - Endpoint 1
%   point2 - Endpoint 2
%
% Outputs:
%
%   perpendicularVector1 - The first vector perpendicular to point 1
%   perpendicularVector2 - The second vector perpendicular to point 1
%

    % Extract the x and y components from each point
    x1 = point1(1); y1 = point1(2);
    x2 = point2(1); y2 = point2(2);

    % Allocate space for the perpendicular vectors
    perpendicularVector1 = zeros(1, 3);
    perpendicularVector2 = zeros(1, 3);

    % Compute the individual components of the slope dy/dx
    % for line through points (x0, y0) and (x1, y1).
    deltaY = (y2 - y1);
    deltaX = (x2 - x1);

    % Compute both perpendicular vectors coming out of
    % point 1 (x1, y1) that face in opposite directions.
    perpendicularVector1(1) = x1 - deltaY;
    perpendicularVector1(2) = y1 + deltaX;    

    perpendicularVector2(1) = x1 + deltaY;
    perpendicularVector2(2) = y1 - deltaX;

end
