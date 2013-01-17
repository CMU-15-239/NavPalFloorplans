/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 *@author Chet
 */
package edu.cmu;

import java.util.LinkedList;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.FloatMath;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.Toast;
import edu.cmu.recognizer.PlanRecognizer;

/**
 * 
 * @author Chet
 */
public class DisplayView extends ImageView implements OnDoubleTapListener
{
    // things that must be read in
    int res;
    String mapType;
    Map VisibleMap;
    MainInterface MI;
    Display display;
    PlanRecognizer recognizer;
    Canvas globalCanvas;
    // Variables to enable scrolling
    // for adding obstacles
    private boolean addingAnnotation = false, sizingAnnotation = false,
	    hasSized = false;
    String typeAnnotation;
    int sizeAnnotation;
    Obstacle tempObs = new Obstacle(0, 0, 0, "", -1, VisibleMap);
    boolean debug = false; // Was originally false;
    private boolean placingDest;

    // We can be in one of these 3 states
    static final int NONE = 0;
    static final int DRAG = 1;
    static final int ZOOM = 2;
    int mode = NONE;

    // Remember some things for zooming
    PointF start = new PointF();
    PointF mid = new PointF();
    float oldDist = 1f;

    Matrix matrix = new Matrix();
    Matrix savedMatrix = new Matrix();
    GestureDetector gestureDetector;

    int iii = 0;
    boolean keydown = false;

    /**
     * This constructor sets up the gesture detector and the image matrix. Not a lot is done. The MainInterface object is passed for convenience. Things like Toast are now possible.
     */
    public DisplayView(MainInterface context)
    {
	super(context);
	gestureDetector = new GestureDetector(context, new GestureListener());
	MI = context;
	display = MI.getWindowManager().getDefaultDisplay();
	this.setScaleType(ImageView.ScaleType.MATRIX);
	this.setImageMatrix(matrix);
	MI.setContentView(this);
    }

    /** This is used so that menu items that were clicked are not carried on when switching views or something like that. */
    public void disableActions()
    {
	addingAnnotation = false;
	sizingAnnotation = false;
	placingDest = false;
    }

    /* Double clicks will now register as place destination */
    public void placeDestination()
    {
	MI.setTitle("Double Click to place destination.");
	placingDest = true;
    }

    /** Sends all necessary pointers to the display view. The matrix can be scaled initially here if desired. */
    public boolean initializeDisplay(int C, Map A, int SCALE, int RESOLUTION, String KINDOFMAP, PlanRecognizer r)
    {
	try
	{
	    this.setImageResource(C);
	    recognizer = r;
	    mapType = KINDOFMAP;
	    res = RESOLUTION;
	    VisibleMap = A;
	    // matrix.postScale(SCALE/(float)res, SCALE/(float)res, 0,0);
	    this.setImageMatrix(matrix);
	    return true;
	}
	catch (NullPointerException ne)
	{
	    ne.printStackTrace();
	    return false;
	}
    }

    /**
     * This is no longer used because the clicking interface was changed. This took into account the menu bar at the top of the screen (offsets Y only) when transforming from android raw coordinates to grid representation coordinates.
     */
    public float correctOffset(float a)
    {
	return a - 24;
    }

    // @Override
    // public boolean onKeyDown(int keyCode, KeyEvent event)
    // {
    // Log.d(Constants.KEY, "Key Down!");
    // keydown = true;
    //
    // return true;
    // }
    //
    // @Override
    // public boolean onKeyUp (int keyCode, KeyEvent event)
    // {
    //
    // System.out.println("Key Up!");
    // Log.d(Constants.KEY, "Key Up!");
    // keydown = false;
    //
    // return true;
    // }

