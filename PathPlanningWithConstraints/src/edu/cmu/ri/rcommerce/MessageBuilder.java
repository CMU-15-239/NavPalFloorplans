package edu.cmu.ri.rcommerce;

import java.util.List;

import android.net.wifi.ScanResult;
import android.telephony.NeighboringCellInfo;
import android.telephony.gsm.GsmCellLocation;
import edu.cmu.ri.rcommerce.Messages.GSMInfo;
import edu.cmu.ri.rcommerce.Messages.GSMScan;
import edu.cmu.ri.rcommerce.Messages.WifiInfo;
import edu.cmu.ri.rcommerce.Messages.WifiScan;

/**
 * Converts android specific gsm and wifi data structures into the log format.
 * @author Nisarg
 *
 */
public class MessageBuilder {

	public static GSMScan buildGSMScanMessage(final int signalStrength, GsmCellLocation cellLocation, final List<NeighboringCellInfo> neighborInfo)
	{
		GSMScan.Builder scanInfoBuilder = GSMScan.newBuilder();
    	scanInfoBuilder.setTimestamp(System.currentTimeMillis());
    	
    	GSMInfo.Builder currentTowerMessageBuilder = GSMInfo.newBuilder()
    		.setLocationAreaCode(cellLocation.getLac())
    		.setCellID(cellLocation.getCid())
    		.setSignalStrength(signalStrength);
    		//Could be in future versions, but currently won't work well until API phones catch up
    		//.setPsc(cellLocation.getPsc());
    	
    	scanInfoBuilder.addScan(currentTowerMessageBuilder.build());
    	
    	for (NeighboringCellInfo result : neighborInfo)
    	{
    		GSMInfo.Builder gsmMessageBuilder = GSMInfo.newBuilder()
    			.setLocationAreaCode(result.getLac())
    			.setCellID(result.getCid())
    			.setSignalStrength(result.getRssi())
    			.setPsc(result.getPsc());
    		
    		scanInfoBuilder.addScan(gsmMessageBuilder.build());
    	}
    	
    	GSMScan message = scanInfoBuilder.build();
    	return message;
	}
	
	public static WifiScan buildWifiScanMessage(List<ScanResult> wifiResults)
	{
		WifiScan.Builder scanInfoBuilder = WifiScan.newBuilder();
    	scanInfoBuilder.setTimestamp(System.currentTimeMillis());
    	
    	for (ScanResult result : wifiResults)
    	{
    		WifiInfo.Builder wifiMessageBuilder= WifiInfo.newBuilder();
    		wifiMessageBuilder.setBSSID(parseBSSID(result.BSSID));
    		wifiMessageBuilder.setFrequency(result.frequency);
    		wifiMessageBuilder.setLevel(result.level);
    		wifiMessageBuilder.setSSID(result.SSID);
    		
    		scanInfoBuilder.addScan(wifiMessageBuilder.build());
    	}
    	WifiScan message = scanInfoBuilder.build();
    	return message;
	}
	
    //Input is a string  in the format of an Ethernet MAC address, e.g., XX:XX:XX:XX:XX:XX where each X is a hex digit. 
    //Output is a long with the lower 48 bits corresponding to the input, and the upper bits 0
    static long parseBSSID(String in)
    {
    	String hex = in.substring(0, 2) + in.substring(3, 5) + in.substring(6,8) + in.substring(9,11) + in.substring(12,14) + in.substring(15,17);
    	return Long.parseLong(hex,16);
    }
	
}
