/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.cmu;

import android.content.res.Resources.NotFoundException;
import android.content.Context;
import java.util.ArrayList;
import java.io.*;

import edu.cmu.ri.rcommerce.particleFilter.Particle2D;
import android.os.Environment;
import android.util.Log;

/**
 *
 * @author Chet
 */

/**
 * These methods are all for reading/writing from text files. The map data is read in MainInterface.loadMap. Rooms, Sectors, and Annotations are written here. There is also a handler for writing particle filter data.
 * 
 * 
 */
public class MapIO
{

    public static void loadExits(int CURRENTMAP, Map H, int res) throws IOException, FileNotFoundException
    {

	File f = new File(Environment.getExternalStorageDirectory() + "/NavPalSaves/" + MapList.MAPS[CURRENTMAP][4] + "_exit.txt");
	FileInputStream inputStream = new FileInputStream(f);
	StreamTokenizer st = new StreamTokenizer(inputStream);
	st.whitespaceChars(' ', ' ');
	st.eolIsSignificant(true);
	int x = 0, y = 0;
	String shortString = "[no title]", longString = "[no title]";
	while (st.nextToken() != StreamTokenizer.TT_EOF)
	{
	    x = (int) st.nval / res;
	    st.nextToken();
	    y = (int) st.nval / res;
	    shortString = "";
	    while (st.nextToken() != StreamTokenizer.TT_EOL)
	    {
		shortString += st.sval + " ";
	    }

	    longString = "";
	    while (st.nextToken() != StreamTokenizer.TT_EOL && st.sval != null)
	    {
		longString += st.sval + " ";
	    }
	    H.annotations.E.add(new Exit(H, x, y, shortString.trim(), H.annotations.assignID()));
	    H.annotations.E.get(H.annotations.E.size() - 1).longMes = longString.trim();
	    if (!H.annotations.E.isEmpty())
	    {
		// Toast.makeText(getApplicationContext(), "Exits succesfully loaded",
		// Toast.LENGTH_SHORT).show();
	    }
	}
    }

    public static void loadLandmarks(int CURRENTMAP, Map H, int res) throws IOException, FileNotFoundException
    {
	File f = new File(Environment.getExternalStorageDirectory() + "/NavPalSaves/" + MapList.MAPS[CURRENTMAP][4] + "_lmrk.txt");
	FileInputStream inputStream = new FileInputStream(f);
	StreamTokenizer st = new StreamTokenizer(inputStream);
	st.whitespaceChars(' ', ' ');
	st.eolIsSignificant(true);
	int x = 0, y = 0;
	String shortString = "[no title]", longString = "[no title]";
	while (st.nextToken() != StreamTokenizer.TT_EOF)
	{
	    x = (int) st.nval / res;
	    st.nextToken();
	    y = (int) st.nval / res;
	    shortString = "";
	    while (st.nextToken() != StreamTokenizer.TT_EOL)
	    {
		shortString += st.sval + " ";
	    }
	    longString = "";
	    while (st.nextToken() != StreamTokenizer.TT_EOL)
	    {
		longString += st.sval + " ";
	    }

	    H.annotations.L.add(new Landmark(x, y, shortString.trim(), H.annotations.assignID(), H));
	    H.annotations.L.get(H.annotations.L.size() - 1).longMes = longString.trim();
	    if (!H.annotations.L.isEmpty())
	    {
		// Toast.makeText(getApplicationContext(), "Landmarks succesfully loaded",
		// Toast.LENGTH_SHORT).show();
	    }

	}

    }

    public static void loadObstacles(int CURRENTMAP, Map H, int res) throws IOException, FileNotFoundException
    {
	File f = new File(Environment.getExternalStorageDirectory() + "/NavPalSaves/" + MapList.MAPS[CURRENTMAP][4] + "_obs.txt");
	FileInputStream inputStream = new FileInputStream(f);
	StreamTokenizer st = new StreamTokenizer(inputStream);
	st.whitespaceChars(' ', ' ');
	st.eolIsSignificant(true);
	new ArrayList<Obstacle>(0);
	int x = 0, y = 0, size = 0;
	String shortString = "[no title]", longString = "[no title]";
	while (st.nextToken() != StreamTokenizer.TT_EOF)
	{
	    x = (int) st.nval / res;
	    st.nextToken();
	    y = (int) st.nval / res;
	    st.nextToken();
	    size = (int) st.nval;
	    shortString = "";
	    while (st.nextToken() != StreamTokenizer.TT_EOL)
	    {
		shortString += st.sval + " ";
	    }
	    longString = "";
	    while (st.nextToken() != StreamTokenizer.TT_EOL)
	    {
		longString += st.sval + " ";
	    }
	    Obstacle obs = new Obstacle(x, y, size, shortString.trim(), H.annotations.assignID(), H);
	    H.annotations.O.add(obs);
	    H.annotations.O.get(H.annotations.O.size() - 1).longMes = longString.trim();
	    if (!H.annotations.O.isEmpty())
	    {
		// Toast.makeText(getApplicationContext(), "Obstacles succesfully loaded",
		// Toast.LENGTH_SHORT).show();
	    }

	}

    }

