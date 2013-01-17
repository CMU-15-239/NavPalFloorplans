import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Point;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.JScrollPane;

import edu.cmu.ri.rcommerce.ObstacleMap;
import edu.cmu.ri.rcommerce.particleFilter.Particle2D;


public class PFLogViewer {
	enum LocMethod{Wifi,Gsm};
	
	static boolean dumpImages = true;
	final static LocMethod method = LocMethod.Wifi; 
	public static void main(String[] args) throws IOException
	{
		//String baseDir = "C:\\Users\\Evan\\Documents\\College Stuff\\RI Summer Stuff\\Tech Report\\Evan NSH2 6.13\\AndroidOnlinePF\\";
		//String logPF = baseDir + "DRtesting1.pf";
		String baseDir = "H:\\logs\\";
		String logPF = baseDir + "pfoutSERVICE.pf";
		String obstacleMap = baseDir + "NSH1-Run6.map";
		
		ObstacleMap map = ObstacleMap.loadFromStream(new FileReader(obstacleMap));
		Scanner in = new Scanner(new FileReader(logPF));
		List<Particle2D> state = new ArrayList<Particle2D>();
		in.nextLine();
		String l = in.nextLine();
		while(l.charAt(0) != 'N') {
			String[] g = l.split(" ");
			state.add(new Particle2D(Float.parseFloat(g[0]), Float.parseFloat(g[1])));
			l = in.nextLine();
		}
		
		JFrame frame = new JFrame();
		frame.setTitle("PFLog Visualizer");
		frame.setSize(map.xSize/2, map.ySize/2);
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		MapViewer mapViewer = new MapViewer(map,state,true);
		JScrollPane scroller = new JScrollPane(mapViewer); 
		scroller.getViewport().setViewPosition(new Point(350, 100));
		Container contentPane = frame.getContentPane();
		contentPane.add(scroller,BorderLayout.CENTER);

		frame.setVisible(true);
		int frameNumber = 0;
		state.clear();
		l = in.nextLine();
		while(in.hasNextLine())
		{
			while(l.charAt(0) != 'N') {
				String[] g = l.split(" ");
				state.add(new Particle2D(Float.parseFloat(g[0]), Float.parseFloat(g[1])));
				l = in.nextLine();
			}
			mapViewer.setPoints(state, true);
			if (dumpImages)
			{
				new File("PF_integrated").mkdir();
				OutputStream out = new FileOutputStream("PF_integrated//img" + frameNumber + ".png");
				mapViewer.dumpImage(out);
				out.close();
			}
			mapViewer.repaint();
			System.out.println("X:" + state.get(0).x + " Y:" + state.get(0).y);
			System.out.println("iteration: " + frameNumber++);
			try{
				Thread.sleep(500);}catch(InterruptedException e){}
			state.clear();
			l = in.nextLine();
		}
		
		
	}

}
