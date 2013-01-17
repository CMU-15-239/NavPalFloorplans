package edu.cmu.ri.rcommerce.sensor;

import java.util.ArrayList;
import java.util.List;

import edu.cmu.ri.rcommerce.Messages.WifiInfo;
import edu.cmu.ri.rcommerce.Messages.WifiScan;

class WifiData
{
	long BSSID;
	String SSID;
	int level;
	int frequency;
	int channel;


	static WifiData decodeFromProtoBufMessage(WifiInfo in)
	{
		WifiData out = new WifiData();
		out.BSSID = in.getBSSID();
		out.channel = in.getChannel();
		out.frequency = in.getFrequency();
		out.level = in.getLevel();
		out.SSID = in.getSSID();
		return out;
	}
	
	static List<WifiData> decodeFromProtoBufMessage(WifiScan in)
	{
		List<WifiData> out = new ArrayList<WifiData>();
		
		for (int i=0; i<in.getScanCount(); i++)
		{
			out.add(decodeFromProtoBufMessage(in.getScan(i)));
		}
		return out;
	}
}