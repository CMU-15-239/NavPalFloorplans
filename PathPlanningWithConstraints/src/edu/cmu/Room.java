/**
 * @Author - Piotr and Chet
 * 
 * */
package edu.cmu;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.Vector;

import android.os.Environment;
import android.util.Log;

/**
 * Rooms are used by both the path planner and the path predictor. They are connected by a list of actions and also interact via D*.
 */
public class Room implements Vertex
{
    Vector<Action> Actions = new Vector<Action>();

    ArrayList<PointF> vertex = new ArrayList<PointF>(0);
    ArrayList<Obstacle> ob = new ArrayList<Obstacle>(0);
    ArrayList<Landmark> lm = new ArrayList<Landmark>(0);
    Vector<Exit> ex = new Vector<Exit>(0);
    Map M;	// Stores the reference of the map object to which this room belongs.
    float g, rhs;// used for graph planner
    Room previousRoom;
    private boolean enroute = false;

    private String color, d, label, type, transform;
    private int id;

    public Room()
    {
	// Log.d(Constants.TAG, "Room::Room() Instantiated!");
    }

    public Room(String color, String label, String d, String type, String transform)
    {
	this.label = label;
	this.color = color;
	this.d = d;
	this.type = type;
	this.transform = transform;
    }

    public String getType()
    {
	return type;
    }

    public void setType(String type)
    {
	this.type = type;
    }

    public boolean isEnroute()
    {
	return enroute;
    }

    public void setEnroute(boolean e)
    {
	enroute = e;
    }

    public String getLabel()
    {
	return label;
    }

    public void setLabel(String label)
    {
	this.label = label;
    }

    public int getID()
    {
	return id;
    }

    public void setID(int id)
    {
	this.id = id;
    }

    public String getd()
    {
	return d;
    }

    public void setd(String d)
    {
	this.d = d;
    }

    public String getColor()
    {
	return color;
    }

    public void setColor(String color)
    {
	this.color = color;
    }

    public String getTransform()
    {
	return transform;
    }

    public void setTranform(String transform)
    {
	this.transform = transform;
    }

    @Override
    public String toString()
    {
	String str = "";

	if (this.label != "")
	{
	    str = "ID: " + this.id + " ";
	}
	
	if (this.label != "")
	{
	    str += "Label: " + this.label + " ";
	}
	else
	{
	    str += "Label: (none) ";
	}

	if (this.color != "")
	{
	    str += "Color: " + this.color + " "; 
	}
	else
	{
	    str += "Color: (none) ";
	}

	if (this.d != "")
	{
	    str += "d: " + this.d + " "; 
	}
	else
	{
	    str += "d: (none) ";
	}

	if (this.type != "")
	{
	    str += "type: " + this.type + " "; 
	}
	else
	{
	    str += "type: (none) ";
	}

	if (this.transform != "")
	{
	    str += "transform: " + this.transform + " "; 
	}
	else
	{
	    str += "transform: (none) ";
	}	

	String vertexStr = "";	
	for(int i=0; i<vertex.size(); i++)
	{
	    vertexStr += vertex.get(i).toString() + " ";
	}

	str += "Vertex: " + vertexStr;

	return str;
    }

