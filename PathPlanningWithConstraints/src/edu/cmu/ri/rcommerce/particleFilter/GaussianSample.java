package edu.cmu.ri.rcommerce.particleFilter;

import edu.cmu.ri.rcommerce.FastRandom;

/**
 * Reasonably fast sampling from an approximate Gaussian distribution
 * @author Nisarg
 *
 */
public class GaussianSample 
{
	final static int NUM_UNIFORM_SAMPLES = 12;
	final static float[] uniformSamples = new float[NUM_UNIFORM_SAMPLES];
	/**
	 *  Samples from an approximate Gaussian distribution with zero mean and variance of meanDeviation squared.
	 *  
	 *  Fast, but with only moderate quality.
	 */
	public static float sample(FastRandom r, float meanDeviation)
	{
		float mdTimes2 = meanDeviation * 2;
		fillFloatArray(r,uniformSamples);
		float out = 0;
		for (int i = 0 ; i<12; i++)
			out += (uniformSamples[i] * mdTimes2) - meanDeviation;
		return ((1.0f/2.0f) * out);
	}
	
	private static void fillFloatArray(FastRandom r,float[] a)
	{
		//unrolled loop
			a[0] = r.nextFloat();
			a[1] = r.nextFloat();
			a[2] = r.nextFloat();
			a[3] = r.nextFloat();
			a[4] = r.nextFloat();
			a[5] = r.nextFloat();
			a[6] = r.nextFloat();
			a[7] = r.nextFloat();
			a[8] = r.nextFloat();
			a[9] = r.nextFloat();
			a[10] = r.nextFloat();
			a[11] = r.nextFloat();
	}

}
