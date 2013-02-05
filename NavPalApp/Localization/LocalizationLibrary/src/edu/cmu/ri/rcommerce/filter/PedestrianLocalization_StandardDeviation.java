package edu.cmu.ri.rcommerce.filter;

import edu.cmu.ri.rcommerce.CONFIGURE;
import edu.cmu.ri.rcommerce.Common;
import edu.cmu.ri.rcommerce.LocationListener;
import edu.cmu.ri.rcommerce.sensor.SensorReading;

/** Localizes pedestrians using accelerometer and magnetometer for heading,
 * and standard deviation method for movement detection */
public class PedestrianLocalization_StandardDeviation implements PedestrianLocalization {

	final int SENSOR_BUFFER_SIZE = 20;
	public float WALKING_VELOCITY = 1.3f; //in meters per second
	public float STD_DEV_THRESHOLD = 15f;
	
	LocationListener listener;
	
	public float[] oldOrientation = new float[]{0,0,0};
	public float oldX,oldY;
	long oldTime;
	float orientationOffset = CONFIGURE.DEFAULT_ORIENTATION_OFFSET;
	
	int totalAccelReadings = 0;
	
	SensorReading currentAccelerometerReading;
	SensorReading currentMagnetometerReading;
	
	float[] readingBuffer = new float[SENSOR_BUFFER_SIZE];
	int readingsLoc = 0; //insertion point for the new reading

	public final static int STATUS_OK = 0;
	public final static int STATUS_MAGNETIC_FIELD_ERROR = 1;
	
	boolean magnetic_field_error = false;
	
	int lastBroadcastStatusChange = -1;
	
	boolean usingRobotFrame;
	
	//not used by the algorithm. They're here for opening up information about how it's performing
	public float lastStdDev;

	
	public PedestrianLocalization_StandardDeviation(LocationListener listener, boolean usingRobotFrame)
	{
		this.usingRobotFrame = usingRobotFrame;
		if (usingRobotFrame)
			orientationOffset = CONFIGURE.ROBOT_ORIENTATION_OFFSET;
		else
			orientationOffset = CONFIGURE.DEFAULT_ORIENTATION_OFFSET;
		oldTime = System.currentTimeMillis();
		this.listener = listener;
	}
	
	public void changeToRobotCoordinateFrame()
	{
		this.usingRobotFrame = true;
		orientationOffset = CONFIGURE.ROBOT_ORIENTATION_OFFSET;
	}

	public void changeToDefaultCoordinateFrame()
	{
		this.usingRobotFrame = false;
		orientationOffset = CONFIGURE.DEFAULT_ORIENTATION_OFFSET;
	}
	
	@Override
	public void addAccelerometerReadings(SensorReading[] readings) 
	{
		totalAccelReadings++;
		currentAccelerometerReading = readings[readings.length - 1];
		
		for (int i = 0 ; i < readings.length ; i++)
		{
			SensorReading e = readings[i];
			readingBuffer[readingsLoc++] = (e.values[0]*e.values[0] + e.values[1]*e.values[1] + e.values[2] * e.values[2]);
			readingsLoc = readingsLoc % SENSOR_BUFFER_SIZE;
		}
		
		float stdDev;
		if (totalAccelReadings < SENSOR_BUFFER_SIZE)
			stdDev = standardDeviation(readingBuffer, 0, totalAccelReadings-1);
		else
			stdDev = standardDeviation(readingBuffer);
		
		lastStdDev = stdDev;
		
		if (currentMagnetometerReading == null)
			return;
		
		long time = readings[readings.length-1].timestamp;
		float[] orientation = Common.getCurrentOrientation(currentMagnetometerReading.values, currentAccelerometerReading.values);
		
		if (stdDev > STD_DEV_THRESHOLD)
		{	
			float newX, newY;
			//assuming readings are spaced 50ms apart for now

			if (usingRobotFrame)
			{
				//using robot frame. Positive x is up. Positive y is to the left
				newX = oldX - WALKING_VELOCITY * (float)-Math.cos(oldOrientation[0] - orientationOffset) * .05f; 
				newY = oldY + WALKING_VELOCITY * (float)Math.sin(oldOrientation[0]  - orientationOffset) * .05f;
				oldOrientation = orientation;
			}
			else
			{
				double angle = -(oldOrientation[0]-orientationOffset) + Math.toRadians(90);
				newX = oldX + WALKING_VELOCITY * (float)Math.cos(angle) * .05f; 
				newY = oldY + WALKING_VELOCITY * (float)Math.sin(angle) * .05f;
				//oldAzimuth = (float)((orientation[0] + Math.toRadians(180)) % (2 * Math.PI)) ;
				oldOrientation = orientation;
				oldOrientation[0] = -oldOrientation[0];
			}
			oldX = newX;
			oldY = newY;
			oldTime = time;
		
			broadcast();
		}
		else
		{
			if (usingRobotFrame)
				oldOrientation = orientation;
			else
			{
				//oldAzimuth = (float)((orientation[0] + Math.toRadians(180)) % (2 * Math.PI)) ; //TODO: check if this is actually necessary. I think it should actually be done on the graphics side
				oldOrientation = orientation;
				oldOrientation[0] = -oldOrientation[0];
			}
			oldTime = time;
			broadcast();	
		}	
	};
	
