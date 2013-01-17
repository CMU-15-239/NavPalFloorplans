/**@author Chet
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.cmu;

import java.util.ArrayList;

import android.graphics.Bitmap;

/**
 * 
 * @author Chet
 */
public class ImageConvert
{

    private int[] pix = null;
    private byte[][] pixbyte = null;

    public Bitmap im;
    public ColorData C[];
    public int W, H;
    private String type;
    ArrayList<Landmark> LandmarksForFloorPlans = new ArrayList<Landmark>();

    public ImageConvert()
    {
    }

    /*
     * \brief Initializes the image
     * 
     * \param The bitmap image itself \param A Description of the butmap image
     */
    public ImageConvert(Bitmap a, String b)
    {
	im = a; // Store the bitmap image
	W = im.getWidth(); // Store the width and height dimensions
	H = im.getHeight();
	type = b; // Store the description of the image
    }

    /** Pulls color data and saves in a 1D array. */
    public void initializePixArray()
    {
	pix = new int[W * H];
	im.getPixels(pix, 0, W, 0, 0, W, H); // The colors are returned in the variable "pix"

	if (type.equals("Robot Map"))
	{
	    im.recycle();
	}
    }

    /** Displays the color by percentage representation */
    public void printColDat()
    {
	if (C == null)
	{
	    return;
	}
	System.out.printf("\nThere are %d colors total.\n", C.length);

	for (int q = 0; q < C.length; q++)
	{
	    System.out.printf("%h %f \n", C[q].i, C[q].d);
	}

    }

    public static double mag(int a, int b)
    {
	return Math.sqrt(Math.pow(a, 2) + Math.pow(b, 2));
    }

    // ___ _____ ___ _____ _____ _____ ___
    // | _`\( _ ) _`\ ( _ )_ _) /'\_/`\ _ ) _`\
    // | (_) ) ( ) | (_) )| ( ) | | | | | (_) | |_) )
    // | , /| | | | _ <'| | | | | | | (_) | _ | ,__/'
    // | |\ \| (_) | (_) )| (_) | | | | | | | | | | |
    // (_) (_)_____)____/'(_____) (_) (_) (_)_) (_)_)
    /*
     * The robot maps are no longer used, but in case one needs displayed, this is the code. Maps with svg files are preferred because more complex operations can be done on them.
     */

    /**
     * This cuts the bitmap from 24 bit color down to 16 colors. This makes it much easier to deal with when converting robot maps to grid representations.
     */
    public ColorData[] lowerBitRate()
    {// removes most of the LSB's color 0x246a3cfe -> 0x246030f0
	if (pix == null)
	{
	    this.initializePixArray();
	}
	int count = 0;
	// initialize array
	int cols[][][] = new int[4][4][4];

	for (int i = 0; i < 4; i++)
	{
	    for (int j = 0; j < 4; j++)
	    {
		for (int k = 0; k < 4; k++)
		{
		    cols[i][j][k] = 0;
		}
	    }
	}

	int R = 0, G = 0, B = 0;
	for (int i = 0; i < W; i++)
	{
	    for (int j = 0; j < H; j++)
	    {

		if (type.equals("Robot Map"))
		{// lowers bitrate on colors
		    R = (col(pix[i + j * W], "R")) & 192;
		    G = (col(pix[i + j * W], "G")) & 192;
		    B = (col(pix[i + j * W], "B")) & 192;
		}
		/*
		 * if(R>G+B){G=0;B=0;R=255;} else if(G>R+B){R=0;B=0;G=255;} else if(B>R+G){R=0;G=0;B=255;} else if(R>150 && G>150 && B>150){R=255;G=255;B=255;} else {R=0;G=0;B=0;}
		 */
		// System.out.printf("%h %h ",R,R<<16);
		pix[i + j * W] = 0xff000000 + (R << 16) + (G << 8) + (B);
		// System.out.printf("%h \n",0xff000000+ (R<<16)+(G<<8)+(B));

		if (cols[R / 64][G / 64][B / 64] == 0)
		{
		    count++;
		}
		cols[R / 64][G / 64][B / 64]++;

	    }

	}

	// counts how many colors are used

	// organizes colors into array
	C = new ColorData[count];
	count = 0;
	for (int i = 0; i < 4; i++)
	{
	    for (int j = 0; j < 4; j++)
	    {
		for (int k = 0; k < 4; k++)
		{
		    if (cols[i][j][k] > 0)
		    {
			C[count] = new ColorData(0xff000000 + (i * 64 << 16) + (j * 64 << 8) + (k * 64), (float) cols[i][j][k] / (W * H));
			count++;
		    }

		}
	    }
	}

	return C;
    }

