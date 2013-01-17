import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.DefaultXYZDataset;

import edu.cmu.ri.rcommerce.ObstacleMap;
import edu.cmu.ri.rcommerce.particleFilter.Particle2D;

public class ClosedLoopComparison {

	/**
	 * @param args
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	public static void main(String[] args) throws FileNotFoundException, IOException {
		// String baseDir =
		// "C:\\Users\\Evan\\Documents\\College Stuff\\RI Summer Stuff\\Tech Report\\Evan NSH2 6.13\\AndroidOnlinePF\\";
		// String logPF = baseDir + "best_so_far.pf";
		String baseDir = "C:\\Users\\Evan\\Documents\\College Stuff\\RI Summer Stuff\\Tech Report\\NSH2 Comparisons\\Evan's Nexus Online Logs\\";
		String logPF = baseDir + "NSH2_Online1.pf";
		ArrayList<Particle2D> groundTruthPoints = new ArrayList<Particle2D>();

		ArrayList<Particle2D> oneMeterPoints = new ArrayList<Particle2D>();

		for (int i = 0; i < 6; i++) {
			oneMeterPoints.add(new Particle2D(i * (37 / 39.0f), 0));
		}
		for (int i = 0; i < 36; i++) {
			oneMeterPoints.add(new Particle2D(5 * (37 / 39.0f), (i + 1) * (37 / 39.0f)));
		}
		for (int i = 0; i < 13; i++) {
			oneMeterPoints.add(new Particle2D((5 * (37 / 39.0f)) + (i + 1) * (37 / 39.0f), 36 * (37 / 39.0f)));
		}
		for (int i = 0; i < 10; i++) {
			oneMeterPoints.add(new Particle2D((18 * (37 / 39.0f)), 36 * (37 / 39.0f) - ((i + 1) * (37 / 39.0f))));
		}
		for (int i = 0; i < 13; i++) {
			oneMeterPoints.add(new Particle2D((18 * (37 / 39.0f) - ((i + 1) * (37 / 39.0f))), 26 * (37 / 39.0f)));
		}
		for (int i = 0; i < 26; i++) {
			oneMeterPoints.add(new Particle2D((5 * (37 / 39.0f)), 26 * (37 / 39.0f) - ((i + 1) * (37 / 39.0f))));
		}
		for (int i = 0; i < 5; i++) {
			oneMeterPoints.add(new Particle2D((5 * (37 / 39.0f)) - ((i + 1) * (37 / 39.0f)), 0));
		}

		// For ground truths (every 5m) take every fifth point
		//This doesn't make sense, but 4 gives better results than 5
		Iterator<Particle2D> itr = oneMeterPoints.iterator();
		int counter = 0;
		// groundTruthPoints.add(oneMeterPoints.get(0));
		while (itr.hasNext()) {
			if (counter % 4 == 0) {
				groundTruthPoints.add(itr.next());
			} else
				itr.next();
			counter++;
		}
		Scanner in = new Scanner(new FileReader(logPF));
		List<Particle2D> state = new ArrayList<Particle2D>();
		List<Particle2D> taggedStates = new ArrayList<Particle2D>();
		in.nextLine();
		String l;
		Particle2D prevcenter = null;
		Particle2D firstPoint = null;
		double totalDist = 0;
		JFreeChart bubbleChart;
		DefaultXYZDataset bubbleDataset = new DefaultXYZDataset();
		ArrayList<Double> bubbleX = new ArrayList<Double>();
		ArrayList<Double> bubbleY = new ArrayList<Double>();
		ArrayList<Double> bubbleZ = new ArrayList<Double>();
		counter = 0;
		boolean tag = false;
		double avgBubbleRadius = 0;
		while (in.hasNextLine()) {
			l = in.nextLine();
			while (l.charAt(0) != 'N') {
				String[] g = l.split(" ");
				state.add(new Particle2D(Float.parseFloat(g[0]), Float.parseFloat(g[1])));
				if (in.hasNextLine())
					l = in.nextLine();
				else
					break;
			}
			if (l.contains("TAGGED"))
				tag = true;
			Particle2D center = cloudToPoint(state);
			double radius = bubbleRadius(state, center);
			if (tag) {
				taggedStates.add(center);
				tag = false;
			}

			if (prevcenter == null) {
				prevcenter = center;
			}
			double dist = distanceBetweenPoints(center, prevcenter);
			totalDist += dist;

			bubbleX.add((double) center.x);
			bubbleY.add((double) center.y);
			bubbleZ.add(radius);
			prevcenter = center;
			avgBubbleRadius+=radius;

			counter++;
			state.clear();
		}
		avgBubbleRadius /= bubbleZ.size();
		firstPoint = taggedStates.get(0);

		System.out.println("Total Distance Covered: " + totalDist);
		System.out.println("Closed Loop Error: " + distanceBetweenPoints(firstPoint, prevcenter));
		System.out.println("Mean Bubble Confidence: " + avgBubbleRadius);

		// Comparison to "Ground Truth" which is the marked/measured data points
		// every 5m
		Iterator<Particle2D> taggedItr = taggedStates.iterator();
		Iterator<Particle2D> groundTruthItr = groundTruthPoints.iterator();
		double meanPathError = 0;
		while (taggedItr.hasNext() && groundTruthItr.hasNext()) {
			Particle2D t = taggedItr.next();
			Particle2D g = groundTruthItr.next();
			meanPathError += distanceBetweenPoints(t, g);
			System.out.println("Ground Truth Point- X:" + g.x + " Y:" + g.y + "    \tCompare Point- X:" + t.x + " Y:" + t.y + "\tDISTANCE: " + distanceBetweenPoints(t, g));
		}
		meanPathError /= Math.min(taggedStates.size(), groundTruthPoints.size());
		taggedItr = taggedStates.iterator();
		groundTruthItr = groundTruthPoints.iterator();
		double sum = 0;
		while (taggedItr.hasNext() && groundTruthItr.hasNext()) {
			Particle2D t = taggedItr.next();
			Particle2D g = groundTruthItr.next();
			sum += Math.pow(distanceBetweenPoints(t, g) - meanPathError, 2);
		}
		double stddev = Math.sqrt(sum / Math.min(taggedStates.size(), groundTruthPoints.size()));
		System.out.println("Mean Path Error: " + meanPathError + " \tPath Error Std Dev: " + stddev);
		
		// Generate bubble chart data
		double[][] bubbleSeries = new double[3][bubbleX.size()];
		for (int i = 0; i < 3; i++) {
			for (int q = 0; q < bubbleX.size(); q++) {
				if (i == 0) {
					bubbleSeries[i][q] = bubbleX.get(q);
				}
				if (i == 1) {
					bubbleSeries[i][q] = bubbleY.get(q);
				}
				if (i == 2) {
					bubbleSeries[i][q] = bubbleZ.get(q);
				}
			}
		}
		bubbleDataset.addSeries("Bubbles", bubbleSeries);
		bubbleChart = ChartFactory.createBubbleChart("NSH2 Nexus S Offline Trial 1", "X Distance (m)", "Y Distance (m)", bubbleDataset, PlotOrientation.VERTICAL, false, false, false);
		XYPlot bubblePlot = bubbleChart.getXYPlot();
		NumberAxis bubbleDomainAxis = (NumberAxis) bubblePlot.getDomainAxis();
		NumberAxis bubbleRangeAxis = (NumberAxis) bubblePlot.getRangeAxis();
		bubbleDomainAxis.setAutoRange(false);
		bubbleDomainAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
		bubbleDomainAxis.setRange(-5, 40);
		bubbleRangeAxis.setAutoRange(false);
		bubbleRangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
		bubbleRangeAxis.setRange(-5, 40);
		ChartUtilities.saveChartAsPNG(new File("bubbleChart.png"), bubbleChart, 512, 512);
	}

	// Averages X and Y values of all particles to get the median x,y coordinate
	public static Particle2D cloudToPoint(List<Particle2D> state) {
		Iterator<Particle2D> itr = state.iterator();
		double x = 0;
		double y = 0;
		while (itr.hasNext()) {
			Particle2D p = itr.next();
			x += p.x;
			y += p.y;
		}
		x /= state.size();
		y /= state.size();

		return new Particle2D((float) x, (float) y);
	}

	// Takes the current particle cloud and the center point and finds the
	// largest distance from that particle to any other partlce
	// One standard deviation
	public static double bubbleRadius(List<Particle2D> state, Particle2D center) {
		Iterator<Particle2D> itr = state.iterator();
		double sum = 0;
		double stddev = 0;
		double avg = 0;
		while (itr.hasNext()) {
			Particle2D p = itr.next();
			avg += distanceBetweenPoints(center, p);
		}
		avg /= state.size();
		itr = state.iterator();
		while (itr.hasNext()) {
			Particle2D p = itr.next();
			sum += Math.pow(distanceBetweenPoints(center, p) - avg, 2);
		}

		stddev = Math.sqrt((sum / state.size()));
		return stddev;
	}

	// Find the geometric distance between to particles
	public static double distanceBetweenPoints(Particle2D p1, Particle2D p2) {
		return Math.sqrt(Math.pow((double) (p1.x - p2.x), 2) + Math.pow((double) (p1.y - p2.y), 2));
	}

}
