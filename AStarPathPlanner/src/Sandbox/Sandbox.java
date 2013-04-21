package Sandbox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import Algorithms.GraphAStar;
import Common.Vector2D;
import Graph.GraphEdge;
import Graph.NavGraphNode;
import Graph.SparseGraph;
import Graph.Utils.DisplayGraph;
import Graph.Utils.GenerateGraphs;
import Graph.Utils.SaveGraph;

public class Sandbox
{
    static List<GraphEdge> edges = new ArrayList<GraphEdge>();
    static HashMap<Integer, NavGraphNode> nodes = new HashMap<Integer, NavGraphNode>();

    public void simpleGraphTest()
    {
	NavGraphNode node1 = new NavGraphNode(1, new Vector2D(1, 1));
	NavGraphNode node2 = new NavGraphNode(2, new Vector2D(2, 2));
	NavGraphNode node3 = new NavGraphNode(3, new Vector2D(3, 3));
	NavGraphNode node4 = new NavGraphNode(4, new Vector2D(4, 4));

	SparseGraph g = new SparseGraph(true);

	DisplayGraph.DisplayGraphNodesToConsole(g, false);

	g.AddNode(node1);
	g.AddNode(node2);
	g.AddNode(node3);
	g.AddNode(node4);

	g.AddEdge(new GraphEdge(1, 2));
	g.AddEdge(new GraphEdge(1, 3));
	g.AddEdge(new GraphEdge(1, 4));

	g.AddEdge(new GraphEdge(2, 3));
	g.AddEdge(new GraphEdge(2, 4));

	g.AddEdge(new GraphEdge(3, 4));

	DisplayGraph.DisplayGraphNodesToConsole(g, false);

	g.removeNode(1);

	DisplayGraph.DisplayGraphNodesToConsole(g, false);

	g.AddNode(node1);
	g.AddEdge(new GraphEdge(1, 2));
	g.AddEdge(new GraphEdge(1, 3));
	g.AddEdge(new GraphEdge(1, 4));

	g.updateEdgeCost(1, 2, 5.0);

	DisplayGraph.DisplayGraphNodesToConsole(g, false);
    }

    private static void gridGraphTest()
    {
	int rows = 10;
	int cols = 10;

	SparseGraph g = GenerateGraphs.generateGridGraph(rows, cols, true);

	DisplayGraph.DisplayGraphToConsole(g, true);

	String graphPath = "C:\\Users\\ggiger\\Documents\\ProgrammingProjects\\CheckedOutSVNRepos\\OctaveScripts\\trunk\\PathPlotting\\";
	String graphFilename = "graph.txt";

	SaveGraph.SaveGraphToFile(g, graphPath + graphFilename);
    }

    /**
     * This method constructs a test graph of 4 by 5 with two obstacles
     */

