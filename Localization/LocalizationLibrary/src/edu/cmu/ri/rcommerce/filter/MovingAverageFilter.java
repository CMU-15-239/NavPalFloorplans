package edu.cmu.ri.rcommerce.filter;

import java.util.ArrayList;

/**
 * A moving average filter meant for online use.
 * 
 * You can add many points at once, and it does not 'lose' old averages.
 */
public class MovingAverageFilter
{
	private ArrayList<Double> values;
	private ArrayList<Double> filtered;
	private int filterSize;
	
	public MovingAverageFilter(int filterSize) 
	{
		this.filterSize = filterSize;
		values = new ArrayList<Double>();
		filtered = new ArrayList<Double>();
	}
	
	//adds the values and computes the corresponding moving average points
	public void addValues(double[] newValues) 
	{
		for (int i = 0 ; i < newValues.length ; i++)
			values.add(newValues[i]);
		int size = values.size();
		
		if (size < filterSize) //haven't accumulated enough values yet
			return;
		
		double acc = 0.0;
		for (int i = 0 ; i < filterSize ; i++)
		{
			acc += values.get(i);
		}
		filtered.add(acc/filterSize);
		
		for (int i = filterSize ; i < size ; i++)
		{
			acc -= values.get(i-filterSize);
			acc += values.get(i);
			
			filtered.add(acc/filterSize);
		}
		
		//Remove the values that we will no longer need, since the filter window is past them
		if (size == filterSize)
			values.remove(0);
		else
			values.subList(0, size - filterSize -1).clear();
		
	};
	
	
	public double[] readFiltered()
	{
		double[] out = new double[filtered.size()];
		for (int i = 0 ; i<filtered.size() ; i++)
			out[i] = filtered.get(i);
		filtered.clear();		
		return out;
	}

}
