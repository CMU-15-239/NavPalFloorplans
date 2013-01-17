import java.io.*;
import java.text.DecimalFormat;

import org.jfree.chart.*;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.*;
import org.jfree.chart.renderer.xy.VectorRenderer;
import org.jfree.data.xy.*;

import edu.cmu.ri.rcommerce.*;
import edu.cmu.ri.rcommerce.Messages.AccelInfo;
import edu.cmu.ri.rcommerce.Messages.GPSInfo;
import edu.cmu.ri.rcommerce.Messages.GyroInfo;
import edu.cmu.ri.rcommerce.Messages.MagneticInfo;
import edu.cmu.ri.rcommerce.Messages.MessageWrapper;
import edu.cmu.ri.rcommerce.Messages.MessageWrapper.Type;
import edu.cmu.ri.rcommerce.Messages.PositionInfo;
import edu.cmu.ri.rcommerce.filter.*;
import edu.cmu.ri.rcommerce.sensor.SensorReading;

/* generate the graph by reproccessing the raw sensor data instead of reading the processed values */
public class PathComparisonGraphFromRaw {
	static VectorSeries MARGPositions;
	
	static PositionStatisticsCalculator MARGStatistics;
	static PedestrianLocalization_Gyro loc_gyro;
	
	static int frameNum = 0;
	static File outDir;
	
	//static final int framerate = 20; //frames per second
	static final int framerate = 1; //frames per second
	static long millisecondsPerFrame = 1000 / framerate;
	static long initialTime = 0;
	static long currentTime = 0;
	static long nextFrameTime = 0;
	
	static boolean manualRange = true;
	static int manualXMin = -95;
	static int manualXMax = 23;
	static int manualYMin = -55;
	static int manualYMax = 65;
	
	static boolean makeVideo = false;
	
	//TODO: read all the messages into memory and sort them by timestamp before processing
	
	public static void main(String[] args) throws IOException {
		InputStream in = new FileInputStream(args[0]);
		outDir = new File("movie/");
		outDir.mkdir();
		MARGPositions = new VectorSeries("MARG");
		MARGStatistics = new PositionStatisticsCalculator();
		
		loc_gyro = new PedestrianLocalization_Gyro(new LocationListener() {
			
			@Override
			public void broadcastLocationUpdate(double x, double y, double theta, long time, double velocity) {				
				double magnitude = 1;
				//convert from heading based on north to mathematical angle
				double angle = -theta + Math.toRadians(90);
			
				double dX = magnitude * Math.cos(angle);
				double dY = magnitude * Math.sin(angle);
				MARGPositions.add(x, y, dX, dY);
				PositionInfo m = PositionInfo.newBuilder()
					.setX((float)x)
					.setY((float)y)
					.setTimestamp(time)
					.build();
				MARGStatistics.addPositionMessage(m);
			}
			
			@Override
			public void broadcastLocationStatusChange(int status) {
				
				
			}

			@Override
			public void broadcastRelativeLocationUpdate(double dR, double dTheta, double timeDiff) {
				// TODO Auto-generated method stub
				
			}
		}, false);
		
		
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
				break;
			case GyroInfo:
				handleGyroMessage(wrap.getGyroInfo());
				break;
			default:
				break;
			}
		}
		
		System.out.println("Total MARG distance: " + MARGStatistics.getTotalDistance());
		
		
		PositionInfo lastMARG = MARGStatistics.getLastMessage();
		System.out.println("Final MARG: " + lastMARG.getX() + "," + lastMARG.getY());
		
		System.out.println("Net MARG distance: " + PositionStatisticsCalculator.magnitude(lastMARG));
		
		long elapsedMilliseconds = currentTime - initialTime;
		System.out.println("Total Time: " + new DecimalFormat("#####").format(elapsedMilliseconds/1000.0) + " seconds");

		VectorSeriesCollection dataset = new VectorSeriesCollection();
		dataset.addSeries(MARGPositions);
		
		XYPlot plot = new XYPlot(dataset,new NumberAxis("X"),new NumberAxis("Y"),new VectorRenderer());
		plot.setOrientation(PlotOrientation.VERTICAL);
		JFreeChart chart = new JFreeChart(plot);
		
		ChartUtilities.saveChartAsJPEG(new File(args[0].substring(args[0].lastIndexOf("\\") + 1) + "_RAW.jpg"), chart, 1024,1024);
		
	}
	
	static void handleAccelMessage(AccelInfo accelInfo)
	{
		float[] values = {accelInfo.getX(),accelInfo.getY(),accelInfo.getZ()};
		SensorReading reading = new SensorReading(accelInfo.getTimestamp(), values, SensorReading.ACCELEROMETER_SENSOR);
		loc_gyro.addAccelerometerReadings(new SensorReading[]{reading});
	}
	static void handleMagneticMessage(MagneticInfo magneticInfo)
	{
		float[] values = {magneticInfo.getX(),magneticInfo.getY(),magneticInfo.getZ()};
		SensorReading reading = new SensorReading(magneticInfo.getTimestamp(), values, SensorReading.MAGNETOMETER_SENSOR);
		loc_gyro.addMagnetometerReadings(new SensorReading[]{reading});
	}
	static void handleGyroMessage(GyroInfo gyroInfo)
	{
		float[] values = {gyroInfo.getX(),gyroInfo.getY(),gyroInfo.getZ()};
		SensorReading reading = new SensorReading(gyroInfo.getTimestamp(), values, SensorReading.GYRO_SENSOR);
		loc_gyro.addGyroReadings(new SensorReading[]{reading});
	}

	private static void handleGPSMessage(GPSInfo gpsInfo) {
		System.out.println("Lat: " + gpsInfo.getLattitude() + " Long: " + gpsInfo.getLongitude());
		
	}
}