    private static SparseGraph generateTestGraphOne()
    {
	SparseGraph g = new SparseGraph(true);

	/*
	 * Manually add nodes to the graph
	 */

	// Row 1
	g.AddNode(new NavGraphNode(g.getNextFreeNodeIndex(), new Vector2D(1, 1)));
	g.AddNode(new NavGraphNode(g.getNextFreeNodeIndex(), new Vector2D(1, 2)));
	g.AddNode(new NavGraphNode(g.getNextFreeNodeIndex(), new Vector2D(1, 3)));
	g.AddNode(new NavGraphNode(g.getNextFreeNodeIndex(), new Vector2D(1, 4)));
	g.AddNode(new NavGraphNode(g.getNextFreeNodeIndex(), new Vector2D(1, 5)));

	// Row 2
	g.AddNode(new NavGraphNode(g.getNextFreeNodeIndex(), new Vector2D(2, 1)));
	g.AddNode(new NavGraphNode(g.getNextFreeNodeIndex(), new Vector2D(2, 3)));
	g.AddNode(new NavGraphNode(g.getNextFreeNodeIndex(), new Vector2D(2, 5)));

	// Row 3
	g.AddNode(new NavGraphNode(g.getNextFreeNodeIndex(), new Vector2D(3, 1)));
	g.AddNode(new NavGraphNode(g.getNextFreeNodeIndex(), new Vector2D(3, 3)));
	g.AddNode(new NavGraphNode(g.getNextFreeNodeIndex(), new Vector2D(3, 5)));

	// Row 4
	g.AddNode(new NavGraphNode(g.getNextFreeNodeIndex(), new Vector2D(4, 1)));
	g.AddNode(new NavGraphNode(g.getNextFreeNodeIndex(), new Vector2D(4, 2)));
	g.AddNode(new NavGraphNode(g.getNextFreeNodeIndex(), new Vector2D(4, 3)));
	g.AddNode(new NavGraphNode(g.getNextFreeNodeIndex(), new Vector2D(4, 4)));
	g.AddNode(new NavGraphNode(g.getNextFreeNodeIndex(), new Vector2D(4, 5)));

	/*
	 * Manually add graph edges
	 */

	g.AddEdge(new GraphEdge(0, 1));
	g.AddEdge(new GraphEdge(0, 5));
	g.AddEdge(new GraphEdge(1, 2));
	g.AddEdge(new GraphEdge(2, 3));
	g.AddEdge(new GraphEdge(3, 4));
	g.AddEdge(new GraphEdge(2, 6));
	g.AddEdge(new GraphEdge(4, 7));
	g.AddEdge(new GraphEdge(5, 8));
	g.AddEdge(new GraphEdge(6, 9));
	g.AddEdge(new GraphEdge(7, 10));
	g.AddEdge(new GraphEdge(8, 11));
	g.AddEdge(new GraphEdge(11, 12));
	g.AddEdge(new GraphEdge(12, 13));
	g.AddEdge(new GraphEdge(9, 13));
	g.AddEdge(new GraphEdge(13, 14));
	g.AddEdge(new GraphEdge(14, 15));
	g.AddEdge(new GraphEdge(15, 10));

	return g;
    }

