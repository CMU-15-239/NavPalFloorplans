package edu.cmu.ri.rcommerce.particleFilter;

import java.util.List;
/**
 * Trivial implementation of a measurer for testing purposes.
 * @author Nisarg
 */
public class NullMeasurer<P extends Particle> implements Measurer<P> {

	@Override
	public List<P> updateWeights(List<P> updatedState) {
		return updatedState;
	}

	@Override
	public boolean shouldResample() {
		return true;
	}

}
