/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.cmu;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import android.os.Environment;
import android.util.Log;

/**
 * 
 * @author Chet
 */
public class Map implements Vertex
{
    ArrayList<Room> rooms = new ArrayList<Room>(0);
    Room GoalRoom = null;
    HashMap<Integer, Room> mapOfIdToRoom; // Map to be able to refer to the rooms if we have their ID's

    int time = 1;
    final boolean nodiag = false;// only 4 neighbors instead of 8
    Building B;
    float rhs;
    boolean isEnroute;
    private int SIZEX, SIZEY;	// Defined when the x-y values are passed into the constructor for this class
    
    public Node node[][];	// Store a x-y grid of Nodes that, in this context, represents a network of rooms in a particular floor.
    
    public ArrayList<Node> OPEN = new ArrayList<Node>(0);
    public ArrayList<Node> CLOSED = new ArrayList<Node>(0);
    int id;

    AnnotationsLayer annotations = new AnnotationsLayer();
    int RoomsMap[][];

    public Vector<Action> allActions = new Vector<Action>();
    public int res = 0;
    public int scale = 0;

    /*
     * \brief When an instance of this class is created, 
     */
    public Map(int x, int y)
    {
	Log.d(Constants.TAG, "Map::Map(" + x + ", " + y + ") A new map instance created.");

	SIZEX = x;
	SIZEY = y;
	node = new Node[x][y];
    }

    /** Sets the size of the map */
    public void setSize(int i, int j)
    {
	SIZEX = i;
	SIZEY = j;
    }

    /** x dimension of grid */

    public int getSizeX()
    {
	return SIZEX;
    }

    /** y dimension of grid */
    public int getSizeY()
    {
	return SIZEY;
    }

    /** passes message along to annotations layer to sync */
    public void syncObstacles()
    {
	annotations.syncObstacles(this);
    }

    /**
     * When using a complete map, this matrix holds room data. At each index, the value is equal to the id of the room. If it is not a room, it is tagged as -1;
     */
    public void initializeRoomMatrix()
    {
	RoomsMap = new int[getSizeX()][getSizeY()];
	int k = 0;
	for (int i = 0; i < getSizeX(); i++)
	{
	    for (int j = 0; j < getSizeY(); j++)
	    {
		RoomsMap[i][j] = -1;

		if (k < rooms.size() && rooms.get(k).pointInRoom(i, j))
		{
		    RoomsMap[i][j] = k;
		}
		else
		{
		    for (k = 0; k < rooms.size(); k++)
		    {
			if (rooms.get(k).pointInRoom(i, j))
			{
			    RoomsMap[i][j] = k;
			    break;
			}
		    }
		}
	    }
	}
    }

    /**
     * Overloaded method-- returns the Label of the room that Node w, coordinates x and y, Landmark, or Obstacle are in.
     */

    // TODO: The method getState can be re-factored to reduce code size since each overloaded method contains duplicate code.
    public String getState(Node w)
    {
	if (w == null || w.x == -1 || w.y == -1)
	    return "null";
	if (RoomsMap != null)
	{

	    if (RoomsMap[w.x][w.y] != -1)
	    {
		if (rooms.get(RoomsMap[w.x][w.y]).getColor().equals("Hallway"))
		{
		    return rooms.get(RoomsMap[w.x][w.y]).getLabel() + " (Hallway)" + RoomsMap[w.x][w.y];
		}
		return rooms.get(RoomsMap[w.x][w.y]).getLabel() + " (Room)" + RoomsMap[w.x][w.y];
	    }
	    return "Not in Room";
	}
	else
	    if (!rooms.isEmpty())
	    {
		for (Room R : this.rooms)
		{
		    if (R.pointInRoom(w.x, w.y))
		    {
			if (R.getColor().equals("Hallway"))
			{
			    return R.getLabel() + " (Hallway)";
			}
			return R.getLabel() + " (Room)";
		    }
		}

		return "Not in Room";

	    }
	return "";
    }

    public String getState(int x, int y)
    {
	if (RoomsMap != null)
	{

	    if (RoomsMap[x][y] != -1)
	    {
		if (rooms.get(RoomsMap[x][y]).getColor().equals("Hallway"))
		{
		    return rooms.get(RoomsMap[x][y]).getLabel() + " (Hallway)";
		}
		return rooms.get(RoomsMap[x][y]).getLabel() + " (Room)";
	    }
	    return "Not in Room";
	}
	else
	    if (!rooms.isEmpty())
	    {
		for (Room R : this.rooms)
		{
		    if (R.pointInRoom(x, y))
		    {
			if (R.getColor().equals("Hallway"))
			{
			    return R.getLabel() + " (Hallway)";
			}
			return R.getLabel() + " (Room)";
		    }
		}

		return "Not in Room";

	    }
	return "";
    }

