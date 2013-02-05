package edu.cmu.ri.rcommerce;

import java.io.IOException;
import java.net.BindException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

/**
 * A thread that sends task message to taskAllocator
 *  
 *  * @author Balajee
 *
 */
public class AnnotationThread extends Thread {

    protected DatagramSocket socket = null;
    public boolean exit = false;

    public AnnotationThread(Context context) throws SocketException {
	//this(context,"AnnotationThread");
    }


    public AnnotationThread(Context context, String name) throws SocketException {
        super(name);
        try
        {
        	socket = new DatagramSocket(10005);
        }
        catch (BindException e)
        {
        	Toast.makeText(context,"unable to bind taskAllocator socket", Toast.LENGTH_SHORT).show();
        }
    }
    
    public void addTask(float X, float Y)
    {	
    	if (socket == null)
    	{
    		Log.d("taskAllocator", "can't send info because the socket couldn't be bound to port 10005");
    	}
    	
    	String message = "#Goto#" + X + '#'+ Y + '\000';
    	byte[] bytes = message.getBytes();
    	
    	try {
    		DatagramPacket p = new DatagramPacket(bytes,bytes.length);
			socket.send(p);
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
}