package edu.cmu.ri.rcommerce.filter;

import java.util.ArrayList;

import android.util.Log;


/**
 * An online peak detector that accumulates data and looks for a peak when requested.
 * 
 * Alternately waits for highThreshold and lowThreshold values and reports a peak when
 * 'cooldown' amount of milliseconds have passed and the current value in the buffer
 * is past the threshold.
 */
public class DoubleThresholdPeakDetector
{
	private ArrayList<Double> buffer;
	private double lowThreshold, highThreshold;
	private boolean waitingForLow = false; //otherwise, waiting for high
	private long cooldown, lastPeakTime;
	
	/**
	 * Create a peak detector filter.
	 * @param lowThreshold after the signal passes the high threshold, it waits for this threshold before reporting another peak
	 * @param highThreshold after the signal passes the low threshold, it waits for this threshold before reporting another peak
	 * @param cooldown the amount of time after a peak that additional peaks are suppressed.
	 */
	public DoubleThresholdPeakDetector(double lowThreshold, double highThreshold,long cooldown) 
	{
		this.lowThreshold = lowThreshold;
		this.highThreshold = highThreshold;
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
	
	
	/**Look in the buffer until it finds a peak.
	Remove all the values up to the peak and return true.
	If a peak cannot be found, return false */
	public boolean foundPeak()
	{
		long time = System.currentTimeMillis();
	
		for (int i = 0 ; i<buffer.size(); i++)
		{
			double val = buffer.get(i);
			if ((waitingForLow && val <= lowThreshold ||
					!waitingForLow && val >= highThreshold)
					
				&&  lastPeakTime + cooldown <= time)
			{
				Log.d("Pedloc","peak found with value: " + val);
				lastPeakTime = time;
				waitingForLow = !waitingForLow;
				buffer.subList(0, i).clear();
				return true;
			}
		}
		
		return false;
		
	}

}
