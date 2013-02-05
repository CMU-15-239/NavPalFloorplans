package edu.cmu.ri.rcommerce.sensor;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

import edu.cmu.ri.rcommerce.filter.AccelerometerListener;
import edu.cmu.ri.rcommerce.filter.MagnetometerListener;

/* currently reads the entire log into memory before playing it back
 */
public class RawSensorLogPlayback {
	
	private AccelerometerListener accelListener;
	private MagnetometerListener magnetometerListener;
	
	private BufferedReader accelFile, compassFile, magnetometerFile;
	
	public RawSensorLogPlayback(BufferedReader accelFile,AccelerometerListener accelListener,
			BufferedReader magnetometerFile, MagnetometerListener magnetometerListener)
	{
		this.accelFile = accelFile;
		this.accelListener = accelListener;
		
		this.magnetometerFile = magnetometerFile;
		this.magnetometerListener = magnetometerListener;
	}
	
	public void startPlayback()
	{
		try
		{
			List<SensorReading> accelList = parse3ValueLogFile(accelFile, SensorReading.ACCELEROMETER_SENSOR);
			List<SensorReading> orientationList = parse3ValueLogFile(compassFile, SensorReading.ORIENTATION_SENSOR);
			List<SensorReading> magnetometerList = parse3ValueLogFile(magnetometerFile, SensorReading.MAGNETOMETER_SENSOR);
			
			List<SensorReading> combinedList = new ArrayList<SensorReading>();
			combinedList.addAll(accelList);
			combinedList.addAll(orientationList);
			combinedList.addAll(magnetometerList);
			
			//sort by timestamp
			Collections.sort(combinedList, new Comparator<SensorReading>() 
					{
						@Override
						public int compare(SensorReading reading1, SensorReading reading2) {
							return (int)(reading1.timestamp - reading2.timestamp);
						}
					});
			
			long lastMessageTime = 0;
			
			SensorReading[] accelBuffer = new SensorReading[1],
							magnetometerBuffer = new SensorReading[1],
							compassBuffer = new SensorReading[1];
			
			for (SensorReading r : combinedList)
			{
				long timeWaitNano;
				if (lastMessageTime != 0)
					timeWaitNano = r.timestamp - lastMessageTime;
				else
					timeWaitNano = 0;
				
				lastMessageTime = r.timestamp;
				
				try{Thread.sleep(timeWaitNano/1000000, (int)timeWaitNano%1000000);}
				catch (InterruptedException e) {}
				
				switch (r.sensorType)
				{
				case SensorReading.ACCELEROMETER_SENSOR:
					accelBuffer[0] = r;
					accelListener.addAccelerometerReadings(accelBuffer);
					break;
				case SensorReading.MAGNETOMETER_SENSOR:
					magnetometerBuffer[0] = r;
					magnetometerListener.addMagnetometerReadings(magnetometerBuffer);
					break;
				}
			}
			
			
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public List<SensorReading> parse3ValueLogFile(BufferedReader in, int SensorType) throws IOException
	{
		List<SensorReading> readingsList = new ArrayList<SensorReading>();
		while (true)
		{
			String str = in.readLine();
			if (str == null)
				break;
			
			//wish I had scanf
			Scanner scan = new Scanner(str);
			scan.useDelimiter(" ");
			float x = scan.nextFloat();
			float y = scan.nextFloat();
			float z = scan.nextFloat();
			long t = scan.nextLong();
			
			SensorReading reading = new SensorReading(t, new float[]{x,y,t},SensorType);
			readingsList.add(reading);
		}
		return readingsList;
	}

	
	
}
