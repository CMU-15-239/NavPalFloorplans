function [midpoint] = f_ComputeMidPointOfLine(point1, point2)

%
% Computes the midpoint between the two specified two endpoints. All points
% must be in the form [x y z].
%
% Inputs:
%
%   point1 - Endpoint 1
%   point2 - Endpoint 2
%
% Outputs:
%
%   midpoint - The midpoint between the two endpoints in the form [x y z]
%

    % Allocate space for the mid-point
    midpoint = zeros(1, 3);

    % extract the x-y-z components for each endpoint
    % Note: This is to improve the readability of this method
    x1 = point1(1);  y1 = point1(2); z1 = point2(3);
    x2 = point2(1);  y2 = point2(2); z2 = point2(3);

    % Compute the midpoint and populate the return variable
    midpoint(1) = (x1 + x2) / 2;
    midpoint(2) = (y1 + y2) / 2;
    midpoint(3) = (z1 + z2) / 2;

end
