package edu.cmu.ri.rcommerce.filter;

import edu.cmu.ri.rcommerce.LocationListener;


/**
 * Interface for methods of localizing pedestrians.
 * 
 * Currently assumes the localization method uses some combination of accelerometer, magnetometer,
 * and gyroscope.
 * @author Nisarg
 *
 */
public interface PedestrianLocalization extends AccelerometerListener,MagnetometerListener,GyroListener {

	
	/** reset the current x,y coordinates */
	public abstract void setLocation(float x, float y);
	/** set the correction term between north and the origin of the desired orientation coordinate system */
	public abstract void setOrientationOffset(float rad);
	public abstract void reset();
	/** The methods on  the callback are called whenever there is a change in position or status */ 
	public abstract void setLocationUpdateCallback(LocationListener callback);
	
	/** Query if the localization method uses an accelerometer */
	public boolean usesAccelerometer();
	/** Query if the localization method uses a magnetometer */
	public boolean usesMagnetometer();
	/** Query if the localization method uses a gyroscope */
	public boolean usesGyro();

}