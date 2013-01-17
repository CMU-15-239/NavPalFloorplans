package edu.cmu.ri.rcommerce;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import edu.cmu.ri.rcommerce.filter.PedestrianLocalization_Gyro;
import edu.cmu.ri.rcommerce.filter.PedestrianLocalization_StandardDeviation;
import edu.cmu.ri.rcommerce.particleFilter.DeadReckoningGaussianUpdater;
import edu.cmu.ri.rcommerce.particleFilter.Particle2D;
import edu.cmu.ri.rcommerce.particleFilter.ParticleFilter;
import edu.cmu.ri.rcommerce.particleFilter.WifiRSSIRuntimeProvider;
import edu.cmu.ri.rcommerce.sensor.RSSIMeasurer;
import edu.cmu.ri.rcommerce.sensor.SensorReading;

/**
 * This is an example of implementing an application service that runs locally
 * in the same process as the application. The
 * {@link LocalServiceActivities.Controller} and
 * {@link LocalServiceActivities.Binding} classes show how to interact with the
 * service.
 * 
 * <p>
 * Notice the use of the {@link NotificationManager} when interesting things
 * happen in the service. This is generally how background services should
 * interact with the user, rather than doing something more disruptive such as
 * calling startActivity().
 */

public class LocalizationService extends Service {

	private static final int ONGOING_NOTIFICATION = 182;
	private WifiManager wifi;
	private SensorManager sensors;

	private final int WIFI_SCAN_RATE = 1000; // in milliseconds
	private final int MAGNETOMETER_UPDATE_RATE = SensorManager.SENSOR_DELAY_UI;
	private final int ACCELEROMETER_UPDATE_RATE = SensorManager.SENSOR_DELAY_UI;
	private final int GYRO_UPDATE_RATE = SensorManager.SENSOR_DELAY_UI;
	//OFFSET FOR NSH 2 with VUnit-1  //private final float ORIENTATION_OFFSET = 2.3f;
	private float ORIENTATION_OFFSET = 2.2f;
	private float WALKING_VELOCITY = 1.3f;
	
	private PedestrianLocalization_Gyro margLocalization;
	private PedestrianLocalization_StandardDeviation stdDevLocalization;

	private SensorEventListener accelerometerListener;
	private SensorEventListener magnetometerListener;
	private SensorEventListener gyroscopeListener;

	private boolean wifiConvergenceWait;

	private boolean flag;
	private ParticleFilter<Particle2D> filter;

	private BroadcastReceiver wifiBroadcastReciever;

	private DeadReckoningGaussianUpdater updater = null;
	private WifiRSSIRuntimeProvider rp = null;

	private int iterationCounter = 0;

	public final static String MESSAGE = "LocalizationService";
	private Thread pfThread;
	private boolean hasGyro;

	/**
	 * Class for clients to access. Because we know this service always runs in
	 * the same process as its clients, we don't need to deal with IPC.
	 */
	public class LocalBinder extends Binder {
		public LocalizationService getService() {
			return LocalizationService.this;
		}
	}

