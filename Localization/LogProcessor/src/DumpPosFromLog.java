import java.io.*;

import edu.cmu.ri.rcommerce.Messages.AccelInfo;
import edu.cmu.ri.rcommerce.Messages.MagneticInfo;
import edu.cmu.ri.rcommerce.Messages.MessageWrapper;
import edu.cmu.ri.rcommerce.Messages.MessageWrapper.Type;
import edu.cmu.ri.rcommerce.*;


public class DumpPosFromLog {

	static FileWriter out = null;
	static AccelInfo currentAccel;
	static MagneticInfo currentMag;
	public static void main(String[] args) {
		File arg = new File("D:\\workspace\\rCommerce\\stuff\\logs\\in pocket heading extraction test\\south then north 5s walk stand intervals.log");
		InputStream in;
		try
		{
			in = new FileInputStream(arg);
			out = new FileWriter(new File(arg.getParentFile(),arg.getName() + ".pos.aprt"));
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
					handleMagMessage(wrap.getMagneticInfo());
					break;
				default:
					break;
				}
			}
			out.close();
	}
	catch (Exception e)
	{
		e.printStackTrace();
	}
		

}

	private static void handleMagMessage(MagneticInfo magneticInfo) throws IOException {
		currentMag = magneticInfo;
		updateAPR();
		
	}

	private static void handleAccelMessage(AccelInfo accelInfo) throws IOException {
		currentAccel = accelInfo;
		updateAPR();
	}
	
	static void updateAPR() throws IOException
	{
		if (currentAccel != null && currentMag != null)
		{
			long latestTimestamp = Math.max(currentAccel.getTimestamp(), currentMag.getTimestamp());
			
			float[] R = new float[9];
			float[] I = new float[9];
			float[] gravity = new float[]{currentAccel.getX(), currentAccel.getY(), currentAccel.getX()};
			float[] geomagnetic = new float[]{currentMag.getX(), currentMag.getY(), currentMag.getZ()};
			float[] orientation = new float[3];
			SensorManager.getRotationMatrix(R, I, gravity, geomagnetic);
			SensorManager.getOrientation(R, orientation);
			
			//convert to degrees
			out.write(Math.toDegrees(orientation[0]) + " " + Math.toDegrees(orientation[1]) + " " + Math.toDegrees(orientation[2]) + " " +  latestTimestamp + "\n");
		}
	}
}