    public String getState(Landmark L)
    {
	if (RoomsMap != null)
	{

	    if (RoomsMap[L.x][L.y] != -1)
	    {
		if (rooms.get(RoomsMap[L.x][L.y]).getColor().equals("Hallway"))
		{
		    return rooms.get(RoomsMap[L.x][L.y]).getLabel() + " (Hallway)";
		}
		return rooms.get(RoomsMap[L.x][L.y]).getLabel() + " (Room)";
	    }
	    return "Not in Room";
	}
	else
	    if (!rooms.isEmpty())
	    {
		for (Room R : this.rooms)
		{
		    if (R.pointInRoom(L.x, L.y))
		    {
			if (R.getColor().equals("Hallway"))
			{
			    return R.getLabel() + " (Hallway)";
			}
			return R.getLabel() + " (Room)";
		    }
		}

		return "Not in Room";

	    }
	return "";
    }

    public String getState(Obstacle O)
    {
	if (RoomsMap != null)
	{

	    if (RoomsMap[O.x][O.y] != -1)
	    {
		if (rooms.get(RoomsMap[O.x][O.y]).getColor().equals("Hallway"))
		{
		    return rooms.get(RoomsMap[O.x][O.y]).getLabel() + " (Hallway)";
		}
		return rooms.get(RoomsMap[O.x][O.y]).getLabel() + " (Room)";
	    }
	    return "Not in Room";
	}
	else
	    if (!rooms.isEmpty())
	    {
		for (Room R : this.rooms)
		{
		    if (R.pointInRoom(O.x, O.y))
		    {
			if (R.getColor().equals("Hallway"))
			{
			    return R.getLabel() + " (Hallway)";
			}
			return R.getLabel() + " (Room)";
		    }
		}

		return "Not in Room";

	    }
	return "";
    }

    /**
     * This is useful for seeing what the map currently looks like. param = 0 -- shows whether node is boundary or free space param = 1 -- shows the distance to the goal node (g) param = 2 -- shows the distance to the goal node (rhs) param
     * = 3 -- shows the cost of crossing node
     */

    public void print(int param)
    {
	/*
	 * PRINTS DATA CONTAINED IN GRID
	 */

	for (int i = 0; i < SIZEX; i++)
	{
	    for (int j = 0; j < SIZEY; j++)
	    {
		switch (param)
		{
		case 0:// which set the data is in
		    try
		    {
			if (node[i][j].set == '0')
			{
			    System.out.print(" ");
			}
			else
			    if (node[i][j].set == '1')
			    {
				System.out.print(".");
			    }
			    else
				if (node[i][j].set == '2')
				{
				    System.out.print("*");
				}
				else
				    if (node[i][j].set == 'X')
				    {
					System.out.print("X");
				    }
				    else
					if (node[i][j].set == 'x')
					{
					    System.out.print("#");
					}
		    }
		    catch (Exception e)
		    {
			System.out.print(i + " " + j + "\n");
		    }
		    break;
		case 1:// min dist to point
		    if (node[i][j].g < 10)
		    {
			System.out.print(" ");
		    }
		    System.out.printf("%4.2f   ", node[i][j].g);
		    break;
		case 2:// min dist to point
		    if (node[i][j].rhs < 10)
		    {
			System.out.print(" ");
		    }
		    System.out.printf("%4.2f   ", node[i][j].g);
		    break;
		case 3:// cost
		    System.out.print(node[i][j].cost.evalCost());

		    break;

		}
	    }
	    System.out.print("\n");
	}

    }

    /**
     * This tests to see if a path is attempting to cut between two diagonal barriers that have no space between them
     */
    public boolean diagCase(int i, int j, int k, int l)
    {
	/*
	 * prevents path from forming diagonally between two barriers ex* X00* instead of X00* 0X*0 0X0* 0*X0 00X* *000 ***0
	 */
	int test = 0;
	try
	{
	    if (k * l == 0)
	    {
		return false;// not diagonal
	    }
	    if (this.node[i + k][j].set == 'X' || this.node[i + k][j].set == 'x')
	    {
		test++;
	    }
	}
	catch (Exception e)
	{
	}
	try
	{
	    if (this.node[i][j + l].set == 'X' || this.node[i][j + l].set == 'x')
	    {
		test++;
	    }

	}
	catch (Exception e)
	{
	}
	if (test == 2)
	{
	    return true;// trying to cut through diagonals
	}
	return false;
    }

