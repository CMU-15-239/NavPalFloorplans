/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Chet
 * chetgnegy@gmail.com
 */
package edu.cmu;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StreamTokenizer;
import java.util.ArrayList;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.os.Vibrator;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SubMenu;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Toast;
import edu.cmu.recognizer.BeliefState;
import edu.cmu.recognizer.PlanRecognizer;
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
import edu.cmu.ri.rcommerce.sensor.GyroCalibrationDataProvider;
import edu.cmu.ri.rcommerce.sensor.MapConstrainedRSSIMeasurer2D;
import edu.cmu.ri.rcommerce.sensor.RSSICalibrateProvider;
import edu.cmu.ri.rcommerce.sensor.WifiSignalMapRSSICalibrateProviderKNN;
import edu.cmu.userplan.UserPlanNode;

public class MainInterface extends Activity implements OnTouchListener, MainInterfaceCallBack
{

    // This is very useful. Use it. It enables an administrator menu. Obviously this is only for development
    boolean admin = true;

    // This where profiling can be switched on and off
    public final boolean WRITE_DSTARGRID = true;
    public final boolean WRITE_DSTARGRAPH = true;
    public final boolean WRITE_IO = true;
    public String DStarGridLog = "";
    public String DStarGraphLog = "";
    public String IOLog = "";
    BufferedWriter DStarGridWriter;
    BufferedWriter DStarGraphWriter;
    BufferedWriter IOWriter;

    long LogTime = 0;
    boolean useLocalization = false;
    boolean useRouteGuidance = true;
    boolean usePathPrediction = true;
    User Neville = new User("Neville");
    // I called the Evan the Localization guy, Neville. This can be refactored if another name is preferred.

    Destination dest = new Destination();
    Node userLevelNodes[] = new Node[2];
    // Localization stuff
    final Context context = this;
    private boolean mIsBound;
    public boolean roomIsPredicted = false;
    public boolean neverUpdated = true;
    FileWriter logWriter;
    BufferedWriter log;
    int pointsShown = 0;
    int numberOfFrames = 0;
    GridPath solution = new GridPath();
    BeliefState newBeliefState;
    AsyncTask<Void, String, Void> load;
    String correlatedData;
    String obstacleMap;
    String output;
    String offlineLogName;
    File logDir;
    BufferedWriter out = null;
    OutputStream logOutputStream;
    boolean SERVICE_IS_BINDED;
    DeadReckoningGaussianUpdater updater = null;
    WifiRSSIRuntimeProvider rp = null;
    ParticleFilter<Particle2D> filter;
    static float errorBubble = 0;
    Vibrator vibrate;

    private edu.cmu.ri.rcommerce.LocalizationService mBoundService;
    private newMessage messageReceiver = new newMessage();

    // GUI stuff
    boolean badLoad;
    DisplayView dispView;
    Building NSH;
    public static Map H;
    boolean showGrid;

    boolean requestToReplan = false;
    static int CURRENTMAPLOAD = -1;
    int CURRENTMAPSHOW = -1;
    int userSize = 1;
    PlanRecognizer recognizer = new PlanRecognizer(this);

    protected void onCreate(Bundle icicle)
    {
	Log.d(Constants.TAG, "OnCreate() Method Entered");
	super.onCreate(icicle);

	File root = Environment.getExternalStorageDirectory();
	File outFile = new File(new File(root, "NavPalSaves/"), "mostRecentLog.txt");// log points from particle filter
	outFile.delete();
	initializeLogIO();

	File savDirectory = new File("/sdcard/NavPalSaves/");
	// have the object build the directory structure, if needed.
	if (!savDirectory.exists())
	{
	    savDirectory.mkdirs();
	}

	File logDirectory = new File("/sdcard/PlannerLog/");
	// have the object build the directory structure, if needed.
	if (!logDirectory.exists())
	{
	    logDirectory.mkdirs();
	}
	// initialize Dstar writer
	prepareLogs(root, logDirectory);

	// This may eventually get done in some larger hierarchy (city)
	NSH = new Building();
	NSH.initialize(this, "Newell Simon");

	// Original value, Floor 1 of NSH
	CURRENTMAPSHOW = 0;// in future-> localization will determine the floor and set it here

	// Show floor 2 of NSH
	// CURRENTMAPSHOW = 1;

	// Show floor 3 of NSH
	// CURRENTMAPSHOW = 2;

	H = NSH.floor.get(CURRENTMAPSHOW);

	try
	{// handles path predictor. belief states are initially null resulting in Exception
	    if (H != null)

		recognizer.init(H.rooms, H.allActions);
	    // doBindService();
	    // load = new runLoadDataTask().execute();
	}
	catch (Exception PiotrWhatDidYouDo/* ?? */)
	{
	    PiotrWhatDidYouDo.printStackTrace();
	}

	displayMap(CURRENTMAPSHOW, H);

	vibrate = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);// vibrator in the phone

