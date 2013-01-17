package edu.cmu.ri.rcommerce.sensor;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import edu.cmu.ri.rcommerce.filter.GyroListener;

/** Helper for initializing the gyroscope sensors and feeding readings to a {@link GyroListener} */
public class Gyroscope {
	private static boolean isInitialized;
	static Sensor gyro;
	public static List<GyroListener> listeners;

	public static boolean initialize(Context context) {
		listeners = new ArrayList<GyroListener>();
		SensorManager sensorMgr = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
		List<Sensor> sensors = sensorMgr.getSensorList(Sensor.TYPE_GYROSCOPE);

		if (sensors.size() == 0) {
			Log.d("Sensor", "failed to find gyro");
			return false;
		} else {
			gyro = sensors.get(0);
			Log.d("Sensor", "found gyro: " + gyro.getName());

			sensorMgr.registerListener(new GyroCallback(context), gyro, SensorManager.SENSOR_DELAY_UI);

			isInitialized = true;
			return true;
		}
	}

	static boolean isInitialized() {
		return isInitialized;
	}

	public static boolean subscribe(GyroListener listener) {
		if (!isInitialized)
			return false;

		listeners.add(listener);
		return true;
	}

}

class GyroCallback implements SensorEventListener {
	SensorReading[] readingBuffer = new SensorReading[1];
	Context context;

	public GyroCallback(Context context) {
		this.context = context;
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		readingBuffer[0] = new SensorReading(event.timestamp, event.values,SensorReading.GYRO_SENSOR);
		for (GyroListener listener : Gyroscope.listeners)
			listener.addGyroReadings(readingBuffer);

	}
}