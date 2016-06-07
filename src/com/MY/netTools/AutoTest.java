package com.MY.netTools;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.MY.pingtest.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

@SuppressLint("HandlerLeak") public class AutoTest extends Activity {

	private WebView webView;
	private TextView AutoTestResult;
    private Timer mTimer;    
    private MyTimerTask mTimerTask;
    private boolean webViewIsLoading = false;
 //   private boolean stopWebLoading = false;
    private boolean stopTask = false;
    private boolean HttpWoring=false;
    private String tmp=" ";
    
	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver()
	{

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			Log.i("onReceive",action);
			if(action.equals("AUTOMESSAGE"))
			{
			   String result = intent.getStringExtra("result"); 
			   if(result!=null)
			   {
				  if(result.contains("restart"))
				{
					   AutoTestResult.append("restart"+"\n");
			//		   Intent intent2 = new Intent(AutoTest.this, clientService.class);  //
				        //启动FxService  
		//		       bindService(intent2,conn,Context.BIND_AUTO_CREATE);//yiweng
				}
			   if(result.contains("URLDONE"))
			   {
				   stopTask = true;
				   if(AutoTestResult != null)
				   AutoTestResult.append("URL DONE"+"\n");
			   }
			   if(result.contains("URLUP"))
			   {
				   String rt[] = result.split(";");
			   try {
				   Log.i("webView",rt[1]);
				   startURL(rt[1]);
			    } catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			   }
			   }
			   if(result.contains("PINGR"))
			   {
			//	   Log.i("PINGR",result);
		    // 	   String rt[] = result.split(";");
		           String s = result.replace("PINGR;", "");			   
				   if(AutoTestResult != null)
				   AutoTestResult.append(s+"\n");
			   }		   
			}
			   String result2 = intent.getStringExtra("show"); 
			   if((AutoTestResult != null)&&(result2!=null))
			   {
				SimpleDateFormat  formatter =   new SimpleDateFormat("yyyy.MM.dd    HH:mm:ss     ");       
				Date  curDate = new Date(System.currentTimeMillis()+clientService.systemTimeOffset);//获取当前时间       
				String ar =formatter.format(curDate);
			    AutoTestResult.append(result2);
			    int size = result2.length();
			    for(int i = size;i<30;i++)
			    {
			    AutoTestResult.append(" ");
			    }
			    AutoTestResult.append(ar+"\n");
			   }
			}
		}
		
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		 setContentView(R.layout.net_tool_aotutest);
		  IntentFilter myIntentFilter = new IntentFilter();  
	      myIntentFilter.addAction("AUTOMESSAGE");  
		 //	myIntentFilter.addAction("发送数据");
	     //注册广播        
	     registerReceiver(mBroadcastReceiver, myIntentFilter);
	     mTimer = new Timer(true);
	     webViewIsLoading = false;
	//     stopWebLoading = false;
	     stopTask = false;
	     AutoTestResult = (TextView) findViewById(R.id.AutoTestResult);
	     AutoTestResult.append("AutoTest Trace Start \n");
	     Button but = (Button) findViewById(R.id.AotuTestClear);
	     webView = (WebView)findViewById(R.id.AutoTestWebView);
	     
	     but.setOnClickListener(new OnClickListener()
	     {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				AutoTestResult.setText("");
//				try {
//					startURL("http://www.163.com");
//				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
			}
	    	 
	     });
	     
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		unregisterReceiver(mBroadcastReceiver);
		super.onDestroy();
	}

	protected void startURL(final String result) throws InterruptedException {
		// TODO Auto-generated method stub
		if(!result.contains("http://"))
		{
			tmp="http://"+result;
		}
		else
		{
			tmp = result;
		}
		new Thread(new Runnable()
		{

			@Override
			public void run() {
				// TODO Auto-generated method stub
		           HttpGet httpRequest = new HttpGet(tmp);
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
					} //在内网WLAN下测试下造成了吊死
			}
			
		}).start();


		stopTask = false;
		webViewIsLoading = false;
		new Thread(new Runnable()
		{

			@Override
			public void run() {
				// TODO Auto-generated method stub
				while(!stopTask)
				{
					if(!webViewIsLoading)
				{
				     if (mTimer != null){
				         if (mTimerTask != null){
				          mTimerTask.cancel();  //将原任务从队列中移除
				         }			        
				         mTimerTask = new MyTimerTask();  // 新建一个任务      
				         mTimer.schedule(mTimerTask, 10000);
				         }
				webViewIsLoading = true;
		//		webView.getSettings().setJavaScriptEnabled(true);  
		 //       webView.getSettings().setDomStorageEnabled(true);
		 //       webView.getSettings().setAllowFileAccessFromFileURLs(true);
		 //       webView.getSettings().setAllowUniversalAccessFromFileURLs(true);
		//		webView.loadUrl(tmp);
				webView.post(new Runnable() {
				    @Override
				    public void run() {
				    	AutoTest.this.handler.sendEmptyMessage(1);
				        webView.loadUrl(tmp);
						webView.setWebChromeClient(new WebChromeClient()
						{
							@Override
							public void onProgressChanged(WebView view, int newProgress) {
								// TODO Auto-generated method stub
								AutoTest.this.setProgress(newProgress);
								if (newProgress == 100) 
								{
								if(HttpWoring)
								{
								AutoTest.this.handler.sendEmptyMessage(2);
								Intent mIntent1 = new Intent("URLACK");
								webViewIsLoading = false;
							    mIntent1.putExtra("result", "DONE");    
							    sendBroadcast(mIntent1);	
								if (mTimerTask != null){
								 mTimerTask.cancel();  //将原任务从队列中移除
								}
					//			webViewIsLoading = false;
								}
								}
								super.onProgressChanged(view, newProgress);
							}
							
						});
				    }
				});
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Intent mIntent1 = new Intent("URLACK");
			    mIntent1.putExtra("result", "DONE");    
			    sendBroadcast(mIntent1);	
			    webViewIsLoading = false;
				}
			}	    
		}
			
		}).start();
			
	}

    class MyTimerTask extends TimerTask{

		@Override
		public void run() {
			// TODO Auto-generated method stub		
			AutoTest.this.handler.sendEmptyMessage(0);
		}
    }
    private final Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch(msg.what)
			{
			case 0:
			{
		//		    webViewIsLoading = false;
            	    String s="Time out(10s)\n";
            	    webViewIsLoading = false;
            	    AutoTestResult.append(s);
				    Intent mIntent1 = new Intent("URLACK");
				    mIntent1.putExtra("result", "DONE");    
				    sendBroadcast(mIntent1);
				    
            	}
			case 1:
			{
				 AutoTestResult.append("start\n");
			}
             break;
			case 2:
			{
				 AutoTestResult.append("end\n");
			}
             break;
             default:
            	 break;
			}
		}
		

	};
	
}
