package com.MY.netTools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import org.apache.http.util.EncodingUtils;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.os.Parcelable.Creator;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.StrictMode;
import android.provider.Settings;
import android.telephony.CellIdentityGsm;
import android.telephony.CellIdentityLte;
import android.telephony.CellInfo;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellLocation;
import android.telephony.CellSignalStrength;
import android.telephony.CellSignalStrengthLte;
import android.telephony.NeighboringCellInfo;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.MY.netTools.business.AirplaneModeService;
import com.MY.netTools.business.RawData;
import com.MY.netTools.business.ToolSrvImpl;
import com.MY.pingtest.R;

public class MainActivity extends Activity {

	private Dialog progressDialog;
	Intent intent1;
	Intent intent2;
	Intent intent3;
	private Activity my = this;
	private boolean k = true;
    private final StringBuffer result = new StringBuffer();
    public ConnectionChangeReceiver mReceiver = new ConnectionChangeReceiver();
    protected static FileOutputStream fout = null;
    public RawData app; 
    long DtachstartTime = 0;
    long AttachstartTime = 0;
    private TextView UE;
    private TextView attention;
    int match = 1;
    int state=0;
    int att_attach = 0;
    int succ_attach = 0;
    PowerManager.WakeLock wakeLock;
    ToolSrvImpl Ob = new ToolSrvImpl();
    private Button btnUrl;
    private boolean nor = true;

    private MyPhoneStateListener MyListener = null;
    
	private void copyfiles(String desPath,String resname) {
		// TODO Auto-generated method stub
		File file1=new File(desPath);//由绝对目的地址创建文件		
		/*
		 * 判断目标文件是否已经存在
		 */
	//	if(!file1.exists()){
			k=true;
	//	}
		/*
		 * 目标文件不存在，复制文件到目的地址
		 */
		if(k){
			  try {
				file1.createNewFile();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				Log.e("demo","创建文件失败");
				Log.e("demo",e1.getMessage());
			}
		try {
            InputStream in = this.getResources().getAssets().open(resname);  
            OutputStream out = new FileOutputStream(file1);  
            // Transfer bytes from in to out  
            byte[] buf = new byte[1024];  
            int len;  
            while ((len = in.read(buf)) > 0) {  
                out.write(buf, 0, len);  
            }  
            in.close();  
            out.close();  
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.e("demo","读取assets文件错误");
		}
		}
		k=false;
	}
	private class MyPhoneStateListener extends PhoneStateListener
	{

		@Override
		public void onCellInfoChanged(List<CellInfo> cellInfo) {
			// TODO Auto-generated method stub
			super.onCellInfoChanged(cellInfo);
			ToolSrvImpl.STrace("Cell changed",cellInfo.toString());
		//	Log.e("Cell changed",cellInfo.toString());
		}

