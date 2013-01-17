import java.io.*;

import edu.cmu.ri.rcommerce.*;
import edu.cmu.ri.rcommerce.Messages.AccelInfo;
import edu.cmu.ri.rcommerce.Messages.GravityInfo;
import edu.cmu.ri.rcommerce.Messages.GyroInfo;
import edu.cmu.ri.rcommerce.Messages.LinearAccelInfo;
import edu.cmu.ri.rcommerce.Messages.MagneticInfo;
import edu.cmu.ri.rcommerce.Messages.MessageWrapper;
import edu.cmu.ri.rcommerce.Messages.MessageWrapper.Type;
import edu.cmu.ri.rcommerce.Messages.RotationInfo;


public class DumpSensorInfoFromLog {

	static FileWriter accelOut = null;
	static FileWriter magneticOut = null;
	static FileWriter gyroOut = null;
	static FileWriter gravOut = null;
	static FileWriter linearAccelOut = null;
	static FileWriter rotationOut = null;
	static FileWriter orientationOut = null;
	
	static GravityInfo latestGravInfo = null;
	static MagneticInfo latestMagneticInfo = null;
	
	
	public static void main(String[] args) {
		File arg = new File("C:\\Users\\Evan\\Documents\\College Stuff\\RI Summer Stuff\\Tech Report\\integration\\compass.log");
		InputStream in;
		try
		{
			in = new FileInputStream(arg);
			accelOut = new FileWriter(new File(arg.getParentFile(),arg.getName() + ".accel.xyzt"));
			magneticOut = new FileWriter(new File(arg.getParentFile(),arg.getName() + ".magnetic.xyzt"));
			gyroOut = new FileWriter(new File(arg.getParentFile(),arg.getName() + ".gyro.xyzt"));
			gravOut = new FileWriter(new File(arg.getParentFile(),arg.getName() + ".grav.xyzt"));
			linearAccelOut = new FileWriter(new File(arg.getParentFile(),arg.getName() + ".linearAccel.xyzt"));
			rotationOut = new FileWriter(new File(arg.getParentFile(),arg.getName() + ".rotation.abct"));
			orientationOut = new FileWriter(new File(arg.getParentFile(),arg.getName() + ".orientation"));
			
			while (true)
			{
				MessageWrapper wrap;
				try
				{
					wrap = MessageWrapper.parseDelimitedFrom(in);
				}
				catch ( com.google.protobuf.InvalidProtocolBufferException e)
				{
					System.out.println("decode error!");
					continue;
				}
				if (wrap == null)
					break;
				Type messageType = wrap.getType();
				
				switch (messageType) {
				case AccelInfo:
					handleAccelMessage(wrap.getAccelInfo());
					break;
				case MagneticInfo:
					handleMagneticMessage(wrap.getMagneticInfo());
					if (latestMagneticInfo == null)
						latestMagneticInfo = wrap.getMagneticInfo();
					break;
				case GyroInfo:
					handleGyroMessage(wrap.getGyroInfo());
					break;
				case GravityInfo:
					handleGravityMessage(wrap.getGravityInfo());
					if (latestGravInfo == null )
						latestGravInfo = wrap.getGravityInfo();
					break;
				case LinearAccelInfo:
					handlLinearAccelMessage(wrap.getLinearAccelInfo());
					break;
				case RotationInfo:
					handleRotationMessage(wrap.getRotationInfo());
					break;
				default:
					break;
				}
				if (latestGravInfo != null && latestMagneticInfo != null)
				{
					float[] orientation =
					Common.getCurrentOrientation( 
							new float[]{latestGravInfo.getX(),latestGravInfo.getY(),latestGravInfo.getZ()},
							new float[]{latestMagneticInfo.getX(),latestMagneticInfo.getY(),latestMagneticInfo.getZ()});
					orientationOut.write(orientation[0] + " " + orientation[1] + " " + orientation[2] + " " + latestGravInfo.getTimestamp() + "\n");
					latestGravInfo = null;
					latestMagneticInfo = null;
				}
			}
	}
	catch (Exception e)
	{
		e.printStackTrace();
	}
		

}

	private static void handleAccelMessage(AccelInfo info) throws IOException {
		accelOut.write(info.getX() + " " + info.getY() + " " + info.getZ() + " " + info.getTimestamp() + "\n");
	}
	private static void handleMagneticMessage(MagneticInfo info) throws IOException {
		magneticOut.write(info.getX() + " " + info.getY() + " " + info.getZ() + " " + info.getTimestamp() + "\n");
	}
	private static void handleGyroMessage(GyroInfo info) throws IOException {
		gyroOut.write(info.getX() + " " + info.getY() + " " + info.getZ() + " " + info.getTimestamp() + "\n");
	}
	private static void handleGravityMessage(GravityInfo info) throws IOException {
		gravOut.write(info.getX() + " " + info.getY() + " " + info.getZ() + " " + info.getTimestamp() + "\n");
	}
	private static void handlLinearAccelMessage(LinearAccelInfo info) throws IOException {
		linearAccelOut.write(info.getX() + " " + info.getY() + " " + info.getZ() + " " + info.getTimestamp() + "\n");
	}
	private static void handleRotationMessage(RotationInfo info) throws IOException {
		rotationOut.write(info.getA() + " " + info.getB() + " " + info.getC() + " " + info.getTimestamp() + "\n");
	}
}