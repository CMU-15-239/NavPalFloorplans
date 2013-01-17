import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.TreeSet;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class GSMChannelComparison {

	/**
	 * @param args
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	public static void main(String[] args) throws IOException {
		Scanner inA = null;
		Scanner in1 = null;
		Scanner in2 = null;
		Scanner in3 = null;
		Scanner in4 = null;
		try {
			inA = new Scanner(new FileReader("C:\\Users\\Evan\\Documents\\College Stuff\\RI Summer Stuff\\GSM SCANS\\GSM-Modem_NSH_A - Cleaned.log"));
			in1 = new Scanner(new FileReader("C:\\Users\\Evan\\Documents\\College Stuff\\RI Summer Stuff\\GSM SCANS\\GSM-Modem_NSH_1 - Cleaned.log"));
			in2 = new Scanner(new FileReader("C:\\Users\\Evan\\Documents\\College Stuff\\RI Summer Stuff\\GSM SCANS\\GSM-Modem_NSH_2 - Cleaned.log"));
			in3 = new Scanner(new FileReader("C:\\Users\\Evan\\Documents\\College Stuff\\RI Summer Stuff\\GSM SCANS\\GSM-Modem_NSH_3 - Cleaned.log"));
			in4 = new Scanner(new FileReader("C:\\Users\\Evan\\Documents\\College Stuff\\RI Summer Stuff\\GSM SCANS\\GSM-Modem_NSH_4 - Cleaned.log"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		Map<Integer, ArrayList<Integer>> mapA = new TreeMap<Integer, ArrayList<Integer>>();
		Map<Integer, ArrayList<Integer>> map1 = new TreeMap<Integer, ArrayList<Integer>>();
		Map<Integer, ArrayList<Integer>> map2 = new TreeMap<Integer, ArrayList<Integer>>();
		Map<Integer, ArrayList<Integer>> map3 = new TreeMap<Integer, ArrayList<Integer>>();
		Map<Integer, ArrayList<Integer>> map4 = new TreeMap<Integer, ArrayList<Integer>>();
		// Process Level A
		while (inA.hasNextLine()) {
			String line = inA.nextLine();
			if (line.contains("arfcn:") && line.contains("rxLev:") && !line.contains("bsic")) {
				Integer chan = Integer.parseInt(line.split(" ")[1]);
				Integer pwr = Integer.parseInt(line.split(" ")[3]);
				mapA = addToMap(chan, pwr, mapA);
			} else if (line.contains("arfcn:") && line.contains("rxLev:") && line.contains("bsic")) {
				Integer chan = Integer.parseInt(line.split(" ")[1]);
				Integer pwr = Integer.parseInt(line.split(" ")[5]);
				mapA = addToMap(chan, pwr, mapA);
			}
		}

		while (in1.hasNextLine()) {
			String line = in1.nextLine();
			if (line.contains("arfcn:") && line.contains("rxLev:") && !line.contains("bsic")) {
				Integer chan = Integer.parseInt(line.split(" ")[1]);
				Integer pwr = Integer.parseInt(line.split(" ")[3]);
				mapA = addToMap(chan, pwr, map1);
			} else if (line.contains("arfcn:") && line.contains("rxLev:") && line.contains("bsic")) {
				Integer chan = Integer.parseInt(line.split(" ")[1]);
				Integer pwr = Integer.parseInt(line.split(" ")[5]);
				mapA = addToMap(chan, pwr, map1);
			}
		}

		while (in2.hasNextLine()) {
			String line = in2.nextLine();
			if (line.contains("arfcn:") && line.contains("rxLev:") && !line.contains("bsic")) {
				Integer chan = Integer.parseInt(line.split(" ")[1]);
				Integer pwr = Integer.parseInt(line.split(" ")[3]);
				mapA = addToMap(chan, pwr, map2);
			} else if (line.contains("arfcn:") && line.contains("rxLev:") && line.contains("bsic")) {
				Integer chan = Integer.parseInt(line.split(" ")[1]);
				Integer pwr = Integer.parseInt(line.split(" ")[5]);
				mapA = addToMap(chan, pwr, map2);
			}
		}

		while (in3.hasNextLine()) {
			String line = in3.nextLine();
			if (line.contains("arfcn:") && line.contains("rxLev:") && !line.contains("bsic")) {
				Integer chan = Integer.parseInt(line.split(" ")[1]);
				Integer pwr = Integer.parseInt(line.split(" ")[3]);
				mapA = addToMap(chan, pwr, map3);
			} else if (line.contains("arfcn:") && line.contains("rxLev:") && line.contains("bsic")) {
				Integer chan = Integer.parseInt(line.split(" ")[1]);
				Integer pwr = Integer.parseInt(line.split(" ")[5]);
				mapA = addToMap(chan, pwr, map3);
			}
		}

		while (in4.hasNextLine()) {
			String line = in4.nextLine();
			if (line.contains("arfcn:") && line.contains("rxLev:") && !line.contains("bsic")) {
				Integer chan = Integer.parseInt(line.split(" ")[1]);
				Integer pwr = Integer.parseInt(line.split(" ")[3]);
				mapA = addToMap(chan, pwr, map4);
			} else if (line.contains("arfcn:") && line.contains("rxLev:") && line.contains("bsic")) {
				Integer chan = Integer.parseInt(line.split(" ")[1]);
				Integer pwr = Integer.parseInt(line.split(" ")[5]);
				mapA = addToMap(chan, pwr, map4);
			}
		}

		// Done importing all channel/power data into the various maps for each
		// floor

		TreeSet<Integer> channels = new TreeSet<Integer>();
		channels.addAll(mapA.keySet());
		channels.addAll(map1.keySet());
		channels.addAll(map2.keySet());
		channels.addAll(map3.keySet());
		channels.addAll(map4.keySet());
		Iterator<Integer> chanItr = channels.iterator();
		while (chanItr.hasNext()) {
			Integer chan = chanItr.next();
			XYSeriesCollection dataset = new XYSeriesCollection();
			ArrayList<Integer> floorApowers = mapA.get(chan);
			ArrayList<Integer> floor1powers = map1.get(chan);
			ArrayList<Integer> floor2powers = map2.get(chan);
			ArrayList<Integer> floor3powers = map3.get(chan);
			ArrayList<Integer> floor4powers = map4.get(chan);
			int maxNumChans = 0;
			if(floorApowers != null)
			maxNumChans = Math.max(maxNumChans, floorApowers.size());
			else
				floorApowers = new ArrayList<Integer>();
			if(floor1powers != null)
			maxNumChans = Math.max(maxNumChans, floor1powers.size());
			else
				floor1powers = new ArrayList<Integer>();
			if(floor2powers != null)
			maxNumChans = Math.max(maxNumChans, floor2powers.size());
			else
				floor2powers = new ArrayList<Integer>();
			if(floor3powers != null)
			maxNumChans = Math.max(maxNumChans, floor3powers.size());
			else
				floor3powers = new ArrayList<Integer>();
			if(floor4powers != null)
			maxNumChans = Math.max(maxNumChans, floor4powers.size());
			else
				floor4powers = new ArrayList<Integer>();
			
			if(maxNumChans < 27)
				continue;
			XYSeries fA = new XYSeries("Floor A");
			XYSeries f1 = new XYSeries("Floor 1");
			XYSeries f2 = new XYSeries("Floor 2");
			XYSeries f3 = new XYSeries("Floor 3");
			XYSeries f4 = new XYSeries("Floor 4");
			for (int i = 0; i < maxNumChans; i++) {
				try {
					fA.add(i, floorApowers.get(i));
				} catch (IndexOutOfBoundsException e) {
					fA.add(i, -120);
				}
				try {
					f1.add(i, floor1powers.get(i));
				} catch (IndexOutOfBoundsException e) {
					f1.add(i, -120);
				}
				try {
					f2.add(i, floor2powers.get(i));
				} catch (IndexOutOfBoundsException e) {
					f2.add(i, -120);
				}
				try {
					f3.add(i, floor3powers.get(i));
				} catch (IndexOutOfBoundsException e) {
					f3.add(i, -120);
				}
				try {
					f4.add(i, floor4powers.get(i));
				} catch (IndexOutOfBoundsException e) {
					f4.add(i, -120);
				}			
			}
			dataset.addSeries(fA);
			dataset.addSeries(f1);
			dataset.addSeries(f2);
			dataset.addSeries(f3);
			dataset.addSeries(f4);
			JFreeChart chart = ChartFactory.createXYLineChart("GSM Signal Strength per Floor", "Position", "Signal Strength (dB)", dataset, PlotOrientation.VERTICAL, true, false, false);
			XYPlot plot = chart.getXYPlot();
			NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
			NumberAxis domainAxis = (NumberAxis) plot.getDomainAxis();
			rangeAxis.setAutoRangeIncludesZero(false);
			domainAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
			ChartUtilities.saveChartAsJPEG(new File("GSMCharts/" + chan + ".jpg"), chart, 1024, 1024);
			
			System.out.println(chan +"");

		}
	}

	/**
	 * Creates a chart.
	 * 
	 * @param dataset
	 *            the data for the chart.
	 * 
	 * @return a chart.
	 */
	private static JFreeChart createChart(final XYDataset dataset) {

		// create the chart...
		final JFreeChart chart = ChartFactory.createXYLineChart("Line Chart Demo 6", // chart
																						// title
		"X", // x axis label
		"Y", // y axis label
		dataset, // data
		PlotOrientation.VERTICAL, true, // include legend
		true, // tooltips
		false // urls
		);

		// NOW DO SOME OPTIONAL CUSTOMISATION OF THE CHART...
		chart.setBackgroundPaint(Color.white);

		// final StandardLegend legend = (StandardLegend) chart.getLegend();
		// legend.setDisplaySeriesShapes(true);

		// get a reference to the plot for further customisation...
		final XYPlot plot = chart.getXYPlot();
		plot.setBackgroundPaint(Color.lightGray);
		// plot.setAxisOffset(new Spacer(Spacer.ABSOLUTE, 5.0, 5.0, 5.0, 5.0));
		plot.setDomainGridlinePaint(Color.white);
		plot.setRangeGridlinePaint(Color.white);

		final XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
		renderer.setSeriesLinesVisible(0, false);
		renderer.setSeriesShapesVisible(1, false);
		plot.setRenderer(renderer);

		// change the auto tick unit selection to integer units only...
		final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
		rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
		// OPTIONAL CUSTOMISATION COMPLETED.

		return chart;
	}

	private static Map<Integer, ArrayList<Integer>> addToMap(Integer chan, Integer pow, Map<Integer, ArrayList<Integer>> map) {
		ArrayList<Integer> pows = map.get(chan);
		if (pows == null) {
			pows = new ArrayList<Integer>();
		}
		pows.add(pow);
		map.put(chan, pows);
		return map;
	}

}