    /**
     * Handles the user touching the screen. Simple movements like finger up and down are here, so are drags and zooms. Double clicks are elsewhere.
     */
    @Override
    public boolean onTouchEvent(MotionEvent Mevent)
    {
	// Context context = MI.getApplicationContext();
	// int duration = Toast.LENGTH_SHORT;
	//
	// if (keydown)
	// {
	// CharSequence text = "Hello toast!" + (++iii);
	// Log.d(Constants.KEY, (String) text);
	// Toast toast = Toast.makeText(context, text, duration);
	// toast.show();
	// }

	WrapMotionEvent event = WrapMotionEvent.wrap(Mevent);

	onTapDebug(Constants.SINGLE_TAP, (int)event.getX(), (int)event.getY());
	
//	Log.d(Constants.SINGLE_TAP, "-------------------------------");
//	Log.d(Constants.SINGLE_TAP, "onTouchEvent Raw x-y Coords (" + event.getX() + ", " + event.getY() + ")");
//	float cCoord[][] = MatrixBuddy.transform(event.getX(), event.getY(), matrix, true);
//	int tmpX = (int) cCoord[0][0] / res;
//	int tmpY = (int) cCoord[1][0] / res;
//	Log.d(Constants.SINGLE_TAP, "onTouchEvent Trans x-y Coords (" + tmpX + ", " + tmpY + ")");
//	Log.d(Constants.DSTAR_TAG, "Dimenions of RoomsMap (x:" + MI.H.RoomsMap.length + ", y:" + MI.H.RoomsMap[0].length + ")" );
//	Log.d(Constants.SINGLE_TAP, "Clicked inside of room " + MI.H.RoomsMap[tmpX][tmpY] + " at location (" + tmpX + "," + tmpY + ")");

	switch ((event.getAction() & MotionEvent.ACTION_MASK))
	{
	    case MotionEvent.ACTION_DOWN: // Mouse button was pressed
		// Log.d(Constants.SINGLE_TAP, "DisplayView::onTouchEvent ACTION_DOWN (" + event.getX() + ", " + event.getY() + ")");

		float[] floatMatrix = new float[9];
		matrix.getValues(floatMatrix);
		
		// Added by Gary for debugging purposes
		//Constants.dump1DFloatArrayToFile("matrixDisplayView_onTouchEvent_ACTION_DOWN", floatMatrix);

		savedMatrix.set(matrix);
		start.set(event.getX(), event.getY());
		if (addingAnnotation)
		{// records the coordinates of the obstacle being laid
		    /*
		     * To convert from clicking coordinates to grid coordinates, multiply by the transform matrix and integer divide by the resolution of the file.
		     */
		    float clickCoord[][] = MatrixBuddy.transform(start.x, start.y, matrix, true);
		    tempObs.x = (int) clickCoord[0][0] / res;
		    tempObs.y = (int) clickCoord[1][0] / res;
		    tempObs.size = 1;
		}

		mode = DRAG;
		break;

	    case MotionEvent.ACTION_POINTER_DOWN:
		// Log.d(Constants.SINGLE_TAP, "DisplayView::onTouchEvent ACTION_POINTER_DOWN (" + event.getX() + ", " + event.getY() + ")");

		oldDist = spacing(event);
		if (oldDist > 10f)
		{// only significant movements are recorded
		    savedMatrix.set(matrix);
		    midPoint(mid, event);
		    mode = ZOOM;
		}
		break;

	    case MotionEvent.ACTION_UP:
		// Log.d(Constants.SINGLE_TAP, "DisplayView::onTouchEvent ACTION_UP (" + event.getX() + ", " + event.getY() + ")");

		if (hasSized)
		{// user has sized object and lifted finger. Annotation is created
		    hasSized = false;
		    sizingAnnotation = false;
		    String s[] =
		    { "DD", "CC" };
		    Intent newIntent = new Intent(MI.getApplicationContext(), NotesMenu.class);
		    newIntent.putExtra("shortMes", s[0]);
		    newIntent.putExtra("longMes", s[1]);

		    MI.startActivityForResult(newIntent, 0);
		}

		break;

	    case MotionEvent.ACTION_POINTER_UP:
		// Log.d(Constants.SINGLE_TAP, "DisplayView::onTouchEvent ACTION_POINTER_UP (" + event.getX() + ", " + event.getY() + ")");

		savedMatrix.set(matrix);
		mode = NONE;
		break;

	    case MotionEvent.ACTION_MOVE:
		// Log.d(Constants.SINGLE_TAP, "DisplayView::onTouchEvent ACTION_MOVE (" + event.getX() + ", " + event.getY() + ")");

		// only significant motions are considered
		if (Math.sqrt(Math.pow(event.getX() - start.x, 2) + Math.pow(correctOffset(event.getY()) - start.y, 2)) > 10.0)
		{
		    matrix.set(savedMatrix);

		    float val[] = new float[9];
		    if (mode == DRAG)
		    {
			tempObs.size = 0;
			float x = event.getX();
			float y = event.getY();
			if (sizingAnnotation)
			{// user did not cancel annotation and is picking the size of it
			    Matrix I = new Matrix();

			    I.set(matrix);
			    I.invert(I);
			    sizeAnnotation = (int) (float) Math.sqrt(Math.pow(I.mapRadius(x - start.x), 2) + Math.pow(I.mapRadius(y - start.y), 2)) / res / res;
			    /*
			     * Obstacles are currently scaled improperly. I am not sure where the problem is yet. This can be verified with Map.print(0). It only seems to consider the lower right quadrant of the obstacle and they are far
			     * larger than they should be.
			     */

			    hasSized = true;
			    System.out.println("Sizing" + sizeAnnotation);
			    tempObs.size = sizeAnnotation;

			} // Update how much the touch moved
			else
			{

			    // shifts the screen
			    matrix.postTranslate(event.getX() - start.x, event.getY() - start.y);
			    matrix.getValues(val);

			}

		    }
		    else
			if (mode == ZOOM)
			{
			    float newDist = spacing(event);
			    if (newDist > 10f)
			    {// zooms the screen around the midpoint of user's fingers.
				matrix.set(savedMatrix);
				matrix.getValues(val);
				float zoom = .35f * (newDist / oldDist - 1) + 1;
				if ((val[0] * zoom <= 3f) && (val[0] * zoom >= 0.5f))
				{
				    // Don't zoom too much or too little.
				    matrix.postScale(zoom, zoom, mid.x, mid.y);

				}
				else
				    if (val[0] * zoom > 3f)
				    {
					zoom = 3f / val[0];
					// don't zoom out farther when at max zoom
					matrix.postScale(zoom, zoom, mid.x, mid.y);
				    }
				    else
					if (val[0] * zoom < .5f)
					{
					    zoom = .5f / val[0];
					    // don't zoom in farther when at min zoom
					    matrix.postScale(zoom, zoom, mid.x, mid.y);
					}
				matrix.getValues(val);

			    }
			}
		    if ((mode == ZOOM || mode == DRAG) && !sizingAnnotation)
		    {

			val[4] = val[0];// scaled same on both axes

			// makes sure user doesn't scale beyond the size of the map.
			float maxheight = -1 * VisibleMap.getSizeX() * res * val[0] + .92f * display.getHeight();
			// .92 seems to center better. Possibly because of the menu bar making the workable screen smaller
			float maxwidth = -1 * VisibleMap.getSizeY() * res * val[0] + display.getWidth();

			if (val[2] < maxwidth)
			{
			    val[2] = maxwidth;
			}
			if (val[5] < maxheight)
			{
			    val[5] = maxheight;
			}
			if (val[2] > 0)
			{
			    val[2] = 0;
			}
			if (val[5] > 0)
			{
			    val[5] = 0;
			}

			matrix.setValues(val);

		    }
		    invalidate();
		}
		break;

	}
	this.setImageMatrix(matrix);

	// Consume event
	return gestureDetector.onTouchEvent(Mevent);
    }

