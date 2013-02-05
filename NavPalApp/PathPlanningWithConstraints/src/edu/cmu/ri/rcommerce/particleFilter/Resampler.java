package edu.cmu.ri.rcommerce.particleFilter;

import java.util.List;

/** Rebalances the distribution of particles to reduce variance */
public interface Resampler<P extends Particle>{

	/**
	 * 
	 * @param weightedState The current list of particles. The contents of the list may be modified by resample()
	 * @return  A list of resampled particles. May be the same object as the input (or a different one)
	 */
	List<P> resample(List<P> weightedState);

}
