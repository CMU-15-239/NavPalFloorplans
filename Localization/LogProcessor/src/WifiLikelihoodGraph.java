import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class WifiLikelihoodGraph {
	static XYDataset dataset;
	static long initTime = 0;
	static long position = 0;
	static TreeMap<Long, XYSeries> map;
	//"C:\\Users\\Evan\\Documents\\College Stuff\\RI Summer Stuff\\Tech Report\\Evan NSH2 6.13\\GyroCalibrated\\wifi and gsm dump.txt";
	public static void main(String[] args) throws IOException {
		Scanner in = new Scanner(new FileReader(args[0]));
		map = new TreeMap<Long, XYSeries>();
		while(in.hasNextLine()) {
			readLine(in.nextLine());
		}
		
		XYSeriesCollection dataset = new XYSeriesCollection();
		Set<Long> APs = map.keySet();
		Iterator<Long> itr = APs.iterator();
		while(itr.hasNext()) {
			dataset.addSeries(map.get(itr.next()));
		}
		JFreeChart chart = 
			ChartFactory.createScatterPlot("Wifi Signal Strength per AP per Location", "Location", "Signal Strength (dB)",
			dataset, PlotOrientation.VERTICAL, false, false, false);
		ChartUtilities.saveChartAsJPEG(new File("chart.jpg"), chart, 1024, 1024);

	}

	private static void readLine(String line) {
		String[] parts = line.split(" ");
		long timestamp = Long.parseLong(parts[0]);
		if(initTime == 0)
			initTime = timestamp;
		double x = Double.parseDouble(parts[1]);
		double y = Double.parseDouble(parts[2]);
		int numWifi = Integer.parseInt(parts[4]);
		for(int i = 0; i <= numWifi; i+=2) {
			long bssid = Long.parseLong(parts[i + 5]);
			long strength = Long.parseLong(parts[i + 6]);
			XYSeries xy = map.get(bssid);
			if(xy == null)
				xy = new XYSeries(bssid);
			xy.add(position, strength);
			map.put(bssid, xy);
		}
		position++;
	}
}
