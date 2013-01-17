package edu.cmu.ri.rcommerce.particleFilter;

import java.util.List;
/**
 * Trivial implementation of a resampler for testing purposes.
 * @author Nisarg
 */
public class NullResampler<P extends Particle> implements Resampler<P> {

	@Override
	public List<P> resample(List<P> weightedState) {
		return weightedState;
	}

}
