package edu.cmu.ri.rcommerce;

/**
 * Generates fast, moderate quality random numbers.
 * 
 * @author Nisarg
 *
 */
final public class FastRandom{

	private long seed;

	public FastRandom() {
		seed = System.nanoTime();
	}

	public FastRandom(long seed) {
		this.seed = seed;
	}

	int next(int nbits) {
		seed ^= (seed << 21);
		seed ^= (seed >>> 35);
		seed ^= (seed << 4);
		return (int) (seed & ((1L << nbits) - 1));
	}
	
	public int nextInt()
	{
		return next(32);
	}

	public float nextFloat() {
		return next(24) / (float) (1 << 24);
	}
}
