package edu.cmu.ri.rcommerce;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Scanner;

/**
 * Represents a free space map of arbitrary size.
 * 
 * Usually, you will want to construct this by calling the loadFromStream method.
 * There are three possible designations for each cell: UNKNOWN, OCCUPIED, or FREE.
 * @author Nisarg
 *
 */
public class ObstacleMap {
	public int xSize, ySize;
	public enum Cell{FREE_SPACE,OCCUPIED,UNKNOWN};
	public Cell[][] cellArray;
	
	public ObstacleMap(int xSize,int ySize) {
		this.xSize = xSize;
		this.ySize = ySize;
		
		cellArray = new Cell[ySize][xSize];
	}
	
	public static ObstacleMap loadFromStream(Reader in) throws IOException
	{
		BufferedReader mapReader = new BufferedReader(in);
		String sizeLine = mapReader.readLine();
		Scanner sizeScanner = new Scanner(sizeLine);
		int mapX = sizeScanner.nextInt();
		int mapY = sizeScanner.nextInt();
		sizeScanner.close();
		
		ObstacleMap map = new ObstacleMap(mapX,mapY);
		
		String line = mapReader.readLine();
		for (int i = 0; i<mapY; i++)
		{
			for (int j=0; j<mapX; j++)
			{
				char c = line.charAt(j);
				switch (c)
				{
				case '-':
					map.cellArray[i][j] = Cell.UNKNOWN;
					break;
				case '#':
					map.cellArray[i][j] = Cell.OCCUPIED;
					break;
				case ' ':
					map.cellArray[i][j] = Cell.FREE_SPACE;
					break;
				}
			}
			
			line = mapReader.readLine();
		}
		return map;
	}

}
