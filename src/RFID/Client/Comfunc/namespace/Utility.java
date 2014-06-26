package RFID.Client.Comfunc.namespace;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;


import RFID.Client.Data.namespace.DetailInfoRFIDStruct;
import RFID.Client.Data.namespace.RegPacketStruct;
import android.util.Log;

public class Utility {
	public static List<String> getLocalIpAddress() {
		final String TAG = "Utility";
		ArrayList<String> iplist = new ArrayList<String>();
		try{
			for(Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();)
			{
				NetworkInterface intf = (NetworkInterface)en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = (InetAddress)enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()) {
						iplist.add(inetAddress.getHostAddress().toString());
					}
				}
			}
		}
		catch(Exception ex){
			Log.e(TAG, ex.toString());
		}
		return iplist;
	}

	public static String getCurrentTime()
	{
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");      
		Date curDate = new Date(System.currentTimeMillis());//获取当前时间      
		String str = formatter.format(curDate);  
		return str;
	}
	//将String转换为定长的字节流
	 public byte[] StringToBytes(String OriStr, int DestBytesLength)  //把String填充成定长的bytes
     {
         byte[] shortBytes = OriStr.getBytes();
         byte[] longBytes = new byte[DestBytesLength];

         for (int i = 0; i < shortBytes.length; i++)
         {
             longBytes[i] = shortBytes[i];
         }
         for (int i = shortBytes.length; i < DestBytesLength; i++)
         {
             longBytes[i] = 0x00;
         }

         return longBytes;
     }
	/*以下两个函数用于将int转换为byte数组，为了和c#的服务器端一致*/ 
	 public static byte[] intToByteArray1(int i) {   
		  byte[] result = new byte[4]; 
		  result[0] = (byte)(i & 0xFF);
		  result[1] = (byte)((i >> 24) & 0xFF);
		  result[2] = (byte)((i >> 16) & 0xFF);
		  result[3] = (byte)((i >> 8) & 0xFF); 
		 // result[3] = (byte)(i & 0xFF);
		  return result;
		 }
		 
		 public static byte[] intToByteArray2(int i) throws Exception {
		  ByteArrayOutputStream buf = new ByteArrayOutputStream();   
		  DataOutputStream out = new DataOutputStream(buf);   
		  out.writeInt(i);   
		  byte[] b = buf.toByteArray();
		  out.close();
		  buf.close();
		  return b;
		 }
	 
	//将登录结构体转换为字节流
 	 public byte[] RegStructToBytes(RegPacketStruct TransStruct)  // 
     {
             /*用于将boolean型转换为长度为4的字节数组*/
     		 ByteArrayOutputStream buf = new ByteArrayOutputStream(); 
     		 DataOutputStream out = new DataOutputStream(buf);  
     		 try {
				out.writeBoolean(TransStruct.getIsLegalPacket());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
     		 byte[] islegal = new byte[4];
     		 byte[] q=buf.toByteArray(); 
     		 System.arraycopy(q, 0, islegal, 0, q.length);
     		 for(int s=1;s<islegal.length;s++){
     			islegal[s]=0x00;		
     		 }
     		 try {
				buf.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
     		 try {
				out.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
     		 /*将int型转化为长度为4 的字节数组*/
     		 byte[] logmode=intToByteArray1(TransStruct.getLogMode());
     		 byte[] opvalue=intToByteArray1(TransStruct.getOperValue());
     		 /*获取字节数组的长度*/
     		 int len=islegal.length+logmode.length+opvalue.length+TransStruct.getPosition().length+
     				TransStruct.getDestIP().length+TransStruct.getSourceIP().length+
     				TransStruct.getUserName().length+TransStruct.getPassword().length;
     		 byte[] bytes=new byte[len];//初始化存放对象的字节流
     		 /*将对象的各个项的字节流都放进去到对象的字节流*/
             System.arraycopy(islegal, 0, bytes, 0, islegal.length);
             System.arraycopy(logmode, 0, bytes, islegal.length, logmode.length);
             System.arraycopy(opvalue, 0, bytes, islegal.length+logmode.length, opvalue.length);
             System.arraycopy(TransStruct.getPosition(), 0, bytes,islegal.length+logmode.length+opvalue.length, TransStruct.getPosition().length);
             System.arraycopy(TransStruct.getSourceIP(), 0, bytes,islegal.length+logmode.length+opvalue.length+TransStruct.getPosition().length, TransStruct.getDestIP().length);
             System.arraycopy(TransStruct.getDestIP(), 0, bytes,islegal.length+logmode.length+opvalue.length+TransStruct.getPosition().length+TransStruct.getDestIP().length, TransStruct.getSourceIP().length);
             System.arraycopy(TransStruct.getUserName(), 0, bytes,islegal.length+logmode.length+opvalue.length+TransStruct.getPosition().length+TransStruct.getDestIP().length+TransStruct.getSourceIP().length, TransStruct.getUserName().length);
             System.arraycopy(TransStruct.getPassword(), 0, bytes,islegal.length+logmode.length+opvalue.length+TransStruct.getPosition().length+TransStruct.getDestIP().length+TransStruct.getSourceIP().length+TransStruct.getUserName().length, TransStruct.getPassword().length);
             return bytes;     
     }
 	 /*将字节流转换成登录结构体*/
 	  public RegPacketStruct BytesToRegStruct(byte[] bytes)
      {
          RegPacketStruct struReturn = new RegPacketStruct();
          if(bytes[0]==1){
        	  struReturn.isLegalPacket=true;
          }
          else{
        	  struReturn.isLegalPacket=false;
          }
          struReturn.LogMode=bytes[4];
          struReturn.OperValue=bytes[8];//获取返回的值，根据返回值进行相应的操作
          struReturn.Position=Arrays.copyOfRange(bytes, 12, 32);
          struReturn.DestIP=Arrays.copyOfRange(bytes, 32, 48);
          struReturn.SourceIP=Arrays.copyOfRange(bytes, 48, 64);
          struReturn.UserName=Arrays.copyOfRange(bytes, 64, 84);
          struReturn.Password=Arrays.copyOfRange(bytes, 84, 104);
          
          return struReturn;
          
      }
 	  /*将定长的字节数组转换成字符串*/
      public String LongByteToString(byte[] OriBytes) //收到的bytes转成string 要删掉末尾的0字符
      {
          String longString = null;
		try {
			longString = new String(OriBytes,"GB2312");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
          int endPoi = longString.indexOf("\0");
          return longString.substring(0, endPoi);
      }
      
      /*将接收到的字节流转换成结构体*/
      public DetailInfoRFIDStruct BytesToRFIDStruct(byte[] bytes)
      {
          DetailInfoRFIDStruct struReturn = new DetailInfoRFIDStruct();
          struReturn.SourceIP=Arrays.copyOfRange(bytes, 0, 16);
          struReturn.DestIP=Arrays.copyOfRange(bytes, 16, 32);
          struReturn.CaptrueTime=Arrays.copyOfRange(bytes, 32, 52);
          struReturn.RFIDNumber=Arrays.copyOfRange(bytes, 52, 76);
          struReturn.PersonName=Arrays.copyOfRange(bytes, 76, 96);
          struReturn.Organization=Arrays.copyOfRange(bytes, 96, 116);
          struReturn.ItemName=Arrays.copyOfRange(bytes, 116, 166);
          struReturn.isItemAllowed=Arrays.copyOfRange(bytes, 166, 186);
          struReturn.Position=Arrays.copyOfRange(bytes, 186, 206);
          return struReturn;
      }
      
 	 
}
