package edu.cmu.ri.rcommerce.sensor;

/** Generic format for accelerometer, magnetometer, gyro, etc. sensor data */
public final class SensorReading {
	public final float[] values;
	public final long timestamp;
	public final int sensorType;
	
	public static final int ACCELEROMETER_SENSOR = 0,
							ORIENTATION_SENSOR = 1,
							MAGNETOMETER_SENSOR = 2,
							GYRO_SENSOR=3,
							GRAVITY_SENSOR=4,
							LINEAR_ACCEL_SENSOR=5,
							ROTATION_SENSOR=6;
	public SensorReading(long timestamp, float[] values, int sensorType) {
		this.timestamp = timestamp;
		this.values = values;
		this.sensorType = sensorType;
	}
}