    /**
     * Finds the timestep of the youngest neighbor. This is used before rolling the timeToOPEN back when Path Replanning.
     */
    public int youngestNeighbor(Node n)
    {
	ArrayList<Node> Neighbor = new ArrayList<Node>(0);

	int i = n.x;
	int j = n.y;
	for (int k = -1; k <= 1; k++)
	{// each neighboring point
	    for (int l = -1; l <= 1; l++)
	    {
		// System.out.print(node[i + k][j + l].set);
		if (!(k == 0 && l == 0) && !nodiag)
		{// not neighbors with self
		    try
		    {
			// is traversable
			if (!(node[i + k][j + l].set == 'X' || node[i + k][j + l].set == 'x' || this.diagCase(i, j, k, l))) // if it is diagonal condition
			{
			    Neighbor.add(node[i + k][j + l]);
			}

		    }
		    catch (Exception e)
		    {// off the edge of grid
		    }
		}

	    }// System.out.println();
	}

	int min = Integer.MAX_VALUE;
	for (int h = 0; h < Neighbor.size(); h++)
	{
	    if (Neighbor.get(h).timeToOPEN < min && Neighbor.get(h).timeToOPEN != -1)
	    {
		min = Neighbor.get(h).timeToOPEN;
	    }
	}
	// System.out.println("Neighbors found: "+ Neighbor.size());
	return min;
    }

    /**
     * Will return all accessible neighbors of node n Assumes person cannot cut through diagonal corridors of width 1 or walk through walls. Sounds like a safe assumption...
     */
    public ArrayList<Node> getNeighbors(Node n)
    {
	ArrayList<Node> Neighbor = new ArrayList<Node>(0);
	// System.out.println("FINDING NEIGHBORS: ");
	int i = n.x;
	int j = n.y;

	// This code loops through the neighbor indices relative to the current node
	// (i.e., see diagram below, the node location is at (0, 0))
	//
	//	( 1, -1) ( 1,  0) ( 1, 1)
	//	( 0, -1) ( 0,  0) ( 0, 1)
	//	(-1, -1) (-1,  0) (-1, 1)
	//
	for (int k = -1; k <= 1; k++)
	{// each neighboring point
	    for (int l = -1; l <= 1; l++)
	    {
		// System.out.print(node[i + k][j + l].set);
		if (!(k == 0 && l == 0) && !nodiag)
		{// not neighbors with self
		    try
		    {
			// is traversable
			if (!(node[i + k][j + l].set == 'X' || node[i + k][j + l].set == 'x' || this.diagCase(i, j, k, l))) // if it is diagonal condition
			{
			    Neighbor.add(node[i + k][j + l]);
			}

		    }
		    catch (Exception e)
		    {// off the edge of grid
		    }
		}

	    }// System.out.println();
	}
	// System.out.println("Neighbors found: "+ Neighbor.size());
	return Neighbor;
    }

    /**
     * Will return all accessible neighbors of node n with constraint that their set is equal to character c. Assumes person cannot cut through diagonal corridors of width 1 or walk through walls. Sounds like a safe assumption...
     */
    public ArrayList<Node> getNeighbors(Node n, char c)
    {

	ArrayList<Node> Neighbor = new ArrayList<Node>(0);
	// System.out.println("FINDING NEIGHBORS: ");
	int i = n.x;
	int j = n.y;
	for (int k = -1; k <= 1; k++)
	{// each neighboring point
	    for (int l = -1; l <= 1; l++)
	    {
		try
		{
		    // System.out.print(node[i + k][j + l].set);
		}
		catch (Exception e)
		{
		    // System.out.print("#");
		}
		if (!(k == 0 && l == 0) && !nodiag)
		{// not neighbors with self
		    try
		    {
			// is traversable
			if (!(node[i + k][j + l].set == 'X' || node[i + k][j + l].set == 'x' || this.diagCase(i, j, k, l)) && c == node[i + k][j + l].set) // if it is diagonal condition
			{
			    Neighbor.add(node[i + k][j + l]);
			}

		    }
		    catch (Exception e)
		    {// off the edge of grid
		    }
		}

	    }
	    // System.out.println();
	}
	// System.out.println("Neighbors found: " + Neighbor.size());
	return Neighbor;
    }

    /**
     * ALL NODES ARE CREATED AND N CONTAINS POINTERS TO ALL NEIGHBORS ON THE GRID
     */
    public void initialize()
    {
	Log.d(Constants.TAG, "Map - initialize() called.");

	// creates all of the nodes
	for (int i = 0; i < SIZEX; i++)
	{
	    for (int j = 0; j < SIZEY; j++)
	    {
		node[i][j] = new Node(i, j, this);
		node[i][j].timeToOPEN = -1;
		node[i][j].timeToCLOSED = -1;
		node[i][j].hasChildren = false;
		node[i][j].set = '0';
		node[i][j].M = this;
		OPEN.clear();
		CLOSED.clear();
		node[i][j].g = Float.POSITIVE_INFINITY;
		node[i][j].rhs = Float.POSITIVE_INFINITY;
	    }
	}

    }

