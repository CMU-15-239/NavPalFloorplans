package edu.cmu.ri.rcommerce.particleFilter;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
/**
 * Throws away impossible particles (weight==0) and duplicates the rest with uniform random sampling to maintain the same particle count 
 * @author Nisarg
 *
 * @param <P> the particle class being used (e.g. Particle2D)
 */
public class ImpossibleParticleResampler<P extends Particle> implements Resampler<P> {

	Random r = new Random(7);
	
	@SuppressWarnings("unchecked")
	@Override
	public List<P> resample(List<P> weightedState) {
		List<P> resampledList = new ArrayList<P>();
		for (P p : weightedState)
		{
			if (p.weight != 0)
				resampledList.add(p);
		}
		
		while (resampledList.size() != weightedState.size())
		{
			P clone = weightedState.get(r.nextInt(weightedState.size()));
			resampledList.add((P)clone.copy());
		}
		
		return resampledList;
	}
	

}