    private static SparseGraph generateTestGraphTwo()
    {
	SparseGraph g = new SparseGraph(true);

	/*
	 * Manually add nodes to the graph
	 */

	// Row 1
	g.AddNode(new NavGraphNode(g.getNextFreeNodeIndex(), new Vector2D(1, 1)));
	g.AddNode(new NavGraphNode(g.getNextFreeNodeIndex(), new Vector2D(1, 2)));
	g.AddNode(new NavGraphNode(g.getNextFreeNodeIndex(), new Vector2D(1, 3)));
	g.AddNode(new NavGraphNode(g.getNextFreeNodeIndex(), new Vector2D(1, 4)));
	g.AddNode(new NavGraphNode(g.getNextFreeNodeIndex(), new Vector2D(1, 5)));
	g.AddNode(new NavGraphNode(g.getNextFreeNodeIndex(), new Vector2D(1, 6)));
	g.AddNode(new NavGraphNode(g.getNextFreeNodeIndex(), new Vector2D(1, 7)));
	g.AddNode(new NavGraphNode(g.getNextFreeNodeIndex(), new Vector2D(1, 8)));

	// Row 2
	g.AddNode(new NavGraphNode(g.getNextFreeNodeIndex(), new Vector2D(2, 1)));
	g.AddNode(new NavGraphNode(g.getNextFreeNodeIndex(), new Vector2D(2, 2)));
	g.AddNode(new NavGraphNode(g.getNextFreeNodeIndex(), new Vector2D(2, 3)));
	g.AddNode(new NavGraphNode(g.getNextFreeNodeIndex(), new Vector2D(2, 4)));
	g.AddNode(new NavGraphNode(g.getNextFreeNodeIndex(), new Vector2D(2, 5)));
	g.AddNode(new NavGraphNode(g.getNextFreeNodeIndex(), new Vector2D(2, 6)));
	g.AddNode(new NavGraphNode(g.getNextFreeNodeIndex(), new Vector2D(2, 7)));
	g.AddNode(new NavGraphNode(g.getNextFreeNodeIndex(), new Vector2D(2, 8)));

	// Row 3
	g.AddNode(new NavGraphNode(g.getNextFreeNodeIndex(), new Vector2D(3, 1)));
	g.AddNode(new NavGraphNode(g.getNextFreeNodeIndex(), new Vector2D(3, 2)));
	g.AddNode(new NavGraphNode(g.getNextFreeNodeIndex(), new Vector2D(3, 4)));
	g.AddNode(new NavGraphNode(g.getNextFreeNodeIndex(), new Vector2D(3, 5)));
	g.AddNode(new NavGraphNode(g.getNextFreeNodeIndex(), new Vector2D(3, 7)));
	g.AddNode(new NavGraphNode(g.getNextFreeNodeIndex(), new Vector2D(3, 8)));

	// Row 4
	g.AddNode(new NavGraphNode(g.getNextFreeNodeIndex(), new Vector2D(4, 1)));
	g.AddNode(new NavGraphNode(g.getNextFreeNodeIndex(), new Vector2D(4, 2)));
	g.AddNode(new NavGraphNode(g.getNextFreeNodeIndex(), new Vector2D(4, 4)));
	g.AddNode(new NavGraphNode(g.getNextFreeNodeIndex(), new Vector2D(4, 5)));
	g.AddNode(new NavGraphNode(g.getNextFreeNodeIndex(), new Vector2D(4, 7)));
	g.AddNode(new NavGraphNode(g.getNextFreeNodeIndex(), new Vector2D(4, 8)));

	// Row 5
	g.AddNode(new NavGraphNode(g.getNextFreeNodeIndex(), new Vector2D(5, 1)));
	g.AddNode(new NavGraphNode(g.getNextFreeNodeIndex(), new Vector2D(5, 2)));
	g.AddNode(new NavGraphNode(g.getNextFreeNodeIndex(), new Vector2D(5, 3)));
	g.AddNode(new NavGraphNode(g.getNextFreeNodeIndex(), new Vector2D(5, 4)));
	g.AddNode(new NavGraphNode(g.getNextFreeNodeIndex(), new Vector2D(5, 5)));
	g.AddNode(new NavGraphNode(g.getNextFreeNodeIndex(), new Vector2D(5, 6)));
	g.AddNode(new NavGraphNode(g.getNextFreeNodeIndex(), new Vector2D(5, 7)));
	g.AddNode(new NavGraphNode(g.getNextFreeNodeIndex(), new Vector2D(5, 8)));

	// Row 6
	g.AddNode(new NavGraphNode(g.getNextFreeNodeIndex(), new Vector2D(6, 1)));
	g.AddNode(new NavGraphNode(g.getNextFreeNodeIndex(), new Vector2D(6, 2)));
	g.AddNode(new NavGraphNode(g.getNextFreeNodeIndex(), new Vector2D(6, 3)));
	g.AddNode(new NavGraphNode(g.getNextFreeNodeIndex(), new Vector2D(6, 4)));
	g.AddNode(new NavGraphNode(g.getNextFreeNodeIndex(), new Vector2D(6, 5)));
	g.AddNode(new NavGraphNode(g.getNextFreeNodeIndex(), new Vector2D(6, 6)));
	g.AddNode(new NavGraphNode(g.getNextFreeNodeIndex(), new Vector2D(6, 7)));
	g.AddNode(new NavGraphNode(g.getNextFreeNodeIndex(), new Vector2D(6, 8)));

	/*
	 * Manually add graph edges
	 */

	return g;
    }

    private static void testAStarAlgorithm()
    {
	SparseGraph g = generateTestGraphOne();
	
	System.out.println("Starting...");

	//String graphPath = "C:\\Users\\ggiger\\Documents\\ProgrammingProjects\\CheckedOutSVNRepos\\OctaveScripts\\trunk\\PathPlotting\\";
	String graphPath = ".\\";
	String graphFilename = "graph.txt";

	SaveGraph.SaveGraphToFile(g, graphPath + graphFilename);

	/*
	 * Plan a route using the AStar algorithm
	 */
	int startNodeIndex = 0;
	int endNodeIndex = 15;

	GraphAStar astar = new GraphAStar(g, startNodeIndex, endNodeIndex);
	ArrayList<Integer> path = astar.getPathAsListOfNodeIds();
	dislayPathNodeIds(path);

	System.out.println("done.");
    }

    private static void dislayPathNodeIds(ArrayList<Integer> path)
    {
	Iterator<Integer> iterator = path.iterator();
	
	System.out.println();
	while(iterator.hasNext())
	{
	    System.out.print(iterator.next() + " ");
	}
	System.out.println();
    }
    
    /**
     * @param args
     */
    public static void main(String[] args)
    {
	testAStarAlgorithm();
    }
}
