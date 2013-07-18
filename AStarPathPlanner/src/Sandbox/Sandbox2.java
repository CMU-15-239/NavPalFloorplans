package Sandbox;

import importgraph.Building;
import importgraph.EdgeWeight;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.google.gson.Gson;

import database.FloorPlanCreatorMongoDatabaseHandle;
import Algorithms.GraphAStar;
import Graph.GraphEdge;
import Graph.NavGraphNode;
import Graph.SparseGraph;
import Graph.Utils.DisplayGraph;
import Graph.Utils.GenerateGraphs;
import Graph.Utils.SaveGraph;
import NavPal.NavPalSparseGraph;
import NavPal.Utils.NavPalGraphUtils;

import org.json.simple.*;

public class Sandbox2
{
    private static void testAStarAlgorithm(SparseGraph g, int startNodeIndex, int endNodeIndex)
    {	
	System.out.println("Starting Test...");

	//String graphPath = "C:\\Users\\ggiger\\Documents\\ProgrammingProjects\\CheckedOutSVNRepos\\OctaveScripts\\trunk\\PathPlotting\\";
	String graphPath = "./";
	String graphFilename = "graph.txt";
	String dotFilename = "graph.dot";

	SaveGraph.SaveGraphToFile(g, graphPath + graphFilename);

	/*
	 * Plan a route using the AStar algorithm
	 */
	System.out.println("Planning Route...");

	GraphAStar astar = new GraphAStar(g, startNodeIndex, endNodeIndex);
	ArrayList<Integer> path = astar.getPathAsListOfNodeIds();
	dislayPathNodeIds(path);

	System.out.println("Text Based Graph Representation");
	DisplayGraph.DisplayGraphToConsole(g, true);

	NavPalGraphUtils.SaveGraphAsDotFileForGraphViz(g, graphPath + dotFilename);

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
     * Connect to the Mongo DB and retrieve the building plan
     */
    public static String retrieveBuilding(String buildingName, boolean testCase, boolean escapeJson)
    {
	String jsonStr = null;
	
	FloorPlanCreatorMongoDatabaseHandle dbHandle = new FloorPlanCreatorMongoDatabaseHandle();
	dbHandle.establishDatabaseConnection();
	dbHandle.connectToDatabase("flow");

	// Return the default test string that was originally provided with this package
	if (testCase)
	{
	    // Original hard coded string
	    jsonStr = "{\"name\":\"buiding1\",\"id\":\"buildingGraph_h36274153\",\"floors\":[{\"name\":\"1\",\"imageId\":\"safdsa\",\"imageScale\":1,\"spaces\":[{\"type\":\"space\",\"edges\":[\"psw_h-260381518\",\"landmark_h1159858036\",\"floorConnection_h1665332854\"],\"id\":\"space_h-217148651\",\"spaceType\":\"room\",\"label\":\"s1\",\"walls\":[{\"p1\":{\"x\":0,\"y\":0},\"p2\":{\"x\":0,\"y\":10},\"isDoor\":false},{\"p1\":{\"x\":0,\"y\":10},\"p2\":{\"x\":10,\"y\":10},\"isDoor\":false},{\"p1\":{\"x\":10,\"y\":0},\"p2\":{\"x\":10,\"y\":1},\"isDoor\":false},{\"p1\":{\"x\":10,\"y\":1},\"p2\":{\"x\":10,\"y\":5},\"isDoor\":true},{\"p1\":{\"x\":10,\"y\":5},\"p2\":{\"x\":10,\"y\":10},\"isDoor\":false},{\"p1\":{\"x\":0,\"y\":0},\"p2\":{\"x\":10,\"y\":0},\"isDoor\":false}]},{\"type\":\"space\",\"edges\":[\"psw_h-260381518\",\"landmark_h-737275099\",\"floorConnection_h-1712102163\"],\"id\":\"space_h-1038962001\",\"spaceType\":\"room\",\"label\":\"s2\",\"walls\":[{\"p1\":{\"x\":10,\"y\":0},\"p2\":{\"x\":20,\"y\":0},\"isDoor\":false},{\"p1\":{\"x\":20,\"y\":0},\"p2\":{\"x\":20,\"y\":25},\"isDoor\":false},{\"p1\":{\"x\":10,\"y\":10},\"p2\":{\"x\":20,\"y\":25},\"isDoor\":false},{\"p1\":{\"x\":10,\"y\":5},\"p2\":{\"x\":10,\"y\":10},\"isDoor\":false},{\"p1\":{\"x\":10,\"y\":1},\"p2\":{\"x\":10,\"y\":5},\"isDoor\":true},{\"p1\":{\"x\":10,\"y\":0},\"p2\":{\"x\":10,\"y\":1},\"isDoor\":false}]}],\"psws\":[{\"type\":\"psw\",\"edges\":[\"space_h-217148651\",\"space_h-1038962001\"],\"id\":\"psw_h-260381518\",\"pswType\":\"door\",\"lineRep\":{\"p1\":{\"x\":10,\"y\":1},\"p2\":{\"x\":10,\"y\":5},\"isDoor\":true}}],\"floorConnections\":[{\"type\":\"floorConnection\",\"edges\":[\"space_h-217148651\"],\"id\":\"floorConnection_h1665332854\",\"label\":\"evil stairs\",\"pointRep\":{\"x\":10,\"y\":10},\"floorConnectionType\":\"stairs\"},{\"type\":\"floorConnection\",\"edges\":[\"space_h-1038962001\"],\"id\":\"floorConnection_h-1712102163\",\"label\":\"good stairs\",\"pointRep\":{\"x\":20,\"y\":20},\"floorConnectionType\":\"stairs\"}],\"landmarks\":[{\"type\":\"landmark\",\"edges\":[\"space_h-217148651\"],\"id\":\"landmark_h1159858036\",\"label\":\"candyman\",\"description\":\"Asdfafa\",\"pointRep\":{\"x\":3,\"y\":3}},{\"type\":\"landmark\",\"edges\":[\"space_h-1038962001\"],\"id\":\"landmark_h-737275099\",\"label\":\"42\",\"description\":\"1as\",\"pointRep\":{\"x\":20,\"y\":25}}]},{\"name\":\"2\",\"imageId\":\"safdsa2\",\"imageScale\":1,\"spaces\":[{\"type\":\"space\",\"edges\":[\"psw_h-260381518\",\"landmark_h1159858036\",\"floorConnection_h-1712102163\"],\"id\":\"space_h2086578003\",\"spaceType\":\"room\",\"label\":\"s3\",\"walls\":[{\"p1\":{\"x\":0,\"y\":0},\"p2\":{\"x\":0,\"y\":10},\"isDoor\":false},{\"p1\":{\"x\":0,\"y\":10},\"p2\":{\"x\":10,\"y\":10},\"isDoor\":false},{\"p1\":{\"x\":10,\"y\":0},\"p2\":{\"x\":10,\"y\":1},\"isDoor\":false},{\"p1\":{\"x\":10,\"y\":1},\"p2\":{\"x\":10,\"y\":5},\"isDoor\":true},{\"p1\":{\"x\":10,\"y\":5},\"p2\":{\"x\":10,\"y\":10},\"isDoor\":false},{\"p1\":{\"x\":0,\"y\":0},\"p2\":{\"x\":10,\"y\":0},\"isDoor\":false}]},{\"type\":\"space\",\"edges\":[\"psw_h-260381518\",\"landmark_h-737275099\"],\"id\":\"space_h1197401197\",\"spaceType\":\"room\",\"label\":\"s4\",\"walls\":[{\"p1\":{\"x\":10,\"y\":0},\"p2\":{\"x\":20,\"y\":0},\"isDoor\":false},{\"p1\":{\"x\":20,\"y\":0},\"p2\":{\"x\":20,\"y\":25},\"isDoor\":false},{\"p1\":{\"x\":10,\"y\":10},\"p2\":{\"x\":20,\"y\":25},\"isDoor\":false},{\"p1\":{\"x\":10,\"y\":5},\"p2\":{\"x\":10,\"y\":10},\"isDoor\":false},{\"p1\":{\"x\":10,\"y\":1},\"p2\":{\"x\":10,\"y\":5},\"isDoor\":true},{\"p1\":{\"x\":10,\"y\":0},\"p2\":{\"x\":10,\"y\":1},\"isDoor\":false}]}],\"psws\":[{\"type\":\"psw\",\"edges\":[\"space_h2086578003\",\"space_h1197401197\"],\"id\":\"psw_h-260381518\",\"pswType\":\"door\",\"lineRep\":{\"p1\":{\"x\":10,\"y\":1},\"p2\":{\"x\":10,\"y\":5},\"isDoor\":true}}],\"floorConnections\":[{\"type\":\"floorConnection\",\"edges\":[],\"id\":\"floorConnection_h1665332854\",\"label\":\"evil stairs\",\"pointRep\":{\"x\":10,\"y\":15},\"floorConnectionType\":\"stairs\"},{\"type\":\"floorConnection\",\"edges\":[\"space_h2086578003\"],\"id\":\"floorConnection_h-1712102163\",\"label\":\"good stairs\",\"pointRep\":{\"x\":10,\"y\":10},\"floorConnectionType\":\"stairs\"}],\"landmarks\":[{\"type\":\"landmark\",\"edges\":[\"space_h2086578003\"],\"id\":\"landmark_h1159858036\",\"label\":\"candyman\",\"description\":\"Asdfafa\",\"pointRep\":{\"x\":3,\"y\":3}},{\"type\":\"landmark\",\"edges\":[\"space_h1197401197\"],\"id\":\"landmark_h-737275099\",\"label\":\"42\",\"description\":\"1as\",\"pointRep\":{\"x\":20,\"y\":25}}]}],\"floorConnectionRefs\":[{\"type\":\"floorConnection\",\"edges\":[\"floorGraph_h-1514562576\",\"floorGraph_h228425249\"],\"id\":\"floorConnection_h1665332854\",\"label\":\"evil stairs\",\"floorConnectionType\":\"stairs\"},{\"type\":\"floorConnection\",\"edges\":[\"floorGraph_h-1514562576\",\"floorGraph_h228425249\"],\"id\":\"floorConnection_h-1712102163\",\"label\":\"good stairs\",\"floorConnectionType\":\"stairs\"}],\"edgeWeights\":[{\"node1Id\":\"psw_h-260381518\",\"node2Id\":\"space_h-217148651\",\"weight\":1},{\"node1Id\":\"psw_h-260381518\",\"node2Id\":\"space_h-1038962001\",\"weight\":1},{\"node1Id\":\"landmark_h1159858036\",\"node2Id\":\"space_h-217148651\",\"weight\":1},{\"node1Id\":\"landmark_h-737275099\",\"node2Id\":\"space_h-1038962001\",\"weight\":1},{\"node1Id\":\"floorConnection_h1665332854\",\"node2Id\":\"space_h-217148651\",\"weight\":1},{\"node1Id\":\"floorConnection_h-1712102163\",\"node2Id\":\"space_h-1038962001\",\"weight\":1},{\"node1Id\":\"psw_h-260381518\",\"node2Id\":\"space_h2086578003\",\"weight\":1},{\"node1Id\":\"psw_h-260381518\",\"node2Id\":\"space_h1197401197\",\"weight\":1},{\"node1Id\":\"landmark_h1159858036\",\"node2Id\":\"space_h2086578003\",\"weight\":1},{\"node1Id\":\"landmark_h-737275099\",\"node2Id\":\"space_h1197401197\",\"weight\":1},{\"node1Id\":\"floorConnection_h-1712102163\",\"node2Id\":\"space_h2086578003\",\"weight\":1},{\"node1Id\":\"psw_h-1472817065\",\"node2Id\":\"space_h-1108955678\",\"weight\":1},{\"node1Id\":\"psw_h-1135339993\",\"node2Id\":\"space_h1990993977\",\"weight\":1},{\"node1Id\":\"psw_h-1581827855\",\"node2Id\":\"space_h911938541\",\"weight\":1},{\"node1Id\":\"psw_h-1594477077\",\"node2Id\":\"space_h1922371737\",\"weight\":1},{\"node1Id\":\"psw_h1662901615\",\"node2Id\":\"space_h-1636909911\",\"weight\":1},{\"node1Id\":\"psw_h-1525086990\",\"node2Id\":\"space_h540264407\",\"weight\":1},{\"node1Id\":\"psw_h-1525086990\",\"node2Id\":\"space_h-1030341857\",\"weight\":1},{\"node1Id\":\"psw_h-388977348\",\"node2Id\":\"space_h-1030341857\",\"weight\":1},{\"node1Id\":\"psw_h493845650\",\"node2Id\":\"space_h-1030341857\",\"weight\":1},{\"node1Id\":\"psw_h493845650\",\"node2Id\":\"space_h1743168299\",\"weight\":1},{\"node1Id\":\"psw_h-848114432\",\"node2Id\":\"space_h1743168299\",\"weight\":1},{\"node1Id\":\"psw_h45102571\",\"node2Id\":\"space_h-1487372014\",\"weight\":1},{\"node1Id\":\"psw_h-1472817065\",\"node2Id\":\"space_h268178738\",\"weight\":1},{\"node1Id\":\"psw_h-1135339993\",\"node2Id\":\"space_h268178738\",\"weight\":1},{\"node1Id\":\"psw_h-1581827855\",\"node2Id\":\"space_h268178738\",\"weight\":1},{\"node1Id\":\"psw_h-1594477077\",\"node2Id\":\"space_h268178738\",\"weight\":1},{\"node1Id\":\"psw_h1662901615\",\"node2Id\":\"space_h268178738\",\"weight\":1},{\"node1Id\":\"psw_h-388977348\",\"node2Id\":\"space_h268178738\",\"weight\":1},{\"node1Id\":\"psw_h-848114432\",\"node2Id\":\"space_h268178738\",\"weight\":1},{\"node1Id\":\"psw_h45102571\",\"node2Id\":\"space_h268178738\",\"weight\":1},{\"node1Id\":\"landmark_h1159858036\",\"node2Id\":\"space_h-1108955678\",\"weight\":1},{\"node1Id\":\"landmark_h-737275099\",\"node2Id\":\"space_h-1487372014\",\"weight\":1}]}";
	}
	else
	{
	    jsonStr = dbHandle.getJSONBuildingRepresentation(buildingName);
	    
	    if (escapeJson)
	    {
		// Escape all quote characters in the provided JSON string
		jsonStr =  JSONObject.escape(jsonStr);
	    }
	}

	return jsonStr;
    }

    /**
     * @param args
     */
    public static void main(String[] args)
    {
	// Testing Data
	String buildingName = "GarysBuilding";	// Initial test building 
	boolean useOriginalTestDataset = true;  // set to true to use original hard coded example, false to read building from DB 
	boolean escapeJSONString = false;

	/*
	 * Query the DB for the JSON representation of the building given the bulding name
	 */
	System.out.println("Attempting to open DB connection to MongoDB to query building.");
	
	// NOTE: To use the original test JSON string, set the variable useOriginalTestDataset to true
	String jsonBuilding = retrieveBuilding(buildingName, useOriginalTestDataset, escapeJSONString);

	if (jsonBuilding == null)
	{
	    System.out.println("The specified building '" + buildingName + "' has no corresponding representation from the Floorplan Creator. Make sure this building exists and has been published in the Floorplan creator.");
	    return;
	}
	System.out.println("Building:\n" + jsonBuilding + "\n");

	// Edited JSON string for GarysBuilding that changed the [ "Stair" 1] substring to "Stair 1" in order to get rid of the JsonSyntaxException.
	// Un-comment this string to test with GarysBuilding
	//jsonBuilding = "{ \"floorConnectionRefs\" : [ { \"floorConnectionType\" : \"STAIR\" , \"label\" : \"Stair 1\" , \"id\" : \"floorConnectionRef_h851499283\" , \"edges\" : [ \"floorGraph_h2108309502\"] , \"type\" : \"floorConnectionRef\"} , { \"floorConnectionType\" : \"STAIR\" , \"label\" : \"Stair 1\" , \"id\" : \"floorConnectionRef_h-686817767\" , \"edges\" : [ \"floorGraph_h941264972\"] , \"type\" : \"floorConnectionRef\"}] , \"floors\" : [ { \"floorConnections\" : [ { \"floorConnectionType\" : \"STAIR\" , \"pointRep\" : { \"y\" : \"444\" , \"x\" : \"450\"} , \"label\" : \"Stair 1\" , \"id\" : \"floorConnectionRef_h851499283\" , \"type\" : \"floorConnection\"}] , \"spaces\" : [ { \"walls\" : [ { \"isDoor\" : \"false\" , \"p2\" : { \"y\" : \"52\" , \"x\" : \"393\"} , \"p1\" : { \"y\" : \"278\" , \"x\" : \"394\"}} , { \"isDoor\" : \"true\" , \"p2\" : { \"y\" : \"278\" , \"x\" : \"394\"} , \"p1\" : { \"y\" : \"379\" , \"x\" : \"395\"}} , { \"isDoor\" : \"false\" , \"p2\" : { \"y\" : \"379\" , \"x\" : \"395\"} , \"p1\" : { \"y\" : \"538\" , \"x\" : \"391\"}} , { \"isDoor\" : \"false\" , \"p2\" : { \"y\" : \"538\" , \"x\" : \"391\"} , \"p1\" : { \"y\" : \"538\" , \"x\" : \"70\"}} , { \"isDoor\" : \"false\" , \"p2\" : { \"y\" : \"538\" , \"x\" : \"70\"} , \"p1\" : { \"y\" : \"53\" , \"x\" : \"70\"}} , { \"isDoor\" : \"false\" , \"p2\" : { \"y\" : \"53\" , \"x\" : \"70\"} , \"p1\" : { \"y\" : \"52\" , \"x\" : \"393\"}}] , \"label\" : \"Room 101\" , \"spaceType\" : \"room\" , \"id\" : \"space_h375260190\" , \"type\" : \"space\"} , { \"walls\" : [ { \"isDoor\" : \"true\" , \"p2\" : { \"y\" : \"278\" , \"x\" : \"394\"} , \"p1\" : { \"y\" : \"379\" , \"x\" : \"395\"}} , { \"isDoor\" : \"false\" , \"p2\" : { \"y\" : \"379\" , \"x\" : \"395\"} , \"p1\" : { \"y\" : \"538\" , \"x\" : \"391\"}} , { \"isDoor\" : \"false\" , \"p2\" : { \"y\" : \"542\" , \"x\" : \"514\"} , \"p1\" : { \"y\" : \"538\" , \"x\" : \"391\"}} , { \"isDoor\" : \"false\" , \"p2\" : { \"y\" : \"53\" , \"x\" : \"514\"} , \"p1\" : { \"y\" : \"542\" , \"x\" : \"514\"}} , { \"isDoor\" : \"false\" , \"p2\" : { \"y\" : \"52\" , \"x\" : \"393\"} , \"p1\" : { \"y\" : \"53\" , \"x\" : \"514\"}} , { \"isDoor\" : \"false\" , \"p2\" : { \"y\" : \"52\" , \"x\" : \"393\"} , \"p1\" : { \"y\" : \"278\" , \"x\" : \"394\"}}] , \"label\" : \"Hallway 1\" , \"spaceType\" : \"hallway\" , \"id\" : \"space_h-364169907\" , \"type\" : \"space\"}] , \"imageScale\" : \"1\" , \"imageId\" : \"image_66\" , \"name\" : \"1\"} , { \"floorConnections\" : [ { \"floorConnectionType\" : \"STAIR\" , \"pointRep\" : { \"y\" : \"423\" , \"x\" : \"655\"} , \"label\" : \"Stair 1\" , \"id\" : \"floorConnectionRef_h-686817767\" , \"type\" : \"floorConnection\"}] , \"spaces\" : [ { \"walls\" : [ { \"isDoor\" : \"false\" , \"p2\" : { \"y\" : \"43\" , \"x\" : \"732\"} , \"p1\" : { \"y\" : \"200\" , \"x\" : \"733\"}} , { \"isDoor\" : \"false\" , \"p2\" : { \"y\" : \"200\" , \"x\" : \"733\"} , \"p1\" : { \"y\" : \"200\" , \"x\" : \"673\"}} , { \"isDoor\" : \"true\" , \"p2\" : { \"y\" : \"200\" , \"x\" : \"673\"} , \"p1\" : { \"y\" : \"200\" , \"x\" : \"619\"}} , { \"isDoor\" : \"false\" , \"p2\" : { \"y\" : \"200\" , \"x\" : \"619\"} , \"p1\" : { \"y\" : \"199\" , \"x\" : \"573\"}} , { \"isDoor\" : \"false\" , \"p2\" : { \"y\" : \"199\" , \"x\" : \"573\"} , \"p1\" : { \"y\" : \"199\" , \"x\" : \"61\"}} , { \"isDoor\" : \"false\" , \"p2\" : { \"y\" : \"199\" , \"x\" : \"61\"} , \"p1\" : { \"y\" : \"42\" , \"x\" : \"60\"}} , { \"isDoor\" : \"false\" , \"p2\" : { \"y\" : \"42\" , \"x\" : \"60\"} , \"p1\" : { \"y\" : \"43\" , \"x\" : \"732\"}}] , \"label\" : \"Room 201\" , \"spaceType\" : \"room\" , \"id\" : \"space_h-1001544162\" , \"type\" : \"space\"} , { \"walls\" : [ { \"isDoor\" : \"true\" , \"p2\" : { \"y\" : \"200\" , \"x\" : \"673\"} , \"p1\" : { \"y\" : \"200\" , \"x\" : \"619\"}} , { \"isDoor\" : \"false\" , \"p2\" : { \"y\" : \"200\" , \"x\" : \"619\"} , \"p1\" : { \"y\" : \"199\" , \"x\" : \"573\"}} , { \"isDoor\" : \"false\" , \"p2\" : { \"y\" : \"296\" , \"x\" : \"575\"} , \"p1\" : { \"y\" : \"199\" , \"x\" : \"573\"}} , { \"isDoor\" : \"true\" , \"p2\" : { \"y\" : \"363\" , \"x\" : \"572\"} , \"p1\" : { \"y\" : \"296\" , \"x\" : \"575\"}} , { \"isDoor\" : \"false\" , \"p2\" : { \"y\" : \"495\" , \"x\" : \"574\"} , \"p1\" : { \"y\" : \"363\" , \"x\" : \"572\"}} , { \"isDoor\" : \"false\" , \"p2\" : { \"y\" : \"500\" , \"x\" : \"733\"} , \"p1\" : { \"y\" : \"495\" , \"x\" : \"574\"}} , { \"isDoor\" : \"false\" , \"p2\" : { \"y\" : \"200\" , \"x\" : \"733\"} , \"p1\" : { \"y\" : \"500\" , \"x\" : \"733\"}} , { \"isDoor\" : \"false\" , \"p2\" : { \"y\" : \"200\" , \"x\" : \"733\"} , \"p1\" : { \"y\" : \"200\" , \"x\" : \"673\"}}] , \"label\" : \"Hallway 2\" , \"spaceType\" : \"hallway\" , \"id\" : \"space_h-618948369\" , \"type\" : \"space\"} , { \"walls\" : [ { \"isDoor\" : \"false\" , \"p2\" : { \"y\" : \"199\" , \"x\" : \"61\"} , \"p1\" : { \"y\" : \"495\" , \"x\" : \"62\"}} , { \"isDoor\" : \"false\" , \"p2\" : { \"y\" : \"495\" , \"x\" : \"62\"} , \"p1\" : { \"y\" : \"495\" , \"x\" : \"574\"}} , { \"isDoor\" : \"false\" , \"p2\" : { \"y\" : \"495\" , \"x\" : \"574\"} , \"p1\" : { \"y\" : \"363\" , \"x\" : \"572\"}} , { \"isDoor\" : \"true\" , \"p2\" : { \"y\" : \"363\" , \"x\" : \"572\"} , \"p1\" : { \"y\" : \"296\" , \"x\" : \"575\"}} , { \"isDoor\" : \"false\" , \"p2\" : { \"y\" : \"296\" , \"x\" : \"575\"} , \"p1\" : { \"y\" : \"199\" , \"x\" : \"573\"}} , { \"isDoor\" : \"false\" , \"p2\" : { \"y\" : \"199\" , \"x\" : \"573\"} , \"p1\" : { \"y\" : \"199\" , \"x\" : \"61\"}}] , \"label\" : \"\" , \"spaceType\" : \"room\" , \"id\" : \"space_h-838368466\" , \"type\" : \"space\"}] , \"imageScale\" : \"1\" , \"imageId\" : \"image_67\" , \"name\" : \"2\"}] , \"id\" : \"buildingGraph_h-289099281\" , \"name\" : \"GarysBuilding\"}";

	/*
	 * Map the JSON building representation to POJOs.
	 */
	Gson gson = new Gson();

	Building b = gson.fromJson(jsonBuilding, Building.class);

	System.out.println("Generating Graph...");
	SparseGraph g = NavPalSparseGraph.importGraph(b);
	DisplayGraph.DisplayGraphNodesToConsole(g, false);
	System.out.println(g.isEdgePresent(0, 2));
	testAStarAlgorithm(g, 5, 6);
    }
}