    /**
     * This extracts the byte of interest from a 32bit color pixel. Argument c is the 32 bit integer and The String accepts values "R", "G", and "B" otherwise returning 0.
     */
    public int col(int c, String s)
    {// extracts color data from a color
     // System.out.printf("%h ",c);

	if (s.equals("R"))
	{
	    c = c >> 16;
	    c &= 255;
	}
	else
	    if (s.equals("G"))
	    {
		c = c >> 8;
		c &= 255;
	    }
	    else
		if (s.equals("B"))
		{
		    c &= 255;
		}
		else
		{
		    c = 0;
		}
	// System.out.printf("%h ",c);
	return c;
    }

    /** Gets the color at coordinates i,j in the pixel array. */
    public int getRGB(int i, int j)
    {
	return pix[i + j * W];
    }

    /** Organizes colors their abundance in the pixel array. Returns an array of ColorData. */
    public ColorData[] colByPercentage()
    {
	int index = 0;
	// reads in all colors and checks their frequency
	ArrayList<Point> colors = new ArrayList<Point>();// x is color, y is frequency
	for (int i = 0; i < W; i++)
	{
	    for (int j = 0; j < H; j++)
	    {
		if (j == 0 && i < 20)
		{
		    System.out.printf("%d %h\n", i, pix[i + j * W]);
		}
		boolean test = false;
		// if((i + j * W)>0)
		// if(pix[i + j * W]==pix[(i + j * W)-1] ){
		// colors.get(index).y++;
		// test = true;
		// }
		// else
		for (index = 0; index < colors.size();)
		{
		    {
			// count++;

			if (colors.get(index).x == pix[i + j * W])
			{
			    colors.get(index).y++;

			    test = true;
			    break;
			}
			index++;
		    }
		}
		if (!test)
		{
		    colors.add(new Point(pix[i + j * W], 1));
		}
	    }
	}
	// coverts to a color and percentage type, ColorData
	C = new ColorData[colors.size()];
	for (int i = 0; i < colors.size(); i++)
	{
	    C[i] = new ColorData(colors.get(i).x, (float) colors.get(i).y / (W * H));

	}
	return C;

    }

    /**
     * Determines what is traversable and what isn't. The color with the highest % is assumed to be unexplored. The next highest is free space and the next is walls. All stray colors are paired up with the closest match and assigned to the
     * same set (O or X).The nodes in Map are either boundary or free space.
     */
    public char[] evaluateBoundariesBotRep()
    {
	// most significant percentage of color is not traversable
	if (C == null)
	{
	    colByPercentage();
	}

	int max = 0;
	char B[] = new char[C.length];
	for (int i = 0; i < C.length; i++)
	{
	    B[i] = '!';
	    if (C[max].d < C[i].d)
	    {
		max = i;
	    }
	}
	// second most significant percentage represents floors
	boolean flag = false;
	int secondmax = 0;
	for (int i = 0; i < C.length; i++)
	{
	    if (i != max && (C[secondmax].d < C[i].d || !flag))
	    {
		secondmax = i;
		flag = true;

	    }
	}

	B[max] = 'X';
	B[secondmax] = '0';

	// if there is a significant percentage that is unaccounted for thus far,
	// it is assumed to be a wall
	for (int i = 0; i < C.length; i++)
	{
	    if (i != max && i != secondmax && C[i].d > .01)
	    {
		B[i] = 'X';
	    }
	    else
		if (i != max && i != secondmax && C[i].d < .01)
		{
		    int min = 0;
		    // insigificant percentages are matched with close colors...damn jpegs
		    for (int j = 0; j < C.length; j++)
		    {
			if (B[j] != '!' && compareCol(C[j].i, C[i].i) < compareCol(C[j].i, C[min].i))
			{
			    min = j;
			}
			B[i] = B[min];

		    }
		}

	}

	return B;
    }

