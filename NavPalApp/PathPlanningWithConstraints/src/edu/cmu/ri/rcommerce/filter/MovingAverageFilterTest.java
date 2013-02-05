package edu.cmu.ri.rcommerce.filter;

import static org.junit.Assert.assertEquals;

import org.junit.Test;


public class MovingAverageFilterTest {
	
	void checkEqualsDoubleArray(double[] expected, double[] actual)
	{
		assertEquals(expected.length,actual.length);
		for(int i = 0 ; i<expected.length; i++)
			assertEquals(expected[i],actual[i],.00001);
	}
	
	
	@Test
	public void trivial()
	{
		MovingAverageFilter filter = new MovingAverageFilter(2);
		double[] values ={1.0, 2.0};
		
		filter.addValues(values);
		
		double[] out = filter.readFiltered();
		double[] expected = {1.5};
		
		checkEqualsDoubleArray(expected,out);
	}
	
	
	@Test
	public void easy()
	{
		MovingAverageFilter filter = new MovingAverageFilter(11);
		double[] values ={-5.0, -4.0, -3.0, -2.0, -1.0, 0.0, 1.0, 2.0, 3.0, 4.0, 5.0};
		
		filter.addValues(values);
		
		double[] out = filter.readFiltered();
		double[] expected = {0};
		
		checkEqualsDoubleArray(expected,out);
	}
	
	@Test
	public void multipleOut()
	{
		MovingAverageFilter filter = new MovingAverageFilter(2);
		double[] values ={1.0, 2.0, 3.0, 4.0, 5.0};
		
		filter.addValues(values);
		
		double[] out = filter.readFiltered();
		double[] expected = {1.5,2.5,3.5,4.5};
		
		checkEqualsDoubleArray(expected,out);
	}
	
	@Test
	public void multipleIn()
	{
		MovingAverageFilter filter = new MovingAverageFilter(2);
		double[] values1 ={1.0, 2.0};
		double[] values2 ={3.0, 4.0, 5.0};
		
		filter.addValues(values1);
		filter.addValues(values2);
		
		double[] out = filter.readFiltered();
		double[] expected = {1.5,2.5,3.5,4.5};
		
		checkEqualsDoubleArray(expected,out);
	}
	
	@Test
	public void multipleInOut()
	{
		MovingAverageFilter filter = new MovingAverageFilter(2);
		double[] values1 ={1.0, 2.0};
		double[] values2 ={3.0, 4.0, 5.0};
		
		filter.addValues(values1);
		
		double[] out1 = filter.readFiltered();
		double[] expected1 = {1.5};
		checkEqualsDoubleArray(expected1,out1);
		
		filter.addValues(values2);
		
		double[] out2 = filter.readFiltered();
		double[] expected2 = {2.5,3.5,4.5};
		checkEqualsDoubleArray(expected2,out2);
	}
	
	@Test
	public void readFilteredTwice()
	{
		MovingAverageFilter filter = new MovingAverageFilter(2);
		double[] values ={1.0, 2.0, 3.0, 4.0, 5.0};
		
		filter.addValues(values);
		
		double[] out1 = filter.readFiltered();
		double[] expected1 = {1.5,2.5,3.5,4.5};
		checkEqualsDoubleArray(expected1,out1);
		
		double[] out2 = filter.readFiltered();
		double[] expected2 = {};
		checkEqualsDoubleArray(expected2,out2);
	}

}
