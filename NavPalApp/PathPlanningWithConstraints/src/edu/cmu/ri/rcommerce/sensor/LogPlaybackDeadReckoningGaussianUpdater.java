package edu.cmu.ri.rcommerce.sensor;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import edu.cmu.ri.rcommerce.FastRandom;
import edu.cmu.ri.rcommerce.LocationListener;
import edu.cmu.ri.rcommerce.Messages.AccelInfo;
import edu.cmu.ri.rcommerce.Messages.GPSInfo;
import edu.cmu.ri.rcommerce.Messages.GyroInfo;
import edu.cmu.ri.rcommerce.Messages.MagneticInfo;
import edu.cmu.ri.rcommerce.Messages.MessageWrapper;
import edu.cmu.ri.rcommerce.Messages.MessageWrapper.Type;
import edu.cmu.ri.rcommerce.filter.PedestrianLocalization_Gyro;
import edu.cmu.ri.rcommerce.filter.PedestrianLocalization_StandardDeviation;
import edu.cmu.ri.rcommerce.particleFilter.GaussianSample;
import edu.cmu.ri.rcommerce.particleFilter.Particle2D;
import edu.cmu.ri.rcommerce.particleFilter.Updater;

public class LogPlaybackDeadReckoningGaussianUpdater implements Updater<Particle2D> {
	String logFile;
	BufferedInputStream logFileInput;
	static PedestrianLocalization_Gyro loc_gyro;
	PedestrianLocalization_StandardDeviation loc_stddev;
	List<Particle2D> state;
	LogPlaybackTimeSource timeSource;
	boolean isTagged;
	public long latestTime;

	public LogPlaybackDeadReckoningGaussianUpdater(String log, LogPlaybackTimeSource timeSource, final FastRandom random, float orientationOffset, final float velocityMeanDeviation, final float headingMeanDeviation) throws IOException {
		logFile = log;
		logFileInput = new BufferedInputStream(new FileInputStream(logFile));
		this.timeSource = timeSource;
		isTagged = false;
		loc_gyro = new PedestrianLocalization_Gyro(new LocationListener() {

			@Override
			public void broadcastLocationUpdate(double x, double y, double theta, long time, double velocity) {
				double timeDiff = 0.03;
				//System.out.println(loc_gyro.WALKING_VELOCITY + " " + velocityMeanDeviation + " " + theta + " " + headingMeanDeviation + " " + timeDiff);
				for (Particle2D p : state) {
					double vHat = loc_gyro.WALKING_VELOCITY + GaussianSample.sample(random, velocityMeanDeviation);
					double thetaHat = theta + GaussianSample.sample(random, headingMeanDeviation);
					thetaHat %= 2 * Math.PI;

					double forwardDistance = vHat * timeDiff;

					p.x = (float) (p.x - forwardDistance * Math.cos(thetaHat));
					p.y = (float) (p.y + forwardDistance * Math.sin(thetaHat));
				}
			}

			@Override
			public void broadcastLocationStatusChange(int status) {
			}

			@Override
			public void broadcastRelativeLocationUpdate(double r, double theta, double timeDiff) {

			}
		}, true);		
		
		/*loc_stddev = new PedestrianLocalization_StandardDeviation(new LocationListener() {

			@Override
			public void broadcastLocationUpdate(double x, double y, double theta, long time) {
				double timeDiff = 0.03;
				//System.out.println(loc_gyro.WALKING_VELOCITY + " " + velocityMeanDeviation + " " + theta + " " + headingMeanDeviation + " " + timeDiff);
				for (Particle2D p : state) {
					double vHat = loc_stddev.WALKING_VELOCITY + GaussianSample.sample(random, velocityMeanDeviation);
					double thetaHat = theta + GaussianSample.sample(random, headingMeanDeviation);
					thetaHat %= 2 * Math.PI;

					double forwardDistance = vHat * timeDiff;

					p.x = (float) (p.x - forwardDistance * Math.cos(thetaHat));
					p.y = (float) (p.y + forwardDistance * Math.sin(thetaHat));
				}
			}

			@Override
			public void broadcastLocationStatusChange(int status) {
			}

			@Override
			public void broadcastRelativeLocationUpdate(double r, double theta, double timeDiff) {

			}
		}, true);
		loc_stddev.setOrientationOffset(orientationOffset);*/
		loc_gyro.setOrientationOffset(orientationOffset);

	}

