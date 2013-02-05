function f_DriverDilatePolygon(magnitude)
%
% This is the method used for testing the dilate polygon function. Call
% this method direcly in the Matlab command prompt passing it the
% parameters defined below. The result of calling this function will be a
% figure that displays the original polygon (red) and the dilated polygon
% (green).
%
% Parameters
%
%       magnatude - a positive number used by which to dilate the polygon 
%

    %
    % Define a simple polygon
    %

    % Polygon 1
    polygon = [15 15 0; 15 45 0; 45 45 0; 45 15 0; 15 15 0];

    % Polygon 2
    %polygon = [10 10 0; 30 40 0; 10 70 0; 30 90 0; 50 70 0; 70 90 0; 80 90 0; 90 50 0; 50 50 0; 50 30 0; 60 10 0; 10 10 0];

    % Polygon 3
    %polygon = [30 20 0; 90 60 0; 70 20 0; 30 20 0];

    % Polygon 4
    %polygon = [80 10 0; 40 10 0; 20 40 0; 20 90 0; 50 70 0; 90 90 0; 40 60 0; 50 20 0; 80 10 0];

    % Polygon 5
    %polygon = [1 -1 0; 1 0 0; 1 1 0; 0 1 0; -1 1 0; -1 0 0; -1 -1 0; 0 -1 0; 1 -1 0];

    % Polygon 6
    %polygon = [10 20 0; 30 40 0; 11 35 0; 5  45 0; 10 20 0];


    [dilatedPolygon] = f_DilatePolygon(polygon, magnitude);
    
    hold on

    f_DrawPolygon(polygon, 'r');
    f_DrawPolygon(dilatedPolygon, 'g');

    %plot(midPoints(:, 1), midPoints(:, 2), 'o', 'MarkerEdgeColor', 'k', 'MarkerFaceColor', 'w', 'LineWidth', 1, 'MarkerSize', 2);
    plot(polygon(:, 1), polygon(:, 2), 'o', 'MarkerEdgeColor', 'k', 'MarkerFaceColor', 'g', 'LineWidth', 1, 'MarkerSize', 2);
    plot(dilatedPolygon(:, 1), dilatedPolygon(:, 2), 'o', 'MarkerEdgeColor', 'k', 'MarkerFaceColor', 'r', 'LineWidth', 1, 'MarkerSize', 4);

    hold off

end
