package edu.cmu.ri.rcommerce.AndroidOnlinePF;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.Vibrator;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import edu.cmu.ri.rcommerce.FastRandom;
import edu.cmu.ri.rcommerce.MessageBuilder;
import edu.cmu.ri.rcommerce.Messages.AccelInfo;
import edu.cmu.ri.rcommerce.Messages.Accuracy;
import edu.cmu.ri.rcommerce.Messages.GyroInfo;
import edu.cmu.ri.rcommerce.Messages.MagneticInfo;
import edu.cmu.ri.rcommerce.Messages.MessageWrapper;
import edu.cmu.ri.rcommerce.Messages.MessageWrapper.Type;
import edu.cmu.ri.rcommerce.Messages.TagInfo;
import edu.cmu.ri.rcommerce.Messages.WifiScan;
import edu.cmu.ri.rcommerce.ObstacleMap;
import edu.cmu.ri.rcommerce.filter.PedestrianLocalization_Gyro;
import edu.cmu.ri.rcommerce.filter.PositionStatisticsCalculator;
import edu.cmu.ri.rcommerce.particleFilter.DeadReckoningGaussianUpdater;
import edu.cmu.ri.rcommerce.particleFilter.ImportanceResampler;
import edu.cmu.ri.rcommerce.particleFilter.Measurer;
import edu.cmu.ri.rcommerce.particleFilter.Particle2D;
import edu.cmu.ri.rcommerce.particleFilter.ParticleFilter;
import edu.cmu.ri.rcommerce.particleFilter.Resampler;
import edu.cmu.ri.rcommerce.particleFilter.WifiRSSIRuntimeProvider;
import edu.cmu.ri.rcommerce.sensor.MapConstrainedRSSIMeasurer2D;
import edu.cmu.ri.rcommerce.sensor.RSSICalibrateProvider;
import edu.cmu.ri.rcommerce.sensor.SensorReading;
import edu.cmu.ri.rcommerce.sensor.WifiSignalMapRSSICalibrateProviderKNN;

/**
 * This activity is the main implementation of the particle filter localization
 * algorithms tailored to run on the Android platform
 * 
 * TO RUN THIS: 1. Load the associated robot map (map.map) and WiFi fingerprint
 * (wifi and gsm fingerprint.txt) into the logs/ folder on the SD Card 2. Start
 * the activity 3. Press the "Load Data" button on the screen and wait for the
 * screen to display "Data Loaded and Ready for PF Localization!" (there will
 * also be a slight vibration alert) 4. Set the phone on a solid surface and
 * calibrate the gyroscope by pressing the menu button and choosing
 * "Calibrate Gyro". After 10 seconds the offsets will display on the screen and
 * you can press the back button 5. When ready, press the "Start PF" button;
 * please remain in one place while the screen displays
 * "waiting for wifi convergence" 6. When finished, press the "Stop PF" button
 * 7. Should you be testing the accuracy of the program, pressing the
 * "Tag State" button will tag the next output state for comparison 8. Check the
 * SD card for a .pf(online log) file and a .log(offline log) file; Copy to a PC
 * for processing.
 * 
 * @author Evan
 * 
 */
public class AndroidOnlinePFActivity extends Activity {
	WifiManager wifi;
	// TelephonyManager gsm;
	// LocationManager gps;
	SensorManager sensors;
	PowerManager power;
	Vibrator vibrate;

	WakeLock wakeLock;

	final int GSM_SCAN_RATE = 1000; // in milliseconds
	final int WIFI_SCAN_RATE = 1000; // in milliseconds
	final int GPS_UPDATE_RATE = 50; // in milliseconds
	final int TRIGGER_CHECK_RATE = 200; // in milliseconds
	final int MAGNETOMETER_UPDATE_RATE = SensorManager.SENSOR_DELAY_UI;
	final int ACCELEROMETER_UPDATE_RATE = SensorManager.SENSOR_DELAY_UI;
	final int GYRO_UPDATE_RATE = SensorManager.SENSOR_DELAY_UI;

	/**
	 * IMPORTANT MAGIC NUMBER - This represents the offset between the map's
	 * north and what the phone thinks is north. This is very finicky.
	 * 
	 * @radians
	 */
	final float ORIENTATION_OFFSET = 2.7f;// in radians

