import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Point;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JScrollPane;

import edu.cmu.ri.rcommerce.FastRandom;
import edu.cmu.ri.rcommerce.ObstacleMap;
import edu.cmu.ri.rcommerce.particleFilter.ImportanceResampler;
import edu.cmu.ri.rcommerce.particleFilter.Measurer;
import edu.cmu.ri.rcommerce.particleFilter.Particle2D;
import edu.cmu.ri.rcommerce.particleFilter.ParticleFilter;
import edu.cmu.ri.rcommerce.particleFilter.Resampler;
import edu.cmu.ri.rcommerce.particleFilter.Updater;
import edu.cmu.ri.rcommerce.sensor.LogPlaybackDeadReckoningGaussianUpdater;
import edu.cmu.ri.rcommerce.sensor.LogPlaybackTimeSource;
import edu.cmu.ri.rcommerce.sensor.LogPlaybackWifiRSSIRuntimeProvider;
import edu.cmu.ri.rcommerce.sensor.MapConstrainedRSSIMeasurer2D;
import edu.cmu.ri.rcommerce.sensor.RSSICalibrateProvider;
import edu.cmu.ri.rcommerce.sensor.RSSIRuntimeProvider;
import edu.cmu.ri.rcommerce.sensor.WifiSignalMapRSSICalibrateProviderKNN;


public class ParticleFilterDRandWifi {
	enum LocMethod{Wifi,Gsm};
	
	static boolean dumpImages = false;
	final static LocMethod method = LocMethod.Wifi; 
	static LogPlaybackTimeSource timeSource;
	static Updater<Particle2D> updater;
	static Measurer<Particle2D> measurer;
	static Resampler<Particle2D> resampler;
	static RSSIRuntimeProvider	rp;
	static BufferedWriter out = null;
	public static void main(String[] args) throws IOException
	{
		String baseDir = "C:\\Users\\Evan\\Documents\\College Stuff\\RI Summer Stuff\\Tech Report\\NSH2 Comparisons\\Evan's Nexus Online Logs\\";
		String correlatedData = baseDir + "wifi and gsm dump.txt";
		String testLog = baseDir + "NSH2_Offline3.log";
		String obstacleMap = baseDir + "map.map";
		String output = "NSH2_Offline3.pfout";
		
		File outFile = new File(baseDir, output);
		if (!outFile.exists())
			try {
				outFile.createNewFile();
			} catch (IOException e3) {
				e3.printStackTrace();
			}
		try {
			out = new BufferedWriter(new FileWriter(outFile));
		} catch (IOException e2) {
			e2.printStackTrace();
		}
		
		ObstacleMap map = ObstacleMap.loadFromStream(new FileReader(obstacleMap));
		ParticleFilter<Particle2D> filter = setupFilter(correlatedData, testLog,map);
		
		
		
		JFrame frame = new JFrame();
		frame.setTitle("Particle Filter Visualizer");
		frame.setSize(map.xSize/4, map.ySize/4);
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		MapViewer mapViewer = new MapViewer(map,filter.getCurrentState(),true);
		JScrollPane scroller = new JScrollPane(mapViewer); 
		scroller.getViewport().setViewPosition(new Point(1000, 1300));
		Container contentPane = frame.getContentPane();
		contentPane.add(scroller,BorderLayout.CENTER);

		frame.setVisible(true);
		int frameNumber = 0;
		int iterationCounter = 0;
		while(true)
		{
			if(iterationCounter > 230) {
				out.close();
				break;
			}
			System.out.println("IT: " + iterationCounter);
			timeSource.incrementTime(500);
			System.out.println("time: " + (timeSource.getCurrentTime() - timeSource.getStartingTime()) / 1e9);
			filter.iterate();
			List<Particle2D> state = filter.getCurrentState();
			mapViewer.setPoints(state, true);
			if (dumpImages)
			{
				new File("PF_Offline1").mkdir();
				OutputStream out = new FileOutputStream("PF_Offline1//img" + frameNumber++ + ".png");
				mapViewer.dumpImage(out);
				out.close();
			}
			long timestamp = Math.max(((LogPlaybackWifiRSSIRuntimeProvider)rp).latestTime, ((LogPlaybackDeadReckoningGaussianUpdater)updater).latestTime);
			try {
				if(((LogPlaybackDeadReckoningGaussianUpdater)updater).isTagged()) {
					out.write("NS:" + iterationCounter +  " " + timestamp + " TAGGED\n");
				}
				else {
					
					out.write("NS:" + iterationCounter + " " + timestamp + "\n");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			for (Particle2D p : state) {
				try {
					out.write(p.x + " " + p.y + "\n");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			mapViewer.repaint();
			//System.out.println("iteration: " + frameNumber);
			try{
				Thread.sleep(10);}catch(InterruptedException e){}
			Particle2D sampleParticle = filter.getCurrentState().get(0);
			System.out.println("sample location: " + sampleParticle.x + "," + sampleParticle.y);
			iterationCounter++;
		}
	}
	
	static ParticleFilter<Particle2D> setupFilter(String correlatedData,String testLog,ObstacleMap map) throws IOException
	{
		FastRandom random = new FastRandom();
		timeSource = new LogPlaybackTimeSource(testLog);
		
		RSSICalibrateProvider<Particle2D> signalMap = new WifiSignalMapRSSICalibrateProviderKNN(
				new BufferedReader(new FileReader(correlatedData)),3);
		
		rp = new LogPlaybackWifiRSSIRuntimeProvider(testLog,timeSource);
		
		java.util.List<Particle2D> startingState = new ArrayList<Particle2D>();
		for (int i=0; i<300; i++)
			startingState.add(new Particle2D(random.nextFloat() * 10 - 5, random.nextFloat() * 10 - 5));
		
		updater = new LogPlaybackDeadReckoningGaussianUpdater(testLog, timeSource, random, 2.7f, 5f, .2f);
		measurer = new MapConstrainedRSSIMeasurer2D<Particle2D>(rp, signalMap,0.5f, map);
		resampler = new ImportanceResampler<Particle2D>();
		ParticleFilter<Particle2D> filter = new ParticleFilter<Particle2D>(startingState, updater, measurer, resampler);
		return filter;
	}
	

}
