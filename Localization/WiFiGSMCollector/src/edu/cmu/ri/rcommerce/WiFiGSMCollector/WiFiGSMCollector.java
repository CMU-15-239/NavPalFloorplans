package edu.cmu.ri.rcommerce.WiFiGSMCollector;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.SensorManager;
import android.location.Location;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.Vibrator;
import android.telephony.NeighboringCellInfo;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import edu.cmu.ri.rcommerce.MessageBuilder;
import edu.cmu.ri.rcommerce.Messages.GSMScan;
import edu.cmu.ri.rcommerce.Messages.MessageWrapper;
import edu.cmu.ri.rcommerce.Messages.MessageWrapper.Type;
import edu.cmu.ri.rcommerce.Messages.WifiScan;
import edu.cmu.ri.rcommerce.filter.PositionStatisticsCalculator;

/**
 * Activity for generating logs of phone data.
 * 
 * Currently tracks GPS, GSM, Wifi, accelerometer, magnetometer, gyroscope, light, and proximity
 * as well as the derived estimates for gravity, linear acceleration, rotation, orientation, and
 * position (step-based method, standard deviation method, and gyroscope enhanced method)
 * @author Nisarg
 */
public class WiFiGSMCollector extends Activity {
	
	WifiManager wifi;
	TelephonyManager gsm;
	SensorManager sensors;
	PowerManager power;
	Vibrator vibrate;
	
	WakeLock wakeLock;
	
	final int GSM_SCAN_RATE = 1000; //in milliseconds
	final int WIFI_SCAN_RATE = 1000; //in milliseconds
	
	boolean sendWifiResults = true;
	boolean sendGSMResults = true;
	
	OutputStream logOutputStream;
	
	final String LOG_MESSAGE = "logState";
	final String POS_MESSAGE = "PoseRequest";
	
	final boolean localLogging = true;

	
	TextView connectionStatusView;
	TextView gsmStatusView;
	TextView wifiStatusView;
	
	Location gpsStartLocation; //used for converting GPS coordinate to local x,y frame
	
	BroadcastReceiver wifiBroadcastReciever;
	
	DecimalFormat nearestWholeFormat = new DecimalFormat("####");
	DecimalFormat oneDecimalFormat = new DecimalFormat("####.#");
	
	final Context context = this;
	
	boolean calibrating  =false;
	PositionStatisticsCalculator calibrationStatistics;
	
	int currentGSMSignal = -1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
       setContentView(R.layout.main);   
       initializeStatusText();
       
       //Debug.startMethodTracing("logger");

