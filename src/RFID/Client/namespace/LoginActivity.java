package RFID.Client.namespace;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import RFID.Client.Comfunc.namespace.Predefine;
import RFID.Client.Comfunc.namespace.ThreadResult;
import RFID.Client.Comfunc.namespace.Utility;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import RFID.Client.Data.*;
import RFID.Client.Data.namespace.RegPacketStruct;
import RFID.Client.Comfunc.*;

public class LoginActivity extends Activity {
	
	private List<String> iplist;  //Record current IP address
	
	DatagramSocket receiveSocket;
	/** Called when the activity is first created. */
    @SuppressWarnings("null")
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);        
        setContentView(R.layout.main);
        //get serverip, username and password's id , and used for write auto
        EditText txtServerip = (EditText)findViewById(R.id.txtServerip);
  	   
  	   EditText txtUsername = (EditText)findViewById(R.id.txtUsername);
  	  
  	   EditText txtPassword = (EditText)findViewById(R.id.txtPassword);
  	 
  	   
  	   String[] dataFromFile=null;
		try {
			//read from private file
			dataFromFile = ReadFromFile();
			if(dataFromFile!=null){
				
				txtServerip.setText(dataFromFile[0]);
				txtUsername.setText(dataFromFile[1]);
				txtPassword.setText(dataFromFile[2]);
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		//txtServerip.setText("wqewq");

        iplist = Utility.getLocalIpAddress();
        ArrayAdapter<String> spinAdapter;
        if(iplist.isEmpty())
        {
        	Toast.makeText(getApplicationContext(), "无法获取本机ip地址",
        		     Toast.LENGTH_SHORT).show();
        }
        else
        {
        	/** put iplist into the spinner*/
        	Spinner mySpinner = (Spinner)findViewById(R.id.spinLocalip);
        	spinAdapter = new ArrayAdapter<String>(this,
        			android.R.layout.simple_spinner_item, iplist);
        	spinAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        	mySpinner.setAdapter(spinAdapter);

    		/** init Socket*/
    		try
    		  { 
    			receiveSocket = new DatagramSocket(Predefine.PortMonitorLog);	   
    		  } 
    		  catch (SocketException e) 
    		  {
    	        	Toast.makeText(getApplicationContext(), "无法创建连接Socket\n"+e.getMessage(),
    	        		     Toast.LENGTH_SHORT).show();
    		  }
        	
        }
        /**bind function to our buttons  */
        Button btnLogin=(Button)findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new Button.OnClickListener()
          {
        	public void onClick(View v)
        	{
        		/** Check local network*/
        		if(iplist.isEmpty()){
        			Toast.makeText(getApplicationContext(), 
        					"无法获取本机ip地址,不能登录",Toast.LENGTH_SHORT).show();
        			return;
        		}
        		
        		
        		EditText txtServerip = (EditText)findViewById(R.id.txtServerip);
        	   String serverip = txtServerip.getText().toString();
        	   EditText txtUsername = (EditText)findViewById(R.id.txtUsername);
        	   String username = txtUsername.getText().toString();
        	   EditText txtPassword = (EditText)findViewById(R.id.txtPassword);
        	   String password = txtPassword.getText().toString();
        	   Spinner mySpinner = (Spinner)findViewById(R.id.spinLocalip);
        	   String localip = mySpinner.getSelectedItem().toString();
        	   try {
        		   //write into priavte file
				WriteInFile(serverip,username,password);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
        	   if(CheckIPValid(serverip)){
        	  RegPacketStruct reg=new RegPacketStruct();
        	  Utility uti=new Utility();
        	  reg.isLegalPacket=true;
        	  reg.Position=uti.StringToBytes("", Predefine.PositionLength);
        	  reg.DestIP=uti.StringToBytes(serverip, Predefine.IPAddressLength);
        	  reg.SourceIP=uti.StringToBytes(localip, Predefine.IPAddressLength);
        	  reg.UserName=uti.StringToBytes(username, Predefine.UserNameLength);
        	  reg.Password=uti.StringToBytes(password, Predefine.PasswordLength);
        	  reg.LogMode=2;
        	  reg.OperValue=1;
        	    
        	   /** put these Strings into proper byte[]*/
        	   
        	   byte[] RegBytes = uti.RegStructToBytes(reg); //test data
        	 //  serverip = "192.168.0.130";
        	   
        	   /**send login packet bytes*/
        	   SendLoginPacket(RegBytes,serverip,Predefine.PortServerLog);
        		
        	   /**start receive thread */
        	   Thread recThread = new Thread(new ReceiveThread(getApplicationContext(), receiveSocket));
        	   recThread.start();
        	   
        	   try {
        		   recThread.join();
        	   } catch (InterruptedException e) {
        		   e.printStackTrace();
        	   }
        	   /*//检测两次是否有接收值
        	   boolean reveived=false;
        	   reveived=ThreadResult.Received;
        	   if(reveived == false){
        		  // Thread recThread = new Thread(new ReceiveThread(getApplicationContext(), receiveSocket));
            	   recThread.start();
            	   try {
            		   recThread.join();
            	   } catch (InterruptedException e) {
            		   e.printStackTrace();
            	   }
            	   reveived=ThreadResult.Received;
        		   *//**未收到返回包*//*
               	Toast.makeText(getApplicationContext(), 
               			"链接超时，请检查服务器连接情况", Toast.LENGTH_SHORT).show();
              // 	return;
        	   }*/
        	   if(ThreadResult.Received==false){
        		   Toast.makeText(getApplicationContext(), 
                  			"链接超时，请检查服务器连接情况", Toast.LENGTH_SHORT).show();
        	   }
        	   else{
        		   /**解析返回包*/
        		   byte[] data = ThreadResult.logdata;
        		   int length = ThreadResult.logdatalength;
        		   RegPacketStruct regpac=new RegPacketStruct();
        		   regpac=uti.BytesToRegStruct(data);
        		  if(regpac.getOperValue()==9){//登陆成功
        			 DealWithRegResult("Pass",1);
        		  }
        		  else{
        			  DealWithRegResult("Fail",regpac.getOperValue());
        		  }

        	   }
        	   
        	   
        	   }
        	   else{
        		   Toast.makeText(getApplicationContext(), 
          					"ip地址不正确",Toast.LENGTH_SHORT).show();
          			return;
        	   }

        	}
          });

        /**the Cancel button , set listener**/
        Button btnCancel=(Button)findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new Button.OnClickListener()
          {
        	public void onClick(View v)
        	{
        		LoginActivity.this.finish();
        	}
          });

    }
    //处理从服务器端返回的情况
    public void DealWithRegResult(String OpValue, int errNumber)
    {
        if (OpValue == "Pass")
        {
        	 /**close this activity, open the Monitor activity*/
        	   if(receiveSocket != null && receiveSocket.isClosed() == false)
        		   receiveSocket.close();
        	   Intent intent = new Intent();
          	intent.setClass(LoginActivity.this, MainActivity.class);
          	startActivity(intent);
          	LoginActivity.this.finish();  
            return;
        }

        // hep
        if (OpValue == "Fail" && errNumber == 12)
        {
        	Toast.makeText(getApplicationContext(), 
           			"密码有误！尝试登陆次数超过限制，该账户已被锁定，请稍后再试!!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (OpValue == "Fail" && errNumber == 11)
        {
        	Toast.makeText(getApplicationContext(), 
           			"该账户已被锁定，请稍后再试!", Toast.LENGTH_SHORT).show();
            return;
        }
        // end add.

        if (OpValue == "Fail" && errNumber == 8)
        {
        	Toast.makeText(getApplicationContext(), 
           			"本机IP地址尚未注册,无法登陆!!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (OpValue == "Fail" && errNumber == 7)
        {
        	Toast.makeText(getApplicationContext(), 
           			"该帐户已登陆,无法登陆!!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (OpValue == "Fail" && errNumber == 6)
        {    
        	Toast.makeText(getApplicationContext(), 
           			"密码有误,请重新输入", Toast.LENGTH_SHORT).show();
            return;
        }

        if (OpValue == "Fail" && errNumber == 5)
        {
        	Toast.makeText(getApplicationContext(), 
           			"用户名不存在,请重新输入", Toast.LENGTH_SHORT).show();
            
            return;
        }
    }
    
    
		public void onDestroy()
		{
			if(receiveSocket != null && receiveSocket.isClosed() == false)
     		   receiveSocket.close();
			super.onDestroy();
		}
		    
     private void SendLoginPacket(byte[] RegBytes, String serverip, int serverport ){
    	DatagramSocket sock = null;
    
    	try{
	    	sock = new DatagramSocket();        	    	
	    	int size = RegBytes.length;
	    	InetAddress server = InetAddress.getByName(serverip);
	    	DatagramPacket packet = new DatagramPacket(RegBytes, size, server, serverport);
	    	sock.send(packet);

	    }
	    catch(Exception e){
	    	final String TAG = "sock error";
	    	Log.e(TAG,e.toString());
	    }
    	if(sock != null && !sock.isClosed())
    		sock.close();
    }
     
     //将登陆的信息写入私有文件  
     public  void WriteInFile(String s1,String s2,String s3) throws IOException{
 		FileOutputStream fos=this.openFileOutput(Predefine.PRIVATE_FILE,Context.MODE_PRIVATE);
 		PrintWriter pr=new PrintWriter(fos);
 		pr.print(s1+"+"+s2+"+"+s3);
 		pr.flush();
 		pr.close();
 		fos.close();
 	}
     //读取私有文件的内容
   	@SuppressWarnings("null")
	public String[] ReadFromFile() throws IOException{
   		 String[] s = new String[3];
   		
   		FileInputStream fis=this.openFileInput(Predefine.PRIVATE_FILE);
   		BufferedReader br=new BufferedReader(new InputStreamReader(fis));
   		String str="";
   		String line="";
   		while((line=br.readLine())!=null){
   			str+=line;;
   		}
   		if((str!=null||!str.equals(""))&&str.contains("+")){
   			
   			s[0]=str.substring(0, str.indexOf("+"));
   			s[1]=str.substring(str.indexOf("+")+1, str.lastIndexOf("+"));
   			s[2]=str.substring(str.lastIndexOf("+")+1,str.length());
   		}
   		br.close();
   		fis.close();
   		
   		return s;
   		
   	}
    public Boolean CheckIPValid(String strIP)  // 判断IP地址合法性 功能很简单 
    {
        //Regex rxIP = new Regex(@"^([1-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])(\.([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])){3}$"); //For IP Address validation
    	boolean flag = false;   
	    Pattern pattern = Pattern.compile("\\b((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\b");   
	    Matcher m = pattern.matcher(strIP);   
	    flag = m.matches();   
	    return flag;   
    } 


    

}