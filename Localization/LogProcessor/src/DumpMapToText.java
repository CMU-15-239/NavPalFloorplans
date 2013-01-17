import java.io.*;
import java.util.List;

import edu.cmu.ri.rcommerce.*;
import edu.cmu.ri.rcommerce.Messages.GSMInfo;
import edu.cmu.ri.rcommerce.Messages.GSMScan;
import edu.cmu.ri.rcommerce.Messages.MessageWrapper;
import edu.cmu.ri.rcommerce.Messages.WifiInfo;
import edu.cmu.ri.rcommerce.Messages.WifiScan;


public class DumpMapToText {

	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		File arg = new File("D:\\workspace\\rCommerce\\stuff\\maps\\2nd-floor");
		List<Annotation> annotations;
		FileWriter out = null;
		
		try
		{
			out = new FileWriter(new File(arg.getParentFile(),arg.getName() + ".dump"));
			ObjectInputStream in = new ObjectInputStream(new FileInputStream(arg));
			annotations = (List<Annotation>) in.readObject();
			in.close();
			
			out.write(annotations.size() + "\n");
			for (Annotation a: annotations)
			{
				if (a.type == Annotation.CALIBRATION_DATA)
				{
					InputStream messages = new ByteArrayInputStream(a.binaryData);
					List<WifiInfo> wifiScanList = null;
					List<GSMInfo> gsmScanList  = null;
					while (true)
					{
						MessageWrapper message = MessageWrapper.parseDelimitedFrom(messages);
						if (message == null)
							break;
						switch (message.getType())
						{
						case WifiScan:
							WifiScan wifi = message.getWifiScan();
							wifiScanList = wifi.getScanList();
							break;
						case GSMScan:
							GSMScan gsm = message.getGsmScan();
							gsmScanList = gsm.getScanList();
							break;
						}
					}
					out.write(a.timestamp + " " + a.locationX + " " + a.locationY);
					if (wifiScanList != null)
					{
						out.write(" WifiScan");
						out.write(" " + wifiScanList.size());
						for (WifiInfo info : wifiScanList)
							out.write(" " + info.getBSSID() + " " + info.getLevel());
					}
					if (gsmScanList != null)
					{
						out.write(" GSMScan");
						out.write(" " + gsmScanList.size());
						for (GSMInfo info : gsmScanList)
							out.write(" " + info.getCellID() + " " + info.getSignalStrength());
					}
					out.write("\n");
				}
			}
			
			out.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

}
