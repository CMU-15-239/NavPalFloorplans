package edu.cmu.ri.rcommerce.particleFilter;

/** Abstract class for a particle. The only property a particle is required to have is a weight. */
public abstract class Particle {
	public float weight;
	
	abstract Particle copy();
}
