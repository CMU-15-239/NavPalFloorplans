package edu.cmu;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import android.os.Environment;

public class Constants
{
    public static String TAG = "PATH_PLANNER";
    public static String DSTAR_TAG = "DSTAR";
    public static String SINGLE_TAP = "SINGLE_TAP";
    public static String DOUBLE_TAP = "DOUBLE_TAP";
    public static String KEY = "key";

    public static void dump1DFloatArrayToFile(String outputFilename, float arr[])
    {
	if (arr == null)
	{
	    return;
	}
	
	if (arr.length == 0)
	{
	    return;
	}
	
	int count = arr.length;
	
	// Setup the file for output
	File root = Environment.getExternalStorageDirectory();
	if (root.canWrite())
	{
	    String writeString = "";
	    String filename = root.getPath() + "/NavPalSaves/" + outputFilename + ".txt";

	    try
	    {
		BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
		
		for (int i = 0; i<count; i++)
		{
		    writeString += String.format("%10.3f\n", arr[i]);
		}

		writer.write(writeString);
		writer.close();
	    }
	    catch (IOException e1)
	    {
		e1.printStackTrace();
	    }
	    catch(Exception e)
	    {
		e.printStackTrace();
	    }
	}
    }
    
    // For debugging purposes
    public static void dump2DFloatArrayToFile(String outputFilename, float arr[][])
    {
	if (arr == null)
	{
	    return;
	}
	
	if (arr.length == 0)
	{
	    return;
	}

	if (arr[0].length == 0)
	{
	    return;
	}
	
	int rows = arr.length;
	int cols = arr[0].length;
	
	// Setup the file for output
	File root = Environment.getExternalStorageDirectory();
	if (root.canWrite())
	{
	    String writeString = "";
	    String filename = root.getPath() + "/NavPalSaves/" + outputFilename + ".txt";

	    try
	    {
		BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
		
		for (int i = 0; i<rows; i++)
		{
		    writeString = "";
		    for (int j = 0; j<cols; j++)
		    {
			writeString += String.format("%10.3f ", arr[i][j]);
		    }
		    writeString += "\n";
		    writer.write(writeString);
		}

		writer.close();
	    }
	    catch (IOException e1)
	    {
		e1.printStackTrace();
	    }
	    catch(Exception e)
	    {
		e.printStackTrace();
	    }
	}
    }
}
