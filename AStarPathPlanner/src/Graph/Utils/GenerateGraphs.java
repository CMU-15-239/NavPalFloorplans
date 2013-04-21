package Graph.Utils;

import Graph.NavGraphNode;
import Graph.SparseGraph;
import Graph.GraphEdge;

import Common.Vector2D;

public class GenerateGraphs
{
    public static SparseGraph generateGridGraph(int rows, int cols, boolean digraph)
    {
	SparseGraph g = new SparseGraph(digraph);

	GraphHelper_CreateGrid(g, rows, cols, rows, cols);

	return g;
    }

    public static SparseGraph generateGridGraph(int cySize, int cxSize, int rows, int cols, boolean digraph)
    {
	SparseGraph g = new SparseGraph(digraph);

	GraphHelper_CreateGrid(g, cySize, cxSize, rows, cols);

	return g;
    }
    
    //    public static SparseGraph genrateRandomGraph(int numNodes, boolean digraph)
    //    {
    //	SparseGraph g = new SparseGraph(digraph);
    //
    //	return g;
    //    }
    //
    //    public static SparseGraph generateFullyConnectedGraph(int numNodes, boolean digraph)
    //    {
    //	SparseGraph g = new SparseGraph(digraph);
    //
    //	return g;
    //    }

    /**
     * Returns true if x,y is a valid position in the map
     * 
     * @param x
     * @param y
     * @param NumCellsX
     * @param NumCellsY
     * @return
     */
    private static boolean ValidNeighbor(int x, int y, int NumCellsX, int NumCellsY)
    {
	return !((x < 0) || (x >= NumCellsX) || (y < 0) || (y >= NumCellsY));
    }

    /**
     * Adds the eight neighboring edges of a graph node that is positioned in a grid layout
     * 
     * @param graph
     * @param row
     * @param col
     * @param NumCellsX
     * @param NumCellsY
     */
    private static void GraphHelper_AddAllNeighboursToGridNode(SparseGraph graph, int row, int col, int NumCellsX, int NumCellsY)
    {
	for (int i = -1; i < 2; ++i)
	{
	    for (int j = -1; j < 2; ++j)
	    {
		int nodeX = col + j;
		int nodeY = row + i;

		//skip if equal to this node
		if ((i == 0) && (j == 0))
		    continue;

		//check to see if this is a valid neighbor
		if (ValidNeighbor(nodeX, nodeY, NumCellsX, NumCellsY))
		{
		    //calculate the distance to this node
		    Vector2D PosNode = graph.getNode(row * NumCellsX + col).getPosition();
		    Vector2D PosNeighbour = graph.getNode(nodeY * NumCellsX + nodeX).getPosition();

		    double dist = PosNode.dist(PosNeighbour);

		    //this neighbor is okay so it can be added
		    GraphEdge NewEdge = new GraphEdge(row * NumCellsX + col, nodeY * NumCellsX + nodeX, dist);
		    graph.AddEdge(NewEdge);

		    //if graph is not a digraph then an edge needs to be added going
		    //in the other direction
		    if (graph.isDigraph())
		    {
			GraphEdge oppositeNewEdge = new GraphEdge(nodeY * NumCellsX + nodeX, row * NumCellsX + col, dist);
			graph.AddEdge(oppositeNewEdge);
		    }
		}
	    }
	}
    }

    /**
     * Creates a graph based on a grid layout. This function requires the dimensions 
     * of the environment and the number of cells required horizontally and vertically. 
     * 
     * @param graph
     * @param cySize
     * @param cxSize
     * @param NumCellsY
     * @param NumCellsX
     */
    private static void GraphHelper_CreateGrid(SparseGraph graph, int cySize, int cxSize, int NumCellsY, int NumCellsX)
    {
	// Need some temporaries to help calculate each node center
	double CellWidth = (double) cySize / (double) NumCellsX;
	double CellHeight = (double) cxSize / (double) NumCellsY;

	double midX = CellWidth / 2;
	double midY = CellHeight / 2;

	// First create all the nodes
	for (int row = 0; row < NumCellsY; ++row)
	{
	    for (int col = 0; col < NumCellsX; ++col)
	    {
		graph.AddNode(new NavGraphNode(graph.getNextFreeNodeIndex(), new Vector2D(midX + (col * CellWidth), midY + (row * CellHeight))));

	    }
	}

	// Now to calculate the edges. (A position in a 2d array [x][y] is the
	// same as [y*NumCellsX + x] in a 1d array). Each cell has up to eight
	// Neighbors.
	for (int row = 0; row < NumCellsY; ++row)
	{
	    for (int col = 0; col < NumCellsX; ++col)
	    {
		GraphHelper_AddAllNeighboursToGridNode(graph, row, col, NumCellsX, NumCellsY);
	    }
	}
    }
}
