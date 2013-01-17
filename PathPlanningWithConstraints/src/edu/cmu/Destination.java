package edu.cmu;

public class Destination extends Node
{
    /**
     * This is simply a node that can be carried across maps. It is paired up with another node and uses it's properties in the path planner.
     */
    private Node pairedNode;
    boolean actsNull;

    String name = "";

    public Destination()
    {

    }

    public void setPairedNode(Node n)
    {
	this.x = n.x;
	this.y = n.y;
	pairedNode = n;

    }

    public Node getPairedNode()
    {
	return pairedNode;
    }

    public Map isOnMap()
    {
	return pairedNode.M;
    }

    public void clearLocation()
    {
	this.x = 0;
	this.y = 0;
	this.pairedNode = null;
    }

    public boolean isPlaced()
    {
	return actsNull;
    }

    public void hide(boolean d)
    {
	actsNull = d;
    }

    public void moveTo(int x, int y)
    {
	this.x = x;
	this.y = y;
    }
}
