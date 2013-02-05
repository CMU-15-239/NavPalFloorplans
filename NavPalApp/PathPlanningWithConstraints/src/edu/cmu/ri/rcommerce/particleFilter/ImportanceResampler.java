package edu.cmu.ri.rcommerce.particleFilter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
/**
 * Implements the importance resampling algorithm for 'focusing' the particles in
 * the filter on the most likely candidates.
 * @author Nisarg
 *
 * @param <P> the particle class being used (e.g. Particle2D)
 */
public class ImportanceResampler<P extends Particle> implements Resampler<P> {

	Random r = new Random(7);
	
	@SuppressWarnings("unchecked")
	@Override
	public List<P> resample(List<P> weightedState) {
		float normalizationFactor = 0;
		int N = weightedState.size();
		
		for (Particle p : weightedState)
			normalizationFactor += p.weight;
		for (Particle p : weightedState)
			p.weight = p.weight / normalizationFactor;
		
		//weights are now normalized to sum to 1
		
		//make a sorted array of running sums to be used during the sampling process
		float[] summedArray = new float[N];
		float sum = 0;
		for (int i = 0 ; i<N ; i++)
		{
			sum += weightedState.get(i).weight;
			summedArray[i] = sum;
		}
		
		List<P> resampledList = new ArrayList<P>(N);
		
		for (int i = 0 ; i < N ; i++)
		{
			float rand = r.nextFloat();
			int sampledParticleIndex = Arrays.binarySearch(summedArray, rand);
			if (sampledParticleIndex < 0)
				sampledParticleIndex = -(sampledParticleIndex + 1);
			if (sampledParticleIndex >= weightedState.size())
			{
				sampledParticleIndex = weightedState.size()-1; //TODO figure out why stuff is out of range
			}
			resampledList.add((P)weightedState.get(sampledParticleIndex).copy());
		}
		
		return resampledList;
	}	

}
