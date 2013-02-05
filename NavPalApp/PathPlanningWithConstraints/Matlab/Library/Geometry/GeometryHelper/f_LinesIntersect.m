function [linesIntersect, pointOfIntersection] = f_LinesIntersect(L1X1, L1Y1, L1X2, L1Y2, L2X1, L2Y1, L2X2, L2Y2)

    % Denominator for ua and ub are the same, so store this calculation
    d = (L2Y2 - L2Y1) * (L1X2 - L1X1) - (L2X2 - L2X1) * (L1Y2 - L1Y1);

    % n_a and n_b are calculated as seperate values for readability
    n_a = (L2X2 - L2X1) * (L1Y1 - L2Y1) - (L2Y2 - L2Y1) * (L1X1 - L2X1);

    n_b = (L1X2 - L1X1) * (L1Y1 - L2Y1) - (L1Y2 - L1Y1) * (L1X1 - L2X1);

    % Make sure there is not a division by zero - this also indicates that
    % the lines are parallel.  
    % If n_a and n_b were both equal to zero the lines would be on top of each 
    % other (coincidental).  This check is not done because it is not 
    % necessary for this implementation (the parallel check accounts for this).
    if (d == 0)

        linesIntersect = 0; % Intersection False
        pointOfIntersection = [NaN, NaN];
        return;

    end

    % Calculate the intermediate fractional point that the lines potentially intersect.
    ua = n_a / d;
    ub = n_b / d;

    % The fractional point will be between 0 and 1 inclusive if the lines
    % intersect.  If the fractional calculation is larger than 1 or smaller
    % than 0 the lines would need to be longer to intersect.
    pointOfIntersection = zeros(1,2);
    if (ua >= 0 && ua <= 1 && ub >= 0 && ub <= 1)
        
        pointOfIntersection(1) = L1X1 + (ua * (L1X2 - L1X1));
        pointOfIntersection(2) = L1Y1 + (ua * (L1Y2 - L1Y1));

        linesIntersect = 1; % Intersection True 
%         return;
    else
        linesIntersect = 0; % Intersection False
        pointOfIntersection = [NaN, NaN];
    end


end
