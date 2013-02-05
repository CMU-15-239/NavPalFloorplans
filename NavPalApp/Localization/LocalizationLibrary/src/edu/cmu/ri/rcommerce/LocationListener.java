package edu.cmu.ri.rcommerce;
/**
 * Interface for classes that are interested in receiving real-time location data.
 * 
 * Provides a set of callbacks for updating position, heading, and status.
 * @author Nisarg
 *
 */
public interface LocationListener {

	//theta is in radians
	/** Callback for being notified of a change of position.
	 * @param x X position in meters
	 * @param y Y position in meters
	 * @param theta orientation in radians
	 * @param time time in unix format
	 * @para velocity if zero, then STD DEV method has declared no movement
	 */
	public abstract void broadcastLocationUpdate(double x, double y, double theta, long time, double velocity);
	/**
	 * Callback for being notified of incremental changes of position. NOTE: does not update when global offsets are changed
	 * @param dR straight line distance traveled since last update
	 * @param theta new heading (TODO: this should be relative, but that would require converting the particle filter from x,y to velocity, heading
	 * @param timeDiff time since the last update in seconds
	 */
	public abstract void broadcastRelativeLocationUpdate(double dR, double dTheta, double timeDiff);
	/** Callback for being notified of a change in status.
	 *  See the constants defined in the localization method for the meaning of the status argument */
	public abstract void broadcastLocationStatusChange(int status);

}