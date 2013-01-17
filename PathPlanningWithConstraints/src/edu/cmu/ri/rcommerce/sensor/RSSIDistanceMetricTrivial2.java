package edu.cmu.ri.rcommerce.sensor;

import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Compares RSSI measurements for similarity
 * Looks at the number of beacons in common and penalizes for extra beacons
 */
public class RSSIDistanceMetricTrivial2 
{
	public static float distanceBetween(RSSIReading r1, RSSIReading r2)
	{
		SortedMap<Long, Float> readings1 = new TreeMap<Long, Float>();
		SortedMap<Long, Float> readings2 = new TreeMap<Long, Float>();

		for (int i = 0 ; i<r1.beacon.length; i++)
			readings1.put(r1.beacon[i], r1.RSSI[i]);
		
		for (int i = 0 ; i<r2.beacon.length; i++)
			readings2.put(r2.beacon[i], r2.RSSI[i]);
		
	
		Set<Long> readingsInCommon = new TreeSet<Long>(readings1.keySet());
		readingsInCommon.retainAll(readings2.keySet());
		
		Set<Long> readingsDisjoint = new TreeSet<Long>(readings1.keySet());
		readingsDisjoint.addAll(readings2.keySet());
		Set<Long> tmp = new TreeSet<Long>(readings1.keySet());
		tmp.retainAll(readings2.keySet());
		readingsDisjoint.removeAll(tmp);

		
		if (readingsInCommon.size() > 0)
			return readingsDisjoint.size()/(2*readingsInCommon.size());
		else
			return 10000;
		

	}

}
