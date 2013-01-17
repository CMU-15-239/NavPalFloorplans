%extract all wifi signal strength readings correlated with position
function [finalReadings] = extractGSM(readings)
allGSM = [readings.gsmInfo];
uniqueGSM = unique(allGSM(2,:));

finalReadings = cell(0,2);
o=0;
for currentBeacon = uniqueGSM(1:end)
    results = zeros(0,3);
    p = 0;
    for i=1:length(readings)
        [r,c,v] = find(readings(i).gsmInfo(2,:) == currentBeacon);
        numMatches = size(c);
        if (numMatches(2) == 0)
            continue;
        end
        gsmInfo = readings(i).gsmInfo;
        p = p+1;
        gsmReading = gsmInfo(3,c);
        if (gsmReading == 99)
            gsmReading = 0;
        end
        results(p,:) = [readings(i).x readings(i).y gsmReading];
    end
    o = o + 1;
    finalReadings(o,:) = {currentBeacon results};
end

end