import java.io.*;

import edu.cmu.ri.rcommerce.Messages.MessageWrapper;
import edu.cmu.ri.rcommerce.Messages.MessageWrapper.Type;
import edu.cmu.ri.rcommerce.Messages.WifiInfo;
import edu.cmu.ri.rcommerce.Messages.WifiScan;

/* outputs data formatted the same way as DumpMapToText for ease of decoding */
public class DumpWifiLog {

	public static void main(String[] args) throws IOException {
		final String OUT_FILE_NAME = "wifi-dump.txt";
		InputStream in = new FileInputStream(args[0]);
		FileWriter out = new FileWriter(OUT_FILE_NAME);

		int numEntries = 0;
		long numDecodeErrors = 0;

		out.write("            \n"); //Leave space for the entry count, which will be inserted later
		while (true)
		{
			MessageWrapper wrap;
			try
			{
				wrap = MessageWrapper.parseDelimitedFrom(in);
			}
			catch ( com.google.protobuf.InvalidProtocolBufferException e)
			{
				numDecodeErrors++;
				System.out.println("decode error!");
				continue;
			}
			if (wrap == null)
				break;
			Type messageType = wrap.getType();
			
			switch (messageType) {
			case WifiScan:
				numEntries++;
				WifiScan scan = wrap.getWifiScan();
				out.write(scan.getTimestamp() + " " + 0 + " " + 0);
				out.write(" WifiScan");
				out.write(" " + scan.getScanCount());
				for (WifiInfo info : scan.getScanList())
						out.write(" " + info.getBSSID() + " " + info.getLevel());
				out.write(" GSMScan 0\n");
				break;
			default:
				break;
			}
		}
		
		out.close();

		//reopen to write the number of entries into the beginning
		RandomAccessFile file = new RandomAccessFile(OUT_FILE_NAME, "rw");
		file.seek(0);
		file.write(Integer.toString(numEntries).getBytes());
		file.close();

		System.out.println("Number of Decode Errors: " + numDecodeErrors);
		System.out.println("Number of Entries Written to File: " + numEntries);
	}	
}
