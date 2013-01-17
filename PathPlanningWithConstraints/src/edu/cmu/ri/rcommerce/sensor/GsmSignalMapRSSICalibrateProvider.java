package edu.cmu.ri.rcommerce.sensor;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.cmu.ri.rcommerce.particleFilter.Particle2D;

/** Stores a map of GSM signal strengths over space, which can be queried for the signal strengths expected at any point.
 * At the moment, returns the strengths of the nearest calibration point as the prediction
 * @author Nisarg
 */
public class GsmSignalMapRSSICalibrateProvider implements RSSICalibrateProvider<Particle2D> {
	List<CorrelatedSignalValues> data;
	public GsmSignalMapRSSICalibrateProvider(BufferedReader input) throws IOException {
		data = new ArrayList<CorrelatedSignalValues>();
		
		while(true)
		{
			String line = input.readLine();
			if (line == null)
				break;
			data.add(CorrelatedSignalValues.fromString(line));
			
		}
	}
	@Override
	//TODO use interpolation and more efficient data structures
	//Currently searches for the calibration point nearest to the particle, and returns that reading
	public List<RSSIReading> getExpectedReadings(Particle2D particle) {
		double minDistance = Double.MAX_VALUE;
		CorrelatedSignalValues closestDataPoint = null;
		
		for (CorrelatedSignalValues v : data)
		{
			double distanceSquared = distanceSquared(v.x, v.y, particle.x, particle.y);
			if ( distanceSquared< minDistance)
			{
				minDistance = distanceSquared;
				closestDataPoint = v;
			}
		}
		List<RSSIReading> out = new ArrayList<RSSIReading>();
		out.add(closestDataPoint.toRSSIReadingGSM());
		return out;
		
	}
	
	double distanceSquared(double x1,double y1,double x2,double y2)
	{
		return Math.pow(x2-x1, 2) + Math.pow(y2-y1, 2);
	}
	@Override
	public float getDistanceToNearestCalibrationPoint(Particle2D particle) {
		double minDistance = Double.MAX_VALUE;
		for (CorrelatedSignalValues v : data)
		{
			double distanceSquared = distanceSquared(v.x, v.y, particle.x, particle.y);
			if ( distanceSquared< minDistance)
			{
				minDistance = distanceSquared;
			}
		}
		return (float) Math.sqrt(minDistance);
	}
	

}
