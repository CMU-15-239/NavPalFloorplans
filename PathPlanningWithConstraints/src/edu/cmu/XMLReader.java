/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.cmu;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.os.Environment;
import android.util.Log;

public class XMLReader
{
    private int numRooms = 0;
    // No generics
    private ArrayList<Room> myRooms = new ArrayList<Room>(0);
    private ArrayList<Point> myGlyphs = new ArrayList<Point>(0);
    Document dom;

    public int numDetectedRooms = 0;
    public int numDetectedHallways = 0;

    public XMLReader()
    {
	//Log.d(Constants.TAG, "XMLReader::XMLReader Instantiated!");
	myRooms = new ArrayList<Room>();
	myGlyphs = new ArrayList<Point>(0);
	//Log.d(Constants.TAG, "XMLReader::XMLReader Exiting!");
    }

    public void runExample(InputStream file)
    {
	//Log.d(Constants.TAG, "XMLReader::runExample Called!");
	// parse the xml file and get the dom object
	parseXmlFile(file);

	// get each employee element and create a Employee object
	parseDocument();

	// Iterate through the list and print the data
	// printData();
	//Log.d(Constants.TAG, "XMLReader::runExample Exiting!");
    }

    private void parseXmlFile(InputStream file)
    {
	//Log.d(Constants.TAG, "XMLReader::parseXmlFile(" + file + ") Called!");

	// get the factory

	//Log.d(Constants.TAG, "XMLReader::parseXmlFile - Creating a new instance of the Document Builder using a Factory");
	DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

	try
	{
	    // Using factory get an instance of document builder
	    //Log.d(Constants.TAG, "XMLReader::parseXmlFile - Creating a new Document Builder.");
	    DocumentBuilder db = dbf.newDocumentBuilder();

	    // parse using builder to get DOM representation of the XML file
	    //Log.d(Constants.TAG, "XMLReader::parseXmlFile - Attempting to parse the file.");
	    // This line causes the program to crash when attempting to read the SVG file, which is in XML format.
	    dom = db.parse(file);
	    //Log.d(Constants.TAG, "XMLReader::parseXmlFile - Finished parsing the file.");
	}
	catch (ParserConfigurationException pce)
	{
	    Log.d(Constants.TAG, "ParserConfigurationException MSG: " + pce.getMessage());

	    pce.printStackTrace();
	}
	catch (SAXException se)
	{
	    Log.d(Constants.TAG, "SAXException MSG: " + se.getMessage());

	    se.printStackTrace();
	}
	catch (IOException ioe)
	{
	    Log.d(Constants.TAG, "IOException MSG: " + ioe.getMessage());

	    ioe.printStackTrace();
	}
	catch (Exception e)
	{
	    Log.d(Constants.TAG, "Exception MSG: " + e.getMessage());
	    e.printStackTrace();
	}

	//Log.d(Constants.TAG, "XMLReader::parseXmlFile() Exiting!");
    }

    private void parseDocument()
    {
	//Log.d(Constants.TAG, "XMLReader::parseDocument() Called!");

	// get the root elememt
	Element docEle = dom.getDocumentElement();

	// get a nodelist of <employee> elements
	NodeList nl = docEle.getElementsByTagName("path");
	if (nl != null && nl.getLength() > 0)
	{
	    for (int i = 0; i < nl.getLength(); i++)
	    {// System.out.println(i);

		// get the employee element
		Element el = (Element) nl.item(i);

		// get the Employee object
		makeRoom(el);

	    }

	    //Log.d("NUMROOMS", "numRooms = " + numDetectedRooms + " " + "numHallways = " + numDetectedHallways);
	}
	// This will read in the numbers. This is possibly useful for giving the rooms their correct numbers.
	// Find all of the glyphs within the room using it's coordinates and the ID designated by roomsMap.
	// Organize their coordinates from left to right and translate the filename to what number they are.
	// These numbers from left to right are the room number.

	/*
	 * NodeList glyphs = docEle.getElementsByTagName("use"); if (nl != null && glyphs.getLength() > 0) { for (int i = 0; i < glyphs.getLength(); i++) {
	 * 
	 * //get the employee element Element el = (Element) glyphs.item(i); //System.out.println(el.getAttribute("id")); String x = el.getAttribute("x"); String y = el.getAttribute("y");
	 * 
	 * //Create a new Employee with the value read from the xml nodes
	 * 
	 * myGlyphs.add(new Point((int)Double.parseDouble(x), (int)(Double.parseDouble(y))));
	 * 
	 * } }
	 */

	//Log.d(Constants.TAG, "XMLReader::parseDocument() Exiting!");
    }

