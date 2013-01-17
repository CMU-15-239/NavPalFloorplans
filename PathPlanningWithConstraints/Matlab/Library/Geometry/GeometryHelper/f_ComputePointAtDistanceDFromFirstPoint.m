function [point] = f_ComputePointAtDistanceDFromFirstPoint(pointA, pointB, k)

%
% Computes the x-y coordinates of a point P that is a specific distance away
% from point A along the line segment AB.
%
% Inputs:
%
%   pointA - The respective [x, y, 0] coordinates for point A
%   pointB - The respective [x, y, 0] coordinates for point B
%   k - Determines the distance of point P from A
%       If k =  1.0, point P wil lie length of line segment AB away from point A
%       If k =  0.0, point P will be the same as point A
%       If k = -1.0 point P will be the same a point B
%       
%       k can take on any real/float value (e.g., k=2.5 will place point P
%       2.5 * length segment AB away from point A).
%
% Output:
%
%   point - The point that is distance k * length AB from point A in the form
%           [p1, p2, 0]
%

    point = zeros(1, 3);

    ax = pointA(1); ay = pointA(2);
    bx = pointB(1); by = pointB(2);

    point(1) = ax + k * (ax - bx);
    point(2) = ay + k * (ay - by);

end
