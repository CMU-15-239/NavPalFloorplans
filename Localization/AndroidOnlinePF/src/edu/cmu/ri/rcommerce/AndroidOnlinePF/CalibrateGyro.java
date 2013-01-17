package edu.cmu.ri.rcommerce.AndroidOnlinePF;

import java.util.List;

import edu.cmu.ri.rcommerce.sensor.GyroCalibrationDataProvider;

import android.app.Activity;
import android.content.Context;
import android.hardware.*;
import android.os.*;
import android.util.Log;
import android.widget.TextView;

/**
 * Activity to calibrate the gyro
 * 
 * Is run through the main Activity
 * @author Evan
 *
 */
public class CalibrateGyro extends Activity {
	float[] data = new float[3];
	Sensor gyro;
	long previousTimestamp;
	private static final float NS2S = 1.0f / 1000000000.0f;
	int calibrateTime = 10000; //10 seconds
	
	static Handler myHandler = new Handler();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final TextView view = new TextView(this);
		view.setText("calibrating gyro. Keep the phone completely still.");
		setContentView(view);
		
		SensorManager sensorMgr = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		List<Sensor> sensors = sensorMgr.getSensorList(Sensor.TYPE_GYROSCOPE);
		if (sensors.size() == 0) {
			Log.d("Sensor", "failed to find gyro");
			finish();
		} else {
			gyro = sensors.get(0);
			Log.d("Sensor", "found gyro: " + gyro.getName());
		}
		
		sensorMgr.registerListener(new SensorEventListener() {
			
			@Override
			public void onSensorChanged(SensorEvent event) {
				long timestamp = event.timestamp;
				if (previousTimestamp == 0)
				{
					previousTimestamp = timestamp;
					return;
				}
				
				final float dT = (timestamp - previousTimestamp) * NS2S;
				float[] readings = event.values;
				
				data[0] += readings[0] * dT;
				data[1] += readings[1] * dT;
				data[2] += readings[2] * dT;
			}
			
			@Override
			public void onAccuracyChanged(Sensor sensor, int accuracy) {
				// TODO Auto-generated method stub
				
			}
		}, gyro, SensorManager.SENSOR_DELAY_UI);
		
		myHandler.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				GyroCalibrationDataProvider.setGyroCalibrationData(new float[]{data[0]/calibrateTime, data[1]/calibrateTime, data[2]/calibrateTime});
				view.setText("x: " + data[0]/calibrateTime + " y: " + data[1]/calibrateTime + " z: " + data[2]/calibrateTime);
			}
		}, calibrateTime);

		
		
	}
}
