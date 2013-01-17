package edu.cmu.ri.rcommerce.sensor;

import java.util.ArrayList;
import java.util.List;

import edu.cmu.ri.rcommerce.particleFilter.Particle2D;

/** dummy provider of RSSI calibration data */
public class TestingRSSICalibrateProvider implements RSSICalibrateProvider<Particle2D> {

	float AP1_X_Pos = 5;
	float AP1_Y_Pos = 5;
	
	float AP2_X_Pos = 5;
	float AP2_Y_Pos = 1;
	
	@Override
	//for testing purposes, implements a simple setup with an access point at a fixed location which has a linear propagation rate in dB
	public List<RSSIReading> getExpectedReadings(Particle2D particle) {
		float distanceToAP1 = distance(particle.x,particle.y,AP1_X_Pos,AP1_Y_Pos);
		float dB1 = -distanceToAP1 * 10;
		
		float distanceToAP2 = distance(particle.x,particle.y,AP2_X_Pos,AP2_Y_Pos);
		float dB2 = -distanceToAP2 * 10;
		
		RSSIReading r = new RSSIReading(System.currentTimeMillis(), new long[]{5,6}, new float[]{dB1,dB2}, RSSIReading.WIFI_RSSI);
		List<RSSIReading> out = new ArrayList<RSSIReading>();
		out.add(r);
		return out;
	}
	
	float distance(float x1, float y1, float x2, float y2)
	{
		return (float)Math.sqrt((x1-x2) * (x1-x2) + (y1-y2) * (y1-y2));
	}

	@Override
	public float getDistanceToNearestCalibrationPoint(Particle2D particle) {
		float distance1 = distance(AP1_X_Pos, AP1_Y_Pos, particle.x, particle.y);
		float distance2 = distance(AP2_X_Pos, AP2_Y_Pos, particle.x, particle.y);
		
		return Math.min(distance1, distance2);
	}
}
