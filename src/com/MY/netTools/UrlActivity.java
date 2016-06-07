package com.MY.netTools;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;


import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.MY.netTools.PingActivity.MyTimerTask;
import com.MY.netTools.business.ToolSrv;
import com.MY.netTools.business.ToolSrvImpl;
import com.MY.pingtest.R;

public class UrlActivity extends Activity {

	private Button btn;

	private WebView webView;

	private EditText editText;

	private ProgressDialog progressDialog;
	
	private CheckBox isNewfile;

	private ToolSrv srv = new ToolSrvImpl();
	
	protected static FileOutputStream fout = null;
	private final StringBuffer report2file = new StringBuffer();
	
    private Timer mTimer;
    
    private MyTimerTask mTimerTask;
    private boolean HttpWoring=false;
    private boolean AutoTest = false;
	private String fileName = "urlreport";
	File file = new File(Environment.getExternalStorageDirectory()+"/NetAnts", fileName);

	@SuppressLint("NewApi") @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.net_tool_url);
		srv.addObserver(observer);
		mTimer = new Timer(true);
		
   //     IntentFilter mFilter1 = new IntentFilter();
 //       mFilter1.addAction("URLDOWN");
 //   //    registerReceiver(mReceiver, mFilter1);
	 //    IntentFilter mFilter1 = new IntentFilter();
	//	 mFilter1.addAction("URL");
	///	 registerReceiver(mReceiver1, mFilter1);
        StrictMode.ThreadPolicy policy=new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

		editText = (EditText) this.findViewById(R.id.url_ip);
		webView = (WebView)this.findViewById(R.id.url_show);
		btn = (Button) this.findViewById(R.id.url_btn);
		isNewfile = (CheckBox)this.findViewById(R.id.checkBox_Ulr);//随便写的
		SharedPreferences outsharedPreferences= getSharedPreferences("myConfig",
				Activity.MODE_PRIVATE); 
		String mUrlAddress =outsharedPreferences.getString("mUrlAddress", "");
		if(!((mUrlAddress!=null)&&(mUrlAddress.length()!=0)))
		{
			mUrlAddress = "www.163.com";
		}
		editText.setText(mUrlAddress);
        isNewfile.setOnCheckedChangeListener(new OnCheckedChangeListener()
        {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				if(isChecked)
				{
		       	 try {
		//    		 fout =openFileOutput(fileName , MODE_PRIVATE);//不覆盖
		       		 fout= new FileOutputStream(file); 
		    		  //   	fout =openFileOutput(fileName,MODE_PRIVATE);
		    		 } catch (FileNotFoundException e) {
		    		 // TODO Auto-generated catch block
		    		 e.printStackTrace();
		    		 }
				}
				
			} 
        });	
	 try {
		// fout =openFileOutput(fileName , MODE_APPEND);//不覆盖
		 fout= new FileOutputStream(file,true); 
		  //   	fout =openFileOutput(fileName,MODE_PRIVATE);
		 } catch (FileNotFoundException e) {
		 // TODO Auto-generated catch block
		 e.printStackTrace();
		 }   
		btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View v) {
			     if (mTimer != null){
			         if (mTimerTask != null){
			          mTimerTask.cancel();  //将原任务从队列中移除
			         }			        
			         mTimerTask = new MyTimerTask();  // 新建一个任务      
			         mTimer.schedule(mTimerTask, 10000);
			         }
				progressDialog = ProgressDialog.show(UrlActivity.this, "", "Loading……");
				final long startTime = System.currentTimeMillis();
				btn.setEnabled(false);
				final String urlStr = editText.getText().toString();//记住写http:// 不然打不开
				String tmp=" ";
				if(!urlStr.contains("http://"))
				{
					tmp="http://"+urlStr;
				}
				else
				{
					tmp = urlStr;
				}
	/*			HttpGet httpRequest = new HttpGet(tmp);
				try {
					HttpResponse httpResponse = new DefaultHttpClient().execute(httpRequest);
			          if(httpResponse.getStatusLine().getStatusCode() == 200)   
			          {   
			        	  HttpWoring = true;
			          }  
			          else  
			          {  
			        	  HttpWoring = false;  	  
			          }  
				} catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}*/ //在内网WLAN下测试下造成了吊死
				webView.loadUrl(tmp);
				webView.setWebChromeClient(new WebChromeClient()
				{

					@Override
					public void onProgressChanged(WebView view,
							int newProgress) {
						// TODO Auto-generated method stub
						UrlActivity.this.setProgress(newProgress);
						if (newProgress == 100) 
						{
			//			if(HttpWoring)
						{
						SimpleDateFormat  formatter =   new SimpleDateFormat("yyyy.MM.dd    HH:mm:ss     ");       
						Date  curDate = new Date(startTime);//获取当前时间       
						String str = "new URL report start time : "+formatter.format(curDate);   	
						try {
							fout.write(str.getBytes());
						} catch (IOException e) {
								// TODO Auto-generated catch block
							e.printStackTrace();
						}
						long usedTime = (System.currentTimeMillis() - startTime);
						String s = "Loading "+urlStr+" use "+ String.valueOf(usedTime) +" millis sec"+"\r\n"+"\r\n";
						Toast.makeText(UrlActivity.this, s, Toast.LENGTH_LONG).show();
						try {
							fout.write(s.getBytes());
							Intent mIntent1 = new Intent("URLACK");
					        mIntent1.putExtra("result", "DONE");    
					        sendBroadcast(mIntent1);	
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
			               if (progressDialog.isShowing()) {
			            	   btn.setEnabled(true);
			                   progressDialog.dismiss();
			                }
					         if (mTimerTask != null){
						          mTimerTask.cancel();  //将原任务从队列中移除
						         }
						}
						}
						super.onProgressChanged(view, newProgress);
					}
					
				});
				
			  
			}
		});
		if(AutoTest)
		{
		btn.post(new Runnable(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				btn.performClick();
			}
			
		});
		}
	}
	
	private BroadcastReceiver mReceiver = new BroadcastReceiver()
	{

		@TargetApi(Build.VERSION_CODES.JELLY_BEAN) @Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();

			if(action.equals("URLDOWN"))
			{
	//			Log.i("fffff","fasdfadsfa");
	//		    final ViewTreeObserver vto = btn.getViewTreeObserver();
	//		    vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
	//		        @Override
	//		        public void onGlobalLayout() {
	//		        	Log.i("fffff","ddff");
	//		            vto.removeOnGlobalLayoutListener(this);
	//		            btn.performClick();
	//		        }
	//		    });
				
			}
		}
		
	};
	private final Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch(msg.what)
			{
			case 0:
            	if(progressDialog != null)
            	{
               if (progressDialog.isShowing()) {
            	    String s="Time out(10s)";
            	    Toast.makeText(UrlActivity.this, s, Toast.LENGTH_LONG).show();
            	    try {
						fout.write(s.getBytes());
						Intent mIntent1 = new Intent("URLACK");
				        mIntent1.putExtra("result", "DONE");    
				        sendBroadcast(mIntent1);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
            	    btn.setEnabled(true);
                    progressDialog.dismiss();
                }
            	}
                break;
			case 1:

				break;
             default:
            	 break;
			}
		}
		

	};
	
	Observer observer = new Observer() {
		public void urlPageChange(int status){
			UrlActivity.this.handler.sendEmptyMessage(status);
		};
	};
    class MyTimerTask extends TimerTask{

		@Override
		public void run() {
			// TODO Auto-generated method stub		
			UrlActivity.this.handler.sendEmptyMessage(0);
		}
    }
    
	private BroadcastReceiver mReceiver1 = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			String data = intent.getStringExtra("data"); 
			Log.e("URL",data);
			if(action.equals("URL"))
			{
				WebView webView = new WebView(UrlActivity.this);
				final long t1 = System.currentTimeMillis();
				Log.e("URL1",data);
				webView.loadUrl(data);
				webView.setWebChromeClient(new WebChromeClient(){

					@Override
					public void onProgressChanged(WebView view, int newProgress) {
						// TODO Auto-generated method stub
						super.onProgressChanged(view, newProgress);
						if (newProgress == 100) 
						{
							long t = System.currentTimeMillis() - t1;
				   		    Intent mIntent = new Intent("URLACKDATA");
				            mIntent.putExtra("data",String.valueOf(t));    
				            sendBroadcast(mIntent);
						}
					}
					
				});
			}
		}	
	};
}
