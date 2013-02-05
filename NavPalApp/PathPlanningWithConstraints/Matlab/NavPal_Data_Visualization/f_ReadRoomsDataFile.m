function [rooms, numberOfRooms, maxCoord, minCoord] = f_ReadRoomsDataFile(roomsDataFile)

    %% Get the file handle
    fid = fopen(roomsDataFile, 'r');

    % Throw an error if the file could not be opened
    if (fid == -1)
        error('The file %s could not be opened. Make sure the path and filename is correct.', roomsDataFile);
    end

    maxCoordinates = {};
    minCoordinates = {};
    
    rooms = {};
    numberOfRooms = 1;
    
    %% Read each line of the data file and store each rooms points in a 
 
    % NOTE: It was not clear to me how to vectorize this operation to
    %       reduce the runtime of this section. If you can think of a better way
    %       to do this, feel free to change it.
    while ~feof(fid)

        %% Tokenize the current line from the data file
        
        % Read the next line of data file as a string
        tline = fgetl(fid);

        % Tokenize the line of the datafile using spaces as the delimiter.
        % NOTE: Elements in the tokens vector is a Nx1 cell array of
        %       strings. The function get_tokens was taken from the
        %       following link:
        %
        %           http://www.mathworks.com/matlabcentral/fileexchange/17999-parse-strings-using-delimiters/content/get_tokens.m
        %
        tokens = get_tokens(tline, ' ');
        
        %% Extract the room type and name

        roomType = tokens{1};
        roomName = tokens{2};
        
        %% Extract the points that represent the shape of the room
        points   = tokens(3:end-1, :);
        points = str2double(points);        

        %% Reshape the matrix so that it is a Nx2 matrix where column 1 represents x values and column 2 represents y values
        points = reshape(points, 2, [])';

        rooms{numberOfRooms} = createRoomStruct(roomType, roomName, points);
        
        maxCoordinates{numberOfRooms} = max(max(points));
        minCoordinates{numberOfRooms} = min(min(points));
        
        numberOfRooms = numberOfRooms + 1;
        
    end

    numberOfRooms = numberOfRooms - 1;
    maxCoord = max(cell2mat(maxCoordinates));
    minCoord = min(cell2mat(minCoordinates));

    fclose(fid);

end

function roomStruct = createRoomStruct(type, name, points)

    roomStruct = struct('Type', type,...
                        'Name', name,...
                        'Points', points);
end