/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.cmu;

import android.graphics.Canvas;
import java.util.LinkedList;
import android.graphics.Paint;
import android.util.FloatMath;

/**
 * 
 * @author Chet
 */
public class GridPath
{
    // The solution path.
    LinkedList<Node> L = new LinkedList<Node>();
    int length;

    public GridPath()
    {
	length = 1;
    }

    public GridPath(int a)
    {
	length = a;
    }

    public boolean establishPath(Node start, Node end)
    {
	Node n = start;
	L.clear();
	while (n != null)
	{

	    L.add(n);
	    if (n == end)
	    {
		return true;
	    }
	    n = n.p;

	}
	L.clear();
	return false;
    }

    public void clear()
    {
	L.clear();
    }

    /*
     * This gives the path length in grid units. To get the length in meters, multiply by the scaling of the map. This can be derived from MapList.mTrans. The localization gives coordinates in decimeters. The matrix in mTrans can be derived
     * by overlaying the images of the robot map used in localization and the map file and then rotating, scaling, and shifting accordingly.
     */
    public float pathLength()
    {
	float length = 0;
	float sqrt2 = FloatMath.sqrt(2);
	for (int i = 0; i < L.size() - 1; i++)
	{
	    if (L.get(i).x == L.get(i + 1).x || L.get(i).y == L.get(i + 1).y)
	    {
		length += 1.0;
	    }
	    else
	    {
		length += sqrt2;
	    }

	}
	return length;
    }

    public int nodesInPath()
    {
	return L.size();
    }

    /** Prints nodes in path. This needs adapted to multi-floor */
    public void printPath()
    {
	for (int i = 0; i < L.size(); i++)
	{
	    System.out.print("(" + L.get(i).x + "," + L.get(i).y + ") ");
	}
	System.out.println();
    }

    public LinkedList<Node> getPath()
    {
	return L;
    }
}
