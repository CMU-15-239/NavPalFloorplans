%take a dumped map file as an input and return a struct array of readings
function [readings] = ImportMap(mapfile)
    fileID = fopen(mapfile);
   
    %read the number of data points
    numPoints = textscan(fileID,'%u *[^\n]');
    numPoints = numPoints{1};
    
    readings = struct;
    
    for (i = 1:numPoints)
        %read the time and x,y coordinates
        scan = textscan(fileID, '%u64 %f %f');
        
        readings(i).time = scan{1};
        readings(i).x = scan{2};
        readings(i).y = scan{3};
        
        %read the wifi metainfo if present (number of wifi scans)
        wifiMeta(i) = textscan(fileID, ' WifiScan %u');

        if (wifiMeta{i}(1) > 0)
            C = textscan(fileID, '%d64 %d64', wifiMeta{i}(1));
            readings(i).wifiInfo = cell2mat(C)';
        end

        %get number of gsm readings
        gsmMeta(i) = textscan(fileID, ' GSMScan %u');

        %extract gsm readings
        if (gsmMeta{i}(1) > 0)
            readings(i).gsmInfo = cell2mat(textscan(fileID, '%d64 %d64 %d64', gsmMeta{i}(1)))';
        end

        %go to end of line
        textscan(fileID,'*[^\n]');
    end

    fclose(fileID);
end