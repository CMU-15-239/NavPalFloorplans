package Sandbox;

import importgraph.Building;
import importgraph.EdgeWeight;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.google.gson.Gson;

import Algorithms.GraphAStar;
import Graph.GraphEdge;
import Graph.NavGraphNode;
import Graph.SparseGraph;
import Graph.Utils.DisplayGraph;
import Graph.Utils.GenerateGraphs;
import Graph.Utils.SaveGraph;

public class Sandbox2
{
    private static void testAStarAlgorithm(SparseGraph g, int startNodeIndex, int endNodeIndex)
    {	
	System.out.println("Starting Test...");

	//String graphPath = "C:\\Users\\ggiger\\Documents\\ProgrammingProjects\\CheckedOutSVNRepos\\OctaveScripts\\trunk\\PathPlotting\\";
	String graphPath = "./";
	String graphFilename = "graph.txt";

	SaveGraph.SaveGraphToFile(g, graphPath + graphFilename);

	/*
	 * Plan a route using the AStar algorithm
	 */
	System.out.println("Planning Route...");

	GraphAStar astar = new GraphAStar(g, startNodeIndex, endNodeIndex);
	ArrayList<Integer> path = astar.getPathAsListOfNodeIds();
	dislayPathNodeIds(path);

	System.out.println("done.");
    }

    private static void dislayPathNodeIds(ArrayList<Integer> path)
    {
	Iterator<Integer> iterator = path.iterator();
	
	System.out.println();
	boolean isFirst = true;
	while(iterator.hasNext())
	{
	    if(!isFirst) {System.out.print(" -> ");}
	    System.out.print(iterator.next());
	    isFirst = false;
	}
	System.out.println();
    }
    