    /** Determine the space between the first two fingers */
    private float spacing(WrapMotionEvent event)
    {
	// ...
	float x = event.getX(0) - event.getX(1);
	float y = event.getY(0) - event.getY(1);
	return FloatMath.sqrt(x * x + y * y);
    }

    /** Calculate the mid point of the first two fingers */
    private void midPoint(PointF point, WrapMotionEvent event)
    {
	// ...
	float x = event.getX(0) + event.getX(1);
	float y = event.getY(0) + event.getY(1);
	point.set(x / 2, y / 2);
    }

    // GARY DEBUG
    
    private void displayMatrix(String tag, float[] matrix)
    {
	String s = "";
	for(int i=0; i<matrix.length; i++)
	{
	    s += String.format("%5.2f ", matrix[i]);
	}

	Log.d(tag, "T Matrix " + s);
    }

    private void onTapDebug(String tag, int rawX, int rawY)
    {
	Log.d(tag, "-------------------------------");
	Log.d(tag, "onTouchEvent Raw x-y Coords (" + rawX + ", " + rawY + ")");
	float cCoord[][] = MatrixBuddy.transform(rawX, rawY, matrix, true);
	int tmpY = (int) cCoord[0][0] / res;
	int tmpX = (int) cCoord[1][0] / res;
	Log.d(tag, "onTouchEvent Trans x-y Coords (" + tmpX + ", " + tmpY + ")");
	Log.d(tag, "Clicked inside of room " + MI.H.RoomsMap[tmpX][tmpY] + " at location (" + tmpX + "," + tmpY + ")");
	float[] f = new float[9];
	matrix.getValues(f);
	displayMatrix(tag, f);
    }
    
    // GARY DEBUG
    
    private class GestureListener extends GestureDetector.SimpleOnGestureListener
    {

	@Override
	public boolean onDown(MotionEvent e)
	{
	    return true;
	}

