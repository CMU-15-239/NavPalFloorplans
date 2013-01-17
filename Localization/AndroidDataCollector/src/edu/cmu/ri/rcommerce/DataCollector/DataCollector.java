package edu.cmu.ri.rcommerce.DataCollector;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.Vibrator;
import android.telephony.NeighboringCellInfo;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.text.format.DateFormat;
import android.text.format.Time;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import edu.cmu.ri.rcommerce.MessageBuilder;
import edu.cmu.ri.rcommerce.Messages.AccelInfo;
import edu.cmu.ri.rcommerce.Messages.Accuracy;
import edu.cmu.ri.rcommerce.Messages.GPSInfo;
import edu.cmu.ri.rcommerce.Messages.GSMScan;
import edu.cmu.ri.rcommerce.Messages.GravityInfo;
import edu.cmu.ri.rcommerce.Messages.GyroInfo;
import edu.cmu.ri.rcommerce.Messages.LightInfo;
import edu.cmu.ri.rcommerce.Messages.LinearAccelInfo;
import edu.cmu.ri.rcommerce.Messages.MagneticInfo;
import edu.cmu.ri.rcommerce.Messages.MessageWrapper;
import edu.cmu.ri.rcommerce.Messages.MessageWrapper.Type;
import edu.cmu.ri.rcommerce.Messages.PositionInfo;
import edu.cmu.ri.rcommerce.Messages.ProximityInfo;
import edu.cmu.ri.rcommerce.Messages.RotationInfo;
import edu.cmu.ri.rcommerce.Messages.TagInfo;
import edu.cmu.ri.rcommerce.Messages.WifiScan;
import edu.cmu.ri.rcommerce.filter.PedestrianLocalization_Gyro;
import edu.cmu.ri.rcommerce.filter.PedestrianLocalization_StandardDeviation;
import edu.cmu.ri.rcommerce.filter.PedestrianLocalization_StepDetection;
import edu.cmu.ri.rcommerce.filter.PositionStatisticsCalculator;
import edu.cmu.ri.rcommerce.sensor.SensorReading;

/**
 * Activity for generating logs of phone data.
 * 
 * Currently tracks GPS, GSM, Wifi, accelerometer, magnetometer, gyroscope,
 * light, and proximity as well as the derived estimates for gravity, linear
 * acceleration, rotation, orientation, and position (step-based method,
 * standard deviation method, and gyroscope enhanced method)
 * 
 * @author Nisarg
 */
public class DataCollector extends Activity
{
	private static int MS_IN_SEC = 1000;
	private static int DURATION_TO_CHECK_FOR_NEW_FILE = MS_IN_SEC * 5;
	private static String LOG_FILE_ATTRIBUTES_FILENAME = "logFileAttributes.txt";

	private Thread thread = null;
    private boolean activeThread = true;
    private Calendar stopLoggingDate;
	private Calendar currentDateTime;
	private static String TAG = "THREAD";
	//private boolean automaticLogFileCreation = true;
	//private boolean alreadyLogging = false;
	private int newLogHour = 8;	  // Represents 8AM
	private int newLogMinute = 0; // Represents 0 minute of the new log hour
	private int newLogSecond = 0;

	// Added by Gary Giger to improve code readability
	// Create menu ids for each menu option
    private static final int MENU_ID_STOP_LOGGING      = Menu.FIRST;
    private static final int MENU_ID_START_LOGGING     = Menu.FIRST + 1;
    private static final int MENU_ID_STDDEV_THRESHOLD  = Menu.FIRST + 2;
    private static final int MENU_ID_START_CALIBRATION = Menu.FIRST + 3;
    private static final int MENU_ID_STOP_CALIBRATION  = Menu.FIRST + 4;
    private static final int MENU_ID_DISCONNECT_CLIENT = Menu.FIRST + 5;
    private static final int MENU_ID_CALIBRATE_GYRO    = Menu.FIRST + 6;
    private static final int MENU_ID_ABOUT   		   = Menu.FIRST + 7;
    private static final int MENU_ID_LOG_ATTRIBUTES    = Menu.FIRST + 8;

    // Added by Gary Giger to improve robustness of the DataLogger

    // Dialog objects used in the app
    private Dialog logFileAttributesDialog;
    //private Dialog logFileAttributesErrorDialog;
    private AtomicBoolean stopDateIsBeingModified = new AtomicBoolean();

    private Spinner spinnerPhoneLocation;
    private Spinner spinnerPhoneId;
    private Spinner spinnerPhoneOrientation;
    private CheckBox checkBoxAutomaticLogFileCreation;
    private TimePicker timePickerStopLoggingTime;

    // System Log used in Data Logger
    DataLoggerSystemLog systemLog;
    String currentLogFilename;

    // Fields used in the logFileAttributesDialog box
//    EditText editTextPhoneLocation;
//    EditText editTextPhoneId;
//    EditText editTextPhoneOrientation;
    TextView errorMsgTextView;

    DataLogFileAttributes dataLogFileAttributes;

	WifiManager wifi;
	TelephonyManager gsm;
	LocationManager gps;
	SensorManager sensors;
	PowerManager power = null;
	Vibrator vibrate;

	WakeLock wakeLock = null;

	// Used for time stamp in WIFI strength data file
	Time now = new Time();

	final int GSM_SCAN_RATE = 1000; // in milliseconds
	final int WIFI_SCAN_RATE = 1000; // in milliseconds
	final int GPS_UPDATE_RATE = 50; // in milliseconds
	final int TRIGGER_CHECK_RATE = 200; // in milliseconds
	final int MAGNETOMETER_UPDATE_RATE = SensorManager.SENSOR_DELAY_UI;
	final int ACCELEROMETER_UPDATE_RATE = SensorManager.SENSOR_DELAY_UI;
	final int GYRO_UPDATE_RATE = SensorManager.SENSOR_DELAY_UI;
	final int PROXIMITY_UPDATE_RATE = SensorManager.SENSOR_DELAY_UI;
	final int LIGHT_UPDATE_RATE = SensorManager.SENSOR_DELAY_UI;
	final int GRAVITY_UPDATE_RATE = SensorManager.SENSOR_DELAY_UI;
	final int LINEAR_ACCEL_UPDATE_RATE = SensorManager.SENSOR_DELAY_UI;
	final int ROTATION_UPDATE_RATE = SensorManager.SENSOR_DELAY_UI;

	// Make all flags false except for the WIfi access to disable the collection of all other sensors
	boolean sendWifiResults = true;
	boolean sendGSMResults = false; // true;
	boolean sendGPSResults = false; // true;
	boolean sendMagnetometerResults = false; // true;
	boolean sendAccelerometerResults = false; // true;
	boolean sendGyroResults = false; // true;
	boolean sendProximityResults = false; // true;
	boolean sendLightResults = false; // true;
	boolean sendPositionResults = false; // true;
	boolean sendGravityResults = false; // true;
	boolean sendLinearAccelResults = false; // true;
	boolean sendRotationResults = false;

	boolean allowExternalTrigger = true;

	// used by the implementation to monitor whether to needs to write to the
	// trigger output
	boolean waitingForTriggeredWifi = false;
	boolean waitingForTriggeredGSM = false;

	ServerSocket serverSocket;
	Socket clientSocket;

	OutputStream logOutputStream;
	OutputStream triggerOutputStream;

	final String LOG_MESSAGE = "logState";
	final String POS_MESSAGE = "PoseRequest";

	final int MESSAGE_BUFFER_SIZE = 256;

	final boolean localLogging = true;

	PedestrianLocalization_StandardDeviation stdDevLocalization;
	PedestrianLocalization_StepDetection stepLocalization;
	PedestrianLocalization_Gyro margLocalization;

	TextView connectionStatusView;
	TextView gsmStatusView;
	TextView wifiStatusView;
	TextView gpsStatusView;
	TextView magStatusView;
	TextView accelStatusView;
	TextView gyroStatusView;
	TextView gpsPositionView;
	TextView stepPositionView;
	TextView stepStatusView;
	TextView stdDevPositionView;
	TextView stdDevStatusView;
	TextView margPositionView;
	TextView margStatusView;
	TextView orientationAccuracyView;
	TextView proximityStatusView;
	TextView lightStatusView;
	TextView triggerStatusView;

	final int CONNECTION_REQUEST_PORT = 10003;

	int BUFFER_SIZE = 1024;
	byte[] buf = new byte[BUFFER_SIZE];
	boolean serverSocketInitialized = false;
	boolean clientSocketConnected = false;
	Thread connectionListener;

	Location gpsStartLocation;	// used for converting GPS coordinate to local
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
	int numTriggeredDataPoints = 0;

	boolean TCPConnectionSetup = true;	// true added by Gary

	Button tag_state;
	
	// BEGIN - Test section to test a new thread

//	private String formatNewLogFileName(Calendar c, String origin)
//	{
//		Log.d(TAG, "CALLING formatNewLogFileName from " + origin);
//		int year   = c.get(Calendar.YEAR);
//	    int month  = c.get(Calendar.MONTH);
//	    int day    = c.get(Calendar.DAY_OF_MONTH);
//	    int hour   = c.get(Calendar.HOUR_OF_DAY);
//	    int minute = c.get(Calendar.MINUTE);
//	    int second = c.get(Calendar.SECOND);
//
//	    //return String.format("Log_%04d_%02d_%02d-%02d_%02d_%02d", year, month+1, day, hour, minute, second);
//
//	    return String.format("Log_%s_%s_%s_%04d_%02d_%02d-%02d_%02d_%02d", dataLogFileAttributes.getPhoneLocation(), dataLogFileAttributes.getPhoneId(), 
//	    		dataLogFileAttributes.getPhoneOrientation(), year, month+1, day, hour, minute, second);
//	}

	private void setNewStopLoggingDate(Calendar currentDateTime, String calledFromMethod)
	{		
		systemLog.appendToLogFile(Calendar.getInstance(Locale.US), DataLoggerSystemLog.Severity.NORMAL, 
								  "setNewStopLoggingDate() called from '" + calledFromMethod + "'.");

		// Set the new log file creation hour and minute
		stopLoggingDate.set(Calendar.HOUR_OF_DAY, dataLogFileAttributes.getStoploggingHour());	// Set the hour to begin the new log file
		stopLoggingDate.set(Calendar.MINUTE, dataLogFileAttributes.getStoploggingMinute());		// Set the minute to begin the new log file
		stopLoggingDate.set(Calendar.SECOND, newLogSecond);										// Set the second to begin the new log file

		systemLog.appendToLogFile(Calendar.getInstance(Locale.US), DataLoggerSystemLog.Severity.NORMAL, 
								  "setNewStopLoggingDate() A new stop logging date was computed (" + 
				  				  DataLoggerSystemLog.formatTimeStamp(stopLoggingDate) + ")");

		/*
		 * TODO: May need to add some logic to handle the case
		 * 
		 * If current hour and minute of today's date is still before next log hour start date, do not increment the day.
		 * Otherwise, add 1 to the day of the month to get tomorrows date.
		 */

		if (currentDateTime.after(stopLoggingDate))
		{
			stopLoggingDate.add(Calendar.DAY_OF_MONTH, 1);			

			systemLog.appendToLogFile(Calendar.getInstance(Locale.US), DataLoggerSystemLog.Severity.NORMAL,
									  "setNewStopLoggingDate() Stop Logging Date is within 24 hours, next Log Date will be: " + 
									  DataLogFileAttributes.formatNextLogFileGenerationDateForView(stopLoggingDate));

			//stopLoggingDate.add(Calendar.MINUTE, 2);
			//Log.d(TAG, "Stop Logging Date is within 24 hours, next Log Date will be: " + formatNextLogFileGenerationDateForView(stopLoggingDate));
		}
		else
		{
			systemLog.appendToLogFile(Calendar.getInstance(Locale.US), DataLoggerSystemLog.Severity.NORMAL,
					  				  "setNewStopLoggingDate() Stop Logging Date is more than 24 hours away, next Log Date will be: " +
					  				  DataLogFileAttributes.formatNextLogFileGenerationDateForView(stopLoggingDate));

			//Log.d(TAG, "Stop Logging Date is more than 24 hours away, next Log Date will be: " + formatNextLogFileGenerationDateForView(stopLoggingDate));
		}
	}

