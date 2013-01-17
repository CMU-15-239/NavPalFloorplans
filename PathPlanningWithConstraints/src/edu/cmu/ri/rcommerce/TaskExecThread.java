package edu.cmu.ri.rcommerce;

import java.io.IOException;
import java.net.BindException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

/**
 * A thread that sends and recieves task message to taskExec
 *  
 *  * @author Nisarg
 *
 */
public class TaskExecThread extends Thread {

    protected DatagramSocket socket = null;
    public boolean exit = false;

    private Handler guiHandler;
    private Context context;
    
    public TaskExecThread(Context context, Handler guiHandler) throws SocketException {
	this(context,guiHandler,"TaskExecThread");
    }
    
    private InetAddress taskExecAddr;
    private int taskExecPort;

    public TaskExecThread(Context context, Handler guiHandler,String name) throws SocketException {
        super(name);
        this.context = context;
        this.guiHandler = guiHandler;
        try
        {
        	socket = new DatagramSocket(10004);
        }
        catch (BindException e)
        {
        	Toast.makeText(context,"unable to bind taskExec socket", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
	public void run() {
    	byte[] buf = new byte[512];
    	if (socket == null)
    	{
    		Log.d("TaskExec", "unable to run taskexec, could not bind socket");
    		return;
    	}
    	while (!exit)
    	{
            try {
                // receive request
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);
                final String message = new String(packet.getData(),0,packet.getLength());
                Log.d("Task", "got message: " + message);
                
                //Update our info for where to send task data
                taskExecAddr = packet.getAddress();
                taskExecPort = packet.getPort();
                
                if (message.length() == 0)
                	continue;
                
                //parse the message
                String[] tokens = message.split(" ");
                if (tokens[0].equals("Newtask"))
                {
                	int id = -1;
                	try
                	{
                		id = Integer.parseInt(tokens[1]);
                	}
                	catch (NumberFormatException e)
                	{
                		Log.d("TaskExec", "Invalid taskExec message");
                		throw new RuntimeException();
                	}
                	if (tokens[2].equals("goto"))
                	{
                		final float x = Float.parseFloat(tokens[3]);
                		final float y = Float.parseFloat(tokens[4]);
                		final Annotation a = new Annotation();
                		//TODO use meters instead of decimeters as fundamental unit
                		a.locationX = x * 10;
                		a.locationY = y * 10;
                		a.ID = id;
                		a.shortName = "goto";
                		a.longDescription = "Goto " + x + "," + y;
                		a.timestamp = System.currentTimeMillis();
                		a.type = Annotation.GOTO_TASK;
                		guiHandler.post(new Runnable() {
							
							@Override
							public void run() {
								StateServer.getInstance(context).addCurrentTask(a);
								
							}
						});
//                		
//        				guiHandler.post(new Runnable() {
//        					
//        					@Override
//        					public void run() {
//        						
//        						/*
//        						AlertDialog d = new AlertDialog.Builder(context).create();
//        						d.setIcon(R.drawable.red_cross);
//        						//remove the last characters because they're unprintable (line break?)
//        						d.setMessage(message.substring(0,message.length()-2));
//        						d.setTitle("New Task");
//        						d.show();
//        						*/
//        					}});
                	}
                	else
                	{
                		Log.d("TaskExec", "Unable to parse taskExec message");
                		throw new RuntimeException();
                	}
                }
                else if (tokens[0].equals("Abort"))
                {
                	if (tokens[1].equals("all"))
                	{
                		StateServer.getInstance(context).clearCurrentTasks();
                	}
                	else 
                	{
                		try
                		{
                			int taskToAbort = Integer.parseInt(tokens[1]);
                			StateServer.getInstance(context).clearTaskWithID(taskToAbort);
                		}
                		catch (NumberFormatException e)
                    	{
                    		Log.d("TaskExec", "Invalid taskExec message");
                    		throw new RuntimeException();
                    	}
                	}
                }
                
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
    	}
        socket.close();
    }
    
    public void finishTask(int taskID)
    {
    	if (taskExecAddr == null)
    	{
    		Log.d("TaskExec", "Don't know where to send task data (haven't heard from taskExec yet)");
    		return;
    	}
    	
    	if (socket == null)
    	{
    		Log.d("TaskExec", "can't send info because the socket couldn't be bound to port 10004");
    	}
    	
    	String message = "Finished " + taskID + '\000';
    	byte[] bytes = message.getBytes();
    	
    	try {
    		DatagramPacket p = new DatagramPacket(bytes,bytes.length,taskExecAddr,taskExecPort);
			socket.send(p);
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
}