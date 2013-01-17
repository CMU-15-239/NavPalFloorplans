package edu.cmu.ri.rcommerce.render;

import static edu.cmu.ri.rcommerce.Annotation.BREADCRUMB_ANNOTATION;
import static edu.cmu.ri.rcommerce.Annotation.CALIBRATION_DATA;
import static edu.cmu.ri.rcommerce.Annotation.GPS_BREADCRUMB_ANNOTATION;
import static edu.cmu.ri.rcommerce.Annotation.GRID_POINT;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.GLU;
import android.util.AttributeSet;
import edu.cmu.ri.rcommerce.Annotation;
import edu.cmu.ri.rcommerce.R;
import edu.cmu.ri.rcommerce.StateServer;

/******************************************************************************
 * @author rcommerce
 *
 * This renderer shows a 4-option menu (landmark, obstacle, point, free look)
 * The initial view shows grid points that are 40 units apart from each other and
 * it shows icon for the current location of the navigator
 * 
 * Choosing point or landmark adds special images to the graph in predefined location
 * Choosing free look allows to move freely over the map and check things
 * 
 * Obstacle: 
 ********************************************************************************************/

public class GLRenderer implements Renderer {
	
	private StateServer data;
	private Canvas canvas;

	private static final int one = 1<<16;
	private IntBuffer vertexBuffer, textureBuffer;

	private Paint textPaint,taskPaint;
	
	private float map_x,map_y,map_width,map_height; //specifies the rectangle in the map that is being displayed on this canvas

	public boolean showingProvisionalTasks = false;
	public boolean showingGridPoints = true;
	
	//has to be one of the textures loaded into openGL
	public int currentLocationDrawable = R.drawable.avatar;

	
	public HashMap<Integer, Integer> resID_to_glID = new HashMap<Integer, Integer>();
	
	private Context context;
	
	//scratchpad point to avoid creating unnecessary objects in the render loop
	private PointF p = new PointF();
	
	int viewWidth, viewHeight;
	
	public GLRenderer(final Context context) {
		this.context = context;
		initialize(context);
		
	}
	
	public GLRenderer(Context context, AttributeSet attrs, int defStyle)
	{
		this.context = context;
		initialize(context);

	}	
	public GLRenderer(Context context, AttributeSet attrs)
	{
		this.context = context;
		initialize(context);

	}
	
	public void initialize(final Context context)
	{	
		 textPaint = new Paint();
		 textPaint.setAntiAlias(true);
		 textPaint.setFakeBoldText(true);
		 textPaint.setTextSize(12); //original 12
 		 textPaint.setColor(Color.rgb(225, 225, 225));
		 
		 taskPaint = new Paint();
		 taskPaint.setAntiAlias(true);
		 taskPaint.setFakeBoldText(true);
		 taskPaint.setTextSize(14); //original 14
		 //taskPaint.setColor(Color.rgb(218, 165, 32)); //yellow
		 taskPaint.setColor(Color.rgb(255, 255, 255));
		
		//((Activity)context).registerForContextMenu(this);
		 
		 int texCoords[] = { 0, one, one, one, 0, 0, one, 0,};
		 int vertexCoords[] = 
		 { 		 0, 0, 0, 
				 one * 32, 0,0,
				 0, one * 32,0,
				 one * 32, one*32, 0};  //original: one*32
	      //&&& specify my own coords using GLVertex...
		 
	      ByteBuffer tbb = ByteBuffer.allocateDirect(texCoords.length * 4);
	      tbb.order(ByteOrder.nativeOrder());
	      textureBuffer = tbb.asIntBuffer();
	      textureBuffer.put(texCoords);
	      textureBuffer.position(0);

	      ByteBuffer vbb = ByteBuffer.allocateDirect(vertexCoords.length * 4);
	      vbb.order(ByteOrder.nativeOrder());
	      vertexBuffer = vbb.asIntBuffer();
	      vertexBuffer.put(vertexCoords);
	      vertexBuffer.position(0);
	}
	
	public void setDataServer(StateServer server)
	{
		this.data = server;
	}

	
	
	//Matrix should be the identity going into the function
	//Note: right now the view does not work properly with aspect ratios that generate non-square pixels (i.e. The width and height ratio have to match the ratio of your view)
	public void setViewRectangle(float x, float y, float width, float height)
	{
		map_x = x;
		map_y = y;
		map_width = width;
		map_height = height;
	}
	
	public void scrollMapBy(float x, float y) //keeps the same zoom level (window size) while translating the window
	{
		map_x += x;
		map_y += y;
	}
	
	public void centerMapOn(float x, float y)
	{
		map_x = x - map_width/2;
		map_y = y - map_height/2;
	}
	
	//relative zoom
	public void zoomMapBy(float zoomFactor) //keeps the same center while scaling the window size
	{
		if (zoomFactor <= 0)
			throw new RuntimeException("zoomFactor must be greater than 0");
		
		float centerX = getCenterX();
		float centerY = getCenterY();
		
		map_width /= zoomFactor;
		map_height /= zoomFactor;
		
		map_x = centerX - map_width/2;
		map_y = centerY - map_height/2;	
	}
	