		@Override
		public void onSignalStrengthsChanged(SignalStrength signalStrength) {
			// TODO Auto-generated method stub
			super.onSignalStrengthsChanged(signalStrength);					      
					Intent mIntent = new Intent("SignalStrengthsChanged");
					 
                     				
					 String local = signalStrength.toString();
					 String[] s = local.split(" ");
					
		//			 Log.e("SignalStrengthsChanged",local);	
		//		        Log.i("MainActivity", "onSignalStrengthsChanged Rsrp is " + s[9]);
		//		        Log.i("MainActivity", "onSignalStrengthsChanged Rsrq is " + s[10]);
			    //      int i = Integer.parseInt(s[8]) * 2 - 113;
		//	          Log.i("MainActivity", "onSignalStrengthsChanged signal sens is " + i + " dbm");
			        
		//	          break;
		//	        setSinr((byte)Integer.parseInt(s[11]));
		//	        setCqi((byte)Integer.parseInt(s[12]));
					//11-16 10:49:27.705: E/SignalStrengthsChanged(24856): SignalStrength:GW:99 -1 -1 255 0,CDMA:-1 255,EVDO:-1 255 255,LTE:99 2147483647 2147483647 2147483647 2147483647,TD:-86;gsm|lte
		       //     mIntent.putExtra("data", " Rsrp is "+ s[9] + " Rsrq "+s[10] +" signal sens "+i +" Sinr = "+Integer.parseInt(s[11]));
					 mIntent.putExtra("data",local);
		            sendBroadcast(mIntent);

		}
		
	}
    public static String getPsdnIp() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                    //if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet6Address) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (Exception e) {
        }
        return "";
    }
    @SuppressLint("SimpleDateFormat") public class ConnectionChangeReceiver extends BroadcastReceiver {   
        @Override   
        public void onReceive( Context context, Intent intent ) { 
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);   
            NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();   
            NetworkInfo mobNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);   
            UE.setText("  UE Mode: \r\n");
            app = (RawData)getApplication();

            if ( activeNetInfo != null ) {   
            //    Toast.makeText(context, "Active Network Type : " +
            //          activeNetInfo.getTypeName(), Toast.LENGTH_SHORT ).show();
                app.activeNetInfo = activeNetInfo.getTypeName();
                UE.append("  Active Network Type : "+activeNetInfo.getTypeName()+"\r\n");
                }   
            if( mobNetInfo != null ) {   
            	 app.mobNetInfo = mobNetInfo.getSubtypeName();
            //    Toast.makeText( context, "Mobile Network Type : " +
          //            mobNetInfo.getSubtypeName(), Toast.LENGTH_SHORT ).show();   
                UE.append("  Mobile Network Type : "+mobNetInfo.getSubtypeName()+"\r\n");
         //       Toast.makeText( context, "Mobile Network TypeID : " +
        //                String.valueOf(mobNetInfo.getSubtype()), Toast.LENGTH_SHORT ).show();  
                UE.append("  Mobile Network TypeID : "+String.valueOf(mobNetInfo.getSubtype())+"\r\n");
               
            }
                
                if(app.getCleanPerformance())
                {
                    att_attach = 0;
                    succ_attach = 0;
                    app.setCleanPerformance(false);
                }
     //			if(app.getrepeattime() == 0)
     //			{
	//				match=0;
	//			}
                //yiweng 3.09
                if(app.getIsTriggered())
                { 
    			  if(mobNetInfo.getSubtype() == 0)//airplane open 
    			  {
    			   switch(state){
    			   case 0://state idle
    			   {
    					 state = 1;
    					 DtachstartTime = System.currentTimeMillis();
    	                 match = 1;
    			   }
    			   break;
    			   case 1://state valid
    			   {
    				    match=2;
    				    state = 0;
    				    DtachstartTime = 0;
    				    AttachstartTime = 0;
    			   }
    			   break;
    			   default:
    				break;
    			   }
    			   att_attach++;
    			  }
  				  else//close airplane open
  				  { 					
     			   switch(state){
     			   case 0://state idle
     			   {
     				  match = -2;
  				      DtachstartTime = 0;
  				      AttachstartTime = 0;
     			   }
     			   break;
     			   case 1://state valid
     			   {
     				  match = 0;
     				  AttachstartTime = System.currentTimeMillis();
     				  if(app.getrepeattime() == 0)
     				  {
     					 app.setIsTriggered(false);
     					 state = 0;
     				  }
     				  else
     				  {
     					 state = 0;
     				  }
     				 succ_attach++;
     			   }
     			   break;
     			   default:
     				break;
     			   }
  				}
                	               
/*                if(app.getIsTriggered())
                {               		
				String str = "";  
				if(mobNetInfo.getSubtype() == 0)//airplane open 
				{
					DtachstartTime = System.currentTimeMillis();
					match ++;
					att_attach++;
				}
				else//close airplane open
				{ 					
					AttachstartTime = System.currentTimeMillis();
					match--;
					succ_attach++;
					SimpleDateFormat  formatter =  new SimpleDateFormat("yyyy.MM.dd    HH:mm:ss     ");
					Date  curDate = new Date(System.currentTimeMillis());//获取当前时间       
					str = "Attach : "+mobNetInfo.getSubtypeName()+"  "+formatter.format(curDate) + "\r\n";  
					if(app.getrepeattime() == 0)
					{
						app.setIsTriggered(false);
						match=0;   //yiweng 3.09 
					}
				}*/
                try {
                	if(match == 0)
                	{
                	String str="";
    				SimpleDateFormat  formatter =  new SimpleDateFormat("yyyy.MM.dd    HH:mm:ss     ");
    				str = "Attach : "+mobNetInfo.getSubtypeName()+"  "+formatter.format(AttachstartTime) + "\r\n";  			
                	String s1 = "Last detach/attach time is "+ String.valueOf((AttachstartTime-DtachstartTime))+" ms "+" Successed detach: "+String.valueOf(att_attach) + " Successe attach: "+String.valueOf(succ_attach)+"\r\n";
                   	fout =openFileOutput("AttachDtachRerport" , MODE_APPEND);//不覆盖
                   	fout.write(str.getBytes());
                   	fout.write(s1.getBytes());
                   	AttachstartTime = 0;
                   	DtachstartTime = 0;
                   	result.append(str).append(s1);
                   	
    				Intent mIntent = new Intent("UESTATUES_MINE");
                    mIntent.putExtra("UEresult", result.toString());    
                    sendBroadcast(mIntent);
                    match = 1;
                    result.delete(0, result.length());
                	}
                	else
                	{
                	if(match > 1)//too much airplane opening
                	{
                    	String s1 = "Miss attach message "+" Successe detach: "+String.valueOf(att_attach) + " Successed attach: "+String.valueOf(succ_attach)+"\r\n"          			
                    			+"\r\n";
                       	fout =openFileOutput("AttachDtachRerport" , MODE_APPEND);//不覆盖
                       	result.append(s1);
        				Intent mIntent = new Intent("UESTATUES_MINE");
                        mIntent.putExtra("UEresult", result.toString());    
                        sendBroadcast(mIntent);
                        result.delete(0, result.length());
                	
                	}
                	else if(match < -1)        //too much airplane colsing
                	{
                    	String s1 = " Miss Detach message,please increase modeChangeDurationTimer "+"\r\n";
                       	fout =openFileOutput("AttachDtachRerport" , MODE_APPEND);//不覆盖
                       	result.append(s1);
        				Intent mIntent = new Intent("UESTATUES_MINE");
                        mIntent.putExtra("UEresult", result.toString());    
                        sendBroadcast(mIntent);
                        result.delete(0, result.length());
                		
                	}
                	}
                 //   	fout =openFileOutput(fileName,MODE_PRIVATE);
            		} catch (FileNotFoundException e) {
            			// TODO Auto-generated catch block
            			e.printStackTrace();
            		} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} 
                } 
