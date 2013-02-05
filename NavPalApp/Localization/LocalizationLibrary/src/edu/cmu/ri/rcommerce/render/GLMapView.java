package edu.cmu.ri.rcommerce.render;

import static edu.cmu.ri.rcommerce.Annotation.FILL_ANNOTATION_REQUEST;

import java.net.SocketException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PointF;
import android.opengl.GLSurfaceView;
import android.os.RemoteException;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.widget.Toast;
import edu.cmu.ri.rcommerce.AnnotationThread;
import edu.cmu.ri.rcommerce.LocalizationIPC;
/**
 * View that provides a scrollable, dynamic map.
 * @author Nisarg
 *
 */
public class GLMapView extends GLSurfaceView {
	
	private GestureDetector gestureDetector;
	private GLRenderer renderer;
	
	private boolean settingLocation = false;
	private boolean scrollEnabled = false;
	private LocalizationIPC locationListener;

	private int touchEventCounter; //used to discard touch events for speed
	private AnnotationThread AnnotationThread;
	
	public GLMapView(Context context) {
		super(context);
		initialize();
	}
	
	public GLMapView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initialize();
	}
	
	private void initialize()
	{
		this.gestureDetector = new GestureDetector(getContext(),new OnGestureListener() {
			
			@Override
			public boolean onSingleTapUp(MotionEvent e) {
				//Log.d("gesture", "single tap up");
				return false;
			}
			
			@Override
			public void onShowPress(MotionEvent e) {
				//Log.d("gesture", "show press");
				
			}
			
			@Override
			public boolean onScroll(MotionEvent e1, MotionEvent e2, final float distanceX, final float distanceY) {
				if (scrollEnabled)
				{
					  queueEvent(new Runnable(){
				            @Override
							public void run() {
				            	renderer.scrollMapBy((int)distanceX, (int)-distanceY);
				            }});
					  return true;
				}
				return false;
			}
			
			@Override
			public void onLongPress(MotionEvent e) {
				PointF mapCoords = new PointF();
				if (!settingLocation)
				{
					Intent i = new Intent();
					renderer.viewCoordinatesToMapCoordinates(mapCoords,e.getX(), e.getY());
	      	    	Log.d("Annotation", "launched annotation activity due to long press at position " + mapCoords.x + "," + mapCoords.y);
	      	    	i.setClassName("edu.cmu.ri.rcommerce", "edu.cmu.ri.rcommerce.AnnotationActivity");
	      	    	i.putExtra("locX", mapCoords.x);
	      	    	i.putExtra("locY", mapCoords.y);
	      	    	//Send new task type to taskAllocator
	      	    	try {
	      				AnnotationThread = new AnnotationThread(getContext());
	      				AnnotationThread.addTask(mapCoords.x, mapCoords.y);
	      			} catch (SocketException e1) {
	      				Toast.makeText(getContext(), "unable to add annotation task", Toast.LENGTH_SHORT).show();
	      			}
	      	    	((Activity) getContext()).startActivityForResult(i,FILL_ANNOTATION_REQUEST);
				}
				else if (locationListener != null)
				{
					renderer.viewCoordinatesToMapCoordinates(mapCoords,e.getX(), e.getY());
					try {
						locationListener.setLocation(mapCoords.x, mapCoords.y);
					} catch (RemoteException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					settingLocation = false;
					Toast.makeText(getContext(), "new location set", 1000).show();
				}
			}
			
			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
				return true;
			}
			
			@Override
			public boolean onDown(MotionEvent e) {
				return true;
			}
		});
		//gestureDetector.setIsLongpressEnabled(false);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		boolean ret = gestureDetector.onTouchEvent(event);
		/*try {
			Thread.sleep(20);
		} catch (InterruptedException e) {
		}*/
		return ret;
	}
	
	//The next down click on the map area will set the location of the user to the selected area and notify the selected localization service
	public void settingLocation(LocalizationIPC callback) {
		settingLocation = true;
		locationListener = callback;
		
	}
	
	public void setScrollEnabled(boolean scroll)
	{
		scrollEnabled = scroll;
	}
	
	@Override
	public void setRenderer(Renderer renderer) {
		this.renderer = (GLRenderer)renderer;
		super.setRenderer(renderer);
	}

}
