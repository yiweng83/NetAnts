package com.MY.netTools;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.Iterator;

import com.MY.netTools.business.RawData;
import com.MY.netTools.business.ToolSrvImpl;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.StrictMode;
import android.telephony.TelephonyManager;
import android.util.Log;

public class SocketService extends Service {
	
	static Selector selector; 
//	public static SocketChannel socketChannel;
	private static final int PORT = 8189;  
	private static String IMEI;
	private static String ServierAddress;
	//private boolean rebootServiceForServerDown = false;
	File dir = new File(Environment.getExternalStorageDirectory().toString() + File.separator + "crash" + File.separator);  
	public RawData theapp;
	public static Object lock = new Object();
	public Object lockKA = new Object();
	
	private static int isAlive = 0;
	
	class TimeThread implements Runnable
	{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			while(true)
			{
    			 Intent mIntent = new Intent("SocketKA");
    	         mIntent.putExtra("data","SocketKA");    
    	         sendBroadcast(mIntent);	  	         
         //   }			
			try {
			//	synchronized(lockKA)
			//	{
			//	 lockKA.wait(5000);
			//	}
				 Thread.sleep(5000);
				 mHandler.sendEmptyMessage(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			}
		}
		
	}
	public static Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			
			switch (msg.what) {
			case 10:
			{
				if(isAlive == 12)
				{
				 ToolSrvImpl.STrace("KA","SocketService is alive");
				 synchronized(lock)
				 {
				 isAlive = 0;
				 }
				 
			 //     Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+ "18190710291"));
			//      startActivity(intent);
				}
				 synchronized(lock)
				 {
				isAlive++;
				 }
			}
			break;
			default:
				break;
			}
			
