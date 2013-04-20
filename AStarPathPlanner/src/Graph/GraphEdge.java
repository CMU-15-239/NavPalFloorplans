package Graph;

import java.text.DecimalFormat;

public class GraphEdge
{
    private int    _from;
    private int    _to;
    private double _cost;

    public GraphEdge()
    {
	this(-1, -1, 1.0);
    }

    public GraphEdge(int from, int to)
    {
	this(from, to, 1.0);
    }

    public GraphEdge(int from, int to, double cost)
    {
	setTo(to);
	setFrom(from);
	setCost(cost);
	
	//System.out.println("DEBUG: Adding GraphEdge(" + getFrom() + ", " + getTo() + ") [" + cost + "]");
    }

    public int getFrom()
    {
	return _from;
    }

    public void setFrom(int from)
    {
	_from = from;
    }

    public int getTo()
    {
	return _to;
    }

    public void setTo(int to)
    {
	_to = to;
    }

    public double getCost()
    {
	return _cost;
    }

    public void setCost(double cost)
    {
	_cost = cost;
    }

    public boolean isEqual(GraphEdge edge)
    {
	return (_from == edge.getFrom() && _to == edge.getTo() && _cost == edge.getCost());
    }
    
    public String toString()
    {
	DecimalFormat fmt = new DecimalFormat("0.00");
	return "E(" + getFrom() + ", " + getTo() + ") [" + fmt.format(_cost) + "] |";
    }
}
