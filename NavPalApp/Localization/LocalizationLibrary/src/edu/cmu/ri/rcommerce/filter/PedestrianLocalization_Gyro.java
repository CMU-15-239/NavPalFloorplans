package edu.cmu.ri.rcommerce.filter;

import android.util.Log;
import edu.cmu.ri.rcommerce.CONFIGURE;
import edu.cmu.ri.rcommerce.LocationListener;
import edu.cmu.ri.rcommerce.sensor.SensorReading;

/** Localizes pedestrians using an OrientationFilter for heading determination and the standard deviation method for detecting movement. */
public class PedestrianLocalization_Gyro implements PedestrianLocalization {

	final int SENSOR_BUFFER_SIZE = 20;
	public float WALKING_VELOCITY = 1.3f; //in meters per second
	public float STD_DEV_THRESHOLD = 15f;
	
	LocationListener listener;

	public float oldX,oldY;
	long oldTime;
	float orientationOffset = CONFIGURE.DEFAULT_ORIENTATION_OFFSET;
	
	int totalAccelReadings = 0;
	
	SensorReading currentAccelerometerReading;
	SensorReading currentMagnetometerReading;
	
	float[] readingBuffer = new float[SENSOR_BUFFER_SIZE];
	int readingsLoc = 0; //insertion point for the new reading

	public final static int STATUS_OK = 0;
	public final static int STATUS_MAGNETIC_FIELD_DISTURBANCE = 1;
	
	int lastBroadcastStatusChange = -1;
	
	boolean usingRobotFrame;
	
	//not used by the algorithm. They're here for opening up information about how it's performing
	public float lastStdDev;

	public OrientationFilter orientationFilter = new OrientationFilter();
	public float[] oldOrientation = null;
	
	long previousAccelReadingTime = 0;
	long latestAccelReadingTime = 0;
	
	public PedestrianLocalization_Gyro(LocationListener listener, boolean usingRobotFrame)
	{
		this.usingRobotFrame = usingRobotFrame;
		if (usingRobotFrame)
			orientationOffset = CONFIGURE.ROBOT_ORIENTATION_OFFSET;
		else
			orientationOffset = CONFIGURE.DEFAULT_ORIENTATION_OFFSET;
		oldTime = System.nanoTime();
		this.listener = listener;
	}
	
	@Override
	public void addAccelerometerReadings(SensorReading[] readings) 
	{
		previousAccelReadingTime = latestAccelReadingTime;
		latestAccelReadingTime = readings[0].timestamp;
		totalAccelReadings++;
		currentAccelerometerReading = readings[readings.length - 1];
		
		float dT = (latestAccelReadingTime - previousAccelReadingTime) / 1e9f; 
		
		orientationFilter.addAccel(currentAccelerometerReading.values);
		
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
		
		long time = readings[readings.length-1].timestamp;
		float[] orientation = orientationFilter.fusedOrientationEstimate;
		if (oldOrientation == null && orientation != null) //no orientation info, so don't know which direction to update location
			oldOrientation = orientation;
		else if (orientation == null)
			return;
	
		if (stdDev > STD_DEV_THRESHOLD && previousAccelReadingTime != 0)
		{	
			float newX, newY;
			
			
			if (usingRobotFrame)
			{
				//using robot frame. Positive x is up. Positive y is to the left
				newX = oldX - WALKING_VELOCITY * (float)-Math.cos(oldOrientation[0] - orientationOffset) * dT;
				newY = oldY + WALKING_VELOCITY * (float)Math.sin(oldOrientation[0]  - orientationOffset) * dT;
			}
			else
			{
				double angle = -(oldOrientation[0]-orientationOffset) + Math.toRadians(90);
				newX = oldX + WALKING_VELOCITY * (float)Math.cos(angle) * dT;
				newY = oldY + WALKING_VELOCITY * (float)Math.sin(angle) * dT;
			}
			//Log.d("gyroLoc", "Broadcasting with: " + WALKING_VELOCITY*dT + "m/s and orient:" + orientation[0] + " and dT:" + dT);
			broadcast(true,WALKING_VELOCITY*dT,orientation[0],dT);
			oldX = newX;
			oldY = newY;
			oldTime = time;
			oldOrientation = orientation;
		
			
		}
		else
		{
			//Log.d("gyroLoc", "Broadcasting with: " + 0 + "m/s and orient:" + orientation[0] + " and dT:" + dT);
			broadcast(true,0,orientation[0], dT);	
			oldOrientation = orientation;
			oldTime = time;	
		}	
	};
	
	@Override
	public void addGyroReadings(SensorReading[] readings)
	{
		SensorReading event = readings[readings.length -1];
		orientationFilter.addGyro(event.values,event.timestamp);
	}

	@Override
	public void addMagnetometerReadings(SensorReading[] readings) {
		currentMagnetometerReading = readings[readings.length - 1];
		orientationFilter.addMagnetometer(currentMagnetometerReading.values);
		broadcastStatus();
	}

	//Note: Uses the current system time as the broadcast time
	@Override
	public void setLocation(float x, float y)
	{
		oldX = x;
		oldY = y;
		broadcast(false,0,0,0);
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
		
		return stdDev;
	}
	
	private float standardDeviation(float[] readings)
	{
		return standardDeviation(readings,0,readings.length-1);
	}

	@Override
	public void setOrientationOffset(float rad) {
		orientationOffset = rad;
		broadcast(false,0,0,0);
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

	private void broadcast(boolean doRelative, double dR, double dTheta, double timeDiff)
	{
		if (listener != null && oldOrientation != null)
		{
			listener.broadcastLocationUpdate(oldX, oldY,oldOrientation[0]+orientationOffset, System.currentTimeMillis(), dR);
			if (doRelative)
				listener.broadcastRelativeLocationUpdate(dR, dTheta, timeDiff);
		}
		
	}
	
	private void broadcastStatus()
	{
		int status = STATUS_OK;
		if (orientationFilter.magneticDisturbance) status = STATUS_MAGNETIC_FIELD_DISTURBANCE;
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
		return true;
	}

	@Override
	public void reset() {
		setLocation(0, 0);
		setOrientationOffset(CONFIGURE.DEFAULT_ORIENTATION_OFFSET);
		orientationFilter = new OrientationFilter();
	}
	

}