			super.handleMessage(msg);
		}
	
	};
	
	class mySocket implements Runnable
	{
		@Override
		public void run() {
			// TODO Auto-generated method stub
			do
			{
				NIOconnectServer();
                Object o = new Object();
                try {       	
            		synchronized(o)
            		{
					o.wait(4000);
            		}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}while((RawData.socketChannel == null)|| !RawData.socketChannel.isConnected());
	        try {  
	            while (true) {  
	                if((RawData.socketChannel != null))
	                {
	                	 ByteBuffer readBuffer = ByteBuffer.allocate(8192);
	                	 int ssize = 0;
	                	 if(( ssize= RawData.socketChannel.read(readBuffer))>0)
	                	 {
	                		 String NIOcontent = new String(readBuffer.array(),0, ssize);	
	              //  		 NIOcontent=NIOcontent+"\nffff;";
	               // 		 ToolSrvImpl.STrace("process",NIOcontent);
	                		 String[] raw = NIOcontent.split("\\\n");
	                		 
	                //		 NIOcontent +=  "\n";
	                //		 String[] raw = NIOcontent.split("");
	                		 int mSize = raw.length;
	                //		 Log.e("process",String.valueOf(mSize));
	                		for(int i = 0;i<raw.length;i++)
	                		 {
	                			 Log.e("process",raw[i]);
	                 //        Message msg;
	                //         msg = Message.obtain();
	                //         msg.what = 0;
	               //          msg.obj = NIOcontent;
	                         ToolSrvImpl.STrace("process",raw[mSize-1]+"\n");
	             			 Intent mIntent = new Intent("INCOMINGDATA");
	            	         mIntent.putExtra("data",raw[i]+"\n");    
	            	         sendBroadcast(mIntent);	
	            	         }
	                	 }
	                }	                
	                Thread.sleep(1000);
	            }  
	        } catch (Exception e) {  
	            e.printStackTrace();  
	        }  
		}
		
	}
	
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		
		ToolSrvImpl.STrace("process","onCreate SocketService");
        StrictMode.ThreadPolicy policy=new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        
        theapp = (RawData)getApplication();
        
   	   final TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
   	   IMEI = tm.getDeviceId();
   	   
       IntentFilter mFilter1 = new IntentFilter();
       mFilter1.addAction("serverCommand");
       registerReceiver(mReceiver1, mFilter1);
       
       IntentFilter mFilter = new IntentFilter();
       mFilter.addAction("SocketQeust");
       registerReceiver(mReceiver, mFilter);
       
	    SharedPreferences settings =this.getSharedPreferences("myConfig", 0);
	    ServierAddress = settings.getString("ServierAddress", "");
	    super.onCreate();
	//	do
	//	{
	//		NIOconnectServer();
	//	}while((RawData.socketChannel == null)|| !RawData.socketChannel.isConnected()); //不能再ONCREAT里面死循环，会导致无法起来
		
	    
	    Thread a = new Thread(new mySocket());
	    a.start();
	    
	    Thread b = new Thread(new TimeThread());
	    b.start();
	//	super.onCreate();
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		unregisterReceiver(mReceiver);
		unregisterReceiver(mReceiver1);
		super.onDestroy();
	}

	public void NIOconnectServer()
	{
		 NIOconnectServer(ServierAddress);
	}
    public  void NIOconnectServer(String IP)
    { 
     	if(clientService.stoprenconnect)
     	{
     		return;
     	}
 	//	SharedPreferences settings =this.getSharedPreferences("myConfig", 0);
 	//	String ServierAddress = settings.getString("ServierAddress", "");
     	try {
     		synchronized(clientService.socketLock)
     		{
     		if(selector != null)
     		{
     	    selector.close();
     		}
     		selector=SelectorProvider.provider().openSelector();  
     		RawData.socketChannel = SocketChannel.open();
     		RawData.socketChannel.configureBlocking(false);  
 	        SocketAddress socketAddress = new InetSocketAddress(IP,PORT);
 	        RawData.socketChannel.connect(socketAddress);
 	        if(selector.isOpen())
 	        {
 	        RawData.socketChannel.register(selector, SelectionKey.OP_CONNECT);  
                
 	             selector.select(5000);     
 	      //       ToolSrvImpl.STrace("2","2");
                 Iterator<SelectionKey> selectedKeys = selector.selectedKeys().iterator();  
                 if (selectedKeys.hasNext())  
                 {  
                     SelectionKey key = (SelectionKey) selectedKeys.next();  
                     selectedKeys.remove();  
                     if (key.isConnectable())   
                     {  
                        SocketChannel socketChannel = (SocketChannel) key.channel();  
                        socketChannel.finishConnect();    
               //         ToolSrvImpl.STrace("3","3");
                     }
                 }
                 else
                 {
              //  	 ToolSrvImpl.STrace("4","4");
                	 selector.close();
                	 if( RawData.socketChannel != null)
                	 {
                	 RawData.socketChannel.finishConnect();
          	    //     selector.close();
          	    //    socketChannel.socket().shutdownInput();
          	         RawData.socketChannel.close();
          	         RawData.socketChannel = null;
          	         }
                 }
 	        }
             //    ToolSrvImpl.STrace("5","5");
     		}
 	              	
 		} catch (IOException e) {
 			// TODO Auto-generated catch block
 			e.printStackTrace();
 		}
// 		NIOsendOut("SYSTEM;ATTACH;"+IMEI+";");	
 		if(!((RawData.socketChannel == null) || (!RawData.socketChannel.isConnected())))
 		{
 			sendMessageToClinetService("TASKDF+ServerDown",false);
 			if(RawData.socketChannel.isConnected())
 			NIOsendOut("SYSTEM;ATTACH;"+IMEI+";");
 		}
 		else
 		{
     //  	     selector.close();
        	 if( RawData.socketChannel != null)
        	 {
       //      if(RawData.socketChannel.isConnected())
		//	 RawData.socketChannel.finishConnect();
	    //    socketChannel.socket().shutdownInput();
      //       if(RawData.socketChannel.isOpen())
	   //      RawData.socketChannel.close();
	   //      RawData.socketChannel = null;
        	 }
 		}
 		
 	//	theapp.reconnectServer = false;
 	//	theapp.rebootServiceForServerDown = false;
     }

	private void sendMessageToClinetService(String string, boolean b) {
		// TODO Auto-generated method stub
		 Intent mIntent = new Intent("INCOMINGDATA");
		 String x;
		 if(b)
		  x = "T";
		 else
		  x = "F";
		 String data = "INTRA;"+string+x;
         mIntent.putExtra("data",data);    
         sendBroadcast(mIntent);	
	}

	public  void NIOsendOut(String string) {
		// TODO Auto-generated method stub
		  if((RawData.socketChannel == null) || (!RawData.socketChannel.isConnected()))
				return;	  
		  
		  String data = string+"\n";//debug
		  byte[] bytes = data.getBytes();
		  ByteBuffer buffer = ByteBuffer.wrap(bytes);
		  try {
	//		  if(RawData.socketChannel.isConnected())
			  RawData.socketChannel.write(buffer);
			if(!string.contains("KA"))//617
			ToolSrvImpl.STrace("SENDOUT",string);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			ToolSrvImpl.STrace("SENDOUT","IOException has been thrown previously"+"  "+string);
			//send broadcast to clientService
			sendMessageToClinetService("TASKDF+ServerDown",true);
     	//	synchronized(RawData.Tlock)
     //		{
     			
     		//	theapp = (RawData)getApplication(); 
     		//	theapp.setrebootServiceForTaskDF(true);
     		//	theapp.setrebootServiceForServerDown(true);
     		//	theapp.rebootServiceForServerDown = true;
   //  	//		theapp.reconnectServer = true;
     	//	}
     		
    	//	Log.e("check","RawData.rebootServiceForServerDown : "+String.valueOf(theapp.getrebootServiceForTaskDF())+
    	//			"  reconnectServer: "+String.valueOf(theapp.getrebootServiceForServerDown())
    	//			);
			e.printStackTrace();
		}
	}
	public static boolean disconnectServer()
	{
		if(RawData.socketChannel != null)
		{
			try {
				RawData.socketChannel.socket().shutdownInput();
				RawData.socketChannel.close();
				RawData.socketChannel = null;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}
	
	public static boolean isSocketWokring()
	{
		if((RawData.socketChannel == null)||!RawData.socketChannel.isConnected())
		{
		   return false;
		}
		return true;
	}	
	
	private BroadcastReceiver mReceiver = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			if(action.equals("SocketQeust"))
			{
            	String result = intent.getStringExtra("data"); 
            	NIOsendOut(result);
			}
		}	
	};
	
	private BroadcastReceiver mReceiver1 = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			if(action.equals("serverCommand"))
			{
            	String result = intent.getStringExtra("data"); 
                 if(result.equals("DisconnnetServer"))
                 {
                	 ToolSrvImpl.STrace("process","start disconnectServer");
               // 	 Log.e("DisconnnetServer","DisconnnetServer");
                	 disconnectServer();
                 }
                 else if(result.equals("connnetServer"))
                 {
               // 	 Log.e("connnetServer","connnetServer");
                	 ToolSrvImpl.STrace("process","start connnetServer");
                	 ToolSrvImpl.DeleteFile(dir);
           //     	 String triggerCoredump = null;
           //     	 triggerCoredump.length();
                	 
                	 
            		 Intent mIntent = new Intent("SocketQeustReStart");
                     mIntent.putExtra("data","restart");    
                     sendBroadcast(mIntent);	
                //	 stopSelf();
                //	 if(!SocketService.isSocketWokring())
               // 	 NIOconnectServer();
                 }
			}
		}

		private ServiceConnection getServiceConnection() {
			// TODO Auto-generated method stub
			return null;
		}	
	};
}
