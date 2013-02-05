package edu.cmu.ri.rcommerce.sensor;

import java.util.Comparator;


public class DistanceToPointComparator implements Comparator<CorrelatedSignalValues> {
	double x,y;
	public DistanceToPointComparator(double x, double y) {
		this.x = x;
		this.y = y;
	}
	@Override
	public int compare(CorrelatedSignalValues p1, CorrelatedSignalValues p2) {
		double distanceSquared1 = distanceSquared(x, y, p1.x, p1.y);
		double distanceSquared2 = distanceSquared(x, y, p2.x, p2.y);
		
		if (distanceSquared1 == distanceSquared2)
			return 0;
		else if (distanceSquared1 < distanceSquared2)
			return -1;
		else
			return 1;
	}
	
	double distanceSquared(double x1,double y1,double x2,double y2)
	{
		return Math.pow(x2-x1, 2) + Math.pow(y2-y1, 2);
	}

}
