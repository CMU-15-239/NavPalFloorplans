package edu.cmu.ri.rcommerce;

public final class CONFIGURE {
	//value for tests in the gates highbay
	public static final float ROBOT_ORIENTATION_OFFSET = 0.802f;
	public final static float DEFAULT_ORIENTATION_OFFSET = 0.0f;
	

	//used for calculating expected magnetic inclination and declination
	//TODO track this with 'my location' API 
	final static float APPROXIMATE_LATITUDE = 40.473069f;
	final static float APPROXIMATE_LONGITUDE = -79.995575f;
	final static float APPROXIMATE_ALTITUDE = 234; //in meters
	public final static GeomagneticField GEOMAGNETIC_MODEL = new GeomagneticField(APPROXIMATE_LATITUDE, APPROXIMATE_LONGITUDE, APPROXIMATE_ALTITUDE, System.currentTimeMillis());
	//public final static float MAGNETIC_DECLINATION_CORRECTION = .16057f;
	
	//in microTeslas
	public final static float MAGNETIC_FIELD_ANOMALY_THRESHOLD = 60.0f;
	public static final float INCLINATION_TOLERANCE = 30; //maximum difference between the expected and actual inclination, in degrees

}