	/** The good old double click. */
	@Override
	public boolean onDoubleTap(MotionEvent Mevent)
	{
	    // Added by Gary: This flag is set if any of the catch blocks are entered, which indicate that
	    // an object was accessed that was null, index out of range etc? Then at the end of this
	    // method, if this flag is set, it will cause the method to exit before the DStar method is
	    // invoked in the MapInterface. This solution is probably not the best solution, but it will
	    // fix the floor plan bug of crashing for now when the user double taps outside of the floor plan.
	    boolean somethingBadHappened = false;

	    WrapMotionEvent event = WrapMotionEvent.wrap(Mevent);
	    float clickCoord[][] = MatrixBuddy.transform(event.getX(), event.getY(), matrix, true);

	    float[] floatMatrix = new float[9];
	    matrix.getValues(floatMatrix);
	    
	    // Added by Gary for debugging purposes
	    //Constants.dump1DFloatArrayToFile("matrix_From_onDoubleTap", floatMatrix);
	    //Constants.dump2DFloatArrayToFile("clickCoord_From_onDoubleTap", clickCoord);

	    int y = (int) (clickCoord[0][0] / res);
	    int x = (int) (clickCoord[1][0] / res);

	    onTapDebug(Constants.DOUBLE_TAP, x, y);
	    ////////
//	    Log.d(Constants.DOUBLE_TAP, "-------------------------------");
//	    Log.d(Constants.DOUBLE_TAP, "onTouchEvent Raw x-y Coords (" + event.getX() + ", " + event.getY() + ")");
//	    float cCoord[][] = MatrixBuddy.transform(event.getX(), event.getY(), matrix, true);
//	    int tmpY = (int) cCoord[0][0] / res;
//	    int tmpX = (int) cCoord[1][0] / res;
//	    Log.d(Constants.DOUBLE_TAP, "onTouchEvent Trans x-y Coords (" + tmpX + ", " + tmpY + ")");
//	    Log.d(Constants.DOUBLE_TAP, "Clicked inside of room " + MI.H.RoomsMap[tmpX][tmpY] + " at location (" + tmpX + "," + tmpY + ")");
	    ////////
	    
	    Log.d(Constants.DSTAR_TAG, "---------------------------------");
	    Log.d(Constants.DSTAR_TAG, "DisplayView::onDoubleTap Raw (" + event.getX() + ", " + event.getY() + ")");
	    Log.d(Constants.DSTAR_TAG, "DisplayView::onDoubleTap Scaled x-y (" + res + ") (" + x + ", " + y + ")");

	    // adding annotations and moving start node
	    if (addingAnnotation)
	    {
		Log.d(Constants.DSTAR_TAG, "addingAnnotation True");
		if (typeAnnotation.equals("Obstacle"))
		{
		    Log.d(Constants.DSTAR_TAG, "Annotation is an Obstacle");
		    MI.setTitle("Drag to size or double click to cancel.");
		    sizingAnnotation = true;
		}
		else
		{
		    Log.d(Constants.DSTAR_TAG, "Annotation is NOT an Obstacle");
		    if (typeAnnotation.equals("Landmark"))
		    {
			Log.d(Constants.DSTAR_TAG, "Annotation is a Landmark");
			String s[] =
			{ "[no value]", "[no value]" };
			Intent newIntent = new Intent(MI.getApplicationContext(), NotesMenu.class);
			newIntent.putExtra("shortMes", s[0]);
			newIntent.putExtra("longMes", s[1]);

			MI.startActivityForResult(newIntent, 0);
		    }
		}
	    }
	    else
	    {
		Log.d(Constants.DSTAR_TAG, "addingAnnotation False");
		if (sizingAnnotation)
		{
		    Log.d(Constants.DSTAR_TAG, "sizingAnnotation is True");
		    // changing the size of the annotation
		    MI.setTitle(R.string.app_name);
		    tempObs.size = 0;
		    sizingAnnotation = false;
		}
		else
		{
		    Log.d(Constants.DSTAR_TAG, "sizingAnnotation is False");
		    if (placingDest && VisibleMap.node[x][y].set != 'X' && VisibleMap.node[x][y].set != 'x')
		    {
			Log.d(Constants.DSTAR_TAG, "placingDest true and node at location (" + x + ", " + y + ") is not an 'X' or 'x'");
			// placing a destination

			MI.setTitle(R.string.app_name);
			placingDest = false;

			VisibleMap.time = 1;
			MI.dest.setPairedNode(VisibleMap.node[x][y]);
			System.out.println("* * * * * * * * * * * * * * * * * * * *");
			System.out.println("Neville: " + MI.Neville.getPairedNode());
			System.out.println("Dest: " + MI.dest.getPairedNode());

			try
			{
			    Log.d(Constants.DSTAR_TAG, "N:" + MI.Neville.isOnMap().getState(MI.Neville));
			    System.out.println("N:" + MI.Neville.isOnMap().getState(MI.Neville));
			}
			catch (Exception e)
			{
			    somethingBadHappened = true;
			    Log.d(Constants.DSTAR_TAG, "Exception: Something happened in accessing MI.Neville.isOnMap().getState(MI.Neville)");
			}

			try
			{
			    Log.d(Constants.DSTAR_TAG, "D:" + MI.dest.isOnMap().getState(MI.dest));
			    System.out.println("D:" + MI.dest.isOnMap().getState(MI.dest));
			}
			catch (Exception e)
			{
			    somethingBadHappened = true;
			    Log.d(Constants.DSTAR_TAG, "Exception: Something happened in accessing MI.dest.isOnMap().getState(MI.dest)");
			}
			try
			{
			    Log.d(Constants.DSTAR_TAG, "Reinitializing visible map.");
			    VisibleMap.reinitialize();
			}
			catch (Exception e)
			{
			    somethingBadHappened = true;
			    Log.d(Constants.DSTAR_TAG, "Something happened while reinitializing visible map.");
			}
		    }
		    else
		    {
			Log.d(Constants.DSTAR_TAG, "placingDest false or node at location (" + x + ", " + y + ") is not an 'X' or 'x'");
			if (MI.Neville.getPairedNode() == null && VisibleMap.node[x][y].set != 'X' && VisibleMap.node[x][y].set != 'x')
			{
			    Log.d(Constants.DSTAR_TAG, "MI.Neville.getPairedNode() is null and visible map node at (" + x + ", " + y + ") is not an 'X' or 'x'");
			    // placing the user for the first time.
			    if (!MI.useLocalization)
			    {
				Log.d(Constants.DSTAR_TAG, "not MI.useLocalization");
				MI.Neville.setPairedNode(VisibleMap.node[x][y]);
				if (VisibleMap.node[x][y].set == '1')
				{
				    Log.d(Constants.DSTAR_TAG, "VisibleMap.node[" + x + "][" + y + "].set == '1'");
				    VisibleMap.OPEN.remove(VisibleMap.node[x][y]);
				}
				else
				{
				    Log.d(Constants.DSTAR_TAG, "VisibleMap.node[" + x + "][" + y + "].set does not equal '1'");
				    if (VisibleMap.node[x][y].set == '2')
				    {
					Log.d(Constants.DSTAR_TAG, "VisibleMap.node[" + x + "][" + y + "].set == '2'");
					VisibleMap.CLOSED.remove(VisibleMap.node[x][y]);
				    }
				}

				VisibleMap.node[x][y].set = '0';
				MI.Neville.getPairedNode().g = 0;

				try
				{
				    System.out.println("* * * * * * * * * * * * * * * * * * * *");
				    System.out.println("Neville: " + MI.Neville.getPairedNode());
				    System.out.println("Dest: " + MI.dest.getPairedNode());
				    Log.d(Constants.DSTAR_TAG, "N:" + MI.Neville.isOnMap().getState(MI.Neville));
				    System.out.println("N:" + MI.Neville.isOnMap().getState(MI.Neville));
				}
				catch (Exception e)
				{
				    somethingBadHappened = true;
				    Log.d(Constants.DSTAR_TAG, "Something happened while accessing N: MI.Neville.isOnMap().getState(MI.Neville)");
				}
				try
				{
				    Log.d(Constants.DSTAR_TAG, "D:" + MI.dest.isOnMap().getState(MI.dest));
				    System.out.println("D:" + MI.dest.isOnMap().getState(MI.dest));
				}
				catch (Exception e)
				{
				    somethingBadHappened = true;
				    Log.d(Constants.DSTAR_TAG, "Something happened while accessing MI.dest.isOnMap().getState(MI.dest)");
				}

			    }
			}
			else
			{
			    Log.d(Constants.DSTAR_TAG, "MI.Neville.getPairedNode() not null or visible map node at (" + x + ", " + y + ") is not an 'X' or 'x'");
			    if (VisibleMap.node[x][y].set != 'X' && VisibleMap.node[x][y].set != 'x')
			    {
				Log.d(Constants.DSTAR_TAG, "VisibleMap.node at location (" + x + "," + y + ") is not 'X' or 'x'");
				// moving user manually. Assumes that replanning is necessary
				if (!MI.useLocalization)
				{
				    Log.d(Constants.DSTAR_TAG, "!MI.useLocalization");
				    MI.Neville.setPairedNode(VisibleMap.node[x][y]);
				    Node user = MI.Neville.getPairedNode();
				    // System.out.println("NODE "+VisibleMap.node[x][y].x+" "+VisibleMap.node[x][y].y);
				    int u = VisibleMap.youngestNeighbor(VisibleMap.node[x][y]);

				    if (u > 0 && u < Integer.MAX_VALUE)
				    {
					Log.d(Constants.DSTAR_TAG, "u > 0 && u < Integer.MAX_VALUE");
					VisibleMap.rollback(u);
					if (user.p != null)
					{
					    Log.d(Constants.DSTAR_TAG, "user.p != null");
					    if (user.p.p == null || user.p.timeToOPEN <= u)
					    {
						Log.d(Constants.DSTAR_TAG, "user.p.p == null || user.p.timeToOPEN <= u");
						user.p = null;
					    }
					}

					VisibleMap.node[x][y].p = null;
				    }
				    VisibleMap.cleanUpOpen();
				    user.timeToOPEN = -1;
				    if (user.set == '1')
				    {
					Log.d(Constants.DSTAR_TAG, "user.set == '1'");
					VisibleMap.OPEN.remove(VisibleMap.node[x][y]);
				    }
				    else
				    {
					Log.d(Constants.DSTAR_TAG, "user.set does not equal '1'");
					if (user.set == '2')
					{
					    Log.d(Constants.DSTAR_TAG, "user.set == '2'");
					    user.set = '0';
					}
				    }
				    user.p = null;

				    user = VisibleMap.node[x][y];
				    if (user.set == '1')
				    {
					Log.d(Constants.DSTAR_TAG, "user.set == '1'");
					VisibleMap.OPEN.remove(VisibleMap.node[x][y]);
				    }

				    if (user.set != '2')
				    {
					Log.d(Constants.DSTAR_TAG, "user.set != '2'");
					VisibleMap.CLOSED.add(VisibleMap.node[x][y]);
				    }

				    user.set = '2';
				    user.timeToOPEN = Integer.MAX_VALUE;

				    try
				    {
					System.out.println("* * * * * * * * * * * * * * * * * * * *");
					System.out.println("Neville: " + MI.Neville.getPairedNode());
					System.out.println("Dest: " + MI.dest.getPairedNode());
					Log.d(Constants.DSTAR_TAG, "N:" + MI.Neville.isOnMap().getState(MI.Neville));
					System.out.println("N:" + MI.Neville.isOnMap().getState(MI.Neville));
				    }
				    catch (Exception e)
				    {
					somethingBadHappened = true;
					Log.d(Constants.DSTAR_TAG, "Something happened while accessing MI.Neville.isOnMap().getState(MI.Neville)");
				    }
				    try
				    {
					Log.d(Constants.DSTAR_TAG, "D:" + MI.dest.isOnMap().getState(MI.dest));
					System.out.println("D:" + MI.dest.isOnMap().getState(MI.dest));
				    }
				    catch (Exception e)
				    {
					somethingBadHappened = true;
					Log.d(Constants.DSTAR_TAG, "Something happened while accessing MI.dest.isOnMap().getState(MI.dest)");
				    }
				}
			    }
			}
		    }
		}
	    }

	    if (somethingBadHappened)
	    {
		// postInvalidate();
		return true;
	    }

	    if (!addingAnnotation)
	    {
		// resets lists and grid
		VisibleMap.cleanUpOpen();

		// regenerates path
		if (MI.DStar(MI.Neville.getPairedNode(), MI.dest.getPairedNode()) == -1)
		{
		    Toast.makeText(MI, "No direct path to specified destination.", Toast.LENGTH_SHORT).show();
		}
	    }
	    addingAnnotation = false;

	    postInvalidate();
	    return true;
	}

    }

