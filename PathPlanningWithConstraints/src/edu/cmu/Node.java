/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.cmu;

/**
 * 
 * @author Chet
 */
public class Node
{
    /**
     * The nodes are equivalent to rooms or buildings, except that they are on the grid level. 
     * All nodes have a pointer to the map that they are on as well as what room 
     * (M.rooms.get(M.RoomsMap[this.x][this.y])). g and rhs are used similarly to how they are 
     * used in Koenig's D*Lite. This could probably get by without using g.
     * 
     * \todo These public members need to be made private and accessed through getters/setters
     */
    public Map M;
    public int x, y; 	// coordinates
    public Node p; 	// pointer to parent
    public char set; 	// 0 is neither OPEN nor CLOSED, 1 is OPEN, 2 is CLOSED
    public float g; 	// The estimate cost of the objective function
    public float rhs; 	//
    public int timeToOPEN;
    public int timeToCLOSED;
    public boolean hasChildren = false;
    public Cost cost = new Cost();

    public Node()
    {
	init(0, 0, null);
//	x = 0;
//	y = 0;
//	set = '0';
//	p = null;
//	timeToOPEN = -1;	// Negative time signifies that a node is not OPEN or CLOSED yet.
//	timeToCLOSED = -1;
//	M = null;		// Added by Gary since the Map was not being assigned anything
    }

    public Node(int a, int b, Map M)
    {
	init(a, b, M);
//	x = a;
//	y = b;
//	set = '0';
//	p = null;
//	timeToOPEN = -1;	// Negative time signifies that a node is not OPEN or CLOSED yet.
//	timeToCLOSED = -1;
//	this.M = M;		// Added by Gary since the Map was not being assigned anything
    }

    private void init(int a, int b, Map M)
    {
	x = a;
	y = b;
	set = '0';
	p = null;
	timeToOPEN = -1;	// Negative time signifies that a node is not OPEN or CLOSED yet.
	timeToCLOSED = -1;
	this.M = M;		// Added by Gary since the Map was not being assigned anything
    }

    public String toString()
    {
	if (M != null)
	{
	    return "Node: (" + x + "," + y + ") -- " + " Room: " + M.rooms.get(M.RoomsMap[this.x][this.y]).getID();
	}
	
	return "Node: (" + x + "," + y + ") -- " + " Room: (not defined)";
    }

    public void print(int i)
    {
	switch (i)
	{
	case 0:
	    System.out.print("X:" + x + " Y:" + y + " ");
	    break;
	}
    }
}
