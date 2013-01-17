package edu.cmu.ri.rcommerce;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import android.content.Context;
import android.util.Log;

/**
 * A thread that listens for and responds to boeing client messages.
 * @author Nisarg
 *
 */
public class StateServerThread extends Thread {

    protected ServerSocket serverSocket = null;
    protected Socket clientSocket = null;
    public boolean exit = false;
    public boolean connected = false;

    private Context context;
    private StateServer data;
    
    public StateServerThread(Context context) throws IOException {
	this(context,"StateServerThread");
    }

    public StateServerThread(Context context,String name) throws IOException {
        super(name);
        this.context = context;
        serverSocket = new ServerSocket(10003);
    }

    @Override
	public void run() {
    	byte[] buf = new byte[512];
    	data = StateServer.getInstance(context);
    	while (!exit)
    	{
            try {
            	if (clientSocket == null || !clientSocket.isConnected())
            	{
            		clientSocket = serverSocket.accept();
            		clientSocket.setTcpNoDelay(true);
            		Log.i("stateserver", "connected to client");
            		connected = true;
            	}
                
            	String message;
                if (clientSocket != null && clientSocket.isConnected())
                {
                	clientSocket.getInputStream().read(buf);
                	message = new String(buf,0,buf.length);
                	if (message.length() == 0)
                    	continue;
                	Log.i("stateserver","recieved packet from client:");
                	Log.i("stateserver", message);
             
                    // Provide the sender an update on your current location
                    String response = "LocUpdate " + data.currentLocation.x + " " + data.currentLocation.y + " " + data.currentOrientation + " 0";
                    Log.d("stateserver", "response: " + response + "\n");
                    Log.d("stateserver", "last update time: " + data.lastLocationUpdateTime);
                    byte[] outBuf = new byte[256];
                    response.getBytes(0, response.length()-1, outBuf, 0);
                    clientSocket.getOutputStream().write(outBuf);
                    clientSocket.getOutputStream().flush();
                    Log.d("stateserver", "sent response");
                }          
            } catch (IOException e) {
            	Log.d("stateserver", e.toString());
                e.printStackTrace();
            }
    	}
    	try
    	{
    		serverSocket.close();
            clientSocket.close();
            connected = false;
        }
    	catch(IOException e)
    	{
    		Log.d("stateserver", e.toString());
    		e.printStackTrace();
    	}
    }
}
