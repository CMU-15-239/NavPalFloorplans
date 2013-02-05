function [path] = f_ComputeEqualDistantPointsAlongCircleArc(circleCenter, radius, startingPoint, endingPoint, direction, distanceBetweenWaypoints)

%
% Given a circle, a starting and ending point on the circumference, a direction
% and a resolution, computes a series of points along the circumference between 
% the starting and ending point in the specified direction that are spaced apart
% by the value specified in resolution.
%
% Inputs:
%
%   circleCenter  - Center of the circle specified as [x, y, 0]
%   startingPoint - Starting point in the form [x, y, 0]
%   endingPoint   - Ending point in the form [x, y, 0]
%   direction - A value of -1 for CCW around the circumference from the starting
%               point to the ending point. A value of +1 for CW.
%   distanceBetweenWaypoints - The number of points to compute between the starting and ending point
%

    % Translate the starting and ending points as vectors around the origin
    startingVector = startingPoint - circleCenter;
    endingVector   = endingPoint - circleCenter;

    % Normalize both starting and ending vectors
    normStartingVector = startingVector / norm(startingVector);
    normEndingVector   = endingVector / norm(endingVector);

    % Compute the cross and dot product for the vectors. The dot product will be
    % used to compute the angle between the start and end vectors and the z
    % component of the cross product will be used to determine of the traversal
    % from the start point to the end point is greater than 180 degrees.
    dotProduct = dot(normStartingVector, normEndingVector);
    crossProduct = cross(normStartingVector, normEndingVector);

    % Compute the angle between the two vectors
    theta = acos(dotProduct);

    % Given the direction of the z component of the cross product and the
    % specified value in direction, determine if the path from the starting
    % vector to the ending vector is greater than 180 degrees. If it is use the
    % complement angle between for theta.
    if (direction ~= sign(crossProduct(3)))
        theta = 2*pi - theta;
    end

    % Convert from Radians to Degrees
    thetaInDegrees = theta*180/pi;

    % Compute the arc length between the starting and ending points
    arcLength = (thetaInDegrees/360)*2*radius*pi;

    numberOfPoints = floor(arcLength / distanceBetweenWaypoints);
    angleSweepForEachWaypointAlongCircumference = (distanceBetweenWaypoints * 360) / (2*pi*radius); 

    % TODO: If waypoints are too close together, then they need to be placed 
    % Corner Case 1: when approaching the ending waypoint, compute the arc
    % length between the previous computed waypoint and ending waypoint. If the
    % arclength is less than the distance between waypoints, then exit and add
    % ending waypoint as the last waypoint for the arc length.

    % Rotate the starting point to the departurePointOuter and compute the
    % intermediate points at specific intervals to create a path
    %numberOfPoints = floor(thetaInDegrees)/resolution;
    arcPoints = zeros(numberOfPoints, 3);

    % Compute the series points between the starting and ending vector
    for i=1:numberOfPoints
        rotatedPoint = f_RotatePoint(startingVector, i*angleSweepForEachWaypointAlongCircumference, direction);
        arcPoints(i, :) = rotatedPoint + circleCenter;
    end

%     hold on
% 
%     plot(startingPoint(1), startingPoint(2), 'o', 'MarkerEdgeColor', 'g', 'MarkerFaceColor', 'g', 'LineWidth', 1, 'MarkerSize', 4);
%     plot(endingPoint(1), endingPoint(2), 'o', 'MarkerEdgeColor', 'r', 'MarkerFaceColor', 'r', 'LineWidth', 1, 'MarkerSize', 4);
% 
%     hold off

    path = [startingPoint; arcPoints; endingPoint];
