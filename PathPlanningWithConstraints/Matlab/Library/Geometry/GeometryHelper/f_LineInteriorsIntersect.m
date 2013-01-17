function [linesIntersect, pointOfIntersection] = f_LineInteriorsIntersect(L1X1, L1Y1, L1X2, L1Y2, L2X1, L2Y1, L2X2, L2Y2)

    % Check if there is an intersection point
    [linesIntersect, pointOfIntersection]= f_LinesIntersect(L1X1, L1Y1, L1X2, L1Y2, L2X1, L2Y1, L2X2, L2Y2);

    % Check both corner cases to see if the point of intersection is an
    % interior point.
    %
    
    %
    %
    if (linesIntersect)

        intersectionX = pointOfIntersection(1);
        intersectionY = pointOfIntersection(2);

        % Corner Case 1: If the intersection point is an endpoint of both
        %                line segments, then the point of intersection is
        %                not an interior point, hence no intersection.
        if ((((L1X1 == intersectionX) && (L1Y1 == intersectionY)) || ((L1X2 == intersectionX) && (L1Y2 == intersectionY))) && ...
            (((L2X1 == intersectionX) && (L2Y1 == intersectionY)) || ((L2X2 == intersectionX) && (L2Y2 == intersectionY))))

            linesIntersect = 0;
            pointOfIntersection = [NaN, NaN];
            return;

        end
        
%         % Corner Case 2: If the intersection point is an endpoint of one
%         %                line segments, but an interior point on the other
%         %                line segment then the point of intersection is
%         %                an interior point, hence an intersection.
%         if (((L1X1 == intersectionX) && (L1Y1 == intersectionY)) || ...
%             ((L1X2 == intersectionX) && (L1Y2 == intersectionY)) || ...
%             ((L2X1 == intersectionX) && (L2Y1 == intersectionY)) || ...
%             ((L2X2 == intersectionX) && (L2Y2 == intersectionY)))
% 
%             linesIntersect = 1;
%             return;

%         end
    end

end