    /**
     * /brief Parses the coordinate data found in the d member variable.
     * 
     * This method will parse the string member variable \c d, which is from
     * the d attribute that is part of the path tag from the SVG file. This 
     * method is only invoked when the actual SVG file is read. If the 
     * _rooms.txt file is read in instead, the \c d member variable in this
     * class will be null.  
     * 
     * /note This method has trouble with the Bezier curves. If this method sees 
     * a curve it will be skipped. This may need to be fixed to include these 
     * curves in the future. This is currently a problem for the high-bay stairs. 
     * The helix in the gates building should be a train wreck. The high-bay on 
     * the second floor was added to the save file manually.
     */
    public void findVertices()
    {
	String logString = "";

	StringTokenizer StringTok = new StringTokenizer(getd(), " ");
	StringTokenizer subStringTok = new StringTokenizer("", ", ");
	boolean absolute = false, curving = false;
	float A[][] = new float[3][1];
	float P[] = new float[2];
	String Token;
	int tokens = 0;
	do
	{
	    Token = StringTok.nextToken();
	    tokens++;
	    if (Token.equals("m"))
	    {
		logString += "'m' Block:\n" + "\tToken: " + Token + "\n\n";
		absolute = false;
	    }
	    else
		if (Token.equals("M"))
		{
		    logString += "'M' Block:\n" + "\tToken: " + Token + "\n\n";

		    absolute = true;
		}
		else
		    if (Token.toLowerCase().equals("z"))
		    {
			if (curving)
			{
			    curving = false;
			    vertex.add(new PointF(Math.abs((P[1] + transformMatrix(getTransform())[0][2])), Math.abs((P[0] + transformMatrix(getTransform())[1][2]))));
			    logString += "'z' and 'curving' Block:\n" + "\tP[1] = " + P[1] + "\n" + "\tP[0] = " + P[0] + "\n" + "\ttransform [0][2] = " + transformMatrix(getTransform())[0][2] + "\n" + "\ttransform [1][2] = " + transformMatrix(getTransform())[1][2] + "\n" + "\tx: " + Math.abs((P[1] + transformMatrix(getTransform())[0][2])) + ", y: " + Math.abs((P[0] + transformMatrix(getTransform())[1][2])) + "\n\n";
			}
			break;
		    }
		    else
			if (Token.equals("L"))
			{
			    if (curving)
			    {
				curving = false;
				vertex.add(new PointF(Math.abs((int) (P[1] + transformMatrix(getTransform())[0][2])), Math.abs((int) (P[0] + transformMatrix(getTransform())[1][2]))));

				logString += "'L' and 'curving' Block:\n" + "\tP[1] = " + P[1] + "\n" + "\tP[0] = " + P[0] + "\n" + "\ttransform [0][2] = " + transformMatrix(getTransform())[0][2] + "\n" + "\ttransform [1][2] = " + transformMatrix(getTransform())[1][2] + "\n" + "\tx: " + Math.abs((P[1] + transformMatrix(getTransform())[0][2])) + ", y: " + Math.abs((P[0] + transformMatrix(getTransform())[1][2])) + "\n\n";
			    }
			    absolute = true;
			}
			else
			    if (Token.equals("l"))
			    {
				if (curving)
				{
				    curving = false;
				    vertex.add(new PointF(Math.abs((int) (P[1] + transformMatrix(getTransform())[0][2])), Math.abs((int) (P[0] + transformMatrix(getTransform())[1][2]))));

				    logString += "'l' and 'curving' Block:\n" + "\tP[1] = " + P[1] + "\n" + "\tP[0] = " + P[0] + "\n" + "\ttransform [0][2] = " + transformMatrix(getTransform())[0][2] + "\n" + "\ttransform [1][2] = " + transformMatrix(getTransform())[1][2] + "\n" + "\tx: " + Math.abs((P[1] + transformMatrix(getTransform())[0][2])) + ", y: " + Math.abs((P[0] + transformMatrix(getTransform())[1][2])) + "\n\n";
				}
				absolute = false;
			    }
			    else
				if (Token.equals("C"))
				{
				    if (curving)
				    {
					curving = false;
					vertex.add(new PointF(Math.abs((int) (P[1] + transformMatrix(getTransform())[0][2])), Math.abs((int) (P[0] + transformMatrix(getTransform())[1][2]))));

					logString += "'C' and 'curving' Block:\n" + "\tP[1] = " + P[1] + "\n" + "\tP[0] = " + P[0] + "\n" + "\ttransform [0][2] = " + transformMatrix(getTransform())[0][2] + "\n" + "\ttransform [1][2] = " + transformMatrix(getTransform())[1][2] + "\n" + "\tx: " + Math.abs((P[1] + transformMatrix(getTransform())[0][2])) + ", y: " + Math.abs((P[0] + transformMatrix(getTransform())[1][2])) + "\n\n";
				    }
				    Token = StringTok.nextToken();
				    absolute = true;
				    curving = true;
				    // System.out.println("****************\nCURVE DATA\n************");
				}
				else
				    if (Token.equals("c"))
				    {
					if (curving)
					{
					    curving = false;
					    vertex.add(new PointF(Math.abs((int) (P[1] + transformMatrix(getTransform())[0][2])), Math.abs((int) (P[0] + transformMatrix(getTransform())[1][2]))));

					    logString += "'c' and 'curving' Block:\n" + "\tP[1] = " + P[1] + "\n" + "\tP[0] = " + P[0] + "\n" + "\ttransform [0][2] = " + transformMatrix(getTransform())[0][2] + "\n" + "\ttransform [1][2] = " + transformMatrix(getTransform())[1][2] + "\n" + "\tx: " + Math.abs((P[1] + transformMatrix(getTransform())[0][2])) + ", y: " + Math.abs((P[0] + transformMatrix(getTransform())[1][2])) + "\n\n";
					}
					absolute = false;
					curving = true;
					// System.out.println("****************\nCURVE DATA\n************");
				    }
				    else
				    {
					logString += "Last Block:\n";

					// System.out.println("Token:"+Token);
					if (!curving)
					{
					    logString += "\tNot curving\n";
					    logString += "\tOld P[0] = " + P[0] + "\n";
					    logString += "\tOld P[1] = " + P[1] + "\n";
					    logString += "\tA[0][0] = " + A[0][0] + "\n";
					    logString += "\tA[1][0] = " + A[1][0] + "\n";

					    P[0] = A[0][0];
					    P[1] = A[1][0];

					    logString += "\tNew P[0] = " + P[0] + "\n";
					    logString += "\tNew P[1] = " + P[1] + "\n";
					    logString += "\tToken: " + Token;

					    subStringTok = new StringTokenizer(Token, " ,");

					    if (absolute || tokens == 1)
					    {
						A[0][0] = Float.parseFloat(subStringTok.nextToken());
						logString += "\t\tabsolute or tokens == 1 Block\n";
						logString += "\tsubStringTok: nextToken Was " + A[0][0] + "\n";
						logString += "\t\tA[0][0] =" + A[0][0] + "\n";

						try
						{
						    A[1][0] = Float.parseFloat(subStringTok.nextToken());
						    logString += "\tsubStringTok: nextToken Was " + A[1][0] + "\n";
						    logString += "\t\tTry A[1][0] =" + A[1][0] + "\n";
						}
						catch (Exception c)
						{
						    A[1][0] = Float.parseFloat(StringTok.nextToken());
						    logString += "\t\tStringTok: nextToken Was " + A[1][0] + "\n";
						    logString += "\t\tCatch A[1][0] =" + A[1][0] + "\n";
						}
					    }
					    else
					    {
						A[0][0] += Float.parseFloat(subStringTok.nextToken());
						logString += "\t\tNot absolute or Not tokens == 1 Block\n";
						logString += "\t\tsubStringTok: nextToken Was " + A[0][0] + "\n";
						logString += "\t\tA[0][0] =" + A[0][0] + "\n";

						try
						{
						    A[1][0] += Float.parseFloat(subStringTok.nextToken());
						    logString += "\t\tTry A[1][0] =" + A[1][0] + "\n";
						    logString += "\tsubStringTok: nextToken Was " + A[1][0] + "\n";
						}
						catch (Exception c)
						{
						    A[1][0] = Float.parseFloat(StringTok.nextToken());
						    logString += "\t\tCatch A[1][0] =" + A[1][0] + "\n";
						    logString += "\t\tStringTok: nextToken Was " + A[1][0] + "\n";
						}
					    }

					    A[2][0] = 1.0f;

					    // USE MATRIX -- doesn't work quite right...
					    // This should be able to invert the matrix and multiply, but it didn't work for some reason.
					    boolean useMatrix = false;
					    if (useMatrix)
					    {
						float M[][] = MatrixBuddy.multiplyMatrices(MatrixBuddy.invertMatrix(transformMatrix(getTransform())), A);
						vertex.add(new PointF(Math.abs(M[0][0]), Math.abs(M[1][0])));
					    }
					    // JUST SHIFT
					    // All the matrices I dealt with were just a shift anyhow, so I hacked this.
					    else
					    {
						// System.out.println(A[1][0] + " " + A[0][0]);
						try
						{
						    vertex.add(new PointF(Math.abs((A[1][0] + transformMatrix(getTransform())[0][2])), Math.abs((A[0][0] + transformMatrix(getTransform())[1][2]))));
						    logString += "Not UseMatrix Block:\n" + "\tA[1][0] = " + A[1][0] + "\n" + "\tA[0][0] = " + A[0][0] + "\n" + "\ttransform [0][2] = " + transformMatrix(getTransform())[0][2] + "\n" + "\ttransform [1][2] = " + transformMatrix(getTransform())[1][2] + "\n" + "\tx: " + Math.abs((A[1][0] + transformMatrix(getTransform())[0][2])) + ", y: " + Math.abs((A[1][0] + transformMatrix(getTransform())[1][2])) + "\n\n";
						}
						catch (NullPointerException e)
						{

						}

					    }
					    // System.out.println(Math.round(A[0][0]) + " " + Math.round(A[1][0])+"\t\t"+Math.round(Math.abs(M[0][0])) + " " + Math.round(Math.abs(M[1][0])));
					}
				    }

	}
	while (StringTok.hasMoreElements());

	// Disabled while testing the reading of the newly generated rooms files.
	// try
	// {
	// logCurrentVertexDataForSpecificRoom(MainInterface.CURRENTMAPLOAD, this.label, logString);
	// }
	// catch (IOException e)
	// {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
    }