    /**
     * Uses the Euclidean distance between channels to compare colors. There are better ways of doing this, but it isn't a big issue because this code is probably never used anyhow.
     */
    public double compareCol(int i, int j)
    {
	// treats colors like points in 3d space
	// close colors return lower numbers
	int R1 = col(i, "R");
	int G1 = col(i, "G");
	int B1 = col(i, "B");
	int R2 = col(j, "R");
	int G2 = col(j, "G");
	int B2 = col(j, "B");

	return Math.sqrt(Math.pow(R1 - R2, 2) + Math.pow(G1 - G2, 2) + Math.pow(B1 - B2, 2));

    }

    // ___ _ _____ _____ ___ ___ _ _____ _ _
    // ( _`\( ) ( _ ) _ ) _`\ ( _`\ ( ) ( _ ) ) ( )
    // | (_(_) | | ( ) | ( ) | (_) ) | |_) )| | | (_) | `\| |
    // | _) | | _ | | | | | | | , / | ,__/'| | _ | _ | , ` |
    // | | | |_( )| (_) | (_) | |\ \ | | | |_( )| | | | |`\ |
    // (_) (____/'(_____)_____)_) (_) (_) (____/'(_) (_)_) (_)

    /**
     * Reads in floorplans from an image file. Color data is also used here. White is assigned to free space, and black is boundary. Green is also free space as it designates doors in the maps that were used. In complete maps (including an
     * svg file) additional processing is done once the room data is read in and the vertices are determined. More specifically, the boundaries generated here are logically ANDed with the grid cells containing a line connecting two vertices
     * of the room (a wall/edge). postProcess tells the image to look for labels designating rooms. This works with reasonable accuracy but is unnecessary when the map includes svg data. These rooms are saved in Landmarks list and can be
     * called with imgCon.LandmarksForFloorPlans. A map is generated with nodes specifying boundary and free space. This is hardly sufficient for reading a map alone, it has been tweaked so that it creates more boundary than would make the
     * map usable. This is so that when it is ANDed with the svg data, it does not leave holes in walls and things of that sort.
     */
    public Node[][] floorPlanInterpret(int res, boolean postProcess, Map M)
    {

	boolean typeOfFloorplan = false; // true for standard, false for CMU type

	// *******<--CHANGING THE IMAGE

	if (typeOfFloorplan)
	{
	    /*
	     * This block processes non-CMU floor plans that are in black or white
	     */

	    // good for black and white floor plans
	    // snaps colors to either black or white
	    for (int i = 0; i < im.getWidth(); i++)
	    {
		for (int j = 0; j < im.getHeight(); j++)
		{
		    // If the pixel is not close to white (i.e., its RGB value is greater than 220),
		    // then make the pixel black (a boundary).
		    if ((pix[i + j * im.getWidth()] & 255) < 220) // 220 is close to 255 (white)
		    {
			// This means that unless pixel is very close to white, it is boundary
			pix[i + j * im.getWidth()] = 0xff000000;
		    }
		    else
		    {
			// Otherwise the pixel is an open area and is assigned white if its value is > 220
			pix[i + j * im.getWidth()] = 0xffffffff;
		    }
		}
	    }
	}
	else
	{
	    /*
	     * This block processes CMU floor plans where doors appear green
	     */

	    for (int i = 0; i < im.getWidth(); i++)
	    {
		for (int j = 0; j < im.getHeight(); j++)
		{
		    // Apparently this checks if the current pixel is green?
		    // TODO: Try to understand what this condition is doing?
		    if ((pix[i + j * im.getWidth()] & 255) - 10 != (pix[i + j * im.getWidth()] & 255) && (pix[i + j * im.getWidth()] >> 8 & 255) - 10 > (pix[i + j * im.getWidth()] >> 16 & 255))
		    {
			pix[i + j * im.getWidth()] = 0xffffffff;
			// sets green things to white. They are then treated as free space.
		    }
		    else
			if ((pix[i + j * im.getWidth()] & 255) < 240)
			{
			    pix[i + j * im.getWidth()] = 0xff000000;
			    // things that are not green or not very close to white are changed to black
			}
			else
			{
			    pix[i + j * im.getWidth()] = 0xffffffff;

			}

		}

	    }
	}
	int rad = res;

	// Allocating space for the Node matrix, making a scaled down version of the original image and representing it as nodes.
	Node nodes[][] = new Node[1 + im.getHeight() / res][1 + im.getWidth() / res];
	int widthFixed = (im.getWidth() / res);
	int heightFixed = (im.getHeight() / res);

	// Initialize the node matrix as indicated by the D* Lite paper
	for (int i = 0; i < widthFixed + 1; i++)
	{
	    for (int j = 0; j < heightFixed + 1; j++)
	    {
		nodes[j][i] = new Node(i, j, M);
		nodes[j][i].set = 'X';
		nodes[j][i].g = Float.POSITIVE_INFINITY;
		nodes[j][i].rhs = Float.POSITIVE_INFINITY;
	    }
	}

	/*
	 * ROOM DETECTION--LOOKS WITHIN A CERTAIN RADIUS FOR A MOSTLY BLACK AREA IF THERE IS NO ADDITIONAL BLACK IN A SLIGHTLY LARGER AREA, IT IS ASSUMED TO BE A LABEL MARKING A ROOM (APPEARING LIKE AN ISLAND OF BLACK ON DIAGRAM) THIS IS
	 * NOT NECESSARY ON COMPLETE MAPS BECAUSE THE SVG FILE DESIGNATES THE ROOMS ANYHOW
	 */
	if (postProcess)
	{
	    int buffer = 2;

	    double test2 = 0, test3 = 0;
	    boolean flag = false;
	    int roomCheckRad = 12;// a number that work well for the resolution used
	    for (roomCheckRad = 8; roomCheckRad <= 22; roomCheckRad++)
	    {// this is pretty slow
		for (int i = roomCheckRad; i < im.getWidth() - roomCheckRad - buffer; i++)
		{
		    // System.out.println(i);
		    for (int j = roomCheckRad; j < im.getHeight() - roomCheckRad - buffer; j++)
		    {
			test2 = 0;
			test3 = 0;
			flag = false;
			for (int m = roomCheckRad * -1; m <= roomCheckRad && flag == false; m++)
			{
			    for (int n = roomCheckRad * -1; n <= roomCheckRad && flag == false; n++)
			    {

				if ((pix[i + m + (j + n) * im.getWidth()]) == 0xff000000 && Math.max(m, n) < roomCheckRad - 1 && Math.min(m, n) > -roomCheckRad + 1)
				{

				    if (mag(m, n) < roomCheckRad - buffer)
				    {
					test2++;

				    }
				}
				if ((pix[i + m + (j + n) * im.getWidth()]) == 0xff000000)
				{
				    if (mag(m, n) < roomCheckRad)
				    {
					test3++;

				    }
				}

				// if (flag)break;
			    }// if (flag)break;
			}

			if (flag == false && test2 / (double) (roomCheckRad - buffer) / (double) (roomCheckRad - buffer) / 3.141 > .5)
			{
			    if (test2 / test3 > 0.97)
			    {// The amount of black in the inner ring must be at least 97 percent of the amount in the
			     // outer ring.

				// fix this line
				// LandmarksForFloorPlans.add(new Landmark(j / rad, i / rad, "Room " + LandmarksForFloorPlans.size(),-1));

				for (int m = roomCheckRad * -1; m <= roomCheckRad && flag == false; m++)
				{
				    for (int n = roomCheckRad * -1; n <= roomCheckRad && flag == false; n++)
				    {
					if (mag(m, n) < roomCheckRad)
					{
					    pix[i + m + (j + n) * im.getWidth()] = 0xffffffff;
					}
				    }
				}

			    }
			}
		    }
		}
	    }
	}
	// End postprocess

	int test = 0;
	for (int i = 0; i < im.getWidth(); i += rad)
	{
	    for (int j = 0; j < im.getHeight(); j += rad)
	    {
		test = 0;
		for (int m = -rad / 2; m <= rad / 2; m++)
		{
		    for (int n = -rad / 2; n <= rad / 2; n++)
		    {
			try
			{
			    if (pix[i + m + (j + n) * im.getWidth()] == 0xff000000)
			    {
				// if (mag(m, n) < rad) {
				test++;

				// }
			    }
			}
			catch (ArrayIndexOutOfBoundsException e)
			{
			}

		    }
		}

		if (test / (double) rad / (double) rad < .10)
		{
		    nodes[j / rad][i / rad].set = '0';
		    // pix[i+j*im.getWidth()] = 0xffff0000;
		}
		nodes[j / rad][i / rad].x = j / rad;
		nodes[j / rad][i / rad].y = i / rad;

	    }
	}

	/*
	 * for (int k = 0; k < nodes.length; k++) { for (int l = 0; l < nodes[0].length; l++) {
	 * 
	 * System.out.print(nodes[k][l].set);
	 * 
	 * } System.out.println(); }
	 */

	// REMOVES OUTSIDE AREA
	/**
	 * This uses a cluster counting algorithm to determine what is outside of the floor plan. This is useful because the inside of the floorplan is white and it is traversable. The outside is also white, but it is not. This is handled
	 * by the svg info as well. Map.RoomsMap[x][y] = -1 is areas that are outside the grid.
	 */

	/*
	 * int removeBorder[][] = new int[nodes.length][nodes[0].length]; for (int i = 0; i < nodes.length; i++) { for (int j = 0; j < nodes[0].length; j++) { if (nodes[i][j].set == 'X') { removeBorder[i][j] = Integer.MAX_VALUE; } else {
	 * removeBorder[i][j] = Integer.MIN_VALUE; } } }
	 * 
	 * int count = 0; for (int i = 0; i < nodes.length; i++) { for (int j = 0; j < nodes[0].length; j++) { //removeBorder[i][j] = count; int y = neighborValue(nodes, removeBorder, i, j);
	 * //System.out.println("Count and neighborval "+count+" "+y); if (removeBorder[i][j] == Integer.MAX_VALUE) { } else if (y < Integer.MAX_VALUE) { removeBorder[i][j] = y; } else { removeBorder[i][j] = count++; }
	 * 
	 * fixIt(nodes, removeBorder, i, j, y);
	 * 
	 * 
	 * 
	 * 
	 * }
	 * 
	 * 
	 * }
	 * 
	 * boolean isInBorder = false; ArrayList<Integer> border = new ArrayList<Integer>(0);
	 * 
	 * for (int i = 0; i < nodes.length; i++) { for (int k = 0; k < nodes[0].length; k++) { if (i * k == 0 || i + 1 == nodes.length || k + 1 == nodes[0].length) { isInBorder = false; for (int j = 0; j < border.size(); j++) { if
	 * (border.get(j) == removeBorder[i][k]) { //System.out.println("AASDF"); isInBorder = true; break; } } if (!isInBorder) { border.add(removeBorder[i][k]); } } } } for (int i = 0; i < nodes.length; i++) { for (int k = 0; k <
	 * nodes[0].length; k++) { isInBorder = false; for (int j = 0; j < border.size(); j++) { if (border.get(j) == removeBorder[i][k]) { isInBorder = true; break; } } if (isInBorder && removeBorder[i][k] < Integer.MAX_VALUE) {
	 * nodes[i][k].set = 'X'; } } }
	 */
	return nodes;

    }

