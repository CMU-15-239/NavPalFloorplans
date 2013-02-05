package edu.cmu.ri.rcommerce.sensor;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import edu.cmu.ri.rcommerce.Messages.AccelInfo;
import edu.cmu.ri.rcommerce.Messages.MessageWrapper;

public class LogPlaybackTimeSource {
	private long startTime;
	private long currentTime;
	/**
	 * Synchronizes log playback between different consumers of log data
	 * @param source The log from which to get the starting time. Uses the first accel reading to establish the time.
	 * @param incrementCount The number of milliseconds to increment the simulation every tick
	 * @throws FileNotFoundException 
	 */
	public LogPlaybackTimeSource(String source) throws IOException
	{
		InputStream in = new FileInputStream(source);
		
		while(true)
		{
			MessageWrapper wrap;
			
				try {
					wrap = MessageWrapper.parseDelimitedFrom(in);
				} catch (com.google.protobuf.InvalidProtocolBufferException e) {
					throw new RuntimeException("corrupted log file");
				}
				if (wrap == null)
					throw new RuntimeException("empty log file or no accelerometer readings");
				if (wrap.hasAccelInfo())
				{
					AccelInfo scan = wrap.getAccelInfo();
					startTime = scan.getTimestamp();
					currentTime = startTime;
					break;
				}
			}
	}
	
	public void incrementTime(int milliseconds)
	{
 		currentTime += milliseconds*1e6; //timestamps are maintained in nanoseconds
	}
	public long getCurrentTime()
	{
		return currentTime;
	}
	
	public long getStartingTime()
	{
		return startTime;
	}
	

}
