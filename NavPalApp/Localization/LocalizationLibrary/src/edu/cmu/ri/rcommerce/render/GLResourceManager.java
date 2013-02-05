package edu.cmu.ri.rcommerce.render;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * Does the low-level manipulations needed for loading a bitmap into the graphics memory.
 * @author Nisarg
 *
 */
public class GLResourceManager {
	
	private static void loadIntoAccelerator(GL10 gl, ByteBuffer bb, int id,
	         int width, int height) {

	      // Load it up
	      gl.glBindTexture(GL10.GL_TEXTURE_2D, id);
	      gl.glTexImage2D(GL10.GL_TEXTURE_2D, 0, GL10.GL_RGBA,
	            width, height, 0, GL10.GL_RGBA,
	            GL10.GL_UNSIGNED_BYTE, bb);
	      gl.glTexParameterx(GL10.GL_TEXTURE_2D,
	            GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
	      gl.glTexParameterx(GL10.GL_TEXTURE_2D,
	            GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
	   }
	
	private static ByteBuffer extractByteBuffer(Bitmap bmp) { 
	      ByteBuffer bb = ByteBuffer.allocateDirect(bmp.getHeight()
	            * bmp.getWidth() * 4);
	      bb.order(ByteOrder.BIG_ENDIAN);
	      IntBuffer ib = bb.asIntBuffer();

	      // Convert ARGB -> RGBA
	      for (int y = bmp.getHeight() - 1; y > -1; y--)
	         for (int x = 0; x < bmp.getWidth(); x++) {
	            int pix = bmp.getPixel(x, bmp.getHeight() - y - 1);
	            int alpha = ((pix >> 24) & 0xFF);
	            int red = ((pix >> 16) & 0xFF);
	            int green = ((pix >> 8) & 0xFF);
	            int blue = ((pix) & 0xFF);

	            ib.put(red << 24 | green << 16 | blue << 8 | alpha);
	         }
	      bb.position(0);
	      return bb;
	   }
	 
	 public static int loadTexture(GL10 gl,Context context, int resID) {
		 int[] GLid = new int[1];
	      Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), resID);
	      
	      ByteBuffer bb = extractByteBuffer(bmp);
	     
	      gl.glGenTextures(1,GLid,0);
	      if (GLid[0] == 0)
	    	  throw new RuntimeException();
	      //Log.d("GL","loading texture");
	      loadIntoAccelerator(gl, bb,GLid[0], bmp.getWidth(), bmp.getHeight());
	      
	      return GLid[0];
	   }

}
