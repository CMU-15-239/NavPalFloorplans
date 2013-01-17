package edu.cmu.ri.rcommerce.sensor;

import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Compares RSSI measurements for similarity
 * Properties of the metric are that any measurements with beacons in common will be closer than completely disjoint measurements
 * The number of beacons in common is the primary measure of similarity, with correspondence in values as a secondary factor
 */
public class RSSIDistanceMetric 
{
	//static final float alpha=0,beta=1,gamma=1,epsilon=1;
	static final float alpha=0,beta=1,gamma=1,epsilon=.1f;
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

		
		float sumofSquaredDiffs  = 0;
		for (Long beacon: readingsInCommon)
			sumofSquaredDiffs += Math.pow((readings1.get(beacon) - readings2.get(beacon)),2);
		
		float sumofDisjoints = 0;
		for (Long beacon: readings1.keySet())
		{
			if (readingsInCommon.contains(beacon))
				continue;
			sumofDisjoints += readings1.get(beacon);
		}
		for (Long beacon: readings2.keySet())
		{
			if (readingsInCommon.contains(beacon))
				continue;
			sumofDisjoints += readings2.get(beacon);
		}
		
		float distance = (alpha*sumofDisjoints + beta*sumofSquaredDiffs) / (gamma * readingsInCommon.size() + epsilon); 
		return distance;	
	}

}