    /*
     * Gary Debug: Output the vertices for the specific room
     */
    private static void logCurrentVertexDataForSpecificRoom(int mapNumber, String roomName, String logString) throws IOException
    {
	// Setup the file for output
	File root = Environment.getExternalStorageDirectory();
	if (root.canWrite())
	{
	    File outFile = new File(new File(root, "NavPalSaves/"), "floor_" + (mapNumber + 1) + "_" + roomName + "_ParsingDStringLog.txt");
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

	    FileWriter mapwriter = new FileWriter(outFile);
	    BufferedWriter out = new BufferedWriter(mapwriter);

	    out.write(logString);

	    out.close();
	}
    }

    /**
     * This converts the string to a matrix. This will be sent Room.getTransform() and it will return the matrix.
     * 
     * */
    public float[][] transformMatrix(String T)
    {
	StringTokenizer st = new StringTokenizer(T, " \t\n,()");

	if (st.hasMoreTokens() && ((String) st.nextToken()).equals("matrix"))
	{
	    int i = -1;
	    float A[][] = new float[3][3];

	    while (st.hasMoreTokens() && i++ < 6)
	    {
		A[i % 2][i / 2] = Float.parseFloat(st.nextToken());
	    }
	    A[2][0] = 0;
	    A[2][1] = 0;
	    A[2][2] = 1;

	    return A;
	}
	return null;
    }

