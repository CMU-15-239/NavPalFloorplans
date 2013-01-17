package edu.cmu.ri.rcommerce.DataCollector;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Calendar;

import android.os.Environment;
import android.util.Log;

public class DataLogFileAttributes
{
	private static char SPACE = ' ';
	private static char UNDERSCORE = '_';
	private static String LOG_FILE_ATTRIBUTES_FILENAME = "logFileAttributes.txt";
	private static String NO_CHOICE_MADE = "(none)";
	private static String DELIMITER = ",";
	private static String FALSE = "false";

	private static String _strPhoneLocation;
	private static String _strPhoneId;
	private static String _strPhoneOrientation;	

	private static int _itemIndexPhoneLocation;
	private static int _itemIndexPhoneId;
	private static int _itemIndexPhoneOrientation;
	private static int _stopLoggingHour;
	private static int _stopLoggingMinute;
	
	private static boolean _automaticLogFileCreation;
	private static boolean _isLogging;
	private static String  _currentLogFilename;

	private static boolean _isPhoneLocationValid;
	private static boolean _isPhoneIdValid;
	private static boolean _isPhoneOrientationValid;

	public DataLogFileAttributes()
	{
		setPhoneLocation("", 0);
		setPhoneId("", 0);
		setPhoneOrientation("", 0);

		_stopLoggingHour = 8;
		_stopLoggingMinute = 0;

		_isPhoneLocationValid	  = false;
		_isPhoneIdValid			  = false;
		_isPhoneOrientationValid  = false;
		_automaticLogFileCreation = false;
		_isLogging				  = false;
	}

	// Getters
	public String getPhoneLocation()
	{
		return _strPhoneLocation;
	}

	public int getPhoneLocationItemIndex()
	{
		return _itemIndexPhoneLocation;
	}

	public String getPhoneId()
	{
		return _strPhoneId;
	}

	public int getPhoneIdItemIndex()
	{
		return _itemIndexPhoneId;
	}

	public String getPhoneOrientation()
	{
		return _strPhoneOrientation;
	}

	public int getPhoneOrientationItemIndex()
	{
		return _itemIndexPhoneOrientation;
	}

	public int getStoploggingHour()
	{
		return _stopLoggingHour;
	}

	public int getStoploggingMinute()
	{
		return _stopLoggingMinute;
	}

	public String getCurrentLogFilename()
	{
		return _currentLogFilename;
	}
	
	public boolean isAutomaticLogFileCreationSet()
	{
		return _automaticLogFileCreation;
	}

	public boolean isLogging()
	{
		return _isLogging;
	}

	// Setters
	public void setPhoneLocation(String location, int itemIndex)
	{
		_itemIndexPhoneLocation = itemIndex;
		_strPhoneLocation = new String (removeWhiteSpaces(location));

		_isPhoneLocationValid = verifyAttributeIsValid(_strPhoneLocation);
	}

	public void setPhoneId(String id, int itemIndex)
	{
		_itemIndexPhoneId = itemIndex;
		_strPhoneId = new String (removeWhiteSpaces(id));

		_isPhoneIdValid = verifyAttributeIsValid(_strPhoneId);
	}

	public void setPhoneOrientation(String orientation, int itemIndex)
	{
		_itemIndexPhoneOrientation = itemIndex;
		_strPhoneOrientation = new String (removeWhiteSpaces(orientation));
		
		_isPhoneOrientationValid = verifyAttributeIsValid(_strPhoneOrientation);
	}

	public void setAutomaticLogFileCreation(boolean automaticLogFileCreation)
	{
		_automaticLogFileCreation = automaticLogFileCreation;
	}

	public void setIsLogging(boolean isLogging)
	{
		_isLogging = isLogging;
	}

	public void setStoploggingHour(int stopLoggingHour)
	{
		_stopLoggingHour = stopLoggingHour;
	}

	public void setStoploggingMinute(int stopLoggingMinute)
	{
		_stopLoggingMinute = stopLoggingMinute;
	}

	public void setCurrentLogFilename(String currentLogFilename)
	{
		_currentLogFilename = currentLogFilename;
	}
	
	public boolean isPhoneLocationValid()
	{
		return _isPhoneLocationValid;
	}

	public boolean isPhoneIdValid()
	{
		return _isPhoneIdValid;
	}
	
	public boolean isPhoneOrientationValid()
	{
		return _isPhoneOrientationValid;
	}
	
	public void clearCurrentLogFilename()
	{
		_currentLogFilename = null;
	}
	
	private String removeWhiteSpaces(String s)
	{
		s.trim();
		s.replace(SPACE, UNDERSCORE);

		return s.replace(SPACE, UNDERSCORE);
	}

	private boolean verifyAttributeIsValid(String s)
	{
		if (containsSpaces(s))
		{
			return false;
		}

		if (s.length() == 0)
		{
			return false;
		}

		if (s.equals(""))
		{
			return false;
		}

		if (s.equals(NO_CHOICE_MADE))
		{
			return false;
		}

		return true;
	}

