package edu.cmu.ri.rcommerce.sensor;

import java.util.Iterator;
import java.util.List;

import edu.cmu.ri.rcommerce.FastRandom;
import edu.cmu.ri.rcommerce.ObstacleMap;
import edu.cmu.ri.rcommerce.ObstacleMap.Cell;
import edu.cmu.ri.rcommerce.particleFilter.Particle2D;

/** Extension of RSSIMeasurer which kills particles that are out of bounds on the map by setting their weights to 0.
 * This should improve convergence by eliminating impossible states.
 * @author Nisarg
 */
public class MapConstrainedRSSIMeasurer2D<P extends Particle2D> extends RSSIMeasurer<P> {

	ObstacleMap map;
	FastRandom rand = new FastRandom();
	public MapConstrainedRSSIMeasurer2D(RSSIRuntimeProvider runtimeProvider, RSSICalibrateProvider<P> calibrateProvider,float epsilon,ObstacleMap map) {
		super(runtimeProvider, calibrateProvider,epsilon);
		this.map = map;
	}
	
	@Override
	public List<P> updateWeights(List<P> updatedState) {
		super.updateWeights(updatedState);
		
		Iterator<P> it = updatedState.iterator();
		while(it.hasNext())
		{
			P particle = it.next();
			//offset into map
			int xPos = robotCoordinatesToMapCoordinatesX(particle.x, map.xSize);
			int yPos = robotCoordinatesToMapCoordinatesY(particle.y, map.ySize);
			if (xPos < 0 || yPos < 0 || xPos >= map.xSize || yPos >= map.ySize || map.cellArray[yPos][xPos] != Cell.FREE_SPACE)
				particle.weight = 0;
		}
		return updatedState;
	}
	
	//assumes the robot's 0,0 point is the center of the image, the robot's coordinates are in meters, and each map section corresponds to a decimeter
	int robotCoordinatesToMapCoordinatesX(float coordX, int xSize)
	{
		return (int) (coordX*10 + xSize/2);
	}
	
	int robotCoordinatesToMapCoordinatesY(float coordY, int ySize)
	{
		return (int) (coordY*10 + ySize/2);
	}
	
	@Override
	public boolean shouldResample() {
		return true;
	}
	

}