	@Override
	public List<Particle2D> update(List<Particle2D> state) {
		this.state = state;
		try {
			updateFromLog(logFileInput);
		} catch (IOException e) {
			System.out.println("no new updates");
		}
		return state;
	}

	void updateFromLog(BufferedInputStream in) throws IOException {
		top: while (true) {
			MessageWrapper wrap;
			try {
				in.mark(10000);
				wrap = MessageWrapper.parseDelimitedFrom(in);
			} catch (com.google.protobuf.InvalidProtocolBufferException e) {
				System.out.println("decode error!");
				return;
			}
			if (wrap == null)
				return;
			Type messageType = wrap.getType();

			switch (messageType) {
			case AccelInfo:
				if (wrap.getAccelInfo().getTimestamp() > timeSource.getCurrentTime()) {
					in.reset();
					break top;
				}
				handleAccelMessage(wrap.getAccelInfo());
				latestTime = Math.max(latestTime, wrap.getAccelInfo().getTimestamp());
				break;
			case MagneticInfo:
				if (wrap.getMagneticInfo().getTimestamp() > timeSource.getCurrentTime()) {
					in.reset();
					break top;
				}
				handleMagneticMessage(wrap.getMagneticInfo());
				latestTime = Math.max(latestTime, wrap.getMagneticInfo().getTimestamp());
				break;
			case GyroInfo:
				if (wrap.getGyroInfo().getTimestamp() > timeSource.getCurrentTime()) {
					in.reset();
					break top;
				}
				handleGyroMessage(wrap.getGyroInfo());
				latestTime = Math.max(latestTime, wrap.getGyroInfo().getTimestamp());
				break;
			case TagInfo:
				isTagged = wrap.getTagInfo().getTag();
				break;
			default:
				continue;
			}
		}
	}

	static void handleAccelMessage(AccelInfo accelInfo) {
		float[] values = { accelInfo.getX(), accelInfo.getY(), accelInfo.getZ() };
		SensorReading reading = new SensorReading(accelInfo.getTimestamp(), values, SensorReading.ACCELEROMETER_SENSOR);
		loc_gyro.addAccelerometerReadings(new SensorReading[] { reading });
	}

	static void handleMagneticMessage(MagneticInfo magneticInfo) {
		float[] values = { magneticInfo.getX(), magneticInfo.getY(), magneticInfo.getZ() };
		SensorReading reading = new SensorReading(magneticInfo.getTimestamp(), values, SensorReading.MAGNETOMETER_SENSOR);
		loc_gyro.addMagnetometerReadings(new SensorReading[] { reading });
	}

	static void handleGyroMessage(GyroInfo gyroInfo) {
		float[] values = { gyroInfo.getX(), gyroInfo.getY(), gyroInfo.getZ() };
		SensorReading reading = new SensorReading(gyroInfo.getTimestamp(), values, SensorReading.GYRO_SENSOR);
		loc_gyro.addGyroReadings(new SensorReading[] { reading });
	}

	private static void handleGPSMessage(GPSInfo gpsInfo) {
		System.out.println("Lat: " + gpsInfo.getLattitude() + " Long: " + gpsInfo.getLongitude());

	}
	
	public boolean isTagged() {
		if(isTagged) {
			//handles resetting since the Offline PF will act on the isTagged as soon as it happens
			isTagged = false;
			return true;
		}
		else
			return false;
	}
}
