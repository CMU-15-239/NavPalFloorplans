package edu.cmu.ri.rcommerce.particleFilter;

import java.util.ArrayList;
import java.util.List;

import android.net.wifi.ScanResult;
import edu.cmu.ri.rcommerce.sensor.RSSIReading;
import edu.cmu.ri.rcommerce.sensor.RSSIRuntimeProvider;

/**
 * Provides a real-time stream of wifi signal strength readings
 * @author Evan
 *
 */
public class WifiRSSIRuntimeProvider implements RSSIRuntimeProvider {
	boolean newReading;
	List<ScanResult> results;
	public static final int WIFI_RSSI = 0;

	public WifiRSSIRuntimeProvider() {
		newReading = false;
	}

	@Override
	public boolean newReadingAvailable() {
		return newReading;
	}

	@Override
	public RSSIReading getCurrentReading() {
		newReading = false;
		List<ScanResult> staticResults = new ArrayList<ScanResult>(results);
		// must be done, otherwise the WiFi system will keep updating the
		// current ScanResult list and the values will change while these lists
		// are being pulled
		long[] beacons = new long[staticResults.size() + 1];
		float[] RSSI = new float[staticResults.size() + 1];
		for (int i = 1; i < beacons.length; i++) {
			ScanResult r = staticResults.get(i - 1);
			beacons[i] = parseBSSID(r.BSSID);
			RSSI[i] = r.level;
		}
		return new RSSIReading(System.nanoTime(), beacons, RSSI, WIFI_RSSI);
	}

	public void setNewReading(List<ScanResult> wifiScan) {
		newReading = true;
		results = new ArrayList<ScanResult>(wifiScan);
	}

	// Input is a string in the format of an Ethernet MAC address, e.g.,
	// XX:XX:XX:XX:XX:XX where each X is a hex digit.
	// Output is a long with the lower 48 bits corresponding to the input, and
	// the upper bits 0
	private long parseBSSID(String in) {
		String hex = in.substring(0, 2) + in.substring(3, 5) + in.substring(6, 8) + in.substring(9, 11) + in.substring(12, 14) + in.substring(15, 17);
		return Long.parseLong(hex, 16);
	}

}
