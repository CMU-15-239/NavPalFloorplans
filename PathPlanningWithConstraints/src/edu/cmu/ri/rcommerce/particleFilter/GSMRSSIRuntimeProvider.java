package edu.cmu.ri.rcommerce.particleFilter;

import java.util.ArrayList;
import java.util.List;

import android.telephony.NeighboringCellInfo;
import edu.cmu.ri.rcommerce.sensor.RSSIReading;
import edu.cmu.ri.rcommerce.sensor.RSSIRuntimeProvider;

/**
 * Provides a real-time stream of GSM signal strength readings
 * 
 * @author Evan
 * 
 */
public class GSMRSSIRuntimeProvider implements RSSIRuntimeProvider {
	boolean newReading;
	List<NeighboringCellInfo> results;
	public static final int GSM_RSSI = 0;

	public GSMRSSIRuntimeProvider() {
		newReading = false;
	}

	@Override
	public boolean newReadingAvailable() {
		return newReading;
	}

	@Override
	public RSSIReading getCurrentReading() {
		newReading = false;
		List<NeighboringCellInfo> staticResults = new ArrayList<NeighboringCellInfo>(results);
		// must be done, otherwise the GSM system will keep updating the
		// current ScanResult list and the values will change while these lists
		// are being pulled
		long[] beacons = new long[staticResults.size() + 1];
		float[] RSSI = new float[staticResults.size() + 1];
		for (int i = 1; i < beacons.length; i++) {
			NeighboringCellInfo r = staticResults.get(i - 1);
			beacons[i] = r.getPsc();
			RSSI[i] = r.getRssi();
		}
		return new RSSIReading(System.nanoTime(), beacons, RSSI, GSM_RSSI);
	}

	public void setNewReading(List<NeighboringCellInfo> GSMScan) {
		newReading = true;
		results = new ArrayList<NeighboringCellInfo>(GSMScan);
	}

}