	public void addCompassReadings(SensorReading[] readings)
	{
		//for catching errors early
		throw new RuntimeException("Tried to give stepdetection compass readings, even though it doesn't need them");		
	};
	
	//Note: Uses the current system time as the broadcast time
	@Override
	public void setLocation(float x, float y)
	{
		oldX = x;
		oldY = y;
		broadcast();
	}
	
	//beginning and end are inclusive
	private float standardDeviation(float[] readings,int beginning,int end)
	{
		float sum = 0;
		for (int i = beginning ; i<= end ; i++)
			sum += readings[i];
		float average = sum/((end - beginning) + 1);
		
		float stdDev = 0;
		
		for (int i = beginning ; i<= end ; i++)
			stdDev += Math.pow(readings[i] - average,2);
		
		stdDev /= ((end - beginning) + 1);
		stdDev = (float)Math.sqrt(stdDev);
		
		//Log.d("PedLocSD", ""+stdDev);
		return stdDev;
	}
	
	private float standardDeviation(float[] readings)
	{
		return standardDeviation(readings,0,readings.length-1);
	}

	@Override
	public void addMagnetometerReadings(SensorReading[] readings) {
		currentMagnetometerReading = readings[readings.length - 1];
		float[] values = currentMagnetometerReading.values;
		float magnetometerMagnitude = magnitude(values[0], values[1],values[2]) ;
		magnetic_field_error = (magnetometerMagnitude >= CONFIGURE.MAGNETIC_FIELD_ANOMALY_THRESHOLD);
		broadcastStatus();
	}

	@Override
	public void setOrientationOffset(float rad) {
		orientationOffset = rad;
		broadcast();
	}
	private void broadcast()
	{
		if (listener != null)
			listener.broadcastLocationUpdate(oldX, oldY,oldOrientation[0]+orientationOffset, System.currentTimeMillis(), WALKING_VELOCITY);
	}
	
	private void broadcastStatus()
	{
		int status = STATUS_OK;
		if (magnetic_field_error) status = STATUS_MAGNETIC_FIELD_ERROR;
		if (listener != null && lastBroadcastStatusChange != status)
		{
			listener.broadcastLocationStatusChange(status);
			lastBroadcastStatusChange = status;
		}
	}

	@Override
	public void setLocationUpdateCallback(LocationListener callback) {
		listener = callback;
		
	}

	@Override
	public boolean usesAccelerometer() {
		return true;
	}

	@Override
	public boolean usesMagnetometer() {
		return true;
	}
	
	@Override
	public boolean usesGyro() {
		return false;
	}
	
	private float magnitude(float x, float y , float z)
	{
		return (float)Math.sqrt(x*x + y*y + z*z);
	}

	@Override
	public void addGyroReadings(SensorReading[] sensorReadings) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void reset() {
		setLocation(0, 0);
		setOrientationOffset(CONFIGURE.DEFAULT_ORIENTATION_OFFSET);
	}
	

}