    public void enableAnnotation(String type)
    {
	addingAnnotation = true;
	typeAnnotation = type;
    }

    /**
     * Makes objects for annotations that were created. This will occasionally throw an ArrayOutOfBoundsException. I did not have time to investigate why. I think x or y is out of range. If user puts a landmark on the boundary of a room, it
     * may not be added to that room's landmark array. This is a defect of the svg files in which the grid square is just on the border of a room and it says that it is not in a room.
     */
    public void completeAnnotation(String s[])
    {
	if (s[2].equals("Accept"))
	{

	    int x = (int) (tempObs.x);
	    int y = (int) (tempObs.y);
	    if (typeAnnotation.equals("Obstacle"))
	    {
		VisibleMap.annotations.createAnnotation(x, y, sizeAnnotation, s[0], s[1], typeAnnotation, VisibleMap);
		VisibleMap.syncObstacles();
		MI.DStar(MI.Neville.getPairedNode(), MI.dest.getPairedNode());
		if (VisibleMap.RoomsMap[x][y] != -1)
		    VisibleMap.rooms.get(VisibleMap.RoomsMap[x][y]).ob.add(VisibleMap.annotations.O.get(VisibleMap.annotations.O.size() - 1));

	    }
	    else
		if (typeAnnotation.equals("Landmark"))
		{
		    VisibleMap.annotations.createAnnotation(x, y, 1, s[0], s[1], typeAnnotation, VisibleMap);
		    if (VisibleMap.RoomsMap[x][y] != -1)
			VisibleMap.rooms.get(VisibleMap.RoomsMap[x][y]).lm.add(VisibleMap.annotations.L.get(VisibleMap.annotations.L.size() - 1));

		}
	    // recognizer.setAnnotations(VisibleMap.rooms.get(VisibleMap.RoomsMap[x][y]));
	}

	postInvalidate();

	MI.setTitle(R.string.app_name);
	tempObs.size = 0;

    }