    /*
     * This is used for cluster counting. When two clusters are found to meet and thus be the same cluster, this corrects them so that they are labeled the same way.
     */
    public void fixIt(Node[][] n, int[][] r, int i, int j, int y)
    {
	try
	{
	    if (n[i - 1][j].set == '0')
	    {
		if (r[i - 1][j] > y)
		{
		    cleanUp(r[i - 1][j], y, r, i);
		}
	    }
	}
	catch (Exception e)
	{
	}
	;
	try
	{
	    if (n[i + 1][j].set == '0')
	    {
		if (r[i + 1][j] > y)
		{
		    cleanUp(r[i + 1][j], y, r, i);
		}
	    }
	}
	catch (Exception e)
	{
	}
	;
	try
	{
	    if (n[i][j - 1].set == '0')
	    {
		if (r[i][j - 1] > y)
		{
		    cleanUp(r[i][j - 1], y, r, i);
		}
	    }
	}
	catch (Exception e)
	{
	}
	;
	try
	{
	    if (n[i][j + 1].set == '0')
	    {
		if (r[i][j + 1] > y)
		{
		    cleanUp(r[i][j + 1], y, r, i);
		}
	    }
	}
	catch (Exception e)
	{
	}
	;

    }

    /**
     * Assists in cleaning up old clusters. This actually sets the values. Not a lot happening, just some renumbering.
     */
    public void cleanUp(int OLD, int NEW, int[][] r, int m)
    {
	for (int i = 0; i <= m; i++)
	{
	    for (int j = 0; j < r[0].length; j++)
	    {
		if (r[i][j] == OLD)
		{
		    r[i][j] = NEW;
		}
	    }
	}

	return;

    }

