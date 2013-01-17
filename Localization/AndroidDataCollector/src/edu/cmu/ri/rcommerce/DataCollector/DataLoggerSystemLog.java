package edu.cmu.ri.rcommerce.DataCollector;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.Locale;

import android.util.Log;

/*
 * The purpose of this class is to create a log file and append to it to record the various states
 * of the DataLogger.
 */

public class DataLoggerSystemLog
{
	public static enum Severity {NORMAL, WARNING, ERROR}

	private String _logFilename = "system.log";
	private String _logFilePath;
	private File   _logFileHandle;
	
	// Constructors
	
	public DataLoggerSystemLog(String logFilePath)
	{
		_logFilePath = logFilePath;
		
		// If the file does not already exist, create the log file
		createNewSystemLogFile();
	}
	
	public DataLoggerSystemLog(String logFilename, String logFilePath)
	{
		_logFilename = logFilename;
		_logFilePath = logFilePath;

		// If the file does not already exist, create the log file
		createNewSystemLogFile();
	}

	/*
	 * TODO: Log when the user changes the log file attributes and when logging starts and stops.
	 * TODO: Add a flag that is written to an output file. If the app suddenly stops and starts again and this
	 * 		 flag is set, it will tell the DataLogger app to resume logging. Also, make a note of it in the 
	 * 		 system log.
	 */	
	
	// Public Methods
	public void appendToLogFile(Calendar timestamp, Severity severity, String statusMsg) 
	{
		try
		{
			String formattedTimeStamp = formatTimeStamp(timestamp);
		    BufferedWriter out = new BufferedWriter(new FileWriter(_logFilePath + "/" + _logFilename, true));
		    out.write(formattedTimeStamp + " | " + severity.toString() + " | " + statusMsg + "\n");
		    out.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	// Private methods
	public static String formatTimeStamp(Calendar c)
	{
	    return String.format("%04d-%02d-%02d %02d:%02d:%02d",
	    		c.get(Calendar.YEAR),
	    		c.get(Calendar.MONTH) + 1,
	    		c.get(Calendar.DAY_OF_MONTH),
	    		c.get(Calendar.HOUR_OF_DAY),
	    		c.get(Calendar.MINUTE),
	    		c.get(Calendar.SECOND));		
	}

	private void createNewSystemLogFile()
	{
		_logFileHandle = new File(_logFilePath + "/" + _logFilename);

		// If the log file does not exist
		if (!_logFileHandle.exists())
		{
			try
			{
				_logFileHandle.createNewFile();
			} 
			catch (IOException e)
			{
		         e.printStackTrace();
			}
		}
	}
	
	//private decode
}
