package edu.cmu.ri.rcommerce.ServiceTester;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class CalibrateCompassActivity extends Activity {		
	SensorManager sensors;
    private TextView disp;
    private float compassOffset;
    private int MAGNETOMETER_UPDATE_RATE = SensorManager.SENSOR_DELAY_UI;
    private SensorEventListener compassListener = null;

	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.compass);
        disp = (TextView)findViewById(R.id.disp);   
        sensors = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        initializeMagnetometer();
        Button button = (Button) findViewById(R.id.tagCompass);
		button.setOnClickListener(mOnTagListener);
    }
    
    private OnClickListener mOnTagListener = new OnClickListener() {
		public void onClick(View v) {
			sensors.unregisterListener(compassListener);
			disp.setText("" + compassOffset);
			Intent i = new Intent();
			i.putExtra("compassOffset", compassOffset);
			setResult(RESULT_OK, i);
			finish();
		}
	};
    
    void initializeMagnetometer() {
		Sensor magnetic = sensors.getDefaultSensor(Sensor.TYPE_ORIENTATION);
		if (magnetic == null) {
			AlertDialog.Builder b = new AlertDialog.Builder(this);
			b.setMessage("Unable to find magnetic sensor! Some functionality will not work").show();
		}
		compassListener = new SensorEventListener() {

			@Override
			public void onSensorChanged(SensorEvent event) {
				//FOR NSH1 ONLY
				float compassNorthOffset = 360 - event.values[0];
				float northToMapNorth = compassNorthOffset + 4.7f;
				compassOffset = 180 - northToMapNorth;
				disp.setText("" + compassOffset);
			}

			@Override
			public void onAccuracyChanged(Sensor sensor, int accuracy) {
			
			}
		};
		sensors.registerListener(compassListener, magnetic, MAGNETOMETER_UPDATE_RATE);
	}

	@Override
	protected void onDestroy() {
		sensors.unregisterListener(compassListener);
		compassListener = null;
		sensors = null;
		disp = null;
		super.onDestroy();
	}
    
    
}