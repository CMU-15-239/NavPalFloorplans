package edu.cmu.ri.rcommerce.sensor;

import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Compares RSSI measurements for similarity
 * Just looks at the number of beacons in common
 */
public class RSSIDistanceMetricTrivial 
{
	public static float distanceBetween(RSSIReading r1, RSSIReading r2)
	{
		SortedMap<Long, Float> readings1 = new TreeMap<Long, Float>();
		SortedMap<Long, Float> readings2 = new TreeMap<Long, Float>();

		for (int i = 0 ; i<r1.beacon.length; i++)
			readings1.put(r1.beacon[i], r1.RSSI[i]);
		
		for (int i = 0 ; i<r2.beacon.length; i++)
			readings2.put(r2.beacon[i], r2.RSSI[i]);
		
		//distance is only calculated on the beacons seen in common
		
		Set<Long> readingsInCommon = new TreeSet<Long>(readings1.keySet());
		readingsInCommon.retainAll(readings2.keySet());

		
		if (readingsInCommon.size() > 0)
			return 1/readingsInCommon.size();
		else
			return 10000;
		

	}

}