    /*
     * // ____ ____ _ _ _ ____ // | _"\U | _"\ uU /"\ u __ __ ___ | \ |"| U /"___|u // /| | | |\| |_) |/ \/ _ \/ \"\ /"/     |_"_| <| \| |>\| | _ / // U| |_| |\| _ < / ___ \ /\ \ /\ / /\ | | U| |\ |u | |_| | // |____/ u|_| \_\ /_/ \ \ U \ V
     * V / U U/| |\U |_| \_| \ ____| // |||_ // \\_ \\ >>.-,_\ /\ /_,-..-,_|___|_,-.|| \\,-._)(|_ // (__)_) (__) (__)__) (__)\_)-' '-(_/ \_)-' '-(_/ (_") (_/(__)__)
     */
    /** The transformation matrices are reset. */
    public void resetView()
    {
	matrix.set(null);
	savedMatrix.set(null);
    }

    /** Puts camera on good ole Neville */
    public void centerOnUser(User Neville)
    {
	// centerOn(Neville.x,Neville.y);
	centerOn(MI.Neville.getPairedNode().x, MI.Neville.getPairedNode().y);
    }

    /**
     * Centers on given coordinates, takes into account the map size and position and doesn't center exactly if user is near edge.
     */
    public void centerOn(int x, int y)
    {
	float val[] = new float[9];
	matrix.getValues(val);
	val[2] = (-y * res) * val[0] + display.getWidth() / 2;
	val[5] = (-x * res) * val[0] + display.getHeight() / 2;

	float maxheight = -1 * VisibleMap.getSizeX() * res * val[0] + .92f * display.getHeight();
	float maxwidth = -1 * VisibleMap.getSizeY() * res * val[0] + display.getWidth();

	if (val[2] < maxwidth)
	{
	    val[2] = maxwidth;
	}
	if (val[5] < maxheight)
	{
	    val[5] = maxheight;
	}
	if (val[2] > 0)
	{
	    val[2] = 0;
	}
	if (val[5] > 0)
	{
	    val[5] = 0;
	}

	matrix.setValues(val);
	this.setImageMatrix(matrix);

	invalidate();
    }

