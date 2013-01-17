function PlotWifi(wifi)
clf
hold on
col=hsv(length(wifi));
for i = 1:length(wifi)
    m = wifi(i,2);
    plot3(m{1}(:,1),m{1}(:,2),m{1}(:,3),'color',col(i,:));
end