       power = (PowerManager)getSystemService(Context.POWER_SERVICE);
       wakeLock = power.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "WiFiGSMCollector");
       vibrate = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
       sensors = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
       
       //GSM and Wifi are currently not compatible with Android OS versions older than 7
       if (Integer.parseInt(android.os.Build.VERSION.SDK) >= 7)
       {
	       initializeWifi();
	       initializeGSM();
       }
       else
       {
    	   gsmStatusView.setText("GSM disabled");
    	   wifiStatusView.setText("Wifi disabled");
    	   Log.d("Compat", "Not initializing Wifi,GSM,GPS since device is older than Android 2.1");
       }    	
    	startLogging(null);    	
    }
    





	@Override
    public boolean onPrepareOptionsMenu(Menu menu) {
    	menu.clear();
    	if (logOutputStream != null)
    		menu.add(Menu.NONE,1,0,"Stop Logging");
    	else
    		menu.add(Menu.NONE,2,0,"Start Logging");
    	menu.add(Menu.NONE,3,0,"stddev threshold");
    	if (!calibrating)
    		menu.add(Menu.NONE,4,0,"Start Calibration");
    	else
    		menu.add(Menu.NONE,5,0,"Stop Calibration");
    	menu.add(Menu.NONE,7,0,"Calibrate gyro");
    	return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
		case 1:
			stopLogging();
			break;
		case 2:
			AlertDialog.Builder alert = new AlertDialog.Builder(this);  
			   
			alert.setTitle("New Log");  
			alert.setMessage("Please enter the log name.");  
			
		  
			final EditText input = new EditText(this);  
			alert.setView(input);  
			   
			alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {  
			public void onClick(DialogInterface dialog, int whichButton) {  
			 	String logName = input.getText().toString(); 
			 	startLogging(logName);
			  }  
			}); 
			
			alert.show();
			break;
		case 3:
			
			break;
		//starting calibration	
		case 4:
			calibrationStatistics = new PositionStatisticsCalculator();
			calibrating = true;
			break;
		//stopping calibration
		case 5:
			final float totalDistance = calibrationStatistics.getTotalDistance();
			calibrating = false;
			
			AlertDialog.Builder stepCountDialog = new AlertDialog.Builder(this);  
			   
			stepCountDialog.setTitle("Enter step count");  
			stepCountDialog.setMessage("Total distance is " + nearestWholeFormat.format(totalDistance) + "meters. " +
					"Please enter the number of steps you took since you pressed 'start calibration'");  
			
		  
			final EditText stepInput = new EditText(this);  
			stepCountDialog.setView(stepInput);  
			   
			stepCountDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {  
			public void onClick(DialogInterface dialog, int whichButton) {  
				try{
				int numSteps = Integer.parseInt(stepInput.getText().toString());
				float stepSize = totalDistance / numSteps;
				float walkingSpeed = calibrationStatistics.getTotalDistance() / (calibrationStatistics.getElapsedTime() / 1000);
				Toast.makeText(context, "step size: " + oneDecimalFormat.format(stepSize) + " m, walking speed: " + oneDecimalFormat.format(walkingSpeed) + "m/s", 5000).show();
				}
				catch (NumberFormatException e) {
					Toast.makeText(context, "couldn't parse the number of steps", 1000).show();
			}}}); 

			stepCountDialog.show();
			break;
		case 6:
			
			break;
    	}
    	return true;
    }    
    
    void startLogging(String logName)
    {
    	if (!wakeLock.isHeld())
    		wakeLock.acquire();
    	if (localLogging)
    		setupLocalLogging(logName);
    }
    
    void stopLogging()
    {
    	if (wakeLock.isHeld())
    		wakeLock.release();
    	
		OutputStream stream = logOutputStream;
		logOutputStream = null;
		try
		{
			stream.flush();
			if (stream instanceof FileOutputStream)
				((FileOutputStream)stream).getFD().sync();
			stream.close();
		}
		catch (IOException e) {
			Log.e("IO", "error trying to close stream: " + e.getMessage());
		}
		connectionStatusView.setText("not logging");
    }

	void logWifiResults(final List<ScanResult> results,OutputStream out) throws IOException
    {
    	wifiStatusView.post(new Runnable() {
			
			@Override
			public void run() {
				String out = "Wifi: " + results.size() + " APs\n";
				for (ScanResult ap : results)
				{
					out += ap.level + " ";
				}
				wifiStatusView.setText(out);
				
			}
		});
    	if (out == null)
    		return;
    	WifiScan message = MessageBuilder.buildWifiScanMessage(results);
    	
    	MessageWrapper wrappedMessage = MessageWrapper.newBuilder()
    		.setType(Type.WifiScan)
    		.setWifiScan(message)
    		.build();
    	wrappedMessage.writeDelimitedTo(out);
    	
    }
    
    //TODO: add info for the current cell
    void logGSMResults(final int signalStrength, GsmCellLocation cellLocation, final List<NeighboringCellInfo> results,OutputStream out) throws IOException
    {
    	gsmStatusView.post(new Runnable() {
			
			@Override
			public void run() {
				String out = "GSM: " + results.size() + " channels ";
				out += signalStrength + " ";
				for (NeighboringCellInfo neighbor: results)
				{
					out += neighbor.getRssi() + " ";
				}
				gsmStatusView.setText(out);
				
			}
		});
    	if (out == null || cellLocation == null || results == null)
    		return;
    	GSMScan message = MessageBuilder.buildGSMScanMessage(signalStrength, cellLocation, results);
    	
    	MessageWrapper wrappedMessage = MessageWrapper.newBuilder()
			.setType(Type.GSMScan)
			.setGsmScan(message)
			.build();
    	
    	wrappedMessage.writeDelimitedTo(out);
    	
    }
    
    void reportCommError(final Exception e)
    {
    	connectionStatusView.post(new Runnable() {
			
			@Override
			public void run() {
				connectionStatusView.setText("comm error: " + e.getMessage());
				
			}
		});
    	
    }
    
    void initializeStatusText()
    {
    	connectionStatusView = (TextView)findViewById(R.id.connectionStatus);
    	connectionStatusView.setText("Waiting for client...");
    	
    	 gsmStatusView = (TextView) findViewById(R.id.gsmStatus);
    	 wifiStatusView = (TextView) findViewById(R.id.wifiStatus);
    	 
    	 if (sendGSMResults)
    		 gsmStatusView.setText("Initializing gsm...");
    	 else
    		 gsmStatusView.setText("GSM logging disabled.");
    	 
    	 if (sendWifiResults)
    		 wifiStatusView.setText("Initializing wifi...");
    	 else
    		 wifiStatusView.setText("Wifi logging disabled.");    	 
    }
    
    @Override
    protected void onDestroy() {
		unregisterReceiver(wifiBroadcastReciever);
		super.onDestroy();
    }
    
    void setupLocalLogging(String filename)
    {
    	try
		{
		connectionStatusView.setText("Logging locally");
		File externalRoot = Environment.getExternalStorageDirectory();
		File logDir = new File(externalRoot, "logs/");
		if (!externalRoot.canWrite())
			throw new IOException("can't write to sd card");
		logDir.mkdir();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss");
		File outFile;
		if (filename == null || filename == "")
			outFile = new File(logDir,  formatter.format(new Date()) + ".log");
		else
			outFile = new File(logDir, filename + ".log");
		Log.d("log", "log file name: " + outFile.toString());
		if (!outFile.exists())
			outFile.createNewFile();
		else
		{
			connectionStatusView.setText("ERROR!! Log file by that name already exists!");
			return;
		}
		logOutputStream = new FileOutputStream(outFile);
		}
		catch (IOException e) {
			connectionStatusView.setText("Error logging locally: " + e.getMessage());
		}
    }
    
    void initializeGSM()
    {
    	 gsm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
    	 gsm.listen(new PhoneStateListener(){
    		 @Override
    		public void onSignalStrengthsChanged(SignalStrength signalStrength) {
    			 currentGSMSignal = signalStrength.getGsmSignalStrength();
    		}
    	 }, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
         Timer GSMTimer = new Timer();
         GSMTimer.scheduleAtFixedRate(new TimerTask() {
 			
 			@Override
 			public void run() {
 				if (sendGSMResults)
 				{
 					List<NeighboringCellInfo> GSMResults = gsm.getNeighboringCellInfo();
 					
 					try{logGSMResults(currentGSMSignal, (GsmCellLocation)gsm.getCellLocation(),GSMResults,logOutputStream);}
 					catch (IOException e) { reportCommError(e);}
 				}
 				
 			}
 		}, GSM_SCAN_RATE, GSM_SCAN_RATE);
    }
    
    void initializeWifi()
    {
    	 wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
    	 wifiBroadcastReciever = new BroadcastReceiver() {
  			
  			@Override
  			public void onReceive(Context context, Intent intent) {
  				if (sendWifiResults)
  				{
  					List<ScanResult> wifiResults = wifi.getScanResults();
  					
  					try{logWifiResults(wifiResults,logOutputStream);}
  					catch (IOException e) { reportCommError(e);}
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
         },WIFI_SCAN_RATE,WIFI_SCAN_RATE); 
    }      
}