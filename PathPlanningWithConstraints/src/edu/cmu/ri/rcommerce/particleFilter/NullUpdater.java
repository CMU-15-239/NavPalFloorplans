package edu.cmu.ri.rcommerce.particleFilter;

import java.util.List;
/**
 * Trivial implementation of an updater for testing purposes.
 * @author Nisarg
 */
public class NullUpdater<P extends Particle> implements Updater<P> {

	@Override
	public List<P> update(List<P> state) {
		return state;
	}

}
