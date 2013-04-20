package Graph;

import java.util.Iterator;

public class GraphEdgeIterator implements Iterator<Object>
{
    private Iterator<GraphEdge> _currentEdge;

    public GraphEdgeIterator(SparseGraph graph, int nodeIndex)
    {
	_currentEdge = graph.getNodeEdges(nodeIndex).iterator();
    }
    
    @Override
    public boolean hasNext()
    {
	return _currentEdge.hasNext();
    }

    @Override
    public Object next()
    {
	return _currentEdge.next();
    }

    @Override
    public void remove()
    {
	_currentEdge.remove();
    }

}
