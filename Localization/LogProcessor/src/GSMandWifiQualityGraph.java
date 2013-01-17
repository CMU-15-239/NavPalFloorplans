import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import edu.cmu.ri.rcommerce.Messages.GSMScan;
import edu.cmu.ri.rcommerce.Messages.MessageWrapper;
import edu.cmu.ri.rcommerce.Messages.MessageWrapper.Type;
import edu.cmu.ri.rcommerce.Messages.WifiScan;


public class GSMandWifiQualityGraph 
{
static XYSeries gsmBeacons, wifiBeacons; 
static long initTimeStamp = 0;
	public static void main(String[] args) throws IOException {
		InputStream in = new FileInputStream(args[0]);
		
		gsmBeacons = new XYSeries("GSM");
		wifiBeacons = new XYSeries("Wifi");
		
		int numWifiMessages = 0;
		
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
			case GSMScan:
				handleGSMMessage(wrap.getGsmScan());
				break;
			case WifiScan:
				numWifiMessages++;
				WifiScan scan = wrap.getWifiScan();
				if(initTimeStamp == 0) {
					initTimeStamp = scan.getTimestamp();
				}
				handleWifiMessage(scan);
				break;
			default:
				break;
			}
		}

		XYSeriesCollection dataset = new XYSeriesCollection();
		dataset.addSeries(gsmBeacons);
		dataset.addSeries(wifiBeacons);
		
		System.out.println("num wifi messages: " + numWifiMessages);
		
		JFreeChart chart = 
			ChartFactory.createXYLineChart("Number of Wifi Beacons", "Time (seconds)", "Beacons",
			dataset, PlotOrientation.VERTICAL, false, false, false);
		XYPlot plot = chart.getXYPlot();
		final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
		ChartUtilities.saveChartAsJPEG(new File("chart.jpg"), chart, 1024,1024);
		
	}

	private static void handleGSMMessage(GSMScan gsmScan) {
		gsmBeacons.add(gsmScan.getTimestamp(), gsmScan.getScanCount());
		
	}

	private static void handleWifiMessage(WifiScan wifiScan) 
	{
		wifiBeacons.add((wifiScan.getTimestamp() - initTimeStamp) / 1e3f,wifiScan.getScanCount());
	}
}