    // Globals which should be set before calling this function:
    //
    // int polySides = how many corners the polygon has
    // float polyX[] = horizontal coordinates of corners
    // float polyY[] = vertical coordinates of corners
    // float x, y = point to be tested
    //
    // (Globals are used in this example for purposes of speed. Change as
    // desired.)
    //
    // The function will return YES if the point x,y is inside the polygon, or
    // NO if it is not. If the point is exactly on the edge of the polygon,
    // then the function may return YES or NO.
    //
    // Note that division by zero is avoided because the division is protected
    // by the "if" clause which surrounds it.
    /**
     * Found this online. It draws a line across the polygon, and if it crosses the polygon's sides an even number of times before finding the point, it is on the outside of the polygon. If it crosses an odd number of times, it is on the
     * inside.
     */
    public boolean pointInRoom(int q, int w)
    {
	//Log.d(Constants.DSTAR_TAG, "point");

	float x = (float) q;
	float y = (float) w;
	int polySides = this.vertex.size();
	float polyY[] = new float[this.vertex.size()];
	float polyX[] = new float[this.vertex.size()];
	for (int i = 0; i < this.vertex.size(); i++)
	{
	    polyY[i] = Math.round(this.vertex.get(i).x);
	    polyX[i] = Math.round(this.vertex.get(i).y);

	}
	int i, j = polySides - 1;
	boolean oddNodes = false;

	for (i = 0; i < polySides; i++)
	{
	    if (polyY[i] <= y && polyY[j] > y || polyY[j] <= y && polyY[i] > y)
	    {
		if (polyX[i] + (y - polyY[i]) / (polyY[j] - polyY[i]) * (polyX[j] - polyX[i]) < x + 1)
		{
		    oddNodes = !oddNodes;
		}
	    }
	    j = i;
	}
	return oddNodes;
    }

