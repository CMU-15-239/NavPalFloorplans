import java.awt.Color;
import java.io.*;
import java.text.DecimalFormat;

import org.jfree.chart.*;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.*;
import org.jfree.chart.renderer.xy.VectorRenderer;
import org.jfree.data.xy.*;

import edu.cmu.ri.rcommerce.Messages.GPSInfo;
import edu.cmu.ri.rcommerce.Messages.MessageWrapper;
import edu.cmu.ri.rcommerce.Messages.MessageWrapper.Type;
import edu.cmu.ri.rcommerce.Messages.PositionInfo;
import edu.cmu.ri.rcommerce.filter.PositionStatisticsCalculator;


public class PathComparisonGraph {
	static VectorSeries stdDevPositions, stepPositions, gpsPositions, MARGPositions;
	
	static PositionStatisticsCalculator stdDevStatistics, gpsStatistics, stepStatistics,MARGStatistics;
	
	static int stepCount = 0;
	
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
		
		stdDevPositions = new VectorSeries("stdDev");
		stepPositions = new VectorSeries("step");
		gpsPositions = new VectorSeries("GPS");
		MARGPositions = new VectorSeries("MARG");
		
		stdDevStatistics = new PositionStatisticsCalculator();
		gpsStatistics = new PositionStatisticsCalculator();
		stepStatistics = new PositionStatisticsCalculator();
		MARGStatistics = new PositionStatisticsCalculator();
		
		
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
			case GPSInfo:
				handleGPSMessage(wrap.getGpsInfo());
				break;
			case PositionInfo:
				handlePositionMessage(wrap.getPositionInfo());
				break;
			default:
				break;
			}
		}
		
		System.out.println("Step Count: " + stepCount);
		
		System.out.println("Total stddev distance: " + stdDevStatistics.getTotalDistance());
		System.out.println("Total step distance: " + stepStatistics.getTotalDistance());
		System.out.println("Total GPS distance: " + gpsStatistics.getTotalDistance());
		System.out.println("Total MARG distance: " + MARGStatistics.getTotalDistance());
		
		PositionInfo lastStdDev = stdDevStatistics.getLastMessage();
		PositionInfo lastStep = stepStatistics.getLastMessage();
		PositionInfo lastGPS = gpsStatistics.getLastMessage();
		PositionInfo lastMARG = MARGStatistics.getLastMessage();
		System.out.println("Final stddev position: " + lastStdDev.getX() + "," + lastStdDev.getY());
		if (lastStep != null)
			System.out.println("Final step position: " + lastStep.getX() + "," + lastStep.getY());
		if (lastGPS != null)
			System.out.println("Final GPS: " + lastGPS.getX() + "," + lastGPS.getY());
		if (lastMARG != null)
			System.out.println("Final MARG: " + lastMARG.getX() + "," + lastMARG.getY());
		
		System.out.println("Net sttdev distance: " + PositionStatisticsCalculator.magnitude(lastStdDev));
		System.out.println("Net step distance: " + PositionStatisticsCalculator.magnitude(lastStep));
		System.out.println("Net GPS distance: " + PositionStatisticsCalculator.magnitude(lastGPS));
		System.out.println("Net MARG distance: " + PositionStatisticsCalculator.magnitude(lastMARG));
		
		long elapsedMilliseconds = currentTime - initialTime;
		System.out.println("Total Time: " + new DecimalFormat("#####").format(elapsedMilliseconds/1000.0) + " seconds");

		VectorSeriesCollection dataset = new VectorSeriesCollection();
		dataset.addSeries(stdDevPositions);
		dataset.addSeries(stepPositions);
		dataset.addSeries(gpsPositions);
		dataset.addSeries(MARGPositions);
		
		XYPlot plot = new XYPlot(dataset,new NumberAxis("X"),new NumberAxis("Y"),new VectorRenderer());
		plot.setOrientation(PlotOrientation.VERTICAL);
		JFreeChart chart = new JFreeChart(plot);
		
		ChartUtilities.saveChartAsJPEG(new File(args[0].substring(args[0].lastIndexOf("\\") + 1) + "_PATH.jpg"), chart, 1024,1024);
		
	}

	private static void handleGPSMessage(GPSInfo gpsInfo) {
		System.out.println("Lat: " + gpsInfo.getLattitude() + " Long: " + gpsInfo.getLongitude());
		
	}

	private static void handlePositionMessage(PositionInfo positionInfo) {		
		double x = positionInfo.getX();
		double y = positionInfo.getY();
		
		double magnitude = 1;
		//convert from heading based on north to mathematical angle
		double angle = -positionInfo.getYaw() + Math.toRadians(90);
	
		double dX = magnitude * Math.cos(angle);
		double dY = magnitude * Math.sin(angle);
		
		long messageTime = positionInfo.getTimestamp();
		
		if (currentTime == 0)
		{
			initialTime = messageTime;
			currentTime = messageTime;
			nextFrameTime = currentTime + millisecondsPerFrame;
		}
		else if (messageTime >= currentTime)
			currentTime = messageTime;
		else
			System.out.println("out of order message! " + (currentTime - messageTime) + " milliseconds off");
		
		System.out.print(".");
		
		if (makeVideo)
			checkSaveFrame();
		
		if (positionInfo.getNotes().equals("StdDev"))
		{
			stdDevStatistics.addPositionMessage(positionInfo);
			stdDevPositions.add(x,y,dX,dY);
		}
		else if (positionInfo.getNotes().equals("Step"))
		{
			stepCount++;
			stepStatistics.addPositionMessage(positionInfo);
			stepPositions.add(x,y,dX,dY);
		}
		else if (positionInfo.getNotes().equals("GPS"))
		{
			gpsStatistics.addPositionMessage(positionInfo);
			gpsPositions.add(x,y,dX,dY);
		}
		else if (positionInfo.getNotes().equals("MARG"))
		{
			MARGStatistics.addPositionMessage(positionInfo);
			MARGPositions.add(x,y,dX,dY);
		}
		else
			throw new RuntimeException("unable to handle position message: " + positionInfo.getNotes());
	}
	
	static void checkSaveFrame()
	{
		if (currentTime < nextFrameTime)
			return;
		System.out.print("\n");
		int duplicates = 0;
		while (currentTime >= nextFrameTime)
		{
			if (duplicates > 0)
				System.out.print("d");
			duplicates++;
			
			nextFrameTime = nextFrameTime + millisecondsPerFrame;
			//The most recent message we got belongs in the next frame, so output the previous frame
			VectorSeriesCollection dataset = new VectorSeriesCollection();
			//dataset.addSeries(stdDevPositions);
			//dataset.addSeries(stepPositions);
			//dataset.addSeries(gpsPositions);
			dataset.addSeries(MARGPositions);
			
			NumberAxis XAxis = new NumberAxis("X");
			NumberAxis YAxis = new NumberAxis("Y");
			if (manualRange)
			{
				XAxis.setAutoRange(false);
				YAxis.setAutoRange(false);
				XAxis.setRange(manualXMin, manualXMax);
				YAxis.setRange(manualYMin, manualYMax);
			}
			VectorRenderer renderer = new VectorRenderer();
			//renderer.setSeriesPaint(0, new Color(255,0,0));
			//renderer.setSeriesPaint(1, new Color(0,0,255));
			renderer.setSeriesPaint(2, new Color(255,255,0));
			XYPlot plot = new XYPlot(dataset,XAxis,YAxis,renderer);
			plot.setOrientation(PlotOrientation.VERTICAL);
			plot.setBackgroundPaint(null);
			JFreeChart chart = new JFreeChart(plot);
			//Image background;
			//try {
			//	background = ImageIO.read(new File("D:\\Desktop\\NavPal Video\\aerial3_16-9.png"));
			//} catch (IOException e1) {
			//	throw new RuntimeException(e1);
			//}
			//chart.setBackgroundImage(background);
			
			
			try{
			ChartUtilities.saveChartAsJPEG(new File(outDir,"chart" + frameNum++ + ".jpg"), chart, 1600,900);
			}
			catch (IOException e)
			{
				throw new RuntimeException(e);
			}
		}
	}
	
	

}