	private Runnable doBackgroundThreadProcessing = new Runnable()
	{
		public void run()
		{
			try
			{
                // main loop. the thread just checks at each 100ms
                // passed if it should still be running and then waits.
                // timeCounter decrements by 1 each loop
                while(activeThread)
                {
                    Thread.sleep(5000);
                    Log.d(TAG, "Thread Kicked Off!");

        			// Get the current date-time
        			currentDateTime = Calendar.getInstance(Locale.US);

        			// If the current date and time fall outside of the stop logging time, create a new 
        			// log file name and broadcast it to the Activity.
        			// NOTE: We only want to notify the receiver when the filename changes.
        			// NOTE: Will also need to block/synchronize the section that writes a file and changes the filename.
        			if (currentDateTime.after(stopLoggingDate))
        			{
        				// TOOD: Possibly use a synchronize or block (Atomic Boolean) here before this method is called since it could be called else where 

        				if (!stopDateIsBeingModified.get())
        				{
        					setNewStopLoggingDate(currentDateTime, "doBackgroundThreadProcessing - run()");
        				}

        				Log.d(TAG, "The threadHandler() just sent create new log message!");

        				// Notify the handler that it is time to create a new log file with a new name.
        				threadHandler.sendEmptyMessage(1);
        			}
        			else
        			{
        				Log.d(TAG, "stop Logging Time not reached yet.");
        			}
                }
            }
			catch(InterruptedException e)
			{
				e.printStackTrace();
            }
			finally
			{
                // this forces the activity to end
                // (also the application in our case)
                finish();
            }

			Log.d(TAG, "The thread has finished!!!");
		}
	};

	// Receives Thread's messages, interprets them and acts
    // on the current Activity as needed
    private Handler threadHandler = new Handler()
    {
        public void handleMessage(android.os.Message msg)
        {
        	// TODO: Possibility exists of user changing this value in log file attributes dialog at precise ms this code is executed. Highly unlikely, but still possible.
        	// TODO: Using an atomic boolean would fix this problem.
        	if (dataLogFileAttributes.isAutomaticLogFileCreationSet())
        	{
        		// SUMMARY: When this handle receives a message, it is an indication that a new log file should be created.
        		//		 	A new log filename will be created.
        		
				// STEP 1: Stop logging to the current data log file\
        		// NOTE: The method stopLogging() clears the current log filename in the object dataLogFileAttributes.
				stopLogging("threadHandler stopped logging so new log file archive can be created.");
				
				// STEP 2: Create a new filename based on the current date time
				dataLogFileAttributes.setCurrentLogFilename(DataLogFileAttributes.formatNewLogFileName(Calendar.getInstance(Locale.US)));
				
				// STEP 3: Begin logging using the new data log filename
				startLogging(dataLogFileAttributes.getCurrentLogFilename(), "handleMessage");
        	}
        	else
        	{
        		Log.d(TAG, "Auto Logging DISABLED. A new log file will not be created.");
        	}
        }
    };

	// END - Test section to test a new thread

    // BEGIN - E-mail function

    /*
     * Even though the code correctly works, there are still a few problems. This code
     * launches the e-mail client on the phone, which requires some user input. Currently
     * I do not know if there is anyway around this.
     */
    public void sendAliveEMail()
    {
    	Intent intent = new Intent(Intent.ACTION_SENDTO); // it's not ACTION_SEND

    	intent.setType("text/plain");
    	
    	String subject = dataLogFileAttributes.getPhoneId() + "_" + 
    					 dataLogFileAttributes.getPhoneLocation() + "_" +
    					 dataLogFileAttributes.getPhoneOrientation();
 
    	Calendar c = Calendar.getInstance(Locale.US);
		String body = "Phone " + dataLogFileAttributes.getPhoneId() + "checking in on " +  
					  String.format("%04d-%02d-%02d %02d:%02d:%02d", c.get(Calendar.YEAR),
																c.get(Calendar.MONTH)+1,
																c.get(Calendar.DAY_OF_MONTH),
																c.get(Calendar.HOUR_OF_DAY),
																c.get(Calendar.MINUTE),
																c.get(Calendar.SECOND));

    	intent.putExtra(Intent.EXTRA_SUBJECT, subject.toString());
    	intent.putExtra(Intent.EXTRA_TEXT, body.toString());
    	intent.setData(Uri.parse("mailto:garyfredgiger@gmail.com")); // or just "mailto:" for blank
    	intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // this will make such that when user returns to your app, your app is displayed, instead of the email app.
    	startActivity(intent);
    }

    // End - E-Mail function 

    /*
     * This method prevents the app from exiting if the back button is accidentally pushed.
     */

    @Override
    public void onBackPressed()
    {	
    	LayoutInflater layoutInflater = LayoutInflater.from(this);
        final View textEntryView = (View)layoutInflater.inflate(R.layout.exit_dialog, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	builder.setView(textEntryView);
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        builder.setTitle("Exit?");
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
    		@Override
    		public void onClick(DialogInterface dialog, int which) {
    			// If the password is correct we can exit!
    			String enteredPassword = ((EditText)textEntryView.findViewById(R.id.exitpassword)).getText().toString();

    			if (enteredPassword.compareTo(getString(R.string.exit_password)) == 0)
    			{
    				systemLog.appendToLogFile(Calendar.getInstance(Locale.US),
    										  DataLoggerSystemLog.Severity.NORMAL,
    										  "onBackPressed() User successfully entered password '" + enteredPassword + "' to exit DataLogger app.");

    				DataCollector.super.onBackPressed();    				
    			}
    			else
    			{
    				systemLog.appendToLogFile(Calendar.getInstance(Locale.US),
    										  DataLoggerSystemLog.Severity.WARNING,
    										  "onBackPressed() User failed to enter correct password to exit DataLogger app. User entered password '" + enteredPassword + "'.");
    			}
    		}
    	});
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				systemLog.appendToLogFile(Calendar.getInstance(Locale.US),
										  DataLoggerSystemLog.Severity.NORMAL,
										  "onBackPressed() User cancelled from exiting DataLogger app.");
				dialog.cancel();
			}
		});

        // Create the actual dialog
        AlertDialog alert = builder.create();
        alert.show();
    }

    /***************************************************************************
     * Check if the data log attributes file exists
     */
    public void checkIfDataLogAttributesFileExists(String calledFromMethod)
    {
    	if (!doesDataLogFileAttributesExist(calledFromMethod))
		{	
			// Since the Log Attributes file does not exist, create default values
			createDefaultLogAttributesFile();
		}
    }

    /***************************************************************************
     * Create the system log object if it does not already exist
     */
    public void createSystemLogger()
    {
    	if (systemLog == null)
		{
			systemLog = new DataLoggerSystemLog(Environment.getExternalStorageDirectory() + "/logs");
		}
    }

    /***************************************************************************
     * Acquire the wake lock 
     */

    public void acquireWakeLock(String calledFromMethod)
    {

    	systemLog.appendToLogFile(Calendar.getInstance(Locale.US),
								  DataLoggerSystemLog.Severity.NORMAL,
								  calledFromMethod + " Attempting to acquire the wake lock.");

    	if (power == null)
    	{
    		power = (PowerManager) getSystemService(Context.POWER_SERVICE);
    	}
    	
    	if (wakeLock == null)
    	{
    		wakeLock = power.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "DataCollector");
    	}
		
    	if (!wakeLock.isHeld())
		{
			wakeLock.acquire();
			systemLog.appendToLogFile(Calendar.getInstance(Locale.US),
					  				  DataLoggerSystemLog.Severity.NORMAL,
					  				  calledFromMethod + " Wake Lock newly acquired.");
		}
		else
		{
			systemLog.appendToLogFile(Calendar.getInstance(Locale.US),
									  DataLoggerSystemLog.Severity.NORMAL,
									  calledFromMethod + " Wake Lock already acquired from earlier.");
		}
    }

    /***************************************************************************
     * 
     */
    public void startLogMonitorThread(String calledFromMethod)
    {
    	try
		{
			if (thread == null)
			{
				systemLog.appendToLogFile(Calendar.getInstance(Locale.US),
										  DataLoggerSystemLog.Severity.NORMAL,
										  calledFromMethod + " Log monitor thread is NOT Active...attempting to start thread.");

				thread = new Thread(null, doBackgroundThreadProcessing, "Background");
				thread.start();
				systemLog.appendToLogFile(Calendar.getInstance(Locale.US),
										  DataLoggerSystemLog.Severity.NORMAL,
										  calledFromMethod + " Log monitor thread was successfully started.");
			}
			else
			{
				systemLog.appendToLogFile(Calendar.getInstance(Locale.US),
										  DataLoggerSystemLog.Severity.NORMAL,
										  calledFromMethod + " Log monitor thread is already Active.");
			}
		}
		catch (Throwable t)
		{
			// Unable to start the log monitor thread. If this thread cannot be created there
			// is no point in continuing the app.
			showAppShuttingDownDialog(calledFromMethod + " Log monitor thread could not be started. Exiting the app...", this);	
			t.printStackTrace();
		}
    }

    /***************************************************************************
     * 
     */
    private void checkIfLoggingWasActiveBeforePreviousAppsShutdown(Boolean previousLoggingTookPlace,
    															  String previousLogFilename,
    															  String calledFromMethod)
    {
    	if (previousLoggingTookPlace)
    	{
    		systemLog.appendToLogFile(Calendar.getInstance(Locale.US),
					  DataLoggerSystemLog.Severity.WARNING,
					  calledFromMethod + "Logging was active when the app was shutdown previously. The log file name was '" + 
					  previousLogFilename + "'. Possible causes, user exit or the app quit expectedly.");
    	}
    }

    /***************************************************************************
     * onCreate Method
     */

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main);
		initializeStatusText();

		// Create the system logger if it does not already exist
		createSystemLogger();

		systemLog.appendToLogFile(Calendar.getInstance(Locale.US), DataLoggerSystemLog.Severity.NORMAL, "onCreate() Method Entered.");

