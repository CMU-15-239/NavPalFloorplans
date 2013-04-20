package Graph;

import Common.Vector2D;

public class NavGraphNode extends GraphNode
{
    private Vector2D _vPosition;

    public NavGraphNode()
    {
	this(-1, new Vector2D());
    }

    public NavGraphNode(int index, Vector2D position)
    {
	setIndex(index);
	setPosition(position);
    }

    public Vector2D getPosition()
    {
	return _vPosition;
    }

    public void setPosition(Vector2D position)
    {
	_vPosition = position;
    }
    
    public String toString()
    {
	return getIndex() + ":\t(" + getPosition().getX() + ", " + getPosition().getY() + ")";
	//return getPosition().getX() + ", " + getPosition().getY() + ";";
    }
}
