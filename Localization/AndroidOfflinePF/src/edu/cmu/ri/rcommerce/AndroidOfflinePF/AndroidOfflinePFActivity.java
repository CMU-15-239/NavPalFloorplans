package edu.cmu.ri.rcommerce.AndroidOfflinePF;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import edu.cmu.ri.rcommerce.FastRandom;
import edu.cmu.ri.rcommerce.ObstacleMap;
import edu.cmu.ri.rcommerce.particleFilter.ImportanceResampler;
import edu.cmu.ri.rcommerce.particleFilter.Measurer;
import edu.cmu.ri.rcommerce.particleFilter.Particle2D;
import edu.cmu.ri.rcommerce.particleFilter.ParticleFilter;
import edu.cmu.ri.rcommerce.particleFilter.Resampler;
import edu.cmu.ri.rcommerce.particleFilter.Updater;
import edu.cmu.ri.rcommerce.sensor.LogPlaybackDeadReckoningGaussianUpdater;
import edu.cmu.ri.rcommerce.sensor.LogPlaybackTimeSource;
import edu.cmu.ri.rcommerce.sensor.LogPlaybackWifiRSSIRuntimeProvider;
import edu.cmu.ri.rcommerce.sensor.MapConstrainedRSSIMeasurer2D;
import edu.cmu.ri.rcommerce.sensor.RSSICalibrateProvider;
import edu.cmu.ri.rcommerce.sensor.RSSIRuntimeProvider;
import edu.cmu.ri.rcommerce.sensor.WifiSignalMapRSSICalibrateProviderKNN;

public class AndroidOfflinePFActivity extends Activity {
enum LocMethod{Wifi,Gsm};
	
	static boolean dumpImages = true;
	final static LocMethod method = LocMethod.Wifi; 
	static LogPlaybackTimeSource timeSource;
	String testLog;
	String outData = "";
	boolean flag = true;
	BufferedWriter buf = null;
	ParticleFilter<Particle2D> filter;
	TextView disp;
	Button stopPF;
	Button startPF;
	String correlatedData;
	String obstacleMap;
	File logDir;
	AsyncTask<Void, String, Void> task;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        disp = (TextView)findViewById(R.id.disp);
        disp.setText("Starting setup for processing...");
        stopPF = (Button)findViewById(R.id.stopPF);
        stopPF.setOnClickListener(new OnClickListener() {
          @Override
          public void onClick(View v) {
            flag = false;
            disp.setText("Stopping...");
            task.cancel(true);
            finish();
          }
        });
        
        startPF = (Button)findViewById(R.id.startPF);
        startPF.setOnClickListener(new OnClickListener() {
          @Override
          public void onClick(View v) {
            disp.setText("Starting PF...");
            task = new runPFTask().execute();
          }
        });
        File externalRoot = Environment.getExternalStorageDirectory();
		logDir = new File(externalRoot, "logs/");
		
		correlatedData = logDir.getAbsolutePath() + "/" + "wifi and gsm dump.txt";
		testLog = logDir.getAbsolutePath() + "/";
		 
		 	testLog += "victor 3.log"; 
		
		
		obstacleMap = logDir.getAbsolutePath() + "/" + "map.map";
		
		
		System.out.println("Setup Done!");
    }
    private class runPFTask extends AsyncTask<Void,String,Void>{

		@Override
		protected void onProgressUpdate(String... values) {
			disp.setText("\n" + values[0]);
			super.onProgressUpdate(values);
		}

		@Override
		protected Void doInBackground(Void... params) {
			runPF();
			return null;
		}
		
		 protected void runPF() {
				publishProgress("Loading Obstacle Map...");
		    	ObstacleMap map = null;
				try {
					map = ObstacleMap.loadFromStream(new FileReader(obstacleMap));
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				filter = null;
				try {
					filter = setupFilter(correlatedData, testLog,map);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				  
				 	outData += "pfout.pf"; 
				
				File outFile = new File(logDir, outData);
				if(!outFile.exists())
					try {
						outFile.createNewFile();
					} catch (IOException e3) {
						// TODO Auto-generated catch block
						e3.printStackTrace();
					}
				try {
					buf = new BufferedWriter(new FileWriter(outFile));
				} catch (IOException e2) {
					e2.printStackTrace();
				}
		    	while(flag)
				{
					try {
						buf.write("NS:\n");
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					timeSource.incrementTime(500);
					System.out.println("time: " + (timeSource.getCurrentTime() - timeSource.getStartingTime()) / 1e9);
					filter.iterate();
					List<Particle2D> state = filter.getCurrentState();
					for(Particle2D p: state) {
						try {
							buf.write("" + p.x + " " + p.y + "\n");
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					try{
						Thread.sleep(0);}catch(InterruptedException e){}
					Particle2D sampleParticle = filter.getCurrentState().get(0);
					publishProgress("sample location: " + sampleParticle.x + "," + sampleParticle.y);
				}		
			}
    	
    }
   
    
    @Override
	protected void onPause() {
		try {
			flag = false;
			buf.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		super.onPause();
	}

	static ParticleFilter<Particle2D> setupFilter(String correlatedData,String testLog,ObstacleMap map) throws IOException
	{
		FastRandom random = new FastRandom();
		timeSource = new LogPlaybackTimeSource(testLog);
		
		RSSICalibrateProvider<Particle2D> signalMap = new WifiSignalMapRSSICalibrateProviderKNN(
				new BufferedReader(new FileReader(correlatedData)),3);
		
		RSSIRuntimeProvider	rp = new LogPlaybackWifiRSSIRuntimeProvider(testLog,timeSource);
		
		java.util.List<Particle2D> startingState = new ArrayList<Particle2D>();
		for (int i=0; i<1000; i++)
			startingState.add(new Particle2D(random.nextFloat()*100-60, random.nextFloat()*100-60));
		
		Updater<Particle2D> updater = new LogPlaybackDeadReckoningGaussianUpdater(testLog, timeSource, random, 2.6f, 5f, .2f);
		Measurer<Particle2D> measurer = new MapConstrainedRSSIMeasurer2D<Particle2D>(rp, signalMap,0.5f, map);
		Resampler<Particle2D> resampler = new ImportanceResampler<Particle2D>();
		ParticleFilter<Particle2D> filter = new ParticleFilter<Particle2D>(startingState, updater, measurer, resampler);
		return filter;
	}
}