//		power = (PowerManager) getSystemService(Context.POWER_SERVICE);
//		wakeLock = power.newWakeLock(PowerManager. PARTIAL_WAKE_LOCK, "DataCollector");
		vibrate = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		sensors = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		
		// NOTE: the wake lock prevents the phone from sleeping or screen going
		//		 black as long as the phone is plugged into a power source.
		acquireWakeLock("onCreate()");

		// Create the object that will be used to store the dataLogFileAttributes
		dataLogFileAttributes = new DataLogFileAttributes();

		/*
		 * Load the log file attributes if they exist
		 */

		// NOTE: The data log attributes file stores all of the menu options set in the log file attributes dialog  
		checkIfDataLogAttributesFileExists("onCreate()");

		checkIfLoggingWasActiveBeforePreviousAppsShutdown(dataLogFileAttributes.isLogging(),
														  dataLogFileAttributes.getCurrentLogFilename(),
														  "onCreate()");

		// By default, no logging will take place.
		// TODO: Depending if these values were 
		connectionStatusView.setText((String) getResources().getText(R.string.msg_wifi_not_logging));

		// NOTE: The stopLoggingDate is the data at which to create a new log file 
		stopLoggingDate = Calendar.getInstance(Locale.US);	// Initialize the stop logging date variable

		stopDateIsBeingModified.set(true);
		setNewStopLoggingDate(Calendar.getInstance(Locale.US), "onCreate()");
		stopDateIsBeingModified.set(false);

		//Debug.startMethodTracing("logger");

		startLogMonitorThread("onCreate()");
		
		// Gravity, Linear Acceleration, and Rotation Vector 'sensors' are not
		// compatible with Android versions older than 9 (Gingerbread)
		if (Integer.parseInt(android.os.Build.VERSION.SDK) >= 9)
		{
			initializeGravity();
			initializeLinearAccel();
			initializeRotation();
		}

		// GSM and Wifi are currently not compatible with Android OS versions
		// older than 7
		if (Integer.parseInt(android.os.Build.VERSION.SDK) >= 7)
		{
			if (sendWifiResults)
			{
				initializeWifi();
				Log.d("Compat", "Wifi Enabled by User.");
			}
			else
			{
				wifiStatusView.setText("Wifi DISABLED by User.");
				Log.d("Compat", "OnCreate: Wifi DISABLED by User.");
			}

			if (sendGSMResults)
			{
				initializeGSM();
				Log.d("Compat", "GSM Enabled by User.");
			}
			else
			{
				gsmStatusView.setText("GSM DISABLED by User.");
				Log.d("Compat", "OnCreate: GSM DISABLED by User.");
			}

			if (sendGPSResults)
			{
				initializeGPS();
				Log.d("Compat", "GSM Enabled by User.");
			}
			else
			{
				gpsStatusView.setText("GPS Disabled by User.");
				Log.d("Compat", "OnCreate: GPS DISABLED by User.");
			}
		}
		else
		{
			gsmStatusView.setText("GSM Disabled b/c OS < 2.1");
			wifiStatusView.setText("Wifi Disabled b/c OS < 2.1");
			gpsStatusView.setText("GPS Disabled b/c OS < 2.1");
			Log.d("Compat", "Not initializing Wifi, GSM and GPS since device is older than Android 2.1");
		}

		if (sendMagnetometerResults)
		{
			initializeMagnetometer();
			Log.d("Compat", "Magnetometer Enabled by User.");
		}
		else
		{
			magStatusView.setText("Magnetometer DISABLED by User.");
			Log.d("Compat", "OnCreate: Magnetometer DISABLED by User.");
		}
		
		if (sendAccelerometerResults)
		{
			initializeAccelerometer();
			Log.d("Compat", "Accelerometer Enabled by User.");
		}
		else
		{
			accelStatusView.setText("Accelerometer DISABLED by User.");
			Log.d("Compat", "OnCreate: Accelerometer DISABLED by User.");
		}

		if (sendGyroResults)
		{
			initializeGyro();
			Log.d("Compat", "OnCreate: Gyro Enabled by User.");
		}
		else
		{
			gyroStatusView.setText("Gyro DISABLED by User.");
			Log.d("Compat", "OnCreate: Gyro DISABLED by User.");
		}

		initializeOrientation();

		if (sendProximityResults)
		{
			initializeProximity();
			Log.d("Compat", "OnCreate: Proximity Enabled by User.");
		}
		else
		{
			proximityStatusView.setText("Proximity DISABLED by User.");
			Log.d("Compat", "OnCreate: Proximity DISABLED by User.");
		}

		if (sendLightResults)
		{
			initializeLight();
		}
		else
		{
			lightStatusView.setText("Light Sensor Disabled by User");
			Log.d("Compat", "OnCreate: Light Sensor Disbaled.");
		}

		// Set up localization methods
		stdDevLocalization = new PedestrianLocalization_StandardDeviation(new edu.cmu.ri.rcommerce.LocationListener() {

			@Override
			public void broadcastLocationUpdate(double x, double y, double theta, long time, double velocity) {
				try {
					logPositionResults(STD_DEV_IDENTIFIER, x, y, theta, time, logOutputStream);
				} catch (IOException e) {
					reportCommError(e);
				}

			}

			@Override
			public void broadcastLocationStatusChange(int status) {
				if (status == PedestrianLocalization_StandardDeviation.STATUS_MAGNETIC_FIELD_ERROR) {
					stdDevStatusView.setTextColor(Color.RED);
					stdDevStatusView.setText("StdDev: Magnetic Field Error");
				} else {
					stdDevStatusView.setTextColor(getResources().getColor(android.R.color.secondary_text_dark));
					stdDevStatusView.setText("StdDev: Status OK");
				}
			}

			@Override
			public void broadcastRelativeLocationUpdate(double dR, double dTheta, double timeDiff) {
				// TODO Auto-generated method stub

			}
		}, false);
		
		stepLocalization = new PedestrianLocalization_StepDetection(this, new edu.cmu.ri.rcommerce.LocationListener() {

			@Override
			public void broadcastLocationUpdate(double x, double y, double theta, long time, double velocity) {
				try {
					logPositionResults(STEP_IDENTIFIER, x, y, theta, time, logOutputStream);
				} catch (IOException e) {
					reportCommError(e);
				}

			}

			@Override
			public void broadcastLocationStatusChange(int status) {
				if (status == PedestrianLocalization_StandardDeviation.STATUS_MAGNETIC_FIELD_ERROR)
					stepStatusView.setText("Step: Magnetic Field Error");
				else
					stepStatusView.setText("Step: Status OK");

			}

			@Override
			public void broadcastRelativeLocationUpdate(double dR, double dTheta, double timeDiff) {
				// TODO Auto-generated method stub

			}
		}, false);
		
		margLocalization = new PedestrianLocalization_Gyro(new edu.cmu.ri.rcommerce.LocationListener() {

			@Override
			public void broadcastLocationUpdate(double x, double y, double theta, long time, double velocity) {
				try {
					logPositionResults(MARG_IDENTIFIER, x, y, theta, time, logOutputStream);
				} catch (IOException e) {
					reportCommError(e);
				}

			}

			@Override
			public void broadcastLocationStatusChange(int status) {
				if (status == PedestrianLocalization_Gyro.STATUS_MAGNETIC_FIELD_DISTURBANCE) {
					margStatusView.setTextColor(Color.RED);
					margStatusView.setText("MARG: Magnetic Field Disturbance");
				} else {
					margStatusView.setTextColor(getResources().getColor(android.R.color.secondary_text_dark));
					margStatusView.setText("MARG: Status OK");
				}
			}

			@Override
			public void broadcastRelativeLocationUpdate(double dR, double dTheta, double timeDiff) {
				// TODO Auto-generated method stub

			}
		}, false);

		resetLocalization();

		// TODO: Write the new attributes file to the sdcard

		//String initialLogFilename = formatInitialLogFileName(Calendar.getInstance(Locale.US));
		//Log.d(TAG, "Initial log filename " + initialLogFilename);
		//startLogging(initialLogFilename, "onCreate");

		//startLogging(null, "onCreate");

		if (allowExternalTrigger) {
			try {
				setupExternalTriggerMonitor();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		tag_state = (Button) findViewById(R.id.tag_state);

		tag_state.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					logTag(logOutputStream);
				} catch (IOException e) {
					reportCommError(e);
				}
			}
		});
	}

	/*
	 * This function will be responsible for checking if the data log attributes file exists.
	 * If it does, then the Phone Location, Phone ID and Phone Orientation will be read from 
	 * the file. Otherwise False is returned.
	 */
	private boolean doesDataLogFileAttributesExist(String calledFromMethod)
	{
		// Check to see if a data log attributes file exists
		String pathOfLogFileAttributes = Environment.getExternalStorageDirectory() + "/logs/" + LOG_FILE_ATTRIBUTES_FILENAME;
		File logFileAttribs = new File(pathOfLogFileAttributes);

		Log.i(TAG, "Path of " + LOG_FILE_ATTRIBUTES_FILENAME + " is " + logFileAttribs);

		systemLog.appendToLogFile(Calendar.getInstance(Locale.US),
								  DataLoggerSystemLog.Severity.NORMAL,
								  "doesDataLogFileAttributesExist() called from method " + calledFromMethod +
								  ". Determining if log attributes file '" + LOG_FILE_ATTRIBUTES_FILENAME + "' exists.");

		if (!logFileAttribs.exists())
		{
			Log.i(TAG, "The File " + LOG_FILE_ATTRIBUTES_FILENAME + " does NOT exist!");
			
			systemLog.appendToLogFile(Calendar.getInstance(Locale.US),
									  DataLoggerSystemLog.Severity.WARNING,
									  "doesDataLogFileAttributesExist() The log attributes file '" + 
									  LOG_FILE_ATTRIBUTES_FILENAME + "' was not found.");
			
			return false;
		}

		Log.i(TAG, "The File " + LOG_FILE_ATTRIBUTES_FILENAME + " DOES exist!");

		// Read the content from the file and store the information in the respective variables
		try
		{
			//Log.d(TAG, "Trying to read the file from the following path: " + pathOfLogFileAttributes.toString());
			dataLogFileAttributes.readContentFromDatalogAttributeFile(pathOfLogFileAttributes);
			
			systemLog.appendToLogFile(Calendar.getInstance(Locale.US),
									  DataLoggerSystemLog.Severity.NORMAL,
									  "doesDataLogFileAttributesExist() Log attributes file '" + LOG_FILE_ATTRIBUTES_FILENAME + 
									  "' was found and successfully loaded. The current values are " +
									  dataLogFileAttributes.toString() + ".");
		}
		catch (FileNotFoundException e)
		{
			systemLog.appendToLogFile(Calendar.getInstance(Locale.US),
									  DataLoggerSystemLog.Severity.WARNING,
									  "doesDataLogFileAttributesExist() The log attributes file '" + 
									  LOG_FILE_ATTRIBUTES_FILENAME + "' was not found.");

			e.printStackTrace();
			return false;
		}
		catch (IOException e)
		{
			systemLog.appendToLogFile(Calendar.getInstance(Locale.US),
									  DataLoggerSystemLog.Severity.ERROR,
									  "doesDataLogFileAttributesExist() An I/O error occured while attempting to read the log attributes file'" +
									  LOG_FILE_ATTRIBUTES_FILENAME + "'.");

			e.printStackTrace();
			return false;
		}
		catch (NumberFormatException e)
		{
			systemLog.appendToLogFile(Calendar.getInstance(Locale.US),
									  DataLoggerSystemLog.Severity.ERROR,
									  "doesDataLogFileAttributesExist() Problem encountered while reading spinner index for log file attribute from file '" + 
									  LOG_FILE_ATTRIBUTES_FILENAME + "'. This file may have become corrupted.");
			
			e.printStackTrace();
			return false;
		}
		catch (Throwable t)
		{
			systemLog.appendToLogFile(Calendar.getInstance(Locale.US),
									  DataLoggerSystemLog.Severity.ERROR,
									  "doesDataLogFileAttributesExist() An I/O error occured while attempting to read the log attributes file'" +
									  LOG_FILE_ATTRIBUTES_FILENAME + "'.");
			return false;
		}

		return true;
	}

	private void createDefaultLogAttributesFile()
	{
		try
		{
			dataLogFileAttributes.setPhoneLocation("(none)", 0);
			dataLogFileAttributes.setPhoneId("(none)", 0);
			dataLogFileAttributes.setPhoneOrientation("(none)", 0);
			dataLogFileAttributes.setAutomaticLogFileCreation(true);
			dataLogFileAttributes.setIsLogging(false);
			dataLogFileAttributes.setStoploggingHour(newLogHour);
			dataLogFileAttributes.setStoploggingMinute(newLogMinute);

			systemLog.appendToLogFile(Calendar.getInstance(Locale.US),
									  DataLoggerSystemLog.Severity.NORMAL,
									  "createDefaultLogAttributesFile() Reverting to default log file attributes. " + 
									  dataLogFileAttributes.toString() + ".");

			dataLogFileAttributes.writeContentToDataLogAttributeFile();

			systemLog.appendToLogFile(Calendar.getInstance(Locale.US),
									  DataLoggerSystemLog.Severity.NORMAL,
									  "createDefaultLogAttributesFile() Default log attributes file '" +
									  LOG_FILE_ATTRIBUTES_FILENAME + "' successfully created.");
		}
		catch (IOException e)
		{
			systemLog.appendToLogFile(Calendar.getInstance(Locale.US),
									  DataLoggerSystemLog.Severity.ERROR,
									  "createDefaultLogAttributesFile() An I/O error occured while attempting to write the default log file attribute values to the log attributes file'" +
									  LOG_FILE_ATTRIBUTES_FILENAME + "'. Log file attributes were not saved.");

			e.printStackTrace();
		}
	}

	@ Override
	public void onResume()
	{
		// Create the system logger if it does not already exist
		createSystemLogger();

		systemLog.appendToLogFile(Calendar.getInstance(Locale.US),
								  DataLoggerSystemLog.Severity.NORMAL,
								  "onResume() Method Entered.");

		// NOTE: the wake lock prevents the phone from sleeping or screen going
		//		 black as long as the phone is plugged into a power source.
		acquireWakeLock("onResume()");

		if (dataLogFileAttributes == null)
		{
			dataLogFileAttributes = new DataLogFileAttributes();
		}

		// NOTE: The data log attributes file stores all of the menu options set in the log file attributes dialog  
		checkIfDataLogAttributesFileExists("onResume()");

		// NOTE: The stopLoggingDate is the data at which to create a new log file 
		stopLoggingDate = Calendar.getInstance(Locale.US);	// Initialize the stop logging date variable

		// Set the start logging date since the attributes where just read in again.
		stopDateIsBeingModified.set(true);
		setNewStopLoggingDate(Calendar.getInstance(Locale.US), "onResume()");
		stopDateIsBeingModified.set(false);

//		try
//		{
//			if (thread == null)
//			{
//				systemLog.appendToLogFile(Calendar.getInstance(Locale.US),
//						  DataLoggerSystemLog.Severity.NORMAL,
//						  "onResume() Log monitor thread is NOT Active...attempting to start thread.");
//
//				thread = new Thread(null, doBackgroundThreadProcessing, "Background");
//				thread.start();
//				systemLog.appendToLogFile(Calendar.getInstance(Locale.US),
//										  DataLoggerSystemLog.Severity.NORMAL,
//										  "onResume() Log monitor thread was successfully started.");
//			}
//			else
//			{
//				systemLog.appendToLogFile(Calendar.getInstance(Locale.US),
//										  DataLoggerSystemLog.Severity.NORMAL,
//										  "onResume() Log monitor thread is already Active.");
//			}
//		}
//		catch (Throwable t)
//		{
//			// Unable to start the log monitor thread. If this thread cannot be created there
//			// is no point in continuing the app.
//			showAppShuttingDownDialog("onResume() Log monitor thread could not be started. Exiting the app...", this);	
//			t.printStackTrace();
//		}
		startLogMonitorThread("onResume()");
		
		// TODO: Depending on the already logging value, start logging if logging was already in place from previous run

		Log.d(TAG, "onResume Called!");
		super.onResume();		
	}

	@Override
	public void onPause()
	{
		Log.d(TAG, "onPause() Called!");

		systemLog.appendToLogFile(Calendar.getInstance(Locale.US),
								  DataLoggerSystemLog.Severity.NORMAL,
								  "onPause() Method Entered.");

		try
		{
			dataLogFileAttributes.writeContentToDataLogAttributeFile();

			systemLog.appendToLogFile(Calendar.getInstance(Locale.US),
									  DataLoggerSystemLog.Severity.NORMAL,
									  "onPause() Current log file attributes successfully saved to file '" +
									  LOG_FILE_ATTRIBUTES_FILENAME + "'. The values are " +
									  dataLogFileAttributes.toString() + ".");
		}
		catch (IOException e)
		{
			systemLog.appendToLogFile(Calendar.getInstance(Locale.US),
									  DataLoggerSystemLog.Severity.ERROR,
									  "onPause() An I/O error occured while attempting to write to the log attributes file'" +
									  LOG_FILE_ATTRIBUTES_FILENAME + "'. Log file attributes were not saved.");

			e.printStackTrace();
		}

		super.onPause();
	}

	@Override
	public void onStart()
	{
		Log.d(TAG, "onStart Called!");
		super.onStart();

		// Create the system logger if it does not already exist
		createSystemLogger();
		
		systemLog.appendToLogFile(Calendar.getInstance(Locale.US),
								  DataLoggerSystemLog.Severity.NORMAL,
								  "onStart() Method Entered.");
	}

	@Override
	public void onRestart()
    {
		Log.d(TAG, "onRestart Called!");
    	super.onRestart();

    	// Create the system logger if it does not already exist
    	createSystemLogger();
    	
    	
		systemLog.appendToLogFile(Calendar.getInstance(Locale.US),
								  DataLoggerSystemLog.Severity.NORMAL,
								  "onRestart() Method Entered.");
    }

	@Override
	public void onStop()
	{
		Log.d(TAG, "onStop Called!");
		super.onStop();
		
		systemLog.appendToLogFile(Calendar.getInstance(Locale.US),
								  DataLoggerSystemLog.Severity.NORMAL,
								  "onStop() Method Entered.");
	}
	
	@Override
	protected void onDestroy()
	{
		Log.d(TAG, "onDestroy() Called!");
		systemLog.appendToLogFile(Calendar.getInstance(Locale.US),
								  DataLoggerSystemLog.Severity.NORMAL,
								  "onDestroy() Method Entered...");
		// Debug.stopMethodTracing();

		try
		{
			if (serverSocket != null)
			{
				serverSocket.close();
				
			}
			
			if (clientSocket != null)
			{
				clientSocket.close();
			}
			
			if (logOutputStream != null)
			{
				//logOutputStream.close();
				
				if (dataLogFileAttributes.isLogging())
				{
					systemLog.appendToLogFile(Calendar.getInstance(Locale.US),
											  DataLoggerSystemLog.Severity.WARNING,
											  "onDestroy() Logging is currently Active...logging will be interrupted without properly stopping the logging process.");

					stopLogging("Called from onDestroy() method");
				}
			}
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		unregisterReceiver(wifiBroadcastReciever);

		if (thread != null)
		{
			systemLog.appendToLogFile(Calendar.getInstance(Locale.US),
									  DataLoggerSystemLog.Severity.NORMAL,
									  "onDestroy() Log monitor thread is Active, attempting to stop the thread!");
			
			try
			{
				thread.interrupt();
				thread = null;
				
				systemLog.appendToLogFile(Calendar.getInstance(Locale.US),
										  DataLoggerSystemLog.Severity.NORMAL,
										  "onDestroy() Log monitor thread was successfully stopped.");
			}
			catch (Throwable t) 
			{
				systemLog.appendToLogFile(Calendar.getInstance(Locale.US),
										  DataLoggerSystemLog.Severity.ERROR,
										  "onDestroy() An error was encountered trying to stop the log monitor thread.");
			}
		}
		else
		{
			systemLog.appendToLogFile(Calendar.getInstance(Locale.US),
									  DataLoggerSystemLog.Severity.WARNING,
									  "onDestroy() Log monitor thread was NOT Active for some unknown reason.");
		}

		systemLog.appendToLogFile(Calendar.getInstance(Locale.US),
				  DataLoggerSystemLog.Severity.NORMAL,
				  "onDestroy() Attempting to release the Wake Lock.");

		if (wakeLock.isHeld())
		{
			wakeLock.release();
			systemLog.appendToLogFile(Calendar.getInstance(Locale.US),
					  				  DataLoggerSystemLog.Severity.NORMAL,
					  				  "onDestroy() Wake Lock was released.");
		}
		else
		{
			systemLog.appendToLogFile(Calendar.getInstance(Locale.US),
	  				  DataLoggerSystemLog.Severity.NORMAL,
	  				  "onDestroy() Wake Lock was already released previously.");
		}
		
		systemLog.appendToLogFile(Calendar.getInstance(Locale.US),
								  DataLoggerSystemLog.Severity.NORMAL,
								  "onDestroy() App is finally being killed!");
		
		super.onDestroy();
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu)
	{
		// For Debugging Purposes
		Log.d(TAG, "onPrepareOptionsMenu called!");

		menu.clear();

		if (logOutputStream !=null)
		{
			menu.add(Menu.NONE, MENU_ID_STOP_LOGGING, 0, R.string.menuitem_stop_logging);
		}
		else
		{
			menu.add(Menu.NONE, MENU_ID_START_LOGGING, 0, R.string.menuitem_start_logging);
		}

		menu.add(Menu.NONE, MENU_ID_STDDEV_THRESHOLD, 0, R.string.menuitem_stddev_threshold);

		if (!calibrating)
		{
			menu.add(Menu.NONE, MENU_ID_START_CALIBRATION, 0, R.string.menuitem_start_calibration);
		}
		else
		{
			menu.add(Menu.NONE, MENU_ID_STOP_CALIBRATION, 0, R.string.menuitem_stop_calibration);
		}

		if (clientSocket != null && clientSocket.isConnected())
		{
			menu.add(Menu.NONE, MENU_ID_DISCONNECT_CLIENT, 0, R.string.menuitem_disconnect_client);
		}

		menu.add(Menu.NONE, MENU_ID_CALIBRATE_GYRO, 0, R.string.menuitem_calibrate_gyro);

		menu.add(Menu.NONE, MENU_ID_ABOUT, 0, R.string.menuitem_about);

		menu.add(Menu.NONE, MENU_ID_LOG_ATTRIBUTES, 0, R.string.menuitem_logfile_attributes);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// For Debugging Purposes
		Log.d(TAG, "onOptionsItemSelected called!");

		switch (item.getItemId())
		{
			case MENU_ID_STOP_LOGGING:

				AlertDialog.Builder builder = new AlertDialog.Builder(this);

		    	builder.setTitle("Stop Logging?");
		    	builder.setMessage("Are you sure you want to stop logging data?")
		    		   .setCancelable(false)
		    	       .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
		    	    	   
		    	           public void onClick(DialogInterface dialog, int id)
		    	           {
		    	        	   Log.d(TAG, "Stop Logging Called!!!");

		    	        	   stopLogging("User properly stopped logging data by selecting proper menu option.");
		    	        	   //alreadyLogging = false;
		    	        	   dataLogFileAttributes.setIsLogging(false);
		    	           }
		    	       })
		    	       .setNegativeButton("No", new DialogInterface.OnClickListener() {
		
		    	           public void onClick(DialogInterface dialog, int id)
		    	           {
		    	                dialog.cancel();
		    	           }
		    	       });

		    	AlertDialog alert = builder.create();
		    	alert.show();
				
				break;

			case MENU_ID_START_LOGGING:

				// NEW CODE TO START LOGGING - BEGIN

				/*
				 *  Check if the data log file attributes are valid
				 */

				systemLog.appendToLogFile(Calendar.getInstance(Locale.US), 
						  DataLoggerSystemLog.Severity.NORMAL,
						  "onOptionsItemSelected() The user selected the option to begin logging data.");

				// If they are valid, the attributes can be used in generating a new log file name, begin logging!
				if (dataLogFileAttributes.allAttributesValid())
				{
					//Log.d(TAG, "Attributes are valid. Starting to log with a new log file name");

					try
					{
						// TODO: Create a new log filename using the attributes
						dataLogFileAttributes.writeContentToDataLogAttributeFile();

						systemLog.appendToLogFile(Calendar.getInstance(Locale.US), 
								  DataLoggerSystemLog.Severity.NORMAL,
								  "onOptionsItemSelected() The data log file attributes are valid and logging will begin " + dataLogFileAttributes.toString());

						systemLog.appendToLogFile(Calendar.getInstance(Locale.US), 
												  DataLoggerSystemLog.Severity.NORMAL,
												  "The user selected the option to begin logging with the data log file attributes " + dataLogFileAttributes.toString());
						
						// Set the new log filename
						dataLogFileAttributes.setCurrentLogFilename(DataLogFileAttributes.formatNewLogFileName(Calendar.getInstance(Locale.US)));
						
						startLogging(DataLogFileAttributes.formatNewLogFileName(Calendar.getInstance(Locale.US)), "onOptionsItemSelected()");
					}
					catch (IOException e)
					{
						systemLog.appendToLogFile(Calendar.getInstance(Locale.US),
												  DataLoggerSystemLog.Severity.ERROR,
												  "An I/O error occured while attempting to write to the log attributes file'" +
												  LOG_FILE_ATTRIBUTES_FILENAME + "'. Log file attributes were not saved.");

						e.printStackTrace();
					}

					// Test e-mail program
					// NOTE: Works, but network connectivity is required and e-mail account created on Phone.
					//		 Need to test further.
					//sendAliveEMail();

					// Call the function to begin logging 
					//String newLogFilename = DataLogFileAttributes.formatNewLogFileName(Calendar.getInstance(Locale.US));
					//startLogging(DataLogFileAttributes.formatNewLogFileName(Calendar.getInstance(Locale.US)), "onCreate");
					//alreadyLogging = true;
					//dataLogFileAttributes.setIsLogging(true);
				}
				else
				{
					Log.d(TAG, "Attributes are NOT valid. Displaying the error dialog message box to user.");
					
					systemLog.appendToLogFile(Calendar.getInstance(Locale.US),
							  DataLoggerSystemLog.Severity.WARNING,
							  "Data log file attributes were changed but not all are valid. The new values are " + dataLogFileAttributes.toString());
					
					showLogFileAttributesErrorDialog();
				}

				// NEW CODE TO START LOGGING - END

				// OLD CODE TO START LOGGING - BEGIN
//				resetLocalization();
//				AlertDialog.Builder alert = new AlertDialog.Builder(this);
//				
//				alert.setTitle("New Log");
//				alert.setMessage("Please enter the log name.");
//	
//				final EditText input = new EditText(this);
//				alert.setView(input);
//	
//				alert.setPositiveButton("Ok", new DialogInterface.OnClickListener()
//				{
//					public void onClick(DialogInterface dialog, int whichButton)
//					{
//						String logName = input.getText().toString();
//						startLogging(logName, "onOptionsItemSelected");
//					}
//				});
//	
//				alert.show();
				// OLD CODE TO START LOGGING - END 
	
				break;

			case MENU_ID_STDDEV_THRESHOLD:
				AlertDialog.Builder stddevDialog = new AlertDialog.Builder(this);
	
				stddevDialog.setTitle("Set Threshold");
				stddevDialog.setMessage("Please enter the new threshold.");
	
				final EditText input2 = new EditText(this);
				stddevDialog.setView(input2);
	
				stddevDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						float threshold;
						try {
							threshold = Float.parseFloat(input2.getText().toString());
						} catch (NumberFormatException e) {
							Toast.makeText(context, "Unable to parse threshold, using default", 1000).show();
							threshold = 50.0f;
						}
						stdDevLocalization.STD_DEV_THRESHOLD = threshold;
					}
				});
	
				stddevDialog.show();
				break;
				
			// starting calibration
			case MENU_ID_START_CALIBRATION:
				calibrationStatistics = new PositionStatisticsCalculator();
				calibrating = true;
				break;
				
			// stopping calibration
			case MENU_ID_STOP_CALIBRATION:
				final float totalDistance = calibrationStatistics.getTotalDistance();
				calibrating = false;
	
				AlertDialog.Builder stepCountDialog = new AlertDialog.Builder(this);
	
				stepCountDialog.setTitle("Enter step count");
				stepCountDialog.setMessage("Total distance is " + nearestWholeFormat.format(totalDistance) + "meters. " + "Please enter the number of steps you took since you pressed 'start calibration'");
	
				final EditText stepInput = new EditText(this);
				stepCountDialog.setView(stepInput);
	
				stepCountDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						try {
							int numSteps = Integer.parseInt(stepInput.getText().toString());
							float stepSize = totalDistance / numSteps;
							float walkingSpeed = calibrationStatistics.getTotalDistance() / (calibrationStatistics.getElapsedTime() / 1000);
							Toast.makeText(context, "step size: " + oneDecimalFormat.format(stepSize) + " m, walking speed: " + oneDecimalFormat.format(walkingSpeed) + "m/s", 5000).show();
						} catch (NumberFormatException e) {
							Toast.makeText(context, "couldn't parse the number of steps", 1000).show();
						}
					}
				});
	
				stepCountDialog.show();
				break;
				
			case MENU_ID_DISCONNECT_CLIENT:
				try
				{
					clientSocket.close();
					clientSocket = null;
					clientSocketConnected = false;
					connectionStatusView.setText("Waiting for client...");
					triggerOutputStream.close();
					if (!localLogging)
						logOutputStream.close();
					numTriggeredDataPoints = 0;
				}
				catch (IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;

			case MENU_ID_CALIBRATE_GYRO:
				Intent i = new Intent();
				i.setClassName(DataCollector.this, CalibrateGyro.class.getName());
				startActivityForResult(i, 0);
				break;

			case MENU_ID_ABOUT:
				showDataLoggerAboutDialogBox();
				break;

			case MENU_ID_LOG_ATTRIBUTES:
				// If not logging show the log file attributes dialog
				if (!dataLogFileAttributes.isLogging())
				{
					showLogFileAttributesDialog();
				}
				else
				{
					showLogFileAttributesUnableToEditDialog();
				}
				break;
		}

		return true;
	}

	private void showAppShuttingDownDialog(String errorMsgTxt, final Activity activity)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		systemLog.appendToLogFile(Calendar.getInstance(Locale.US),
								  DataLoggerSystemLog.Severity.ERROR,
								  errorMsgTxt);
		
		builder.setTitle("Shutting Down App!");
		builder.setMessage(errorMsgTxt)
			   .setCancelable(false)
	    	   .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

	    		   public void onClick(DialogInterface dialog, int id)
    	           {
	    			   activity.finish();
    	           }
    	       });

		AlertDialog alert = builder.create();
		alert.show();
	}

	private void showLogFileAttributesErrorDialog()
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	
		String invalidAttribsMsg 	  = (String) getResources().getText(R.string.msg_invalid_attribs);
		String logFileAttribsMenuText = (String) getResources().getText(R.string.menuitem_logfile_attributes);
		String errorMsgTxt 			  = String.format(invalidAttribsMsg, logFileAttribsMenuText);
		
		builder.setTitle("Log File Error Attributes?");
		builder.setMessage(errorMsgTxt)
			   .setCancelable(false)
	    	   .setPositiveButton(android.R.string.ok, null);

		AlertDialog alert = builder.create();
		alert.show();
	}

	// display a dialog for setting the line width
	private void showLogFileAttributesDialog()
	{
		// create the dialog and inflate its content
	    logFileAttributesDialog = new Dialog(this);
	    logFileAttributesDialog.setContentView(R.layout.logfileattributes);
	    logFileAttributesDialog.setTitle(R.string.title_logfile_attributes);
	    logFileAttributesDialog.setCancelable(false);

	    //logFileAttributesDialog.getWindow().setLayout(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);

	    // Bind the spinner controls from the dialog view to their corresponding variables 
	    spinnerPhoneLocation    		 = (Spinner)logFileAttributesDialog.findViewById(R.id.spinnerPhoneLocation);
	    spinnerPhoneId		    		 = (Spinner)logFileAttributesDialog.findViewById(R.id.spinnerPhoneId);
	    spinnerPhoneOrientation 		 = (Spinner)logFileAttributesDialog.findViewById(R.id.spinnerPhoneOrientation);
	    checkBoxAutomaticLogFileCreation = (CheckBox)logFileAttributesDialog.findViewById(R.id.checkBoxAutoLogFile);
	    timePickerStopLoggingTime   	 = (TimePicker)logFileAttributesDialog.findViewById(R.id.timePickerStopLoggingTime);

	    // Get the 
	    ArrayAdapter<CharSequence> adapterPhoneLocation    = ArrayAdapter.createFromResource(this, R.array.str_arr_phone_location, android.R.layout.simple_spinner_item);
	    ArrayAdapter<CharSequence> adapterPhoneId          = ArrayAdapter.createFromResource(this, R.array.str_arr_phone_id, android.R.layout.simple_spinner_item);
	    ArrayAdapter<CharSequence> adapterPhoneOrientation = ArrayAdapter.createFromResource(this, R.array.str_arr_phone_orientation, android.R.layout.simple_spinner_item);

	    adapterPhoneLocation.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    adapterPhoneId.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    adapterPhoneOrientation.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

	    spinnerPhoneLocation.setAdapter(adapterPhoneLocation);
	    spinnerPhoneId.setAdapter(adapterPhoneId);
	    spinnerPhoneOrientation.setAdapter(adapterPhoneOrientation);

	    Log.d(TAG, "LOADING: dataLogFileAttributes.getPhoneLocation() = " + dataLogFileAttributes.getPhoneLocation().toString());

	    if (dataLogFileAttributes.isPhoneLocationValid())
	    {
	    	spinnerPhoneLocation.setSelection(dataLogFileAttributes.getPhoneLocationItemIndex());
	    }

	    if (dataLogFileAttributes.isPhoneIdValid())
	    {
	    	spinnerPhoneId.setSelection(dataLogFileAttributes.getPhoneIdItemIndex());
	    }

	    if (dataLogFileAttributes.isPhoneLocationValid())
	    {
	    	spinnerPhoneOrientation.setSelection(dataLogFileAttributes.getPhoneOrientationItemIndex());
	    }

	    checkBoxAutomaticLogFileCreation.setChecked(dataLogFileAttributes.isAutomaticLogFileCreationSet());

	    //
	    timePickerStopLoggingTime.setIs24HourView(true);
	    timePickerStopLoggingTime.setCurrentHour(dataLogFileAttributes.getStoploggingHour());
	    timePickerStopLoggingTime.setCurrentMinute(dataLogFileAttributes.getStoploggingMinute());

	    // set the Set Line Width Button's onClickListener
	    Button setSaveAttributesButton = (Button) logFileAttributesDialog.findViewById(R.id.btnSaveAttributes);
	    Button cancelButton = (Button) logFileAttributesDialog.findViewById(R.id.btnCancelAttributes);
	    setSaveAttributesButton.setOnClickListener(setSaveDataLogAttributesButtonListener);
	    cancelButton.setOnClickListener(setCancelButtonListenerLogFileAttributes);
	    //cancelButton.setOnClickListener(null);

	    logFileAttributesDialog.show(); // show the dialog      
	}

	   // OnClickListener for the color dialog's Set Color Button
	private OnClickListener setSaveDataLogAttributesButtonListener = new OnClickListener() 
	{
		@Override
	    public void onClick(View v) 
	    {
			dataLogFileAttributes.setPhoneLocation((String)spinnerPhoneLocation.getSelectedItem(), spinnerPhoneLocation.getSelectedItemPosition());
		    dataLogFileAttributes.setPhoneId((String)spinnerPhoneId.getSelectedItem(), spinnerPhoneId.getSelectedItemPosition());
		    dataLogFileAttributes.setPhoneOrientation((String)spinnerPhoneOrientation.getSelectedItem(), spinnerPhoneOrientation.getSelectedItemPosition());

		    dataLogFileAttributes.setAutomaticLogFileCreation(checkBoxAutomaticLogFileCreation.isChecked());

		    dataLogFileAttributes.setStoploggingHour(timePickerStopLoggingTime.getCurrentHour());
		    dataLogFileAttributes.setStoploggingMinute(timePickerStopLoggingTime.getCurrentMinute());

			// NOTE: The stopLoggingDate is the data at which to create a new log file 
			stopLoggingDate = Calendar.getInstance(Locale.US);	// Initialize the stop logging date variable
		    
		    // TODO: Save the previous values before this dialog is call, compare them to the new values, if they have changed then call this method.
		    stopDateIsBeingModified.set(true);
		    setNewStopLoggingDate(Calendar.getInstance(Locale.US), "setSaveDataLogAttributesButtonListener - onClick()");
		    stopDateIsBeingModified.set(false);

		    systemLog.appendToLogFile(Calendar.getInstance(Locale.US),
		    						  DataLoggerSystemLog.Severity.NORMAL,
				   					  "setSaveDataLogAttributesButtonListener - onClick() Data log file attributes were changed. The new values are " +
				   					  dataLogFileAttributes.toString());

		    // TODO: Save the data log file contents here
		    logFileAttributesDialog.dismiss();		// hide the dialog
		    logFileAttributesDialog = null; 		// dialog no longer needed
	    }
	};

	   private OnClickListener setCancelButtonListenerLogFileAttributes = new OnClickListener() 
	   {
		   @Override
		   public void onClick(View v)
		   {
			   systemLog.appendToLogFile(Calendar.getInstance(Locale.US),
					   					 DataLoggerSystemLog.Severity.NORMAL,
					   					 "setCancelButtonListenerLogFileAttributes - onClick() User cancelled out of Log File Attributes Dialog.");
			   
			   logFileAttributesDialog.dismiss();			// hide the dialog
			   logFileAttributesDialog = null; 				// dialog no longer needed	
		   }
	   };

	/*
	 * If the app is currently logging data, this dialog will be displayed if the
	 * user attempts to access the data log file attributes dialog. Logging must be 
	 * stopped in order to change thel log file attributes.
	 */
	   private void showLogFileAttributesUnableToEditDialog()
	   {
		   AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    	
		   builder.setTitle("Unable to Change Attributes?");
		   builder.setMessage("Attributes cannot be changed until logging is stopped.")
		   		  .setCancelable(false)
	    	      .setPositiveButton(android.R.string.ok, null);
	   
		   AlertDialog alert = builder.create();
		   alert.show();
		   
		   systemLog.appendToLogFile(Calendar.getInstance(Locale.US),
				   					 DataLoggerSystemLog.Severity.NORMAL,
				   					 "showLogFileAttributesUnableToEditDialog() User attempted to change log file attributes while DataLogger app is currently logging.");
	   }

	   private void showDataLoggerAboutDialogBox()
	   {
		   PackageInfo packageInfo;
		   String	   aboutDialogTxt;
		   String	   versionName = "(Unavaialble)";
		   String	   versionCode = "(Unavaialble)";
		   String	   versionDate = "(Unavaialble)";
		   String	   versionTime = "(Unavaialble)";

		   try
		   {
			   packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);

			   // NOTE: This code block is required to get the build date time of the file 'classes.dex' since 
			   // 		this is created each time the project is built since the preferred method packageInfo.lastUpdateTime
			   //		is not compatible with Android version SDK version 7.
			   //		
			   ApplicationInfo ai = getPackageManager().getApplicationInfo(getPackageName(), 0);
			   ZipFile zf = new ZipFile(ai.sourceDir);
			   ZipEntry ze = zf.getEntry("classes.dex");
			   long time = ze.getTime();

			   // Get the last build date for the package
			   SimpleDateFormat dateFormatter = new SimpleDateFormat("MMM-dd-yyyy");
			   SimpleDateFormat timeFormatter = new SimpleDateFormat("kk:mm:ss");
			   Calendar calendar = Calendar.getInstance();
			   calendar.setTimeInMillis(time);
		       //calendar.setTimeInMillis(packageInfo.lastUpdateTime);	// Does not work with Android SDK Version 7

		       versionName = packageInfo.versionName;
		       versionCode = String.valueOf(packageInfo.versionCode);
		       versionDate = dateFormatter.format(calendar.getTime());
		       versionTime = timeFormatter.format(calendar.getTime());
		   }
		   catch (NameNotFoundException e)
		   {
			   e.printStackTrace();
		   } 
		   catch (IOException e)
		   {
			   e.printStackTrace();
		   }

		   // Build the About Dialog text
		   aboutDialogTxt = "Version Number: " + versionCode +
  			    			"\nBuild Number: " + versionName +
  			    			"\nBuild Date: "   + versionDate +
  			    			"\nBuild Time: "   + versionTime;

		   AlertDialog.Builder builder = new AlertDialog.Builder(this);

		   builder.setTitle("About DataLogger");
		   builder.setMessage(aboutDialogTxt)
		   		  .setCancelable(false)
	    	      .setPositiveButton(android.R.string.ok, null);
	   
		   AlertDialog alert = builder.create();
		   alert.show();
	   }

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// Reset Localization after GyroCalibration
		if (requestCode == 0) {
			resetLocalization();
			Toast.makeText(context, "Gyro Calibrated!", Toast.LENGTH_SHORT).show();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	void resetLocalization() {
		stdDevLocalization.reset();
		stepLocalization.reset();
		margLocalization.reset();
		gpsStartLocation = null;

		stdDevPositionView.setText("stdDev reset");
		gpsPositionView.setText("gps reset");
		stepPositionView.setText("step reset");
		margPositionView.setText("MARG reset");
	}

	void startLogging(String logName, String calledFromMethod)
	{
		//Log.d(TAG, "startLogging called from" + calledFrom + "!");
		
		// TODO: Remove this commented section since the wake lock is "grabbed" at application start.
//		if (!wakeLock.isHeld())
//		{
//			wakeLock.acquire();
//		}

		if (localLogging)
		{
			setupLocalLogging(logName, calledFromMethod);
		}

		if (!TCPConnectionSetup)
		{
			setupTCPConnection();
			TCPConnectionSetup = true;
		}
		else
		{
			resetTCPConnection();
		}
	}

	void stopLogging(String msgFromCaller)
	{
//		if (wakeLock.isHeld())
//		{
//			wakeLock.release();
//		}

		OutputStream stream = logOutputStream;
		logOutputStream = null;
		try
		{
			systemLog.appendToLogFile(Calendar.getInstance(Locale.US),
									  DataLoggerSystemLog.Severity.NORMAL,
									  "stopLogging() " + msgFromCaller);

			stream.flush();
			if (stream instanceof FileOutputStream)
				((FileOutputStream) stream).getFD().sync();
			stream.close();

			systemLog.appendToLogFile(Calendar.getInstance(Locale.US),
									  DataLoggerSystemLog.Severity.NORMAL,
									  "stopLogging() Local logging STOPPED. Log file name was \"" + dataLogFileAttributes.getCurrentLogFilename() + "\"");

			dataLogFileAttributes.clearCurrentLogFilename();

			//logOutputStream = null;	// Added by Gary
			//stream = null;			// Added by Gary
		}
		catch (IOException e)
		{
			Log.e("IO", "error trying to close stream: " + e.getMessage());
			systemLog.appendToLogFile(Calendar.getInstance(Locale.US),
									  DataLoggerSystemLog.Severity.ERROR,
									  "stopLogging() An error occured while trying to stop logging to the file '" + dataLogFileAttributes.getCurrentLogFilename() + "'.");
		}
		connectionStatusView.setText("Logging Stopped.");
	}

	void logWifiResults(final List<ScanResult> results, OutputStream out) throws IOException {
		wifiStatusView.post(new Runnable() {

			@Override
			public void run() {

//				String wifiLogFileName = "wifiLogFile.txt";
//				File root;
//
//		        root = Environment.getExternalStorageDirectory();					
//				
//				File logFile = new File(root + "/" + wifiLogFileName);
//				Log.i("WIFILogPath", "DataCollector - Path of root: " + logFile );
//				if (!logFile.exists())
//				{
//					try
//				      {
//				         logFile.createNewFile();
//				      } 
//				      catch (IOException e)
//				      {
//				         // TODO Auto-generated catch block
//				         e.printStackTrace();
//				      }
//				}

				String out = "Wifi: " + results.size() + " APs\n";

//				now.setToNow();

				for (ScanResult ap : results) {
					
					//Log.i("WIFI Signal", "" + now.format2445() + ", Add: " + ap.BSSID + ", Freq: " + ap.frequency + ", Level: " + ap.level);
					
//					String logFileRow = now.format2445() + ", " + ap.BSSID + ", " + ap.frequency + ", " + ap.level;					
			        
//		            try
//		            {		            	
//		            	BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true)); 
//					    buf.append(logFileRow);
//					    buf.newLine();
//					    buf.close();
//					}
//		            catch (IOException e)
//		            {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}

					out += ap.level + " ";
				}
				wifiStatusView.setText(out);

				// TODO: Gary Note: Is is here where the WiFi signal strength should be logged?
			}
		});
		if (out == null)
			return;
		WifiScan message = MessageBuilder.buildWifiScanMessage(results);
		
		MessageWrapper wrappedMessage = MessageWrapper.newBuilder().setType(Type.WifiScan).setWifiScan(message).build();
		wrappedMessage.writeDelimitedTo(out);

	}

	// TODO: add info for the current cell
	void logGSMResults(final int signalStrength, GsmCellLocation cellLocation, final List<NeighboringCellInfo> results, OutputStream out) throws IOException {
		gsmStatusView.post(new Runnable() {

			@Override
			public void run() {
				String out = "GSM: " + results.size() + " channels ";
				out += signalStrength + " ";
				for (NeighboringCellInfo neighbor : results) {
					out += neighbor.getRssi() + " ";
				}
				gsmStatusView.setText(out);

			}
		});
		if (out == null || cellLocation == null || results == null)
			return;
		GSMScan message = MessageBuilder.buildGSMScanMessage(signalStrength, cellLocation, results);

		MessageWrapper wrappedMessage = MessageWrapper.newBuilder().setType(Type.GSMScan).setGsmScan(message).build();

		wrappedMessage.writeDelimitedTo(out);

	}

	void logGPSResults(final Location results, OutputStream out) throws IOException {
		gpsStatusView.post(new Runnable() {

			@Override
			public void run() {
				gpsStatusView.setText("GPS: lat: " + results.getLatitude() + " long: " + results.getLongitude() + " accuracy(m): " + results.getAccuracy() + "\n heading: " + results.getBearing());

			}
		});
		if (out == null)
			return;
		GPSInfo.Builder gpsMessageBuilder = GPSInfo.newBuilder();

		gpsMessageBuilder.setAccuracy(results.getAccuracy()).setBearing(results.getBearing()).setLattitude(results.getLatitude()).setLongitude(results.getLongitude()).setSpeed(results.getSpeed()).setTimestamp(results.getTime());

		GPSInfo message = gpsMessageBuilder.build();

		MessageWrapper wrappedMessage = MessageWrapper.newBuilder().setType(Type.GPSInfo).setGpsInfo(message).build();

		wrappedMessage.writeDelimitedTo(out);

	}

	void logMagnetometerResults(SensorEvent results, OutputStream out) throws IOException {
		float magnitude = magnitude(results.values[0], results.values[1], results.values[2]);
		magStatusView.setText("magnetic strength: " + nearestWholeFormat.format(magnitude) + " uT");

		if (out == null)
			return;
		MagneticInfo.Builder magMessageBuilder = MagneticInfo.newBuilder();

		magMessageBuilder.setAccuracy(Accuracy.valueOf(results.accuracy)).setTimestamp(results.timestamp).setX(results.values[0]).setY(results.values[1]).setZ(results.values[2]);

		MagneticInfo message = magMessageBuilder.build();

		MessageWrapper wrappedMessage = MessageWrapper.newBuilder().setType(Type.MagneticInfo).setMagneticInfo(message).build();

		wrappedMessage.writeDelimitedTo(out);

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
		String x = oneDecimalFormat.format(results.values[0]);
		String y = oneDecimalFormat.format(results.values[1]);
		String z = oneDecimalFormat.format(results.values[2]);
		if (sendAccelerometerResults && out != null) {
			accelStatusView.setText("Accel: " + x + " " + y + " " + z + " ");
			AccelInfo.Builder accelMessageBuilder = AccelInfo.newBuilder();

			accelMessageBuilder.setAccuracy(Accuracy.valueOf(results.accuracy)).setTimestamp(results.timestamp).setX(results.values[0]).setY(results.values[1]).setZ(results.values[2]);

			AccelInfo message = accelMessageBuilder.build();

			MessageWrapper wrappedMessage = MessageWrapper.newBuilder().setType(Type.AccelInfo).setAccelInfo(message).build();

			wrappedMessage.writeDelimitedTo(out);
		} else
			accelStatusView.setText("Accel (NOT LOGGING): " + x + " " + y + " " + z + " ");
	}

	void logGyroResults(SensorEvent results, OutputStream out) throws IOException {
		String x = oneDecimalFormat.format(results.values[0]);
		String y = oneDecimalFormat.format(results.values[1]);
		String z = oneDecimalFormat.format(results.values[2]);
		if (sendGyroResults && out != null) {
			gyroStatusView.setText("Gyro: " + x + " " + y + " " + z + " ");
			GyroInfo.Builder gyroMessageBuilder = GyroInfo.newBuilder();

			gyroMessageBuilder.setAccuracy(Accuracy.valueOf(results.accuracy)).setTimestamp(results.timestamp).setX(results.values[0]).setY(results.values[1]).setZ(results.values[2]);

			GyroInfo message = gyroMessageBuilder.build();

			MessageWrapper wrappedMessage = MessageWrapper.newBuilder().setType(Type.GyroInfo).setGyroInfo(message).build();

			wrappedMessage.writeDelimitedTo(out);
		} else
			gyroStatusView.setText("Gyro (NOT LOGGING): " + x + " " + y + " " + z + " ");
	}

	void logGravityResults(SensorEvent results, OutputStream out) throws IOException {
		if (sendGravityResults && out != null) {
			GravityInfo.Builder gravityMessageBuilder = GravityInfo.newBuilder();

			gravityMessageBuilder.setAccuracy(Accuracy.valueOf(results.accuracy)).setTimestamp(results.timestamp).setX(results.values[0]).setY(results.values[1]).setZ(results.values[2]);

			GravityInfo message = gravityMessageBuilder.build();

			MessageWrapper wrappedMessage = MessageWrapper.newBuilder().setType(Type.GravityInfo).setGravityInfo(message).build();

			wrappedMessage.writeDelimitedTo(out);
		}
	}

	void logLinearAccelResults(SensorEvent results, OutputStream out) throws IOException {
		if (sendLinearAccelResults && out != null) {
			LinearAccelInfo.Builder linearAccelMessageBuilder = LinearAccelInfo.newBuilder();

			linearAccelMessageBuilder.setAccuracy(Accuracy.valueOf(results.accuracy)).setTimestamp(results.timestamp).setX(results.values[0]).setY(results.values[1]).setZ(results.values[2]);

			LinearAccelInfo message = linearAccelMessageBuilder.build();

			MessageWrapper wrappedMessage = MessageWrapper.newBuilder().setType(Type.LinearAccelInfo).setLinearAccelInfo(message).build();

			wrappedMessage.writeDelimitedTo(out);
		}
	}

	void logRotationResults(SensorEvent results, OutputStream out) throws IOException {
		if (sendRotationResults && out != null) {
			RotationInfo.Builder rotationMessageBuilder = RotationInfo.newBuilder();

			rotationMessageBuilder.setAccuracy(Accuracy.valueOf(results.accuracy)).setTimestamp(results.timestamp).setA(results.values[0]).setB(results.values[1]).setC(results.values[2]);

			RotationInfo message = rotationMessageBuilder.build();

			MessageWrapper wrappedMessage = MessageWrapper.newBuilder().setType(Type.RotationInfo).setRotationInfo(message).build();

			wrappedMessage.writeDelimitedTo(out);
		}
	}

	void logProximityResults(SensorEvent results, OutputStream out) throws IOException {

		if (sendProximityResults) {
			proximityStatusView.setText("Proximity: " + results.values[0] + " cm");
			ProximityInfo.Builder proxMessageBuilder = ProximityInfo.newBuilder();

			proxMessageBuilder.setTimestamp(results.timestamp).setProximity(results.values[0]);

			ProximityInfo message = proxMessageBuilder.build();

			MessageWrapper wrappedMessage = MessageWrapper.newBuilder().setType(Type.ProximityInfo).setProximityInfo(message).build();

			wrappedMessage.writeDelimitedTo(out);
		} else
			proximityStatusView.setText("Proximity (NOT LOGGING): " + results.values[0] + " cm");
	}

	void logLightResults(SensorEvent results, OutputStream out) throws IOException {
		if (sendLightResults) {
			lightStatusView.setText("Light: " + results.values[0] + " lux");
			LightInfo.Builder lightMessageBuilder = LightInfo.newBuilder();

			lightMessageBuilder.setTimestamp(results.timestamp).setLight(results.values[0]);

			LightInfo message = lightMessageBuilder.build();

			MessageWrapper wrappedMessage = MessageWrapper.newBuilder().setType(Type.LightInfo).setLightInfo(message).build();

			wrappedMessage.writeDelimitedTo(out);
		} else
			lightStatusView.setText("Light (NOT LOGGING): " + results.values[0] + " lux");
	}

	void logPositionResults(String notes, double x, double y, double theta, long time, OutputStream out) throws IOException {
		String roundedX = oneDecimalFormat.format(x);
		String roundedY = oneDecimalFormat.format(y);
		Accuracy accuracy;
		if (notes.equals(GPS_IDENTIFIER)) {
			accuracy = Accuracy.MEDIUM;
			gpsPositionView.setText("GPS Pos: " + roundedX + "," + roundedY);
			if (!sendPositionResults)
				gpsPositionView.setTextColor(Color.RED);
			else
				gpsPositionView.setTextColor(getResources().getColor(android.R.color.secondary_text_dark));
		} else if (notes.equals(STEP_IDENTIFIER)) {
			accuracy = Accuracy.UNRELIABLE;
			stepPositionView.setText("Step Pos: " + roundedX + "," + roundedY);
			if (!sendPositionResults)
				stepPositionView.setTextColor(Color.RED);
			else
				stepPositionView.setTextColor(getResources().getColor(android.R.color.secondary_text_dark));
		} else if (notes.equals(STD_DEV_IDENTIFIER)) {
			String roundedStdDev = nearestWholeFormat.format(stdDevLocalization.lastStdDev);
			String roundedAzimuth = nearestWholeFormat.format(Math.toDegrees(stdDevLocalization.oldOrientation[0]));
			String roundedPitch = nearestWholeFormat.format(Math.toDegrees(stdDevLocalization.oldOrientation[1]));
			String roundedRoll = nearestWholeFormat.format(Math.toDegrees(stdDevLocalization.oldOrientation[2]));
			accuracy = Accuracy.UNRELIABLE;
			stdDevPositionView.setText("StdDev Pos: " + roundedX + "," + roundedY + " stdDev: " + roundedStdDev + "\nAzimuth: " + roundedAzimuth + " Pitch: " + roundedPitch + " Roll: " + roundedRoll);
			if (!sendPositionResults)
				stdDevPositionView.setTextColor(Color.RED);
			else
				stdDevPositionView.setTextColor(getResources().getColor(android.R.color.secondary_text_dark));
		} else if (notes.equals(MARG_IDENTIFIER)) {
			String roundedAzimuth = nearestWholeFormat.format(Math.toDegrees(margLocalization.orientationFilter.fusedOrientationEstimate[0]));
			String roundedPitch = nearestWholeFormat.format(Math.toDegrees(margLocalization.orientationFilter.fusedOrientationEstimate[1]));
			String roundedRoll = nearestWholeFormat.format(Math.toDegrees(margLocalization.orientationFilter.fusedOrientationEstimate[2]));

			String roundedrAzimuth = nearestWholeFormat.format(Math.toDegrees(margLocalization.orientationFilter.referenceOrientationEstimate[0]));
			String roundedrPitch = nearestWholeFormat.format(Math.toDegrees(margLocalization.orientationFilter.referenceOrientationEstimate[1]));
			String roundedrRoll = nearestWholeFormat.format(Math.toDegrees(margLocalization.orientationFilter.referenceOrientationEstimate[2]));
			accuracy = Accuracy.UNRELIABLE;
			margPositionView.setText("MARG Pos: " + roundedX + "," + roundedY + "\nAzimuth: " + roundedAzimuth + " Pitch: " + roundedPitch + " Roll: " + roundedRoll + "\nrAzimuth: " + roundedrAzimuth + " rPitch: " + roundedrPitch + " rRoll: " + roundedrRoll);
			if (!sendPositionResults)
				margPositionView.setTextColor(Color.RED);
			else
				margPositionView.setTextColor(getResources().getColor(android.R.color.secondary_text_dark));
		} else
			throw new RuntimeException("Unknown positioning method");

		if (out == null)
			return;
		PositionInfo.Builder posMessageBuilder = PositionInfo.newBuilder();
		posMessageBuilder.setNotes(notes).setAccuracy(accuracy).setX((float) x).setY((float) y).setYaw((float) theta).setTimestamp(time);

		PositionInfo message = posMessageBuilder.build();

		if (calibrationStatistics != null && notes.equals(GPS_IDENTIFIER))
			calibrationStatistics.addPositionMessage(message);

		if (sendPositionResults) {
			MessageWrapper wrappedMessage = MessageWrapper.newBuilder().setType(Type.PositionInfo).setPositionInfo(message).build();

			wrappedMessage.writeDelimitedTo(out);
		}

	}

	void logGPSOffsets(Location location, OutputStream out) throws IOException {
		if (gpsStartLocation == null && location.getLatitude() != 0)
			gpsStartLocation = location;

		float[] results = new float[3];
		Location.distanceBetween(gpsStartLocation.getLatitude(), gpsStartLocation.getLongitude(), location.getLatitude(), location.getLongitude(), results);
		float distance = results[0];
		float bearing = results[1];

		double angle = Math.toRadians(-bearing + 90);

		double x = distance * Math.cos(angle);
		double y = distance * Math.sin(angle);
		long time = location.getTime();

		logPositionResults(GPS_IDENTIFIER, x, y, Math.toRadians(location.getBearing()), time, out);
	}

	void reportCommError(final Exception e) {
		connectionStatusView.post(new Runnable() {

			@Override
			public void run() {
				connectionStatusView.setText("comm error: " + e.getMessage());

			}
		});

	}

	void initializeStatusText() {
		connectionStatusView = (TextView) findViewById(R.id.connectionStatus);
		connectionStatusView.setText("Waiting for client...");

		gsmStatusView = (TextView) findViewById(R.id.gsmStatus);
		wifiStatusView = (TextView) findViewById(R.id.wifiStatus);
		gpsStatusView = (TextView) findViewById(R.id.gpsStatus);
		magStatusView = (TextView) findViewById(R.id.magStatus);
		accelStatusView = (TextView) findViewById(R.id.accelStatus);
		gyroStatusView = (TextView) findViewById(R.id.gyroStatus);
		gpsPositionView = (TextView) findViewById(R.id.gpsPosition);
		stepPositionView = (TextView) findViewById(R.id.stepPosition);
		stdDevPositionView = (TextView) findViewById(R.id.stdDevPosition);
		margPositionView = (TextView) findViewById(R.id.margPosition);
		margStatusView = (TextView) findViewById(R.id.margStatus);
		stdDevStatusView = (TextView) findViewById(R.id.stdDevStatus);
		stepStatusView = (TextView) findViewById(R.id.stepStatus);

		proximityStatusView = (TextView) findViewById(R.id.proximityStatus);
		lightStatusView = (TextView) findViewById(R.id.lightStatus);
		triggerStatusView = (TextView) findViewById(R.id.triggerStatus);

		orientationAccuracyView = (TextView) findViewById(R.id.orientationAccuracy);

		if (sendGSMResults)
			gsmStatusView.setText("Initializing gsm...");
		else
			gsmStatusView.setText("GSM logging disabled.");

		if (sendWifiResults)
			wifiStatusView.setText("Initializing wifi...");
		else
			wifiStatusView.setText("Wifi logging disabled.");

		if (sendGPSResults)
			gpsStatusView.setText("Initializing gps...");
		else
			gpsStatusView.setText("GPS logging disabled.");

		if (sendMagnetometerResults)
			magStatusView.setText("Logging magnetometer data.");
		else
			magStatusView.setText("Magnetometer logging disabled.");

		if (sendAccelerometerResults)
			accelStatusView.setText("Logging accelerometer data.");
		else
			accelStatusView.setText("Accelerometer logging disabled.");

		if (sendGyroResults)
			gyroStatusView.setText("Logging gyro data.");
		else
			gyroStatusView.setText("Gyro logging disabled.");

		if (sendProximityResults)
			proximityStatusView.setText("Initializing proximity...");
		else
			proximityStatusView.setText("Proximity logging disabled.");

		if (sendLightResults)
			lightStatusView.setText("Initializing light...");
		else
			lightStatusView.setText("Light logging disabled");

		if (sendPositionResults) {
			gpsPositionView.setText("no GPS Position");
			stepPositionView.setText("no step position");
			stepStatusView.setText("no step Status");
			stdDevPositionView.setText("no stdDev Position");
			stdDevStatusView.setText("no stdDev Status");
			margPositionView.setText("no MARG position");
			margStatusView.setText("no MARG status");
		} else {
			gpsPositionView.setText("not logging GPS Position");
			stepPositionView.setText("not logging step position");
			stepStatusView.setText("not logging step Status");
			stdDevPositionView.setText("not logging stdDev Position");
			stdDevStatusView.setText("not logging stdDev Status");
		}

		triggerStatusView.setText(numTriggeredDataPoints + " triggered data points");
		Timer triggerUpdateTimer = new Timer();
		triggerUpdateTimer.scheduleAtFixedRate(new TimerTask() {
			// TODO schedule the messages to repeat directly with the Android
			// message system instead of repeatedly posting from another thread
			@Override
			public void run() {
				triggerStatusView.post(new Runnable() {

					@Override
					public void run() {
						triggerStatusView.setText(numTriggeredDataPoints + " triggered data points");
					}
				});

			}
		}, 2 * TRIGGER_CHECK_RATE, 2 * TRIGGER_CHECK_RATE);
	}

	void resetTCPConnection() {
		try {
			if (clientSocket != null)
				clientSocket.close();
			clientSocket = null;
			clientSocketConnected = false;
		} catch (IOException e) {
			connectionStatusView.setText("error resetting connection: " + e.getMessage());
		}
	}

	void setupTCPConnection() {
		try {
			if (serverSocket != null)
				serverSocket.close();
			if (clientSocket != null) {
				clientSocket.close();
				clientSocket = null;
			}
			serverSocket = new ServerSocket(CONNECTION_REQUEST_PORT);
			serverSocket.setSoTimeout(500);
			connectionStatusView.setText("Waiting for client...");
			serverSocketInitialized = true;
		} catch (IOException e) {
			connectionStatusView.setText("got a socket exception: " + e.getMessage());
		}

		if (connectionListener == null) {
			connectionListener = new Thread(new Runnable() {

				@Override
				public void run() {
					while (true) {
						// see if we've been disconnected and handle it
						// gracefully
						if (clientSocketConnected && (clientSocket == null || !clientSocket.isConnected())) {
							numTriggeredDataPoints = 0;
							connectionStatusView.setText("Disconnected from client!");
							Log.d("DataCollector", "Disconnected from client!");

						}
						if (serverSocketInitialized && clientSocket == null) {
							try {
								clientSocket = serverSocket.accept();
								if (clientSocket.isConnected()) {
									clientSocketConnected = true;
									connectionStatusView.post(new Runnable() {

										@Override
										public void run() {
											connectionStatusView.setText("Connected to client");
										}
									});

									if (!localLogging)
										logOutputStream = clientSocket.getOutputStream();
								}
							} catch (SocketTimeoutException e) {

							} catch (final IOException e) {
								connectionStatusView.post(new Runnable() {

									@Override
									public void run() {
										connectionStatusView.setText("Error on recieving packet: " + e.getMessage());
									}
								});
							}
						} else {
							try {
								Thread.sleep(200);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}

					}

				}

			}, "connectionListener");
			connectionListener.start();
		}
	}

	// BUG: I need to store the next log file after the stop logging date is computed.
	void setupLocalLogging(String filename, String calledFromMethod)
	{
		//Log.d(TAG, "setupLocalLogging called!");
		try
		{
			systemLog.appendToLogFile(Calendar.getInstance(Locale.US),
									  DataLoggerSystemLog.Severity.NORMAL,
									  "setupLocalLogging() A request to START logging data was received by '" + calledFromMethod + "'.");

			//connectionStatusView.setText("Logging locally");
			//Log.d(TAG, "setupLocalLogging: Logging locally");
			File externalRoot = Environment.getExternalStorageDirectory();
			File logDir = new File(externalRoot, "logs/");

			if (!externalRoot.canWrite())
			{
				systemLog.appendToLogFile(Calendar.getInstance(Locale.US),
						  DataLoggerSystemLog.Severity.ERROR,
						  "setupLocalLogging() Device is unable to write to SD card.");

				throw new IOException("can't write to sd card");
			}

			logDir.mkdir();
			//SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss");
			File outFile;
			if (filename == null || filename == "")
			{
				systemLog.appendToLogFile(Calendar.getInstance(Locale.US),
						  DataLoggerSystemLog.Severity.WARNING,
						  "setupLocalLogging() No filename specified, using default name 'Default.log'.");
				
				//outFile = new File(logDir, formatter.format(new Date()) + ".log");
				outFile = new File(logDir, "Default.log");
			}
			else
			{
				outFile = new File(logDir, filename + ".log");
			}

			//connectionStatusView.setText(outFile.toString());
			connectionStatusView.setText(filename);
			//Log.d(TAG, "setupLocalLogging: log filename: " + outFile.toString());

			if (!outFile.exists())
			{
				outFile.createNewFile();
			}
			else
			{
				systemLog.appendToLogFile(Calendar.getInstance(Locale.US),
						  DataLoggerSystemLog.Severity.ERROR,
						  "setupLocalLogging() Specfied log file '" + filename + "' already exists.");

				connectionStatusView.setText("ERROR!! Log file by that name already exists!");
				dataLogFileAttributes.setIsLogging(false);
				return;
			}

			logOutputStream = new FileOutputStream(outFile);
			dataLogFileAttributes.setIsLogging(true);
			//currentLogFilename = outFile.toString();
			//dataLogFileAttributes.setCurrentLogFilename(currentLogFilename);
			systemLog.appendToLogFile(Calendar.getInstance(Locale.US),
									  DataLoggerSystemLog.Severity.NORMAL,
									  "setupLocalLogging() Local logging started. New log file name is \"" + dataLogFileAttributes.getCurrentLogFilename() + "\"");
		}
		catch (IOException e)
		{
			systemLog.appendToLogFile(Calendar.getInstance(Locale.US),
									  DataLoggerSystemLog.Severity.ERROR,
									  "setupLocalLogging() An error occured while trying to start logging to the file '" + currentLogFilename + "'.");

			connectionStatusView.setText("Error logging locally: " + e.getMessage());
		}
	}

	void setupExternalTriggerMonitor() throws IOException {
		File externalRoot = Environment.getExternalStorageDirectory();
		File logDir = new File(externalRoot, "logs/");
		if (!externalRoot.canWrite())
			throw new IOException("can't write to sd card");
		logDir.mkdir();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss");
		File triggerOutputFile = new File(logDir, "triggered " + formatter.format(new Date()) + ".log");
		triggerOutputFile.createNewFile();
		triggerOutputStream = new FileOutputStream(triggerOutputFile);
		Timer triggerMonitor = new Timer();
		triggerMonitor.scheduleAtFixedRate(new TimerTask() {

			@Override
			public void run() {
				if (clientSocket != null && clientSocket.isConnected() && allowExternalTrigger) {
					try {
						InputStream in = clientSocket.getInputStream();
						int availableBytes = in.available();
						if (availableBytes > 0) {
							byte[] b = new byte[MESSAGE_BUFFER_SIZE];
							in.read(b);
							String message = new String(b);
							if (message.contains(LOG_MESSAGE)) {
								// set a flag for wifi and gsm that says we want
								// to write the next result to the trigger
								// output as well
								waitingForTriggeredWifi = true;
								waitingForTriggeredGSM = true;
								vibrate.vibrate(200);
								numTriggeredDataPoints++;
							}
							if (message.contains(POS_MESSAGE)) {
								// send our current pos estimate back
								Log.d("DataCollector", "got pose request message");
								OutputStream out = clientSocket.getOutputStream();
								String posResponse = "LocUpdate " + stdDevLocalization.oldX + " " + stdDevLocalization.oldY + " " + stdDevLocalization.oldOrientation[0] + " 0";

								byte[] outBuf = new byte[256];
								posResponse.getBytes(0, posResponse.length() - 1, outBuf, 0);
								out.write(outBuf);
								out.flush();
							}
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

			}
		}, TRIGGER_CHECK_RATE, TRIGGER_CHECK_RATE);
	}

	void initializeGSM() {
		gsm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		gsm.listen(new PhoneStateListener() {
			@Override
			public void onSignalStrengthsChanged(SignalStrength signalStrength) {
				currentGSMSignal = signalStrength.getGsmSignalStrength();
			}
		}, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
		Timer GSMTimer = new Timer();
		GSMTimer.scheduleAtFixedRate(new TimerTask() {

			@Override
			public void run() {
				if (sendGSMResults) {
					List<NeighboringCellInfo> GSMResults = gsm.getNeighboringCellInfo();

					try {
						logGSMResults(currentGSMSignal, (GsmCellLocation) gsm.getCellLocation(), GSMResults, logOutputStream);
					} catch (IOException e) {
						reportCommError(e);
					}
				}
				if (waitingForTriggeredGSM) {
					List<NeighboringCellInfo> GSMResults = gsm.getNeighboringCellInfo();

					try {
						logGSMResults(currentGSMSignal, (GsmCellLocation) gsm.getCellLocation(), GSMResults, triggerOutputStream);
					} catch (IOException e) {
						reportCommError(e);
					}
					waitingForTriggeredGSM = false;
				}

			}
		}, GSM_SCAN_RATE, GSM_SCAN_RATE);
	}

	void initializeWifi() {
		
		Log.i("WIFI", "DataCollector::initializeWifi() Called");
		
		/*
		 * GARY NOTE: 09-04-2012 This is only called once since the above log entry only shows up once 
		 * 			  in the LogCat viewer. It is here where the log file will be created to store the 
		 * 			  WIFI signal strength data. The data should contain the following information:
		 * 
		 * 				Time Stamp, Address of access point, frequency, level of signal
		 */
		
		// Setup the log file to record WIFI signal strength

		wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		wifiBroadcastReciever = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				if (sendWifiResults) {
					List<ScanResult> wifiResults = wifi.getScanResults();

					try {
						logWifiResults(wifiResults, logOutputStream);
						
					} catch (IOException e) {
						reportCommError(e);
					}
				}

				if (waitingForTriggeredWifi) {
					List<ScanResult> wifiResults = wifi.getScanResults();

					try {
						logWifiResults(wifiResults, triggerOutputStream);
					} catch (IOException e) {
						reportCommError(e);
					}
					waitingForTriggeredWifi = false;
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

	void initializeGPS() {
		gps = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		Criteria c = new Criteria();
		c.setAccuracy(Criteria.ACCURACY_FINE);
		c.setPowerRequirement(Criteria.NO_REQUIREMENT);

		String provider = gps.getBestProvider(c, false);
		gps.requestLocationUpdates(provider, GPS_UPDATE_RATE, 0, new LocationListener() {

			@Override
			public void onStatusChanged(String provider, int status, Bundle extras) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onProviderEnabled(String provider) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onProviderDisabled(String provider) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onLocationChanged(Location location) {
				if (sendGPSResults) {
					try {
						logGPSResults(location, logOutputStream);
					} catch (IOException e) {
						reportCommError(e);
					}

					try {
						logGPSOffsets(location, logOutputStream);
					} catch (IOException e) {
						reportCommError(e);
					}
				}
			}
		});
	}

	void initializeAccelerometer() {
		Sensor accelerometer = sensors.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		sensors.registerListener(new SensorEventListener() {

			@Override
			public void onSensorChanged(SensorEvent event) {
				if (sendAccelerometerResults) {
					try {
						logAccelerometerResults(event, logOutputStream);
					} catch (Exception e) {
						reportCommError(e);
					}
				}
				SensorReading reading = new SensorReading(event.timestamp, event.values, SensorReading.ACCELEROMETER_SENSOR);

				stdDevLocalization.addAccelerometerReadings(new SensorReading[] { reading });
				stepLocalization.addAccelerometerReadings(new SensorReading[] { reading });
				margLocalization.addAccelerometerReadings(new SensorReading[] { reading });

			}

			@Override
			public void onAccuracyChanged(Sensor sensor, int accuracy) {
				// TODO Auto-generated method stub

			}
		}, accelerometer, ACCELEROMETER_UPDATE_RATE);
	}

	void initializeMagnetometer() {
		Sensor magnetic = sensors.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
		if (magnetic == null) {
			AlertDialog.Builder b = new AlertDialog.Builder(this);
			b.setMessage("Unable to find magnetic sensor! Some functionality will not work").show();
		}
		sensors.registerListener(new SensorEventListener() {

			@Override
			public void onSensorChanged(SensorEvent event) {
				if (sendMagnetometerResults) {
					try {
						logMagnetometerResults(event, logOutputStream);
					} catch (IOException e) {
						reportCommError(e);
					}
				}
				SensorReading reading = new SensorReading(event.timestamp, event.values, SensorReading.MAGNETOMETER_SENSOR);
				stdDevLocalization.addMagnetometerReadings(new SensorReading[] { reading });
				stepLocalization.addMagnetometerReadings(new SensorReading[] { reading });
				margLocalization.addMagnetometerReadings(new SensorReading[] { reading });

			}

			@Override
			public void onAccuracyChanged(Sensor sensor, int accuracy) {
				if (accuracy < Accuracy.HIGH.getNumber()) {
					AlertDialog.Builder b = new AlertDialog.Builder(context);
					b.setMessage("Please calibrate magnetometer").show();
				}
			}
		}, magnetic, MAGNETOMETER_UPDATE_RATE);
	}

	void initializeGyro() {
		Sensor gyro = sensors.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
		if (gyro == null) {
			AlertDialog.Builder b = new AlertDialog.Builder(this);
			b.setMessage("Unable to find gyro sensor! Some functionality will not work").show();
		}
		sensors.registerListener(new SensorEventListener() {

			@Override
			public void onSensorChanged(SensorEvent event) {
				if (sendGyroResults) {
					try {
						logGyroResults(event, logOutputStream);
					} catch (IOException e) {
						reportCommError(e);
					}
				}
				SensorReading reading = new SensorReading(event.timestamp, event.values, SensorReading.GYRO_SENSOR);
				margLocalization.addGyroReadings(new SensorReading[] { reading });
			}

			@Override
			public void onAccuracyChanged(Sensor sensor, int accuracy) {
			}
		}, gyro, GYRO_UPDATE_RATE);
	}

	void initializeGravity() {
		Sensor gravity = sensors.getDefaultSensor(Sensor.TYPE_GRAVITY);
		if (gravity == null) {
			AlertDialog.Builder b = new AlertDialog.Builder(this);
			b.setMessage("Unable to find gravity sensor! Some functionality will not work").show();
		}
		sensors.registerListener(new SensorEventListener() {

			@Override
			public void onSensorChanged(SensorEvent event) {
				if (sendGravityResults) {
					try {
						logGravityResults(event, logOutputStream);
					} catch (IOException e) {
						reportCommError(e);
					}
				}
				SensorReading reading = new SensorReading(event.timestamp, event.values, SensorReading.GRAVITY_SENSOR);
				// stdDevLocalization.addGyroReadings(new
				// SensorReading[]{reading});
				// stepLocalization.addGyroReadings(new
				// SensorReading[]{reading});

			}

			@Override
			public void onAccuracyChanged(Sensor sensor, int accuracy) {
			}
		}, gravity, GRAVITY_UPDATE_RATE);
	}

	void initializeLinearAccel() {
		Sensor linearAccel = sensors.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
		if (linearAccel == null) {
			AlertDialog.Builder b = new AlertDialog.Builder(this);
			b.setMessage("Unable to find linear accel sensor! Some functionality will not work").show();
		}
		sensors.registerListener(new SensorEventListener() {

			@Override
			public void onSensorChanged(SensorEvent event) {
				if (sendLinearAccelResults) {
					try {
						logLinearAccelResults(event, logOutputStream);
					} catch (IOException e) {
						reportCommError(e);
					}
				}
				SensorReading reading = new SensorReading(event.timestamp, event.values, SensorReading.LINEAR_ACCEL_SENSOR);
			}

			@Override
			public void onAccuracyChanged(Sensor sensor, int accuracy) {
			}
		}, linearAccel, LINEAR_ACCEL_UPDATE_RATE);
	}

	void initializeRotation() {
		Sensor rotation = sensors.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
		if (rotation == null) {
			AlertDialog.Builder b = new AlertDialog.Builder(this);
			b.setMessage("Unable to find rotation sensor! Some functionality will not work").show();
		}
		sensors.registerListener(new SensorEventListener() {

			@Override
			public void onSensorChanged(SensorEvent event) {
				if (sendRotationResults) {
					try {
						logRotationResults(event, logOutputStream);
					} catch (IOException e) {
						reportCommError(e);
					}
				}
				SensorReading reading = new SensorReading(event.timestamp, event.values, SensorReading.ROTATION_SENSOR);
				// margLocalization.addRotationReadings(new
				// SensorReading[]{reading});
			}

			@Override
			public void onAccuracyChanged(Sensor sensor, int accuracy) {
			}
		}, rotation, ROTATION_UPDATE_RATE);
	}

	void initializeOrientation() {
		Sensor orientation = sensors.getDefaultSensor(SensorManager.SENSOR_ORIENTATION);
		sensors.registerListener(new SensorEventListener() {

			@Override
			public void onSensorChanged(SensorEvent event) {

			}

			@Override
			public void onAccuracyChanged(Sensor sensor, int accuracy) {
				orientationAccuracyView.setText("Orientation Accuracy: " + Accuracy.valueOf(accuracy).toString());

			}
		}, orientation, SensorManager.SENSOR_DELAY_GAME);

	}

	private void initializeLight() {
		Sensor light = sensors.getDefaultSensor(Sensor.TYPE_LIGHT);
		if (light == null) {
			lightStatusView.setText("No light sensor detected");
		}
		sensors.registerListener(new SensorEventListener() {

			@Override
			public void onSensorChanged(SensorEvent event) {
				if (sendLightResults) {
					try {
						logLightResults(event, logOutputStream);
					} catch (IOException e) {
						reportCommError(e);
					}
				}
			}

			@Override
			public void onAccuracyChanged(Sensor sensor, int accuracy) {
			}
		}, light, LIGHT_UPDATE_RATE);

	}

	private void initializeProximity() {
		Sensor proximity = sensors.getDefaultSensor(Sensor.TYPE_PROXIMITY);
		if (proximity == null) {
			proximityStatusView.setText("No proximity sensor detected");
			return;
		}
		sensors.registerListener(new SensorEventListener() {

			@Override
			public void onSensorChanged(SensorEvent event) {
				if (sendProximityResults) {
					try {
						logProximityResults(event, logOutputStream);
					} catch (IOException e) {
						reportCommError(e);
					}
				}
			}

			@Override
			public void onAccuracyChanged(Sensor sensor, int accuracy) {
			}
		}, proximity, PROXIMITY_UPDATE_RATE);
	}

	private float magnitude(float x, float y, float z) {
		return (float) Math.sqrt(x * x + y * y + z * z);
	}

}