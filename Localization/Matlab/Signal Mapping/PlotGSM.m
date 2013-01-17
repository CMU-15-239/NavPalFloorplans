clf
gsm = ExtractGSM(readings);
hold on
col=hsv(length(gsm));
for i = 1:length(gsm)
    m = gsm(i,2);
    plot3(m{1}(:,1),m{1}(:,2),m{1}(:,3),'d','color',col(i,:));
end