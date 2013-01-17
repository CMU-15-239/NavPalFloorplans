%extract all wifi signal strength readings correlated with position
function [finalReadings] = extractWifi(readings)
allWifi = [readings.wifiInfo];
uniqueWifi = unique(allWifi(1,:));

finalReadings = cell(0,2);
o=0;
for currentBeacon = uniqueWifi(1:end)
    results = cell(0,4);
    p = 0;
    for i=1:length(readings)
        [r,c,v] = find(readings(i).wifiInfo(1,:) == currentBeacon);
        numMatches = size(c);
        if (numMatches(2) == 0)
            continue;
        end
        wifiInfo = readings(i).wifiInfo;
        p = p+1;
        results(p,:) = {readings(i).time readings(i).x readings(i).y wifiInfo(2,c)};
    end
    o = o + 1;
    finalReadings(o,:) = {currentBeacon results};
end

end