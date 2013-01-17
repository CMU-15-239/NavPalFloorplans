package edu.cmu.ri.rcommerce.sensor;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import edu.cmu.ri.rcommerce.Messages;
import edu.cmu.ri.rcommerce.Messages.MessageWrapper;
import edu.cmu.ri.rcommerce.Messages.WifiScan;

/** Simulate real-time localization by playing back a pre-recorded sequence of wifi signal strength readings */
public class LogPlaybackWifiRSSIRuntimeProvider implements RSSIRuntimeProvider {
	int currentPlaybackPosition = 0;
	List<WifiScan> scans;
	LogPlaybackTimeSource timeSource;
	List<Boolean> isTagged;
	public long latestTime;
	
	public LogPlaybackWifiRSSIRuntimeProvider(String log,LogPlaybackTimeSource timeSource) throws IOException {
		InputStream in = new FileInputStream(log);
		this.timeSource = timeSource;
		
		MessageWrapper wrap;
		scans = new ArrayList<Messages.WifiScan>();
		isTagged = new ArrayList<Boolean>();
		isTagged.add(false);
		isTagged.add(false);
		isTagged.add(false);
		isTagged.add(false);
		isTagged.add(false);
		isTagged.add(false);
		isTagged.add(false);
		
		while(true)
		{
			try {
				wrap = MessageWrapper.parseDelimitedFrom(in);
			} catch (com.google.protobuf.InvalidProtocolBufferException e) {
				System.out.println("decode error!");
				break;
			}
			if (wrap == null)
				break;
			if (wrap.hasWifiScan())
			{
				WifiScan scan = wrap.getWifiScan();
				latestTime = wrap.getWifiScan().getTimestamp();
				scans.add(scan);
				isTagged.add(false);
			}
		if(wrap.hasTagInfo())
			isTagged.set(scans.size(), true);
		}
	}
	@Override
	public boolean newReadingAvailable() {
		if (currentPlaybackPosition >= scans.size())
			return false;
		//Wifi timestamps are kept in unix time in milliseconds, whereas playback timestamps are in arbitrary time in nanoseconds.
		//Convert based on elapsed times
		//TODO look at doing an overhaul of timestamps for consistency
		long myTimestamp =(scans.get(currentPlaybackPosition).getTimestamp() - scans.get(0).getTimestamp())*1000*1000;
		long playbackTimestamp = timeSource.getCurrentTime() - timeSource.getStartingTime();
		System.out.println("myT: " + myTimestamp + " theirT: " + playbackTimestamp);
		return myTimestamp <= playbackTimestamp; 
	}

	@Override
	public RSSIReading getCurrentReading() {
		System.out.println("Provided reading " + currentPlaybackPosition);
		WifiScan currentScan = scans.get(currentPlaybackPosition++);
		return RSSIReading.fromWifiScan(currentScan);
	}
	
	public boolean isTagged() {		
			return isTagged.get(currentPlaybackPosition);
	}

}