	//absolute zoom, where a zoom factor of 1 means one map cell per pixel
	public void setZoomFactor(float zoomFactor)
	{
		if (zoomFactor <= 0)
			throw new RuntimeException("zoomFactor must be greater than 0");
		
		float centerX = getCenterX();
		float centerY = getCenterY();
		
		map_width = viewWidth / zoomFactor;
		map_height = viewHeight / zoomFactor;
		
		map_x = centerX - map_width/2;
		map_y = centerY - map_height/2;
		
		//invalidate();
	}
	
	float getCenterX()
	{
		return map_x + map_width/(2*(map_width/viewWidth));
	}
	
	float getCenterY()
	{
		return map_y + map_height/(2*(map_height/viewHeight));
	}
	
	
	//remember positive y goes down
	@Override
	public void onDrawFrame(GL10 gl) 
	{
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
		
		// Clear the screen to green
		gl.glClearColor(0, .3f,0,1);
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		
		gl.glTranslatef(-map_x,-map_y, 0);
		
		gl.glVertexPointer(3, GL10.GL_FIXED, 0, vertexBuffer);
	    gl.glTexCoordPointer(2, GL10.GL_FIXED, 0, textureBuffer);
		
		drawAnnotations(gl);
		drawTasks(gl);
		
		Annotation provTask = data.provisionalTask;
		if (p != null && showingProvisionalTasks)
		{
			int resID = R.drawable.task;
			gl.glBindTexture(GL10.GL_TEXTURE_2D, resID_to_glID.get(resID));
			gl.glPushMatrix();
			gl.glTranslatef(provTask.locationX-16,provTask.locationY-16,0);
		    gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
		    gl.glPopMatrix();
			
			canvas.drawText("   " + provTask.shortName, provTask.locationX,  provTask.locationY, textPaint);
		}
		
		PointF loc = data.currentLocation;
		gl.glPushMatrix();
			int resID = currentLocationDrawable;
			gl.glBindTexture(GL10.GL_TEXTURE_2D, resID_to_glID.get(resID));
			gl.glTranslatef(loc.x,loc.y,0);
			gl.glRotatef((float)Math.toDegrees(data.currentOrientation), 0, 0, -1);
			gl.glTranslatef(-16, -16, 0);
		    gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
	    gl.glPopMatrix();

	}
	
	//Project the pixel offsets of clicking on the screen into a position on the map
	public void viewCoordinatesToMapCoordinates(PointF p, float x, float y)
	{		
		float projectedX = (x/ viewWidth) * map_width + map_x;
		float projectedY = (1-(y / viewHeight)) * map_height + map_y;
		
		p.x = projectedX;
		p.y = projectedY;		
	}
	
	//Project a point on the map into pixel coordinates on the screen,
	//with the origin in the lower left
	public void MapCoordinatesToViewCoordinates(PointF p,float x, float y)
	{		
		float projectedX = ((x-map_x) / map_width) * viewWidth;
		float projectedY = (1 - ((y-map_y) / map_height)) * viewHeight;
		
		//origin transform
		projectedY = viewHeight - projectedY;
		
		p.x = projectedX;
		p.y = projectedY;
	}


	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		viewHeight = height;
		viewWidth = width;
		
		// Define the view frustum
		gl.glViewport(0, 0, viewWidth, viewHeight);
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();
		GLU.gluOrtho2D(gl, 0, 320, 0, 320);

