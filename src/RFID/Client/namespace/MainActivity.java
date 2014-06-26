package RFID.Client.namespace;

import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;

import RFID.Client.Comfunc.namespace.Predefine;
import RFID.Client.Comfunc.namespace.ThreadResult;
import RFID.Client.Comfunc.namespace.Utility;
import RFID.Client.Data.namespace.DetailInfoRFIDStruct;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class MainActivity extends ListActivity {

	DatagramSocket receiveSocket;
	SimpleAdapter adapter;
	Thread recThread;
	ArrayList<HashMap<String,String>> list;
	private AlertDialog mQuitDlg = null;
	static final int QUIT_DLG = 0;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        
       /* //要获取屏幕的宽和高等参数，首先需要声明一个DisplayMetrics对象，屏幕的宽高等属性存放在这个对象中
        DisplayMetrics DM = new DisplayMetrics();
        //获取窗口管理器,获取当前的窗口,调用getDefaultDisplay()后，其将关于屏幕的一些信息写进DM对象中,最后通过getMetrics(DM)获取
        getWindowManager().getDefaultDisplay().getMetrics(DM);
        //打印获取的宽和高
        int width=DM.widthPixels;
        int height=DM.heightPixels;
        
        ListView lv=(ListView)findViewById(R.layout.listview);
        
        ListView.LayoutParams lp = new ListView.LayoutParams(width, height);
        lv.setLayoutParams(lp);//
*/       // ListView.LayoutParams lp1=(ListView.LayoutParams)lv.seti.
       // ListView.		
       /* LinearLayout.LayoutParams linearParams0 = (LinearLayout.LayoutParams)table_item_item0.getLayoutParams();  
        linearParams0.width = itemWidth;  
        table_item_item0.setLayoutParams(linearParams0);*/

        
        
        
        setContentView(R.layout.mainact);
     

        list = new ArrayList<HashMap<String,String>>();  
        

        /**set alarm list adapter*/
        adapter = new SimpleAdapter(this,list,R.layout.listview,
        		new String[]{"ItemName","PersonName","Organization","AlarmTime",
        		"Position","IsAllowed"},
        		new int[]{R.id.txtItemName,R.id.txtPersonName,R.id.txtOrganization,
        		R.id.txtAlarmTime,R.id.txtPosition, R.id.txtIsAllowed});  
          
         this.getListView().addHeaderView(
         		this.getLayoutInflater().inflate(R.layout.header_view, null), 
         		null, false); 
         setListAdapter(adapter); 
         
         //ZYF
         final AlertDialog.Builder builder = new AlertDialog.Builder(this); 
         this.getListView().setOnItemClickListener(new OnItemClickListener(){   
        	 @SuppressWarnings("unchecked")  
        	 //单击显示告警的详细信息
        	 public void onItemClick(AdapterView<?> parent, View view, int position, long id) {   
        		 ListView listView = (ListView)parent;    
        		 HashMap<String, String> map = (HashMap<String, String>) listView.getItemAtPosition(position);    
        		 String temp = "物品名称 ："+map.get("ItemName")+"\r\n"; 
        		 temp+="责任人     ："+map.get("PersonName")+"\r\n";
        		 temp+="所属部门 ："+map.get("Organization")+"\r\n";
        		 temp+="告警时间 ："+map.get("AlarmTime")+"\r\n";
        		 temp+="检测位置 ："+map.get("Position")+"\r\n";
        		 temp+="是否授权 ："+map.get("IsAllowed")+"\r\n";
        		 builder.setTitle("告警详细信息");
        		 builder.setMessage(temp);
        		 builder.show();
        		 }        
        	 });
         
         /**the Cancel button , set listener**/
         Button btnCancel=(Button)findViewById(R.id.btnQuit);
         btnCancel.setOnClickListener(new Button.OnClickListener()
           {
         	public void onClick(View v)
         	{
         		showDialog(QUIT_DLG);
         	}
           });
         
         /**the Clear button , set listener**/
         Button btnClear=(Button)findViewById(R.id.btnClear);
         btnClear.setOnClickListener(new Button.OnClickListener()
           {
         	public void onClick(View v)
         	{
         		list.clear();
         		adapter.notifyDataSetChanged();
         		setListAdapter(adapter);
         	}
           });
         
         
         
         /** init Socket*/
 			try
 		  { 
 			receiveSocket = new DatagramSocket(Predefine.PortMonitorData);	   
 		  } 
 		  catch (SocketException e) 
 		  {
 	        	Toast.makeText(getApplicationContext(), "无法创建报警接收Socket\n"+
 	        			e.getMessage(),Toast.LENGTH_SHORT).show();
 	        	return;
 		  }
	  	   /**start receive thread */
 			ThreadResult.bStopRec = false;
	  	   recThread = new Thread(new AlarmRecThread(receiveSocket,list,handler));
	  	   recThread.start();
	  	
    }
    
       /** Receive message from other thread, refresh UI **/
       private Handler handler = new Handler(){
    	public void handleMessage(Message msg) {
    		switch(msg.what){
	    		case Predefine.MsgRefreshlist:
	    			/** 得到新的报警，刷新*/
	    			HashMap<String,String> map1 = new HashMap<String,String>();
	    			map1 = (HashMap<String,String>)msg.obj;
	    			list.add(0,map1);
	    			adapter.notifyDataSetChanged();
	    			setListAdapter(adapter);
	    			break;
    			default:
    				break;
    		}
    	}
    };
    protected Dialog onCreateDialog(int id) {
        switch(id) {
	        case QUIT_DLG:
	        	return (initQuitDlg() );           	
	        default:
	            return null;
        }
    }    
    
    private Dialog initQuitDlg() {

		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setMessage("退出涉密物品监控端? ");
		builder.setCancelable(false);
		builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				
				MainActivity.this.finish();
			}				
		});

		builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				
				dialog.cancel();
			}
				
		});
		
		mQuitDlg = builder.create();
        return (mQuitDlg);   
	}
    public void onDestroy(){
    	ThreadResult.bStopRec = true;
 	   try {
		   recThread.join();
	   } catch (InterruptedException e) {
		   e.printStackTrace();
	   } 	
    	if(receiveSocket != null && receiveSocket.isClosed() == false)
    		receiveSocket.close();
    	super.onDestroy();
    }
    
   
    public void onConfigurationChanged(Configuration newConfig) {


        super.onConfigurationChanged(newConfig);


        

        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {

        }

        else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {

        }

        }    
       
    
    
}

