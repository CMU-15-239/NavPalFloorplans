package edu.cmu.ri.rcommerce.filter;

import edu.cmu.ri.rcommerce.sensor.SensorReading;

/** Interface for any filter that uses accelerometer readings */
public interface AccelerometerListener {
	
	public void addAccelerometerReadings(SensorReading[] readings) ;

}
