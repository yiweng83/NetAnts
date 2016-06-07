package com.MY.netTools;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.MY.pingtest.R;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Fxservice extends Service{
    
	private final String ACTION_NAME1 = "发送数据";
	private final StringBuffer result = new StringBuffer();
	 //定义浮动窗口布局  
    LinearLayout mFloatLayout;  
    WindowManager.LayoutParams wmParams;  
    //创建浮动窗口设置布局参数的对象  
    WindowManager mWindowManager;  
      
    TextView down;
    TextView up;
      
    
    
    private static final String TAG = "FxService";
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		createFloatView();
		
        
		registerBoradcastReceiver();//注册广播接收
		MyTask task=new MyTask();//运行异步任务，执行对应脚本
	    task.execute(1000);
	}
	

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mWindowManager.removeViewImmediate(mFloatLayout);
		unregisterReceiver(mBroadcastReceiver);
	}



	BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver(){

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String result = intent.getStringExtra("result"); 
		
			
            String sin = "";
            String sout ="";
            int ssize = result.length();
            
            String r = runFtp.normalizationgSriptsInput(result);
            

           String[] s = r.split(";");    
           down.setText(s[2]);
           up.setText(s[3]);
   //         Log.e("e",result);
            
       /*     boolean lastIsSpace = false;
            ArrayList<String> k = new ArrayList<String>();
            for(int i=0;i<ssize;i++)
            {
            	if(result.charAt(i)==' ')
            	{
            		if(lastIsSpace)
            		{
            			lastIsSpace = true;
            		}
            		else
            		{
            			if(i!=0)
            			{
            			k.add(";");
            			lastIsSpace = true;
            			}
            		}
            	}
            	else
            	{
            		lastIsSpace = false;
            		k.add(String.valueOf(result.charAt(i)));
            	}
            }
            String[] s = (String[])k.toArray(new String[0]);
            
            String str = "";
            for(String sk : s){
                    str+=sk;
            }
            
      //      str="rmnet0:;52575007;90059;0;0;0;0;0;0;12907064;111737;0;0;0;0;0;0;rmnet0:;52579369;90070;0;0;0;0;0;0;12908792;111751;0;0;0;0;0;0";//for sony with out grep
            String[] resultarray=str.split(";");
            
 //rmnet0:;52575007;90059;0;0;0;0;0;0;12907064;111737;0;0;0;0;0;0;rmnet0:;52579369;90070;0;0;0;0;0;0;12908792;111751;0;0;0;0;0;0  
//            52649816 12962812 52651163 12965072
          
            String oldin ;
            String oldout;
            String newin;
            String newout;
            if(str.contains("lo"))
            {             
                oldin = resultarray[2].trim();
                oldout=resultarray[10].trim();
                newin=resultarray[19].trim();
                newout=resultarray[27].trim();
                Log.e("e",oldin+" "+oldout+" "+newin+" "+newout);
            }
            else
            {
            oldin = resultarray[1].trim();
            oldout=resultarray[9].trim();
            newin=resultarray[18].trim();
            newout=resultarray[26].trim();
            }
            
              float oldin1 = Long.parseLong(oldin);
              float oldout1= Long.parseLong(oldout);
              float in = (Long.parseLong(newin) - oldin1)*8/1024;
              float out = (Long.parseLong(newout) - oldout1)*8/1024;
              String sin = String.format("%.2f", in)+"Kb/s";
              String sout =String.format("%.2f", out)+"Kb/s";
                      
             down.setText(sin);
             up.setText(sout);*/
		}
	};

	private void registerBoradcastReceiver() {
		// TODO Auto-generated method stub
		IntentFilter myIntentFilter = new IntentFilter();  
        myIntentFilter.addAction(ACTION_NAME1);  
        //注册广播        
        registerReceiver(mBroadcastReceiver, myIntentFilter);  //yiweng for test
	}
	
	@SuppressWarnings("static-access")
	private void createFloatView() {
		// TODO Auto-generated method stub
		 wmParams = new WindowManager.LayoutParams();  
	        //获取的是WindowManagerImpl.CompatModeWrapper  
	        mWindowManager = (WindowManager)getApplication().getSystemService(getApplication().WINDOW_SERVICE);  
	        Log.i(TAG, "mWindowManager--->" + mWindowManager);  
	        //设置window type  
	        wmParams.type = LayoutParams.TYPE_PHONE;   
	        //设置图片格式，效果为背景透明  
	        wmParams.format = PixelFormat.RGBA_8888;   
	        //设置浮动窗口不可聚焦（实现操作除浮动窗口外的其他可见窗口的操作）  
	        wmParams.flags = LayoutParams.FLAG_NOT_FOCUSABLE;        
	        //调整悬浮窗显示的停靠位置为左侧置顶  
	        wmParams.gravity = Gravity.RIGHT | Gravity.TOP;         
	        // 以屏幕左上角为原点，设置x、y初始值，相对于gravity  
	        wmParams.x = 0;  
	        wmParams.y = 0;  
	  
	        //设置悬浮窗口长宽数据    
	        wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;  
	        wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;  
	  
	         /*// 设置悬浮窗口长宽数据 
	        wmParams.width = 200; 
	        wmParams.height = 80;*/  
	     
	        LayoutInflater inflater = LayoutInflater.from(getApplication());  
	        //获取浮动窗口视图所在布局  
	        mFloatLayout = (LinearLayout) inflater.inflate(R.layout.float_layout, null);  
	        //添加mFloatLayout  
	        mWindowManager.addView(mFloatLayout, wmParams);  
	        //浮动窗口按钮  
	        down = (TextView)mFloatLayout.findViewById(R.id.textView_down);
	        up = (TextView)mFloatLayout.findViewById(R.id.textView_up);
	          
	        mFloatLayout.measure(View.MeasureSpec.makeMeasureSpec(0,  
	                View.MeasureSpec.UNSPECIFIED), View.MeasureSpec  
	                .makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));  
	}
	
	
	
	class MyTask extends AsyncTask<Integer,String,String>{
		
		public MyTask() {
			// TODO Auto-generated constructor stub
		}
		
		
		@Override
		protected String doInBackground(Integer... params) {
			// TODO Auto-generated method stub
		    String workdirectory="/data/local/tmp";
		    String path="/data/local/tmp";
		//	String[] getrmnet={"netcfg"};
			String rmnet=null;
		    
	/*	    try {
				String result=run(getrmnet,path);
				String[] resultstr=result.split("0x");
				int k=0;
				while(k<resultstr.length){
					if(resultstr[k].contains("00000041")){
						String str=resultstr[k-1];
						String[] strarray=str.split("UP");
						String[] strarray1=strarray[0].split("\n");
						rmnet=strarray1[1].trim();
						k=k+1;
					}else{
						k=k+1;
					}
				}
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}*/
		    rmnet = "eth0";//yiweng hardcode for sumulater
		    String[] cmd={"sh ","/storage/emulated/0/netspeed", rmnet,"1"};
		    
		    /*
		     * 循环运行netspeed脚本，直到用户停止
		    */
		   try { 
			   while(true){
				   String runresult;
                   String cmdPing = "sh /data/data/com.MY.pingtest/netspeed rmnet0 1";
                   Process p = Runtime.getRuntime().exec(cmdPing);
                   BufferedReader bufferReader = new BufferedReader(new InputStreamReader(p.getInputStream()));
                   while ((runresult = bufferReader.readLine()) != null) {
               // 	   Log.i("check eth0",runresult);//yiweng
                	   result.append(runresult.toString()).append("\r\n");
                	   }
		//		String runresult=run(cmd,workdirectory);
				
				Intent mIntent = new Intent(ACTION_NAME1);
                mIntent.putExtra("result", result.toString());    
                sendBroadcast(mIntent);
                
        		Intent mIntent1 = new Intent("URLACK");
                mIntent1.putExtra("result", "Speed"+result.toString());    
                sendBroadcast(mIntent1);
                
                result.delete(0, result.length());
                Thread.sleep(1000);
                }
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			return null;
		}  
		
		public void registerBoradcastReceiver() {
			// TODO Auto-generated method stub
			IntentFilter myIntentFilter = new IntentFilter();  
	        myIntentFilter.addAction(ACTION_NAME1);        
		}
		
		
		/*
		 * 执行linux指令，并返回输出结果
		 */
	  public synchronized String run(String[] cmd, String workdirectory) 
			throws IOException { 
			StringBuffer result = new StringBuffer(); 
			try { 
			ProcessBuilder builder = new ProcessBuilder(cmd); 
			InputStream in = null; 
			if (workdirectory != null) { 
	//		builder.directory(new File(workdirectory));  //yiweng
	//		builder.redirectErrorStream(true); 
			Process process = builder.start(); 
			in = process.getInputStream(); 
			byte[] re = new byte[1024]; 
			while (in.read(re) != -1) { 
			result = result.append(new String(re)); 
			} 
			} 
			if (in != null) { 
			in.close(); 
			} 
			} catch (Exception ex) { 
			ex.printStackTrace(); 
			} 
			return result.toString(); 
			}
	}
}
