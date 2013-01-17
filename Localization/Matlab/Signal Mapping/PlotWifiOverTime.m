function PlotWifiOverTime(extractedWifi,color)
numBeacons = length(extractedWifi);
%clf;
hold on;

for i=1:numBeacons
    time = cell2mat(extractedWifi{i,2}(:,1));
    strength = cell2mat(extractedWifi{i,2}(:,4));
    plot(time,strength,color);
end
end