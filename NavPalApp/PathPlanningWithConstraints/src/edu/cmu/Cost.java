/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.cmu;

/**
 * 
 * @author Chet
 */
public class Cost
{
    /**
     * Very little has actually been done with this. The cost is already considered when path planning, but it is currently the same for all nodes. Some work has been done in making narrow hallways have a higher cost, but that is it. This
     * is done by making squares next to the wall have a high cost, and squares next to that getting progressively lower.
     */
    int sizeRestraint;
    boolean useSizeRestraint = true;

    public Cost()
    {
	sizeRestraint = 0;
    }

    public int evalCost()
    {
	if (useSizeRestraint)
	{
	    return sizeRestraint + 1;
	}

	return -1;
    }

}
