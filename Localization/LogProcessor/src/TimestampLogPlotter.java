import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;


public class TimestampLogPlotter {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		String baseDir = "C:\\Users\\Evan\\Documents\\College Stuff\\RI Summer Stuff\\Tech Report\\NSH2 Comparisons\\Evan's Nexus Online Logs\\";
		String logPF = baseDir + "NSH2_Online4.pf";
		String logOffline = baseDir + "NSH2_Offline4.pfout";
		Scanner inPF = new Scanner(new FileReader(logPF));
		Scanner inOffline = new Scanner(new FileReader(logOffline));
		String l;

		XYSeries online = new XYSeries("Online");
		XYSeries offline = new XYSeries("Offline");
		
		long lastTimeStampPF = 0;
		long lastTimeStampOffline = 0;
		int counter = 0;
		while (inPF.hasNextLine()) {
			l = inPF.nextLine();
			if(l.contains("NS")) {
			String[] q = l.split(" ");
			if(lastTimeStampPF != 0) {
				online.add(counter, (Long.parseLong(q[1]) - lastTimeStampPF));
			}
			lastTimeStampPF = Long.parseLong(q[1]);
			counter++;
			}
			
		}
		counter = 0;
		while (inOffline.hasNextLine()) {
			l = inOffline.nextLine();
			if(l.contains("NS")) {
			String[] q = l.split(" ");
			if(lastTimeStampOffline != 0) {
				offline.add(counter, (Long.parseLong(q[1]) - lastTimeStampOffline) / 1e6);
			}
			lastTimeStampOffline = Long.parseLong(q[1]);
			counter++;
			}
			
		}
		
		XYSeriesCollection dataset = new XYSeriesCollection();
		dataset.addSeries(online);
		dataset.addSeries(offline);
		
		JFreeChart chart = 
			ChartFactory.createXYLineChart("Computational Time", "State Output", "Time (milliseconds)",
			dataset, PlotOrientation.VERTICAL, true, false, false);
		XYPlot plot = chart.getXYPlot();
		final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
		ChartUtilities.saveChartAsPNG(new File("TimestampPlot.PNG"), chart, 512,512);

	}

}