    public static void loadRooms(int CURRENTMAP, Map H, int res) throws IOException
    {
	Log.d(Constants.TAG, "MapIO::loadRooms() Called!");

	File f = new File(Environment.getExternalStorageDirectory() + "/NavPalSaves/" + MapList.MAPS[CURRENTMAP][4] + "_room.txt");

	FileInputStream inputStream = new FileInputStream(f);
	StreamTokenizer st = new StreamTokenizer(inputStream);
	st.whitespaceChars(' ', ' ');
	st.eolIsSignificant(true);
	float x = 0, y = 0;
	String shortString = "[no title]", longString = "[no title]";

	while (st.nextToken() != StreamTokenizer.TT_EOF)
	{
	    // System.out.println("SIZE "+H.rooms.size());

	    shortString = st.sval;
	    st.nextToken();
	    longString = st.sval;
	    H.rooms.add(new Room());
	    int token = st.nextToken();
	    while (token != StreamTokenizer.TT_EOL && token != StreamTokenizer.TT_EOF)
	    {
		// System.out.println("X: "+st.sval+" "+st.nval);
		x = (float) (st.nval / (double) res);
		st.nextToken();
		// System.out.println("Y: "+st.sval+" "+st.nval);
		y = (float) (st.nval / (double) res);
		H.rooms.get(H.rooms.size() - 1).vertex.add(new PointF(x, y));
		token = st.nextToken();
	    }

	    H.rooms.get(H.rooms.size() - 1).setLabel(longString.trim());	// Either Room or Hallway
	    H.rooms.get(H.rooms.size() - 1).setColor(shortString.trim());	// The name of the room, typically pathXXXX where XXXX is a number
	    if (!H.rooms.isEmpty())
	    {
		// Toast.makeText(getApplicationContext(), "Rooms successfully loaded",
		// Toast.LENGTH_SHORT).show();
	    }
	}
	System.out.println("Rooms Loaded");

	// for (int i = 0; i < H.rooms.size();) {

	// for (int j = 0; j < H.rooms.get(i).vertex.size(); j++) {
	// System.out.println(H.rooms.get(i).getColor() + " " + H.rooms.get(i).getLabel() + " Shape " + i + ", Vertex " + j + " (" + scaleFactorRoomsx * H.rooms.get(i).vertex.get(j).x + "," + scaleFactorRoomsy *
	// H.rooms.get(i).vertex.get(j).y + ")");
	// }
	// i++;
    }

    public static void loadSector(int CURRENTMAP, Map H, int res) throws Exception, FileNotFoundException, IOException
    {
	H.RoomsMap = new int[H.getSizeX()][H.getSizeY()];

	// System.out.println("Attempt To Load " + MapList.MAPS[CURRENTMAP][4]);
	File f = new File(Environment.getExternalStorageDirectory() + "/NavPalSaves/" + MapList.MAPS[CURRENTMAP][4] + "_sector.txt");
	FileInputStream inputStream = new FileInputStream(f);

	StreamTokenizer st = new StreamTokenizer(inputStream);
	st.whitespaceChars(' ', ' ');
	st.eolIsSignificant(true);
	// System.out.println("Starts to read " + MapList.MAPS[CURRENTMAP][4]);

	int x, y, readRes;
	st.nextToken();
	x = (int) st.nval;
	st.nextToken();
	y = (int) st.nval;
	st.nextToken();
	st.nextToken();
	readRes = (int) st.nval;
	if (x != H.getSizeX() || y != H.getSizeY() || readRes != res)
	{
	    // System.out.println((x != H.getSizeX()) + " " + (y != H.getSizeY()) + " " + (readRes != res));
	    throw new Exception();
	}

	int token;
	// System.out.println("Attempts to read Map");
	for (int i = 0; i < x; i++)
	{
	    for (int j = 0; j < y; j++)
	    {
		token = st.nextToken();
		if (token == StreamTokenizer.TT_EOF)
		{
		    System.out.println("Reached EOF while Parsing");
		    break;
		}
		if (token == StreamTokenizer.TT_EOL)
		{
		    st.nextToken();
		}
		H.RoomsMap[i][j] = (int) st.nval;

	    }
	}
	System.out.println("RoomsMap Loaded");
	inputStream.close();
    }