	public void writeContentToDataLogAttributeFile() throws IOException
	{
		String pathOfLogFileAttributes = Environment.getExternalStorageDirectory() + "/logs/" + LOG_FILE_ATTRIBUTES_FILENAME;

		FileOutputStream fileOutputStream = new FileOutputStream(pathOfLogFileAttributes);			
		DataOutputStream out = new DataOutputStream(fileOutputStream);
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(out));

		bw.write(getPhoneLocationItemIndex() 	 + DELIMITER + getPhoneLocation() + "\n");
		bw.write(getPhoneIdItemIndex() 			 + DELIMITER + getPhoneId() + "\n");
		bw.write(getPhoneOrientationItemIndex()  + DELIMITER + getPhoneOrientation() + "\n");
		bw.write(isAutomaticLogFileCreationSet() + "\n");
		bw.write(isLogging() + "\n");
		bw.write(getStoploggingHour() + DELIMITER + getStoploggingMinute() + "\n");

		bw.close();	
	}

	public void readContentFromDatalogAttributeFile(String pathOfLogFileAttributes) throws FileNotFoundException, IOException, NumberFormatException
	{
		FileInputStream fileInputStream = new FileInputStream(pathOfLogFileAttributes);			
		DataInputStream in = new DataInputStream(fileInputStream);
		BufferedReader br = new BufferedReader(new InputStreamReader(in));

		String delims = "[" + DELIMITER + " ]+";
		String currentLine;
		String[] tokens;
		int currentItemIndex;
		String currentItemText;

		// Read the location spinner line and parse the text
		currentLine = br.readLine();
		tokens = currentLine.split(delims);
		currentItemIndex = Integer.parseInt(tokens[0]);
		currentItemText = tokens[1];
		setPhoneLocation(currentItemText, currentItemIndex);

		// Read the location spinner line and parse the text
		currentLine = br.readLine();
		tokens = currentLine.split(delims);
		currentItemIndex = Integer.parseInt(tokens[0]);
		currentItemText = tokens[1];
		setPhoneId(currentItemText, currentItemIndex);

		// Read the location spinner line and parse the text
		currentLine = br.readLine();
		tokens = currentLine.split(delims);
		currentItemIndex = Integer.parseInt(tokens[0]);
		currentItemText = tokens[1];
		setPhoneOrientation(currentItemText, currentItemIndex);

		currentLine = br.readLine();
		_automaticLogFileCreation = (currentLine.compareTo(FALSE) == 0) ? false : true;

		currentLine = br.readLine();
		_isLogging = (currentLine.compareTo(FALSE) == 0) ? false : true;

		// Read the stop logging time
		currentLine = br.readLine();
		tokens = currentLine.split(delims);
		setStoploggingHour(Integer.parseInt(tokens[0]));
		setStoploggingMinute(Integer.parseInt(tokens[1]));

		// Close the input stream
		in.close();
	}

	public static String formatNewLogFileName(Calendar c)
	{
	    return String.format("Log_%s_%s_%s_%04d_%02d_%02d-%02d_%02d_%02d",
	    		_strPhoneLocation,
	    		_strPhoneId,
	    		_strPhoneOrientation, 
	    		c.get(Calendar.YEAR),
	    		c.get(Calendar.MONTH) + 1,
	    		c.get(Calendar.DAY_OF_MONTH),
	    		c.get(Calendar.HOUR_OF_DAY),
	    		c.get(Calendar.MINUTE), 
	    		c.get(Calendar.SECOND));
	}

	public static String formatNextLogFileGenerationDateForView(Calendar c)
	{
		return String.format("%04d-%02d-%02d %02d:%02d",
				c.get(Calendar.YEAR),
	    		c.get(Calendar.MONTH) + 1,
	    		c.get(Calendar.DAY_OF_MONTH),
	    		c.get(Calendar.HOUR_OF_DAY),
	    		c.get(Calendar.MINUTE));
	}

	public String toString()
	{
		return "Phone Location: '"   	 + _strPhoneLocation +
   				"' Phone ID: '" 		 + _strPhoneId +
   				"' Phone Orientation: '" + _strPhoneOrientation +
   				"' Automatic Logging: '" + _automaticLogFileCreation +
   				"' Already Logging: " 	 + _isLogging +
   				"' Stop Logging Time: '" + _stopLoggingHour + ":" + _stopLoggingMinute + "'";
	}

	/*
	 * Helper function used by function validateDataLogFileAttributes  
	 */
	private boolean containsSpaces(String str)
	{
		return (str.indexOf(SPACE) == -1) ? false : true;
	}

	public boolean allAttributesValid()
	{
		return (_isPhoneLocationValid && _isPhoneIdValid && _isPhoneOrientationValid);
	}
}
