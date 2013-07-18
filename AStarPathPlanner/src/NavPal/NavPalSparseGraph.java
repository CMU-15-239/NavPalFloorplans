package NavPal;

import importgraph.Building;
import importgraph.EdgeWeight;
import importgraph.Floor;
import importgraph.FloorConnection;
import importgraph.Landmark;
import importgraph.Point;
import importgraph.Psw;
import importgraph.Space;

import java.util.HashMap;

import Common.Vector2D;
import Graph.NavGraphEdge;
import Graph.NavGraphNode;
import Graph.SparseGraph;

public class NavPalSparseGraph extends SparseGraph
{
    public NavPalSparseGraph(boolean digraph)
    {
	super(digraph);
    }

    // TODO: the variable NodeCount can be removed and replaced with the value returned form the addNode method since this returns the next available index with which a node can be labeled.
    public static SparseGraph importGraph(Building b)
    {
        SparseGraph graph = new SparseGraph(true);
        
        
        
        Floor f = b.floors[0];

        //
        // TODO: What all information do we need to store in the graph nodes to better aid in planning the path?
        //
        HashMap<Integer, Space> sparseIdToSpaces = new HashMap<Integer, Space>();
        HashMap<Integer, Psw> sparseIdToPsws = new HashMap<Integer, Psw>();
        HashMap<Integer, FloorConnection> sparseIdToFloorConnections =  new HashMap<Integer, FloorConnection>();
        HashMap<Integer, Landmark> sparseIdToLandmark = new HashMap<Integer, Landmark>();
        HashMap<String, Integer> nodeIdToSparseId = new HashMap<String, Integer>();

        int nodeCounter = 0;
        int edgeCounter = 0;

        System.out.println("Generating Spaces...");
        System.out.println("\t" + f.spaces.length + " spaces were found...");
        for(int s = 0; s < f.spaces.length; s++) {
            Space space = f.spaces[s];
            if(space != null) {
        	int x = 0;
        	int y = 0;
        	if(space.walls.length > 0) {
        	    Point p1 = space.walls[0].p1;
        	    x = p1.x;
        	    y = p1.y;
        	}
    
        	NavGraphNode n = new NavGraphNode(nodeCounter, new Vector2D(x,y));
        	graph.AddNode(n);
        	sparseIdToSpaces.put(nodeCounter, space);
        	nodeIdToSparseId.put(space.id, nodeCounter);

        	// NOTE: Code crashes in this block that process edges since there is no edges between the spaces. I receive the an java.lang.NullPointerException exception on the line
        	// for(int e = 0; e < space.edges.length; e++) when the edges array is accessed.
        	// The edge array that is part of the spaces object is null. This is because there is no spaces defined in the JSON file.
        	//
        	if (space.edges != null)
        	{
        	    for(int e = 0; e < space.edges.length; e++)
        	    {
        		String n2Id = space.edges[e];
        		Integer s2 = nodeIdToSparseId.get(n2Id);
        		//System.out.println((nodeCounter) + " -> " + s2);
        		if(s2 != null)
        		{
        		    EdgeWeight edge = b.getEdge(space.id, n2Id);
        		    NavGraphEdge graphEdge = new NavGraphEdge(nodeCounter, s2, edge.weight, 0, edgeCounter);
        		    graph.AddEdge(graphEdge);
        		    edgeCounter++;
        		}
        	    }
        	}

        	nodeCounter++;
            }
        }
        System.out.println("\tGenerated Spaces " + nodeCounter + ", " + edgeCounter + ".");
        
        System.out.println("Generating Psws...");
        
        // NOTE: Code crashes in this block that processes psws since there is no psws and inturn no edges between the passage ways and other nodes spaces. I receive the an java.lang.NullPointerException exception on the line
	// for(int p = 0; p < f.psws.length; p++) when the psws array is accessed.
	// The psws array that is part of the floor object is null. This is because there is no psws defined in the JSON file.
	//
        if (f.psws != null)
        {
            System.out.println("\t" + f.psws.length + " passageways were found...");
            for(int p = 0; p < f.psws.length; p++)
            {
                Psw psw = f.psws[p];
                if(psw != null) {
            	int x = 0;
            	int y = 0;
            	if(psw.lineRep != null) {
            	    Point p1 = psw.lineRep.p1;
            	    x = p1.x;
            	    y = p1.y;
            	}
        
            	NavGraphNode n = new NavGraphNode(nodeCounter, new Vector2D(x,y));
            	graph.AddNode(n);
            	sparseIdToPsws.put(nodeCounter, psw);
            	nodeIdToSparseId.put(psw.id, nodeCounter);
            	
            	for(int e = 0; e < psw.edges.length; e++)
            	{
            	    String n2Id = psw.edges[e];
            	    Integer p2 = nodeIdToSparseId.get(n2Id);
            	    //System.out.print(nodeCounter + "/" + psw.id + " -> " + p2 + "/" + n2Id);
            	    if(p2 != null)
            	    {
            		EdgeWeight edge = b.getEdge(psw.id, n2Id);
            		//System.out.print(": " + edge.weight);
            		NavGraphEdge graphEdge = new NavGraphEdge(nodeCounter, p2, edge.weight, 0, edgeCounter);
            		graph.AddEdge(graphEdge);
            		edgeCounter++;
            	    }
            	    //System.out.println();
            	}
        
            	nodeCounter++;
                }
            }
            System.out.println("Generated Psws " + nodeCounter + ", " + edgeCounter + ".");
        }
        else
        {
            System.out.println("\tNo passageways were found.");
        }

        System.out.println("Generating FloorConnections...");
        // NOTE: Code crashes in this block that process floorConnections. I receive the an java.lang.NullPointerException exception on the line
     	// for(int fc = 0; fc < f.floorConnections.length; fc++) when the psws array is accessed.
        if (f.floorConnections != null)
        {
            System.out.println("\t" + f.floorConnections.length + " floor connections were found...");
            
            for(int fc = 0; fc < f.floorConnections.length; fc++)
            {
                FloorConnection floorConnection = f.floorConnections[fc];
                if(floorConnection != null)
                {
                    int x = 0;
                    int y = 0;
                    if(floorConnection.pointRep != null)
                    {
                	    Point pointRep = floorConnection.pointRep;
                	    x = pointRep.x;
                	    y = pointRep.y;
                    }
                	
                    NavGraphNode n = new NavGraphNode(nodeCounter, new Vector2D(x, y));
                    graph.AddNode(n);
                    sparseIdToFloorConnections.put(nodeCounter, floorConnection);
                    nodeIdToSparseId.put(floorConnection.id, nodeCounter);

                    if (floorConnection.edges != null)
                    {
                	System.out.println("\t" + floorConnection.edges.length + " edges for current floor connection were found.");
                    	for(int e = 0; e < floorConnection.edges.length; e++)
                    	{
                    	    String n2Id = floorConnection.edges[e];
                    	    Integer fc2 = nodeIdToSparseId.get(n2Id);
                    	    if(fc2 != null)
                    	    {
                    		EdgeWeight edge = b.getEdge(floorConnection.id, n2Id);
                    		NavGraphEdge graphEdge = new NavGraphEdge(nodeCounter, fc2, edge.weight, 0, edgeCounter);
                    		graph.AddEdge(graphEdge);
                    		edgeCounter++;
                    	    }
                    	}

                    	nodeCounter++;
                    }
                    else
                    {
                	System.out.println("\tNo edges for current floor connections were found.");
                    }
                }
            }
            System.out.println("\tGenerated FloorConnections " + nodeCounter + ", " + edgeCounter + ".");
        }
        else
        {
            System.out.println("\tNo floor connections were found.");
        }

        System.out.println("Generating Landmarks...");
        // NOTE: Code crashes in this block that process floorConnections since there is no edges between the spaces. I receive the an java.lang.NullPointerException exception on the line
     	// for(int p = 0; p < f.psws.length; p++) when the psws array is accessed.
     	// The psws array that is part of the floor object is null. This is because there is no psws defined in the JSON file.
     	//
        if (f.floorConnections != null)
        {
            for(int l = 0; l < f.floorConnections.length; l++)
            {
        	if (f.landmarks != null)
        	{
                    Landmark landmark = f.landmarks[l];
                    if(landmark != null)
                    {
                		int x = 0;
                		int y = 0;
                		if(landmark.pointRep != null)
                		{
                		    Point pointRep = landmark.pointRep;
                		    x = pointRep.x;
                		    y = pointRep.y;
                		}
                	
                		NavGraphNode n = new NavGraphNode(nodeCounter, new Vector2D(x, y));
                		graph.AddNode(n);
                		sparseIdToLandmark.put(nodeCounter, landmark);
                		nodeIdToSparseId.put(landmark.id, nodeCounter);
                	
                		for(int e = 0; e < landmark.edges.length; e++)
                		{
                		    String n2Id = landmark.edges[e];
                		    Integer l2 = nodeIdToSparseId.get(n2Id);
                		    if(l2 != null)
                		    {
                			EdgeWeight edge = b.getEdge(landmark.id, n2Id);
                			NavGraphEdge graphEdge = new NavGraphEdge(nodeCounter, l2, edge.weight, 0, edgeCounter);
                			graph.AddEdge(graphEdge);
                			edgeCounter++;
                		    }
                		}

                		nodeCounter++;
                    }
                    System.out.println("\tGenerated Landmarks " + nodeCounter + ", " + edgeCounter + ".");
        	}
        	else
        	{
        	    System.out.println("\tNo landmarks were found.");
        	}
            }
        }
        else
        {
            System.out.println("\tNo landmarks were found.");
        }
        
        
        return graph;
    }    
}
