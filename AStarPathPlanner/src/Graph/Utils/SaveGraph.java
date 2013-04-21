package Graph.Utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;

import Graph.GraphEdge;
import Graph.GraphNodeIterator;
import Graph.GraphEdgeIterator;
import Graph.NavGraphNode;
import Graph.SparseGraph;

public class SaveGraph
{
    public static void SaveGraphToFile(SparseGraph g, String pathToSave)
    {
	try
	{
	    File file = new File(pathToSave);
	    DecimalFormat fmt = new DecimalFormat("0.00");

	    // If file does not exist, then create it
	    if (!file.exists())
	    {
		file.createNewFile();
	    }

	    FileWriter fw = new FileWriter(file.getAbsoluteFile());
	    BufferedWriter bw = new BufferedWriter(fw);

	    bw.write(g.numActiveNodes() + "\n");
	    
	    // Iterate through all nodes, getting their list of 
	    GraphNodeIterator iterator = new GraphNodeIterator(g, true);
	    while (iterator.hasNext())
	    {
		NavGraphNode node = (NavGraphNode) iterator.next();

		String graphNodeWithEdges = node.getIndex() + " (" + node.getPosition().getX() + ", " + node.getPosition().getY() + ")\t";
		
		GraphEdgeIterator edgeIterator = new GraphEdgeIterator(g, node.getIndex());
		while (edgeIterator.hasNext())
		{
		    GraphEdge edge = (GraphEdge) edgeIterator.next();
		    graphNodeWithEdges += "| " + edge.getTo() + ", " + fmt.format(edge.getCost()) + " ";
		}
		bw.write(graphNodeWithEdges + "\n");
	    }

	    bw.close();
	}
	catch (IOException e)
	{
	    e.printStackTrace();
	}
    }
}
