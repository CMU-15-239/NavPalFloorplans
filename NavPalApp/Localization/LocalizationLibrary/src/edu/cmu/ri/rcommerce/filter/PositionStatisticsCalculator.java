package edu.cmu.ri.rcommerce.filter;

import edu.cmu.ri.rcommerce.Messages.PositionInfo;



/**Can be fed PositionInfo messages, and generates useful statistics that are kept updated.
 * 
 *  Currently calculates total distance, final position, and distance from initial position to final position.
 */
public class PositionStatisticsCalculator 
{
	PositionInfo first;
	PositionInfo last;
	float totalDistance;
	public void addPositionMessage(PositionInfo positionInfo)
	{
		totalDistance += distanceDifference(last, positionInfo);
		if (first == null)
			first = positionInfo;
		last = positionInfo;
	}
	
	public float getTotalDistance()
	{
		return totalDistance;
	}
	
	public PositionInfo getLastMessage()
	{
		return last;
	}
	
	public float getNetDistance()
	{
		return distanceDifference(first, last);
	}
	
	public float getElapsedTime()
	{
		return last.getTimestamp() - first.getTimestamp();
	}
	
	
	public static float distanceDifference(PositionInfo position1, PositionInfo position2)
	{
		if (position1 == null || position2 == null)
			return 0;
		return (float) Math.sqrt(Math.pow(position1.getX() - position2.getX(),2) + Math.pow(position1.getY() - position2.getY(),2));
	}
	
	public static float magnitude(PositionInfo position1)
	{
		if (position1 == null)
			return 0;
		return (float) Math.sqrt(Math.pow(position1.getX(),2) + Math.pow(position1.getY(),2));
	}
}
