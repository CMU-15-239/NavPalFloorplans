%function [distance] = EuclideanDistance(x1, y1, x2, y2)
function [distance] = f_EuclideanDistance(point1, point2)
%
% Computes the Euclidean distance between point1 and point2. Both points must
% have the form [x, y, z]. To compute distances in the 2D euclidean space, set
% the value for z in each point to 0.
%
    x1 = point1(1); y1 = point1(2); z1 = point1(3);
    x2 = point2(1); y2 = point2(2); z2 = point2(3);

    deltaX = x2-x1;
    deltaY = y2-y1;
    deltaZ = z2-z1;

    distance = sqrt(deltaX*deltaX + deltaY*deltaY + deltaZ*deltaZ);

end
