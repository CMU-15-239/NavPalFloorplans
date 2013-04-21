package Graph;

public class GraphNode
{
    private int _index;

    public GraphNode()
    {
	this(-1);
    }

    public GraphNode(int index)
    {
	setIndex(index);
    }

    public int getIndex()
    {
	return _index;
    }

    public void setIndex(int index)
    {
	_index = index;
    }
}
