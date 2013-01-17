package edu.cmu.ri.rcommerce.filter;

import static org.junit.Assert.assertEquals;

import org.junit.Test;


public class FirstDifferenceFilterTest{
	
	void checkEqualsDoubleArray(double[] expected, double[] actual)
	{
		assertEquals(expected.length,actual.length);
		for(int i = 0 ; i<expected.length; i++)
			assertEquals(expected[i],actual[i],.00001);
	}
	
	@Test
	public void simple()
	{
		FirstDifferenceFilter filter = new FirstDifferenceFilter();
		double[] values ={1.0, 5.0,6.0};
		
		filter.addValues(values);
		
		double[] out = filter.readFiltered();
		double[] expected = {4.0,1.0};
		
		checkEqualsDoubleArray(expected,out);
	}

}
