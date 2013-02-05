/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.cmu;

/**
 * 
 * @author Chet
 */

public class Exit implements Edge
{
    public int x;
    public int id;
    public int y;
    public Exit linkedExit;
    String type = new String();
    String shortMes = "";
    String longMes = "";
    public Vertex to;
    public Vertex from;

    public Exit()
    {
    }

    Map M;

    public Exit(Map M, int a, int b, String st, int id)
    {
	x = a;
	y = b;
	shortMes = st;
	this.id = id;
	this.M = M;
    }

    @Override
    public Vertex getFrom()
    {
	return M;
    }

    @Override
    public void setFrom(Vertex s)
    {
	from = s;
    }

    @Override
    public Vertex getTo()
    {
	return linkedExit.M;
    }

    @Override
    public void setTo(Vertex s)
    {
	to = s;
    }

    @Override
    public Vertex getToSub()
    {
	return this.linkedExit.M.rooms.get(this.linkedExit.M.RoomsMap[this.linkedExit.x][this.linkedExit.y]);
    }

    /** Returns true if the exit leads to the outside. */
    public boolean isToHigherLevel()
    {
	if (shortMes.equals("Exit"))
	    return true;
	return false;
    }

    @Override
    public Node fromNode()
    {
	return M.node[this.x][this.y];
    }

    @Override
    public Node toNode()
    {
	return this.linkedExit.M.node[linkedExit.x][linkedExit.y];
    }

}
