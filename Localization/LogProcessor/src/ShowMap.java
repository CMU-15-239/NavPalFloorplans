import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.JScrollPane;

import edu.cmu.ri.rcommerce.ObstacleMap;
import edu.cmu.ri.rcommerce.particleFilter.Particle2D;


public class ShowMap {

	public static void main(String[] args) throws IOException {
		//String mapFile = "D:\\workspace\\rCommerce\\stuff\\logs\\IROS Paper\\Gates 6 April 15\\g6r2\\map.map";
		//String correlatedDataFile = "D:\\workspace\\rCommerce\\stuff\\logs\\IROS Paper\\Gates 6 April 15\\g6r2\\wifi and gsm dump.txt";
		String mapFile = "/home/bkannan/software/GoogleAI/logs/NSH1-Run6.map";
		String correlatedDataFile = "/home/bkannan/software/GoogleAI/logs/NSH1-Run6.txt";
		Reader mapReader = new FileReader(mapFile);
		//BufferedReader dataPointReader = new BufferedReader(new FileReader(correlatedDataFile));
		OutputStream imageOut = new FileOutputStream("map.png");
		
		/*ArrayList<Particle2D> oneMeterPoints = new ArrayList<Particle2D>();
		oneMeterPoints.add(new Particle2D(0.5f, 0));
		oneMeterPoints.add(new Particle2D(1, 0));
		oneMeterPoints.add(new Particle2D(1.5f, 0));
		oneMeterPoints.add(new Particle2D(2, 0));
		oneMeterPoints.add(new Particle2D(2.5f, 0));
		oneMeterPoints.add(new Particle2D(3, 0));
		oneMeterPoints.add(new Particle2D(3.5f, 0));
		oneMeterPoints.add(new Particle2D(4, 0));
		for (int i = 0; i < 69; i++) {
			oneMeterPoints.add(new Particle2D(4.25f, i / 2.0f));
		}
		for (int i = 0; i < 25; i++) {
			oneMeterPoints.add(new Particle2D(4.75f + i / 2.0f, 34));
		}
		for (int i = 0; i < 19; i++) {
			oneMeterPoints.add(new Particle2D(16.75f, 33.5f - i / 2.0f));
		}
		for (int i = 0; i < 24; i++) {
			oneMeterPoints.add(new Particle2D(16.25f - i / 2.0f, 33.5f - 9));
		}
		for (int i = 0; i < 49; i++) {
			oneMeterPoints.add(new Particle2D(4.25f, 24f - i / 2.0f));
		}
		oneMeterPoints.add(new Particle2D(4f, 0f));
		oneMeterPoints.add(new Particle2D(3.5f, 0));
		oneMeterPoints.add(new Particle2D(3f, 0));
		oneMeterPoints.add(new Particle2D(2.5f, 0));
		oneMeterPoints.add(new Particle2D(2f, 0));
		oneMeterPoints.add(new Particle2D(1.5f, 0));
		oneMeterPoints.add(new Particle2D(1f, 0));
		oneMeterPoints.add(new Particle2D(0.5f, 0));
		
		ArrayList<Particle2D> groundTruthPoints = new ArrayList<Particle2D>();
		
		Iterator<Particle2D> itr = oneMeterPoints.iterator();
		int counter = 0;
		groundTruthPoints.add(oneMeterPoints.get(0));
		while (itr.hasNext()) {
			if (counter ==10) {
				groundTruthPoints.add(itr.next());
				counter = 0;
			} else
				itr.next();
			counter++;
		}*/
		
		//List<Point2D> loadedWiFiPoints = getDataLocations(dataPointReader);
		ArrayList<Particle2D> grid = new ArrayList<Particle2D>();
		for(int x = 0; x < 1; x++) {
			for(int y = 0; y < 1; y++) {
				grid.add(new Particle2D(x,y));
			}
		}
		
		ObstacleMap map = ObstacleMap.loadFromStream(mapReader);
		
		JFrame frame = new JFrame();
		frame.setTitle("Map Visualizer");
		frame.setSize(map.xSize/4, map.ySize/4);
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		MapViewer mapViewer = new MapViewer(map, grid, true);
		JScrollPane scroller = new JScrollPane(mapViewer); 
		Container contentPane = frame.getContentPane();
		contentPane.add(scroller,BorderLayout.CENTER);

		frame.setVisible(true);
		
		mapViewer.dumpImage(imageOut);
		imageOut.close();

	}
	
	public static List<Point2D> getDataLocations(BufferedReader data) throws IOException
	{
		List<Point2D> out = new ArrayList<Point2D>();
		String reading = data.readLine();
		while (reading != null)
		{
			Scanner s = new Scanner(reading);
			double time = s.nextDouble();
			double x = s.nextDouble();
			double y = s.nextDouble();
			
			Point2D p = new Point2D.Double(x,y);
			out.add(p);
			
			reading = data.readLine();
		}
		return out;
	}
	
	

}