    /**
     * All nodes are reset but not recreated. This is used when the map is still good, but the path is being recalculated.
     */
    public void reinitialize()
    {

	// creates all of the nodes

	for (int i = 0; i < SIZEX; i++)
	{
	    for (int j = 0; j < SIZEY; j++)
	    {
		if (node[i][j].set != 'X' && node[i][j].set != 'x')
		{
		    node[i][j].set = '0';
		}
		node[i][j].timeToOPEN = -1;
		node[i][j].timeToCLOSED = -1;
		node[i][j].hasChildren = false;
		OPEN.clear();
		CLOSED.clear();
		node[i][j].p = null;
		node[i][j].g = Float.POSITIVE_INFINITY;
		node[i][j].rhs = Float.POSITIVE_INFINITY;
	    }
	}
	time = 1;

    }

    /** Adds a new Landmark at position (x,y) with name st */
    public void addLandmark(int x, int y, String st)
    {
	annotations.L.add(new Landmark(x, y, st, annotations.assignID(), this));
    }

    /** Adds a new Obstacle at position (x,y) and size s with name st */
    public void addObstacle(int x, int y, int s, String st)
    {
	Obstacle obsTemp = annotations.obstacleAt(x, y);
	if (obsTemp != null)
	{
	}
	else
	{
	    annotations.O.add(new Obstacle(x, y, s, st, annotations.assignID(), this));
	    rooms.get(RoomsMap[annotations.O.get(annotations.O.size() - 1).x][annotations.O.get(annotations.O.size() - 1).y]).ob.add(annotations.O.get(annotations.O.size() - 1));
	}
	syncObstacles();
    }

    /**
     * Uses timestamps on the nodes to bring the Map back to it's state at time t
     */
    public void rollback(int t)
    {
	System.out.println("Rollback to time: " + t);
	for (int i = 0; i < OPEN.size();)
	{
	    if (OPEN.get(i).timeToOPEN >= t && OPEN.get(i).timeToOPEN != Integer.MAX_VALUE)
	    {
		OPEN.get(i).timeToOPEN = -1;
		OPEN.get(i).p = null;
		OPEN.get(i).set = '0';
		OPEN.get(i).rhs = Float.POSITIVE_INFINITY;
		OPEN.get(i).g = Float.POSITIVE_INFINITY;
		OPEN.remove(i);
	    }
	    else
	    {
		i++;
	    }

	}
	for (int i = 0; i < CLOSED.size();)
	{// timeToOPEN is always less than timeToClose
	    if (CLOSED.get(i).timeToOPEN >= t && CLOSED.get(i).timeToOPEN != Integer.MAX_VALUE)
	    {
		CLOSED.get(i).timeToCLOSED = -1;
		CLOSED.get(i).timeToOPEN = -1;
		CLOSED.get(i).p = null;
		CLOSED.get(i).set = '0';
		CLOSED.get(i).rhs = Float.POSITIVE_INFINITY;
		CLOSED.get(i).g = Float.POSITIVE_INFINITY;
		CLOSED.remove(i);
	    }
	    else
		if (CLOSED.get(i).timeToCLOSED >= t && CLOSED.get(i).timeToOPEN != Integer.MAX_VALUE)
		{
		    CLOSED.get(i).timeToCLOSED = -1;
		    CLOSED.get(i).set = '1';
		    OPEN.add(CLOSED.get(i));
		    CLOSED.remove(i);
		}
		else
		{
		    i++;
		}

	}

	cleanUpOpen();
	time = t;
	// System.out.println("Completed Rollback");
    }

    /**
     * The node is along the path of the Graph nodes and will be used in the grid planner.
     */
    public boolean alongPath(Node A)
    {
	return (RoomsMap != null && (RoomsMap[A.x][A.y] == -1 || rooms.get(RoomsMap[A.x][A.y]).isEnroute()));
    }

