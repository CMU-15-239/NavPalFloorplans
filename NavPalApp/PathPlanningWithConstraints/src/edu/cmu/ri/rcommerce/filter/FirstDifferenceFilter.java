package edu.cmu.ri.rcommerce.filter;

import java.util.ArrayList;
/**
 * A simple first difference filter meant for online use.
 * @author Nisarg
 *
 */
public class FirstDifferenceFilter
{
	private ArrayList<Double> filtered;
	
	public FirstDifferenceFilter() 
	{
		filtered = new ArrayList<Double>();
	}
	
	public void addValues(double[] newValues) 
	{
		for (int i = 0; i<newValues.length; i++)
			filtered.add(newValues[i]);
	};
	
	
	public double[] readFiltered()
	{
		if (filtered.size() < 2)
			return new double[0];
		else
		{
			double lastVal = 0;
			if (filtered.size() %2 == 1)
				lastVal = filtered.get(filtered.size()-1);
			double[] out = new double[filtered.size()-1];
			for (int i = 0 ; i<filtered.size()-1 ; i++)
				out[i] = filtered.get(i+1) - filtered.get(i);
			filtered.clear();
			if (lastVal != 0)
				filtered.add(lastVal);
			return out;
		}
	}

}