    /* More cluster counting stuff. This looks for the smallest value and uses this when renumbering. */
    public int neighborValue(Node[][] n, int[][] r, int i, int j)
    {
	int val = Integer.MAX_VALUE;
	// System.out.println(i+" " +j);
	try
	{
	    if (n[i - 1][j].set == '0')
	    {
		if (r[i - 1][j] != Integer.MIN_VALUE)
		{
		    val = r[i - 1][j];
		}
	    }
	    // System.out.println("Neighbor1: "+ r[i - 1][j]);
	}
	catch (ArrayIndexOutOfBoundsException e)
	{
	    // System.out.println("Neighbor1: DNE");
	}
	;
	try
	{
	    if (n[i + 1][j].set == '0')
	    {
		if (r[i + 1][j] != Integer.MIN_VALUE)
		{
		    val = Math.min(r[i + 1][j], val);
		}
	    }
	    // System.out.println("Neighbor2: "+ r[i + 1][j]);
	}
	catch (ArrayIndexOutOfBoundsException e)
	{
	    // System.out.println("Neighbor2: DNE");
	}
	;
	try
	{
	    if (n[i][j - 1].set == '0')
	    {
		if (r[i][j - 1] != Integer.MIN_VALUE)
		{
		    val = Math.min(r[i][j - 1], val);
		}
	    }
	    // System.out.println("Neighbor3: "+ r[i][j-1]);
	}
	catch (ArrayIndexOutOfBoundsException e)
	{
	    // System.out.println("Neighbor3: DNE");
	}
	;
	try
	{
	    if (n[i][j + 1].set == '0')
	    {
		if (r[i][j + 1] != Integer.MIN_VALUE)
		{
		    val = Math.min(r[i][j + 1], val);
		}
	    }
	    // System.out.println("Neighbor4: "+ r[i][j+1]);
	}
	catch (ArrayIndexOutOfBoundsException e)
	{
	    // System.out.println("Neighbor4: DNE");
	}
	;
	return val;
	// check for other numbers

    }

}
