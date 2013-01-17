function [dilatedPolygon] = f_DilatePolygon(polygon, magnitude)
%
% Dilates a polygon by a specified magnitude. Dilation occurs by computing
% the perpendicular vector of each polygon side then moving each side by
% the specified magnitude along the perpendicular vector away from the
% polygon's center.
%
% Parameters:
%
%       polygon - A polygon specified by a 3 x N list of points that have
%                 the form shown below. Note that the z component of the
%                 point is included to be consistent with the other
%                 functions that use a 3 component point. However, this
%                 function only uses the x and y components for polygon
%                 dilation. Also note that when specifying the points
%                 (vertices) of the polygon, the list must end with the
%                 first point as shown below by defining a square.
%
%                       [x-coordinate y-coordinate z-coordinate]
%
%                       polygon = [10, 10, 0;
%                                  10, 20, 0;
%                                  20, 20, 0;
%                                  20, 10, 0;
%                                  10, 10, 0]
%
%       magnitude - The value by which to increase the polygon dilation.
%                   For instance, a value of 2 will move each side of the
%                   polygon outward along its perpendicular vector by 2 *
%                   the unit vector for each side.
%
% Returns:
%
%       dilatedPolygon - List of the dilated points. Note that the list
%                        of points begins with the first points and ends
%                        with the first point.
%

    %
    % Initialize Data Structures used for the 
    %
    numberOfPoints = size(polygon, 1);
    midPoints = zeros(numberOfPoints-1, 3);
    dilatedPolygon = zeros(numberOfPoints, 3);

    perpendiculatVectorsSet1 = zeros(numberOfPoints-1, 3);
    perpendiculatVectorsSet2 = zeros(numberOfPoints-1, 3);

    perpendiculatUnitVectorsSet1 = zeros(numberOfPoints-1, 3);
    perpendiculatUnitVectorsSet2 = zeros(numberOfPoints-1, 3);

    perpendicularVectorsUsedToDilatePolygon = zeros(numberOfPoints-1, 3);

    % For each polygon side, compute the midpoint
    for i=1:numberOfPoints-1

        point1 = polygon(i, :);
        point2 = polygon(i+1, :);
        midPoints(i, :) = f_ComputeMidPointOfLine(point1, point2);

        % Compute the perpendicular vectors for each side
        [unitVector1, unitVector2] = f_ComputePerpendicularLine(point1, point2);

        normUnitVector1 = unitVector1 / norm(unitVector1);
        normUnitVector2 = unitVector2 / norm(unitVector2);

        perpendiculatVectorsSet1(i, :) = midPoints(i, :) + normUnitVector1;
        perpendiculatVectorsSet2(i, :) = midPoints(i, :) + normUnitVector2;

        perpendiculatUnitVectorsSet1(i, :) = normUnitVector1;
        perpendiculatUnitVectorsSet2(i, :) = normUnitVector2;

    end

	% Determine which vectors lie outside the polygon (i.e., the ones pointing away from the polygon center)
    in1 = f_PointInPolygon(perpendiculatVectorsSet1(:, 1:2), polygon(:, 1:2));
    in2 = f_PointInPolygon(perpendiculatVectorsSet2(:, 1:2), polygon(:, 1:2));

    if (1 && all(in1 == 1))
        perpendicularVectorsUsedToDilatePolygon = perpendiculatUnitVectorsSet2;
    elseif (1 && all(in2 == 1))
        perpendicularVectorsUsedToDilatePolygon = perpendiculatUnitVectorsSet1;    
    else
%         disp('Something went wrong');
    end

    % Move each endpoint for each side out by the specified vector
    newLineSegments = zeros(numberOfPoints, 6); 
    for i=1:numberOfPoints-1

        point1 = polygon(i, :);
        point2 = polygon(i+1, :);

        unitVector = perpendicularVectorsUsedToDilatePolygon(i, :);

        %line([midPoints(i, 1) (unitVector(1) + midPoints(i, 1))], [ midPoints(i, 2) (unitVector(2) + midPoints(i, 2))], 'Color', 'b', 'LineWidth', 1);

        translatedPoint1 = point1 + magnitude*unitVector;
        translatedPoint2 = point2 + magnitude*unitVector;

        newLineSegments(i, 1:3) = translatedPoint1;
        newLineSegments(i, 4:6) = translatedPoint2;

        %line([translatedPoint1(1) translatedPoint2(1)], [translatedPoint1(2) translatedPoint2(2)], 'Color', 'g', 'LineWidth', 1);

    end

    % Add the points for the first line segment to the end of the list to
    % prepare the data for the next loop, which will to find the intersecting
    % points.
    newLineSegments(numberOfPoints, 1:3) = newLineSegments(1, 1:3);
    newLineSegments(numberOfPoints, 4:6) = newLineSegments(1, 4:6);

    % Find the point of intersection for each new line segment
    dilatedPolygonIndex = 2;    % Computing the intersecting point using
                                % the line segments causes the second
                                % dilated point to be computed first, the
                                % offset must be handled so the dilated
                                % points appear in the same order as the
                                % original points.
    for i=1:numberOfPoints-1

        a0 = newLineSegments(i,1);
        a1 = newLineSegments(i,2);
        b0 = newLineSegments(i,4);
        b1 = newLineSegments(i,5);
        x0 = newLineSegments(i+1, 1);
        x1 = newLineSegments(i+1, 2);
        y0 = newLineSegments(i+1, 4);
        y1 = newLineSegments(i+1, 5);

        denominator = (b0 - a0)*(x1 - y1) - (b1 - a1)*(x0 - y0);

        % If the denominator is zero then the lines are parallel
        if (denominator ~= 0)

            sNumerator   = (x0 - y0)*(a1 - x1) - (x1 - y1)*(a0 - x0);
            tNumerator   = (a0 - x0)*(b1 - a1) - (a1 - x1)*(b0 - a0);

            s = sNumerator/denominator;
            t = tNumerator/denominator;

        else
            s = NaN;
            t = NaN;
        end

        % Now that the scaling factors are known, compute the line of intersection
        if ((~isnan(s)) && (~isnan(t)))

            c0 = a0 + s*(b0 - a0);
            c1 = a1 + s*(b1 - a1);

            z0 = x0 + t*(y0 - x0);
            z1 = x1 + t*(y1 - x1);

            % TODO: The points (c0, c1) and (z0, z1) should be the same. Add a
            %       check to see if they are the same. If not then something in
            %       the calculations in incorrect.

            % Add the newly computed point to the list of points 
            dilatedPolygon(dilatedPolygonIndex, :) = [c0, c1, 0];
            dilatedPolygonIndex = dilatedPolygonIndex + 1;

            %hold on
            %plot(z0, z1, 'o', 'MarkerEdgeColor', 'k', 'MarkerFaceColor', 'y', 'LineWidth', 1, 'MarkerSize', 4);
            %hold off

        end
    end
    dilatedPolygon(1, :) = dilatedPolygon(numberOfPoints, :);   % Account for the offset point

%     hold on
%     f_DrawPolygon(dilatedPolygon, 'r');
%     f_DrawPolygon(polygon, 'g');
%     hold off
    
end
