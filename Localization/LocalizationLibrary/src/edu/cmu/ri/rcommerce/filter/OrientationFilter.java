package edu.cmu.ri.rcommerce.filter;

import edu.cmu.ri.rcommerce.CONFIGURE;
import edu.cmu.ri.rcommerce.Common;
import edu.cmu.ri.rcommerce.SensorManager;
import edu.cmu.ri.rcommerce.sensor.GyroCalibrationDataProvider;

/**
 * Does sensor fusion of accelerometer, magnetometer, and gyroscope data to
 * derive an orientation estimate
 * 
 * @author Nisarg
 * 
 */
public class OrientationFilter {
	float[] estimatedGravityVector = null;
	float[] estimatedNorthVector = null;

	// derived by filtering of the accelerometer readings
	float[] gravityReferenceVector = null;
	final float gravityLowpassParameter = 0.2f; // In range 0-1. 1 means no
												// lowpass, 0 means always keep
												// original reading

	// derived by filtering of the magnetometer readings and using the
	// gravityReferenceVector
	float[] magneticReferenceVector = null;
	final float magneticLowpassParameter = 0.1f; // In range 0-1. 1 means no
													// lowpass, 0 means always
													// keep original reading

	boolean gravityDisturbance = false;
	boolean magneticDisturbance = false;

	float[] currentAccelData;
	float[] currentMagneticData;
	float[] currentGyroData;
	float[] previousGyroData; // used for more accurate integration
	long previousGyroTimestamp;
	long currentGyroTimestemp;

	public float[] gyroCalibrationOffsets = new float[3];
	long lastGyroCalibrationTime;

	public float[] fusedOrientationEstimate = null;
	public float[] referenceOrientationEstimate = new float[3];
	public float[] globalToLocalTransform = new float[9];
	public float[] inclinationMatrix = new float[9];

	public OrientationFilter() {
		setInitialGyroCalibrationOffsets(GyroCalibrationDataProvider
				.getGyroCalibrationData());
	}

	/**
	 * Input a new acceleration reading.
	 * 
	 * @param data
	 *            3 element array of acceleration data
	 */
	public void addAccel(float[] data) {
		currentAccelData = data;
		float magnitude = Common.magnitude(data[0], data[1], data[2]);
		// check if the acceleration is so far out of the range for gravity that
		// the gravity signal
		// is either not there or is being swamped by other accelerations
		gravityDisturbance = (magnitude <= 6f || magnitude >= 12f);

		runLoop();
	}

	/**
	 * Input a new magnetometer reading
	 * 
	 * @param data
	 *            3 element array of magnetometer data
	 */
	public void addMagnetometer(float[] data) {
		currentMagneticData = data;
		float magnitude = Common.magnitude(data[0], data[1], data[2]);
		magneticDisturbance = (magnitude >= CONFIGURE.MAGNETIC_FIELD_ANOMALY_THRESHOLD);

		runLoop();
	}

	private static final float NS2S = 1.0f / 1000000000.0f;

	/**
	 * Input a new gyro reading
	 * 
	 * @param data
	 *            3 element array of gyro data
	 * @param timestamp
	 *            time of the gyro reading
	 */
	public void addGyro(float[] data, long timestamp) {
		if (currentGyroData == null) {
			currentGyroData = data;
			currentGyroTimestemp = timestamp;
			return;
		}

		previousGyroData = currentGyroData;
		previousGyroTimestamp = currentGyroTimestemp;
		currentGyroData = data;
		currentGyroTimestemp = timestamp;

		if (fusedOrientationEstimate != null) {
			final float dT = (timestamp - previousGyroTimestamp) * NS2S;
			float[] localAngularVelocity = new float[4];
			float[] globalAngularVelocity = new float[4];

			localAngularVelocity[0] = -(data[0] + previousGyroData[0]) * dT / 2
					+ gyroCalibrationOffsets[0] * dT;
			localAngularVelocity[1] = -(data[1] + previousGyroData[1]) * dT / 2
					+ gyroCalibrationOffsets[1] * dT;
			localAngularVelocity[2] = -(data[2] + previousGyroData[2]) * dT / 2
					+ gyroCalibrationOffsets[2] * dT;
			transposeMandMultiplybyV(globalAngularVelocity,
					globalToLocalTransform, localAngularVelocity);

			// note: left and right side indices intentionally don't match
			fusedOrientationEstimate[0] += globalAngularVelocity[2];
			fusedOrientationEstimate[0] %= 2 * Math.PI;

			fusedOrientationEstimate[1] += globalAngularVelocity[1];
			fusedOrientationEstimate[1] %= 2 * Math.PI;

			fusedOrientationEstimate[2] += globalAngularVelocity[0];
			fusedOrientationEstimate[2] %= 2 * Math.PI;
		}

		runLoop();
	}

	public void setInitialGyroCalibrationOffsets(float[] offsets) {
		// intentionally changed from offsets.close() so that offsets is now
		// passed by reference and any updates to GyroCalibrationDataProvider
		// are automatically supplied here as the reference is updated
		gyroCalibrationOffsets = offsets;
	}

	int logTimer = 1000;

