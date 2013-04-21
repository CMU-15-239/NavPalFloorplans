package Algorithms;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import Algorithms.GraphDFS.NodeStatus;
import Graph.GraphEdge;
import Graph.SparseGraph;

public class GraphDFS
{
    enum NodeStatus
    {
	VISITED, UNVISITED, NO_PARENT_ASSIGNED
    };

    // A reference to the graph to be searched
    SparseGraph _mGraph;

    // this records the indexes of all the nodes that are visited as the
    // search progresses
    List<NodeStatus> _mVisited;

    // This holds the route taken to the target. Given a node index, the value
    // at that index is the node's parent. ie if the path to the target is
    // 3-8-27, then m_Route[8] will hold 3 and m_Route[27] will hold 8.
    List<NodeStatus> _mRoute;

    // As the search progresses, this will hold all the edges the algorithm has
    // examined. THIS IS NOT NECESSARY FOR THE SEARCH, IT IS HERE PURELY
    // TO PROVIDE THE USER WITH SOME VISUAL FEEDBACK
    ArrayList<GraphEdge> m_SpanningTree;

    // the source and target node indices
    int _mSource, _mTarget;

    // true if a path to the target has been found
    boolean _mFound;

    // bool Search();

    public GraphDFS(SparseGraph graph, int sourceIndex, int targetIndex)
    {
	_mGraph = graph;
	_mSource = sourceIndex;
	_mTarget = targetIndex;
	_mFound = false;

	// These need to be initialized
	_mVisited = new ArrayList<NodeStatus>(_mGraph.numNodes());
	_mRoute = new ArrayList<NodeStatus>(_mGraph.numNodes());

	initializeBookkeepingLists(_mVisited, NodeStatus.VISITED);
	initializeBookkeepingLists(_mRoute, NodeStatus.NO_PARENT_ASSIGNED);

	_mFound = Search();
    }

    private void initializeBookkeepingLists(List<NodeStatus> list, NodeStatus status)
    {
	Iterator<NodeStatus> iterator = list.iterator();

	while (iterator.hasNext())
	{
	    NodeStatus element = (NodeStatus) iterator.next();
	    element = status;
	}
    }

    // returns a vector containing pointers to all the edges the search has examined
    // std::vector<const Edge*> GetSearchTree()const{return m_SpanningTree;}

    // returns true if the target node has been located
    // bool Found()const{return m_bFound;}

    // returns a vector of node indexes that comprise the shortest path
    // from the source to the target
    // std::list<int> GetPathToTarget()const;

    // This method performs the DFS search
    public boolean Search()
    {
	// create a std stack of edges
	Stack<GraphEdge> stack = new Stack<GraphEdge>();

	// create a dummy edge and put on the stack
	GraphEdge Dummy = new GraphEdge(_mSource, _mSource, 0.0);

	stack.push(Dummy);

	// While there are edges in the stack keep searching
	while (!stack.empty())
	{
	    // //grab the next edge
	    // const Edge* Next = stack.top();
	    //
	    // //remove the edge from the stack
	    // stack.pop();
	    //
	    // //make a note of the parent of the node this edge points to
	    // m_Route[Next->To()] = Next->From();
	    //
	    // //put it on the tree. (making sure the dummy edge is not placed on the tree)
	    // if (Next != &Dummy)
	    // {
	    // m_SpanningTree.push_back(Next);
	    // }
	    //
	    // //and mark it visited
	    // m_Visited[Next->To()] = visited;
	    //
	    // //if the target has been found the method can return success
	    // if (Next->To() == m_iTarget)
	    // {
	    // return true;
	    // }
	    //
	    // //push the edges leading from the node this edge points to onto
	    // //the stack (provided the edge does not point to a previously
	    // //visited node)
	    // graph_type::ConstEdgeIterator ConstEdgeItr(m_Graph, Next->To());
	    //
	    // for (const Edge* pE=ConstEdgeItr.begin();
	    // !ConstEdgeItr.end();
	    // pE=ConstEdgeItr.next())
	    // {
	    // if (m_Visited[pE->To()] == unvisited)
	    // {
	    // stack.push(pE);
	    // }
	    // }
	}

	// no path to target
	return false;
    }
}

// //-----------------------------------------------------------------------------
//
//
// //-----------------------------------------------------------------------------
// template <class graph_type>
// std::list<int> Graph_SearchDFS<graph_type>::GetPathToTarget()const
// {
// std::list<int> path;
//
// //just return an empty path if no path to target found or if
// //no target has been specified
// if (!m_bFound || m_iTarget<0) return path;
//
// int nd = m_iTarget;
//
// path.push_front(nd);
//
// while (nd != m_iSource)
// {
// nd = m_Route[nd];
//
// path.push_front(nd);
// }
//
// return path;
// }
// }
