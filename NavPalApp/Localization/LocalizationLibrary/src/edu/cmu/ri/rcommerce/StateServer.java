package edu.cmu.ri.rcommerce;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.PointF;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import com.vividsolutions.jts.geom.Envelope;
//import com.vividsolutions.jts.index.quadtree.Quadtree;
import com.vividsolutions.jts.index.quadtree.*;

import edu.cmu.ri.rcommerce.Messages.GSMScan;
import edu.cmu.ri.rcommerce.Messages.MessageWrapper;
import edu.cmu.ri.rcommerce.Messages.MessageWrapper.Type;
import edu.cmu.ri.rcommerce.Messages.PositionInfo;
import edu.cmu.ri.rcommerce.Messages.WifiScan;



/* sole point of contact outside of HumanRobotInterface (and localization service) */
public class StateServer 
{
	static StateServer instance;
	
	public Quadtree map = new Quadtree();
	public LocalizationIPC localizationService;
	private LocationManager locationManager;
	ServiceConnection localizationServiceConnection;
	public PointF currentLocation = new PointF(0, 0);
	public float currentOrientation = 0;
	long lastLocationUpdateTime = 0;
	long lastBreadcrumbUpdateTime = 0;
	private float lastBreadcrumbX, lastBreadcrumbY;
	Context context;
	public Handler mhandler = new Handler();
	
	private double startupLattitude, startupLongitude;
	private double currentLattitude, currentLongitude;
	private double lastGPSBreadcrumbX,lastGPSBreadcrumbY;
	
	public List<Annotation> currentTasks = new ArrayList<Annotation>();
	/* allows the map to draw a potential task without having it already be accepted */
	public Annotation provisionalTask;
	List<SSListener> listeners;
	
	private TaskExecThread taskExecThread;
	
	public synchronized static StateServer getInstance(Context context)
	{
		if (instance == null)
			instance = new StateServer(context,true, true);
		return instance;
	}
	
	public static void setCurrentInstance(StateServer server)
	{
		instance = server;
	}
	
	public StateServer(Context context, boolean enableLocationTracking, boolean enableTaskExec) {
		this.context = context;
		listeners = new ArrayList<SSListener>();
		
		if (enableLocationTracking)
		{
			initializeLocalizationService();
			initializeGPS();
		}
		if (enableTaskExec)
		{
		try {
			taskExecThread = new TaskExecThread(context,mhandler);
			taskExecThread.start();
		} catch (SocketException e) {
			Toast.makeText(context, "unabled to create TaskExec thread", Toast.LENGTH_SHORT).show();
		}
		}
	}
	
