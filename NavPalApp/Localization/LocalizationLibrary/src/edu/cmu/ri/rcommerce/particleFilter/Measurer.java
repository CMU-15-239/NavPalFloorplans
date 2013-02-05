package edu.cmu.ri.rcommerce.particleFilter;

import java.util.List;

/** Given a list of particles, updates the likelihood weights of each of them based on new information */
public interface Measurer<P extends Particle>{

	/** Update likelihood weights. The weights don't have to be normalized.
	 * @param updatedState The current list of particles. The contents of the list may be modified by updateWeights.
	 * @return A list of particles with updated weights. May be the same object as the input (or a different one)
	 */
	List<P> updateWeights(List<P> updatedState);

	/**
	 * After updateWeights has been run, this indicates whether a resample step should be run
	 * @return boolean indicating whether a resample step is recommended
	 */
	boolean shouldResample();

}