    /**
     * Gets the best node of the OPEN set. This node is the most likely candidate for the next step in an optimal solution path. It tries to return a grid cell that is enRoute(), but if it can't, it gives one that isn't. This has been seen
     * to stray from the path of the graph nodes. I'm not entirely sure why. It would be faster if it didn't but it works either way.
     */
    public Node getBestNode(Node Start, Node End)
    {
	Node B = null;
	Node C = null;

	for (int i = 0; i < OPEN.size(); i++)
	{
	    if (alongPath(OPEN.get(i)))
	    {
		if (B == null || (eval(B, Start) * B.cost.evalCost() > eval(OPEN.get(i), Start) * OPEN.get(i).cost.evalCost() || (B.rhs * B.cost.evalCost() == OPEN.get(i).rhs * OPEN.get(i).cost.evalCost() && (mag(B, Start) + mag(B, End) > mag(OPEN.get(i), Start) + mag(OPEN.get(i), End)))))
		{
		    B = OPEN.get(i);
		}
	    }
	    else
	    {
		if (C == null || (eval(C, Start) * C.cost.evalCost() > eval(OPEN.get(i), Start) * OPEN.get(i).cost.evalCost() || (C.rhs * C.cost.evalCost() == OPEN.get(i).rhs * OPEN.get(i).cost.evalCost() && (mag(C, Start) + mag(C, End) > mag(OPEN.get(i), Start) + mag(OPEN.get(i), End)))))
		{
		    C = OPEN.get(i);
		}
	    }

	}
	if (B != null)
	    return B;
	// System.out.println("Gave a silly node");
	return C;

    }

    /**
     * Makes sure everything on the open list has the correct set and does not have a parent in the open list.
     */
    public void cleanUpOpen()
    {
	for (int i = 0; i < OPEN.size(); i++)
	{
	    try
	    {
		if (OPEN.get(i).p == null || OPEN.get(i).p.set == '0' || OPEN.get(i).p.set == '1')
		{
		    OPEN.get(i).set = '0';
		    OPEN.get(i).p = null;
		    OPEN.remove(i);
		}
	    }
	    catch (Exception e)
	    {
	    }
	}
    }

    /**
     * Sets up the cost for each node in the map. This currently only considers the width of the halls. Narrow halls have a higher cost. The way this works: All grid squares adjacent to a wall have a high cost and as the squares get
     * progressively farther from walls, their cost function lowers. This will close off paths when the user is wheelchair bound and may have a size that is larger than a grid cell. These paths can still be found only if no others are
     * available.
     */
    public void initializeCostFunction(int userSize)
    {
	for (int i = 0; i < this.SIZEX; i++)
	{
	    for (int j = 0; j < this.SIZEY; j++)
	    {
		if (this.node[i][j].set == 'X')
		{

		    for (int m = -1 * userSize; m <= userSize; m++)
		    {
			for (int n = -1 * userSize; n <= userSize; n++)
			{
			    try
			    {
				if (this.node[i + m][j + n].cost.sizeRestraint < userSize - Math.max(Math.abs(m), Math.abs(n)) + 1)
				{
				    this.node[i + m][j + n].cost.sizeRestraint = userSize - Math.max(Math.abs(m), Math.abs(n)) + 1;
				}
			    }
			    catch (ArrayIndexOutOfBoundsException e)
			    {
			    }
			}
		    }

		}
	    }
	}

    }

    /**
     * This is the evaluation function used by D*Lite. It is the cost so far...to node n plus the straight line distance to the start. Remeber- D* works backwards.
     */
    private float eval(Node n, Node start)
    {
	return (float) (Math.min(n.rhs, n.g) + mag(n, start));
    }

    /** Returns the euclidean distance between two nodes */
    public float mag(Node a, Node b)
    {
	return (float) Math.pow(Math.pow(a.x - b.x, 2) + Math.pow(a.y - b.y, 2), .5);
    }

    /**
     * \brief ANDs the SVG file with the corresponding PBNG version to better define the map boundaries
     * 
     * This method uses the svg files to clean up what is considered boundaries on the map.
     * It also forms a network of Rooms/States linked by actions. This is only called when 
     * an svg file is available for the current map. This is where the original map is AND'ed
     * with the map from processFloorPlan().
     */

