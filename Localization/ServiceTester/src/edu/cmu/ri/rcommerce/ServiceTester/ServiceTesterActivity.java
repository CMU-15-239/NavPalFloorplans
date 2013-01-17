package edu.cmu.ri.rcommerce.ServiceTester;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
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
import edu.cmu.ri.rcommerce.LocalizationService;
import edu.cmu.ri.rcommerce.ObstacleMap;
import edu.cmu.ri.rcommerce.particleFilter.DeadReckoningGaussianUpdater;
import edu.cmu.ri.rcommerce.particleFilter.ImportanceResampler;
import edu.cmu.ri.rcommerce.particleFilter.Measurer;
import edu.cmu.ri.rcommerce.particleFilter.Particle2D;
import edu.cmu.ri.rcommerce.particleFilter.ParticleFilter;
import edu.cmu.ri.rcommerce.particleFilter.Resampler;
import edu.cmu.ri.rcommerce.particleFilter.WifiRSSIRuntimeProvider;
import edu.cmu.ri.rcommerce.sensor.MapConstrainedRSSIMeasurer2D;
import edu.cmu.ri.rcommerce.sensor.RSSICalibrateProvider;
import edu.cmu.ri.rcommerce.sensor.WifiSignalMapRSSICalibrateProviderKNN;

public class ServiceTesterActivity extends Activity {

	final Context context = this;
	private boolean mIsBound;
	TextView disp;

	AsyncTask<Void, String, Void> load;
	String correlatedData;
	String obstacleMap;
	String output;
	String offlineLogName;
	File logDir;
	BufferedWriter out = null;
	OutputStream logOutputStream;

	DeadReckoningGaussianUpdater updater = null;
	WifiRSSIRuntimeProvider rp = null;
	ParticleFilter<Particle2D> filter;

	Vibrator vibrate;

	private edu.cmu.ri.rcommerce.LocalizationService mBoundService;
	private newMessage messageReceiver = new newMessage();
	
	long time;

	private ServiceConnection mConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder service) {
			// This is called when the connection with the service has been
			// established, giving us the service object we can use to
			// interact with the service. Because we have bound to a explicit
			// service that we know is running in our own process, we can
			// cast its IBinder to a concrete class and directly access it.
			mBoundService = ((edu.cmu.ri.rcommerce.LocalizationService.LocalBinder) service).getService();

			// Tell the user about this for our demo.
			// no
		}

		public void onServiceDisconnected(ComponentName className) {
			// This is called when the connection with the service has been
			// unexpectedly disconnected -- that is, its process crashed.
			// Because it is running in our same process, we should never
			// see this happen.
			mBoundService = null;
		}
	};

	void doBindService() {
		// Establish a connection with the service. We use an explicit
		// class name because we want a specific service implementation that
		// we know will be running in our own process (and thus won't be
		// supporting component replacement by other applications).
		bindService(new Intent(ServiceTesterActivity.this, edu.cmu.ri.rcommerce.LocalizationService.class), mConnection, Context.BIND_AUTO_CREATE);
		mIsBound = true;
	}

	void doStartService() {
		// start the service
		startService(new Intent(ServiceTesterActivity.this, edu.cmu.ri.rcommerce.LocalizationService.class));
	}
	

	void doUnbindService() {
		if (mIsBound) {
			// Detach our existing connection.
			unbindService(mConnection);
			mIsBound = false;
		}
		stopService(new Intent(ServiceTesterActivity.this, edu.cmu.ri.rcommerce.LocalizationService.class));
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		doUnbindService();
	}

	private OnClickListener mLoadDataListener = new OnClickListener() {
		public void onClick(View v) {
			doBindService();
			load = new runLoadDataTask().execute();

		}
	};
	private OnClickListener mstartServiceListener = new OnClickListener() {
		public void onClick(View v) {
			doStartService();
			time = System.currentTimeMillis();
		}
	};

	private OnClickListener mstopServiceListener = new OnClickListener() {
		public void onClick(View v) {
			doUnbindService();
			try {
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	};

	public class newMessage extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equalsIgnoreCase(LocalizationService.MESSAGE)) {
				Bundle extra = intent.getExtras();
				Particle2D center = extra.getParcelable("center");
				float radius = extra.getFloat("radius");
				ArrayList<Particle2D> state = extra.getParcelableArrayList("points");
				Log.i("serviceTester", "RECEIVED LOC: " + center.x + " " + center.y + " " + radius);
				disp.setText("RECEIVED LOC: " + center.x + " " + center.y + " " + radius);
				try {
					out.write("NS\n");
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				for (Particle2D p : state) {
					try {
						out.write(p.x + " " + p.y + "\n");
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				Log.d("serviceTester", "Time since last compute:" + (System.currentTimeMillis() - time));
				time = System.currentTimeMillis();
			}
		}
	}

	@Override
	protected void onPause() {
		unregisterReceiver(messageReceiver);
		super.onPause();
	}

	@Override
	protected void onResume() {
		registerReceiver(messageReceiver, new IntentFilter(LocalizationService.MESSAGE));

		super.onResume();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main);
		disp = (TextView) findViewById(R.id.disp);
		disp.setText("App Loaded.");

		// Watch for button clicks.
		Button button = (Button) findViewById(R.id.loadData);
		button.setOnClickListener(mLoadDataListener);
		button = (Button) findViewById(R.id.startService);
		button.setOnClickListener(mstartServiceListener);
		button = (Button) findViewById(R.id.stopService);
		button.setOnClickListener(mstopServiceListener);
		vibrate = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		initializeLogIO();
		Toast.makeText(context, "Please Calibrate Gyro!", Toast.LENGTH_LONG).show();
	}
	
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.clear();
		menu.add(Menu.NONE, 1, 0, "Calibrate Gyro");
		menu.add(Menu.NONE, 2, 0, "Calibrate Compass");
		return true;
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getTitle().equals("Calibrate Gyro")) {
		Intent i = new Intent();
		i.setClassName(ServiceTesterActivity.this, CalibrateGyroActivity.class.getName());
		startActivityForResult(i, 0);
		return true;
		}
		else
		{
			Intent i = new Intent();
			i.setClassName(ServiceTesterActivity.this, CalibrateCompassActivity.class.getName());
			startActivityForResult(i, 1);
			return true;
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == 1338) {//Calibrate Compass activity has finished
			float offset = 0;
			offset = data.getExtras().getFloat("compassOffset");
			float radianOfset = (float)Math.toRadians((double) offset);
			mBoundService.setOrientationOffset(radianOfset);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private class runLoadDataTask extends AsyncTask<Void, String, Void> {

		@Override
		protected void onPostExecute(Void result) {
			disp.setText("Data Loaded and Ready for PF Localization!");
			vibrate.vibrate(100);
			mBoundService.setParticleFilter(filter);
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

	private void initializeLogIO() {
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) == false) {
			Toast.makeText(context, "SD CARD NOT DOES NOT HAVE READ/WRITE ACCESS!", Toast.LENGTH_LONG).show();
		} else {
			File externalRoot = Environment.getExternalStorageDirectory();
			logDir = new File(externalRoot, "logs/");
			correlatedData = logDir.getAbsolutePath() + "/" + "NSH1-Run6.txt";
			obstacleMap = logDir.getAbsolutePath() + "/" + "NSH1-Run6.map";
			output = "pfoutSERVICE.pf";
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
}