//				if(app.getrepeattime() == 0)
//				{
//					match=0;
//				}
                //yiweng 3.09
               }   
    }  
    
    public static class BootBroadcastReceiver extends BroadcastReceiver {
        static final String action_boot="android.intent.action.BOOT_COMPLETED"; 
     
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(action_boot)){ 
                Intent ootStartIntent=new Intent(context,MainActivity.class); 
                ootStartIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
                context.startActivity(ootStartIntent); 
            }
     
        }
     
    }
    
	private ServiceConnection conn = new ServiceConnection() {
        /** 获取服务对象时的操作 */
        public void onServiceConnected(ComponentName name, IBinder service) {
            // TODO Auto-generated method stub
        }
        
        /** 无法获取到服务对象时的操作 */
        public void onServiceDisconnected(ComponentName name) {
            // TODO Auto-generated method stub
        }

    };
    
	private ServiceConnection conn1 = new ServiceConnection() {
        /** 获取服务对象时的操作 */
        public void onServiceConnected(ComponentName name, IBinder service) {
            // TODO Auto-generated method stub
        }
        
        /** 无法获取到服务对象时的操作 */
        public void onServiceDisconnected(ComponentName name) {
            // TODO Auto-generated method stub
        }

    };
    

@SuppressWarnings("deprecation")
@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
    	try {
			fout =openFileOutput("1" , MODE_APPEND);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}//不覆盖
        if (wakeLock ==null) {
           
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, this.getClass().getCanonicalName());
            wakeLock.acquire();
        }
        
        MyListener = new MyPhoneStateListener();
 //       copyfiles("/data/local/tmp/busybox","busybox");//复制busybox到手机中 //yiweng
 /*       AirplaneModeService.execRootCmdSilent("chmod 777 /data/local/tmp");
        copyfiles("/data/local/tmp/netspeed_private","netspeed_private");//复制busybox到手机中 //yiweng
		copyfiles("/data/local/tmp/netspeed","netspeed-1");//复制netspeed脚本到手机中 //yiweng
		copyfiles("/data/local/tmp/busybox","busybox");//复制busybox到手机中 //yiweng
		copyfiles("/data/local/tmp/PID","PID");//复制busybox到手机中 //yiweng
		File file2=new File("/data/local/tmp/busyboxdownup");//由绝对目的地址创建文件		
		if(!file2.exists()){
			AirplaneModeService.execRootCmdSilent("cp /data/local/tmp/busybox /data/local/tmp/busyboxdownup");
		}
		AirplaneModeService.execRootCmdSilent("chmod 777 /data/local/tmp/*");*/
        copyfiles("/data/data/com.MY.pingtest/netspeed","netspeed_private");//复制busybox到手机中 //yiweng
	//	copyfiles("/data/data/com.MY.pingtest/netspeed","netspeed-1");//复制netspeed脚本到手机中 //yiweng
	//	copyfiles("/data/data/com.MY.pingtest/busybox","busybox");//复制busybox到手机中 //yiweng
		copyfiles("/data/data/com.MY.pingtest/PID","PID");//复制busybox到手机中 //yiweng
		copyfiles("/data/data/com.MY.pingtest/getNetoutput","getNetoutput");//复制busybox到手机中 //yiweng
		
	     String Imei = ((TelephonyManager) getSystemService(TELEPHONY_SERVICE)).getDeviceId();
	     String[] createupfile={"dd","if=/dev/zero","of=upfiledemo"+Imei,"bs=8192","count=10000"};
		//String[] chcmdupfiledemo={"chmod","777","/data/local/tmp/upfiledemo"};
		String path="/data/data/com.MY.pingtest/";
		
	    try {
	    	runFtp.runOnly(createupfile,path);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} //yiweng
		UE = (TextView)findViewById(R.id.UE);
		UE.append("  UE Mode: \r\n");
		
		attention = (TextView)findViewById(R.id.attention);
		attention.setText("");
	   
		app = (RawData)getApplication();
		app.setServiceConnection(conn);
		intent1 = new Intent(MainActivity.this, Fxservice.class);  //yiweng  手机测试的时候暂时关掉
        //启动FxService  
        bindService(intent1,conn,Context.BIND_AUTO_CREATE);//yiweng
		SharedPreferences settings = this.getSharedPreferences("myConfig", 0);
		String DisabelAutoTest = settings.getString("DisableAutoTest", "");
		if(!DisabelAutoTest.equals("Yes"))
		{
		intent2 = new Intent(MainActivity.this, clientService.class);  //
        //启动FxService  
        bindService(intent2,conn,Context.BIND_AUTO_CREATE);//yiweng
		}
		intent3 = new Intent(MainActivity.this, SocketService.class);  //
		bindService(intent3,conn1,Context.BIND_AUTO_CREATE);//
		
        Button imageButton1;
        //1 read config files
   //     SharedPreferences sharedPreferences = getSharedPreferences("soliu", MODE_PRIVATE); //配置文件
        IntentFilter mFilter = new IntentFilter();
        mFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mReceiver, mFilter);
        
        IntentFilter mFilter1 = new IntentFilter();
        mFilter1.addAction("AUTOTEST");
        registerReceiver(mReceiver1, mFilter1);
        
        IntentFilter mFilter2 = new IntentFilter();
        mFilter2.addAction("SocketQeustReStart");
        registerReceiver(mReceiver2, mFilter2);
        
	     IntentFilter mFilter3 = new IntentFilter();
		 mFilter3.addAction("URL");
		 registerReceiver(mReceiver3, mFilter3);
        
        nor = true;
        
        imageButton1 = (Button)this.findViewById(R.id.ImageButton);
        Button btnPing = (Button) this.findViewById(R.id.main_btn_ping);
        File file = new File(Environment.getExternalStorageDirectory(), "NetAnts");
        file.mkdirs();
        
        final TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
      
        tm.listen(MyListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS | PhoneStateListener.LISTEN_SERVICE_STATE);
        //以下分别为每个按钮设置事件监听 setOnClickListener  
        
    //    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+ "13658054649"));
    //    startActivity(intent);
        imageButton1.setOnClickListener(new OnClickListener()
        {

			@SuppressLint("NewApi") @Override
			public void onClick(View v) {
				
	                Intent intent = new Intent(MainActivity.this, UEstatuesActivity.class);
	                startActivity(intent);
			      }
			   
			}//end of click view
        
        );
        
        btnPing.setOnClickListener(new OnClickListener() {
            @SuppressWarnings("deprecation")
			@Override
            public void onClick(final View v) {
                Intent intent = new Intent(MainActivity.this, PingActivity.class);
                startActivity(intent);
            }
        });

        btnUrl = (Button) this.findViewById(R.id.main_btn_url);
        btnUrl.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View v) {
                Intent intent = new Intent(MainActivity.this, UrlActivity.class);
                startActivity(intent);
            }
        });
        //mail
        Button btnTraceroute = (Button) this.findViewById(R.id.main_btn_traceroute);
        btnTraceroute.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View v) {
                  Intent intent = new Intent(MainActivity.this, MailActivity.class);
                  startActivity(intent);
              }                 
        });
        //FTP
        Button btnFtp=(Button)this.findViewById(R.id.main_btn_ftp);
        btnFtp.setOnClickListener(new OnClickListener()
        {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(MainActivity.this,FtpLogin.class);
				startActivity(intent);
			}
        	
        });
        // config
        Button btnConfig = (Button)findViewById(R.id.Config);
        btnConfig.setOnClickListener(new OnClickListener()
        {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(MainActivity.this,myConfig.class);
				startActivity(intent);
			}

			private ContentResolver getContentResolver() {
				// TODO Auto-generated method stub
				return null;
			}
        	
        });
        //airplane
        Button btnAirplane=(Button)this.findViewById(R.id.airplane);
        btnAirplane.setOnClickListener(new OnClickListener()
        {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(MainActivity.this,AttachActivity.class);
				startActivity(intent);
			}
        });
        //autotest
        Button btnAutoTest = (Button)this.findViewById(R.id.btnAutoTest);
        btnAutoTest.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(MainActivity.this,AutoTest.class);
				startActivity(intent);
			}
        	
        });
    }
