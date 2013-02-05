% Same as f_DetermineWhichDirectionToMoveBetweenVectors
function [rotationDirection] = f_FindRotationalDirection(startingVector, endingVector, pointAtWhichToRotate)

    % Translate perpendicular points and the entry point to the origin (0, 0, 0)
    % (e.g., translatedPoint = [x1, y1, z1] - [x2, y2, z2])
    translatedStartingPoint = startingVector - pointAtWhichToRotate;
    translatedEndingPoint   = endingVector   - pointAtWhichToRotate;

    % Take the cross product between the starting and ending point to
    % determine the sign of the Z componet.
    crossProductBetweenStartingPointAndEndingPoint = cross(translatedStartingPoint, translatedEndingPoint);

    % Extract the sign of the z component. The sign will indicate which
    % direction the point will need to be rotated, either CW (-1) or CWW (1).
    rotationDirection = sign(crossProductBetweenStartingPointAndEndingPoint(3));

end