import java.io.*;

import org.jfree.chart.*;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.*;

import edu.cmu.ri.rcommerce.Messages.LightInfo;
import edu.cmu.ri.rcommerce.Messages.MessageWrapper;
import edu.cmu.ri.rcommerce.Messages.MessageWrapper.Type;
import edu.cmu.ri.rcommerce.Messages.ProximityInfo;


public class LightandProximityGraph {
	static XYSeries proxValues, lightValues; 
	
	public static void main(String[] args) throws IOException {
		InputStream in = new FileInputStream(args[0]);
		
		proxValues = new XYSeries("Proximity");
		lightValues = new XYSeries("Light");
		
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
			case ProximityInfo:
				handleProxMessage(wrap.getProximityInfo());
				break;
			case LightInfo:
				//handleLightMessage(wrap.getLightInfo());
				break;
			default:
				break;
			}
		}

		XYSeriesCollection dataset = new XYSeriesCollection();
		dataset.addSeries(proxValues);
		dataset.addSeries(lightValues);
		
		JFreeChart chart = 
			ChartFactory.createXYLineChart("Proximity and Light", "Time", "proximity: cm, light: lux",
			dataset, PlotOrientation.VERTICAL, true, false, false);
		
		ChartUtilities.saveChartAsJPEG(new File("chart.jpg"), chart, 1024,1024);
		
	}

	private static void handleProxMessage(ProximityInfo proxInfo) {
		System.out.println("decoding proximity");
		proxValues.add(proxInfo.getTimestamp(), proxInfo.getProximity());
		
	}

	private static void handleLightMessage(LightInfo lightInfo) 
	{
		System.out.println("decoding light");
		lightValues.add(lightInfo.getTimestamp(),lightInfo.getLight());
	}

}
