package edu.cmu.ri.rcommerce;

import android.util.Log;

public class Common {
	
	//returns a float[3] with orientation in x,y, and z
	public static float[] getCurrentOrientation(float[] magnetometerReading, float[] accelerometerReading)
	{
		float[] R = new float[9];
		float[] orientation = new float[3];
		if (magnetometerReading == null || accelerometerReading == null)
		{
			Log.d("orientation", "not enough info to determine orientation");
			return orientation;
		}
		if (!SensorManager.getRotationMatrix  (R, null,
				accelerometerReading, magnetometerReading))
		{
			Log.d("orientation", "could not determine orientation");
			return orientation;
		}
		SensorManager.getOrientation(R, orientation);
		orientation[0] = -orientation[0];
		
		//compensate for magnetic declination
		//orientation[0] += CONFIGURE.GEOMAGNETIC_MODEL.getDeclination();
		return orientation;
	}
	
	public static float magnitude(float x, float y , float z)
	{
		return (float)Math.sqrt(x*x + y*y + z*z);
	}
	
	

}
