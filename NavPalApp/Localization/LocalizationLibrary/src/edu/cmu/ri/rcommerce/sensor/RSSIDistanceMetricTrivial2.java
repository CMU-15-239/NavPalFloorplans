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
		RSSIReading r1local = r1;
		RSSIReading r2local = r2;
		int len1 = r1local.beacon.length;
		for (int i = 0 ; i<len1; i++)
			readings1.put(r1local.beacon[i], r1local.RSSI[i]);
		int len2 = r2local.beacon.length;
		for (int i = 0 ; i<len2; i++)
			readings2.put(r2local.beacon[i], r2local.RSSI[i]);
		
		Set<Long> readings1KeySet = readings1.keySet();
		Set<Long> readings2KeySet = readings2.keySet();
		Set<Long> readingsInCommon = new TreeSet<Long>(readings1KeySet);
		readingsInCommon.retainAll(readings2KeySet);
		
		Set<Long> readingsDisjoint = new TreeSet<Long>(readings1KeySet);
		readingsDisjoint.addAll(readings2KeySet);
		/*Set<Long> tmp = new TreeSet<Long>(readings1.keySet());
		tmp.retainAll(readings2.keySet());*/
		readingsDisjoint.removeAll(readingsInCommon);

		
		if (readingsInCommon.size() > 0)
			return readingsDisjoint.size()/(2*readingsInCommon.size());
		else
			return 10000;
		

	}

}