    // @SuppressWarnings("unused")
    @Override
    protected void onDraw(Canvas g)
    {
	super.onDraw(g);// draws the map itself

	Paint myPaint = new Paint();
	Matrix M = matrix;

	myPaint.setStyle(Paint.Style.FILL);
	myPaint.setColor(Color.RED);
	myPaint.setStrokeWidth(3);
	// Draws the solution path
	try
	{
	    LinkedList<Node> L = MI.solution.getPath();

	    // Check if the map reference from the current solution is the same as the visible map
	    if (L.get(0).M == VisibleMap)
		for (int i = 0; i < L.size() - 1; i++)
		{
		    float arr[] =
		    {L.get(i).y * res, L.get(i).x * res, L.get(i + 1).y * res, L.get(i + 1).x * res };

		    // Constants.dump1DFloatArrayToFile("OnDrawInitialArrMatrix", arr);

		    float a[][] = MatrixBuddy.transform(arr[0], arr[1], M, false);
		    float b[][] = MatrixBuddy.transform(arr[2], arr[3], M, false);

		    // Constants.dump2DFloatArrayToFile("OnDrawMatrixA", a);
		    // Constants.dump2DFloatArrayToFile("OnDrawMatrixB", b);

		    if (VisibleMap.GoalRoom == null || VisibleMap.RoomsMap[L.get(i).x][L.get(i).y] != VisibleMap.GoalRoom.getID())
			g.drawLine(a[0][0], a[1][0], b[0][0], b[1][0], myPaint);
		}
	}
	catch (Exception de)
	{
	    de.printStackTrace();
	}

	// draws the goal room when applicable
	// if (MI.roomIsPredicted && VisibleMap.GoalRoom != null)
	if (VisibleMap.GoalRoom != null) // Changed by Gary to see if GoalRoom is defined so it can be drawn in green.
	{
	    myPaint.setColor(Color.GREEN);

	    for (int j = 0; j < VisibleMap.GoalRoom.vertex.size() - 1; j++)
	    {

		float arr[] =
		{ VisibleMap.GoalRoom.vertex.get(j).x * res, VisibleMap.GoalRoom.vertex.get(j).y * res, VisibleMap.GoalRoom.vertex.get(j + 1).x * res, VisibleMap.GoalRoom.vertex.get(j + 1).y * res };
		M.mapPoints(arr);

		g.drawLine(arr[0], arr[1], arr[2], arr[3], myPaint);
	    }

	    // System.out.println(M.toString());
	    float arr[] =
	    { VisibleMap.GoalRoom.vertex.get(0).x * res, VisibleMap.GoalRoom.vertex.get(0).y * res, VisibleMap.GoalRoom.vertex.get(VisibleMap.GoalRoom.vertex.size() - 1).x * res, VisibleMap.GoalRoom.vertex.get(VisibleMap.GoalRoom.vertex.size() - 1).y * res };
	    M.mapPoints(arr);
	    g.drawLine(arr[0], arr[1], arr[2], arr[3], myPaint);
	    // System.out.println(H.rooms.get(i).vertex.get(0).x);

	}

	// draws all of the room when admin->Show Obstacles is selected
	if (debug)
	{
	    for (int i = 0; i < VisibleMap.rooms.size(); i++)
	    {
		if (VisibleMap.rooms.get(i).isEnroute())
		{
		    myPaint.setColor(Color.BLACK);
		}
		else
		    if (VisibleMap.rooms.get(i).getd() != null && VisibleMap.rooms.get(i).getd().equals("XXY"))
		    {
			myPaint.setColor(Color.RED);
		    }
		    else
			if (VisibleMap.rooms.get(i).getColor().equals("Room"))
			{
			    myPaint.setColor(Color.GREEN);
			}
			else
			{
			    myPaint.setColor(Color.CYAN);
			}

		for (int j = 0; j < VisibleMap.rooms.get(i).vertex.size() - 1; j++)
		{

		    float arr[] =
		    { VisibleMap.rooms.get(i).vertex.get(j).x * res, VisibleMap.rooms.get(i).vertex.get(j).y * res, VisibleMap.rooms.get(i).vertex.get(j + 1).x * res, VisibleMap.rooms.get(i).vertex.get(j + 1).y * res };

		    M.mapPoints(arr);

		    g.drawLine(arr[0], arr[1], arr[2], arr[3], myPaint);
		}

		// System.out.println(M.toString());
		float arr[] =
		{ VisibleMap.rooms.get(i).vertex.get(0).x * res, VisibleMap.rooms.get(i).vertex.get(0).y * res, VisibleMap.rooms.get(i).vertex.get(VisibleMap.rooms.get(i).vertex.size() - 1).x * res, VisibleMap.rooms.get(i).vertex.get(VisibleMap.rooms.get(i).vertex.size() - 1).y * res };
		M.mapPoints(arr);
		g.drawLine(arr[0], arr[1], arr[2], arr[3], myPaint);
		// System.out.println(H.rooms.get(i).vertex.get(0).x);

	    }
	    // draws red dots for walls and blue to show svg defects and out of bounds areas
	    for (int i = 0; i < VisibleMap.getSizeX(); i++)
	    {
		for (int j = 0; j < VisibleMap.getSizeY(); j++)
		{
		    myPaint.setStyle(Paint.Style.FILL);

		    if (VisibleMap.node[i][j].set == 'X')
		    {
			myPaint.setColor(Color.RED);
			float arr[] =
			{ j * res, i * res };
			M.mapPoints(arr);
			g.drawCircle(arr[0], arr[1], 4f, myPaint);
		    }
		    if (VisibleMap.node[i][j].set == '2')
		    {
			myPaint.setColor(Color.GRAY);
			float arr[] =
			{ j * res, i * res };
			M.mapPoints(arr);
			g.drawCircle(arr[0], arr[1], 3f, myPaint);
		    }
		    if (VisibleMap.node[i][j].set == '1')
		    {
			myPaint.setStyle(Paint.Style.STROKE);
			myPaint.setColor(Color.GRAY);
			float arr[] =
			{ j * res, i * res };
			M.mapPoints(arr);
			g.drawCircle(arr[0], arr[1], 3f, myPaint);
		    }
		    if (VisibleMap.RoomsMap[i][j] == -1)
		    {
			myPaint.setColor(Color.BLUE);
			float arr[] =
			{ j * res, i * res };
			M.mapPoints(arr);
			g.drawCircle(arr[0], arr[1], 1f, myPaint);
		    }

		}
	    }

	}
	// End of (debug) condition

	myPaint.setStrokeWidth(1);
	myPaint.setStyle(Paint.Style.FILL);

	myPaint.setColor(Color.RED);
	// leave this off if not using points Transform.
	if (false)// used in MainInterface.PointTransform only and is slow
	    for (int i = 0; i < 5000; i++)
	    {
		try
		{
		    float arr[] =
		    { px[i].y * res, px[i].x * res };
		    M.mapPoints(arr);
		    g.drawCircle(arr[0], arr[1], 1, myPaint);
		}
		catch (Exception de)
		{
		}
	    }

	// Draws the user
	myPaint.setColor(Color.BLUE);
	if ((MI.Neville.x > 0 || MI.Neville.getPairedNode() != null) && MI.Neville.isOnMap() == VisibleMap)
	{
	    float arr[] =
	    { MI.Neville.getPairedNode().y * res, MI.Neville.getPairedNode().x * res };
	    M.mapPoints(arr);
	    g.drawCircle(arr[0], arr[1], 4, myPaint);
	    myPaint.setStyle(Paint.Style.STROKE);
	    g.drawCircle(arr[0], arr[1], M.mapRadius(MainInterface.errorBubble), myPaint);
	}

	// draws the destination
	if (MI.dest.getPairedNode() != null && MI.dest.isOnMap() == VisibleMap)
	{
	    myPaint.setColor(Color.GREEN);

	    if (!MI.roomIsPredicted)
	    {
		myPaint.setStyle(Paint.Style.FILL);
		float arr[] =
		{ MI.dest.getPairedNode().y * res, MI.dest.getPairedNode().x * res };
		M.mapPoints(arr);
		g.drawCircle(arr[0], arr[1], 4, myPaint);

	    }

	}

	myPaint.setStyle(Paint.Style.STROKE);
	myPaint.setStrokeWidth(3);
	// draws the obstacle while it's being created
	if (tempObs.size != 0)
	{
	    myPaint.setColor(Color.argb(255, 120, 120, 0));
	    float arr[] =
	    { tempObs.x * res, tempObs.y * res };
	    M.mapPoints(arr);
	    g.drawCircle(arr[0], arr[1], Math.abs(M.mapRadius(tempObs.size)), myPaint);

	}

	myPaint.setStyle(Paint.Style.FILL);
	myPaint.setStrokeWidth(1);
	// Draw Annotations
	/*
	 * for(Region R : H.roomRegions){ R.translate((int)(shiftx), (int)(shifty)); g.drawPath(R.getBoundaryPath(), myPaint); R.translate(-1*(int)(shiftx) ,-1*(int)(shifty)); }
	 */

	int fontColor;
	if (mapType.equals("Floorplan"))
	{
	    fontColor = Color.MAGENTA;
	}
	else
	{
	    fontColor = Color.GRAY;
	}
	int textSize = (int) M.mapRadius(18.0f);
	// draws obstacles
	for (int i = 0; i < VisibleMap.annotations.O.size(); i++)
	{
	    float a = (VisibleMap.annotations.O.get(i).y) * res;
	    float b = (VisibleMap.annotations.O.get(i).x) * res;
	    float arr[][] = MatrixBuddy.transform(a, b, M, false);
	    myPaint.setColor(Color.BLACK);
	    g.drawCircle(arr[0][0], arr[1][0], M.mapRadius(VisibleMap.annotations.O.get(i).size), myPaint);
	    myPaint.setColor(Color.argb(255, 200, 200, 240));
	    g.drawCircle(arr[0][0], arr[1][0], M.mapRadius(VisibleMap.annotations.O.get(i).size * .8f), myPaint);

	    myPaint.setColor(fontColor);
	    myPaint.setTextSize(textSize);
	    if (textSize > 10)
	    {
		myPaint.setColor(Color.argb(255, 120, 0, 200));

		g.drawText(VisibleMap.annotations.O.get(i).shortMes, arr[0][0], arr[1][0], myPaint);
	    }

	}
	// draws landmarks.
	for (int i = 0; i < VisibleMap.annotations.L.size(); i++)
	{
	    float a = (VisibleMap.annotations.L.get(i).y) * res;
	    float b = (VisibleMap.annotations.L.get(i).x) * res;
	    float arr[][] = MatrixBuddy.transform(a, b, M, false);
	    myPaint.setColor(Color.BLACK);
	    g.drawCircle(arr[0][0], arr[1][0], M.mapRadius(res / 2), myPaint);

	    myPaint.setColor(Color.YELLOW);
	    g.drawCircle(arr[0][0], arr[1][0], M.mapRadius(res / 2 - 2), myPaint);

	    myPaint.setColor(fontColor);
	    myPaint.setTextSize(textSize);
	    if (textSize > 10)
	    {
		g.drawText(VisibleMap.annotations.L.get(i).shortMes, arr[0][0], arr[1][0], myPaint);
	    }

	}
	// draws exits
	for (int i = 0; i < VisibleMap.annotations.E.size(); i++)
	{
	    float a = (VisibleMap.annotations.E.get(i).y) * res;
	    float b = (VisibleMap.annotations.E.get(i).x) * res;
	    float arr[][] = MatrixBuddy.transform(a, b, M, false);
	    myPaint.setColor(Color.BLACK);
	    g.drawCircle(arr[0][0], arr[1][0], M.mapRadius(res / 2), myPaint);

	    myPaint.setColor(Color.YELLOW);
	    g.drawCircle(arr[0][0], arr[1][0], M.mapRadius(res / 2 - 2), myPaint);

	    myPaint.setColor(Color.RED);
	    myPaint.setTextSize(textSize);
	    if (textSize > 10)
	    {
		g.drawText(VisibleMap.annotations.E.get(i).shortMes, arr[0][0], arr[1][0], myPaint);
	    }

	}
    }

    // End of cnDraw() method

    Point px[] = new Point[5000];

    @Override
    public boolean onDoubleTap(MotionEvent e)
    {
	// TODO Auto-generated method stub
	return false;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e)
    {
	// TODO Auto-generated method stub
	return false;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e)
    {
	// TODO Auto-generated method stub
	return false;
    }

}