	PedestrianLocalization_Gyro margLocalization;

	boolean offlineLog = false;

	final String LOG_MESSAGE = "logState";
	final String POS_MESSAGE = "PoseRequest";

	Location gpsStartLocation; // used for converting GPS coordinate to local
	// x,y frame

	BroadcastReceiver wifiBroadcastReciever;

	static final String STD_DEV_IDENTIFIER = "StdDev", GPS_IDENTIFIER = "GPS",
			STEP_IDENTIFIER = "Step", MARG_IDENTIFIER = "MARG";

	DecimalFormat nearestWholeFormat = new DecimalFormat("####");
	DecimalFormat oneDecimalFormat = new DecimalFormat("####.#");

	final Context context = this;

	boolean calibrating = false;
	PositionStatisticsCalculator calibrationStatistics;

	int currentGSMSignal = -1;

	enum LocMethod {
		Wifi, Gsm
	};

	final static LocMethod method = LocMethod.Wifi;

	boolean flag;
	ParticleFilter<Particle2D> filter;
	TextView disp;
	Button loadData;
	Button stopPF;
	Button startPF;
	Button tag_state;
	String correlatedData;
	String obstacleMap;
	String output;
	String offlineLogName;
	File logDir;
	BufferedWriter out = null;
	AsyncTask<Void, String, Void> task;
	AsyncTask<Void, String, Void> load;
	DeadReckoningGaussianUpdater updater = null;

	WifiRSSIRuntimeProvider rp = null;

	int iterationCounter = 0;
	SensorEventListener accelerometerListener;
	SensorEventListener magnetometerListener;
	SensorEventListener gyroscopeListener;

	boolean wifiConvergenceWait;
	boolean tag;

	OutputStream logOutputStream;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		setContentView(R.layout.main);
		disp = (TextView) findViewById(R.id.disp);
		disp.setText("App Loaded.");

