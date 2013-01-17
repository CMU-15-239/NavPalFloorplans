package edu.cmu.ri.rcommerce.particleFilter;

import java.util.ArrayList;
import java.util.List;

/**
 * Generic particle filter algorithm. 
 * @author Nisarg
 *
 * @param <P> describes the state space the algorithm is operating in
 */
public class ParticleFilter <P extends Particle>{
	private List<P> currentState;
	public Updater<P> updater;
	public Measurer<P> measurer;
	private Resampler<P> resampler;
	/**
	 * 
	 * @param startingState A list of particles initialized to the best guess of the starting state. 
	 * Often this is just a uniform random distribution over the entire environment.
	 * @param updater Provides estimated future trajectories of particles in the absence of additional measurements. 
	 * @param measurer Reweights particles based on new information about their likelihoods.
	 * @param resampler Removes unlikely particles and provides additional density near
	 *  more likely states to concentrate the effort of the filter.
	 *  
	 * @see {@link DeadReckoningGaussianUpdater}
	 * @see {@link ImportanceResampler} 
	 */
	public ParticleFilter(List<P> startingState, Updater<P> updater, Measurer<P> measurer, Resampler<P> resampler)
	{
		currentState = startingState;
		this.updater = updater;
		this.measurer = measurer;
		this.resampler = resampler;
	}
	
	/**
	 * Updates the world state based on the current state, updater, measurer, and resampler
	 * @return updated world state
	 */
	public List<P> iterate()
	{
		/* assume that functions are destructive to their inputs (for efficiency) */
		List<P> updatedState = updater.update(currentState);
		List<P> weightedState = measurer.updateWeights(updatedState);
		
		List<P> resampledState;
		if (measurer.shouldResample())
			resampledState = resampler.resample(weightedState);
		else
			resampledState = weightedState;
		currentState = resampledState;
		return resampledState;
	}
	
	public List<P> iterateWithoutUpdater()
	{
		/* assume that functions are destructive to their inputs (for efficiency) */
		List<P> updatedState = currentState;
		List<P> weightedState = measurer.updateWeights(updatedState);
		
		List<P> resampledState;
		if (measurer.shouldResample())
			resampledState = resampler.resample(weightedState);
		else
			resampledState = weightedState;
		currentState = resampledState;
		return resampledState;
	}
	
	public List<P> getCurrentState()
	{
		return currentState;
	}
	public void setCurrentState(List<P> state) {
		List<P> copy = new ArrayList<P>();
		for(P p:state) {
			copy.add(p);
		}
		currentState = copy;
	}
}
