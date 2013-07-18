package Graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class SparseGraph
{
    /**
     * A hash map of nodes
     */
    private HashMap<Integer, NavGraphNode> _mNodes = new HashMap<Integer, NavGraphNode>();
    private HashMap<Integer, List<GraphEdge>> _mEdges = new HashMap<Integer, List<GraphEdge>>();

    private int _numActiveNodes; // Bookkeeping to track number of active nodes
    private boolean _mDigraph;
    private int _mNextFreeNodeIndex;

    private static int invalid_node_index = -1;

    /**
     * Default Constructor
     * 
     * @param digraph
     */
    public SparseGraph(boolean digraph)
    {
	_mNextFreeNodeIndex = 0;
	_mDigraph = digraph; //undirected graph
	_numActiveNodes = 0;
    }

    // /////////////////////////////////////////////////////////////////////////
    // ____ _ __ __ _ _ _
    // / ___|_ __ __ _ _ __ | |__ | \/ | ___| |_| |__ ___ __| |___
    // | | _| '__/ _` | '_ \| '_ \ | |\/| |/ _ \ __| '_ \ / _ \ / _` / __|
    // | |_| | | | (_| | |_) | | | | | | | | __/ |_| | | | (_) | (_| \__ \
    // \____|_| \__,_| .__/|_| |_| |_| |_|\___|\__|_| |_|\___/ \__,_|___/
    // |_|
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Indicates whether or not the graph is empty
     * 
     * @return
     */
    public boolean isEmpty()
    {
	return _mNodes.isEmpty();
    }

    /**
     * 
     * @return
     */
    public int numActiveNodes()
    {
	return _numActiveNodes;
    }

    /**
     * 
     * @return
     */
    public int numNodes()
    {
	return _mNodes.size();
    }

    /**
     * 
     * @return
     */
    public int getNextFreeNodeIndex()
    {
	return _mNextFreeNodeIndex;
    }

    /**
     * 
     * @return
     */
    public boolean isDigraph()
    {
	return _mDigraph;
    }

    // /////////////////////////////////////////////////////////////////////////
    // _ _ _ __ __ _ _ _
    // | \ | | ___ __| | ___ | \/ | ___| |_| |__ ___ __| |___
    // | \| |/ _ \ / _` |/ _ \ | |\/| |/ _ \ __| '_ \ / _ \ / _` / __|
    // | |\ | (_) | (_| | __/ | | | | __/ |_| | | | (_) | (_| \__ \
    // |_| \_|\___/ \__,_|\___| |_| |_|\___|\__|_| |_|\___/ \__,_|___/
    //
    // /////////////////////////////////////////////////////////////////////////

    /**
     * 
     * @param node
     * @return
     * 
     *         TODO When deleting a node from the graph, consider making the value null leaving the key
     * 
     *         TODO This method needs to be tested
     */
    public int AddNode(NavGraphNode node)
    {
	// If the current node already exists in the hash map, simply return its index
	if (isNodePresent(node.getIndex()))
	{
	    return node.getIndex();
	}

	// Add the node and assign an empty edge list to that node in the corresponding graph edges
	_mNodes.put(node.getIndex(), node);
	_numActiveNodes++; // This will need to be decremented when everytime a node is removed

	// QUESTION Note sure if ArrayList is the best List implementation to use for the edges
	_mEdges.put(node.getIndex(), new ArrayList<GraphEdge>(0));

	// Since the node did not previously exist, return the index of the new node and increment it.
	return _mNextFreeNodeIndex++;
    }

    /**
     * 
     */
    public void removeNode(int nodeIndex)
    {
	// If the node does not exist
	if (!isNodePresent(nodeIndex))
	{
	    return;
	}

	// Remove all edges pointing to the node that needs to be removed
	if (_mDigraph)
	{
	    // Get the list of edges for the node to be removed
	    List<GraphEdge> nodeEdges = _mEdges.get(nodeIndex);
	    Iterator<GraphEdge> iteratorEdges = nodeEdges.iterator();

	    // First, remove all the edges that point to this node from other nodes
	    while (iteratorEdges.hasNext())
	    {
		GraphEdge edge = iteratorEdges.next();
		removeEdge(edge.getTo(), edge.getFrom());
	    }
	}

	// Clear the list of edges of the node to delete
	_mEdges.get(nodeIndex).clear();

	// Remove the node from the list. The node object is removed from the actual hashmap,
	// but the node index remain in case the node is added again at some point in the future.
	_mNodes.put(nodeIndex, null);

	// Update the bookkeeping variables
	_numActiveNodes--;
    }

    /**
     * Checks if a node with the specified index already exists in the graph
     * 
     * @param nodeIndex
     * @return
     * 
     *         TODO This method needs to be tested
     */
    public boolean isNodePresent(int nodeIndex)
    {
	// Check if the key exists in the hashmap and that its value is not null
	// Note that a key can exist and its value can be null. When a node is
	// removed from the graph rather than deleting the node from the hashmap, its
	// value will be set to null thus preserving the key. This is to ensure that
	// when a node is added and removed, its index is preserved so that if the
	// same node is added again in the future, it will not interfere with any
	// other nodes etc...
	if (_mNodes.containsKey(nodeIndex) && (_mNodes.get(nodeIndex) != null))
	{
	    return true;
	}

	return false;
    }

    /**
     * 
     * @param nodeIndex
     * @return
     */
    public NavGraphNode getNode(int nodeIndex)
    {
	if (isNodePresent(nodeIndex))
	{
	    return _mNodes.get(nodeIndex);
	}
	
	return null;
    }

    /**
     * Active nodes are returned in an unordered list with regard to node index. 
     * 
     * @return
     */
    public List<NavGraphNode> getNodes()
    {
	// Create the list of nodes to return
	List<NavGraphNode> listOfActiveNodes = new ArrayList<NavGraphNode>(this.numActiveNodes());
	
	Iterator<Entry<Integer, NavGraphNode>> iteratorNodes = _mNodes.entrySet().iterator();
	while (iteratorNodes.hasNext())
	{
	    Entry<Integer, NavGraphNode> nodes = iteratorNodes.next();
	    NavGraphNode node = (NavGraphNode) nodes.getValue();

	    if (node != null)
	    {
		listOfActiveNodes.add(node);
	    }
	}

	return listOfActiveNodes;
    }
    
    /**
     * Active nodes are returned ordered by their index in ascending order.
     * 
     * @return
     */
    public List<NavGraphNode> getOrderedNodes()
    {
	// Create the list of nodes to return
	List<NavGraphNode> listOfActiveNodes = new ArrayList<NavGraphNode>(this.numActiveNodes());
	
	for (int currentNodeIndex=0; currentNodeIndex<this.numNodes(); currentNodeIndex++)
	{
	    if (this.isNodePresent(currentNodeIndex))
	    {
		listOfActiveNodes.add(getNode(currentNodeIndex));
	    }
	}

	return listOfActiveNodes;
    }
    
    // /////////////////////////////////////////////////////////////////////////
    // _____ _ __ __ _ _ _
    // | ____|__| | __ _ ___ | \/ | ___| |_| |__ ___ __| |___
    // | _| / _` | / _` | / _ \ | |\/| |/ _ \ __| '_ \ / _ \ / _` / __|
    // | |__| (_| | (_| | __/ | | | | 			__/ |_| | | | (_) | (_| \__ \
    // |_____\__,_|\__, |\___| |_| |_|\___|\__|_| |_|\___/ \__,_|___/
    // |___/
    // /////////////////////////////////////////////////////////////////////////

    /**
     * 
     * @param edge
     * 
     *            TODO This needs to be tested.
     * 
     */
    public void AddEdge(GraphEdge edge)
    {
	int fromNodeIndex = edge.getFrom();
	int toNodeIndex = edge.getTo();

	// If either of the source or destination nodes do not exist, exit since an edge
	// cannot exist between non-existent nodes.
	if (!isNodePresent(fromNodeIndex) || !isNodePresent(toNodeIndex))
	{
	    return;
	}

	// add the edge, first making sure it is unique
	// if (!uniqueEdge(fromNodeIndex, toNodeIndex))
	GraphEdge existingEdge = uniqueEdge(fromNodeIndex, toNodeIndex);
	if (existingEdge == null)
	{
	    _mEdges.get(fromNodeIndex).add(edge);
	}
	else
	{
	    // Since the edge already exists, simply update the edge with the cost
	    existingEdge.setCost(edge.getCost());
	}

	// if the graph is undirected we must add another connection in the opposite
	// direction
	if (_mDigraph)
	{
	    // Check to make sure the edge is unique before adding
	    GraphEdge returningEdge = uniqueEdge(toNodeIndex, fromNodeIndex);
	    if (returningEdge == null)
	    {
		// Create a new edge and make it point from the From node back to the To node.
		GraphEdge newEdge = new GraphEdge(toNodeIndex, fromNodeIndex, edge.getCost());
		_mEdges.get(toNodeIndex).add(newEdge);
	    }
	    else
	    {
		// Since the edge already exists, simply update the edge with the cost
		returningEdge.setCost(edge.getCost());
	    }
	}
    }

    /**
     * 
     * @param from
     * @param to
     */
    public void removeEdge(int from, int to)
    {
	// If either of the source or destination nodes do not exist, exit since an edge
	// cannot exist between non-existent nodes.
	if (!isNodePresent(from) || !isNodePresent(to))
	{
	    return;
	}

	// Check if the edge does not exist
	GraphEdge edgeToRemove = uniqueEdge(from, to);
	if (edgeToRemove != null)
	{
	    _mEdges.get(from).remove(edgeToRemove);
	}
    }

    /**
     * Checks if an edge already exists in the graph. This method assumes that both nodes already exist since this method is called within the AddEdge method.
     * 
     * @param from
     * @param to
     * @return
     * 
     *         TODO Should this method be private? If the method is public, then this method may need to test if both nodes already exist
     * 
     *         TODO Find a more efficient way to implement finding the "To" node when looking for the edge. Currently it is linear time O(n).
     * 
     */
    private GraphEdge uniqueEdge(int from, int to)
    {
	GraphEdge existingEdge = null;
	Iterator<GraphEdge> edgesIterator = _mEdges.get(from).iterator();

	while (edgesIterator.hasNext())
	{
	    GraphEdge edge = edgesIterator.next();

	    if (edge.getTo() == to)
	    {
		existingEdge = edge;
		break;
	    }
	}

	return existingEdge;
    }

    /**
     * Changes the cost of the edge between the from and to nodes. Note that assigning a cost of zero to the edge will not remove the edge from the graph as is the case with other graph representations. Negative costs can also be assigned
     * to the edge.
     * 
     * @param from
     *            - The index of the node where the edge begins
     * @param to
     *            - The index of the node where the edge stops
     * @param cost
     *            - The numeric cost for traversing the edge
     * @return True if the cost was successfully updated, false otherwise
     */
    public boolean updateEdgeCost(int from, int to, double cost)
    {
	// If either of the source or destination nodes do not exist, exit since an edge
	// cannot exist between non-existent nodes.
	if (!isNodePresent(from) || !isNodePresent(to))
	{
	    return false;
	}

	GraphEdge edgeToUpdate = uniqueEdge(from, to);
	GraphEdge oppositeEdgeToUpdate = null;

	if (edgeToUpdate == null)
	{
	    return false;
	}

	// Remove all edges pointing to the node that needs to be removed
	if (_mDigraph)
	{
	    oppositeEdgeToUpdate = uniqueEdge(to, from);

	    if (oppositeEdgeToUpdate == null)
	    {
		return false;
	    }
	}

	edgeToUpdate.setCost(cost);
	oppositeEdgeToUpdate.setCost(cost);

	return true;
    }

    /**
     * Return the edge between the two specified node indices.
     * 
     * @param from - Index of the source/starting node
     * @param to - Index of the target/ending node
     * @return A reference to the node
     */
    public GraphEdge getEdge(int from, int to)
    {
	return uniqueEdge(from, to);
    }

    /**
     * 
     * @return
     */
    public List<GraphEdge> getNodeEdges(int nodeIndex)
    {
	return _mEdges.get(nodeIndex);
    }

    /**
     * 
     * @return
     */
    public boolean isEdgePresent(int from, int to)
    {
	if (uniqueEdge(from, to) == null)
	{
	    return false;
	}

	return true;
    }

    // /////////////////////////////////////////////////////////////////////////
    // ____ _ __ __ _ _ _
    // | _ \ ___| |__ _ _ __ _ | \/ | ___| |_| |__ ___ __| |___
    // | | | |/ _ \ '_ \| | | |/ _` | | |\/| |/ _ \ __| '_ \ / _ \ / _` / __|
    // | |_| | __/ |_) | |_| | (_| | | | | | __/ |_| | | | (_) | (_| \__ \
    // |____/ \___|_.__/ \__,_|\__, | |_| |_|\___|\__|_| |_|\___/ \__,_|___/
    // |___/
    // /////////////////////////////////////////////////////////////////////////

    
}
