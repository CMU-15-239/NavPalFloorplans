package edu.cmu.recognizer;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import edu.cmu.MainInterface;
import edu.cmu.R;

public class myProperties {
	Properties properties;
	
	myProperties(MainInterface mainInterface){
		Resources resources = mainInterface.getResources();
		try {
			InputStream rawResource = resources.openRawResource(R.raw.roomnames);
			properties.load(rawResource);
		} catch (NotFoundException e) {
			System.err.println("Did not find raw resource: "+e);
		} catch (IOException e) {
			System.err.println("Failed to open microlog property file");
		}
	}
	
	public String get(String pathName){
		return (String) properties.get(pathName);
	}

}
