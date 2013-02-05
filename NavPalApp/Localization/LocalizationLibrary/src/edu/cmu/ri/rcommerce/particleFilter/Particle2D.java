package edu.cmu.ri.rcommerce.particleFilter;

import android.os.Parcel;
import android.os.Parcelable;

/** Implementation of the particle class for a 2D real-valued particle. For example, it could be used to track x,y locations */
public class Particle2D extends Particle implements Parcelable
{
	public float x,y;
	
	public Particle2D(float x, float y)
	{
		this.x = x;
		this.y = y;
		weight = 1;
	}

	public Particle2D(Parcel in) {
		weight = in.readFloat();
		x = in.readFloat();
		y = in.readFloat();
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Particle2D))
			return false;
		
		Particle2D p = (Particle2D) o;
		
		return p.weight == weight && p.x == x && p.y == y;
	}
	
	@Override
	Particle copy() {
		Particle p = new Particle2D(x, y);
		p.weight = weight;
		return p;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel out, int flags) {
		out.writeFloat(weight);
		out.writeFloat(x);
		out.writeFloat(y);
		
	}
	
	public static final Parcelable.Creator<Particle2D> CREATOR
    = new Parcelable.Creator<Particle2D>() {
public Particle2D createFromParcel(Parcel in) {
    return new Particle2D(in);
}

public Particle2D[] newArray(int size) {
    return new Particle2D[size];
}
};

}
