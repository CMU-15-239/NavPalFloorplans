import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import edu.cmu.ri.rcommerce.*;
import edu.cmu.ri.rcommerce.ObstacleMap.Cell;
import edu.cmu.ri.rcommerce.particleFilter.Particle2D;


public class MapViewer extends JPanel {
	double scale = 1f;
	ObstacleMap map;
	List<Point2D> markedPoints;
	List<Double> weights;
	public MapViewer(ObstacleMap map,List<Point2D> markedPoints)
	{
		this.map = map;
		this.markedPoints = markedPoints;
		setPreferredSize(new Dimension(map.xSize, map.ySize));
	}
	//3rd argument added to distinguish the constructor from the List<Point2D> constructor
	public MapViewer(ObstacleMap map,List<Particle2D> markedPoints, boolean useParticles)
	{
		this.map = map;
		this.markedPoints = new ArrayList<Point2D>();
		this.weights = new ArrayList<Double>();
		for (Particle2D p : markedPoints)
		{
			this.markedPoints.add(new Point2D.Double(p.x,p.y));
			weights.add(new Double(p.weight));
		}
		setPreferredSize(new Dimension(map.xSize, map.ySize));
	}
	public void paintComponent(Graphics g_k) {
		super.paintComponent(g_k);
		if (map == null)
			return;
		
		Graphics2D g = (Graphics2D) g_k;
		drawToGraphics(g);

	}
	
	public void drawToGraphics(Graphics2D g)
	{
		g.scale(scale, -scale);
		g.translate(0, -map.ySize);
		
		
		BufferedImage img = new BufferedImage(map.xSize, map.ySize, BufferedImage.TYPE_INT_ARGB);
		
		for (int y = 0 ; y<map.ySize ; y++)
		{
			for (int x=0 ; x<map.xSize ; x++)
			{
				Cell c = map.cellArray[y][x];
				switch (c)
				{
				case FREE_SPACE:
					img.setRGB(x, y, 0xFF00AA00);
					break;
				case OCCUPIED:
					img.setRGB(x, y, 0xFF00FF00);
					break;
				case UNKNOWN:
					img.setRGB(x, y, 0xFF000000);
					break;
				}
				
			}
		}
		
		double max = 0;
		if (weights != null)
			max = Collections.max(weights);
		
		if (markedPoints != null)
		{
			g.setColor(Color.red);
			for (int i = 0 ; i<markedPoints.size() ; i++)
			{
				Point imageP = robotCoordinatesToImageCoordinates(markedPoints.get(i), map.xSize, map.ySize);
				//g.fillOval(imageP.x, imageP.y, 5, 5);
				if (imageP.x > 1 && imageP.y > 1 && imageP.x < img.getWidth()-1 && imageP.y < img.getHeight()-1)
				{
					//uncomment for larger points
					/*for (int x = -1 ; x<= 1 ; x++)
						for (int y = -1 ; y<= 1 ; y++)
							img.setRGB(imageP.x+x, imageP.y+y, 0xFFFF0000);
						*/	
					img.setRGB(imageP.x, imageP.y, 0xFFFF0000);
				}
			}
		}
		//img.setRGB(500, 500, 0xFF0000FF);
		//img.setRGB(510, 500, 0xFF00FFFF);
		//img.setRGB(500, 510, 0xFFFF00FF);
		
		g.drawImage(img, 0, 0, map.xSize, map.ySize, null, null);	
	}
	
	//assumes the robot's 0,0 point is the center of the image, the robot's coordinates are in meters, and each pixel corresponds to a decimeter
	Point robotCoordinatesToImageCoordinates(Point2D coords,int xSize, int ySize)
	{
		double x = coords.getX()*10 + xSize/2;
		double y = coords.getY()*10 + ySize/2;
		
		return new Point((int)x, (int)y);
	}
	
	public void setMap(ObstacleMap map)
	{
		this.map = map;
	}
	
	public void setPoints(List<Point2D> points)
	{
		markedPoints = points;
	}
	public void setPoints(List<Particle2D> points, boolean useParticles)
	{
		markedPoints = new ArrayList<Point2D>();
		for (Particle2D p : points)
			markedPoints.add(new Point2D.Double(p.x,p.y));
	}
	public void dumpImage(OutputStream out) throws IOException
	{
		BufferedImage im = new BufferedImage((int)(map.xSize*scale),(int)(map.ySize*scale), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = im.createGraphics();
		drawToGraphics(g);
		ImageIO.write(im, "PNG", out);
	}

}
