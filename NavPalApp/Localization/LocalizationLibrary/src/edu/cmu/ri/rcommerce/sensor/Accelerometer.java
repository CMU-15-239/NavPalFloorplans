package edu.cmu.ri.rcommerce.sensor;

import static android.hardware.SensorManager.SENSOR_DELAY_UI;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import edu.cmu.ri.rcommerce.filter.AccelerometerListener;

/** Helper for initializing the accelerometer sensors and feeding readings to an {@link AccelerometerListener} */
public class Accelerometer {
	private static boolean isInitialized;
	static Sensor accelerometer;
	public static List<AccelerometerListener> listeners;

	public static boolean initialize(Context context)
	{
		listeners = new ArrayList<AccelerometerListener>();
		SensorManager sensorMgr = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
		List<Sensor> sensors = sensorMgr.getSensorList(Sensor.TYPE_ACCELEROMETER);

		if (sensors.size() == 0) {
			Log.d("Sensor", "failed to find accelerometer");
			return false;
		} else {
			accelerometer = sensors.get(0);
			Log.d("Sensor", "found accelerometer: " + accelerometer.getName());

			sensorMgr.registerListener(new AccelerometerCallback(context), accelerometer, SENSOR_DELAY_UI);

			isInitialized = true;
			return true;
		}
	}

	public static boolean subscribe(AccelerometerListener listener) {
		if (!isInitialized)
			return false;

		listeners.add(listener);
		return true;
	}

	public static boolean isInitialized() {
		return isInitialized;
	}

}

class AccelerometerCallback implements SensorEventListener {
	SensorReading[] readingBuffer = new SensorReading[1];
	Context context;

	public AccelerometerCallback(Context context) {
		this.context = context;
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		readingBuffer[0] = new SensorReading(event.timestamp,event.values,SensorReading.ACCELEROMETER_SENSOR);
		for (AccelerometerListener listener : Accelerometer.listeners)
			listener.addAccelerometerReadings(readingBuffer);

	}
}