		// Set up any other options we need
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		//      
		//      
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		gl.glEnable(GL10.GL_TEXTURE_2D);
		gl.glEnable(GL10.GL_BLEND);
		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);

		// Optional: disable dither to boost performance
		gl.glDisable(GL10.GL_DITHER);
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		canvas = new Canvas(gl);
		loadTextures(gl);
	}
	
	private void loadTextures(GL10 gl)
	{
		for (int resID : new int[] {
				R.drawable.annotation,
				R.drawable.avatar,
				R.drawable.breadcrumb,
				R.drawable.grid_point,
				R.drawable.red_cross,
				R.drawable.task,
				R.drawable.gps_breadcrumb,
				R.drawable.cursor})
		{
			int glID = GLResourceManager.loadTexture(gl, context, resID);
			resID_to_glID.put(resID, glID);
		}
	}
	
	private void drawAnnotations(GL10 gl)
	{ 		   
		List<Annotation> toRender;
		synchronized(data.map)
		{
			toRender = data.rangeQuery(map_x, map_x+map_width, map_y, map_y+map_height);
		}
			
		LabelMaker label = new LabelMaker(false, 512, 512);
		label.initialize(gl);
		label.beginAdding(gl);
		    
		//one pass to load up the textures for the text
		for(Annotation a :toRender)
		{
			if (a.shortName != null)
			{
				a.extension = label.add(gl, a.shortName, textPaint);
			}
		}
		
		label.endAdding(gl);				
		label.beginDrawing(gl);
		
		for(Annotation a :toRender)
		{
			if (a.type == Annotation.GRID_POINT && !showingGridPoints)
				continue;
			int resID = R.drawable.annotation;
			switch (a.type)
			{
			case BREADCRUMB_ANNOTATION:
				resID = R.drawable.breadcrumb; break;
			case GPS_BREADCRUMB_ANNOTATION:
				resID = R.drawable.gps_breadcrumb; break;
			case GRID_POINT:
				resID = R.drawable.grid_point; break;
			case CALIBRATION_DATA:
				resID = R.drawable.breadcrumb; break;
			default:	
			}
					
			if(a.isObstacle==true)
				drawObstacle(resID, gl, a);
			else
				draw(resID, gl, a);
			
			
			
			gl.glTexParameterf(GL10.GL_TEXTURE_2D,
	                   GL10.GL_TEXTURE_WRAP_S,
	                   GL10.GL_CLAMP_TO_EDGE);
			
			gl.glTexParameterf(GL10.GL_TEXTURE_2D,
	                   GL10.GL_TEXTURE_WRAP_T,
	                   GL10.GL_CLAMP_TO_EDGE);
	
	
			
			
			//gl.glScalef(map_height, map_height, 0);
			
//			// Scale up if the texture if smaller.
//			gl.glTexParameterf(GL10.GL_TEXTURE_2D,
//			                   GL10.GL_TEXTURE_MAG_FILTER,
//			                   GL10.GL_LINEAR);
//			
//			// scale linearly when image smalled than texture
//			gl.glTexParameterf(GL10.GL_TEXTURE_2D,
//			                   GL10.GL_TEXTURE_MIN_FILTER,
//			                   GL10.GL_LINEAR);
//			
//		
		    
		   
		    /*	float obstacleCoords[] = 
				 { 	0, 0, 0, 
					one * a.width, 0,0,
					0, one * a.height,0,
					one * a.width, one*a.height, 0};
		    	 ByteBuffer obb = ByteBuffer.allocateDirect(obstacleCoords.length * 4);
			      obb.order(ByteOrder.nativeOrder());
			      FloatBuffer obsBuffer = obb.asFloatBuffer();
			      obsBuffer.put(obstacleCoords);
			      obsBuffer.position(0);
		    	  	
		    	//gl.glVertexPointer(size, type, stride, pointer)
			    gl.glDisable(GLES11.GL_TEXTURE); //&&& check texture type...
			    gl.glColor4f(.3f, .3f,.3f,1);
		    	gl.glVertexPointer(3, GL10.GL_FLOAT, 0, obsBuffer);
		    	gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
		    	gl.glEnable(GL10.GL_DEPTH_TEST);
		    	//&&& clear memory
		    */
			
		   			
			if (a.shortName != null)
			{				
				MapCoordinatesToViewCoordinates(p,a.locationX, a.locationY);
				label.draw(gl, p.x, p.y, ((Integer)a.extension).intValue());
			}

		}
		
		label.endDrawing(gl);
		label.shutdown(gl);
	}

	private void draw(int resID, GL10 gl, Annotation a){
		
		gl.glBindTexture(GL10.GL_TEXTURE_2D, resID_to_glID.get(resID));
		gl.glPushMatrix();
		
		gl.glTranslatef(a.locationX-16,a.locationY-16,0);
	    gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
	    //&&& draw calls different 
	    gl.glPopMatrix();
	    
	    
	}
	
	private void drawObstacle(int resID, GL10 gl, Annotation a){
	      
		int vertexCoordsObs[] = 
		 { 		 0, 0, 0, 
				 (int) (one * a.width), 0,0,
				 0, (int) (one * a.height),0,
				 (int) (one * a.width), (int) (one*a.height), 0};  //original: one*32
		
		IntBuffer vertexBufferObs;
		
		ByteBuffer vbb = ByteBuffer.allocateDirect(vertexCoordsObs.length * 4);
		vbb.order(ByteOrder.nativeOrder());
		vertexBufferObs = vbb.asIntBuffer();
		vertexBufferObs.put(vertexCoordsObs);
		vertexBufferObs.position(0);
	      
		gl.glVertexPointer(3, GL10.GL_FIXED, 0, vertexBufferObs);
		
		gl.glBindTexture(GL10.GL_TEXTURE_2D, resID_to_glID.get(resID));
		gl.glPushMatrix();
		
		gl.glTranslatef(a.locationX-16,a.locationY-16,0);
	    gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
	    //&&& draw calls different 
	    gl.glPopMatrix();
	    
	    gl.glVertexPointer(3, GL10.GL_FIXED, 0, vertexBuffer);
		
	}
	
	private void drawTasks(GL10 gl)
	{ 		   
		
		int resID = R.drawable.task;
		gl.glBindTexture(GL10.GL_TEXTURE_2D, resID_to_glID.get(resID));
		
		synchronized(data.currentTasks)
		{
		for (Annotation a : data.currentTasks)
		{
			gl.glPushMatrix();
			gl.glTranslatef(a.locationX-16,a.locationY-16,0);
		    gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
		    gl.glPopMatrix();
			
			canvas.drawText("   " + a.shortName, a.locationX,  a.locationY, taskPaint);
		}}
	}


}
