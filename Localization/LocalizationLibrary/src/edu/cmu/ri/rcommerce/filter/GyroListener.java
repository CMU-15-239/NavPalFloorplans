package edu.cmu.ri.rcommerce.filter;

import edu.cmu.ri.rcommerce.sensor.SensorReading;

/** Interface for any filter that uses gyroscope readings */
public interface GyroListener {

	public void addGyroReadings(SensorReading[] readings);
}