		loadData = (Button) findViewById(R.id.loadData);
		loadData.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				disp.setText("Starting Data Loading...");
				load = new runLoadDataTask().execute();
			}
		});

		stopPF = (Button) findViewById(R.id.stopPF);
		stopPF.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				offlineLog = false;
				sensors.unregisterListener(accelerometerListener);
				sensors.unregisterListener(magnetometerListener);
				sensors.unregisterListener(gyroscopeListener);
				flag = false;
				disp.setText("Stopping...");
				task.cancel(true);
				load.cancel(true);
				try {
					if (out != null)
						out.close();
					if (logOutputStream != null) {
						OutputStream stream = logOutputStream;
						logOutputStream = null;
						try {
							stream.flush();
							if (stream instanceof FileOutputStream)
								((FileOutputStream) stream).getFD().sync();
							stream.close();
						} catch (IOException e) {
							Log.e("IO", "error trying to close stream: " + e.getMessage());
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				if (wakeLock.isHeld())
					wakeLock.release();
				finish();
			}
		});

		startPF = (Button) findViewById(R.id.startPF);
		startPF.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				disp.setText("Starting PF...");
				if (!wakeLock.isHeld())
					wakeLock.acquire();
				task = new runPFTask().execute();
				offlineLog = true;
			}
		});

		tag_state = (Button) findViewById(R.id.tag_state);
		tag_state.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				tag = true;
				try {
					logTag(logOutputStream);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});

		power = (PowerManager) getSystemService(Context.POWER_SERVICE);
		wakeLock = power.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "AndroidOnlinePF");
		vibrate = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		sensors = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

		initializeLogIO();
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
		initializeGyro();

		margLocalization = new PedestrianLocalization_Gyro(new edu.cmu.ri.rcommerce.LocationListener() {
			long prevtime = 0;

			@Override
			public void broadcastLocationUpdate(double x, double y, double theta, long time, double velocity) {

				if (prevtime == 0)
					prevtime = time;
				if (updater != null) {
					if (!wifiConvergenceWait) {
						// This is where the updater is separated from the
						// particle filter iteration process
						// These are the parameters to tweak the behavior of the
						// dead reckoning portion
						updater.setParameters(1.3f, 5f, (float) theta, 0.2f, (time - prevtime) / 1e3f);
						filter.setCurrentState(updater.update(filter.getCurrentState()));
					}
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
		Toast.makeText(context, "Please Calibrate Gyro!", Toast.LENGTH_LONG).show();
		wifiConvergenceWait = true;
		tag = false;
	}

	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.clear();
		menu.add(Menu.NONE, 1, 0, "Calibrate Gyro");
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		Intent i = new Intent();
		i.setClassName(AndroidOnlinePFActivity.this, CalibrateGyro.class.getName());
		startActivityForResult(i, 0);
		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 0)
			Toast.makeText(context, "Gyro Calibrated!", Toast.LENGTH_SHORT).show();
		super.onActivityResult(requestCode, resultCode, data);
	}

	protected void onDestroy() {
		task.cancel(true);
		load.cancel(true);
		unregisterReceiver(wifiBroadcastReciever);
		super.onDestroy();
	}

	/**
	 * Initializes the hard-coded log files and alerts via {@link Toast}
	 */
	private void initializeLogIO() {
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) == false) {
			Toast.makeText(context, "SD CARD NOT DOES NOT HAVE READ/WRITE ACCESS!", Toast.LENGTH_LONG).show();
		} else {
			File externalRoot = Environment.getExternalStorageDirectory();
			logDir = new File(externalRoot, "logs/");
			correlatedData = logDir.getAbsolutePath() + "/" + "wifi and gsm dump.txt";
			obstacleMap = logDir.getAbsolutePath() + "/" + "map.map";
			output = "pfout.pf";
			offlineLogName = "offlineLog.log";

			File outFile = new File(logDir, output);
			if (!outFile.exists())
				try {
					outFile.createNewFile();
				} catch (IOException e3) {
					e3.printStackTrace();
				}
			try {
				out = new BufferedWriter(new FileWriter(outFile));
			} catch (IOException e2) {
				e2.printStackTrace();
			}

			File outFileOffline = new File(logDir, offlineLogName);
			if (!outFileOffline.exists())
				try {
					outFileOffline.createNewFile();
				} catch (IOException e3) {
					e3.printStackTrace();
				}
			try {
				logOutputStream = new FileOutputStream(outFileOffline);
			} catch (IOException e2) {
				e2.printStackTrace();
			}
		}
	}

	/**
	 * This handles the threading for the particle filter computations
	 * 
	 * Android has the very nice {@link AsyncTask} to handle threading with
	 * updates to the UI thread
	 * 
	 * @author Evan
	 * 
	 */
	private class runPFTask extends AsyncTask<Void, String, Void> {

		@Override
		protected void onProgressUpdate(String... values) {
			disp.setText(values[0]);
			super.onProgressUpdate(values);
		}

		@Override
		protected Void doInBackground(Void... params) {
			flag = true;
			// reset Localization on start of PF
			margLocalization.reset();
			margLocalization.setOrientationOffset(ORIENTATION_OFFSET);
			runPF();
			return null;
		}

		/**
		 * Primary PF loop
		 */
		protected void runPF() {
			while (flag) {
				isCancelled();
				if (iterationCounter == 5) {
					wifiConvergenceWait = false;
				} else if (iterationCounter < 5) {
					publishProgress("Please wait for WiFi Convergence before moving!");
				}
				if (wifiConvergenceWait)
					filter.iterate();
				else
					filter.iterateWithoutUpdater();
				List<Particle2D> state = filter.getCurrentState();
				try {
					if (tag == true) {
						out.write("NS:" + iterationCounter + " " + System.currentTimeMillis() + " TAGGED\n");
						tag = false;
					} else {
						out.write("NS:" + iterationCounter + " " + System.currentTimeMillis() + "\n");
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				for (Particle2D p : state) {
					try {
						out.write(p.x + " " + p.y + "\n");
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				publishProgress("Iteration: " + iterationCounter++ + "\n" + "Sample X:" + filter.getCurrentState().get(0).x + " Sample Y:" + filter.getCurrentState().get(0).y);
			}
		}
	}

	/**
	 * Handles loading of Obstacle Map and WiFi Fingerprint data Again, the
	 * {@link AsyncTask} handles this so everything is easy and UI updates are
	 * possible
	 * 
	 * @author Evan
	 * 
	 */
	private class runLoadDataTask extends AsyncTask<Void, String, Void> {

		@Override
		protected void onPostExecute(Void result) {
			disp.setText("Data Loaded and Ready for PF Localization!");
			vibrate.vibrate(100);
			super.onPostExecute(result);
		}

		@Override
		protected void onProgressUpdate(String... values) {
			disp.setText(values[0]);
			super.onProgressUpdate(values);
		}

		@Override
		protected Void doInBackground(Void... params) {
			ObstacleMap map = loadMap();
			publishProgress("Passing Map to PF...");
			setupPF(map);
			return null;
		}

		protected ObstacleMap loadMap() {
			publishProgress("Loading Obstacle Map...");
			ObstacleMap map = null;
			try {
				map = ObstacleMap.loadFromStream(new FileReader(obstacleMap));
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			publishProgress("Map Loaded.");
			return map;
		}

		protected void setupPF(ObstacleMap map) {
			filter = null;
			try {
				filter = setupFilter(correlatedData, map);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}

		/**
		 * This takes in all the data and combines it into one ParticleFilter
		 * object that can be easily iterated.
		 * 
		 * *NOTE:This is where the starting state initialization occurs
		 * 
		 * @param correlatedData
		 *            {@link String} representing the WiFi fingerprint file
		 *            location
		 * @param map
		 *            {@link ObstacleMap} representing the robot map data
		 * @return a {@link ParticleFilter} containing updater, resampler, and
		 *         measurer objects
		 * @throws IOException
		 */
		ParticleFilter<Particle2D> setupFilter(String correlatedData, ObstacleMap map) throws IOException {
			FastRandom random = new FastRandom();
			publishProgress("Loading Signal Map...");
			RSSICalibrateProvider<Particle2D> signalMap = new WifiSignalMapRSSICalibrateProviderKNN(new BufferedReader(new FileReader(correlatedData)), 3);
			publishProgress("Signal Map Loaded. Init Runtime Provider");
			rp = new WifiRSSIRuntimeProvider();
			publishProgress("Initializing Starting State...");
			java.util.List<Particle2D> startingState = new ArrayList<Particle2D>();
			for (int i = 0; i < 300; i++)
				// TODO ADJUST THIS!!!
				// startingState.add(new Particle2D(random.nextFloat() * 100 -
				// 60, random.nextFloat() * 100 - 60));
				startingState.add(new Particle2D(random.nextFloat() * 10 - 5, random.nextFloat() * 10 - 5));

			publishProgress("Starting State initialized. Init updater");
			// updater = new DeadReckoningGaussianUpdater(random, 1.5f, 0.2f,
			// 0.0f, 5f, 0);
			updater = new DeadReckoningGaussianUpdater(random, 0, 2f, 0, (float) (2 * Math.PI), 1);
			publishProgress("Init measurer");
			Measurer<Particle2D> measurer = new MapConstrainedRSSIMeasurer2D<Particle2D>(rp, signalMap, 0.5f, map);
			// Measurer<Particle2D> measurer = new
			// MapConstrainMeasurer<Particle2D>(map);
			publishProgress("Init resampler");
			Resampler<Particle2D> resampler = new ImportanceResampler<Particle2D>();
			// Resampler<Particle2D> resampler = new
			// ImpossibleParticleResampler<Particle2D>();
			publishProgress("Init filter");
			ParticleFilter<Particle2D> filter = new ParticleFilter<Particle2D>(startingState, updater, measurer, resampler);
			publishProgress("Return Filter");
			return filter;
		}
	}

	// Below follows all the initialization for the various sensors as well as
	// the associated methods to do a straight log of all incoming sensor data
	// without processing (so that offline logs can be compared against the
	// online information)

	void initializeWifi() {
		wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		wifiBroadcastReciever = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				List<ScanResult> wifiResults = wifi.getScanResults();
				if (offlineLog)
					try {
						logWifiResults(wifiResults, logOutputStream);
					} catch (IOException e) {
						e.printStackTrace();
					}
				// Try this method of passing wifi results to update the
				// runtime provider for PF
				if (rp != null)
					rp.setNewReading(wifiResults);

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
				if (offlineLog)
					try {
						logAccelerometerResults(event, logOutputStream);
					} catch (Exception e) {
						e.printStackTrace();
					}
				SensorReading reading = new SensorReading(event.timestamp, event.values, SensorReading.ACCELEROMETER_SENSOR);
				margLocalization.addAccelerometerReadings(new SensorReading[] { reading });

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
				if (offlineLog)
					try {
						logMagnetometerResults(event, logOutputStream);
					} catch (IOException e) {
						e.printStackTrace();
					}
				SensorReading reading = new SensorReading(event.timestamp, event.values, SensorReading.MAGNETOMETER_SENSOR);
				margLocalization.addMagnetometerReadings(new SensorReading[] { reading });
			}

			@Override
			public void onAccuracyChanged(Sensor sensor, int accuracy) {
				if (accuracy < Accuracy.HIGH.getNumber()) {
					AlertDialog.Builder b = new AlertDialog.Builder(context);
					b.setMessage("Please calibrate magnetometer").show();
				}
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
				if (offlineLog)
					try {
						logGyroResults(event, logOutputStream);
					} catch (IOException e) {
						e.printStackTrace();
					}
				SensorReading reading = new SensorReading(event.timestamp, event.values, SensorReading.GYRO_SENSOR);
				margLocalization.addGyroReadings(new SensorReading[] { reading });

			}

			@Override
			public void onAccuracyChanged(Sensor sensor, int accuracy) {
			}
		};
		sensors.registerListener(gyroscopeListener, gyro, GYRO_UPDATE_RATE);
	}

	void logTag(OutputStream out) throws IOException {
		if (out == null)
			return;
		TagInfo.Builder tagMessageBuilder = TagInfo.newBuilder();

		tagMessageBuilder.setTag(true);

		TagInfo message = tagMessageBuilder.build();

		MessageWrapper wrappedMessage = MessageWrapper.newBuilder().setType(Type.TagInfo).setTagInfo(message).build();

		wrappedMessage.writeDelimitedTo(out);
	}

	void logAccelerometerResults(SensorEvent results, OutputStream out) throws IOException {
		if (out != null) {
			AccelInfo.Builder accelMessageBuilder = AccelInfo.newBuilder();

			accelMessageBuilder.setAccuracy(Accuracy.valueOf(results.accuracy)).setTimestamp(results.timestamp).setX(results.values[0]).setY(results.values[1]).setZ(results.values[2]);

			AccelInfo message = accelMessageBuilder.build();

			MessageWrapper wrappedMessage = MessageWrapper.newBuilder().setType(Type.AccelInfo).setAccelInfo(message).build();

			wrappedMessage.writeDelimitedTo(out);
		}
	}

	void logGyroResults(SensorEvent results, OutputStream out) throws IOException {
		if (out != null) {
			GyroInfo.Builder gyroMessageBuilder = GyroInfo.newBuilder();

			gyroMessageBuilder.setAccuracy(Accuracy.valueOf(results.accuracy)).setTimestamp(results.timestamp).setX(results.values[0]).setY(results.values[1]).setZ(results.values[2]);

			GyroInfo message = gyroMessageBuilder.build();

			MessageWrapper wrappedMessage = MessageWrapper.newBuilder().setType(Type.GyroInfo).setGyroInfo(message).build();

			wrappedMessage.writeDelimitedTo(out);
		}
	}

	void logMagnetometerResults(SensorEvent results, OutputStream out) throws IOException {
		if (out == null)
			return;
		MagneticInfo.Builder magMessageBuilder = MagneticInfo.newBuilder();

		magMessageBuilder.setAccuracy(Accuracy.valueOf(results.accuracy)).setTimestamp(results.timestamp).setX(results.values[0]).setY(results.values[1]).setZ(results.values[2]);

		MagneticInfo message = magMessageBuilder.build();

		MessageWrapper wrappedMessage = MessageWrapper.newBuilder().setType(Type.MagneticInfo).setMagneticInfo(message).build();

		wrappedMessage.writeDelimitedTo(out);

	}

	void logWifiResults(final List<ScanResult> results, OutputStream out) throws IOException {
		if (out == null)
			return;
		WifiScan message = MessageBuilder.buildWifiScanMessage(results);

		MessageWrapper wrappedMessage = MessageWrapper.newBuilder().setType(Type.WifiScan).setWifiScan(message).build();
		wrappedMessage.writeDelimitedTo(out);

	}

}