package edu.cmu;

public class User extends Node
{
    private Node pairedNode;
    boolean actsNull;// we don't want Neville to be null, because we want to keep his name and properties intact.
    // also...Neville wouldn't hurt a fly and we like him.

    String name = "";// Not really used, but would make for a nice feature in a completed app.

    public User(String name)
    {
	this.name = name;
	this.x = -1;
	this.y = -1;

    }

    public void setPairedNode(Node n)
    {
	this.x = n.x;
	this.y = n.y;
	System.out.println(this.y + " " + n.y);
	pairedNode = n;

    }

    /** Returns the map that the user is on. */
    public Map isOnMap()
    {
	return pairedNode.M;
    }

    /** Returns the node that the user is paired with */
    public Node getPairedNode()
    {
	return pairedNode;
    }

    /** Tells the user not to be paired with the node any more */
    public void clearLocation()
    {
	this.x = -1;
	this.y = -1;
	this.pairedNode = null;
    }

    public boolean isPlaced()
    {// has not been used thus far.
	return actsNull;
    }

    public void hide(boolean d)
    {// has not been used thus far.
	actsNull = d;
    }

    /** Moves the user to to the coordinates x,y on Map M. */
    public void moveTo(int x, int y)
    {
	this.x = x;
	this.y = y;
    }

    /** Moves the user to coordinates x,y and remains on the same map. */
    public void moveTo(int x, int y, Map M)
    {
	this.x = x;
	this.y = y;
	this.M = M;
    }

    public String toString()
    {
	return "Neville is at " + x + " " + y;
    }
}
