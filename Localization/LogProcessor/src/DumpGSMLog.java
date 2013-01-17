import java.io.*;

import edu.cmu.ri.rcommerce.Messages.MessageWrapper;
import edu.cmu.ri.rcommerce.Messages.MessageWrapper.Type;
import edu.cmu.ri.rcommerce.Messages.GSMInfo;
import edu.cmu.ri.rcommerce.Messages.GSMScan;

/* outputs data formatted the same way as DumpMapToText for ease of decoding */
public class DumpGSMLog {

	public static void main(String[] args) throws IOException {
		final String OUT_FILE_NAME = "gsm-dump.txt";
		InputStream in = new FileInputStream(args[0]);
		FileWriter out = new FileWriter(OUT_FILE_NAME);

		int numEntries = 0;
		out.write("            \n"); // Leave space for the entry count, which
		// will be inserted later
		while (true) {
			MessageWrapper wrap;
			try {
				wrap = MessageWrapper.parseDelimitedFrom(in);
			} catch (com.google.protobuf.InvalidProtocolBufferException e) {
				System.out.println("decode error!");
				continue;
			}
			if (wrap == null)
				break;
			Type messageType = wrap.getType();

			switch (messageType) {
			case GSMScan:
				numEntries++;
				GSMScan scan = wrap.getGsmScan();
				out.write(scan.getTimestamp() + " " + 0 + " " + 0);
				out.write(" GSMScan");
				out.write(" " + scan.getScanCount());
				for (GSMInfo info : scan.getScanList())
					out.write(" BaseStationID:" + info.getBaseStationId() + " CellID:" + info.getCellID() + " CountryCode:" + info.getCountryCode() + " LAC:" + info.getLocationAreaCode() + " NetCode:" + info.getNetworkCode() + " SigStr:" + info.getSignalStrength() + " Status:" + info.getStatus() + " Type:" + info.getType());
				out.write("\n");
				break;
			default:
				break;
			}
		}

		out.close();

		// reopen to write the number of entries into the beginning
		RandomAccessFile file = new RandomAccessFile(OUT_FILE_NAME, "rw");
		file.seek(0);
		file.write(Integer.toString(numEntries).getBytes());
		file.close();

	}
}
