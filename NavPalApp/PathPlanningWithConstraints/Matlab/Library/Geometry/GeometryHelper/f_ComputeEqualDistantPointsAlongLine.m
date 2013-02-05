function [path] = f_ComputeEqualDistantPointsAlongLine(startingPoint, endingPoint, distanceBetweenWaypoints)
% TODO: Rename to 

    % Find the unit vector for the tangent
    vector = endingPoint - startingPoint;           % Translate the vector to the origin
    normalizedVector = vector/norm(vector);         % Normalize the vector

    % Compute the length of the tangent line
    lengthOfTangent = f_EuclideanDistance(startingPoint, endingPoint);

    % Determine the number of points that need to be computed
    numnberOfPoints = floor(lengthOfTangent / distanceBetweenWaypoints);

    path = zeros(numnberOfPoints, 3);

    for i=1:numnberOfPoints
        path(i, :) = i*normalizedVector*distanceBetweenWaypoints + startingPoint;
    end

end
