package edu.cmu.ri.rcommerce.particleFilter;

import java.util.List;

/** Evolves the model of the world forward in time in the absence of new observations */ 
public interface Updater<P extends Particle>
{
	/** For each particle, evolve its state by sampling from the distribution of new states
	 * @param state The current list of particles. The contents of the list may be modified by update().
	 * @return A list of particles with updated values. May be the same object as the input (or a different one)
	 */
	public List<P> update(List<P> state);
}
