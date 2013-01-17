function interpolated = interpolateWifi(wifi,min,max,numPoints)
interpolated = cell(length(wifi),2);
for i = 1:length(wifi)
    interpolated{i,1} = wifi{i,1};
    signals = wifi{i,2};
    range = linspace(min,max,numPoints);
    interpolated{i,2} = [num2cell(range') num2cell(zeros(numPoints,2)) num2cell(interp1(double(cell2mat(signals(:,1))),double(cell2mat(signals(:,4))),range)')];
end
end