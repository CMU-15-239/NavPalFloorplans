package edu.cmu.ri.rcommerce.sensor;

import java.util.List;

import android.util.Log;
import edu.cmu.ri.rcommerce.particleFilter.Measurer;
import edu.cmu.ri.rcommerce.particleFilter.Particle;

/** Updates probability of particles based on new signal strength readings */
public class RSSIMeasurer<P extends Particle> implements Measurer<P> {

	public RSSIRuntimeProvider runtimeProvider;
	RSSICalibrateProvider<P> calibrateProvider;
	boolean shouldResample = false;

	// scales the magnitude range of pre-normalized readings
	// larger epsilons have the effect of giving less credence to very close
	// signal strength matches
	// must be nonzero to prevent division by 0 errors
	float epsilon = 0.005f;

	public RSSIMeasurer(RSSIRuntimeProvider runtimeProvider, RSSICalibrateProvider<P> calibrateProvider, float epsilon) {
		this.runtimeProvider = runtimeProvider;
		this.calibrateProvider = calibrateProvider;
		this.epsilon = epsilon;
	}

	@Override
	public List<P> updateWeights(List<P> updatedState) {
		//long lastTime = System.currentTimeMillis();
		//long lastTimeB = System.currentTimeMillis();
		//long lastTimeD = System.currentTimeMillis();
		if (!runtimeProvider.newReadingAvailable()) {
			shouldResample = false;
			return updatedState;
		}
		//Log.i("PROFILING_LOCALIZATION", "Time A:" + (System.currentTimeMillis() - lastTime));
		//lastTime = System.currentTimeMillis();
		RSSIReading newReading = runtimeProvider.getCurrentReading();
		//Log.i("PROFILING_LOCALIZATION", "Time B:" + (System.currentTimeMillis() - lastTime));
		//lastTime = lastTimeB = System.currentTimeMillis();
		// RSSIReading expectedReading;
		for (P particle : updatedState) {
			List<RSSIReading> l = calibrateProvider.getExpectedReadings(particle);
			//Log.i("PROFILING_LOCALIZATION", "Time C:" + (System.currentTimeMillis() - lastTime));
			//lastTime = lastTimeD = System.currentTimeMillis();
			float signalSpaceDistance = 0;
			for (RSSIReading expected : l) {
				signalSpaceDistance += RSSIDistanceMetricTrivial2.distanceBetween(newReading, expected);
				//Log.i("PROFILING_LOCALIZATION", "Time D:" + (System.currentTimeMillis() - lastTime));
				//lastTime = System.currentTimeMillis();
			}
			//Log.i("PROFILING_LOCALIZATION", "Time D TOTAL:" + (System.currentTimeMillis() - lastTimeD));
			//lastTime = System.currentTimeMillis();
			if (l.size() == 0)
				signalSpaceDistance = 10000;
			else
				signalSpaceDistance /= l.size();
			// float calibrationPointDistance =
			// calibrateProvider.getDistanceToNearestCalibrationPoint(particle);
			//Log.i("PROFILING_LOCALIZATION", "Time E:" + (System.currentTimeMillis() - lastTime));
			//lastTime = System.currentTimeMillis();
			particle.weight = 1.0f / (epsilon + signalSpaceDistance);
		}
		//Log.i("PROFILING_LOCALIZATION", "Time B TOTAL:" + (System.currentTimeMillis() - lastTimeB));
		//lastTime = System.currentTimeMillis();

		shouldResample = true;
		return updatedState;

	}

	@Override
	public boolean shouldResample() {
		return shouldResample;
	}

}
