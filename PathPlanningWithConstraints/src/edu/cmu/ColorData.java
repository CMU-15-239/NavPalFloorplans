/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.cmu;

/**
 * 
 * @author Chet
 */
public class ColorData
{
    public int i;
    // The int is the color
    public float d;

    // the float is its percentage in the bitmap.
    public ColorData()
    {
	i = 0;
	d = 0.0f;
    }

    public ColorData(int a, float b)
    {
	i = a;
	d = b;
    }

}
