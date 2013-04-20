package Graph.Utils;

import Graph.GraphEdge;
import Graph.GraphNodeIterator;
import Graph.GraphEdgeIterator;
import Graph.NavGraphNode;
import Graph.SparseGraph;

public class DisplayGraph
{
    public static void DisplayGraphNodesToConsole(SparseGraph g, boolean ordered)
    {
	System.out.println("----------------------------------");

	if (g.isEmpty() || g.numActiveNodes() == 0)
	{
	    System.out.println("The graph is empty.");
	    System.out.println("----------------------------------");
	    return;
	}

	System.out.println("There are " + g.numActiveNodes() + " in the graph.");
	System.out.println();

	GraphNodeIterator iterator = new GraphNodeIterator(g, ordered);

	while (iterator.hasNext())
	{
	    NavGraphNode node = (NavGraphNode) iterator.next();

	    System.out.println(node.toString());
	}
    }

    /**
     * Display the graph contents to the console window
     */
    public static void DisplayGraphToConsole(SparseGraph g, boolean ordered)
    {
	System.out.println("----------------------------------");

	if (g.isEmpty() || g.numActiveNodes() == 0)
	{
	    System.out.println("The graph is empty.");
	    System.out.println("----------------------------------");
	    return;
	}

	System.out.println("There are " + g.numActiveNodes() + " in the graph.");
	System.out.println();

	GraphNodeIterator iterator = new GraphNodeIterator(g, ordered);
	while (iterator.hasNext())
	{
	    NavGraphNode node = (NavGraphNode) iterator.next();

	    String graphNodeWithEdges = node.getIndex() + "\t:";
	    
	    GraphEdgeIterator edgeIterator = new GraphEdgeIterator(g, node.getIndex());
	    while (edgeIterator.hasNext())
	    {
		GraphEdge edge = (GraphEdge) edgeIterator.next();
		graphNodeWithEdges += edge.toString() + " ";
	    }
	    System.out.println(graphNodeWithEdges);
	}

	System.out.println("----------------------------------");
    }
}
