package edu.cmu;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

/**
 * This shows a lovely splash screen. It is currently disabled, but all loading can certainly take place while it is being displayed.
 */
public class MainActivity extends Activity
{
    // AutoCompleteTextView destText;

    /**
     * \brief User override of the onResume() method in the Activity class
     * 
     * The onResume() method is invoked when the application regains focus. There are a few issues here that I am not sure what the reasoning of the developer was:
     * 
     * \li Why is the finish() method being called from within the onResume() method? When onResume() is called, the will prepare the app for closing. \li Why is garbage collection (GC) being forced? GC should be handled by the OS since it
     * may cause performance issues if invoked by the user. There are times when the user should invoke GC, but I feel this is not one of them. \li Why call the onDestroy() method? This does not make sense.
     * 
     * If anyone else is able to understand why the previous programmer added this logic, please shed some light on this matter. Could it be to automatically start the app if it is exited prematurely?
     * 
     */
    @Override
    public void onResume()
    {
	System.gc();
	finish();
	System.gc();
	super.onDestroy();
    }

    /**
     * \brief This method is called when the app begins for the very first time.
     * 
     * This method is responsible for the following actions:
     * 
     * \li Displays the splash screen for the Path Planner \li Forces the screne orientation to be portrait so the screen does not rotate while the blind person is using the app. \li The Path Planner is added to the main launcher list if
     * the home button or center keyboard button is pressed. \li The activity for the path planner is started
     * 
     */
    @Override
    public void onCreate(Bundle icicle)
    {
	super.onCreate(icicle);
	setContentView(R.layout.splash);
	this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

	startActivity(new Intent("edu.cmu.CLEARSCREEN"));

	new MainInterface();
    }
}