package edu.cmu.ri.rcommerce.sensor;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

class CorrelatedSignalValues
{
	long timestamp;
	double x,y;
	List<WifiData> wifiReadings;
	List<GSMData> gsmReadings;
	
	private long[] getWifiBeacons()
	{
		long[] out = new long[wifiReadings.size()];
		for(int i=0; i<wifiReadings.size();i++)
			out[i] = wifiReadings.get(i).BSSID;
		return out;
	}
	private float[] getWifiRSSIs()
	{
		float[] out = new float[wifiReadings.size()];
		for(int i=0; i<wifiReadings.size();i++)
			out[i] = wifiReadings.get(i).level;
		return out;
	}
	private long[] getGSMBeacons()
	{
		long[] out = new long[gsmReadings.size()];
		for(int i=0; i<gsmReadings.size();i++)
			out[i] = gsmReadings.get(i).cellID;
		return out;
	}
	private long[] getGSMPsc()
	{
		long[] out = new long[gsmReadings.size()];
		for(int i=0; i<gsmReadings.size();i++)
			out[i] = gsmReadings.get(i).psc;
		return out;
	}
	private float[] getGSMRSSIs()
	{
		float[] out = new float[gsmReadings.size()];
		for(int i=0; i<gsmReadings.size();i++)
			out[i] = gsmReadings.get(i).signal_strength;
		return out;
	}
	
	RSSIReading toRSSIReadingWifi()
	{
		return new RSSIReading(timestamp, getWifiBeacons(), getWifiRSSIs(), RSSIReading.WIFI_RSSI);
	}
	
	RSSIReading toRSSIReadingGSM()
	{
		//TODO replace this beacons with PSC
		return new RSSIReading(timestamp, getGSMBeacons(), getGSMRSSIs(), RSSIReading.GSM_RSSI);
	}
	
	static CorrelatedSignalValues fromString(String line)
	{
		CorrelatedSignalValues datum = new CorrelatedSignalValues();
		try
		{
			/*Scanner scan = new Scanner(line);
			datum.timestamp = scan.nextLong();
			datum.x = scan.nextDouble();
			datum.y = scan.nextDouble();
			
			scan.next("WifiScan"); //skip WifiScan string
			int numWifiScans = scan.nextInt();
			datum.wifiReadings = new ArrayList<WifiData>();
			for (int i = 0 ; i<numWifiScans; i++)
			{
				WifiData reading = new WifiData();
				reading.BSSID = scan.nextLong();
				reading.level = scan.nextInt();
				datum.wifiReadings.add(reading);
			}
			
			scan.next("GSMScan"); //skip GSMScan string
			int numGSMScans = scan.nextInt();
			datum.gsmReadings = new ArrayList<GSMData>();
			for (int i=0; i<numGSMScans; i++)
			{
				GSMData reading = new GSMData();
				reading.cellID = scan.nextInt();
				reading.signal_strength = scan.nextInt();
				datum.gsmReadings.add(reading);
			}*/
			
			String[] data = line.split(" ");
			datum.timestamp = Long.parseLong(data[0]);
			datum.x = Double.parseDouble(data[1]);
			datum.y = Double.parseDouble(data[2]);
			//skip data[3]
			int numWifiScans = Integer.parseInt(data[4]);
			datum.wifiReadings = new ArrayList<WifiData>();
			for (int i = 0 ; i<numWifiScans; i++)
			{
				WifiData reading = new WifiData();
				reading.BSSID = Long.parseLong(data[5 + i*2]);
				reading.level = Integer.parseInt(data[6 + i*2]);
				datum.wifiReadings.add(reading);
			}
			//skip data[6+ numWifiScans * 2]
			int numGSMScans = Integer.parseInt(data[6 + numWifiScans * 2]);
			datum.gsmReadings = new ArrayList<GSMData>();
			for (int i=0; i<numGSMScans; i++)
			{
				GSMData reading = new GSMData();
				reading.cellID = Integer.parseInt(data[7 + numWifiScans * 2 + i*2]);
				reading.signal_strength = Integer.parseInt(data[8 + numWifiScans * 2 + i*2]);
				datum.gsmReadings.add(reading);
			}
			return datum;
		}
		catch(NoSuchElementException e)
		{
			System.err.println("Error in input file format");
			throw e;
		}
	}
	
}