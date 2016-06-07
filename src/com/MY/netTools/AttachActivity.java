package com.MY.netTools;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import com.MY.netTools.business.AirplaneModeService;
import com.MY.netTools.business.RawData;
import com.MY.pingtest.R;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

public class AttachActivity extends Activity {
	protected static FileOutputStream fout = null;
//	private final StringBuffer report2file = new StringBuffer();
	private String fileName = "AttachDtachRerport";
	private Button btnStart;
	private Button btnStop;
	private TextView resultText;
	private TextView showConfig;
	private CheckBox usingnewfile;
	private CheckBox cleanPerformance;
	private AirplaneModeService Air = new AirplaneModeService();
	private int timer = 1000;
	private int repeattime = 1;
    public RawData app; 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.net_tool_attach);
        String fileName = "IPreport";
		//create new file for IP report
        try {
       	fout =openFileOutput(fileName , MODE_APPEND);//²»¸²¸Ç
     //   	fout =openFileOutput(fileName,MODE_PRIVATE);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}   
        
        btnStart = (Button) findViewById(R.id.Attach_Start);
        btnStop = (Button) findViewById(R.id.Attach_Stop);
        resultText = (TextView) findViewById(R.id.Attach_Result);
        showConfig = (TextView) findViewById(R.id.showAttachConfig);
        usingnewfile = (CheckBox) findViewById(R.id.checkBox_Attach_useNew);
        cleanPerformance=(CheckBox) findViewById(R.id.checkBox_cleanPerformance);
        SharedPreferences outsharedPreferences= getSharedPreferences("myConfig",
				Activity.MODE_PRIVATE); 
		    String modeChangeDurationTimer =outsharedPreferences.getString("modeChangeDurationTimer", "");
		    String modeChangeTime =outsharedPreferences.getString("modeChangeTime", "");

		    if(!((modeChangeDurationTimer!=null)&&(modeChangeDurationTimer.length()!=0)))
		   {
			  modeChangeDurationTimer = "5000";
		   }
		   if(!((modeChangeTime!=null)&&(modeChangeTime.length()!=0)))
		   {
		  	modeChangeTime = "1";
		   }
		   showConfig.setText("Config data: \r\n"+"modeChangeDurationTimer :"+modeChangeDurationTimer+"\r\n"+" modeChangeTime :"+modeChangeTime+"\r\n");
		   
			IntentFilter myIntentFilter = new IntentFilter();  
	        myIntentFilter.addAction("UESTATUES_MINE");  
	        //×¢²á¹ã²¥        
	        registerReceiver(myBroadcastReceiver, myIntentFilter);
            run();
	   }
	BroadcastReceiver myBroadcastReceiver = new BroadcastReceiver(){

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String result = intent.getStringExtra("UEresult"); 
			resultText.append("  "+result+"\r\n");
		}
	};
	final Handler handler = new Handler(){
	        @Override
	        public void handleMessage(final Message msg) {
	            switch (msg.what) {
	            case 0:			
	            	btnStart.setEnabled(true);
	                break;

	            default:
	                break;
	            }
	        }
	    };
	private void run() {
		// TODO Auto-generated method stub
		//1 read config file
		
		
	//	if(usingnewfile.isChecked())
	//	{			
	//	      try {
	//			fout =openFileOutput(fileName , MODE_PRIVATE);
	////		} catch (FileNotFoundException e) {
	//			// TODO Auto-generated catch block
	//			e.printStackTrace();
	//		}	
	//	}
	//	if(cleanPerformance.isChecked())
	//	{
	//		app = (RawData)getApplication(); 
	//		app.setCleanPerformance(true);
	//	}
		btnStart.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) {
				
				btnStart.setEnabled(false);
				final TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
				int netType = tm.getNetworkType();
				if(netType == 0)
				{
					Toast.makeText(AttachActivity.this, "Please turn off air plane mode firstly", Toast.LENGTH_LONG).show();
					return;
				}
				
				if(usingnewfile.isChecked())
				{			
				      try {
						fout =openFileOutput(fileName , MODE_PRIVATE);
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}	
				}
				if(cleanPerformance.isChecked())
				{
					app = (RawData)getApplication(); 
					app.setCleanPerformance(true);
				}
			        SharedPreferences outsharedPreferences= getSharedPreferences("myConfig",
							Activity.MODE_PRIVATE); 
					    String modeChangeDurationTimer =outsharedPreferences.getString("modeChangeDurationTimer", "");
					    String modeChangeTime =outsharedPreferences.getString("modeChangeTime", "");

					    if(!((modeChangeDurationTimer!=null)&&(modeChangeDurationTimer.length()!=0)))
					   {
						  modeChangeDurationTimer = "5000";
					   }
					   if(!((modeChangeTime!=null)&&(modeChangeTime.length()!=0)))
					   {
					  	modeChangeTime = "1";
					   }
					  
				   modeChangeTime = modeChangeTime.trim();
				   repeattime = Integer.parseInt(modeChangeTime);
				   modeChangeDurationTimer = modeChangeDurationTimer.trim();
				   timer = Integer.parseInt(modeChangeDurationTimer);
				   app = (RawData)getApplication();   
				   app.setIsTriggered(true);
				   app.setrepeattime(repeattime);
				// TODO Auto-generated method stub
			new Thread(new Runnable()
			{
				@Override
				public void run() {
					// TODO Auto-generated method stub
					while(repeattime>0)
					{
					try {
						Air.run(AttachActivity.this);
						Air.run(AttachActivity.this);
						repeattime--;
						app.setrepeattime(repeattime);
						Thread.sleep(timer);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}			
					}
					AttachActivity.this.handler.sendEmptyMessage(0);
				}
			}).start();
		}
		});
		btnStop.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				repeattime = 0;
				btnStart.setEnabled(true);
				try {
					Air.reset(AttachActivity.this);
					app.setrepeattime(0);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		});		
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK)
		{					
			unregisterReceiver(myBroadcastReceiver);
		}
		return super.onKeyDown(keyCode, event);
	}
	
}
