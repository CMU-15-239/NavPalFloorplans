import java.io.*;
import java.util.*;

import edu.cmu.ri.rcommerce.Messages.GSMInfo;
import edu.cmu.ri.rcommerce.Messages.GSMScan;
import edu.cmu.ri.rcommerce.Messages.MessageWrapper;
import edu.cmu.ri.rcommerce.Messages.MessageWrapper.Type;
import edu.cmu.ri.rcommerce.Messages.WifiInfo;
import edu.cmu.ri.rcommerce.Messages.WifiScan;

public class CorrelateRobotAndPhoneLogs {

	public static void main(String[] args) {
		String baseDir = "C:\\Users\\Evan\\Documents\\College Stuff\\RI Summer Stuff\\Tech Report\\NSH 2 Aug9\\Run6\\";
		String phoneLog = baseDir + "triggered 2011-08-09_16.36.25.log";
		String robotLog = baseDir + "robotStatus-cleaned.txt";
		String output 	= baseDir + "wifi and gsm dump.txt";
		InputStream phoneIn;
		Scanner robotInScanner;
		FileWriter out;
		try {
			phoneIn = new FileInputStream(phoneLog);
			robotInScanner = new Scanner(new FileReader(robotLog));
			out = new FileWriter(new File(output));
			GSMScan gsmScan = null;
			WifiScan wifiScan = null;
			int numEntries = 0;
			top:
			while (true) {
				MessageWrapper wrap;

				try {
					wrap = MessageWrapper.parseDelimitedFrom(phoneIn);
				} catch (com.google.protobuf.InvalidProtocolBufferException e) {
					System.out.println("decode error!");
					continue;
				}
				if (wrap == null)
					break;
				Type messageType = wrap.getType();

				switch (messageType) {
				case WifiScan:
					if (wifiScan != null)
						System.err.println("Overwriting Wifi Scan!");
					wifiScan = wrap.getWifiScan();
					break;
				case GSMScan:
					if (gsmScan != null)
						System.err.println("Overwriting GSM Scan");
					gsmScan = wrap.getGsmScan();
				default:
					break;
				}

				//wifi and gsm timestamps are in integer milliseconds, robot timestamps are in float seconds
				if (gsmScan != null && wifiScan != null) {
					//get the next robot entry
					double time,x,y,theta;
					while (true)
					{
						try
						{
							time = robotInScanner.nextDouble();
							x = robotInScanner.nextDouble();
							y = robotInScanner.nextDouble();
							theta = robotInScanner.nextDouble();
							robotInScanner.nextLine();
							
							double timeDiff = wifiScan.getTimestamp()/1000.0 - time;
							System.out.println("timeDiff: " + timeDiff);
							
							if (timeDiff > 1)
							{
								System.out.println("skipping");
								continue;
							}
							
							break;
						}
						catch (NoSuchElementException e)
						{
							System.err.println("missing a robot log entry!");
							break top;
						}
					}
					
					//we have all the information for a full entry
					numEntries++;
					System.out.println("entry: " + numEntries);
					
					//output the correlated entry
					out.write(wifiScan.getTimestamp() + " " + x + " " + y);
					out.write(" WifiScan");
					out.write(" " + wifiScan.getScanCount());
					for (WifiInfo info : wifiScan.getScanList())
						out.write(" " + info.getBSSID() + " " + info.getLevel());
					out.write(" GSMScan");
					out.write(" " + gsmScan.getScanCount());
					for (GSMInfo info : gsmScan.getScanList())
						out.write(" " + info.getCellID() + " " + info.getSignalStrength());
					out.write("\n");
					
					gsmScan = null;
					wifiScan = null;
				}
			}
			phoneIn.close();
			robotInScanner.close();
			out.close();

		}

		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
