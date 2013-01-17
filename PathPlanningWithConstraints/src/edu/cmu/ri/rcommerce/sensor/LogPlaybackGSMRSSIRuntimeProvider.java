package edu.cmu.ri.rcommerce.sensor;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import edu.cmu.ri.rcommerce.Messages;
import edu.cmu.ri.rcommerce.Messages.GSMScan;
import edu.cmu.ri.rcommerce.Messages.MessageWrapper;

/** Simulate real-time localization by playing back a pre-recorded sequence of GSM signal strength readings */ 
public class LogPlaybackGSMRSSIRuntimeProvider implements RSSIRuntimeProvider {
	int currentPlaybackPosition = 0;
	List<GSMScan> scans;
	
	public LogPlaybackGSMRSSIRuntimeProvider(String log) throws IOException {
		InputStream in = new FileInputStream(log);
		
		MessageWrapper wrap;
		scans = new ArrayList<Messages.GSMScan>();
		
		while(true)
		{
			wrap = MessageWrapper.parseDelimitedFrom(in);
			if (wrap == null)
				break;
			if (wrap.hasGsmScan())
			{
				GSMScan scan = wrap.getGsmScan();
				scans.add(scan);
			}
		}
	}
	@Override
	public boolean newReadingAvailable() {
		return currentPlaybackPosition < scans.size();
	}

	@Override
	public RSSIReading getCurrentReading() {
		System.out.println("Provided reading " + currentPlaybackPosition);
		GSMScan currentScan = scans.get(currentPlaybackPosition++);
		if (!newReadingAvailable())
			System.out.println("LOG FINISHED");
		return RSSIReading.fromGsmScan(currentScan);
	}

}
