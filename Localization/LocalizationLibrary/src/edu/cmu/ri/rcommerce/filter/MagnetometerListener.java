package edu.cmu.ri.rcommerce.filter;

import edu.cmu.ri.rcommerce.sensor.SensorReading;

/** Interface for any filter that uses magnetometer readings */
public interface MagnetometerListener {

	public void addMagnetometerReadings(SensorReading[] readings);
}
