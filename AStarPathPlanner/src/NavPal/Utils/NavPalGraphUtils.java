package NavPal.Utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;

import Graph.GraphEdge;
import Graph.GraphEdgeIterator;
import Graph.GraphNodeIterator;
import Graph.NavGraphNode;
import Graph.SparseGraph;

public class NavPalGraphUtils
{
    public static void SaveGraphAsDotFileForGraphViz(SparseGraph g, String pathToSave)
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

	    bw.write("digraph G \n{");

	    // Write out each node with its list of neighbors
	    //
	    //	node -> neighbor1
	    //	node -> neighbor2
	    //	...
	    //	node -> neighborN
	    //
	    GraphNodeIterator iterator = new GraphNodeIterator(g, true);
	    while (iterator.hasNext())
	    {
		NavGraphNode node = (NavGraphNode) iterator.next();
		GraphEdgeIterator edgeIterator = new GraphEdgeIterator(g, node.getIndex());
		while (edgeIterator.hasNext())
		{
		    GraphEdge edge = (GraphEdge) edgeIterator.next();
		    bw.write("\t" + node.getIndex() + " -> " + edge.getTo() + ";\n");
		}

		bw.write("\n\t" + node.getIndex() + "\t[pos=\"" + node.getPosition().getX() + "," + node.getPosition().getY() + "!\"];\n\n");
	    }

	    bw.write("}\n");

	    bw.close();
	}
	catch (IOException e)
	{
	    e.printStackTrace();
	}
    }
}
