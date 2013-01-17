package edu.cmu.ri.rcommerce.sensor;

import edu.cmu.ri.rcommerce.Messages.GSMScan;
import edu.cmu.ri.rcommerce.Messages.WifiScan;

/** Data structure for holding signal strength readings that is agnostic to source (either Wifi or GSM) */
public final class RSSIReading {
	public final float[] RSSI;
	public final long[]  beacon;
	public final long timestamp;
	public final int sensorType;
	
	public static final int WIFI_RSSI = 0, GSM_RSSI = 1;
	
	public RSSIReading(long timestamp,long[] beacons, float[] RSSI, int sensorType) {
		this.timestamp = timestamp;
		this.beacon = beacons;
		this.RSSI = RSSI;
		this.sensorType = sensorType;
	}
	
	public static RSSIReading fromWifiScan(WifiScan in)
	{
		long[] beacons = new long[in.getScanCount()];
		float[] RSSI = new float[in.getScanCount()];
		
		for (int i=1; i<in.getScanCount(); i++)
		{
			beacons[i] = in.getScan(i).getBSSID();
			RSSI[i] = in.getScan(i).getLevel();
		}
		return new RSSIReading(in.getTimestamp(), beacons, RSSI, WIFI_RSSI);
	}
	
	public static RSSIReading fromGsmScan(GSMScan in)
	{
		long[] beacons = new long[in.getScanCount()];
		float[] RSSI = new float[in.getScanCount()];
		
		for (int i=1; i<in.getScanCount(); i++)
		{
			beacons[i] = in.getScan(i).getCellID();
			RSSI[i] = in.getScan(i).getSignalStrength();
		}
		return new RSSIReading(in.getTimestamp(), beacons, RSSI, GSM_RSSI);
	}
}
