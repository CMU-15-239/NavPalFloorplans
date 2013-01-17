/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.cmu;

/**
 *
 * @author Chet
 */
/**
 * Obstacles are acting a little buggy. The transform is not scaling them properly and they seem to only be blocking grid cells for a fraction of their own size. They are circular, but this can be reworked. The obstacles have not been fully
 * functional since the introduction of graph level planning. It should still plan around obstacles at the grid level once they are scaled properly.
 */
public class Obstacle
{
    String shortMes = "";
    String longMes = "";
    public int x;
    public int y;
    public int size;
    public int id;

    public Obstacle()
    {
    }

    public Obstacle(int X, int Y, int s, String d, int id, Map M)
    {
	x = X;
	y = Y;
	size = s;
	shortMes = d;
	this.id = id;
    }

    public int getSize()
    {
	return size;
    }
}
