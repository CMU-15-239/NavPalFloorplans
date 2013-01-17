%calculates the magnitudes of the differences between two wifi signals that
%have corresponding entries
function norms = CorrelatedWifiNorms(wifi1,wifi2)
for i=1:length(wifi1)
    norms(i) = norm(cell2mat(wifi1{i,2}(:,4))-cell2mat(wifi2{i,2}(:,4)));
end
end