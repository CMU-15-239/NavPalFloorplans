package edu.cmu.ri.rcommerce.sensor;

import java.util.List;

import edu.cmu.ri.rcommerce.particleFilter.Measurer;
import edu.cmu.ri.rcommerce.particleFilter.Particle;

/** Updates probability of particles based on new signal strength readings */
public class RSSIMeasurer<P extends Particle> implements Measurer<P>{

	public RSSIRuntimeProvider runtimeProvider;
	RSSICalibrateProvider<P> calibrateProvider;
	boolean shouldResample = false;
	
	//scales the magnitude range of pre-normalized readings
	//larger epsilons have the effect of giving less credence to very close signal strength matches
	//must be nonzero to prevent division by 0 errors
	float epsilon = 0.005f;
	
	public RSSIMeasurer(RSSIRuntimeProvider runtimeProvider, RSSICalibrateProvider<P> calibrateProvider,float epsilon)
	{
		this.runtimeProvider = runtimeProvider;
		this.calibrateProvider = calibrateProvider;
		this.epsilon = epsilon;
	}
	
	@Override
	public List<P> updateWeights(List<P> updatedState) {
		if (!runtimeProvider.newReadingAvailable())
		{
			shouldResample = false;
			return updatedState;
		}
		
		RSSIReading newReading = runtimeProvider.getCurrentReading();
		RSSIReading expectedReading;
		for (P particle: updatedState)
		{
			List<RSSIReading> l = calibrateProvider.getExpectedReadings(particle);
			float signalSpaceDistance = 0;
			for (RSSIReading expected : l)
				signalSpaceDistance +=RSSIDistanceMetricTrivial2.distanceBetween(newReading, expected);
			if (l.size() == 0)
				signalSpaceDistance = 10000;
			else
				signalSpaceDistance /= l.size();
			float calibrationPointDistance = calibrateProvider.getDistanceToNearestCalibrationPoint(particle);
			particle.weight = 1.0f / (epsilon + signalSpaceDistance);
		}
		
		shouldResample = true;
		return updatedState;
			
	}

	@Override
	public boolean shouldResample() {
		return shouldResample;
	}

}
