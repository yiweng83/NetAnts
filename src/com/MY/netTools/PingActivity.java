package com.MY.netTools;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.MY.netTools.business.RawData;
import com.MY.netTools.business.ToolSrv;
import com.MY.netTools.business.ToolSrvImpl;
import com.MY.pingtest.R;

@SuppressLint({ "WorldReadableFiles", "WorldWriteableFiles" }) public class PingActivity extends Activity {

    protected static FileOutputStream fout = null;

	private Button btn;

	private Button btnClean;
    private EditText editText;

    private TextView resultView;
    
    private TextView showPingConfig;

    private ProgressDialog progressDialog;

    private final ToolSrv srv = new ToolSrvImpl();

    private final StringBuffer result = new StringBuffer();
    
    private final StringBuffer report2file = new StringBuffer();
    
    private Timer mTimer;
    
    public RawData app;
    private MyTimerTask mTimerTask;
    String fileName = "IPreport";
	//create new file for IP report
    File file = new File(Environment.getExternalStorageDirectory()+"/NetAnts", fileName);

/*    public void onToggleClicked(View view) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        // Is the toggle on?

        if (false) {

            // Enable data
            ConnectivityManager dataManager;
            dataManager  = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
            Method dataMtd = ConnectivityManager.class.getDeclaredMethod("setMobileDataEnabled", boolean.class);
            dataMtd.setAccessible(true);
            dataMtd.invoke(dataManager, true);        //True - to enable data connectivity .


        } else {
            // Disable data

            ConnectivityManager dataManager;
            dataManager  = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
            Method dataMtd = ConnectivityManager.class.getDeclaredMethod("setMobileDataEnabled", boolean.class);
            dataMtd.setAccessible(true);
            dataMtd.invoke(dataManager, false);        //True - to enable data connectivity .


        }
    }*/
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1) @SuppressLint("InlinedApi") @SuppressWarnings("deprecation")
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.net_tool_ping);
        srv.addObserver(observer);
        mTimer = new Timer(true);

		 app = (RawData)getApplication();
		 app.exitAppUtils.addActivity(PingActivity.this);
        try {
       //	fout =openFileOutput(fileName , MODE_APPEND);//不覆盖
            fout= new FileOutputStream(file,true); 
         
     //   	fout =openFileOutput(fileName,MODE_PRIVATE);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}   
        resultView = (TextView) findViewById(R.id.ping_result);
        showPingConfig = (TextView)findViewById(R.id.showPingConfig);
        editText = (EditText) findViewById(R.id.ping_ip);
        btn = (Button) findViewById(R.id.ping_btn);
        btnClean=(Button) findViewById(R.id.ping_clean_btn);
        
		SharedPreferences outsharedPreferences= getSharedPreferences("myConfig",
				Activity.MODE_PRIVATE); 
		String mPingPackageSize =outsharedPreferences.getString("mPingPackageSize", "");
		String mPingCount =outsharedPreferences.getString("mPingCount", "");
		String mPingTimer =outsharedPreferences.getString("mPingTimer", "");
		String mPingAddress =outsharedPreferences.getString("mPingAddress", "");
		if(!((mPingPackageSize!=null)&&(mPingPackageSize.length()!=0)))
		{
			mPingPackageSize = "250";
		}
		if(!((mPingCount!=null)&&(mPingCount.length()!=0)))
		{
			mPingCount = "1";
		}
		if(!((mPingTimer!=null)&&(mPingTimer.length()!=0)))
		{
			mPingTimer = "100";
		}
		if(!((mPingAddress!=null)&&(mPingAddress.length()!=0)))
		{
			mPingAddress = "www.163.com";
		}
		String show = "Package size : "+ mPingPackageSize + " Repeat count : "+mPingCount+" Sleep timer :"+mPingTimer;
		showPingConfig.setText(show);
    /*    ConnectivityManager dataManager;
        dataManager  = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        Method dataMtd;
		try {
			dataMtd = ConnectivityManager.class.getDeclaredMethod("setMobileDataEnabled", boolean.class);
	        dataMtd.setAccessible(true);
	        dataMtd.invoke(dataManager, false);
		} catch (NoSuchMethodException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
*/
        
    //    Settings.System.putString(getContentResolver(), android.provider.Settings.Global.AIRPLANE_MODE_ON , "1"); 

        btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View v) {
            
            	btn.setEnabled(false);
                progressDialog = ProgressDialog.show(PingActivity.this, "", "Loading……");
       //     	 final RawData app = (RawData)getApplication(); 
       //     	 app.setIpData("A");
       //     	 Log.i("check",app.getIpData());
				SimpleDateFormat  formatter =   new SimpleDateFormat("yyyy.MM.dd    HH:mm:ss     ");       
				Date  curDate = new Date(System.currentTimeMillis());//获取当前时间       
				String str = "new IP report start time : "+formatter.format(curDate) + "\r\n";   	
				try {
					if(fout != null)
					fout.write(str.getBytes());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				SharedPreferences outsharedPreferences= getSharedPreferences("myConfig",
						Activity.MODE_PRIVATE); 
				String mPingPackageSize =outsharedPreferences.getString("mPingPackageSize", "");
				String mPingCount =outsharedPreferences.getString("mPingCount", "");
				String mPingTimer =outsharedPreferences.getString("mPingTimer", "");
				String mPingAddress =outsharedPreferences.getString("mPingAddress", "");
				if(!((mPingPackageSize!=null)&&(mPingPackageSize.length()!=0)))
				{
					mPingPackageSize = "250";
				}
				if(!((mPingCount!=null)&&(mPingCount.length()!=0)))
				{
					mPingCount = "1";
				}
				if(!((mPingTimer!=null)&&(mPingTimer.length()!=0)))
				{
					mPingTimer = "100";
				}
				if(!((mPingAddress!=null)&&(mPingAddress.length()!=0)))
				{
					mPingAddress = "www.163.com";
				}
		///		editText.setText(mPingAddress);
		//		Toast.makeText(PingActivity.this, mPingPackageSize +mPingCount+mPingTimer+mPingAddress , Toast.LENGTH_LONG).show();
				String tmp = editText.getText().toString();
				if((tmp!=null)&&(tmp.length()!=0))
				{
					mPingAddress = tmp;
				}
			     if (mTimer != null){
			         if (mTimerTask != null){
			          mTimerTask.cancel();  //将原任务从队列中移除
			         }
			         
			         mTimerTask = new MyTimerTask();  // 新建一个任务      
			         mTimer.schedule(mTimerTask, 10000);
			         }
                srv.ping(mPingAddress,mPingPackageSize,mPingCount,mPingTimer);
            }
        });
        
        btnClean.setOnClickListener(new OnClickListener()
        {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
			      try {
				//	fout =openFileOutput("IPreport" , MODE_PRIVATE);
			    	  fout= new FileOutputStream(file,false); 
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	
			}
        	
        });

    }

    private final Handler handler = new Handler(){
        @Override
        public void handleMessage(final Message msg) {
            switch (msg.what) {
            case 0:			
            	if(result!=null)
            	{
	            resultView.setText(result.toString());
            	}
            	if(progressDialog != null)
            	{
               if (progressDialog.isShowing()) {
            	   btn.setEnabled(true);
                    progressDialog.dismiss();
                }
            	}
                break;
            case 1:
                result.delete(0, result.length());
                break;
            case 2:
            	if(progressDialog != null)
            	{
               if (progressDialog.isShowing()) {
            	   String s = "ping time out(10s)";
            	   resultView.setText(s);
            	   try {
            		   if(fout != null)
					fout.write(s.getBytes());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            	   btn.setEnabled(true);
                   progressDialog.dismiss();
                }
            	}
                break;
            default:
                break;
            }
        }
    };

    private final Observer observer = new Observer() {
        @Override
        public void pingPageChange(String str, int status) {
        	//这里存储数据
        	 final RawData app = (RawData)getApplication(); 
        	 app.setIpData(str);
      //  	 Log.i("check",app.getIpData());
             result.append(str).append("\r\n");
             PingActivity.this.handler.sendEmptyMessage(0);
        };

        @Override
        public void cleanScreen() {
            PingActivity.this.handler.sendEmptyMessage(1);
        }

		@Override
		public void passLastR(String str) {
			// TODO Auto-generated method stub
			try {
	/*			fout =openFileOutput("IPreport",MODE_PRIVATE);   //how to clean the old file
				String s = "";
				fout.write(s.toString().getBytes());
				fout.close();
				fout =openFileOutput("IPreport",MODE_APPEND);*/
				report2file.append(str).append("\r\n");
				if(fout != null)
				fout.write(report2file.toString().getBytes());
				Log.i("check",report2file.toString());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
        
    };
    class MyTimerTask extends TimerTask{

		@Override
		public void run() {
			// TODO Auto-generated method stub			
			 PingActivity.this.handler.sendEmptyMessage(2);
		}
    }
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		 app.exitAppUtils.delActivity(PingActivity.this);
		super.onDestroy();
		
	}
    
}
