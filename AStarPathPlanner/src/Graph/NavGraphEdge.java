package Graph;

public class NavGraphEdge extends GraphEdge
{
    private int _flags;
    private int _intersectEntity;

    public NavGraphEdge(int from, int to, double cost, int flags)
    {
	this(from, to, cost, flags, -1);
    }

    public NavGraphEdge(int from, int to, double cost, int flags, int id)
    {
	setFrom(from);
	setTo(to);
	setCost(cost);
	setFlags(flags);
	setIntersectEntity(-1);
    }

    public int getFlags()
    {
	return _flags;
    }

    public void setFlags(int flags)
    {
	_flags = flags;
    }

    public int getIntersectEntity()
    {
	return _intersectEntity;
    }

    public void setIntersectEntity(int intersectEntity)
    {
	_intersectEntity = intersectEntity;
    }

}