	Log.d(Constants.TAG, "OnCreate() Method Exited");
    }

    /** writes scripts on the SD card of the android. This is used for profiling purposes */
    @SuppressWarnings("unused")
    private void prepareLogs(File root, File logDirectory)
    {
	Log.d(Constants.TAG, "On prepareLogs() Method Entered");
	long fileTime = System.currentTimeMillis();

	if (WRITE_DSTARGRID && root.canWrite())
	{
	    File dStarLog = new File(logDirectory, "DStarGridlog_" + fileTime + ".txt");
	    if (!dStarLog.exists())
	    {
		try
		{
		    dStarLog.createNewFile();
		}
		catch (IOException e3)
		{
		    e3.printStackTrace();
		}
	    }
	    try
	    {
		FileWriter dStarGridWriter = new FileWriter(dStarLog);
		DStarGridWriter = new BufferedWriter(dStarGridWriter);
	    }
	    catch (Exception e)
	    {
		e.printStackTrace();
	    }

	}
	if (WRITE_DSTARGRAPH && root.canWrite())
	{
	    File dStarGraphLog = new File(logDirectory, "DStarGraphlog_" + fileTime + ".txt");
	    if (!dStarGraphLog.exists())
	    {
		try
		{
		    dStarGraphLog.createNewFile();
		}
		catch (IOException e3)
		{
		    e3.printStackTrace();
		}
	    }
	    try
	    {
		FileWriter dStarGraphWriter = new FileWriter(dStarGraphLog);
		DStarGraphWriter = new BufferedWriter(dStarGraphWriter);
	    }
	    catch (Exception e)
	    {
		e.printStackTrace();
	    }

	}
	if (WRITE_IO && root.canWrite())
	{
	    File IOLog = new File(logDirectory, "IOlog_" + fileTime + ".txt");
	    if (!IOLog.exists())
	    {
		try
		{
		    IOLog.createNewFile();
		}
		catch (IOException e3)
		{
		    e3.printStackTrace();
		}
	    }
	    try
	    {
		FileWriter IOFileWriter = new FileWriter(IOLog);
		IOWriter = new BufferedWriter(IOFileWriter);
	    }
	    catch (Exception e)
	    {
		e.printStackTrace();
	    }

	}
    }

    /**
     * Sets the destination to the grid coordinates x and y This works for a single floor, but for multi-floor, this will require additional parameters to specift the floor and the building.
     * 
     **/
    public synchronized void placeDest(int x, int y)
    {
	Log.d(Constants.DSTAR_TAG, "placeDest(" + x + ", " + y + ") Method Entered");
	roomIsPredicted = false;
	H.GoalRoom = null;
	try
	{
	    if (WRITE_DSTARGRAPH)
		DStarGraphLog += "New Selected Destination\n";
	    if (WRITE_DSTARGRID)
		DStarGridLog += "New Selected Destination\n";
	}
	catch (Exception e)
	{
	}
	H.time = 1;
	dest.setPairedNode(H.node[x][y]);
	try
	{

	    H.reinitialize();

	}
	catch (Exception e)
	{
	}

	// resets lists and grid
	H.cleanUpOpen();

	// regenerates path

	if (DStar(Neville.getPairedNode(), dest.getPairedNode()) == -1)
	{
	    Toast.makeText(this, "No direct path to specified destination.", Toast.LENGTH_SHORT).show();
	}
	dispView.postInvalidate();

	// Log.d(Constants.TAG, "placeDest(x, y) Method Exited");
    }

    /*
     * This specifies a more general destination. Room R is passed by the path predictor and a point within the room is found. The point within the room is arbitrarily chosen. It may be better to use the center of the room in the future,
     * but the path is set to only display up to the door now.
     */
    @Override
    public synchronized void placeDest(Room R)
    {
	Log.d(Constants.DSTAR_TAG, "placeDest(" + R.getID() + ") Method Entered");
	if (!usePathPrediction)
	    return;
	try
	{
	    if (WRITE_DSTARGRAPH)
		DStarGraphLog += "New Predicted Destination\n";
	    if (WRITE_DSTARGRID)
		DStarGridLog += "New Predicted Destination\n";
	}
	catch (Exception e)
	{
	}
	roomIsPredicted = true;
	H.GoalRoom = R;
	H.time = 1;
	for (int i = 0; i < H.getSizeX(); i += 2)
	    for (int j = 0; j < H.getSizeY(); j += 2)
	    {
		if (H.node[i][j].set != 'X' && H.RoomsMap[i][j] == R.getID())
		{
		    dest.setPairedNode(H.node[i][j]);
		    break;
		}
	    }
	try
	{

	    H.reinitialize();

	}
	catch (Exception e)
	{
	    e.printStackTrace();
	}
	// resets lists and grid
	H.cleanUpOpen();
	// regenerates path
	if (DStar(Neville.getPairedNode(), dest.getPairedNode()) == -1)
	{
	    Toast.makeText(this, "No direct path to specified destination.", Toast.LENGTH_SHORT).show();
	}
	dispView.postInvalidate();

	// Log.d(Constants.TAG, "placeDest(R) Method Entered");
    }

    /**
     * The robot representation maps are read in here. They are converted from just an image to a Map object. The pixel data of the map is read in and the image is reduced to 16 colors. The colors are divided by percentage. The color with
     * the highest % is assumed to be unexplored. The next highest is free space and the next is walls. All stray colors are paired up with the closest match and assigned to the same set (O or X).The nodes in Map are either boundary or free
     * space. This may no longer be supported by the current state of the application because it does not include an svg file and therefore has no room data.
     */
    private Map readRobotRep(int file)
    {
	Bitmap mBitmap = BitmapFactory.decodeResource(getResources(), file);
	ImageConvert imgCon = new ImageConvert(mBitmap, "Robot Map");// reads file
	imgCon.initializePixArray();// loads data from image into pix array
	imgCon.lowerBitRate();// lowers the bitrate
	// imgCon.printColDat();
	char B[] = imgCon.evaluateBoundariesBotRep();// uses color data to create a graph
	int res = 4;
	Map G = new Map(imgCon.H / res, imgCon.W / res);
	G.initialize();
	// Translates map to Map

	for (int i = 0; i < imgCon.W; i += res)
	{
	    for (int j = 0; j < imgCon.H; j += res)
	    {
		char marker = '!';
		try
		{
		    G.node[j / res][i / res].set = '0';
		}
		catch (Exception e)
		{
		}

		// preserves walls under low resolution
		for (int k = 0; k < res; k += 1)
		{
		    for (int l = 0; l < res; l += 1)
		    {
			for (int m = 0; m < imgCon.C.length; m++)
			{
			    try
			    {
				if (imgCon.getRGB(i, j) == imgCon.C[m].i)
				{
				    marker = B[m];
				    break;
				}
			    }
			    catch (Exception e)
			    {
			    }
			}
			if (marker == 'X')
			{
			    try
			    {
				G.node[j / res][i / res].set = 'X';
			    }
			    catch (Exception e)
			    {
			    }
			}
		    }
		}
	    }
	}
	imgCon = null;
	G.res = res;

	// G.initializeCostFunction(userSize);
	// G.print(3);
	return G;
    }

    /**
     * Reads in floorplans from an image file. Color data is also used here. White is assigned to free space, and black is boundary. Green is also free space as it designates doors in the maps that were used. In complete maps (including an
     * svg file) additional processing is done once the room data is read in and the vertices are determined. More specifically, the boundaries generated here are logically ANDed with the grid cells containing a line connecting two vertices
     * of the room (a wall/edge). postProcess tells the image to look for labels designating rooms. This works with reasonable accuracy but is unnecessary when the map includes svg data. These rooms are saved in Landmarks list and can be
     * called with imgCon.LandmarksForFloorPlans. A map is generated with nodes specifying boundary and free space.
     */
    private Map readFloorPlan(int file, boolean postProcess)
    {
	// This reads the PNG file here
	Log.d(Constants.TAG, "MainInterface::readFloorPlan() called!");

	Bitmap mBitmap = BitmapFactory.decodeResource(getResources(), file);
	int res = 6;
	ImageConvert imgCon = new ImageConvert(mBitmap, "FloorPlan");// reads file
	imgCon.initializePixArray();// loads data from image into pix array
	Map G = new Map(imgCon.H / res, imgCon.W / res);

	// Returns a 2D array of nodes
	Node data[][] = imgCon.floorPlanInterpret(res, postProcess, G);

	// imgCon.colByPercentage();
	// imgCon.printColDat();

	G.initialize();
	G.node = data;
	if (postProcess)
	{
	    G.annotations.L = imgCon.LandmarksForFloorPlans;
	}
	imgCon = null; // Make it null for garbage collector to free memory
	G.res = res;

	Log.d(Constants.TAG, "MainInterface::readFloorPlan() exiting!");

	return G;
    }

    /**
     * Runs D star on the entire Grid. If start and end nodes are null or if there is already a path, the algorithm does not run. Otherwise it uses current status of grid and runs from there. Always terminates in solution path or returns
     * -1. Calls DStarGraph internally when room data is supplied. This algorithm is very similar to DStarLite. The replanning is handled a bit differently. Each node is assigned a timeToOPEN and a timeToCLOSED upon being added to either
     * list. When replanning, a nearby node is chosen and the whole map is brought back to its state at that time. See Map.rollback(time) Nodes J and K are the start and finish nodes for the path.
     */
    public int DStar(Node J, Node K)
    {
	// Begin debugging info for the log window
	String jStr = "";
	String kStr = "";

	if (J == null)
	    jStr = "J: (null)";
	else
	    jStr = "J:" + J.toString();

	if (K == null)
	    kStr = "K: (null)";
	else
	    kStr = "K:" + K.toString();
	Log.d(Constants.DSTAR_TAG, "DStar(" + jStr + ", " + kStr + ") Method Entered");
	// End debugging info for the log window

	boolean Dbug = true; // false;

	if (Dbug)
	    System.out.println("Started DStar");

	if (J == null || K == null)
	{
	    if (Dbug)
		System.out.println("Start is null: " + (J == null) + " Dest is null: " + (K == null));
	    solution.clear();
	    return 0;
	}
	else
	{
	    // Debug
	    Log.d(Constants.DSTAR_TAG, "Neville's paired Node: " + J.toString());
	    Log.d(Constants.DSTAR_TAG, "dest paired Node: " + K.toString());

	    J.timeToOPEN = Integer.MAX_VALUE;
	    K.timeToOPEN = Integer.MAX_VALUE;
	}

	Node S = J;
	Node T = K;

	if (MapList.MAPS[CURRENTMAPSHOW][5].equals("C"))
	{
	    LogTime = System.currentTimeMillis();

	    // Neville's room
	    Room nevilleRoom = S.M.rooms.get(S.M.RoomsMap[J.x][J.y]);
	    Log.d(Constants.DSTAR_TAG, "********************");
	    Log.d(Constants.DSTAR_TAG, "Neville RoomsMap[" + J.x + "][" + J.y + "] = " + S.M.RoomsMap[J.x][J.y]);
	    Log.d(Constants.DSTAR_TAG, "S.M.rooms.get(" + S.M.RoomsMap[J.x][J.y] + ") = " + S.M.rooms.get(S.M.RoomsMap[J.x][J.y]).getID());

	    // destination's room
	    Room destRoom = T.M.rooms.get(T.M.RoomsMap[K.x][K.y]);
	    Log.d(Constants.DSTAR_TAG, "Dest RoomsMap[" + K.x + "][" + K.y + "] = " + T.M.RoomsMap[K.x][K.y]);
	    Log.d(Constants.DSTAR_TAG, "T.M.rooms.get(" + T.M.RoomsMap[K.x][K.y] + ") = " + T.M.rooms.get(T.M.RoomsMap[K.x][K.y]).getID());

	    Log.d(Constants.DSTAR_TAG, "********************");

	    nevilleRoom.setEnroute(true);
	    userLevelNodes[0] = Neville.getPairedNode();
	    userLevelNodes[1] = dest.getPairedNode();

	    if (nevilleRoom != destRoom)
	    {// if start room and endroom are same...
		if (DStarGraph(nevilleRoom, destRoom, 0) == -1)
		{
		    try
		    {
			if (WRITE_DSTARGRAPH)
			    DStarGraphLog += "Fails in " + (System.currentTimeMillis() - LogTime) + " ms\n";
		    }
		    catch (Exception e)
		    {
		    }
		    return -1;
		}
		try
		{
		    if (WRITE_DSTARGRAPH)
			DStarGraphLog += "Completes in " + (System.currentTimeMillis() - LogTime) + " ms\n";
		}
		catch (Exception e)
		{
		}
		if (Dbug)
		{
		    System.out.println("Escapes Graph D*");
		}

	    }
	    S = userLevelNodes[0];
	    T = userLevelNodes[1];
	}

	LogTime = System.currentTimeMillis();
	Node n;
	// uses first node
	T.rhs = 0;
	if (H.OPEN.isEmpty() && H.time == 1)
	{
	    H.OPEN.add(T);
	}
	int negcount = 0;
	if (Dbug)
	{
	    System.out.println("Before Loop");
	}
	while (!H.OPEN.isEmpty())
	{// runs while there are nodes in the open set
	    if (Dbug)
	    {
		// System.out.println("OPEN size " + H.OPEN.size());
	    }

	    n = H.getBestNode(S, T);// most likely node for next element in best path

	    if (n.g > n.rhs)
	    {
		n.g = n.rhs;
	    }
	    n.timeToCLOSED = H.time;// node is added to closed set at this time.
	    n.set = '2';// move the current node into the closed set
	    H.OPEN.remove(n);
	    H.CLOSED.add(n);

	    if (Dbug)
	    {
		// System.out.println("Neighbor size " + H.getNeighbors(n).size());
	    }
	    for (Node i : H.getNeighbors(n))
	    {// for each child node

		if (i == S)
		{// solution is found
		    i.p = n;
		    n.hasChildren = true;
		    H.OPEN.remove(S);
		    H.CLOSED.add(S);
		    i.rhs = n.rhs + mag(i, n);
		    solution.establishPath(S, T);

		    try
		    {
			if (WRITE_DSTARGRID)
			    DStarGridLog += "Completes in " + (System.currentTimeMillis() - LogTime) + " ms\n";
		    }
		    catch (Exception e)
		    {
		    }
		    return 1;
		    // the end node has been reached
		}

		if (MapList.MAPS[CURRENTMAPSHOW][5].equals("C") && H.RoomsMap[i.x][i.y] == -1)
		{// every so often, a doorway gets mislabeled as "Not in a room"
		    negcount++;// This allows these to be traversable, but if it exceeds a threshold (10) the user has left the map, and that's bad.
		}

		if (i.set == '0' && ((H.RoomsMap[i.x][i.y] == -1 && negcount < 10) || (H.RoomsMap[i.x][i.y] != -1)))
		{// new node added to open
		    i.p = n;
		    i.timeToOPEN = H.time;
		    i.rhs = n.rhs + mag(i, n);
		    i.set = '1';
		    H.OPEN.add(0, i);
		    // all neighbors neighbors that are not descendents of n are
		    // now in the open set

		}
		else
		    if (i.set == '1' || i.set == '2')
		    {// takes most optimal node
			if (i.rhs > n.rhs + mag(i, n))
			{
			    i.p = n;
			    i.rhs = n.rhs + mag(i, n);
			    n.hasChildren = true;

			    if (i.set == '2')
			    {
				H.CLOSED.remove(i);
				// i.timeToOPEN=H.time;
				H.OPEN.add(0, i);
				i.set = '1';
			    }
			}
		    }

	    }
	    H.time++;
	}
	try
	{
	    if (WRITE_DSTARGRID)
		DStarGridLog += "Fails in " + (System.currentTimeMillis() - LogTime) + " ms\n";
	}
	catch (Exception e)
	{
	}
	return -1;
    }

    /**
     * Uses the nodes from DStar. First, it checks to see if the nodes can support a path. It does this by continuously moving up in the Building>Floor>Room>Node hierarchy until it finds that the start and end node share a common ancestor.
     * This is evidence of a path. The algorithm then runs from one level beneath the common ancestor node and recursively narrows in on the are where the user is currently at. The algorithm returns values to the Grid Planner and a path is
     * calculated only on the floor that the user can currently see.
     */
    public int DStarGraph(Vertex _Start, Vertex _Goal, int code)
    {
	// at this point the vertex arguments are both rooms
	System.out.println("Enters DStarGraph at recursion level " + code);
	System.out.println("Vertices: " + _Start + " \n\t\tand " + _Goal);

	Vertex Start = _Start;
	Vertex Goal = _Goal;
	System.out.println("Hierarchy: S = " + Start.getHierarchy() + " G = " + Goal.getHierarchy());
	if (code == 0)
	{
	    // runs first time only--raises both to same hierarchy and finds common node
	    for (Vertex M : Neville.isOnMap().rooms)
	    {
		M.setParent(null);
		M.setEnroute(false);
	    }
	    System.out.println("Enters first level block.");

	    while (Start.getHierarchy() < Goal.getHierarchy())
	    {
		// if user only specifies a beginning building or floor
		System.out.println("Start raising.");
		Start = Start.raise();
		System.out.println("Hierarchy: S = " + Start.getHierarchy() + " G = " + Goal.getHierarchy() + " Start was raised.");
	    }
	    try
	    {
		while (Start.raise() != Goal.raise())
		{
		    // common node
		    System.out.println("Both raising.");

		    Start = Start.raise();
		    Goal = Goal.raise();

		    System.out.println("Hierarchy: S = " + Start.getHierarchy() + " G = " + Goal.getHierarchy() + " Both were raised.");
		}
	    }
	    catch (NullPointerException npe)
	    {
		// npe.printStackTrace();
		return -1;// There is no connection between these rooms.
	    }
	}

	System.out.println("Planning from " + Start + " \n\t\tto " + Goal);

	ArrayList<Vertex> OPEN = new ArrayList<Vertex>();// to do replanning, these may need to be global. or
	ArrayList<Vertex> CLOSED = new ArrayList<Vertex>();// the setParent parameter of Vertex may be enough

	float cost;
	Vertex V = Goal;

	V.setCumulativeCost(0.0f);
	// a temporary node that marks the closest node to whichever
	OPEN.add(V);
	// System.out.println("R.id = " + R.getID());

	while (!OPEN.isEmpty())
	{// runs while there are nodes in the open set

	    // This doesn't need sorted, the node with the minimum cost just needs be found
	    int minV = 0;
	    for (int y = 1; y < OPEN.size() && OPEN.size() > 1; y++)
	    {
		if (OPEN.get(y).getCumulativeCost() < OPEN.get(minV).getCumulativeCost())
		{
		    minV = y;
		}
	    }

	    V = OPEN.get(minV);
	    OPEN.remove(minV);
	    CLOSED.add(V);
	    // System.out.println(V+" is the new V");

	    for (Edge A : V.getEdges())
	    {
		// expands node
		// System.out.println("Checks from "+A.getFrom() +" to "+A.getTo());
		if (A.isToHigherLevel())// doesn't consider building exits as a connection between floors
		    continue;
		cost = A.getTo().getCost();

		if (A.getTo() == Start)
		{
		    // found a connection (probably non-optimal)
		    System.out.println("Found the User at hierarchy: " + Start.getHierarchy());
		    if (Start.getHierarchy() == 0)
		    {
			// restricts path to rooms at hierarchy level 0
			A.getTo().setParent(A.getFrom());

			System.out.println("Plans Rooms.");
			A.getTo().setCumulativeCost(V.getCumulativeCost() + cost);

			OPEN.remove(A.getTo());
			CLOSED.add(A.getTo());
			Start.setEnroute(true);
			Goal.setEnroute(true);
			V = Start;
			System.out.println("\t" + V);
			while (V.getParent() != null && V != Goal)
			{
			    V.setEnroute(true);
			    V = V.getParent();
			    System.out.println("\t" + V);
			}
			return 1;
		    }
		    System.out.println("\t" + A.toNode() + " \n\t" + A.fromNode());
		    if (Start.getHierarchy() == 1)
		    {
			// establishes destination and start at hierarchy level 1
			userLevelNodes[0] = Neville.getPairedNode();
			System.out.println("Selects D* start and end points.");

			if (dest.isOnMap() == Neville.isOnMap())
			{
			    userLevelNodes[1] = dest.getPairedNode();
			    System.out.println("Using Dest");

			}
			else
			{
			    System.out.println("Not Using Dest");
			    userLevelNodes[1] = A.toNode();

			    /*
			     * for(Exit E : Neville.isOnMap().annotations.E){ //TODO this should be just the exits for the particular room, not whole map System.out.println("Checking Exits "+E.longMes+" "
			     * +"\n\t GetSub: "+E.getToSub()+"\n\t GetTo: "+A.getTo()); if(E.getToSub()==A.getTo()){ userLevelNodes[1]=Neville.isOnMap().node[((Exit)A).x][((Exit)A).y]; System.out.println("Is sending "+E+" to the Grid.");
			     * break; } }
			     */
			}

		    }
		    System.out.println("Going to plan from " + _Start + " to " + A.getToSub());
		    try
		    {
			System.out.println(((Exit) A).shortMes + "-" + ((Exit) A).longMes + " is the exit being used. it is in room " + A.getToSub());
		    }
		    catch (Exception e)
		    {
		    }

		    DStarGraph(_Start, A.getToSub(), code + 1);// moves down the hierarchy.
		    return 1;
		    // the end node has been reached
		}

		if (!OPEN.contains(A.getTo()) && !CLOSED.contains(A.getTo()))
		{

		    A.getTo().setCumulativeCost(V.getCumulativeCost() + cost);
		    OPEN.add(A.getTo());
		    A.getTo().setParent(A.getFrom());

		    // all neighbors neighbors that are not descendents of n are
		    // now in the open set

		}
		else
		{
		    // takes most optimal node
		    if (A.getTo().getCumulativeCost() > V.getCumulativeCost() + cost)
		    {
			A.getTo().setCumulativeCost(V.getCumulativeCost() + cost);
			A.getTo().setParent(A.getFrom());
			// CLOSED.remove(A.getTo());//??
			// OPEN.add(A.getTo());
		    }

		}
	    }
	}

	return -1;
    }

    Menu subDest;
    Menu subOptions;

    /** Menu does nothing upon generation. Reloads every time it is called */
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
	super.onCreateOptionsMenu(menu);
	return true;
    }

    /**
     * Menu is set up. It can be different at different parts of application and must be loaded new each time.
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
	menu.clear();

	if (dispView != null)
	{

	    // DESTINATION MENU item.setIcon(R.drawable.paint);//<--can be used to make sexy icons
	    subDest = menu.addSubMenu("Select Destination");
	    while (subDest.hasVisibleItems())
	    {
		subDest.removeItem(0);
	    }
	    subDest.add("Manually");
	    if (!H.annotations.L.isEmpty())
	    {
		subDest.add("Goto Landmark");
	    }
	    subDest.add("Nearest Exit");

	    // ANNOTATIONS MENU
	    SubMenu subTag = menu.addSubMenu("Tag Location");
	    subTag.setGroupCheckable(1, true, false);
	    subTag.add("Add Landmark");
	    subTag.add("Add Obstacle");
	    // OPTIONS MENU
	    subOptions = menu.addSubMenu("Options");
	    MenuItem syncButton = subOptions.add(2, Menu.NONE, Menu.NONE, "Sync with Localization");
	    MenuItem routeButton = subOptions.add(2, Menu.NONE, Menu.NONE, "Route Guidance");
	    MenuItem predictButton = subOptions.add(2, Menu.NONE, Menu.NONE, "Path Prediction");
	    subOptions.setGroupCheckable(2, true, false);
	    if (useLocalization)
	    {
		syncButton.setChecked(true);
	    }
	    else
	    {
		syncButton.setChecked(false);
	    }
	    if (useRouteGuidance)
	    {
		routeButton.setChecked(true);
	    }
	    else
	    {
		routeButton.setChecked(false);
	    }
	    if (usePathPrediction)
	    {
		predictButton.setChecked(true);
	    }
	    else
	    {
		predictButton.setChecked(false);
	    }
	    subOptions.performIdentifierAction(2, Menu.FLAG_PERFORM_NO_CLOSE);
	    // ADMINISTRATOR MENU
	    if (admin)
	    {
		// System.out.println(syncButton.isChecked());
		SubMenu subAdmin = menu.addSubMenu("Admin");
		subAdmin.add("Show Rooms");
		subAdmin.add("Remove Exits");
		subAdmin.add("Push Exits");
		subAdmin.add("Points Transform");
		subAdmin.add("Flush Saves");
		subAdmin.add("Load Another Map");
	    }
	    if (dest != null && Neville != null /* Neville.isPlaced() */)
	    {
		menu.add("Replan Route");
	    }
	    // LOAD MENU
	    // menu.add("Load Map");
	    // USER MENU
	    SubMenu subUser = menu.addSubMenu("User");
	    subUser.add("Push Annotations");
	    subUser.add("Clear Path");
	    subUser.add("Clear Path and Annotations");
	    subUser.add("Cancel");

	    SubMenu subLoc = menu.addSubMenu("Localization");
	    if (((SensorManager) getSystemService(Context.SENSOR_SERVICE)).getSensorList(Sensor.TYPE_GYROSCOPE).size() > 0)
		subLoc.add("Calibrate Gyro");
	    else
		subLoc.add("Load Data");
	    subLoc.add("Calibrate Compass");
	    if (SERVICE_IS_BINDED)
	    {
		subLoc.add("Start Service");
		subLoc.add("Stop Service");
	    }
	    menu.add("Close Application");

	}
	else
	{
	    // LOAD MENU AND OPTIONS ONLY AT START-UP
	    menu.add("Load Map");
	    if (admin)
	    {
		// System.out.println(syncButton.isChecked());
		SubMenu subAdmin = menu.addSubMenu("Admin");
		subAdmin.add("Flush Saves");
	    }
	    subOptions = menu.addSubMenu("Options");
	    MenuItem syncButton = subOptions.add(2, Menu.NONE, Menu.NONE, "Sync with Localization");
	    MenuItem routeButton = subOptions.add(2, Menu.NONE, Menu.NONE, "Route Guidance");
	    subOptions.setGroupCheckable(2, true, false);
	    if (useLocalization)
	    {
		syncButton.setChecked(true);
	    }
	    else
	    {
		syncButton.setChecked(false);
	    }
	    if (useRouteGuidance)
	    {
		routeButton.setChecked(true);
	    }
	    else
	    {
		routeButton.setChecked(false);
	    }
	    subOptions.performIdentifierAction(2, Menu.FLAG_PERFORM_NO_CLOSE);
	}
	return true;

    }

    /** Handles the clicking of a menu item */
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {

	if (dispView != null)
	{
	    dispView.disableActions();
	}
	// USER MENU
	if (item.getTitle().equals("Clear Path"))
	{// unlinks user and destination nodes and deletes path
	    if (reset())
	    {
		Toast.makeText(getApplicationContext(), "Path Cleared", Toast.LENGTH_SHORT).show();
	    }
	    return true;
	}
	else
	    if (item.getTitle().equals("Clear Path and Annotations"))
	    {// clears annotations as well.

		H.annotations.clearAnnotations();
		H.syncObstacles();
		reset();
		Toast.makeText(getApplicationContext(), "Path and Annotations Cleared", Toast.LENGTH_SHORT).show();
		return true;
	    }
	    else
		if (item.getTitle().equals("Push Annotations"))
		{// writes current landmarks and obstacles to file
		 // dispView.debug=!dispView.debug;
		    pushAnnotations();
		    return true;
		} // IN ANNOTATIONS MENU
		else
		    if (item.getTitle().equals("Add Obstacle"))
		    {
			setTitle("Double click a position to add obstacle.");
			dispView.enableAnnotation("Obstacle");
			return true;
		    }
		    else
			if (item.getTitle().equals("Add Landmark"))
			{
			    setTitle("Double click a position to add landmark.");
			    dispView.enableAnnotation("Landmark");
			    return true;

			} // GOTO DESTINATION MENU
			else
			    if (item.getTitle().equals("Goto Landmark"))
			    {
				Intent newIntent = new Intent(getApplicationContext(), ListMenu.class);
				newIntent.putExtra("Landmarks", H.annotations.getLandmarkTitles());
				startActivityForResult(newIntent, 0);
				usePathPrediction = false;
			    }
			    else
				if (item.getTitle().equals("Manually"))
				{
				    dispView.disableActions();
				    dispView.placeDestination();
				    usePathPrediction = false;// if user specifies destination, path predictor is not needed.
				}
				else
				    if (item.getTitle().equals("Nearest Exit"))
				    {

					if (Neville.getPairedNode() != null)
					{
					    // System.out.println("HI");
					    Toast.makeText(this, "Specify a starting point.", Toast.LENGTH_SHORT).show();

					    return true;
					}
					if (!H.annotations.E.isEmpty())
					{
					    // TODO make sure code is not checking other maps. and if it is, make sure it is happening better than this.
					    // This worked for single floor but won't for multifloor yet.
					    double min = Double.POSITIVE_INFINITY;
					    double dist;
					    int minIndex = 0;
					    for (int i = 0; i < H.annotations.E.size(); i++)
					    {
						dist = mag(Neville.getPairedNode()/* Neville */, H.node[H.annotations.E.get(i).x][H.annotations.E.get(i).y]);
						if (dist < min)
						{
						    min = dist;
						    minIndex = i;
						}

					    }
					    dest.setPairedNode(H.node[H.annotations.E.get(minIndex).x][H.annotations.E.get(minIndex).y]);
					    H.reinitialize();
					    H.cleanUpOpen();

					    // regenerates path
					    if (DStar(Neville.getPairedNode()/* Neville */, dest.getPairedNode()) == -1)
					    {
						Toast.makeText(this, "No direct path to specified destination.", Toast.LENGTH_SHORT).show();
					    }
					    dispView.postInvalidate();
					    usePathPrediction = false;
					}
					else
					{
					    Toast.makeText(this, "Exits Unknown.", Toast.LENGTH_SHORT).show();
					}
					// OPTIONS MENU
				    }
				    else
					if (item.getTitle().equals("Sync with Localization"))
					{// localization will be computed still, but not used
					    useLocalization = !useLocalization;
					    return true;
					}
					else
					    if (item.getTitle().equals("Route Guidance"))
					    {// when localization updates, camera follows user.
						useRouteGuidance = !useRouteGuidance;
						return true;
					    }
					    else
						if (item.getTitle().equals("Path Prediction"))
						{// allows path prediction to change destination
						    usePathPrediction = !usePathPrediction;
						    return true;
						}
						else
						    if (item.getTitle().equals("Replan Route"))
						    {// force recalculate
							requestToReplan = true;
							return true;
						    }// ADMINISTRATOR MENU
						    else
							if (item.getTitle().equals("Show Rooms"))
							{// shows rooms generated along with walls (red) and grid cells not
							 // included in the RoomsMap (blue)
							    dispView.debug = !dispView.debug;
							    return true;
							}
							else
							    if (item.getTitle().equals("Load Another Map"))
							    {
								// currently toggles between floor one and two. A path only generated on the floor with the user
								if (CURRENTMAPSHOW == 0)
								{
								    CURRENTMAPSHOW = 1;
								    H = NSH.floor.get(1);
								    displayMap(CURRENTMAPSHOW, H);
								}
								else
								{
								    CURRENTMAPSHOW = 0;
								    H = NSH.floor.get(0);
								    displayMap(CURRENTMAPSHOW, H);

								}
								return true;
							    }
							    else
								if (item.getTitle().equals("Points Transform"))
								{
								    // used when testing a Maplist.mTrans transform
								    pointsTransform();
								    return true;
								}
								else
								    if (item.getTitle().equals("Remove Exits"))
								    {
									H.annotations.clearAnnotations("E");
									return true;
								    }
								    else
									if (item.getTitle().equals("Flush Saves"))
									{
									    for (int o = 0; o < MapList.MAPS.length; o++)
									    {
										try
										{
										    deleteFile(MapList.MAPS[o][4] + "_map.txt");
										}
										catch (Exception e)
										{
										}
										try
										{
										    deleteFile(MapList.MAPS[o][4] + "_sector.txt");
										}
										catch (Exception e)
										{
										}
										try
										{
										    deleteFile(MapList.MAPS[o][4] + "_room.txt");
										}
										catch (Exception e)
										{
										}
										try
										{
										    deleteFile(MapList.MAPS[o][4] + "_exit.txt");
										}
										catch (Exception e)
										{
										}
									    }
									    return true;

									}
									else
									    if (item.getTitle().equals("Push Exits"))
									    {// only admin can save exits
										MapIO.saveExitsFromLandmarks(CURRENTMAPSHOW, H, H.scale, H.res);
									    }
									    else
										if (item.getTitle().equals("Load Map"))
										{// may not work anymore
										    Intent newIntent = new Intent(getApplicationContext(), ListMenu.class);
										    newIntent.putExtra("MapSelect", MapList.MAPS);
										    startActivityForResult(newIntent, 0);
										}
										else
										    if (item.getTitle().equals("Start Service"))
										    {// calls localization to run
											doStartService();
											return true;
										    }
										    else
											if (item.getTitle().equals("Stop Service"))
											{// stops localization
											    doUnbindService();
											    recognizer.reset();
											    return true;
											}
											else
											    if (item.getTitle().equals("Calibrate Gyro") || item.getTitle().equals("Load Data"))
											    {
												if (((SensorManager) getSystemService(Context.SENSOR_SERVICE)).getSensorList(Sensor.TYPE_GYROSCOPE).size() > 0)
												{
												    Intent i = new Intent(getApplicationContext(), CalibrateGyroActivity.class);
												    startActivityForResult(i, 1337);
												}
												doBindService();
												load = new runLoadDataTask().execute();
												return true;
											    }
											    else
												if (item.getTitle().equals("Calibrate Compass"))
												{
												    Intent i = new Intent(getApplicationContext(), CalibrateCompassActivity.class);
												    startActivityForResult(i, 1338);
												    return true;
												}
												else
												    if (item.getTitle().equals("Close Application"))
												    {// necessary for writing files
													try
													{
													    DStarGridWriter.write(DStarGridLog);
													    DStarGraphWriter.write(DStarGraphLog);
													    DStarGridWriter.close();
													    DStarGraphWriter.close();
													}
													catch (Exception e)
													{
													}
													try
													{
													    if (WRITE_IO)
														IOWriter.write(IOLog);
													    if (WRITE_IO)
														IOWriter.close();
													}
													catch (Exception e)
													{
													}

													finish();
													return true;
												    }

	// Consume the selection event.
	return true;
    }

    /** Clears path */
    private boolean reset()
    {

	H.reinitialize();
	solution.clear();
	H.time = 1;
	dispView.postInvalidate();
	Neville.clearLocation();
	dest.clearLocation();
	return true;
    }

    /** magnitude of a vector */
    public static float mag(Node a, Node b)
    {// distance between two nodes
	return (float) Math.pow(Math.pow(a.x - b.x, 2) + Math.pow(a.y - b.y, 2), .5);
    }

    /** This is where the program goes after being in one of the List Menus */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
	super.onActivityResult(requestCode, resultCode, data);
	if (resultCode == 1)
	{// an annotation is being created
	    dispView.completeAnnotation(data.getStringArrayExtra("NoteData"));
	}
	if (resultCode == 2)
	{// a landmark is being chosen as the next location
	    int ind = data.getIntExtra("Index", -1);

	    dest.setPairedNode(H.node[H.annotations.L.get(ind).x][H.annotations.L.get(ind).y]);
	    H.time = 1;
	    try
	    {
		H.reinitialize();
	    }
	    catch (Exception e)
	    {
	    }
	    H.cleanUpOpen();

	    // regenerates path
	    if (DStar(Neville.getPairedNode(), dest.getPairedNode()) == -1)
	    {
		Toast.makeText(getApplicationContext(), "No direct path to " + H.annotations.L.get(ind).shortMes + "!", Toast.LENGTH_SHORT).show();
	    }
	    dispView.postInvalidate();
	}
	if (resultCode == 3)
	{// a map is being loaded based on selection
	    loadMap(data.getIntExtra("MapIndex", -1));

	}
	if (requestCode == 1337)
	{// Calibrate Gyro activity finished
	    float[] gyroConstants = GyroCalibrationDataProvider.getGyroCalibrationData();
	    Log.i("calibrateGyro", "GYRO: X:" + gyroConstants[0] + " Y:" + gyroConstants[1] + " Z:" + gyroConstants[2]);
	}
	if (requestCode == 1338)
	{// Calibrate Compass activity has finished
	    float offset = 0;
	    offset = data.getExtras().getFloat("compassOffset");
	    float radianOffset = (float) Math.toRadians((double) offset);
	    // Toast.makeText(this, "OFFSET: " + radianOfset, Toast.LENGTH_LONG);
	    Log.i("AHHH", "OFFSET: " + radianOffset);
	    mBoundService.setOrientationOffset(radianOffset);
	}
    }

    /**
     * Landmarks and Obstacles are written to a file so that they can be loaded by other users upon opening the program.
     */

    public void pushAnnotations()
    {

	// saves landmark information
	try
	{
	    MapIO.saveLandmarks(CURRENTMAPSHOW, H, H.scale, H.res);
	}
	catch (Exception e)
	{
	}
	try
	{
	    MapIO.saveObstacles(CURRENTMAPSHOW, H, H.scale, H.res);
	}
	catch (Exception Ahhhh_HELL_NO)
	{
	}
	Toast.makeText(getApplicationContext(), "Annotations Saved", Toast.LENGTH_SHORT).show();
    }

    /** Changes contents of Display view to the map listed at MapList.MAPS */
    public void displayMap(int WhichMap, Map Q)
    {
	Log.d(Constants.TAG, "displayMap() Method Entered");

	boolean dispBool = dispView == null;
	if (dispBool)
	{
	    dispView = new DisplayView(this);
	}
	dispView.initializeDisplay(Integer.parseInt(MapList.MAPS[WhichMap][3]), Q, Q.scale, Q.res, MapList.MAPS[WhichMap][2], recognizer);
	Log.d(Constants.TAG, "displayMap() Method Exited");
    }

    static boolean directMapLoad = true;

    /**
     * Loads the selected map. Tries to read from text file first. Does nothing if it tries to load same map it is currently on. Map is read from scratch if file is not found. If map is complete, a map saying what node each room is in is
     * created. Rooms are loaded or created as well. Landmarks and Obstacles are loaded from respective files. Maps are only saved when they are generated and not when they are loaded.READ_ONLY_MAP makes sure the program does not write new
     * files for the maps. This will be true upon releasing application. Program has a tendency to overwrite files when an attempt to run app with droid mounted is made.
     */
    public Map loadMap(int data) throws NumberFormatException
    {
	Log.d(Constants.TAG, "MainInterface::loadMap(" + data + ") Called");

	boolean READ_ONLY_MAP = false;
	Map Q = null;
	CURRENTMAPLOAD = data;

	if (!READ_ONLY_MAP)
	{
	    Log.d(Constants.TAG, "loadMap() - Map is NOT Read Only.");
	    IOLog += "MAP DATA IS NOT READ ONLY\n";
	}
	else
	{
	    Log.d(Constants.TAG, "loadMap() - Map is Read Only.");
	}

	long StartedLoadingTime = System.currentTimeMillis();
	long MidLoadingTime = System.currentTimeMillis();
	IOLog += "Loading map " + MapList.MAPS[CURRENTMAPLOAD][0] + "\n";

	// attempts to load map data
	try
	{
	    /*
	     * 
	     * THIS SECTION READS THE FILE <floor plan filename>_map.txt THAT WAS GENERATED FROM A PREVIOUS RUN WHEN SVG FILE WAS PARSED.
	     * 
	     * Once the map file is read, the 'set' member variable in the node class for each node in the node[][] array is set to either 'X' or '0'.
	     */
	    Log.d(Constants.TAG, "loadMap() - Attempting to load map: " + Environment.getExternalStorageDirectory() + "/NavPalSaves/" + MapList.MAPS[CURRENTMAPLOAD][4] + "_map.txt");

	    File f = new File(Environment.getExternalStorageDirectory() + "/NavPalSaves/" + MapList.MAPS[CURRENTMAPLOAD][4] + "_map.txt");
	    FileInputStream inputStream = new FileInputStream(f);
	    StreamTokenizer st = new StreamTokenizer(inputStream);

	    st.whitespaceChars(' ', ' ');
	    st.eolIsSignificant(true);
	    // System.out.println("Starts to read " + MapList.MAPS[CURRENTMAP][4]);

	    Log.d(Constants.TAG, "loadMap() - Starting to read the map file '" + MapList.MAPS[CURRENTMAPLOAD][4] + "_map.txt'.");
	    int x, y, s, readRes;
	    st.nextToken();
	    x = (int) st.nval;
	    st.nextToken();
	    y = (int) st.nval;
	    st.nextToken();
	    s = (int) st.nval;
	    st.nextToken();
	    readRes = (int) st.nval;

	    Log.d(Constants.TAG, "loadMap() - Values Read: x = " + x + ", y= " + y + ", s= " + s + ", readRes = " + readRes);

	    Q = new Map(x, y);
	    Q.initialize();
	    int token;
	    for (int i = 0; i < x; i++)
	    {
		for (int j = 0; j < y; j++)
		{
		    token = st.nextToken();
		    if (token == StreamTokenizer.TT_EOF)
		    {
			System.out.println("Reached EOF while Parsing");
			Log.d(Constants.TAG, "loadmap() - Reached EOF while Parsing.");
			break;
		    }
		    if (token == StreamTokenizer.TT_EOL)
		    {
			st.nextToken();
		    }
		    if (st.sval.charAt(0) == 'X')
		    {
			Q.node[i][j].set = 'X';
		    }
		    else
		    {
			Q.node[i][j].set = '0';
		    }
		}
	    }
	    inputStream.close();
	    Q.scale = s;
	    Q.res = readRes;
	    IOLog += "Map Loaded in " + (System.currentTimeMillis() - MidLoadingTime) + " ms\n";
	    Log.d(Constants.TAG, "loadMap() - Map Loaded in " + (System.currentTimeMillis() - MidLoadingTime) + " ms.");

	    // System.out.println("Map Loaded");
	    badLoad = false;

	    if (Q.res == 0)
	    {
		Log.d(Constants.TAG, "loadMap() - Q.Res == 0, Map failed to load.");
		badLoad = true;
		throw new Exception();// Map has failed to load
	    }

	}
	catch (Exception exc)
	{
	    // System.out.println("Failed To Load " + MapList.MAPS[CURRENTMAP][4] + ".");
	    // couldn't load from text file cause it doesn't exist
	    IOLog += "Map Loading Failed after " + (System.currentTimeMillis() - MidLoadingTime) + " ms\n";

	    Log.d(Constants.TAG, "loadMap() - Failed to load map file '" + MapList.MAPS[CURRENTMAPLOAD][4] + "_map.txt' after " + (System.currentTimeMillis() - MidLoadingTime) + " ms.");
	    Log.d(Constants.TAG, "loadMap() - Reverting to corresponding image file " + getResources().getResourceName(Integer.parseInt(MapList.MAPS[CURRENTMAPLOAD][3])) + " for floor plan '" + MapList.MAPS[CURRENTMAPLOAD][0] + ".");

	    MidLoadingTime = System.currentTimeMillis();

	    Log.d(Constants.TAG, "loadMap() - Checking if provided image file is a floor plan or robot map...");
	    if (MapList.MAPS[CURRENTMAPLOAD][2].equals("Floorplan"))
	    {
		Log.d(Constants.TAG, "loadMap() - The provided file is a floor plan.");
		Log.d(Constants.TAG, "loadMap() - Attempting to read the floor plan file " + getResources().getResourceName(Integer.parseInt(MapList.MAPS[CURRENTMAPLOAD][3])) + ".");

		// If a text file of the parsed map does not already exist, read the PNG image to get the floor plan data
		Q = readFloorPlan(Integer.parseInt(MapList.MAPS[CURRENTMAPLOAD][3]), false);
		System.out.println("loadMap() - Floor Plan Map Processed");
		Log.d(Constants.TAG, "loadMap() - Floor Plan Map Processed.");
	    }
	    else
	    {
		Log.d(Constants.TAG, "loadMap() - Map is NOT a floor plan!");
	    }

	    if (MapList.MAPS[CURRENTMAPLOAD][2].equals("Robot Map"))
	    {
		Log.d(Constants.TAG, "loadMap() - The provided file is a robot map.");
		Log.d(Constants.TAG, "loadMap() - Attempting to read the robot map file " + getResources().getResourceName(Integer.parseInt(MapList.MAPS[CURRENTMAPLOAD][3])) + ".");

		Q = readRobotRep(Integer.parseInt(MapList.MAPS[CURRENTMAPLOAD][3]));
		Log.d(Constants.TAG, "loadMap() - Robot Map Processed.");
	    }
	    else
	    {
		Log.d(Constants.TAG, "loadMap() - Map is NOT a robot map!");
	    }

	    Q.scale = 13;// arbitrary, as it can be adjusted by zooming at runtime

	    Log.d(Constants.TAG, "loadMap() - set scale" + Q.scale);
	    System.out.println("set scale" + Q.scale);

	    if (!READ_ONLY_MAP)
	    {
		Log.d(Constants.TAG, "Saving the Map '" + MapList.MAPS[CURRENTMAPLOAD][0] + "'.");
		MapIO.saveMap(CURRENTMAPLOAD, Q, Q.scale, Q.res);
	    }
	    IOLog += "Map for floorplan '" + MapList.MAPS[CURRENTMAPLOAD][0] + "' generated in " + (System.currentTimeMillis() - MidLoadingTime) + " ms\n";
	    Log.d(Constants.TAG, "loadMap() - Map for floorplan '" + MapList.MAPS[CURRENTMAPLOAD][0] + "' generated in " + (System.currentTimeMillis() - MidLoadingTime) + " ms.");
	}
	System.gc();

	Log.d(Constants.TAG, "loadMap() - After try-catch block");

	solution.clear();

	Log.d(Constants.TAG, "loadMap() - Checking if map is 'C'...");
	if (MapList.MAPS[CURRENTMAPLOAD][5].equals("C"))
	{
	    Log.d(Constants.TAG, "loadMap() - Map IS 'C'...");

	    try
	    {
		// attempts to load room information from a previously saved room.txt file
		if (badLoad)
		{
		    Log.d(Constants.TAG, "loadMap() - Bad Data Encountered.");
		    throw new Exception();
		}

		/*
		 * 
		 * THIS SECTION READS THE FILE <floor plan filename>_room.txt THAT WAS GENERATED FROM A PREVIOUS RUN WHEN SVG FILE WAS PARSED.
		 * 
		 * The contents of the room file are stored in the room Array List, which contains a complete list of all rooms
		 */
		Log.d(Constants.TAG, "loadMap() - Attempting to load the rooms for the current floor plan ");
		MapIO.loadRooms(CURRENTMAPLOAD, Q, Q.res);
		IOLog += "Rooms Loaded in " + (System.currentTimeMillis() - MidLoadingTime) + " ms\n";
		Log.d(Constants.TAG, "loadMap() - Rooms loaded in " + (System.currentTimeMillis() - MidLoadingTime) + " ms.");
	    }
	    catch (Exception e1)
	    {
		// reads in an svg file and creates an ArrayList of rooms.
		// If a previous map data files does not exist, get the rooms from the corresponding SVG file.

		// it fails, try to load room data from scratch
		e1.printStackTrace();
		IOLog += "Rooms Failed to load " + (System.currentTimeMillis() - MidLoadingTime) + " ms\n";
		Log.d(Constants.TAG, "loadMap() - Rooms Failed to load " + (System.currentTimeMillis() - MidLoadingTime) + " ms.");

		MidLoadingTime = System.currentTimeMillis();
		System.out.println("Parsing XML for Rooms");

		try
		{
		    if (!READ_ONLY_MAP)
		    {
			Log.d(Constants.TAG, "loadMap() - Attempting to parse rooms");

			// This method reads the SVG file and determines the rooms from the colored map
			MapIO.parseRooms(this, CURRENTMAPLOAD, Q, Q.scale, Q.res);
		    }

		    IOLog += "Rooms Generated in " + (System.currentTimeMillis() - MidLoadingTime) + " ms\n";
		    Log.d(Constants.TAG, "loadMap() - Rooms Generated in " + (System.currentTimeMillis() - MidLoadingTime) + " ms\n");
		}
		catch (Exception e)
		{
		    System.out.println("Failed to Parse Room Data");
		    Log.d(Constants.TAG, "loadMap() - Failed to Parse Room Data");
		    e.printStackTrace();
		}
	    }
	}

	for (int i = 0; i < Q.rooms.size(); i++)
	{
	    Q.rooms.get(i).setID(i);
	    Q.rooms.get(i).M = Q;
	}
	MidLoadingTime = System.currentTimeMillis();

	// LANDMARKS
	try
	{
	    MapIO.loadLandmarks(CURRENTMAPLOAD, Q, Q.res);
	}
	catch (Exception e)
	{

	}
	// EXITS
	try
	{
	    MapIO.loadExits(CURRENTMAPLOAD, Q, Q.res);
	}
	catch (Exception e)
	{

	}
	// OBSTACLES
	try
	{
	    MapIO.loadObstacles(CURRENTMAPLOAD, Q, Q.res);
	}
	catch (Exception e)
	{

	}
	IOLog += "Annotations loaded in " + (System.currentTimeMillis() - MidLoadingTime) + " ms\n";
	Log.d(Constants.TAG, "loadMap() - Annotations loaded in " + (System.currentTimeMillis() - MidLoadingTime) + " ms\n");

	if (MapList.MAPS[CURRENTMAPLOAD][5].equals("C"))
	{
	    try
	    {
		/*
		 * This section loads the sector.txt file and loads it content into the RoomsMap file.
		 */
		MidLoadingTime = System.currentTimeMillis();

		MapIO.loadSector(CURRENTMAPLOAD, Q, Q.res);
		IOLog += "Sector loaded in " + (System.currentTimeMillis() - MidLoadingTime) + " ms\n";
	    }
	    catch (Exception e)
	    {
		IOLog += "Sector failed in " + (System.currentTimeMillis() - MidLoadingTime) + " ms\n";
		MidLoadingTime = System.currentTimeMillis();
		Q.initializeRoomMatrix();
		IOLog += "Sector generated in " + (System.currentTimeMillis() - MidLoadingTime) + " ms\n";
		try
		{
		    if (!READ_ONLY_MAP)
			MapIO.saveSector(CURRENTMAPLOAD, Q, Q.scale, Q.res);
		}
		catch (Exception aae)
		{
		    // System.out.println("Failed to save Sector Data");
		}
	    }
	    MidLoadingTime = System.currentTimeMillis();

	    // Q.dumpNodeArrayToFile("before_rifineMap");
	    // Q.dumpRoomsArrayListToFile("before_rifineMap");
	    // Q.dumpRoomsMapArrayToFile("before_rifineMap");

	    Q.refineMap();

	    // Q.dumpNodeArrayToFile("after_rifineMap");
	    // Q.dumpRoomsArrayListToFile("after_rifineMap");
	    // Q.dumpRoomsMapArrayToFile("after_rifineMap");

	    IOLog += "Connectivity established in " + (System.currentTimeMillis() - MidLoadingTime) + " ms\n";
	    MidLoadingTime = System.currentTimeMillis();

	    for (Exit E : Q.annotations.E)
	    {
		if (Q.RoomsMap[E.x][E.y] != -1)
		{
		    Q.rooms.get(Q.RoomsMap[E.x][E.y]).ex.add(E);
		}
	    }
	    for (Landmark L : Q.annotations.L)
	    {
		if (Q.RoomsMap[L.x][L.y] != -1)
		{
		    Q.rooms.get(Q.RoomsMap[L.x][L.y]).lm.add(L);
		}
	    }
	    for (Obstacle O : Q.annotations.O)
	    {
		if (Q.RoomsMap[O.x][O.y] != -1)
		{
		    Q.rooms.get(Q.RoomsMap[O.x][O.y]).ob.add(O);
		}
	    }
	}

	IOLog += "Landmarks assigned to rooms in " + (System.currentTimeMillis() - MidLoadingTime) + " ms\n";
	IOLog += "Entire map loaded in " + (System.currentTimeMillis() - StartedLoadingTime) + " ms\n";

	Log.d(Constants.TAG, "loadMap() - Landmarks assigned to rooms in " + (System.currentTimeMillis() - MidLoadingTime) + " ms");
	Log.d(Constants.TAG, "loadMap() - Entire map loaded in " + (System.currentTimeMillis() - StartedLoadingTime) + " ms");

	Q.dumpNodeArrayToFile("python");
	Q.dumpRoomsArrayListToFile("python");
	Q.dumpRoomsMapArrayToFile("python");

	//Log.d(Constants.DSTAR_TAG, "Dimenions of RoomsMap (x:" + Q.RoomsMap.length + ", y:" + Q.RoomsMap[0].length + ")" );
	
	return Q;
    }

    /**
     * THIS IS CURRENTLY FOR DEBUGGING ONLY. IF EVANFILE CONTAINS LOG DATA FOR THE FLOOR OF INTEREST, THE DATA WILL BE TRANSFORMED AND PROJECTED ONTO THE MAP. SINCE THE LOG DATA IS OF THE SAME NATURE AS THE POINT GIVEN BY LOCALIZATION, THE
     * CONTENTS OF THE LOG WILL ALLOW DEVELOPER TO TEST IF THE TRANSFORM IS CORRECT. RED POINTS WILL BE OVERLAYED ON THE MAP THAT SHOULD ALIGN WELL WITH THE SHAPE OF THE FLOOR
     */
    private void pointsTransform()
    {
	System.out.println(CURRENTMAPSHOW + " F");
	try
	{
	    InputStream readfile = getResources().openRawResource(R.raw.evanfile);
	    // System.out.println("Converting File...");
	    StreamTokenizer st = new StreamTokenizer(readfile);
	    st.whitespaceChars(' ', ' ');
	    // the transformation matrix
	    st.eolIsSignificant(true);
	    float x = 0, y = 0;

	    int token = st.nextToken();

	    while (token != StreamTokenizer.TT_EOF)
	    {
		// System.out.println("REPEATS");
		if (token == StreamTokenizer.TT_EOL)
		{// do nothing for end of line
		 // System.out.println("NEWLINE");
		    token = st.nextToken();

		}
		if (st.sval != null && st.sval.substring(0, 2).equals("NS"))
		{
		    // System.out.println("STRING");//print the label and dump all data){
		    token = st.nextToken();

		}
		else
		{
		    // System.out.println("NUMBERS");//collect another data point
		    y = (float) st.nval;
		    st.nextToken();
		    x = (float) st.nval;
		    // System.out.println(x + " " + y);
		    float A[][] = new float[3][1];
		    A[0][0] = y;
		    A[1][0] = x;
		    A[2][0] = 1.0f;
		    float B[][] = MapList.mTrans[CURRENTMAPSHOW];
		    float C[][] = MatrixBuddy.multiplyMatrices(B, A);// System.out.println(C[1][0] + " " + C[0][0]);
		    try
		    {
			boolean chekk = false;
			for (int i = 0; i < pointsShown; i++)
			{
			    if (dispView.px[i].x == (int) (C[1][0] / (double) H.res) && dispView.px[i].y == (int) (C[0][0] / (double) H.res))
			    {
				chekk = true;
				break;
			    }

			}
			if (!chekk)
			    dispView.px[pointsShown++] = new Point((int) (C[1][0] / (double) H.res), (int) (C[0][0] / (double) H.res));
		    }
		    catch (Exception e)
		    {
		    }

		}
		token = st.nextToken();
		// System.out.println(st.sval + " " + st.nval);

	    }
	}
	catch (Exception e)
	{
	    e.printStackTrace();
	}
    }

    /**
     * Localization data is sent here. It is transformed and updates position of user. Beliefstates are sent to the path predictor if the the user has changed rooms.
     */

    public void updateUserPosition(float x, float y, float r, ArrayList<Particle2D> AllParticles)
    {

	if (!useLocalization)
	{
	    return;
	}
	// System.out.print("("+x+","+y+") -> ");
	try
	{
	    MapIO.printLog(AllParticles, "NS:" + numberOfFrames++, log);
	}
	catch (Exception e)
	{
	}
	float A[][] = new float[3][1];
	A[0][0] = y;
	A[1][0] = x;
	A[2][0] = 1.0f;
	float B[][] = MapList.mTrans[CURRENTMAPSHOW];
	float C[][] = MatrixBuddy.multiplyMatrices(B, A);

	int newX = (int) (C[1][0] / (double) H.res);
	int newY = (int) (C[0][0] / (double) H.res);
	boolean needsUpdated = false;

	try
	{
	    if (neverUpdated || Neville.getPairedNode() != null/* !Neville.isPlaced() */|| H.RoomsMap[Neville.getPairedNode().x][Neville.getPairedNode().y] != H.RoomsMap[newX][newY])
	    {
		needsUpdated = true;
		neverUpdated = false;
	    }
	}
	catch (ArrayIndexOutOfBoundsException arrrrrgh)
	{
	    System.out.println("User is out of Bounds");
	    return;
	}
	// changes the user's position based on Localization data
	if (Neville.getPairedNode() == null/* !Neville.isPlaced() */)
	{
	    Neville.moveTo(newX, newY);
	    Neville.setPairedNode(H.node[newX][newY]);
	    if (H.node[newX][newY].set == '1')
		H.OPEN.remove(H.node[newX][newY]);
	    else
		if (H.node[newX][newY].set == '2')
		    H.CLOSED.remove(H.node[newX][newY]);
	    H.node[newX][newY].set = '0';
	    Neville.getPairedNode().g = 0;

	}
	else
	{

	    int u = H.youngestNeighbor(H.node[newX][newY]);
	    if (u > 0 && u < Integer.MAX_VALUE)
	    {
		H.rollback(u);
		H.node[newX][newY].p = null;
	    }
	    H.cleanUpOpen();
	    Neville.getPairedNode().p = null;
	    Neville.setPairedNode(H.node[newX][newY]);
	    // Neville.moveTo(newX,newY);
	    Neville.getPairedNode().set = '2';
	    Neville.getPairedNode().timeToOPEN = Integer.MAX_VALUE;

	}
	// All particles are sent to updater because Room has changed.
	if (needsUpdated && MapList.MAPS[CURRENTMAPSHOW][5].equals("C"))
	{
	    long beliefStatesTime = System.currentTimeMillis();
	    ArrayList<PointF> data = new ArrayList<PointF>();
	    int count = 0;
	    // System.out.println(AllParticles.size());
	    for (int i = 0; i < AllParticles.size(); i++)
	    {
		y = (float) AllParticles.get(i).x;
		x = (float) AllParticles.get(i).y;
		// System.out.println(AllParticles.get(i).x+" "+AllParticles.get(i).y);

		A = new float[3][1];
		A[0][0] = x;
		A[1][0] = y;
		A[2][0] = 1.0f;
		C = MatrixBuddy.multiplyMatrices(B, A);// transform to different coordinates
		boolean foundIt = false;
		int a = 0;
		try
		{
		    a = H.RoomsMap[(int) (C[1][0] / (double) H.res)][(int) (C[0][0] / (double) H.res)];// checks to see which state
		}
		catch (Exception e)
		{
		    continue;
		}
		for (int j = 0; j < data.size(); j++)
		{// adds data to arraylist or increments its count
		    if (data.get(j).x == (float) a && a != -1)
		    {
			data.get(j).y++;
			foundIt = true;
			break;
		    }
		}

		if (!foundIt)
		{
		    data.add(new PointF((float) a, 1.0f));
		}
		if (a != -1)
		{
		    count++;
		}

	    }

	    newBeliefState = new BeliefState(H.rooms.size());
	    // System.out.println("------------------");
	    for (int i = 0; i < data.size(); i++)
	    {
		data.get(i).y /= (float) count;
		if ((int) data.get(i).x != -1)
		{
		    double prob = data.get(i).y;
		    try
		    {
			Room currentRoom = H.getRoomById((int) data.get(i).x);
			UserPlanNode tempUserNode = new UserPlanNode(recognizer.stateHash.get(currentRoom.getLabel()), prob);
			newBeliefState.put(tempUserNode, prob);

		    }
		    catch (Exception e)
		    {
			e.printStackTrace();
		    }

		}

	    }
	    try
	    {
		if (newBeliefState != null)
		{
		    recognizer.setBeliefState(newBeliefState);
		    IOLog += "Belief States Processed in " + (System.currentTimeMillis() - beliefStatesTime) + " ms\n";
		}
	    }
	    catch (Exception e)
	    {
	    }

	}

	// TODO verify that the radius of the circle is projected/transformed on the map
	// TODO fix this section. It always replans, it would be faster not to.

	// Decides whether to recalculate the path
	boolean recalculate = true;
	r = 2.0f * r / (float) H.res;// uses twice the radius of the error
	// System.out.println(r);

	if (solution.establishPath(Neville.getPairedNode(), dest.getPairedNode()))
	{
	    // if the first 10 nodes of the path are not close to the user, the path needs recalculated
	    for (int i = 0; i < Math.min(solution.nodesInPath(), 10); i++)
	    {
		if (mag(solution.getPath().get(i), Neville.getPairedNode()) < r)
		{// the user has left the error bubble
		    if (H.line_draw(Neville.getPairedNode().x, Neville.getPairedNode().y, solution.getPath().get(i).x, solution.getPath().get(i).y, null, 1))
		    {
			// a straight line can be drawn between the user and the path without crossing a boundary
			// If the user has a clear sight of the path, it may not be necessary to recalculate.
			recalculate = false;
			// System.out.println("Don't recalculate");
			break;
		    }
		}
	    }

	}
	if (recalculate || requestToReplan)
	{
	    if (requestToReplan)
		requestToReplan = false;
	    // resets lists and grid
	    H.cleanUpOpen();

	    // regenerates path
	    if (DStar(Neville.getPairedNode(), dest.getPairedNode()) == -1)
	    {
		Toast.makeText(this, "No direct path to specified destination.", Toast.LENGTH_SHORT).show();
	    }
	    if (useRouteGuidance)// if this is selected, the user will be centered on the view at every update
		dispView.centerOnUser(Neville);

	}
	dispView.postInvalidate();

    }

    @Override
    protected void onStart()
    {

	super.onStart();

    }

    /** This is basically a plea for the garbage collecter to free some memory */
    @Override
    protected void onDestroy()
    {
	NSH = null;
	H = null;
	dispView = null;
	newBeliefState = null;
	load = null;
	updater = null;
	rp = null;
	filter = null;
	vibrate = null;
	mBoundService = null;
	messageReceiver = null;
	System.gc();
	doUnbindService();
	super.onDestroy();

    }

    @Override
    protected void onStop()
    {
	super.onStop();
	// setContentView(R.layout.splash);
    }

    public void onClick(View v)
    {
	// destText.setText("You Clicked!");
    }

    /** Recieves data from Localization */
    public class newMessage extends BroadcastReceiver
    {
	@Override
	public void onReceive(Context context, Intent intent)
	{
	    String action = intent.getAction();
	    if (action.equalsIgnoreCase(LocalizationService.MESSAGE))
	    {
		Bundle extra = intent.getExtras();
		Particle2D center = extra.getParcelable("center");
		errorBubble = extra.getFloat("radius") * -1 * (MapList.mTrans[CURRENTMAPSHOW][0][0]);
		ArrayList<Particle2D> AllParticles = extra.getParcelableArrayList("points");

		// Log.i("serviceTester", "Received Broadcast");
		updateUserPosition(center.x, center.y, errorBubble, AllParticles);

		// System.out.println("Recieved Broadcast: X:" + center.x + " Y:" + center.y + " " + errorBubble);
	    }
	}
    }

    @Override
    protected void onPause()
    {
	unregisterReceiver(messageReceiver);
	super.onPause();
    }

    @Override
    protected void onResume()
    {
	registerReceiver(messageReceiver, new IntentFilter(LocalizationService.MESSAGE));
	this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

	super.onResume();
    }

    private ServiceConnection mConnection = new ServiceConnection()
    {
	public void onServiceConnected(ComponentName className, IBinder service)
	{
	    // This is called when the connection with the service has been
	    // established, giving us the service object we can use to
	    // interact with the service. Because we have bound to a explicit
	    // service that we know is running in our own process, we can
	    // cast its IBinder to a concrete class and directly access it.
	    mBoundService = ((edu.cmu.ri.rcommerce.LocalizationService.LocalBinder) service).getService();

	    // Tell the user about this for our demo.
	    // no
	}

	public void onServiceDisconnected(ComponentName className)
	{
	    // This is called when the connection with the service has been
	    // unexpectedly disconnected -- that is, its process crashed.
	    // Because it is running in our same process, we should never
	    // see this happen.
	    mBoundService = null;
	}
    };

    void doBindService()
    {
	// Establish a connection with the service. We use an explicit
	// class name because we want a specific service implementation that
	// we know will be running in our own process (and thus won't be
	// supporting component replacement by other applications).
	bindService(new Intent(MainInterface.this, edu.cmu.ri.rcommerce.LocalizationService.class), mConnection, Context.BIND_AUTO_CREATE);
	mIsBound = true;
    }

    void doStartService()
    {
	// start the service

	File root = Environment.getExternalStorageDirectory();
	if (root.canWrite())
	{
	    File outFile = new File(new File(root, "NavPalSaves/"), "mostRecentLog.txt");

	    if (!outFile.exists())
	    {
		try
		{
		    outFile.createNewFile();
		}
		catch (IOException e3)
		{
		    e3.printStackTrace();
		}
	    }

	    try
	    {
		logWriter = new FileWriter(outFile);
		log = new BufferedWriter(logWriter);
	    }
	    catch (IOException e)
	    {
		e.printStackTrace();
	    }

	}

	startService(new Intent(MainInterface.this, edu.cmu.ri.rcommerce.LocalizationService.class));
    }

    void doUnbindService()
    {
	if (mIsBound)
	{
	    // Detach our existing connection.
	    unbindService(mConnection);
	    mIsBound = false;
	}
	try
	{
	    log.close();
	}
	catch (IOException e)
	{

	    e.printStackTrace();
	}
	catch (NullPointerException es)
	{

	    es.printStackTrace();
	}
	stopService(new Intent(MainInterface.this, edu.cmu.ri.rcommerce.LocalizationService.class));
    }

    private class runLoadDataTask extends AsyncTask<Void, String, Void>
    {

	@Override
	protected void onPostExecute(Void result)
	{
	    // disp.setText("Data Loaded and Ready for PF Localization!");
	    SERVICE_IS_BINDED = true;
	    mBoundService.setParticleFilter(filter);
	    super.onPostExecute(result);
	}

	@Override
	protected void onProgressUpdate(String... values)
	{
	    // disp.setText(values[0]);
	    super.onProgressUpdate(values);
	}

	@Override
	protected Void doInBackground(Void... params)
	{
	    ObstacleMap map = loadMap();
	    publishProgress("Passing Map to PF...");
	    setupPF(map);
	    return null;
	}

	protected ObstacleMap loadMap()
	{
	    publishProgress("Loading Obstacle Map...");
	    ObstacleMap map = null;
	    try
	    {
		map = ObstacleMap.loadFromStream(new FileReader(obstacleMap));
	    }
	    catch (FileNotFoundException e1)
	    {
		e1.printStackTrace();
	    }
	    catch (IOException e1)
	    {
		e1.printStackTrace();
	    }
	    publishProgress("Map Loaded.");
	    return map;
	}

	protected void setupPF(ObstacleMap map)
	{
	    filter = null;
	    try
	    {
		filter = setupFilter(correlatedData, map);
	    }
	    catch (IOException e1)
	    {
		e1.printStackTrace();
	    }
	}

	ParticleFilter<Particle2D> setupFilter(String correlatedData, ObstacleMap map) throws IOException
	{
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

    private void initializeLogIO()
    {
	if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) == false)
	{
	    Toast.makeText(context, "SD CARD NOT DOES NOT HAVE READ/WRITE ACCESS!", Toast.LENGTH_LONG).show();
	}
	else
	{
	    File externalRoot = Environment.getExternalStorageDirectory();
	    logDir = new File(externalRoot, "logs/");
	    correlatedData = logDir.getAbsolutePath() + "/" + "NSH1-Run6.txt";
	    obstacleMap = logDir.getAbsolutePath() + "/" + "NSH1-Run6.map";
	    output = "pfout.pf";
	    offlineLogName = "offlineLog.log";

	    File outFile = new File(logDir, output);
	    if (!outFile.exists())
		try
		{
		    outFile.createNewFile();
		}
		catch (IOException e3)
		{
		    e3.printStackTrace();
		}
	    try
	    {
		out = new BufferedWriter(new FileWriter(outFile));
	    }
	    catch (IOException e2)
	    {
		e2.printStackTrace();
	    }

	    File outFileOffline = new File(logDir, offlineLogName);
	    if (!outFileOffline.exists())
		try
		{
		    outFileOffline.createNewFile();
		}
		catch (IOException e3)
		{
		    e3.printStackTrace();
		}
	    try
	    {
		logOutputStream = new FileOutputStream(outFileOffline);
	    }
	    catch (IOException e2)
	    {
		e2.printStackTrace();
	    }
	}
    }

    @Override
    public boolean onTouch(View arg0, MotionEvent arg1)
    {
	// TODO Auto-generated method stub
	return false;
    }
}
