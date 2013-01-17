function [unitVector1, unitVector2] = f_ComputePerpendicularLine(point1, point2)

    a0 = point1(1);
    a1 = point1(2);
    b0 = point2(1);
    b1 = point2(2);

    rise  = b1 - a1;
    run   = b0 - a0;

    unitVector1 = [-rise, run, 0];
    unitVector2 = [rise, -run, 0];

end