	private void runLoop() {
		if (!gravityDisturbance && currentAccelData != null) {
			if (gravityReferenceVector == null)
				gravityReferenceVector = currentAccelData;
			else {
				gravityReferenceVector[0] = gravityLowpassParameter
						* currentAccelData[0] + gravityReferenceVector[0]
						* (1f - gravityLowpassParameter);
				gravityReferenceVector[1] = gravityLowpassParameter
						* currentAccelData[1] + gravityReferenceVector[1]
						* (1f - gravityLowpassParameter);
				gravityReferenceVector[2] = gravityLowpassParameter
						* currentAccelData[2] + gravityReferenceVector[2]
						* (1f - gravityLowpassParameter);
			}
		}

		if (!magneticDisturbance && currentMagneticData != null
				&& gravityReferenceVector != null) {
			if (magneticReferenceVector == null)
				magneticReferenceVector = currentMagneticData;
			else {
				magneticReferenceVector[0] = magneticLowpassParameter
						* currentMagneticData[0] + magneticReferenceVector[0]
						* (1f - magneticLowpassParameter);
				magneticReferenceVector[1] = magneticLowpassParameter
						* currentMagneticData[1] + magneticReferenceVector[1]
						* (1f - magneticLowpassParameter);
				magneticReferenceVector[2] = magneticLowpassParameter
						* currentMagneticData[2] + magneticReferenceVector[2]
						* (1f - magneticLowpassParameter);
			}
		}

		// check if we can't do any more useful work due to lack of data
		if (magneticReferenceVector == null || gravityReferenceVector == null)
			return;

		SensorManager.getRotationMatrix(globalToLocalTransform,
				inclinationMatrix, gravityReferenceVector,
				magneticReferenceVector);
		double inclination = -Math.toDegrees(SensorManager
				.getInclination(inclinationMatrix));
		// Log.d("Inclination", "Inclination (degrees): " + inclination);
		double inclinationError = inclination
				- CONFIGURE.GEOMAGNETIC_MODEL.getInclination();
		if (Math.abs(inclinationError) > CONFIGURE.INCLINATION_TOLERANCE) {
			// Log.d("inclination", "expected: " +
			// -CONFIGURE.GEOMAGNETIC_MODEL.getInclination());
			// Log.d("inclination", "error: " + inclinationError);
			magneticDisturbance = true;
		}

		SensorManager.getOrientation(globalToLocalTransform,
				referenceOrientationEstimate);
		// correct for magnetic declination so this is relative to true north
		// rather than magnetic north
		referenceOrientationEstimate[0] += Math
				.toRadians(CONFIGURE.GEOMAGNETIC_MODEL.getDeclination());

		if (fusedOrientationEstimate == null) {
			fusedOrientationEstimate = referenceOrientationEstimate.clone();
			lastGyroCalibrationTime = System.nanoTime();
		} else {
			float pitchError = referenceOrientationEstimate[0]
					- fusedOrientationEstimate[0];
			float rollError = referenceOrientationEstimate[1]
					- fusedOrientationEstimate[1];
			float azimuthError = referenceOrientationEstimate[2]
					- fusedOrientationEstimate[2];
			// if (logTimer-- == 0)
			// {
			// Log.d("reference", "pitch: " + referenceOrientationEstimate[0] +
			// " roll: " + referenceOrientationEstimate[1] + " azimuth:" +
			// referenceOrientationEstimate[2]);
			// Log.d("fusedR", "pitch: " + fusedOrientationEstimate[0] +
			// " roll: " + fusedOrientationEstimate[1] + " azimuth:" +
			// fusedOrientationEstimate[2]);
			// Log.d("errors", "pitch: " + pitchError + " roll: " + rollError +
			// " azimuth:" + azimuthError);
			// Log.d("gyro", "pitch: " + currentGyroData[0] + " roll: " +
			// currentGyroData[1] + " azimuth:" + currentGyroData[2]);
			// logTimer = 1000;
			// }

			// disable gyro resets, since they tend to just make things worse
			// indoors
			/*
			 * float dT = (currentGyroTimestemp - lastGyroCalibrationTime)*
			 * NS2S; if (dT >= 10000 && !magneticDisturbance &&
			 * !gravityDisturbance) { //calculate a new estimate for the gyro
			 * drift, then reset the gyro to clear accumulated error
			 * 
			 * //convert errors from global coordinates to gyro coordinates
			 * float[] globalErrorVector = new
			 * float[]{pitchError,rollError,azimuthError,1}; float[]
			 * localErrorVector = new float[4];
			 * 
			 * Matrix.multiplyMV(localErrorVector, 0, globalToLocalTransform, 0,
			 * globalErrorVector, 0);
			 * 
			 * float xErrorPerSecond = localErrorVector[0] / dT; float
			 * yErrorPerSecond = localErrorVector[1] / dT; float zErrorPerSecond
			 * = localErrorVector[2] / dT;
			 * 
			 * //gyroCalibrationOffsets[0] = xErrorPerSecond;
			 * //gyroCalibrationOffsets[1] = yErrorPerSecond;
			 * //gyroCalibrationOffsets[2] = zErrorPerSecond;
			 * Log.d("new offsets","X: " + xErrorPerSecond + " Y: " +
			 * yErrorPerSecond + " Z:" + zErrorPerSecond);
			 * 
			 * fusedOrientationEstimate = referenceOrientationEstimate.clone();
			 * lastGyroCalibrationTime = currentGyroTimestemp; }
			 */
		}

	}

	// 3x3 matrix and 3-vector
	static void transposeMandMultiplybyV(float[] out, float[] m, float[] v) {
		out[0] = m[0] * v[0] + m[3] * v[1] + m[6] * v[2];
		out[1] = m[1] * v[0] + m[4] * v[1] + m[7] * v[2];
		out[2] = m[2] * v[0] + m[5] * v[1] + m[8] * v[2];
	}

}
