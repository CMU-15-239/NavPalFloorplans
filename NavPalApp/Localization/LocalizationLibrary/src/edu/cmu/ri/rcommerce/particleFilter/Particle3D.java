package edu.cmu.ri.rcommerce.particleFilter;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Implementation of the particle class for a 2D real-valued particle. For
 * example, it could be used to track x,y locations
 */
public class Particle3D extends Particle implements Parcelable {
	public float x, y;
	public int floor;

	public Particle3D(float x, float y, int floor) {
		this.x = x;
		this.y = y;
		this.floor = floor;
		weight = 1;
	}

	public Particle3D(Parcel in) {
		weight = in.readFloat();
		x = in.readFloat();
		y = in.readFloat();
		floor = in.readInt();
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Particle3D))
			return false;

		Particle3D p = (Particle3D) o;

		return p.weight == weight && p.x == x && p.y == y && p.floor == floor;
	}

	@Override
	Particle copy() {
		Particle p = new Particle3D(x, y, floor);
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
		out.writeInt(floor);

	}

	public static final Parcelable.Creator<Particle3D> CREATOR = new Parcelable.Creator<Particle3D>() {
		public Particle3D createFromParcel(Parcel in) {
			return new Particle3D(in);
		}

		public Particle3D[] newArray(int size) {
			return new Particle3D[size];
		}
	};

}