    /* This works on the assumption that the svg data is scaled to a bitmap of equal size. If this is not the case, additional scaling must take place below. */
    public static void parseRooms(Context act, int CURRENTMAP, Map H, int scale, int res) throws NotFoundException, NumberFormatException, IOException
    {
	Log.d(Constants.TAG, "MapIO::parseRooms(" + act.getClass().getName() + ", " + CURRENTMAP + ", ...) called!");

	// Loads the SVG file to parse the rooms from the colored floor plan
	InputStream svgFile = act.getResources().openRawResource(Integer.parseInt(MapList.MAPS[CURRENTMAP][6]));
	XMLReader xmlr = new XMLReader();
	xmlr.runExample(svgFile);
	H.rooms = xmlr.getRooms();

	// used for making sure labels on maps dont show up as barriers.
	/*
	 * ArrayList<Point> glyphs = xmlr.getGlyphs(); System.out.println(glyphs.size()); for (Point p : glyphs) { try { H.node[(int) (scaleFactorRoomsy * p.y)][(int) (scaleFactorRoomsx * p.x)].set = '0'; } catch (Exception e) { } }
	 */
	xmlr = null;
	File root = Environment.getExternalStorageDirectory();
	if (root.canWrite())
	{
	    File outFile = new File(new File(root, "NavPalSaves/"), MapList.MAPS[CURRENTMAP][4] + "_room.txt");
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
	    // scale factor corresponds to the conversion between points and pixels. Google it or something
	    final double scaleFactorRoomsy = MapList.mReadSvg[CURRENTMAP][0];
	    final double scaleFactorRoomsx = MapList.mReadSvg[CURRENTMAP][1];
	    // shift factor was chosen to line up the svg better with the map. This number was chosen by
	    // using the Show Rooms option in the Admin menu
	    final double shiftFactorRoomsy = MapList.mReadSvg[CURRENTMAP][2];
	    final double shiftFactorRoomsx = MapList.mReadSvg[CURRENTMAP][3];

	    FileWriter mapwriter = new FileWriter(outFile);
	    BufferedWriter out = new BufferedWriter(mapwriter);
	    String writeString = "";
	    for (int i = 0; i < H.rooms.size();)
	    {
		if (H.rooms.get(i).vertex.size() < 3)
		{
		    H.rooms.remove(i);
		}
		else
		{
		    if (H.rooms.get(i).getColor().trim().equals("Hallway") || H.rooms.get(i).getColor().trim().equals("Room"))
		    {
			// Gary Debug: Writes out the raw vertices to a file named after the room label. This is to compare
			// these values with the corresponding values from the Python Code SVG alternate tool.
			//writeCurrnetRoomVertices(CURRENTMAP, H.rooms.get(i).getLabel(), H.rooms.get(i).vertex);

			writeString = H.rooms.get(i).getColor() + " " + H.rooms.get(i).getLabel() + " ";
			for (int j = 0; j < H.rooms.get(i).vertex.size(); j++)
			{
			    // System.out.println("Shape " + i + ", Vertex " + j + " (" + scaleFactorRoomsx * H.rooms.get(i).vertex.get(j).x + "," + scaleFactorRoomsy * H.rooms.get(i).vertex.get(j).y + ")");
			    H.rooms.get(i).vertex.get(j).x = (float) (scaleFactorRoomsx * H.rooms.get(i).vertex.get(j).x + shiftFactorRoomsx);
			    H.rooms.get(i).vertex.get(j).y = (float) (scaleFactorRoomsy * H.rooms.get(i).vertex.get(j).y + shiftFactorRoomsy);

			    writeString += H.rooms.get(i).vertex.get(j).x + " " + H.rooms.get(i).vertex.get(j).y + " ";
			    H.rooms.get(i).vertex.get(j).x = (float) (H.rooms.get(i).vertex.get(j).x / (double) res);
			    H.rooms.get(i).vertex.get(j).y = (float) (H.rooms.get(i).vertex.get(j).y / (double) res);

			}
			writeString += "\n";
			out.write(writeString);
			i++;
		    }
		}
	    }
	    out.close();

	    // Toast.makeText(getApplicationContext(), "Rooms succesfully created",
	    // Toast.LENGTH_SHORT).show();

	    // System.out.println("Rooms Processed");

	    Log.d("NUMROOMS", "rooms Length: " + H.rooms.size());

	    Log.d(Constants.TAG, "MapIO::parseRooms() Exiting.");
	}
    }

    /*
     * Gary Debug: Output the vertices for the specific room
     */
    private static void writeCurrnetRoomVertices(int mapNumber, String roomName, ArrayList<PointF> vertexList) throws IOException
    {
	// Setup the file for output
	File root = Environment.getExternalStorageDirectory();
	if (root.canWrite())
	{
	    File outFile = new File(new File(root, "NavPalSaves/"), "floor_" + (mapNumber + 1) + "_" + roomName + "_vertices.txt");
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
	    String writeString = "";

	    // Write out the vertex list
	    for (int j = 0; j < vertexList.size(); j++)
	    {
		writeString += vertexList.get(j).x + " " + vertexList.get(j).y + " ";
	    }
	    writeString += "\nRoom Name: " + roomName + "\nNumber of Vertices: " + vertexList.size() + "\n";
	    out.write(writeString);

	    out.close();
	}
    }