    /**
     * @param args
     */
    public static void main(String[] args)
    {
	Gson gson = new Gson();
	String json = "{\"name\":\"buiding1\",\"id\":\"buildingGraph_h36274153\",\"floors\":[{\"name\":\"1\",\"imageId\":\"safdsa\",\"imageScale\":1,\"spaces\":[{\"type\":\"space\",\"edges\":[\"psw_h-260381518\",\"landmark_h1159858036\",\"floorConnection_h1665332854\"],\"id\":\"space_h-217148651\",\"spaceType\":\"room\",\"label\":\"s1\",\"walls\":[{\"p1\":{\"x\":0,\"y\":0},\"p2\":{\"x\":0,\"y\":10},\"isDoor\":false},{\"p1\":{\"x\":0,\"y\":10},\"p2\":{\"x\":10,\"y\":10},\"isDoor\":false},{\"p1\":{\"x\":10,\"y\":0},\"p2\":{\"x\":10,\"y\":1},\"isDoor\":false},{\"p1\":{\"x\":10,\"y\":1},\"p2\":{\"x\":10,\"y\":5},\"isDoor\":true},{\"p1\":{\"x\":10,\"y\":5},\"p2\":{\"x\":10,\"y\":10},\"isDoor\":false},{\"p1\":{\"x\":0,\"y\":0},\"p2\":{\"x\":10,\"y\":0},\"isDoor\":false}]},{\"type\":\"space\",\"edges\":[\"psw_h-260381518\",\"landmark_h-737275099\",\"floorConnection_h-1712102163\"],\"id\":\"space_h-1038962001\",\"spaceType\":\"room\",\"label\":\"s2\",\"walls\":[{\"p1\":{\"x\":10,\"y\":0},\"p2\":{\"x\":20,\"y\":0},\"isDoor\":false},{\"p1\":{\"x\":20,\"y\":0},\"p2\":{\"x\":20,\"y\":25},\"isDoor\":false},{\"p1\":{\"x\":10,\"y\":10},\"p2\":{\"x\":20,\"y\":25},\"isDoor\":false},{\"p1\":{\"x\":10,\"y\":5},\"p2\":{\"x\":10,\"y\":10},\"isDoor\":false},{\"p1\":{\"x\":10,\"y\":1},\"p2\":{\"x\":10,\"y\":5},\"isDoor\":true},{\"p1\":{\"x\":10,\"y\":0},\"p2\":{\"x\":10,\"y\":1},\"isDoor\":false}]}],\"psws\":[{\"type\":\"psw\",\"edges\":[\"space_h-217148651\",\"space_h-1038962001\"],\"id\":\"psw_h-260381518\",\"pswType\":\"door\",\"lineRep\":{\"p1\":{\"x\":10,\"y\":1},\"p2\":{\"x\":10,\"y\":5},\"isDoor\":true}}],\"floorConnections\":[{\"type\":\"floorConnection\",\"edges\":[\"space_h-217148651\"],\"id\":\"floorConnection_h1665332854\",\"label\":\"evil stairs\",\"pointRep\":{\"x\":10,\"y\":10},\"floorConnectionType\":\"stairs\"},{\"type\":\"floorConnection\",\"edges\":[\"space_h-1038962001\"],\"id\":\"floorConnection_h-1712102163\",\"label\":\"good stairs\",\"pointRep\":{\"x\":20,\"y\":20},\"floorConnectionType\":\"stairs\"}],\"landmarks\":[{\"type\":\"landmark\",\"edges\":[\"space_h-217148651\"],\"id\":\"landmark_h1159858036\",\"label\":\"candyman\",\"description\":\"Asdfafa\",\"pointRep\":{\"x\":3,\"y\":3}},{\"type\":\"landmark\",\"edges\":[\"space_h-1038962001\"],\"id\":\"landmark_h-737275099\",\"label\":\"42\",\"description\":\"1as\",\"pointRep\":{\"x\":20,\"y\":25}}]},{\"name\":\"2\",\"imageId\":\"safdsa2\",\"imageScale\":1,\"spaces\":[{\"type\":\"space\",\"edges\":[\"psw_h-260381518\",\"landmark_h1159858036\",\"floorConnection_h-1712102163\"],\"id\":\"space_h2086578003\",\"spaceType\":\"room\",\"label\":\"s3\",\"walls\":[{\"p1\":{\"x\":0,\"y\":0},\"p2\":{\"x\":0,\"y\":10},\"isDoor\":false},{\"p1\":{\"x\":0,\"y\":10},\"p2\":{\"x\":10,\"y\":10},\"isDoor\":false},{\"p1\":{\"x\":10,\"y\":0},\"p2\":{\"x\":10,\"y\":1},\"isDoor\":false},{\"p1\":{\"x\":10,\"y\":1},\"p2\":{\"x\":10,\"y\":5},\"isDoor\":true},{\"p1\":{\"x\":10,\"y\":5},\"p2\":{\"x\":10,\"y\":10},\"isDoor\":false},{\"p1\":{\"x\":0,\"y\":0},\"p2\":{\"x\":10,\"y\":0},\"isDoor\":false}]},{\"type\":\"space\",\"edges\":[\"psw_h-260381518\",\"landmark_h-737275099\"],\"id\":\"space_h1197401197\",\"spaceType\":\"room\",\"label\":\"s4\",\"walls\":[{\"p1\":{\"x\":10,\"y\":0},\"p2\":{\"x\":20,\"y\":0},\"isDoor\":false},{\"p1\":{\"x\":20,\"y\":0},\"p2\":{\"x\":20,\"y\":25},\"isDoor\":false},{\"p1\":{\"x\":10,\"y\":10},\"p2\":{\"x\":20,\"y\":25},\"isDoor\":false},{\"p1\":{\"x\":10,\"y\":5},\"p2\":{\"x\":10,\"y\":10},\"isDoor\":false},{\"p1\":{\"x\":10,\"y\":1},\"p2\":{\"x\":10,\"y\":5},\"isDoor\":true},{\"p1\":{\"x\":10,\"y\":0},\"p2\":{\"x\":10,\"y\":1},\"isDoor\":false}]}],\"psws\":[{\"type\":\"psw\",\"edges\":[\"space_h2086578003\",\"space_h1197401197\"],\"id\":\"psw_h-260381518\",\"pswType\":\"door\",\"lineRep\":{\"p1\":{\"x\":10,\"y\":1},\"p2\":{\"x\":10,\"y\":5},\"isDoor\":true}}],\"floorConnections\":[{\"type\":\"floorConnection\",\"edges\":[],\"id\":\"floorConnection_h1665332854\",\"label\":\"evil stairs\",\"pointRep\":{\"x\":10,\"y\":15},\"floorConnectionType\":\"stairs\"},{\"type\":\"floorConnection\",\"edges\":[\"space_h2086578003\"],\"id\":\"floorConnection_h-1712102163\",\"label\":\"good stairs\",\"pointRep\":{\"x\":10,\"y\":10},\"floorConnectionType\":\"stairs\"}],\"landmarks\":[{\"type\":\"landmark\",\"edges\":[\"space_h2086578003\"],\"id\":\"landmark_h1159858036\",\"label\":\"candyman\",\"description\":\"Asdfafa\",\"pointRep\":{\"x\":3,\"y\":3}},{\"type\":\"landmark\",\"edges\":[\"space_h1197401197\"],\"id\":\"landmark_h-737275099\",\"label\":\"42\",\"description\":\"1as\",\"pointRep\":{\"x\":20,\"y\":25}}]}],\"floorConnectionRefs\":[{\"type\":\"floorConnection\",\"edges\":[\"floorGraph_h-1514562576\",\"floorGraph_h228425249\"],\"id\":\"floorConnection_h1665332854\",\"label\":\"evil stairs\",\"floorConnectionType\":\"stairs\"},{\"type\":\"floorConnection\",\"edges\":[\"floorGraph_h-1514562576\",\"floorGraph_h228425249\"],\"id\":\"floorConnection_h-1712102163\",\"label\":\"good stairs\",\"floorConnectionType\":\"stairs\"}],\"edgeWeights\":[{\"node1Id\":\"psw_h-260381518\",\"node2Id\":\"space_h-217148651\",\"weight\":1},{\"node1Id\":\"psw_h-260381518\",\"node2Id\":\"space_h-1038962001\",\"weight\":1},{\"node1Id\":\"landmark_h1159858036\",\"node2Id\":\"space_h-217148651\",\"weight\":1},{\"node1Id\":\"landmark_h-737275099\",\"node2Id\":\"space_h-1038962001\",\"weight\":1},{\"node1Id\":\"floorConnection_h1665332854\",\"node2Id\":\"space_h-217148651\",\"weight\":1},{\"node1Id\":\"floorConnection_h-1712102163\",\"node2Id\":\"space_h-1038962001\",\"weight\":1},{\"node1Id\":\"psw_h-260381518\",\"node2Id\":\"space_h2086578003\",\"weight\":1},{\"node1Id\":\"psw_h-260381518\",\"node2Id\":\"space_h1197401197\",\"weight\":1},{\"node1Id\":\"landmark_h1159858036\",\"node2Id\":\"space_h2086578003\",\"weight\":1},{\"node1Id\":\"landmark_h-737275099\",\"node2Id\":\"space_h1197401197\",\"weight\":1},{\"node1Id\":\"floorConnection_h-1712102163\",\"node2Id\":\"space_h2086578003\",\"weight\":1},{\"node1Id\":\"psw_h-1472817065\",\"node2Id\":\"space_h-1108955678\",\"weight\":1},{\"node1Id\":\"psw_h-1135339993\",\"node2Id\":\"space_h1990993977\",\"weight\":1},{\"node1Id\":\"psw_h-1581827855\",\"node2Id\":\"space_h911938541\",\"weight\":1},{\"node1Id\":\"psw_h-1594477077\",\"node2Id\":\"space_h1922371737\",\"weight\":1},{\"node1Id\":\"psw_h1662901615\",\"node2Id\":\"space_h-1636909911\",\"weight\":1},{\"node1Id\":\"psw_h-1525086990\",\"node2Id\":\"space_h540264407\",\"weight\":1},{\"node1Id\":\"psw_h-1525086990\",\"node2Id\":\"space_h-1030341857\",\"weight\":1},{\"node1Id\":\"psw_h-388977348\",\"node2Id\":\"space_h-1030341857\",\"weight\":1},{\"node1Id\":\"psw_h493845650\",\"node2Id\":\"space_h-1030341857\",\"weight\":1},{\"node1Id\":\"psw_h493845650\",\"node2Id\":\"space_h1743168299\",\"weight\":1},{\"node1Id\":\"psw_h-848114432\",\"node2Id\":\"space_h1743168299\",\"weight\":1},{\"node1Id\":\"psw_h45102571\",\"node2Id\":\"space_h-1487372014\",\"weight\":1},{\"node1Id\":\"psw_h-1472817065\",\"node2Id\":\"space_h268178738\",\"weight\":1},{\"node1Id\":\"psw_h-1135339993\",\"node2Id\":\"space_h268178738\",\"weight\":1},{\"node1Id\":\"psw_h-1581827855\",\"node2Id\":\"space_h268178738\",\"weight\":1},{\"node1Id\":\"psw_h-1594477077\",\"node2Id\":\"space_h268178738\",\"weight\":1},{\"node1Id\":\"psw_h1662901615\",\"node2Id\":\"space_h268178738\",\"weight\":1},{\"node1Id\":\"psw_h-388977348\",\"node2Id\":\"space_h268178738\",\"weight\":1},{\"node1Id\":\"psw_h-848114432\",\"node2Id\":\"space_h268178738\",\"weight\":1},{\"node1Id\":\"psw_h45102571\",\"node2Id\":\"space_h268178738\",\"weight\":1},{\"node1Id\":\"landmark_h1159858036\",\"node2Id\":\"space_h-1108955678\",\"weight\":1},{\"node1Id\":\"landmark_h-737275099\",\"node2Id\":\"space_h-1487372014\",\"weight\":1}]}";
	Building b = gson.fromJson(json, Building.class);

	System.out.println("Generating Graph...");
	SparseGraph g = SparseGraph.importGraph(b);
	DisplayGraph.DisplayGraphNodesToConsole(g, false);
	System.out.println(g.isEdgePresent(0, 2));
	testAStarAlgorithm(g, 0, 3);
    }
}
