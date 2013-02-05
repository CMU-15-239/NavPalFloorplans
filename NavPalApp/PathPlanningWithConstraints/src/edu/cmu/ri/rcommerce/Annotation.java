package edu.cmu.ri.rcommerce;

import java.io.Serializable;

/**
 * Class to hold standard annotation messages.
 * 
 * Designed for future extensions as well as interoperability with C / C++ code.
 * @author Nisarg
 *
 */
public class Annotation implements Serializable
{
	//'enum' of annotation types. These are constants rather than java enums for serializability and compatibility with C++
	public static final int FREEFORM_ANNOTATION = 0;
	public static final int TREASURE_ANNOTATION = 1;
	public static final int GRID_POINT = 2;
	public final static int BREADCRUMB_ANNOTATION = 3;
	public final static int GOTO_TASK = 4;
	public final static int CALIBRATION_DATA = 5;
	public static final int OBSTACLE = 6;
	
	public final static int GPS_BREADCRUMB_ANNOTATION = 1000; //TODO: Temporary type for debugging GPS
	
	
	public final static int FILL_ANNOTATION_REQUEST = 0;
	
	
	/**Top 8 bits of ID are the last 8 bits of the agent's IP address. The remaining bits are a 24 bit counter */
	public int ID; 
	
	/**If this annotation is an update rather than entirely new, this is the ID of the old annotation, so it can be deleted.
	If there is no such annotation, this is 0.*/
	public int IDtoReplace; 
	
	/**Must be in unix format*/
	public long timestamp;
	
	/**Must be one of the constants defined in Annotation (e.g. FREEFORM_ANNOTATION)*/ 
	public int type;
	
	/** A word or two suitable for display on a map */
	public String shortName;
	/** Full details of the annotation */
	public String longDescription;
	
	public float locationX, locationY;
	public float width, height; //needed for general rectangular obstacles
	
	public boolean isObstacle;
	
	public byte[] binaryData;
	
	public transient Object extension; //used for local purposes, not part of the specification
}
