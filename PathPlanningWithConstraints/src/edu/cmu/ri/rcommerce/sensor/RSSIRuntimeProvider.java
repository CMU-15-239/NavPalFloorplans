package edu.cmu.ri.rcommerce.sensor;

/** Interface for a source of signal strength samples derived at localization time (as opposed to calibration time) */
public interface RSSIRuntimeProvider {
	
	public boolean newReadingAvailable();
	public RSSIReading getCurrentReading();

}