	private void initializeLocalizationService()
	{
		final LocalizationCallback locCallback = new LocalizationCallback.Stub(){

			@Override
			public void locationUpdate(double x, double y,double theta, long time) throws RemoteException {
				
				//TODO switch to meter based coordinates instead of decimeter
				x = 10 * x;
				y = 10 * y;
				if (time > lastLocationUpdateTime)
				{
					//check if distance moved is over update threshold
					if (Math.pow((x-lastBreadcrumbX),2) + Math.pow((y-lastBreadcrumbY),2) > 225)
					{
						Annotation a = new Annotation();
						a.locationX = (float)x;
						a.locationY = (float)y;
						a.timestamp = time;
						a.type = Annotation.BREADCRUMB_ANNOTATION;
						addAnnotation(a);
						lastBreadcrumbUpdateTime = time;
						lastBreadcrumbX = (float)x;
						lastBreadcrumbY = (float)y;
						//Log.d("LocService", "loc broadcast: " + x + " " + y);
					}
					
					currentLocation.set((float)x, (float)y);
					currentOrientation = (float)theta;
					lastLocationUpdateTime = time;
				}
			
				updateListeners();
				
			}
			
		};
		
		Intent in = new Intent();
		in.setClassName( "edu.cmu.ri.rcommerce.MapInterface", "edu.cmu.ri.rcommerce.MapInterface.LocalizationService" );
		context.startService(in);
		Log.d("StateServer", "started service");

		localizationServiceConnection = new ServiceConnection() {
			
			@Override
			public void onServiceDisconnected(ComponentName name) {
				Log.d("StateServer", "Localization Service Disconnected");
			}
			
			@Override
			public void onServiceConnected(ComponentName name, IBinder service) {
				Log.d("StateServer","Localization Service Connected");
				localizationService = LocalizationIPC.Stub.asInterface((service));
				try {
					localizationService.registerCallback(locCallback);
				} catch (RemoteException e) {
					throw new RuntimeException(e);
				}
				}
		};
		
		Intent in2 = new Intent();
		in2.setAction("edu.cmu.ri.rcommerce.MapInterface.LOCALIZE");
		/* attempt to start localization service. Also, provide a callback that continues to monitor it
		 * and writes to the log if it dies */
		if (!(context.bindService(in2, localizationServiceConnection, 0)))
		{
			Log.d("StateServer", "Error binding localization service");
		}
		
		Thread socketMonitor;
		try {
			socketMonitor = new StateServerThread(context);
			socketMonitor.start();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	private void initializeGPS()
	{
		//Connect to GPS if possible
		locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

		// List all providers:
		List<String> providers = locationManager.getAllProviders();
		for (String provider : providers) {
			Log.d("LocationManager", provider.toString());
		}

		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		String bestProvider = locationManager.getBestProvider(criteria, false);
		Log.d("LocationManager", "best provider: " + bestProvider);

		
		Location location = locationManager.getLastKnownLocation(bestProvider);
		if (location != null)
		{
		startupLattitude = location.getLatitude();
		startupLongitude = location.getLongitude();
		}
		
		if (startupLattitude != 0)
		{
			Log.d("LocationManager", "got a GPS fix");
			insertGPSStartupAnnotation(startupLattitude,startupLongitude);
		}
		
		locationManager.requestLocationUpdates(bestProvider, 2000, 1, new LocationListener() {
			
			@Override
			public void onStatusChanged(String provider, int status, Bundle extras) {
				//Log.d("LocationManager", "status change: " + provider);
				
			}
			
			@Override
			public void onProviderEnabled(String provider) {
				Log.d("LocationManager", "got GPS");
				
			}
			
			@Override
			public void onProviderDisabled(String provider) {
				Log.d("LocationManager", "lost GPS");
				
			}
			
			@Override
			public void onLocationChanged(Location location) {
				
				Annotation a = new Annotation();
				double latitude = location.getLatitude();
				double longitude = location.getLongitude();
				
				if (startupLattitude == 0 && latitude != 0)
				{
					Log.d("LocationManager", "Got a gps fix");
					startupLattitude = latitude;
					startupLongitude = longitude;
					insertGPSStartupAnnotation(startupLattitude,startupLongitude);
					return;
				}
				
				float[] results = new float[3];
				//Log.d("LocationManager", "Computing distance between GPS origin and current GPS:");
				//Log.d("LocationManager", "startupLattitude: " + startupLattitude + "   StartupLongitude: " + startupLongitude);
				//Log.d("LocationManager", "current lattitude: " + latitude + "   current longitude: " + longitude);
				
				
				Location.distanceBetween(
						startupLattitude, startupLongitude, latitude, longitude, results);
				float distance = results[0];
				float bearing = results[1];
				
				//Log.d("LocationManager", "computed distance: " + distance);
				//Log.d("LocationManager", "computed bearing: " + bearing);
				
				
				a.locationX = (float) (10 * distance * Math.cos(Math.toRadians(bearing)));
				a.locationY = (float) (10 * distance * Math.sin(Math.toRadians(bearing)));
				
				a.timestamp = location.getTime();
				a.type = Annotation.GPS_BREADCRUMB_ANNOTATION;
				addAnnotation(a);
				//Log.d("LocationManager", "Added GPS update annotation: " + a.locationX + "," + a.locationY);
				
				//Log.d("LocationManager", "Accuracy: " + location.getAccuracy());
			}
		});
		
	}
	
	private void insertGPSStartupAnnotation(double initialLattitude, double initialLongitude) {
		Annotation a = new Annotation();
		a.locationX = 0;
		a.locationY = 0;
		a.type = Annotation.GPS_BREADCRUMB_ANNOTATION;
		a.shortName = "GPS origin";
		a.longDescription = "coordinates: " + initialLattitude + " " + initialLongitude;
		Log.d("LocationManager", "added GPS startup annotation");
		addAnnotation(a);
	}

	public synchronized void addAnnotationFromBundle(Bundle b)
	{
		Annotation a = new Annotation();
		a.shortName = b.getString("shortName");
		a.locationX = b.getFloat("locX");
		a.locationY = b.getFloat("locY");
		if (b.getBoolean("isTask"))
			addCurrentTask(a);
		else
		{
			//All annotations are 20x20 squares for now
			Envelope envelope = new Envelope(a.locationX-10, a.locationX+10, a.locationY-10, a.locationY+10);
			synchronized (map) {
				map.insert(envelope, a);
			}
		}
		
		updateListeners();
	}
	
	public synchronized void addAnnotation(Annotation a)
	{
		addAnnotation(a,map);
		updateListeners();
	}
	
	public synchronized void removeAnnotation(Annotation a)
	{
		removeAnnotation(a,map);
		updateListeners();
	}
	
	
	public static void addAnnotation (Annotation a, Quadtree map)
	{
		Envelope envelope = new Envelope(a.locationX-10, a.locationX+10, a.locationY-10, a.locationY+10);
		synchronized (map) {
			map.insert(envelope, a);
		}
	}
	
	public static boolean removeAnnotation (Annotation a, Quadtree map)
	{
		Envelope envelope = new Envelope(a.locationX-10, a.locationX+10, a.locationY-10, a.locationY+10);
		boolean out;
		synchronized (map) {
			out = map.remove(envelope, a);
		}
		return out;
	}
	
	public synchronized void addStateServerListener(SSListener l)
	{
		listeners.add(l);
	}
	
	//thread safe
	public void addCurrentTask(Annotation task)
	{
		synchronized(currentTasks)
		{
			currentTasks.add(task);
		}
		newTaskUpdate(task);
	}
	
	public void updateListeners()
	{
		for (SSListener l : listeners)
			l.SSUpdate(this);
	}
	
	private void newTaskUpdate(Annotation task)
	{
		for (SSListener l : listeners)
			l.newTaskUpdate(task);
	}
	
	public synchronized void reset()
	{
		map = new Quadtree();
		if (localizationService != null)
		{
			try {
				localizationService.reset();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		lastLocationUpdateTime = 0;
		lastBreadcrumbUpdateTime = 0;
		currentTasks.clear();
		provisionalTask = null;
		
		for (int i = -10 ; i<= 10 ; i++)
		{
			for (int j = -10 ; j<=10 ; j++)
			{
				if( i == 0 && j == 0) continue;
				Annotation a = new Annotation();
				a.locationX = i*100;
				a.locationY = j*100;
				a.shortName = (i*10) + "," + (j*10);
				a.type = Annotation.GRID_POINT;
				addAnnotation(a);
			}
			
		}
		
		Annotation a = new Annotation();
		a.locationX =0; a.locationY = 0;
		a.shortName = "Home";
		addAnnotation(a);
		
		updateListeners();
	}
	
	@SuppressWarnings("unchecked")
	public List<Annotation> rangeQuery(float x1, float x2, float y1, float y2)
	{
		Envelope e = new Envelope(x1,x2,y1,y2);
		return map.query(e);
	}

	public void setProvisionalTask(Annotation annotation) {
		provisionalTask = annotation;
		updateListeners();
		
	}

	//Simply clears the task list without notifying anyone
	public void clearCurrentTasks() {
		currentTasks.clear();
		updateListeners();
		
	}
	
	public void shutdown()
	{
		try{
			context.unbindService(localizationServiceConnection);
		}
		catch (IllegalArgumentException e) {
			e.printStackTrace();
			// TODO: handle exception
		}
	}

	//returns true if the task was removed, and false if it was not in the system
	public boolean clearTaskWithID(int taskID) {
		for (Annotation t : currentTasks)
		{
			if (t.ID == taskID)
				return currentTasks.remove(t);
		}
		
		return false;
		
	}

	// Marks the current task as finished and notifies the local taskExec
	public void finishCurrentTask() {
		if (currentTasks.size() == 0)
		{
			Log.d("Task", "told to finish current task when there are no outstanding tasks");
			return;
		}
		else
		{
			Annotation t = currentTasks.remove(0);
			if (taskExecThread != null)
				taskExecThread.finishTask(t.ID);
		}
	}
	
	//Marks that a new task has been 
	
	public void saveMapToStream(ObjectOutputStream out) throws IOException
	{
		out.writeObject(map.queryAll());
	}
	
	@SuppressWarnings("unchecked")
	public static Quadtree loadMapFile(ObjectInputStream in) throws IOException,ClassNotFoundException
	{
		List<Annotation> list = (List<Annotation>)in.readObject();
		Quadtree map = new Quadtree();
		for (Annotation a : list) {
			addAnnotation(a,map);
			if (a.binaryData != null) {
				ByteArrayInputStream stream = new ByteArrayInputStream(a.binaryData);
				while (true) {
					MessageWrapper wrapped = MessageWrapper.parseDelimitedFrom(stream);
					if (wrapped == null)
						break;
					Type t = wrapped.getType();
					switch (t) {
					case GSMScan:
						GSMScan gsm = wrapped.getGsmScan();
						Log.d("load", "GSMScan count: " + gsm.getScanCount());
						break;
					case WifiScan:
						WifiScan wifi = wrapped.getWifiScan();
						Log.d("load", "WifiScan count: " + wifi.getScanCount());
						break;
					case PositionInfo:
						PositionInfo pos = wrapped.getPositionInfo();
						Log.d("load", "Pos: " + pos.getX() + "," + pos.getY());
						break;
					}
				}
			}
		}
		
		return map;
	}
	
	public void loadMapFromStream(ObjectInputStream in) throws IOException,ClassNotFoundException
	{
		map = StateServer.loadMapFile(in);
	}

	public void saveLocationToStream(ObjectOutputStream out) throws IOException
	{
		out.writeFloat(currentLocation.x);
		out.writeFloat(currentLocation.y);
		out.writeFloat(currentOrientation);	
	}
	
	public void loadLocationFromStream(ObjectInputStream in) throws IOException
	{
		currentLocation.x = in.readFloat();
		currentLocation.y  = in.readFloat();
		currentOrientation = in.readFloat();
		if (localizationService != null)
		{
			try {
				localizationService.setLocation(currentLocation.x, currentLocation.y);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}