function f_DrawPoints(pointList, displayIds, MarkerFaceColor)
%
% Display a set of points in the current figure. The marker size is 5, the
% marker edge color is black and the line width of the market edge is 1.
% Future versions of this function should include the following additional
% parameters:
%
%   MarkerEdgeColor
%   LineWidth
%   MarkerSize
%
% Parameters:
%
%   pointList - The set of points to display that has the form shown below.
%
%       [uniqueId, x-coordinate, y-coordinate]
%
%       Example:
%
%           pointList = [1, 10, 20;
%                        2, 12, 4;
%                        3, 1, 23];
%
%   displayIds - Boolean value that indicates whether or not to display the
%                point Ids. A value of 1 (True) will display the Ids and a
%                value of 0 (false) will not display the Ids.
%
% Returns:
%
%   (none)
%

    % The offset to display point labels
    xOffset             = 1.0;
    yOffset             = 1.0;

    hold on

    % Display the waypoints in the plot along with the waypoint ID
    plot(pointList(:, 2), pointList(:, 3), 'o', 'MarkerEdgeColor', 'k', 'MarkerFaceColor', MarkerFaceColor, 'LineWidth', 1, 'MarkerSize', 5);
    
    % Display point IDs if specified
    if (displayIds)

        text(pointList(:, 2) + xOffset, pointList(:, 3) + yOffset, num2str(pointList(:, 1)), 'fontsize', 10, 'fontweight', 'light');

    end

    hold off
