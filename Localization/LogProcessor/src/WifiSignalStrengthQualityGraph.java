import java.io.Console;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import edu.cmu.ri.rcommerce.Messages.GSMScan;
import edu.cmu.ri.rcommerce.Messages.MessageWrapper;
import edu.cmu.ri.rcommerce.Messages.MessageWrapper.Type;
import edu.cmu.ri.rcommerce.Messages.WifiInfo;
import edu.cmu.ri.rcommerce.Messages.WifiScan;


public class WifiSignalStrengthQualityGraph 
{
	static TreeMap<Long, XYSeries> wifis;
	static long initTime = 0;
	public static void main(String[] args) throws IOException {
		InputStream in = new FileInputStream(args[0]);
		
		int numWifiMessages = 0;
		long numDecodeErrors = 0;

		wifis = new TreeMap<Long, XYSeries>();
		while (true)
		{
			MessageWrapper wrap;
			try
			{
				wrap = MessageWrapper.parseDelimitedFrom(in);
			}
			catch ( com.google.protobuf.InvalidProtocolBufferException e)
			{
				numDecodeErrors++;
				System.out.println("decode error!");
				continue;
			}
			if (wrap == null)
				break;
			Type messageType = wrap.getType();
			
			switch (messageType) {
			case GSMScan:
				handleGSMMessage(wrap.getGsmScan());
				break;
			case WifiScan:
				numWifiMessages++;
				WifiScan scan = wrap.getWifiScan();				
				if(initTime == 0)
					initTime = scan.getTimestamp();				
				handleWifiMessage(scan);
				break;
			default:
				break;
			}
		}
		//XYSeriesCollection dataset = new XYSeriesCollection();
		Set<Long> APs = wifis.keySet();
		System.out.println("AP: " + APs.size());
		Iterator<Long> itr = APs.iterator();

		/*
		 * NOTE: This block of code below will create individual plots for each series so they are more readable
		 */
		int index = 0;
		while(itr.hasNext())
		{
			// Get the value of the next iterator (the Access point address)
			long accessPointBSSID = itr.next(); 

			// Retrieve the signal strengths for the particular access point
			XYSeries signalStrengthLevels = wifis.get(accessPointBSSID);	

			// Create a new data series object and assign them the current access point levels
			XYSeriesCollection dataset = new XYSeriesCollection();
			dataset.addSeries(signalStrengthLevels);

			// Create a unique name for the next chart
			String chartName = "chart" + index++ + ".jpg";
			
			JFreeChart chart = 
				ChartFactory.createScatterPlot("Wifi Signal Strength for AP " + accessPointBSSID + " vs Time", "Time (seconds)", "Signal Strength (dB)",
				dataset, PlotOrientation.VERTICAL, false, false, false);

			ChartUtilities.saveChartAsJPEG(new File(chartName), chart, 1024,1024);
		}

		System.out.println("Number of Decode Errors: " + numDecodeErrors);
		
		// Code below will put each series on the same plot.
//		while(itr.hasNext()) {
//			dataset.addSeries(wifis.get(itr.next()));
//		}
		
//		JFreeChart chart = 
//			ChartFactory.createScatterPlot("Wifi Signal Strength per AP over Time", "Time (seconds)", "Signal Strength (dB)",
//			dataset, PlotOrientation.VERTICAL, false, false, false);
//
//		ChartUtilities.saveChartAsJPEG(new File("chart.jpg"), chart, 1024,1024);
		
	}

	private static void handleGSMMessage(GSMScan gsmScan) {
		//gsmBeacons.add(gsmScan.getTimestamp(), gsmScan.getScanCount());
		
	}

	private static void handleWifiMessage(WifiScan wifiScan) 
	{
		List<WifiInfo> list = wifiScan.getScanList();

		for(WifiInfo i: list) {
			XYSeries xy = wifis.get(i.getBSSID());
			if(xy == null)
				xy = new XYSeries(i.getBSSID());

			// GARY: This was for debugging to understand the individual parameters
//			long tDifference = (wifiScan.getTimestamp() - initTime);
//			double key = tDifference / 1e3f;
//			int level = i.getLevel();
//			xy.add(key, level);

			xy.add((wifiScan.getTimestamp() - initTime)/ 1e3f, i.getLevel());
			wifis.put(i.getBSSID(), xy);
		}
	}
}

class APDataSample
{
	long apID;
}