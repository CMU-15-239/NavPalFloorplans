package edu.cmu.ri.rcommerce.sensor;


/** Manages calibration data for gyro sensors */
public class GyroCalibrationDataProvider {
	public static float[] data;
	public static boolean newData = false;
	public static void setGyroCalibrationData(float[] d) {
		newData = true;
		if(data == null)
			data = new float[3];
		data[0] = d[0];
		data[1] = d[1];
		data[2] = d[2];
	}

	// returns an array of size 3. The values are the amount of drift in the
	// x,y, and z axes respectively in radians / second
	public static float[] getGyroCalibrationData() {
		newData=false;
		if (data == null) {
			data = new float[3];
			// particular to each device
			//defaults for V-Unit 01
			data[0] = 0.0032324856f;
			data[1] = 0.0020621484f;
			data[2] = -0.0150996065f;
		}

		return data;
	}

}
