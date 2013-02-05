package edu.cmu;

import java.util.ArrayList;
import java.util.Vector;

import android.util.Log;

public class Building implements Vertex
{

    ArrayList<Map> floor = new ArrayList<Map>();
    String name;

    public Building()
    {

    }

    public void initialize(MainInterface C, String S)
    {
	Log.d(Constants.TAG, "Building::initialize(" + C.getClass().getName() + ", " + S + ") Called");

	name = S + " Hall";

	for (int i = 0; i < MapList.MAPS.length; i++)
	{
	    Log.d(Constants.TAG, "initialize() - Checking Floor Plan '" + MapList.MAPS[i][0] + "'");

	    if (MapList.MAPS[i][0].substring(0, S.length()).equals(S))
	    {
		Log.d(Constants.TAG, "initialize() - Found a match with Floor Plan '" + MapList.MAPS[i][0] + "'. Loading...");

		floor.add(C.loadMap(i));

		Log.d(Constants.TAG, "initialize() - Found a match with Floor Plan '" + MapList.MAPS[i][0] + "'");

		// System.out.println("Loading " + MapList.MAPS[i][0] + " with ID: " + i);

		floor.get(i).id = i;
		floor.get(i).B = this;

	    }
	    // break;//will be removed
	}

	linkExits();

    }

    /* This guy compares all exits in all maps in the same building to see where the connections are */
    public void linkExits()
    {
	for (int i = 0; i < floor.size(); i++)
	    for (int j = floor.size() - 1; j > i; j--)
	    {
		for (int k = 0; k < floor.get(i).annotations.E.size(); k++)
		{
		    floor.get(i).annotations.E.get(k).M = floor.get(i);// sets the map of the exit
		    for (int l = 0; l < floor.get(j).annotations.E.size(); l++)
		    {
			// if both labels are the same and it isn't an exit, a link is formed
			if (floor.get(i).annotations.E.get(k).shortMes.equals(floor.get(j).annotations.E.get(l).shortMes))
			{// same label
			    if (!floor.get(i).annotations.E.get(k).longMes.equals("Exit"))
			    {// is not an exit
				if (!floor.get(j).annotations.E.get(l).longMes.equals("Exit"))
				{
				    // same linkage
				    if (floor.get(i).annotations.E.get(k).longMes.equals(floor.get(j).annotations.E.get(l).longMes))
				    {
					floor.get(i).annotations.E.get(k).linkedExit = floor.get(j).annotations.E.get(l);
					floor.get(j).annotations.E.get(l).linkedExit = floor.get(i).annotations.E.get(k);

				    }
				}
			    }
			}
		    }
		}

	    }

    }

    @Override
    public Vector<? extends Edge> getEdges()
    {
	// Uhhh....these would be roads or something like that.
	return null;
    }

    @Override
    public Vertex getSubVertex()
    {
	// this is going to be rooms
	return null;
    }

    @Override
    public int getHierarchy()
    {
	return 2;
    }

    @Override
    public float getCumulativeCost()
    {
	// the cost of crossing...a building
	return 0;
    }

    @Override
    public void setCumulativeCost(float a)
    {
	// TODO Auto-generated method stub

    }

    @Override
    public float getCost()
    {
	// TODO Auto-generated method stub
	return 0;
    }

    @Override
    public boolean getEnroute()
    {
	// TODO Auto-generated method stub
	return false;
    }

    @Override
    public void setEnroute(boolean a)
    {
	// TODO Auto-generated method stub

    }

    @Override
    public Vertex getParent()
    {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public void setParent(Vertex a)
    {
	// TODO Auto-generated method stub

    }

    @Override
    public Vertex raise()
    {
	return this;// if more hierarchies were supported, this would be a city or something
    }

    @Override
    public String toString()
    {
	return name;

    }
}
