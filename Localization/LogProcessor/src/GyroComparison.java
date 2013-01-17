import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;


public class GyroComparison {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		Scanner phone = new Scanner(new FileReader("C:\\Users\\Evan\\Documents\\College Stuff\\RI Summer Stuff\\Tech Report\\gyro calibration\\phone\\gyro test.log.gyro.xyzt"));
		Scanner robot = new Scanner(new FileReader("C:\\Users\\Evan\\Documents\\College Stuff\\RI Summer Stuff\\Tech Report\\gyro calibration\\robot\\gyro_counterclockwise.log"));
		int phoneAxis = 2;
		long pinitTime = 0;
		double rinitTime = 0;
		double pinitGyro = 0;
		double rinitGyro = 0;
		XYSeries p = new XYSeries("Phone Gyro");
		XYSeries r = new XYSeries("Robot Gyro");
		
		while(phone.hasNext() && robot.hasNext()) {
			String phoneLine = phone.nextLine();
			phone.nextLine();
			phone.nextLine();
			String[] phoneArray = phoneLine.split(" ");
			robot.nextLine();
			robot.nextLine();
			robot.nextLine();
			String robotLine = robot.nextLine();
			String[] robotArray = robotLine.split(" ");
			
			String pTimestampS = phoneArray[3];
			long pTimestamp = Long.parseLong(pTimestampS);
			String rTimestampS = robotArray[0];
			double rTimestamp = Double.parseDouble(rTimestampS);
			String pGyroS = phoneArray[phoneAxis];
			double pGyro = Double.parseDouble(pGyroS);
			String rGyroS = robotArray[3];
			double rGyro = Double.parseDouble(rGyroS);
			
			if(pinitTime == 0)
				pinitTime = pTimestamp;
			if(rinitTime == 0)
				rinitTime = rTimestamp;
			if(pinitGyro == 0)
				pinitGyro = pGyro;
			if(rinitGyro == 0)
				rinitGyro = rGyro;
			if(pTimestamp - pinitTime < 7500000000.0)
			p.add(pTimestamp - pinitTime, (pGyro - pinitGyro) * -10);
			r.add((rTimestamp - rinitTime + 0) * 10000000, rGyro - rinitGyro);
			
		}
		
		XYSeriesCollection dataset = new XYSeriesCollection();
		dataset.addSeries(p);
		dataset.addSeries(r);
		JFreeChart chart = 
			ChartFactory.createXYLineChart("Gyro Drift Comparison", "Time", "Gyro",
			dataset, PlotOrientation.VERTICAL, true, false, false);
		ChartUtilities.saveChartAsJPEG(new File("chart.jpg"), chart, 1024, 1024);

	}

}
