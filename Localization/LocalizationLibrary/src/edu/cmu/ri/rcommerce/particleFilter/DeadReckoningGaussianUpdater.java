package edu.cmu.ri.rcommerce.particleFilter;

import java.util.List;

import edu.cmu.ri.rcommerce.FastRandom;

/**
 * Provides predictions of the future position of a particle by sampling from a
 * gaussian velocity and heading distribution.
 * 
 * @author Nisarg
 * 
 */
public class DeadReckoningGaussianUpdater implements Updater<Particle2D> {
	private float velocity, velocityMeanDeviation, compass,
			compassMeanDeviation, timeDifference;
	private FastRandom r;
	private float queuedTime;

	public DeadReckoningGaussianUpdater(FastRandom r, float velocity, float velocityMeanDeviation, float compass, float compassMeanDeviation, float timeDifference) {
		this.r = r;
		this.velocity = velocity;
		this.velocityMeanDeviation = velocityMeanDeviation;
		this.compass = compass;
		this.compassMeanDeviation = compassMeanDeviation;
		this.timeDifference = timeDifference;
		queuedTime = 0.0f;
	}

	public void setParameters(float velocity, float velocityMeanDeviation, float compass, float compassMeanDeviation, float timeDifference) {
		this.velocity = velocity;
		this.velocityMeanDeviation = velocityMeanDeviation;
		this.compass = compass;
		this.compassMeanDeviation = compassMeanDeviation;
		this.timeDifference = timeDifference;
		queuedTime += timeDifference;
	}

	@Override
	public List<Particle2D> update(List<Particle2D> state) {
		/*List<Particle2D> cstate = new ArrayList<Particle2D>();
		for(Particle2D p: state) {
			cstate.add((Particle2D)p.copy());
		}*/
		// Since setParameters is called in a separate thread, this needs to be
		// done to prevent data from changing inside an update
		float svelocity = this.velocity;
		float svelocityMeanDeviation = this.velocityMeanDeviation;
		float scompass = this.compass;
		float scompassMeanDeviation = this.compassMeanDeviation;			
		float stimeDifference = this.queuedTime;
		if(timeDifference == 1)
			stimeDifference = 1;
		queuedTime = 0;
		//System.out.println(svelocity + " " + svelocityMeanDeviation + " " + scompass + " " + scompassMeanDeviation + " " + stimeDifference);
		for (Particle2D p : state) {
			double vHat = svelocity + GaussianSample.sample(r, svelocityMeanDeviation);
			double thetaHat = scompass + GaussianSample.sample(r, scompassMeanDeviation);
			thetaHat %= 2 * Math.PI;
			double forwardDistance = vHat * stimeDifference;

			p.x = (float) (p.x - forwardDistance * Math.cos(thetaHat));
			p.y = (float) (p.y + forwardDistance * Math.sin(thetaHat));
		}		
		return state;
	}
}
