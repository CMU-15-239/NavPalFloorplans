package edu.cmu.ri.rcommerce.sensor;

import java.util.List;

import edu.cmu.ri.rcommerce.particleFilter.Particle;

/** Interface for a source of signal strength samples derived at calibration time (as opposed to localization time) */
public interface RSSICalibrateProvider<P extends Particle>{
	
	public List<RSSIReading> getExpectedReadings(P particle);
	public float getDistanceToNearestCalibrationPoint(P particle);

}
