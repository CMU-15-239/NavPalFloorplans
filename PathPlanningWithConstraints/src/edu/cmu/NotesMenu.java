/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.cmu;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * The annotations activity. When called, the user is prompted for a name, type and description of the annotation. A more updated version of this file should be found in the PreNav branch. Type is not included in this because this branch
 * was created to work on the recursive path planner and the other is for commits to the trunk. Type is only used by the path predictor at this point.
 * 
 * @author Chet
 */
public class NotesMenu extends Activity
{

    Button accept, cancel, clear;
    EditText shortDesc, longDesc;
    String s[] = new String[3];

    protected void onCreate(Bundle icicle)
    {
	super.onCreate(icicle);
	setContentView(R.layout.notes);
	this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

	accept = (Button) findViewById(R.id.acceptButton);
	cancel = (Button) findViewById(R.id.cancelButton);
	clear = (Button) findViewById(R.id.clearButton);

	shortDesc = (EditText) findViewById(R.id.shortDes);
	longDesc = (EditText) findViewById(R.id.longDes);
	accept.setOnClickListener(acceptHandler);
	cancel.setOnClickListener(cancelHandler);
	clear.setOnClickListener(clearHandler);

	shortDesc.setMaxEms(25);
	shortDesc.setMaxLines(1);
	longDesc.setMaxLines(4);

	longDesc.setGravity(48);
    }

    @Override
    protected void onResume()
    {
	super.onResume();
	// setContentView(R.layout.splash);
    }

    @Override
    protected void onPause()
    {
	super.onPause();
	// setContentView(R.layout.splash);
    }

    @Override
    protected void onDestroy()
    {
	super.onDestroy();
	// setContentView(R.layout.splash);
    }

    @Override
    protected void onStop()
    {
	super.onStop();
	// setContentView(R.layout.splash);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
    }

    View.OnClickListener acceptHandler = new View.OnClickListener()
    {

	public void onClick(View v)
	{
	    if (shortDesc.getText().toString().length() < 1)
	    {

	    }
	    else
	    {
		s[0] = shortDesc.getText().toString();
		s[1] = longDesc.getText().toString();
		s[2] = "Accept";
		Bundle bundle = new Bundle();
		bundle.putStringArray("NoteData", s);
		Intent mIntent = new Intent();
		mIntent.putExtras(bundle);
		setResult(1, mIntent);
		bundle.putBoolean("Cancelled", false);

		finish();
	    }
	}
    };

    View.OnClickListener cancelHandler = new View.OnClickListener()
    {

	public void onClick(View v)
	{
	    s[0] = shortDesc.getText().toString();
	    s[1] = longDesc.getText().toString();
	    s[2] = "Cancel";

	    Bundle bundle = new Bundle();
	    bundle.putStringArray("NoteData", s);
	    Intent mIntent = new Intent();
	    mIntent.putExtras(bundle);
	    setResult(1, mIntent);
	    finish();
	    // it was the 2nd button
	    finish();
	}
    };

    View.OnClickListener clearHandler = new View.OnClickListener()
    {

	public void onClick(View v)
	{
	    // it was the 2nd button
	    shortDesc.setText("");
	    longDesc.setText("");
	}
    };
}