    void refineMap()
    {

	/* Gets rid of unnecessary barriers and marks doorways. This will aid in making a network of rooms. */
	int ExtraMap[][] = new int[getSizeX()][getSizeY()];
	for (int i = 0; i < getSizeX(); i++)
	{
	    for (int j = 0; j < getSizeY(); j++)
	    {
		ExtraMap[i][j] = 0;
	    }
	}
	for (int i = 0; i < rooms.size(); i++)
	{
	    for (int j = 0; j < rooms.get(i).vertex.size() - 1; j++)
	    {
		line_draw((int) rooms.get(i).vertex.get(j).x, (int) rooms.get(i).vertex.get(j).y, (int) rooms.get(i).vertex.get(j + 1).x, (int) rooms.get(i).vertex.get(j + 1).y, ExtraMap, 0);

	    }

	    line_draw((int) rooms.get(i).vertex.get(rooms.get(i).vertex.size() - 1).x, (int) rooms.get(i).vertex.get(rooms.get(i).vertex.size() - 1).y, (int) rooms.get(i).vertex.get(0).x, (int) rooms.get(i).vertex.get(0).y, ExtraMap, 0);
	}
	// logical AND
	for (int i = 0; i < getSizeX(); i++)
	{
	    for (int j = 0; j < getSizeY(); j++)
	    {
		if (ExtraMap[i][j] == 1 && node[i][j].set == 'X')
		{
		    node[i][j].set = 'X';
		}
		else
		    if (node[i][j].set != '!')
		    {
			node[i][j].set = '0';
		    }
	    }
	}
	boolean Adj[][] = new boolean[rooms.size()][rooms.size()];
	for (int i = 0; i < rooms.size(); i++)
	{
	    for (int j = 0; j < rooms.size(); j++)
	    {
		Adj[i][j] = false;
	    }
	}
	int count = 0;
	// makes a list of all immediate neighbors to a current space (that has been marked as a doorway)
	ArrayList<Integer> tempList = new ArrayList<Integer>();
	ArrayList<Integer> temp2List = new ArrayList<Integer>();
	for (int i = 0; i < getSizeX(); i++)
	{
	    for (int j = 0; j < getSizeY(); j++)
	    {
		if (node[i][j].set == '!')
		{
		    /*
		     * if (RoomsMap[i][j]==23) { test = true; if(test)System.out.println("RoomID:"+RoomsMap[i][j ]); }
		     */
		    tempList.clear();
		    temp2List.clear();

		    for (int k = -1; k < 2; k++)
		    {

			for (int l = -1; l < 2; l++)
			{
			    if ((k != 0 || l != 0) && k * l == 0)
			    {
				if (i + k >= 0 && j + l >= 0 && i + k < getSizeX() && j + l < getSizeY())
				{
				    // spaces in rooms that link rooms
				    if (RoomsMap[i][j] != RoomsMap[i + k][j + l] && RoomsMap[i + k][j + l] != -1 && node[i + k][j + l].set != 'X')
				    {
					tempList.add(RoomsMap[i + k][j + l]);

				    }
				    // spaces not in rooms that link rooms
				    if (RoomsMap[i][j] == -1 && RoomsMap[i + k][j + l] != -1)
				    {
					temp2List.add(RoomsMap[i + k][j + l]);
				    }
				}
			    }
			}
		    }
		    // attempts to match out of bounds rooms with a room
		    if (RoomsMap[i][j] == -1 && temp2List.size() > 1)
		    {
			RoomsMap[i][j] = temp2List.get(0);
		    }

		    // creates an adjacency matrix
		    for (int m = 0; m < tempList.size(); m++)
		    {
			// System.out.println("Debug: " + RoomsMap[i][j] + " " + tempList.get(m));
			if (RoomsMap[i][j] >= 0 && tempList.get(m) >= 0)
			{
			    Adj[RoomsMap[i][j]][tempList.get(m)] = true;
			    Adj[tempList.get(m)][RoomsMap[i][j]] = true;

			}
		    }
		    for (int m = 0; m < temp2List.size(); m++)
		    {
			for (int n = m; n < temp2List.size(); n++)
			{
			    // System.out.println("Debug: " + RoomsMap[i][j] + " " + tempList.get(m));
			    if (temp2List.get(m) >= 0 && temp2List.get(n) >= 0 && temp2List.get(m) != temp2List.get(n))
			    {
				Adj[temp2List.get(m)][temp2List.get(n)] = true;
				Adj[temp2List.get(n)][temp2List.get(m)] = true;

			    }
			}
		    }
		    node[i][j].set = '0';
		}
	    }
	}

	count = 0;
	// makes all of the actions.
	for (int i = 0; i < rooms.size(); i++)
	{
	    for (int j = 0; j < rooms.size(); j++)
	    {
		if (Adj[i][j] && i != j)
		{
		    rooms.get(i).Actions.add(new Action("to_" + rooms.get(j).getLabel(), j, i, count++));
		    rooms.get(i).Actions.get(rooms.get(i).Actions.size() - 1).setTo(rooms.get(j));
		    rooms.get(i).Actions.get(rooms.get(i).Actions.size() - 1).setFrom(rooms.get(i));

		    if (i < j)
		    {
			allActions.add(rooms.get(i).Actions.get(rooms.get(i).Actions.size() - 1));
			// allActions.add(new Action(rooms.get(i).getLabel()+"_to_" + rooms.get(j).getLabel(), j, i, count));
		    }
		    // System.out.print("T");
		}
		else
		{
		    // System.out.print("F");
		}
	    }

	    // System.out.println(" " + rooms.get(i).Actions.size() + " ");
	}

	// see unconnected rooms
	boolean check = false;
	for (int i = 0; i < rooms.size(); i++)
	{
	    check = false;

	    for (int j = 0; j < rooms.size(); j++)
	    {
		if (Adj[i][j])
		{
		    check = true;
		}
	    }
	    if (check == false)
	    {
	    }

	}
    }

