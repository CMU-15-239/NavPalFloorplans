allWifi = [readings.wifiInfo];
uniqueWifi = unique(allWifi(1,:));

allGSM = [readings.gsmInfo];
uniqueGSM = unique(allGSM(2,:));