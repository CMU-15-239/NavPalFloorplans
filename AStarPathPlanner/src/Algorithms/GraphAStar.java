package Algorithms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.TreeSet;

import AStar.Utils.AStarPriorityQueue;
import AStar.Utils.AStarPriorityQueueComparator;
import Common.Tuple;
import Graph.GraphEdge;
import Graph.GraphEdgeIterator;
import Graph.NavGraphNode;
import Graph.SparseGraph;

public class GraphAStar
{
    private SparseGraph _mGraph;

    // TODO: May need to wrap each node in a path node class that stores the cost between this node
    //	     and another node (previous node/parent node) in the list since costs are relative
    //	     depending on the current nodes relation to other nodes.

    private int _mSource;
    private int _mTarget;

    AStarPriorityQueue<Integer, Double> _openSet;

    // The closed set is implemented as a HashSet storing the id of the nodes already visited.
    // A hashset implementation is used since it provides O(1) lookup time.
    private Set<Integer> _closedSet;

    // Hashmaps are used in order to keep the amount of space to a minimum
    // NOTE: To increase performance, these maps may need to be sufficiently sized.
    // 	     Since for very large graphs (1000's of nodes), it might not be wise to 
    //	     allocate space for each node in the graph since it would require twice 
    //	     the space (one entry in the F Cost list and one in the G cost list).
    private Map<Integer, Double> _mFCosts;
    private Map<Integer, Double> _mGCosts;

    // Back points of path node Ids (is a path exists).
    private Map<Integer, Integer> _cameFrom = new HashMap<Integer, Integer>();

    // Path variables
    private List<Integer> _pathOfNodeIds = new ArrayList<Integer>();

    public GraphAStar(SparseGraph graph, int source, int target)
    {
	_mGraph = graph;
	_mSource = source;
	_mTarget = target;

	if (_mGraph == null)
	{
	    return;
	}

	// Check if both source and destination nodes exist.
	if (!_mGraph.isNodePresent(_mSource) || !_mGraph.isNodePresent(_mTarget))
	{
	    return;
	}

	// Create an indexed priority queue of node indexes and their F score. The 
	// nodes with the lowest overall F cost (G+H) are positioned at the front.
	_openSet = new AStarPriorityQueue<Integer, Double>(_mGraph.numNodes(), new AStarPriorityQueueComparator());

	// Keep a set of node indices that have already been visited.
	_closedSet = new HashSet<Integer>(graph.numNodes());

	_mFCosts = new HashMap<Integer, Double>(_mGraph.numNodes() / 2);
	_mGCosts = new HashMap<Integer, Double>(_mGraph.numNodes() / 2);

	Search();
    }

    private void reconstructPath(int currentNodeId)
    {
	if (currentNodeId == _mSource)
	{
	    _pathOfNodeIds.add(_mSource);
	    return;
	}

	reconstructPath(_cameFrom.get(currentNodeId));
	_pathOfNodeIds.add(currentNodeId);
    }

    private void Search()
    {
	_mGCosts.put(_mSource, 0.0);
	_mFCosts.put(_mSource, _mGCosts.get(_mSource) + _mGraph.getNode(_mSource).getPosition().dist(_mGraph.getNode(_mTarget).getPosition()));

	_openSet.add(_mSource, _mFCosts.get(_mSource));

	while (!_openSet.isEmpty())
	{
	    Tuple<Integer, Double> nextLowestNodeFScorePair = _openSet.remove();
	    int currentNodeIndex = nextLowestNodeFScorePair.getFirst();

	    // If current node index with next lowest F score is the same as the target index, a path was found. 
	    if (currentNodeIndex == _mTarget)
	    {
		// TODO: Call method that will reconstruct path
		_pathOfNodeIds.clear();
		reconstructPath(_mTarget);
		//Collections.reverse(_pathOfNodeIds);
		break;
	    }

	    // Add the index of the current node to the closed list
	    // NOTE: Only the index for the node needs to be stored since it can be used to get the actual node from the graph 
	    _closedSet.add(currentNodeIndex);

	    // TODO: Get the neighbors of the current node
	    GraphEdgeIterator edgeIterator = new GraphEdgeIterator(_mGraph, currentNodeIndex);
	    while (edgeIterator.hasNext())
	    {
		GraphEdge edgeToCurrentNeighbor = (GraphEdge) edgeIterator.next();
		int currentNeighborIndex = edgeToCurrentNeighbor.getTo();

		// NOTE: The edge cost was already computed when the graph was made. This works in this case since the neighbors are adjacent.
		double tentativeGScore = _mGCosts.get(currentNodeIndex) + edgeToCurrentNeighbor.getCost();

		if (_closedSet.contains(currentNeighborIndex))
		{
		    if (tentativeGScore >= _mGCosts.get(currentNeighborIndex))
		    {
			continue;
		    }
		}

		// Check if current current neighbor index already exists in open list
		if ((!_openSet.contains(currentNeighborIndex)) || (tentativeGScore < _mGCosts.get(currentNeighborIndex)))
		{
		    _cameFrom.put(currentNeighborIndex, currentNodeIndex);
		    _mGCosts.put(currentNeighborIndex, tentativeGScore);
		    _mFCosts.put(currentNeighborIndex, _mGCosts.get(currentNeighborIndex) + _mGraph.getNode(currentNeighborIndex).getPosition().dist(_mGraph.getNode(_mTarget).getPosition()));

		    if (!_openSet.contains(currentNeighborIndex))
		    {
			_openSet.add(currentNeighborIndex, _mFCosts.get(currentNeighborIndex));
		    }
		}
	    }
	}
    }

//    private void displayCameFromList()
//    {
//	for (Map.Entry entry : _cameFrom.entrySet())
//	{
//	    System.out.println(entry.getKey() + ", " + entry.getValue());
//	}
//    }

    public ArrayList<Integer> getPathAsListOfNodeIds()
    {
	return (ArrayList<Integer>) _pathOfNodeIds;
    }
}
