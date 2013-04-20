package Common;

import java.lang.Math;

public class Vector2D
{
    public static double TO_RADIANS = (1 / 180.0f) * (double) Math.PI;
    public static double TO_DEGREES = (1 / (double) Math.PI) * 180;
    private double       _x, _y;

    public Vector2D()
    {
	this(0.0f, 0.0f);
    }

    public Vector2D(Vector2D other)
    {
	this(other._x, other._y);
    }

    public Vector2D(float x, float y)
    {
	this._x = x;
	this._y = y;
    }

    public Vector2D(double x, double y)
    {
	this._x = x;
	this._y = y;
    }
    
    public Vector2D cpy()
    {
	return new Vector2D(_x, _y);
    }

    public void setX(int x)
    {
	this._x = x;
    }

    public void setY(int y)
    {
	this._y = y;
    }

    public float getX()
    {
	return (float)this._x;
    }

    public float getY()
    {
	return (float)this._y;
    }

    public Vector2D set(float x, float y)
    {
	this._x = x;
	this._y = y;
	return this;
    }

    public Vector2D set(Vector2D other)
    {
	this._x = other._x;
	this._y = other._y;
	return this;
    }

    public Vector2D add(float x, float y)
    {
	this._x += x;
	this._y += y;
	return this;
    }

    public Vector2D add(Vector2D other)
    {
	this._x += other._x;
	this._y += other._y;
	return this;
    }

    public Vector2D sub(float x, float y)
    {
	this._x -= x;
	this._y -= y;
	return this;
    }

    public Vector2D sub(Vector2D other)
    {
	this._x -= other._x;
	this._y -= other._y;
	return this;
    }

    public Vector2D mul(float scalar)
    {
	this._x *= scalar;
	this._y *= scalar;
	return this;
    }

    public float len()
    {
	return (float) Math.sqrt(_x * _x + _y * _y);
    }

    public Vector2D nor()
    {
	float len = len();
	if (len != 0)
	{
	    this._x /= len;
	    this._y /= len;
	}
	return this;
    }

    public float angle()
    {
	double angle = (double) Math.atan2(_y, _x) * TO_DEGREES;
	if (angle < 0)
	    angle += 360;
	return (float)angle;
    }

    public Vector2D rotate(float angle)
    {
	double rad = angle * TO_RADIANS;
	double cos = (float) Math.cos(rad);
	double sin = (float) Math.sin(rad);

	double newX = this._x * cos - this._y * sin;
	double newY = this._x * sin + this._y * cos;

	this._x = newX;
	this._y = newY;

	return this;
    }

    public float dist(Vector2D other)
    {
	// float distX = this._x - other._x;
	// float distY = this._y - other._y;
	// return (float) Math.sqrt(distX * distX + distY * distY);
	return dist(other.getX(), other.getY());
    }

    public float dist(double x, double y)
    {
	double distX = this._x - x;
	double distY = this._y - y;
	return (float) Math.sqrt(distX * distX + distY * distY);
    }

    public float distSquared(Vector2D other)
    {
	// float distX = this._x - other._x;
	// float distY = this._y - other._y;
	// return distX * distX + distY * distY;
	return distSquared(other.getX(), other.getY());
    }

    public float distSquared(double x, double y)
    {
	double distX = this._x - x;
	double distY = this._y - y;
	return (float) ((float)distX * distX + distY * distY);
    }

    public boolean equals(Vector2D other)
    {
	return ((other.getX() == this._x) && (other.getY() == this._y));
    }

    public String toString()
    {
	return "(" + _x + "," + _y + ")";
    }
}