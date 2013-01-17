/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.cmu;

import java.util.ArrayList;
import java.util.Vector;

/**
 * 
 * @author Chet
 */
public class AnnotationsLayer
{

    ArrayList<Landmark> L = new ArrayList<Landmark>();
    ArrayList<Obstacle> O = new ArrayList<Obstacle>();
    Vector<Exit> E = new Vector<Exit>();
    private int currentID;

    public AnnotationsLayer()
    {
	currentID = 0;
    }

    public int assignID()
    {
	return currentID++;
    }

    public void clearAnnotations()
    {
	L.clear();
	O.clear();
	// E.clear();
    }

    public void clearAnnotations(ArrayList<Room> rooms)
    {

	L.clear();
	O.clear();
	// E.clear();
	for (Room R : rooms)
	{
	    R.ex.clear();
	    R.ob.clear();
	    R.lm.clear();
	}
	// signalChangedAnnotation();
    }

    public String[] getLandmarkTitles()
    {
	String s[] = new String[L.size()];
	for (int i = 0; i < L.size(); i++)
	{
	    s[i] = L.get(i).shortMes;
	}
	return s;

    }

    public void createAnnotation(int y, int x, int s, String srt, String lng, String typeAnnotation, Map M)
    {
	if (typeAnnotation.equals("Obstacle"))
	{
	    O.add(new Obstacle(x, y, s, srt, assignID(), M));

	    O.get(O.size() - 1).longMes = lng;

	}
	if (typeAnnotation.equals("Landmark"))
	{
	    L.add(new Landmark(x, y, srt, assignID(), M));
	    L.get(L.size() - 1).longMes = lng;
	}
    }

    public void createAnnotation(int y, int x, int s, String srt, String lng, String typeAnnotation, Room R, Map M)
    {
	if (typeAnnotation.equals("Obstacle"))
	{
	    O.add(new Obstacle(x, y, s, srt, assignID(), M));
	    R.ob.add(O.get(O.size() - 1));
	    // PR.signalChangedAnnotation(R.getID);
	    O.get(O.size() - 1).longMes = lng;

	}
	if (typeAnnotation.equals("Landmark"))
	{
	    L.add(new Landmark(x, y, srt, assignID(), M));
	    L.get(L.size() - 1).longMes = lng;
	    R.lm.add(L.get(L.size() - 1));
	    // PR.signalChangedAnnotation(R.getID);

	}
    }

    public void clearAnnotations(String s)
    {
	if (s.substring(0, 1).toUpperCase().equals("L"))
	{
	    L.clear();
	}
	if (s.substring(0, 1).toUpperCase().equals("O"))
	{
	    O.clear();
	}
	if (s.substring(0, 1).toUpperCase().equals("E"))
	{
	    E.clear();
	}
    }

    public Obstacle obstacleAt(int x, int y)
    {
	for (Obstacle k : O)
	{
	    for (int i = -1 * k.size; i <= k.size; i++)
	    {
		for (int j = -1 * k.size; j <= k.size; j++)
		{
		    if (Math.sqrt(Math.pow(i, 2) + Math.pow(j, 2)) < k.size)
		    {
			if (k.x + i == x && k.y + j == y)
			{
			    return k;
			}
		    }
		}
	    }
	}
	return null;
    }

    public void syncObstacles(Map F)
    {
	for (int i = 0; i < F.getSizeX(); i++)
	{
	    for (int j = 0; j < F.getSizeY(); j++)
	    {
		if (F.node[i][j].set == 'x')
		{
		    int u = F.youngestNeighbor(F.node[i][j]);
		    if (F.node[i][j].set == '1')
			F.OPEN.remove(F.node[i][j]);
		    if (F.node[i][j].set == '2')
			F.CLOSED.remove(F.node[i][j]);

		    F.node[i][j].set = '0';
		    if (u > 0 && u < Integer.MAX_VALUE)
		    {
			F.rollback(u);
			F.node[i][j].p = null;
		    }
		    // resets lists and grid
		    F.cleanUpOpen();
		}
	    }
	}

	for (Obstacle k : O)
	{
	    for (int i = -1 * k.size; i <= k.size; i++)
	    {
		for (int j = -1 * k.size; j <= k.size; j++)
		{
		    if (Math.sqrt(Math.pow(i, 2) + Math.pow(j, 2)) <= k.size) // F.node[k.x+i][k.y+j].set='x';
		    {
			try
			{
			    // converts input to coordinates
			    // if (F.node[k.x + i][k.y + j] != F.s && F.node[k.x + i][k.y + j] != F.t) {
			    if (F.node[k.x + i][k.y + j].set != 'X' && F.node[k.x + i][k.y + j].set != 'x')
			    {
				if (F.node[k.x + i][k.y + j].timeToOPEN > 0 && F.node[k.x + i][k.y + j].timeToOPEN < Integer.MAX_VALUE)
				{
				    F.rollback(F.node[k.x + i][k.y + j].timeToOPEN);
				    F.node[k.x + i][k.y + j].p = null;

				}
				if (F.node[i][j].set == '1')
				    F.OPEN.remove(F.node[i][j]);
				if (F.node[i][j].set == '2')
				    F.CLOSED.remove(F.node[i][j]);
				F.node[k.x + i][k.y + j].set = 'x';

				// resets lists and grid
				F.cleanUpOpen();

			    }

			    // }

			}
			catch (ArrayIndexOutOfBoundsException e)
			{// Off the grid
			}
		    }
		}
	    }
	}
    }

}