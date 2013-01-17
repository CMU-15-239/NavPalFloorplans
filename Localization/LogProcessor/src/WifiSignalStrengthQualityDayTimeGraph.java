import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import edu.cmu.ri.rcommerce.Messages.GSMScan;
import edu.cmu.ri.rcommerce.Messages.MessageWrapper;
import edu.cmu.ri.rcommerce.Messages.MessageWrapper.Type;
import edu.cmu.ri.rcommerce.Messages.WifiInfo;
import edu.cmu.ri.rcommerce.Messages.WifiScan;

public class WifiSignalStrengthQualityDayTimeGraph {
	static TreeMap<Long, XYSeries> wifis;

	public static void main(String[] args) throws IOException {
		InputStream a = new FileInputStream("C:\\Users\\Evan\\Documents\\College Stuff\\RI Summer Stuff\\Tech Report\\Evan NSH2 6.13\\GyroCalibrated\\nsh2calibrated 2.log");
		InputStream b = new FileInputStream("C:\\Users\\Evan\\Documents\\College Stuff\\RI Summer Stuff\\Tech Report\\Evan NSH2 6.13\\Trial1\\nsh2t1.log");

		int numWifiMessages = 0;
		wifis = new TreeMap<Long, XYSeries>();
		Queue<WifiScan> wifisA = new LinkedList<WifiScan>();
		Queue<WifiScan> wifisB = new LinkedList<WifiScan>();
		while (true) {
			MessageWrapper wrap;
			try {
				wrap = MessageWrapper.parseDelimitedFrom(a);
			} catch (com.google.protobuf.InvalidProtocolBufferException e) {
				System.out.println("decode error!");
				continue;
			}
			if (wrap == null)
				break;
			Type messageType = wrap.getType();

			switch (messageType) {
			case WifiScan:
				numWifiMessages++;
				handleWifiMessage(wrap.getWifiScan(), wifisA);
				break;
			default:
				break;
			}
			try {
				wrap = MessageWrapper.parseDelimitedFrom(b);
			} catch (com.google.protobuf.InvalidProtocolBufferException e) {
				System.out.println("decode error!");
				continue;
			}
			if (wrap == null)
				break;
			messageType = wrap.getType();

			switch (messageType) {
			case WifiScan:
				numWifiMessages++;
				handleWifiMessage(wrap.getWifiScan(), wifisB);
				break;
			default:
				break;
			}
		}

		int l = Math.min(wifisA.size(), wifisB.size());
		long initTimestampA = wifisA.peek().getTimestamp();
		WifiScan A = null;
		WifiScan B = null;
		for (int i = 0; i < l; i++) {
			A = wifisA.poll();
			B = wifisB.poll();
			if (A == null || B == null)
				break;
			else {

				List<WifiInfo> listA = A.getScanList();
				List<WifiInfo> listB = B.getScanList();
				TreeMap<Long, Integer> map = new TreeMap<Long, Integer>();
				for (WifiInfo q : listA) {
					map.put(q.getBSSID(), q.getLevel());
				}
				for (WifiInfo q : listB) {
					Integer diff = map.get(q.getBSSID());
					if (diff == null)
						diff = 0;
					diff -= q.getLevel();
					map.put(q.getBSSID(), diff);
				}
				Set<Long> set = map.keySet();
				Iterator<Long> itr = set.iterator();
				while (itr.hasNext()) {
					Long bssid = itr.next();
					Integer diff = map.get(bssid);
					XYSeries xy = wifis.get(bssid);
					if (xy == null)
						xy = new XYSeries(bssid);
					xy.add(A.getTimestamp() - initTimestampA, diff);
					wifis.put(bssid, xy);
				}
			}
		}

		XYSeriesCollection dataset = new XYSeriesCollection();
		Set<Long> APs = wifis.keySet();
		Iterator<Long> itr = APs.iterator();
		while (itr.hasNext()) {
			dataset.addSeries(wifis.get(itr.next()));
		}

		JFreeChart chart = ChartFactory.createScatterPlot("Wifi Signal Strength per AP", "Time", "Difference in Signal Strength (dB)", dataset, PlotOrientation.VERTICAL, false, false, false);

		ChartUtilities.saveChartAsJPEG(new File("chart.jpg"), chart, 1024, 1024);

	}

	private static void handleGSMMessage(GSMScan gsmScan) {
		// gsmBeacons.add(gsmScan.getTimestamp(), gsmScan.getScanCount());

	}

	private static void handleWifiMessage(WifiScan wifiScan, Queue<WifiScan> q) {
		q.offer(wifiScan);

		/*
		 * List<WifiInfo> list = wifiScan.getScanList();
		 * 
		 * for(WifiInfo i: list) { XYSeries xy = wifis.get(i.getBSSID()); if(xy
		 * == null) xy = new XYSeries(i.getBSSID());
		 * xy.add(wifiScan.getTimestamp(), i.getLevel());
		 * wifis.put(i.getBSSID(), xy); }
		 */
	}
}