    public static void printLog(ArrayList<Particle2D> A, String s, BufferedWriter out) throws IOException
    {
	out.write(s + "\n");
	for (Particle2D l : A)
	{
	    s = l.x + " " + l.y + "\n";
	    out.write(s);
	}

    }

    public static void saveExitsFromLandmarks(int CURRENTMAP, Map H, int scale, int res)
    {
	String writeString;
	File root = Environment.getExternalStorageDirectory();
	// saves landmark information
	if (root.canWrite())
	{
	    try
	    {
		File outFile = new File(new File(root, "NavPalSaves/"), MapList.MAPS[CURRENTMAP][4] + "_exit.txt");
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

		for (Landmark l : H.annotations.L)
		{
		    writeString = l.x * res + " " + l.y * res + " " + l.shortMes + "\n" + l.longMes + "\n";
		    out.write(writeString);
		}
		out.close();

	    }
	    catch (Exception e)
	    {
	    }

	    // Toast.makeText(getApplicationContext(), "Exits Saved",
	    // Toast.LENGTH_SHORT).show();
	} // LOAD MENU
    }

    public static void saveLandmarks(int CURRENTMAP, Map H, int scale, int res) throws IOException
    {
	String writeString;
	File root = Environment.getExternalStorageDirectory();
	if (root.canWrite())
	{
	    File outFile = new File(new File(root, "NavPalSaves/"), MapList.MAPS[CURRENTMAP][4] + "_lmrk.txt");
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

	    for (Landmark l : H.annotations.L)
	    {
		writeString = l.x * res + " " + l.y * res + " " + l.shortMes + "\n" + l.longMes + "\n";
		out.write(writeString);
	    }
	    out.close();
	}
    }

    public static void saveMap(int CURRENTMAP, Map H, int scale, int res)
    {
	Log.d(Constants.TAG, "MapIO::saveMap() Called.");

	// ATTEMPTS TO SAVE GENERATED MAP
	String writeString;
	File root = Environment.getExternalStorageDirectory();
	if (root.canWrite())
	{
	    File outFile = new File(new File(root, "NavPalSaves/"), MapList.MAPS[CURRENTMAP][4] + "_map.txt");
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
		FileWriter mapwriter = new FileWriter(outFile);
		BufferedWriter out = new BufferedWriter(mapwriter);
		writeString = H.getSizeX() + " " + H.getSizeY() + " " + scale + " " + res;
		out.write(writeString);
		for (int i = 0; i < H.getSizeX(); i++)
		{
		    writeString = "\n";

		    for (int j = 0; j < H.getSizeY(); j++)
		    {
			if (H.node[i][j].set == 'X')
			{
			    writeString += 'X' + " ";
			}
			else
			{
			    writeString += 'O' + " ";
			}
		    }
		    out.write(writeString);
		}
		out.close();

	    }
	    catch (Exception e)
	    {
		System.out.println("Generated map but failed to save");
	    }
	}

	Log.d(Constants.TAG, "MapIO::saveMap() Exiting.");
    }

    public static void saveObstacles(int CURRENTMAP, Map H, int scale, int res) throws IOException
    {
	String writeString;
	File root = Environment.getExternalStorageDirectory();
	if (root.canWrite())
	{
	    File outFile = new File(new File(root, "NavPalSaves/"), MapList.MAPS[CURRENTMAP][4] + "_obs.txt");
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
	    for (Obstacle l : H.annotations.O)
	    {
		writeString = l.x * res + " " + l.y * res + " " + l.size + " " + l.shortMes + "\n" + l.longMes + "\n";
		out.write(writeString);
	    }
	    out.close();
	}

    }

    public static void saveSector(int CURRENTMAP, Map H, int scale, int res) throws IOException
    {
	File root = Environment.getExternalStorageDirectory();
	String writeString;
	if (root.canWrite())
	{
	    File outFile = new File(new File(root, "NavPalSaves/"), MapList.MAPS[CURRENTMAP][4] + "_sector.txt");
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

	    System.out.println("Is Saving Sector");

	    writeString = H.getSizeX() + " " + H.getSizeY() + " " + scale + " " + res;
	    // System.out.println(writeString + " Printed");
	    out.write(writeString);
	    for (int i = 0; i < H.getSizeX(); i++)
	    {
		writeString = "\n";

		for (int j = 0; j < H.getSizeY(); j++)
		{
		    writeString += H.RoomsMap[i][j] + " ";

		}
		out.write(writeString);

	    }
	    out.close();

	}
    }
}
