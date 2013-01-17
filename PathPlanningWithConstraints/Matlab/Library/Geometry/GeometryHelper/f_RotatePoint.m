function [rotatedPoint] = f_RotatePoint(originalPoint, thetaInDegrees, direction)
%
% Rotates a point about the origin (0, 0) by the specified angle in either
% a clockwise or counter-clockwise direction
%
% Parameters:
%
%       originalPoint - The point to rotate. Specified as vector [x, y]
%       thetaInDegrees - The angle (in degrees) at which to rotate the
%                        point
%       direction - Indicates the direction which to rotate
%                   -1 for CW
%                   +1 for CCW
%
% Outputs: The rotated point about the z axis
%

    rotatedPoint = zeros(1, 3);

    x = originalPoint(1);
    y = originalPoint(2);

    % Adjust the direction of the angle if needed
    if (direction < 0)
       thetaInDegrees = -thetaInDegrees; 
    end

    rotatedPoint(1) = x * cosd(thetaInDegrees) - y * sind(thetaInDegrees);
    rotatedPoint(2) = x * sind(thetaInDegrees) + y * cosd(thetaInDegrees);

end
