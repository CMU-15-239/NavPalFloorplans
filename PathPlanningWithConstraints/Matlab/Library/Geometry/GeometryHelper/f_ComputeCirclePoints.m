function [X, Y] = f_ComputeCirclePoints(center, radius, numberOfPoints)
    %---------------------------------------------------------------------------------------------
    % [X, Y] = CIRCLE(CENTER,RADIUS,NOP)
    %
    % This routine computes the points of a circle with
    % center defined as a vector CENTER, radius as a
    % scaler RADIS. numberOfPoints is the number of
    % points on the circle. Returns the X and Y
    % components of each point as seperate vectors.
    %
    %   Usage Examples,
    %
    %   circle([1,3], 3, 1000); 
    %   circle([2,4], 2, 1000);
    %
    %---------------------------------------------------------------------------------------------

    if (nargin <3),
        error('Please see help for INPUT DATA.');
    end

    THETA=linspace(0, 2*pi, numberOfPoints);
    RHO=ones(1, numberOfPoints)*radius;
    [X, Y] = pol2cart(THETA, RHO);
    X= X + center(1);
    Y= Y + center(2);