	@Override
	public void onCreate() {
		sensors = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

		// Gravity, Linear Acceleration, and Rotation Vector 'sensors' are not
		// compatible with Android versions older than 9 (Gingerbread)
		if (Integer.parseInt(android.os.Build.VERSION.SDK) >= 9) {
			// initializeGravity();
			// initializeLinearAccel();
			// initializeRotation();
		}

		// GSM and Wifi are currently not compatible with Android OS versions
		// older than 7
		if (Integer.parseInt(android.os.Build.VERSION.SDK) >= 7) {
			initializeWifi();
			// initializeGSM();
			// initializeGPS();
		} else {
			Log.d("Compat", "Not initializing Wifi,GSM,GPS since device is older than Android 2.1");
		}

		initializeMagnetometer();
		initializeAccelerometer();
		
		if(sensors.getSensorList(Sensor.TYPE_GYROSCOPE).size() >0) {
			//HAS GYRO
			hasGyro = true;
			initializeGyro();
			margLocalization = new PedestrianLocalization_Gyro(new edu.cmu.ri.rcommerce.LocationListener() {
				long prevtime = 0;

				@Override
				public void broadcastLocationUpdate(double x, double y, double theta, long time, double velocity) {
					float v = 0;
					if (prevtime == 0)
						prevtime = time;
					if (updater != null && !wifiConvergenceWait) {
						if(velocity != 0)
							v = WALKING_VELOCITY;
						updater.setParameters(v, 5f, (float) theta, 0.2f, (time - prevtime) / 1e3f);
						// enables more updater 'updating' without the rest of the
						// iterator running
						filter.setCurrentState(updater.update(filter.getCurrentState()));
					}

					prevtime = time;
				}

				@Override
				public void broadcastLocationStatusChange(int status) {

				}

				@Override
				public void broadcastRelativeLocationUpdate(double dR, double dTheta, double timeDiff) {

				}
			}, false);
			margLocalization.reset();
		}
		else {
			hasGyro = false;
		stdDevLocalization = new PedestrianLocalization_StandardDeviation(new edu.cmu.ri.rcommerce.LocationListener() {
			long prevtime = 0;
			@Override
			public void broadcastLocationUpdate(double x, double y, double theta, long time, double velocity) {
				float v = 0;
				if (prevtime == 0)
					prevtime = time;
				if (updater != null && !wifiConvergenceWait) {
					if(velocity != 0)
						v = WALKING_VELOCITY;
					updater.setParameters(v, 5f, (float) theta, 0.2f, (time - prevtime) / 1e3f);
					// enables more updater 'updating' without the rest of the
					// iterator running
					filter.setCurrentState(updater.update(filter.getCurrentState()));
				}

				prevtime = time;

			}

			@Override
			public void broadcastLocationStatusChange(int status) {
				
			}

			@Override
			public void broadcastRelativeLocationUpdate(double dR, double dTheta, double timeDiff) {
				// TODO Auto-generated method stub

			}
		}, false);
		stdDevLocalization.reset();
		}
		
		wifiConvergenceWait = true;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i("localizationService", "Received start id " + startId + ": " + intent);
		if(hasGyro) {
		margLocalization.reset();
		margLocalization.setOrientationOffset(ORIENTATION_OFFSET);
		}
		else
		{
			stdDevLocalization.reset();
			stdDevLocalization.setOrientationOffset(ORIENTATION_OFFSET);
		}
		flag = true;
		pfThread = new Thread(new Runnable() {
			public void run() {
				while (flag) {
					if (iterationCounter == 5) {
						wifiConvergenceWait = false;
					}
					if (wifiConvergenceWait)
						filter.iterate();
					else
						filter.iterateWithoutUpdater();
					ArrayList<Particle2D> state = (ArrayList<Particle2D>) filter.getCurrentState();
					// Do something with the state
					Particle2D center = cloudToPoint(state);
					float radius = bubbleRadius(state, center);

					Intent i = new Intent(MESSAGE);
					i.putExtra("center", center);
					i.putExtra("radius", radius);
					i.putParcelableArrayListExtra("points", state);
					//Log.i("localizationService", "BROADCAST LOC: " + center.x + " " + center.y + " " + radius);
					sendBroadcast(i);
					iterationCounter++;
				}
			}
		});
		pfThread.start();
		//Log.i("localizationService", "Supposedly started the main thread");
		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		unregisterReceiver(wifiBroadcastReciever);
		sensors.unregisterListener(accelerometerListener);
		sensors.unregisterListener(magnetometerListener);
		sensors.unregisterListener(gyroscopeListener);
		flag = false;
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	public void setParticleFilter(ParticleFilter<Particle2D> pf) {
		filter = pf;
		RSSIMeasurer<Particle2D> measurer = (RSSIMeasurer<Particle2D>) filter.measurer;
		rp = (WifiRSSIRuntimeProvider) measurer.runtimeProvider;
		updater = (DeadReckoningGaussianUpdater) filter.updater;
	}
	
	public void setOrientationOffset(float offset) {
		ORIENTATION_OFFSET = offset;
	}
	public void setWalkingVelocity(float v) {
		WALKING_VELOCITY = v;
	}

	// This is the object that receives interactions from clients. See
	// RemoteService for a more complete example.
	private final IBinder mBinder = new LocalBinder();

	void initializeWifi() {
		wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		wifiBroadcastReciever = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {

				List<ScanResult> wifiResults = wifi.getScanResults();
				// Try this method of passing wifi results to update the
				// runtime provider for PF
				if (rp != null) {
					rp.setNewReading(wifiResults);
					//Log.d("localizationService", "Actually logging WIFI results");
				}

			}
		};
		registerReceiver(wifiBroadcastReciever, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

		Timer wifiTimer = new Timer();
		wifiTimer.scheduleAtFixedRate(new TimerTask() {

			@Override
			public void run() {
				wifi.startScan();

			}
		}, WIFI_SCAN_RATE, WIFI_SCAN_RATE);
	}

	void initializeAccelerometer() {
		Sensor accelerometer = sensors.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		accelerometerListener = new SensorEventListener() {

			@Override
			public void onSensorChanged(SensorEvent event) {
				SensorReading reading = new SensorReading(event.timestamp, event.values, SensorReading.ACCELEROMETER_SENSOR);
				if(hasGyro)
				margLocalization.addAccelerometerReadings(new SensorReading[] { reading });
				else
					stdDevLocalization.addAccelerometerReadings(new SensorReading[] { reading });
			}

			@Override
			public void onAccuracyChanged(Sensor sensor, int accuracy) {

			}
		};
		sensors.registerListener(accelerometerListener, accelerometer, ACCELEROMETER_UPDATE_RATE);
	}

	void initializeMagnetometer() {
		Sensor magnetic = sensors.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
		if (magnetic == null) {
			AlertDialog.Builder b = new AlertDialog.Builder(this);
			b.setMessage("Unable to find magnetic sensor! Some functionality will not work").show();
		}
		magnetometerListener = new SensorEventListener() {

			@Override
			public void onSensorChanged(SensorEvent event) {
				SensorReading reading = new SensorReading(event.timestamp, event.values, SensorReading.MAGNETOMETER_SENSOR);
				if(hasGyro)
				margLocalization.addMagnetometerReadings(new SensorReading[] { reading });
				else
					stdDevLocalization.addMagnetometerReadings(new SensorReading[] { reading });
			}

			@Override
			public void onAccuracyChanged(Sensor sensor, int accuracy) {
			}
		};
		sensors.registerListener(magnetometerListener, magnetic, MAGNETOMETER_UPDATE_RATE);
	}

	void initializeGyro() {
		Sensor gyro = sensors.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
		if (gyro == null) {
			AlertDialog.Builder b = new AlertDialog.Builder(this);
			b.setMessage("Unable to find gyro sensor! Some functionality will not work").show();
		}

		gyroscopeListener = new SensorEventListener() {

			@Override
			public void onSensorChanged(SensorEvent event) {
				SensorReading reading = new SensorReading(event.timestamp, event.values, SensorReading.GYRO_SENSOR);
				margLocalization.addGyroReadings(new SensorReading[] { reading });
			}

			@Override
			public void onAccuracyChanged(Sensor sensor, int accuracy) {
			}
		};
		sensors.registerListener(gyroscopeListener, gyro, GYRO_UPDATE_RATE);
	}

	private Particle2D cloudToPoint(List<Particle2D> state) {
		Iterator<Particle2D> itr = state.iterator();
		float x = 0;
		float y = 0;
		while (itr.hasNext()) {
			Particle2D p = itr.next();
			x += p.x;
			y += p.y;
		}
		x /= state.size();
		y /= state.size();
		
		Particle2D mean = new Particle2D(x,y);
		Particle2D bestPoint = null;
		float minDist = Float.MAX_VALUE;
		for(Particle2D p : state) {
			float d = distanceBetweenPoints(p, mean);
			if(d < minDist) {
				minDist = d;
				bestPoint = new Particle2D(p.x,p.y);
			}
			
		}

		return bestPoint;
	}

	// Takes the current particle cloud and the center point and finds the
	// largest distance from that particle to any other partlce
	// One standard deviation
	private float bubbleRadius(List<Particle2D> state, Particle2D center) {
		Iterator<Particle2D> itr = state.iterator();
		float sum = 0;
		float stddev = 0;
		float avg = 0;
		while (itr.hasNext()) {
			Particle2D p = itr.next();
			avg += distanceBetweenPoints(center, p);
		}
		avg /= state.size();
		itr = state.iterator();
		while (itr.hasNext()) {
			Particle2D p = itr.next();
			sum += Math.pow(distanceBetweenPoints(center, p) - avg, 2);
		}

		stddev = (float) Math.sqrt((sum / state.size()));
		return stddev;
	}

	// Find the geometric distance between to particles
	public float distanceBetweenPoints(Particle2D p1, Particle2D p2) {
		return (float) Math.sqrt(Math.pow(p1.x - p2.x, 2) + Math.pow(p1.y - p2.y, 2));
	}

}
