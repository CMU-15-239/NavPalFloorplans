/**
 * @author Chet and Piotr
 * */
package edu.cmu;

//testing a small change
public class Action implements Edge
{
    /**
     * Actions are used both by the map representation and path planning and also by the path predictor. They represent a link between rooms. A doorway, essentially.
     */
    protected String name;
    public int to;
    public int from;
    public Vertex toRoom;
    public Vertex fromRoom;
    // each state has action 0 through nActions
    protected int id;
    // query (e.g., door id) if info-action; null otherwise
    protected String query = null;
    // whether this action is information-gathering action
    boolean isInfo = false;

    public Point edge;

    protected double cost = 0; // cost of taking this action

    public Action(String s, int id)
    {
	this.name = s;
	this.id = id;
	isInfo = false;
    }

    public Action(String name, int to, int from, int id)
    {
	this.to = to;
	this.name = name;
	this.from = from;
	this.id = id;
	isInfo = false;
    }

    public Action(String name, int id, boolean isInfoAction)
    {
	this(name, id);
	isInfo = isInfoAction;
    }

    public int getId()
    {
	return id;
    }

    public boolean isInfoAction()
    {
	return isInfo;
    }

    public void setInfoAction(boolean b)
    {
	isInfo = b;
    }

    public String getName()
    {
	return name;
    }

    @Override
    public String toString()
    {
	return name + ":" + id + "\n";
    }

    @Override
    public boolean equals(Object a)
    {
	return ((Action) a).id == id;
    }

    public String getQuery()
    {
	return query;
    }

    public void setQuery(String query)
    {
	this.query = query;
    }

    @Override
    public int hashCode()
    {
	return id;
    }

    public void setCost(Double cost)
    {
	this.cost = cost;
    }

    public double getCost()
    {
	return cost;
    }

    public void setId(int id)
    {
	this.id = id;
    }

    @Override
    public Vertex getFrom()
    {
	return this.fromRoom;
    }

    @Override
    public void setFrom(Vertex r)
    {
	// TODO Auto-generated method stub
	this.fromRoom = r;
    }

    @Override
    public Vertex getTo()
    {
	// TODO Auto-generated method stub
	return this.toRoom;
    }

    @Override
    public void setTo(Vertex r)
    {
	this.toRoom = r;
    }

    public Vertex getToSub()
    {
	return null;
    }

    public boolean isToHigherLevel()
    {
	return false;
    }

    @Override
    public Node toNode()
    {
	// These should not be needed
	return null;
    }

    @Override
    public Node fromNode()
    {
	// These should not be needed
	return null;
    }
}
