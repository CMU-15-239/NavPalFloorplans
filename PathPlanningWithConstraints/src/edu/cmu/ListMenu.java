/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.cmu;

import android.app.ListActivity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 
 * @author Chet
 */
public class ListMenu extends ListActivity
{
    Button go, cancel;
    ListView LView;
    String s[] = new String[3];

    /** This should be pretty adaptable for other functions. It */
    protected void onCreate(Bundle icicle)
    {
	super.onCreate(icicle);
	// setContentView(R.layout.landmarklist);
	this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	Intent sender = getIntent();
	String[] landmarks = sender.getExtras().getStringArray("Landmarks");
	if (landmarks != null)
	{// selecting a landmark for a destination
	    this.setTitle("Select a Landmark");
	    setListAdapter(new ArrayAdapter<String>(this, R.layout.landmarklist, landmarks));

	    LView = getListView();
	    LView.setTextFilterEnabled(true);

	    LView.setOnItemClickListener(new OnItemClickListener()
	    {

		public void onItemClick(AdapterView<?> parent, View view, int position, long id)
		{
		    // When clicked, show a toast with the TextView text
		    ;
		    Toast.makeText(getApplicationContext(), "Going to " + ((TextView) view).getText() + ".", Toast.LENGTH_SHORT).show();
		    Bundle bundle = new Bundle();
		    bundle.putInt("Index", position);
		    Intent mIntent = new Intent();
		    mIntent.putExtras(bundle);
		    setResult(2, mIntent);
		    finish();

		}
	    });

	}
	else
	{// selecting a map to lad
	    this.setTitle("Select a Map");

	    String[] maps = new String[MapList.MAPS.length];
	    for (int i = 0; i < MapList.MAPS.length; i++)
	    {
		// if(MapList.MAPS[i][5].equals("C"))//only complete maps are available
		maps[i] = MapList.MAPS[i][0];

	    }
	    setListAdapter(new ArrayAdapter<String>(this, R.layout.landmarklist, maps));

	    LView = getListView();
	    LView.setTextFilterEnabled(true);

	    LView.setOnItemClickListener(new OnItemClickListener()
	    {
		public void onItemClick(AdapterView<?> parent, View view, int position, long id)
		{
		    // When clicked, show a toast with the TextView text
		    Bundle bundle = new Bundle();
		    bundle.putInt("MapIndex", position);
		    Intent mIntent = new Intent();
		    mIntent.putExtras(bundle);
		    setResult(3, mIntent);
		    finish();
		}
	    });
	}

    }

    @Override
    protected void onResume()
    {
	super.onResume();

    }

    @Override
    protected void onPause()
    {
	super.onPause();

    }

    @Override
    protected void onDestroy()
    {
	super.onDestroy();

    }

    @Override
    protected void onStop()
    {
	super.onStop();

    }
}
