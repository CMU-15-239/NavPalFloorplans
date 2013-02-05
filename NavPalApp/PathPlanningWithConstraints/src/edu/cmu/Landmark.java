/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.cmu;

/**
 * 
 * @author Chet
 */

public class Landmark
{
    public int x;
    public int id;
    public int y;
    String type = new String();
    String shortMes = "";
    String longMes = "";

    public Landmark()
    {
    }

    public Landmark(int a, int b, String st, int id, Map M)
    {
	x = a;
	y = b;
	shortMes = st;
	this.id = id;
    }
}