    /** Bresenham's line algorithm. When called, it traces the border of a room and looks for open spaces (doors) */

    public boolean line_draw(int x0, int y0, int x1, int y1, int[][] ExtraMap, int opt)
    {
	int dx, dy, sx, sy, err, e2;
	dx = Math.abs(x1 - x0);
	dy = Math.abs(y1 - y0);
	if (x0 < x1)
	{
	    sx = 1;
	}
	else
	{
	    sx = -1;
	}
	if (y0 < y1)
	{
	    sy = 1;
	}
	else
	{
	    sy = -1;
	}
	err = dx - dy;
	int i = 0;
	while (i == 0)
	{
	    if (opt == 0)
	    {
		try
		{
		    ExtraMap[y0][x0] = 1;
		    if (node[y0][x0].set != 'X')
		    {

			node[y0][x0].set = '!';
		    }
		}
		catch (Exception e)
		{
		}
	    }
	    else
		if (opt == 1)
		{
		    try
		    {
			if (node[y0][x0].set == 'X')
			{
			    return false;
			}
		    }
		    catch (Exception e)
		    {
		    }
		}
	    if (x0 == x1 && y0 == y1)
	    {
		break;
	    }
	    e2 = 2 * err;
	    if (e2 > -1 * dy)
	    {
		err = err - dy;
		x0 = x0 + sx;
	    }
	    if (e2 < dx)
	    {
		err = err + dx;
		y0 = y0 + sy;
	    }
	}

	return true;
    }

    /**
     * Getting Data to the Markov Model This is an easy way to get the rooms. They are unchanging throughout the program and their id is the same as their index in the array.
     */
    public ArrayList<Room> getRooms()
    {
	if (MapList.MAPS[MainInterface.CURRENTMAPLOAD][5].equals("C"))
	{
	    return rooms;
	}
	else
	{
	    System.out.println("This map has no rooms");
	}
	return null;
    }

    boolean mapCreated = false;

    /**
     * This method takes and id and returns the room that has that ID
     * 
     * @param ID
     *            of the room to be return
     * @return room returned
     */
    public Room getRoomById(int ID)
    {
	if (mapCreated)
	{
	    return mapOfIdToRoom.get(ID);
	}
	else
	{
	    createMap();
	    mapCreated = true;
	    return mapOfIdToRoom.get(ID);
	}

    }

    /**
     * Method that populates the mapOfIdToRoom hashmap
     */
    private void createMap()
    {
	mapOfIdToRoom = new HashMap<Integer, Room>();
	for (Room room : rooms)
	    mapOfIdToRoom.put(room.getID(), room);

    }

    /*
     * Contains all data about the Landmarks, Obstacles, and Exits
     */

    public AnnotationsLayer getAnnotations()
    {
	return annotations;
    }

    @Override
    public Vector<? extends Edge> getEdges()
    {
	return annotations.E;

    }

