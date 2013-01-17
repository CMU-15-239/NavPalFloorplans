package edu.cmu.ri.rcommerce.filter;

import java.util.ArrayList;

/**
 * An online peak detector that accumulates data and looks for a peak when requested.
 * 
 * Reports a peak when 'cooldown' amount of milliseconds have passed since the last peak
 * and the current value in the buffer is above the threshold.
 * 
 * @author Nisarg
 */
public class SingleThresholdPeakDetector
{
	private ArrayList<Double> buffer;
	private double threshold;
	private long cooldown, lastPeakTime;
	
	// cooldown is in milliseconds
	public SingleThresholdPeakDetector(double threshold, long cooldown) 
	{
		this.threshold = threshold;
		this.cooldown = cooldown;
		buffer = new ArrayList<Double>();
	}
	
	public void addValues(double[] newValues) 
	{	
		long time = System.currentTimeMillis();
		//if still in cooldown, don't accumulate values
		if (lastPeakTime + cooldown > time)
			return;
		for (int i = 0 ; i <newValues.length ; i++)
			buffer.add(newValues[i]);
	};
	
	
	/*When called, it will look in the buffer until it finds a peak.
	It will then remove all the values up to the peak and return true.
	If it can not find a peak, it will return false */
	public boolean foundPeak()
	{
		long time = System.currentTimeMillis();
		for (int i = 0 ; i<buffer.size(); i++)
		{
			if (buffer.get(i) >= threshold & lastPeakTime + cooldown <= time)
			{
				lastPeakTime = time;
				buffer.subList(0, i).clear();
				return true;
			}
		}
		
		return false;
		
	}

}
