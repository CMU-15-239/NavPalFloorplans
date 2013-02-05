function f_VisualizeRooms(figNum, roomsDataFile)

    addpath('.\..\Library\Geometry\GeometryDrawing');

    % Read the rooms data file
    [rooms, numberOfRooms, maxCoord, minCoord] = f_ReadRoomsDataFile(roomsDataFile);
    
%     % Setup the figure
%     hold on
% 
%     figure(figNum);
%     clf;
%     axis square
% 
%     buffer = 100;
%     axis([minCoord-buffer maxCoord+buffer minCoord-buffer maxCoord+buffer]);
% 
%     hold off
    
    setupFigure(figNum, minCoord, maxCoord, 100);
%     setupFigure(figNum+1, minCoord, maxCoord, 100);

    for i=1:numberOfRooms
        
        roomStruct = rooms{i};
        type   = roomStruct.Type;
        points = roomStruct.Points;
%         name   = roomStruct.Name;
%         disp(name);
        
        if (strcmp('Room', type))
            color = 'r';
            hold on
            figure(figNum);
            f_DrawPolygon(points, color);
            hold off
        elseif (strcmp('Hallway', type))
            color = 'b';
            hold on
            figure(figNum);
            f_DrawPolygon(points, color);
            hold off
        else
            color = 'y';
            hold on
            figure(figNum);
            f_DrawPolygon(points, color);
            hold off
        end

%         disp(points);
        
%         figure(figNum+1);
%         f_DrawPolygon(flipud(points), color);
%         hold off

%         waitforbuttonpress;
        
    end

end

function [fighandle] = setupFigure(figNum, minCoord, maxCoord, buffer)

    % Setup the figure
    hold on

    fighandle = figure(figNum);
    clf;
    axis square

    axis([minCoord-buffer maxCoord+buffer minCoord-buffer maxCoord+buffer]);

    hold off
    
end