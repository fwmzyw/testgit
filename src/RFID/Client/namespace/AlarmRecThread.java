package RFID.Client.namespace;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import RFID.Client.Comfunc.namespace.Predefine;
import RFID.Client.Comfunc.namespace.ThreadResult;
import RFID.Client.Comfunc.namespace.Utility;
import RFID.Client.Data.namespace.DetailInfoRFIDStruct;


public class AlarmRecThread implements Runnable {

	
	DatagramSocket receiveSocket;
	Handler handler;
	 ArrayList<HashMap<String,String>> list;
	public AlarmRecThread(DatagramSocket receiveSocket,  ArrayList<HashMap<String,String>> list,Handler handler)
	{
		this.receiveSocket = receiveSocket;
		this.handler = handler;
		this.list = list;
	}
	public void run() {

		/** init packet*/
		 DatagramPacket packet; 
	    byte[] data = new byte[Predefine.MaxRFIDList]; 
	    packet = new DatagramPacket(data, data.length);       
	    while(!ThreadResult.bStopRec){
		    try {
		    	receiveSocket.setSoTimeout(Predefine.WaitTimeout);
				receiveSocket.receive(packet);
				
				byte[] alarmdata = packet.getData();	
				
				/** 解析数据包，加入到list,发message*/
				Utility uti=new Utility();
				DetailInfoRFIDStruct detail=new DetailInfoRFIDStruct();
				HashMap<String,String> map1 = new HashMap<String,String>(); 
				detail=uti.BytesToRFIDStruct(alarmdata);//字节流转换成结构体
				map1.put("ItemName", uti.LongByteToString(detail.getItemName()));
				map1.put("PersonName", uti.LongByteToString(detail.getPersonName()));
				map1.put("Organization", uti.LongByteToString(detail.getOrganization()));
				map1.put("AlarmTime", uti.LongByteToString(detail.getCaptrueTime()));
				map1.put("Position", uti.LongByteToString(detail.getPosition()));
				map1.put("IsAllowed", uti.LongByteToString(detail.getIsItemAllowed()));
				
				Message msgAlarm = handler.obtainMessage(Predefine.MsgRefreshlist, map1);
				handler.sendMessage(msgAlarm);
				} 
		    catch (IOException e){
				e.printStackTrace();

				} 
    
	    }
	    receiveSocket.close();
	}

}
