function f_DrawCircleStrip(circleStrip, radius, NOP, style)
%
% Draws a list of circles
%
% Input Parameters:
%
%   circleStrip - The list of circles to draw. The matrix is an Nx2 where
%                 each row must be in the form shown below.
%
%                       [Circle Center X, Circle Center Y]
%
%                 Ex:
%
%                       circleStrip = [1, 2;
%                                      4, 10;
%                                      12, 4];
%
%   turningRadius - The radius of the circle.
%
%   NOP - Number of points. Since an approximation of a circle is drawn by
%         a number of smaller lines representing the circumference, this 
%         value indicates how many lines to use. A good number to use in
%         most cases is 36.
%
%   style - Use it the same way as you use in in the function PLOT.
%
    numberOfCircle = size(circleStrip, 1);

    for i=1:numberOfCircle

        f_DrawCircle(circleStrip(i, :), radius, NOP, style);

    end

end
