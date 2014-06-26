package RFID.Client.namespace;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import android.content.Context;
import RFID.Client.Comfunc.namespace.Predefine;
import RFID.Client.Comfunc.namespace.ThreadResult;

/** receive login reply from the server*/
public class ReceiveThread implements Runnable {

	
	DatagramSocket receiveSocket;
	Context context;
	public ReceiveThread(Context context,DatagramSocket receiveSocket)
	{
		this.receiveSocket = receiveSocket;
		this.context = context;
	}
	public void run() {
		
		ThreadResult.Received = false;
		/** init packet*/
		 DatagramPacket packet; 
	    byte[] data = new byte[Predefine.MaxRecLogSize]; 
	    packet = new DatagramPacket(data, data.length);       
	    
	    try {
	    	receiveSocket.setSoTimeout(Predefine.WaitTimeout);
			receiveSocket.receive(packet);
			ThreadResult.Received = true;
			ThreadResult.logdata = packet.getData();	
			ThreadResult.logdatalength = packet.getLength();
			} 
	    catch (IOException e){
			e.printStackTrace();
			} 

	}

}