private BroadcastReceiver mReceiver1 = new BroadcastReceiver()
{

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		String action = intent.getAction();
		if(action.equals("AUTOTEST"))
		{
			String result = intent.getStringExtra("show"); 
			if(result != null)
			{
				if(result.contains("InServerCommand")){
					RawData.ServerHaveCommand = true;
			//		Log.e("InServerCommand","InServerCommand");
					//attention.setText("UE is in server mode, any of your operation in this UE will influence the server’s action");
					setTitle("UE is in server mode");
				}
				if(result.contains("OutServerCommand"))
				{
					RawData.ServerHaveCommand = false;
			//		Log.e("OutServerCommand","OutServerCommand");
				//	attention.setText("");
					setTitle("NetAnts 2.7");
				}
			}
		}
		
			if(nor)
			{
		    nor = false;
		    RawData.ServerHaveCommand = false;
            Intent intent1 = new Intent(MainActivity.this, AutoTest.class);
            startActivity(intent1);
			}
		
	}
	
};
    @Override
protected void onDestroy() {
	// TODO Auto-generated method stub
    unregisterReceiver(mReceiver);
    unregisterReceiver(mReceiver1);
    unregisterReceiver(mReceiver2);
    if (wakeLock !=null&& wakeLock.isHeld()) {
        wakeLock.release();
        wakeLock =null;
    }
    ToolSrvImpl.cleanTrace();
	super.onDestroy();
    android.os.Process.killProcess(android.os.Process.myPid());
}

	public void showProgressDialog() {
        if (this.progressDialog == null) {
            this.progressDialog = new Dialog(this);
            //this.progressDialog.setContentView(R.layout.wisdombud_loading_dialog);
            this.progressDialog.setCancelable(true);
        }

        this.progressDialog.show();
    }
    
    public void closeProgressDialog() {
        if (this.progressDialog != null) {
            this.progressDialog.dismiss();
        }
    }
    
	private BroadcastReceiver mReceiver2 = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			if(action.equals("SocketQeustReStart"))
			{
				ToolSrvImpl.STrace("SocketQeustReStart","SocketQeustReStart");
		//		Log.e("SocketQeustReStart","SocketQeustReStart");
				unbindService(conn1);
				stopService(intent3);
		//		intent3 = new Intent(MainActivity.this, SocketService.class);
				bindService(intent3,conn1,Context.BIND_AUTO_CREATE);//              
			}
		}	
	};
	private BroadcastReceiver mReceiver3 = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent) {
			 final String runresult1;
			 
			 StringBuffer result = new StringBuffer();
			// TODO Auto-generated method stub
			String action = intent.getAction();
			String data = intent.getStringExtra("data"); 
			//Log.e("URL",data);
			if(action.equals("URL"))
			{
				WebView webView = new WebView(MainActivity.this);
				final long t1 = System.currentTimeMillis();
				Log.e("URL1",data);
				webView.loadUrl(data);	
				
				{
	                   String cmdPing = "sh /data/data/com.MY.pingtest/getNetoutput";
	                   Process p;
					try {
						String str;
						p = Runtime.getRuntime().exec(cmdPing);
						BufferedReader bufferReader = new BufferedReader(new InputStreamReader(p.getInputStream()));
		                 while ((str = bufferReader.readLine()) != null) {
		               // 	   Log.i("check eth0",runresult);//yiweng
		                	   result.append(str.toString()).append("\r\n");
		                	   }
					  } catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					  }
					runresult1 = runFtp.normalizationgSriptsInputForGetNetOutPut(result.toString());
				}
				Log.e("         ",runresult1);
				webView.setWebChromeClient(new WebChromeClient(){

					@Override
					public void onProgressChanged(WebView view, int newProgress) {
						// TODO Auto-generated method stub
						super.onProgressChanged(view, newProgress);
						if (newProgress == 100) 
						{
							long t = System.currentTimeMillis() - t1;
							 StringBuffer result1 = new StringBuffer();
							 String runresult2;
							
				                   String cmdPing = "sh /data/data/com.MY.pingtest/getNetoutput";
				                   Process p;
								try {
									String str;
									p = Runtime.getRuntime().exec(cmdPing);
									BufferedReader bufferReader = new BufferedReader(new InputStreamReader(p.getInputStream()));
					                 while ((str = bufferReader.readLine()) != null) {
					               // 	   Log.i("check eth0",runresult);//yiweng
					                	   result1.append(str.toString()).append("\r\n");
					                	   }
								  } catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								  }
								runresult2 = runFtp.normalizationgSriptsInputForGetNetOutPut(result1.toString());
								
								String data0[] = runresult1.split(";");
								String data1[] = runresult2.split(";");
								long in = Long.parseLong(data1[0]) - Long.parseLong(data0[0]);
								long out = Long.parseLong(data1[1]) - Long.parseLong(data0[1]);
							
							
				   		    Intent mIntent = new Intent("URLACKDATA");
				            mIntent.putExtra("data",String.valueOf(t)+";"+String.valueOf(in));    
				            sendBroadcast(mIntent);
						}
					}
					
				});
				webView.setWebViewClient(new WebViewClient(){

					@Override
					public void onReceivedError(WebView view, int errorCode,
							String description, String failingUrl) {
						// TODO Auto-generated method stub
						Log.e("receive error",description);
			   		    Intent mIntent = new Intent("URLACKDATA1");
			            mIntent.putExtra("data","");    
			            sendBroadcast(mIntent);
						super.onReceivedError(view, errorCode, description, failingUrl);
					}
					
				});
				 
			}
		}	
	};
}