    @Override
    public Vertex getSubVertex()
    {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public int getHierarchy()
    {
	return 1;
    }

    @Override
    public float getCumulativeCost()
    {
	return rhs;
    }

    @Override
    public void setCumulativeCost(float a)
    {
	this.rhs = a;
    }

    @Override
    public float getCost()
    {
	return 10;
    }

    @Override
    public boolean getEnroute()
    {
	return isEnroute;
    }

    @Override
    public void setEnroute(boolean a)
    {
	isEnroute = a;
    }

    @Override
    public Vertex getParent()
    {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public void setParent(Vertex a)
    {
	// TODO Auto-generated method stub

    }

    @Override
    public Vertex raise()
    {
	return B;
    }

    @Override
    public String toString()
    {
	return "Map " + id + " from " + B.name;
    }

    // Debug Methods
    public void dumpNodeArrayToFile(String lbl)
    {
	//System.out.println("Entering dumpNodeArrayToFile");

	//System.out.println("Checking if can write to sdcard");
	// Setup the file for output
	File root = Environment.getExternalStorageDirectory();
	if (root.canWrite())
	{
	    //System.out.println("Able to write!");
	    String filename = root.getPath() + "/NavPalSaves/" + "floor_" + (MainInterface.CURRENTMAPLOAD + 1) +"_Nodes_file_" + lbl + ".txt";

	    //System.out.println("Filename: '" + filename + "'");
	    try
	    {
		//System.out.println("Trying to Create the BufferedWriter.");
		BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
		//System.out.println("BufferedWriter was created.");

		//System.out.println("Attempting to write Node Data to file.");
		
//		if (node != null)
//		{
//		    System.out.println("'node' array is defined");
//		    System.out.println("Dimension of 'node' array is [" + SIZEX + ", " + SIZEY + "]");
//		    System.out.println("Size of 'node' array is " + node.length);
//		}
//		else
//		{
//		    System.out.println("'node' array is NULL");
//		}
		
		for (int i = 0; i<SIZEX; i++)
		{
		    for (int j = 0; j<SIZEY; j++)
		    {
			String writeString = "";

			if (node[i][j] != null)
			{
			    writeString = node[i][j].x + " " + node[i][j].y + " ";

			    if (node[i][j].p != null)
			    {
				writeString += node[i][j].p.x + " " + node[i][j].p.y + "\n";
			    }
			    else
			    {
				writeString += "-1 -1\n";
			    }
			}
			else
			{
			    writeString = "# node[" + i + "][" + j + "] is null";
			}

			writer.write(writeString);
		    }
		}

		//System.out.println("Node Data Written to file.");
		//System.out.println("Output file closed.");
		writer.close();
	    }
	    catch (IOException e1)
	    {
		//System.out.println("Exception Occured while writing to output file.");
		e1.printStackTrace();
	    }
	    catch(Exception e)
	    {
		//System.out.println("Another exception occured!");
		e.printStackTrace();
	    }
	}
	else
	{
	    //System.out.println("NOT Able to write!");
	}
	
	//System.out.println("Leaving dumpNodeArrayToFile");
    }

    public void dumpRoomsArrayListToFile(String lbl)
    {
	File root = Environment.getExternalStorageDirectory();
	if (root.canWrite())
	{
	    //System.out.println("Able to write!");
	    String filename = root.getPath() + "/NavPalSaves/" + "floor_" + (MainInterface.CURRENTMAPLOAD + 1) +"_RoomsArray_file_" + lbl + ".txt";

	    //System.out.println("Filename: '" + filename + "'");
	    try
	    {
		//System.out.println("Trying to Create the BufferedWriter.");
		BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
		//System.out.println("BufferedWriter was created.");

		//System.out.println("Attempting to write Node Data to file.");

//		if (rooms != null)
//		{
//		    System.out.println("'rooms' array is defined");
//		    System.out.println("Size of 'rooms' array is " + rooms.size());
//		}
//		else
//		{
//		    System.out.println("'rooms' array is NULL");
//		}
		BufferedWriter out = new BufferedWriter(writer);
		    
		String roomsStr = "";
		for(int i=0; i<rooms.size(); i++)
		{
		    roomsStr += rooms.get(i).toString() + "\n";
		}
   
		out.write(roomsStr);
		out.close();
	    }
	    catch (IOException e)
	    {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
	    catch(Exception e)
	    {
		//System.out.println("Another exception occured!");
		e.printStackTrace();
	    }
	}
    }

    public void dumpRoomsMapArrayToFile(String lbl)
    {
	// Setup the file for output
	File root = Environment.getExternalStorageDirectory();
	if (root.canWrite())
	{
	    //System.out.println("Able to write!");
	    String filename = root.getPath() + "/NavPalSaves/" + "floor_" + (MainInterface.CURRENTMAPLOAD + 1) + "_RoomsMap_file_" + lbl + ".txt";

	    //System.out.println("Filename: '" + filename + "'");
	    try
	    {
		//System.out.println("Trying to Create the BufferedWriter.");
		BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
		//System.out.println("BufferedWriter was created.");

		//System.out.println("Attempting to write Node Data to file.");

//		if (RoomsMap != null)
//		{
//		    System.out.println("'RoomsMap' array is defined");
//		    System.out.println("Dimension of 'RoomsMap' array is [" + SIZEX + ", " + SIZEY + "]");
//		}
//		else
//		{
//		    System.out.println("'RoomsMap' array is NULL");
//		}

		BufferedWriter out = new BufferedWriter(writer);
	    
		for (int i = 0; i<SIZEX; i++)
		{
		    String writeString = "";
		    for (int j = 0; j<SIZEY; j++)
		    {
			if (RoomsMap[i][j] == -1)
			{
			    writeString += "     ";
			}
			else
			{
			    writeString += String.format("%4d ", RoomsMap[i][j]);
			}
		    }
		    writeString += "\n";
		    out.write(writeString);
		}

		out.close();
	    }
	    catch (IOException e)
	    {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
	    catch(Exception e)
	    {
		//System.out.println("Another exception occured!");
		e.printStackTrace();
	    }
	}
    }
}