    /**
     * This may need changed on a case by case basis. For example, the tag ##!## below shows a line that sets the label to hallway. On NSH floor 1, it should be set to hallway, but on floor 2, it should be set to room. You may have to look
     * at the actual SVG file when setting this up in order for it to parse correctly.
     */
    private void makeRoom(Element empEl)
    {
	//Log.d(Constants.TAG, "XMLReader::makeRoom() Called!");

	// for each <employee> element get text or int values of
	// name ,id, age and name
	numRooms++;
	String id = empEl.getAttribute("id");
	if (id.equals(""))
	{
	    id = "tag" + numRooms;
	}
	String d = empEl.getAttribute("d");
	String type = empEl.getAttribute("style");
	String transform = empEl.getAttribute("transform");
	// add it to list
	Room e = null; // new Room();

	// Create a new Employee with the value read from the xml nodes
	if (type.contains("fill:#ff0000")// RED ROOMS
		|| type.contains("fill:rgb(100%,0%,0%)"))
	{
	    e = new Room("Room", id, d, type, transform);
	    // System.out.println(id);
	    // add it to list
	    numDetectedRooms++;
	    myRooms.add(e);
	} // Create a new Employee with the value read from the xml nodes
	else
	    if (type.contains("fill:#ffff00")// YELLOW ROOMS
		    || type.contains("fill:rgb(100%,100%,0%)"))
	    {
		e = new Room("Room", id, d, type, transform);
		// System.out.println(type);
		// add it to list
		numDetectedRooms++;
		myRooms.add(e);
	    }
	    else
		if (type.contains("fill:#"))
		{// ANY OTHER COLOR
		    e = new Room("Room", id, d, type, transform);
		    // System.out.println(type);
		    // add it to list
		    numDetectedRooms++;
		    myRooms.add(e);
		}
		else
		    if (type.contains("stroke:#00ff00"))
		    {// A ROOM BORDER
		    }
		    else
			if (type.contains("fill:none") && type.contains("stroke-width:0.4"))
			{
			    // ROOMS FILLED WITH DIAGONAL LINES
			    // System.out.println(type);
			    if (type.contains("stroke:rgb(25.097656%,39.607239%,92.156982%)"))
			    {
				// System.out.println("true");
				e = new Room("Hallway", id, d, type, transform);
			    }
			    else
			    {
				// System.out.println("false");
				e = new Room("Hallway", id, d, type, transform);
				/** ##!## */
			    }// add it to list
			    numDetectedHallways++;
			    myRooms.add(e);
			}

	// if (e != null)
	// {
	// try
	// {
	// if ((e.getColor() == "Room") || (e.getColor() == "Hallway"))
	// {
	// writeRawRoomData(e);
	// }
	// }
	// catch (IOException e1)
	// {
	// // TODO Auto-generated catch block
	// e1.printStackTrace();
	// }
	// }

	// System.out.println(e.getLabel());
	//Log.d(Constants.TAG, "XMLReader::makeRoom() Exiting!");
    }

    /*
     * Gary Debug: Output the vertices for the specific room
     */
    private void writeRawRoomData(Room e) throws IOException
    {
	// Setup the file for output
	File root = Environment.getExternalStorageDirectory();
	if (root.canWrite())
	{
	    File outFile = new File(new File(root, "NavPalSaves/"), "floor_" + (MainInterface.CURRENTMAPLOAD + 1) + "_" + e.getLabel() + "_RoomObj.txt");
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
	    String writeString = "" + e.getColor() + "\n";
	    writeString += "Style: " + e.getType() + "\n";
	    writeString += "d: " + e.getd() + "\n";
	    writeString += "Label: " + e.getLabel() + "\n";
	    writeString += "Transform: " + e.getTransform() + "\n";

	    out.write(writeString);
	    out.close();
	}
    }

    public ArrayList<Room> getRooms()
    {
	Log.d(Constants.TAG, "XMLReader::getRooms() Called!");

	Log.d("NUMROOMS", "myRooms Length: " + myRooms.size());

	for (Room R : myRooms)
	{
	    R.findVertices();
	}

	Log.d(Constants.TAG, "XMLReader::getRooms() Exiting!");

	return myRooms;
    }

    public ArrayList<Point> getGlyphs()
    {
	Log.d(Constants.TAG, "XMLReader::getGlyphs() Called!");

	Log.d(Constants.TAG, "XMLReader::getGlyphs() Exiting!");
	return myGlyphs;
    }

    /**
     * Iterate through the list and print the content to console
     */
    @SuppressWarnings("unused")
    private void printData()
    {
	Log.d(Constants.TAG, "XMLReader::printData() Called!");

	Iterator<Room> it = myRooms.iterator();
	int count = 0;
	while (it.hasNext())
	{
	    count++;
	    System.out.println("Room " + count + "\n" + it.next().toString());

	}

	Log.d(Constants.TAG, "XMLReader::printData() Exiting!");
    }
}