    public Vector<Action> getActions()
    {
	return Actions;
    }

    public float getCumulativeCost()
    {
	return this.rhs;
    }

    public void setCumulativeCostCost(float a)
    {
	this.rhs = a;
    }

    // interments the implaface Vertex

    // @Override
    public Vector<? extends Edge> getEdges()
    {
	return Actions;
    }

    public float getCost()
    {
	if (this.getColor().equals("Room"))
	{
	    return 15;// I dunno. Just make this bigger than hallways so the user takes hallways.
	}
	else
	{
	    return 1;
	}
    }

    @Override
    public Vertex getSubVertex()
    {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public int getHierarchy()
    {
	return 0;
    }

    @Override
    public void setCumulativeCost(float a)
    {
	rhs = a;
    }

    @Override
    public boolean getEnroute()
    {
	return this.getEnroute();
    }

    @Override
    public Vertex getParent()
    {
	return this.previousRoom;

    }

    @Override
    public void setParent(Vertex a)
    {
	this.previousRoom = (Room) a;

    }

    public boolean outOfBounds()
    {
	return false;// return H.RoomsMap[this.x][this.y]==-1;
    }

    @Override
    public Vertex raise()
    {
	return M;
    }
    
    // Debug Methods
    
    public void dumpVertexArrayToFile()
    {
//	// Setup the file for output
//	File root = Environment.getExternalStorageDirectory();
//	if (root.canWrite())
//	{
//	    File outFile = new File(new File(root, "NavPalSaves/"), "floor_" + (MainInterface.CURRENTMAPLOAD + 1) +"_Room_Vertex_file.txt");
//	    if (!outFile.exists())
//	    {
//		try
//		{
//		    outFile.createNewFile();
//		}
//		catch (IOException e3)
//		{
//		    e3.printStackTrace();
//		}
//	    }
//
//	    FileWriter mapwriter = null;
//	    try
//	    {
//		mapwriter = new FileWriter(outFile);
//	    }
//	    catch (IOException e)
//	    {
//		// TODO Auto-generated catch block
//		e.printStackTrace();
//	    }
//	    BufferedWriter out = new BufferedWriter(mapwriter);
//	    
//	    for (int i = 0; i<SIZEX; i++)
//	    {
//		String writeString = "";
//		for (int j = 0; j<SIZEY; j++)
//		{
//		    writeString += String.format("%4d ", RoomsMap[i][j]);
//		}
//
//		try
//		{
//			out.write(writeString);
//		}
//		catch (IOException e)
//		{
//		    // TODO Auto-generated catch block
//		    e.printStackTrace();
//		}
//	    }
//
//	    try
//	    {
//		out.close();
//	    }
//	    catch (IOException e)
//	    {
//		// TODO Auto-generated catch block
//		e.printStackTrace();
//	    }
//	}
    }
}
