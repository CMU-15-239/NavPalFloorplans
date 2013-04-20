package Graph;

import java.util.Iterator;

public class GraphNodeIterator implements Iterator<Object>
{
    private Iterator<NavGraphNode> _currentNode;

    public GraphNodeIterator(SparseGraph graph, boolean ordered)
    {
	if (ordered)
	{
	    _currentNode = graph.getOrderedNodes().iterator();
	}
	else
	{
	    _currentNode = graph.getNodes().iterator();	    
	}
    }

    @Override
    public boolean hasNext()
    {
	return _currentNode.hasNext();
    }

    @Override
    public Object next()
    {
	return _currentNode.next();
    }

    @Override
    public void remove()
    {
	_currentNode.remove();
    }
}
