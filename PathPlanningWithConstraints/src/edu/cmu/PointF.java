/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.cmu;

/**
 * 
 * @author Chet
 */
public class PointF
{
    float x, y;

    public PointF()
    {
	x = 0;
	y = 0;
    }

    public PointF(float a, float b)
    {
	x = a;
	y = b;
    }

    // Added by Gary
    public String toString()
    {
	return "(" + x + "," + y + ")";    
    }
}
