package edu.cmu.ri.rcommerce.filter;

import android.content.Context;
import android.os.Vibrator;
import edu.cmu.ri.rcommerce.CONFIGURE;
import edu.cmu.ri.rcommerce.Common;
import edu.cmu.ri.rcommerce.LocationListener;
import edu.cmu.ri.rcommerce.sensor.SensorReading;

/** Localizes pedestrians using accelerometer and magnetometer for heading,
 * and step detection method for tracking movement */
public class PedestrianLocalization_StepDetection implements PedestrianLocalization {

	final int MOVING_AVERAGE_FILTER_WINDOW_SIZE = 5;
	public float STRIDE_LENGTH = 1.0f; //in meters
	
	MovingAverageFilter step1 = new MovingAverageFilter(MOVING_AVERAGE_FILTER_WINDOW_SIZE);
	FirstDifferenceFilter step2 = new FirstDifferenceFilter();
	DoubleThresholdPeakDetector step3 = new DoubleThresholdPeakDetector(-1.3,1.3,400);
	//SingleThresholdPeakDetector step3 = new SingleThresholdPeakDetector(1.5, 600);
	
	Context context;
	LocationListener listener;
	Vibrator vibrator;
	
	/* the oldAzimuth is used to extrapolate what direction the person
	 * was moving in over the course of the last detected step */
	float oldAzimuth;
	
	/* these are required to compute the azimuth when a step is detected */
	SensorReading currentAccelerometerReading;
	SensorReading currentMagnetometerReading;
	
	float orientationOffset = CONFIGURE.DEFAULT_ORIENTATION_OFFSET;
	
	float oldX,oldY;
	
	boolean usingRobotFrame;
	
	public PedestrianLocalization_StepDetection(Context context,LocationListener listener, boolean usingRobotFrame)
	{
		this.context = context;this.usingRobotFrame = usingRobotFrame;
		if (usingRobotFrame)
			orientationOffset = CONFIGURE.ROBOT_ORIENTATION_OFFSET;
		else
			orientationOffset = CONFIGURE.DEFAULT_ORIENTATION_OFFSET;
		vibrator = (Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE);
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
		currentAccelerometerReading = readings[readings.length - 1];
		
		//assuming that the time difference between readings is regular
		double[] summedDimensions = new double[readings.length];
		for (int i = 0 ; i<readings.length ; i++)
		{
			summedDimensions[i] = readings[i].values[0] + readings[i].values[1] + readings[i].values[2];
		}
		step1.addValues(summedDimensions);
		double[] averaged = step1.readFiltered();
		step2.addValues(averaged);
		double[] filtered = step2.readFiltered();
		
		step3.addValues(filtered);
		
		if (step3.foundPeak())
		{
			//vibrator.vibrate(100);
			long time = System.currentTimeMillis();
			
			float[] orientation = Common.getCurrentOrientation(currentMagnetometerReading.values, currentAccelerometerReading.values);
			
			float newX, newY;
			if (usingRobotFrame)
			{
				newX = oldX - STRIDE_LENGTH * (float)-Math.cos(oldAzimuth - orientationOffset);
				newY = oldY + STRIDE_LENGTH * (float)Math.sin(oldAzimuth - orientationOffset);
				oldAzimuth = orientation[0];
			}
			else
			{
				double angle = -(oldAzimuth-orientationOffset) + Math.toRadians(90);
				newX = oldX + STRIDE_LENGTH * (float)Math.cos(angle); 
				newY = oldY + STRIDE_LENGTH * (float)Math.sin(angle);
				oldAzimuth = -orientation[0];
			}
			
			oldX = newX;
			oldY = newY;
			
			broadcast();
		}
		
		

		
	};
	
	@Override
	public void setLocation(float x, float y)
	{
		oldX = x;
		oldY = y;
		broadcast();
	}
	
	


	@Override
	public void addMagnetometerReadings(SensorReading[] readings) {
		currentMagnetometerReading = readings[readings.length - 1];
		
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
	
	private void broadcast()
	{
		if (listener != null)
			listener.broadcastLocationUpdate(oldX, oldY,oldAzimuth+orientationOffset, System.currentTimeMillis(), 1);
	}


	@Override
	public void setOrientationOffset(float rad) {
		orientationOffset = rad;
		
	}
	
	@Override
	public void addGyroReadings(SensorReading[] readings) {
		throw new RuntimeException("This module doesn't use gyro readings");
	}
	
	@Override
	public void reset() {
		setLocation(0, 0);
		setOrientationOffset(CONFIGURE.DEFAULT_ORIENTATION_OFFSET);
	}
	

}
