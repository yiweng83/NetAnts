package com.MY.netTools;

import it.sauronsoftware.ftp4j.FTPAbortedException;
import it.sauronsoftware.ftp4j.FTPClient;
import it.sauronsoftware.ftp4j.FTPDataTransferException;
import it.sauronsoftware.ftp4j.FTPDataTransferListener;
import it.sauronsoftware.ftp4j.FTPException;
import it.sauronsoftware.ftp4j.FTPIllegalReplyException;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;


import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.NumberPicker.Formatter;
import android.widget.NumberPicker.OnScrollListener;
import android.widget.NumberPicker.OnValueChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.MY.netTools.business.RawData;
import com.MY.netTools.business.ToolSrv;
import com.MY.netTools.business.ToolSrvImpl;
import com.MY.netTools.business.myMessage;
import com.MY.pingtest.R;

public class MailActivity extends Activity {
    public class MutiTaskData
    {
		public class FTPDLPROFILE
		{
			public String address = "";
			public String user = "";
			public String pass = "";
			public String file = "";
			FTPDLPROFILE(String a,String b,String c,String d)
			{
				address= a;
				user = b;
				pass = c;
				file = d;
			}
		}
		public class FTPULPROFILE
		{
			public String address = "";
			public String user = "";
			public String pass = "";
			FTPULPROFILE(String a,String b,String c)
			{
				address= a;
				user = b;
				pass = c;			
			}
		}
		public class PINGPROFILE
		{
			public String address = "";
			public String c = "";
			public String m = "";
			PINGPROFILE(String a,String b,String d)
			{
				address= a;
				c = b;
				m = d;			
			}
		}

    	public int status;
    	public int loop = -99;
    	public String describer = "";
    	FTPDLPROFILE DL;
    	FTPULPROFILE UL;
    	PINGPROFILE PING;
    	
    	
    	MutiTaskData(int a,int b)
    	{
    		status = a;
    		loop = b;
    		initProfile();
    	}
        public String toString()
        {
        	String s = " status "+status+" loop "+loop;
			return s;
        	
        }
    	
    	
		private void initProfile() {
			// TODO Auto-generated method stub
			SharedPreferences settings = MailActivity.this.getSharedPreferences("myConfig", 0);
			switch(status)
			{
			case 1:   //DL
			{
							
				DL = new FTPDLPROFILE(settings.getString("address", ""),settings.getString("user", ""),
						settings.getString("password", ""),settings.getString("FtpFileName",""));
				describer = "DL ";
			}
		    break;
			case 2:   //UL
			{
				UL = new FTPULPROFILE(settings.getString("address", ""),settings.getString("user", ""),
						settings.getString("password", ""));
				describer = "UL ";
			}
		    break;
			case 3:
			{
				DL = new FTPDLPROFILE(settings.getString("address", ""),settings.getString("user", ""),
						settings.getString("password", ""),settings.getString("FtpFileName",""));
				UL = new FTPULPROFILE(settings.getString("address", ""),settings.getString("user", ""),
						settings.getString("password", ""));
				describer = "DL+UL ";
			}
				break;
			case 4:   //PING
			{		
				PING = new PINGPROFILE(settings.getString("mPingAddress", ""),
						settings.getString("mPingCount", ""),settings.getString("mPingPackageSize", ""));
				describer = "PING ";
			}
		    break;
			case 5:
			{
				DL = new FTPDLPROFILE(settings.getString("address", ""),settings.getString("user", ""),
						settings.getString("password", ""),settings.getString("FtpFileName",""));
				PING = new PINGPROFILE(settings.getString("mPingAddress", ""),
						settings.getString("mPingCount", ""),settings.getString("mPingPackageSize", ""));
				describer = "DL+PING ";
			}
				break;
			case 6:
			{
				UL = new FTPULPROFILE(settings.getString("address", ""),settings.getString("user", ""),
						settings.getString("password", ""));
				PING = new PINGPROFILE(settings.getString("mPingAddress", ""),
						settings.getString("mPingCount", ""),settings.getString("mPingPackageSize", ""));
				describer = "UL+PING ";
			}
				break;	
			case 7:
			{
				DL = new FTPDLPROFILE(settings.getString("address", ""),settings.getString("user", ""),
						settings.getString("password", ""),settings.getString("FtpFileName",""));
				UL = new FTPULPROFILE(settings.getString("address", ""),settings.getString("user", ""),
						settings.getString("password", ""));
				PING = new PINGPROFILE(settings.getString("mPingAddress", ""),
						settings.getString("mPingCount", ""),settings.getString("mPingPackageSize", ""));
				describer = "DL+UL+PING ";
			}
				break;	
		    default:
		    	break;
			}
		}
    };
    private static Object pv = new Object();
    private static Object pvDL = new Object();
  //  private static Object DL = new Object();
    private static Object pvUL = new Object();
//    private static Object UL = new Object();
    private static Object pvPing = new Object();
//    private static Object Ping = new Object();
    private static boolean modeSynchronism = true;

    private static int loopT = -99;
    private List<MutiTaskData> thelist = new ArrayList<MutiTaskData>();
	private Button btn;
	private Button stopbtn;
	private Button selectbtn;
	private Button deletebtn;
	private TextView textview;
	private TextView textview1;
	private TextView textview2;
	private NumberPicker numberPicker;
	private static final String SAVESTRING_INFOS = "myConfig";
	
	private List<String> list = new ArrayList<String>();
 
	private ListView listview;
    private ArrayAdapter<String> adapter;
    
	boolean checked0 = false;
	boolean checked1 = false;
	boolean checked2 = false;
	boolean ThreadWorking = false;
	
	int status = 0;
	int endstatus = 0;
	private FTPClient[] dl = new FTPClient[10];
	private FTPClient[] ul = new FTPClient[10];
//	boolean dlIsWorking = false;
//	boolean ulIsWorking = false;
	boolean pingIsWorking = false;
	Process p;
	 
	public int FtpLoopNum = 0;
	public int FtpLoopTime = 0;
	private boolean foucedown = false;
	
	private String Textview1String = "";
	
	private int dlThread = 1;
	private int ulThread = 1;
	private int pingThread  = 1;
	

	
	public class ThreadDL implements Runnable
	{
		private int mid = 0;
		ThreadDL(int id)
		{
			mid = id;
		}
		@Override
		public void run() {
			// TODO Auto-generated method stub

			// TODO Auto-generated method stub
		//	foucedown = false;
			SharedPreferences settings1 = getSharedPreferences(SAVESTRING_INFOS, 0);
			String address = settings1.getString("address", "");
			String user = settings1.getString("user", "");
			String password = settings1.getString("password", "");
			String filename = settings1.getString("FtpFileName", "");

			String tmpFtpLoopNum = settings1.getString("FtpLoopNum","");
			String tmpFtpLoopTime = settings1.getString("FtpLoopTime","");
			if(!((address!=null)&&(address.length()!=0)))
			{
				address = "172.24.169.64";
			}
		    if(!((user!=null)&&(user.length()!=0)))
		    {
		    	user = "test";
		    }
			if(!((password!=null)&&(password.length()!=0)))
			{
				password = "test";
			}
			if(!((filename!=null)&&(filename.length()!=0)))
			{
				filename = "DL.msi";
			}
			if(!((tmpFtpLoopNum!=null)&&(tmpFtpLoopNum.length()!=0)))
			{
				tmpFtpLoopNum = "0";
			}
			if(!((tmpFtpLoopTime!=null)&&(tmpFtpLoopTime.length()!=0)))
			{
				tmpFtpLoopTime = "0";
			}
			FtpLoopNum = Integer.parseInt(tmpFtpLoopNum);
	        FtpLoopTime = Integer.parseInt(tmpFtpLoopTime);
			
			if(FtpLoopNum != 0)
			{
			int fullLoop = FtpLoopNum;
			while((fullLoop > 0)&&(!foucedown))
			{
			fullLoop --;
	        String s = "Config data: \r\n"+" FTP loop:"+String.valueOf(FtpLoopNum) + " Ftp Time loop:"+String.valueOf(FtpLoopTime)+"\r\n";
	        String s1 = "current loop is "+String.valueOf(FtpLoopNum - fullLoop);
	    //    	showFtpConfig.setText(s+s1);
	        Message msg;
	        msg = Message.obtain();
	        msg.what = 0;
	        msg.obj = s+s1;
	        mHandler.sendMessage(msg);
			MyDownTransferListener one = new MyDownTransferListener();

			File fileDir =getFilesDir();
			File localfile = new File("/data/data/com.MY.pingtest/files/net-tool.zip");
			FTPClient clientdown = new FTPClient();

			dl[mid] = clientdown;
			
			try {
				clientdown.connect(address);
				clientdown.login(user,password);
				clientdown.download(filename, localfile,one);
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (FTPIllegalReplyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (FTPException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (FTPDataTransferException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (FTPAbortedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			}//while
			}
			else if(FtpLoopTime != 0)
			{
				SimpleDateFormat  formatter =   new SimpleDateFormat("yyyy.MM.dd    HH:mm:ss     ");     
            	long endTime = System.currentTimeMillis()+FtpLoopTime*1000;//毫秒
         	   while((System.currentTimeMillis()<endTime)&&(!foucedown))
         	   {
         			String s = "Config data: \r\n"+" FTP loop:"+String.valueOf(FtpLoopNum) + " Ftp Time loop:"+String.valueOf(FtpLoopTime)+"\r\n";
         			Date  curDate = new Date(System.currentTimeMillis());//获取当前时间       
           			//	String str = formatter.format(curDate);   	
           				Date  curDate1 = new Date(endTime);//获取当前时间       
                       	String s1 = "current time is "+formatter.format(curDate)+" End time is "+ formatter.format(curDate1);
                       //	showFtpConfig.setText(s+s1);
                           Message msg;
                           msg = Message.obtain();
                           msg.what = 0;
                           msg.obj = s+s1;
                           mHandler.sendMessage(msg);
               			MyDownTransferListener one = new MyDownTransferListener();

            			File fileDir =getFilesDir();
            			File localfile = new File("/data/data/com.MY.pingtest/files/net-tool.zip");
            			FTPClient clientdown = new FTPClient();
            			dl[mid] = clientdown;
            			try {
            				clientdown.connect(address);
            				clientdown.login(user,password);
            				clientdown.download(filename, localfile,one);
            			} catch (IllegalStateException e) {
            				// TODO Auto-generated catch block
            				e.printStackTrace();
            			} catch (IOException e) {
            				// TODO Auto-generated catch block
            				e.printStackTrace();
            			} catch (FTPIllegalReplyException e) {
            				// TODO Auto-generated catch block
            				e.printStackTrace();
            			} catch (FTPException e) {
            				// TODO Auto-generated catch block
            				e.printStackTrace();
            			} catch (FTPDataTransferException e) {
            				// TODO Auto-generated catch block
            				e.printStackTrace();
            			} catch (FTPAbortedException e) {
            				// TODO Auto-generated catch block
            				e.printStackTrace();
            			}
         	   }
			}
			
			mHandler.sendEmptyMessage(104);

		
		}
		
	}
 /*   Runnable threadDL = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			foucedown = false;
			SharedPreferences settings1 = getSharedPreferences(SAVESTRING_INFOS, 0);
			String address = settings1.getString("address", "");
			String user = settings1.getString("user", "");
			String password = settings1.getString("password", "");
			String filename = settings1.getString("FtpFileName", "");

			String tmpFtpLoopNum = settings1.getString("FtpLoopNum","");
			String tmpFtpLoopTime = settings1.getString("FtpLoopTime","");
			if(!((address!=null)&&(address.length()!=0)))
			{
				address = "172.24.169.64";
			}
		    if(!((user!=null)&&(user.length()!=0)))
		    {
		    	user = "test";
		    }
			if(!((password!=null)&&(password.length()!=0)))
			{
				password = "test";
			}
			if(!((filename!=null)&&(filename.length()!=0)))
			{
				filename = "DL.msi";
			}
			if(!((tmpFtpLoopNum!=null)&&(tmpFtpLoopNum.length()!=0)))
			{
				tmpFtpLoopNum = "0";
			}
			if(!((tmpFtpLoopTime!=null)&&(tmpFtpLoopTime.length()!=0)))
			{
				tmpFtpLoopTime = "0";
			}
			FtpLoopNum = Integer.parseInt(tmpFtpLoopNum);
	        FtpLoopTime = Integer.parseInt(tmpFtpLoopTime);
			
			if(FtpLoopNum != 0)
			{
			int fullLoop = FtpLoopNum;
			while((fullLoop > 0)&&(!foucedown))
			{
			fullLoop --;
	        String s = "Config data: \r\n"+" FTP loop:"+String.valueOf(FtpLoopNum) + " Ftp Time loop:"+String.valueOf(FtpLoopTime)+"\r\n";
	        String s1 = "current loop is "+String.valueOf(FtpLoopNum - fullLoop);
	    //    	showFtpConfig.setText(s+s1);
	        Message msg;
	        msg = Message.obtain();
	        msg.what = 0;
	        msg.obj = s+s1;
	        mHandler.sendMessage(msg);
			MyDownTransferListener one = new MyDownTransferListener();

			File fileDir =getFilesDir();
			File localfile = new File("/data/data/com.MY.pingtest/files/net-tool.zip");
			FTPClient clientdown = new FTPClient();

			dl = clientdown;
			
			try {
				clientdown.connect(address);
				clientdown.login(user,password);
				clientdown.download(filename, localfile,one);
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (FTPIllegalReplyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (FTPException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (FTPDataTransferException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (FTPAbortedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			}//while
			}
			else if(FtpLoopTime != 0)
			{
				SimpleDateFormat  formatter =   new SimpleDateFormat("yyyy.MM.dd    HH:mm:ss     ");     
            	long endTime = System.currentTimeMillis()+FtpLoopTime*1000;//毫秒
         	   while((System.currentTimeMillis()<endTime)&&(!foucedown))
         	   {
         			String s = "Config data: \r\n"+" FTP loop:"+String.valueOf(FtpLoopNum) + " Ftp Time loop:"+String.valueOf(FtpLoopTime)+"\r\n";
         			Date  curDate = new Date(System.currentTimeMillis());//获取当前时间       
           			//	String str = formatter.format(curDate);   	
           				Date  curDate1 = new Date(endTime);//获取当前时间       
                       	String s1 = "current time is "+formatter.format(curDate)+" End time is "+ formatter.format(curDate1);
                       //	showFtpConfig.setText(s+s1);
                           Message msg;
                           msg = Message.obtain();
                           msg.what = 0;
                           msg.obj = s+s1;
                           mHandler.sendMessage(msg);
               			MyDownTransferListener one = new MyDownTransferListener();

            			File fileDir =getFilesDir();
            			File localfile = new File("/data/data/com.MY.pingtest/files/net-tool.zip");
            			FTPClient clientdown = new FTPClient();
            			dl = clientdown;
            			try {
            				clientdown.connect(address);
            				clientdown.login(user,password);
            				clientdown.download(filename, localfile,one);
            			} catch (IllegalStateException e) {
            				// TODO Auto-generated catch block
            				e.printStackTrace();
            			} catch (IOException e) {
            				// TODO Auto-generated catch block
            				e.printStackTrace();
            			} catch (FTPIllegalReplyException e) {
            				// TODO Auto-generated catch block
            				e.printStackTrace();
            			} catch (FTPException e) {
            				// TODO Auto-generated catch block
            				e.printStackTrace();
            			} catch (FTPDataTransferException e) {
            				// TODO Auto-generated catch block
            				e.printStackTrace();
            			} catch (FTPAbortedException e) {
            				// TODO Auto-generated catch block
            				e.printStackTrace();
            			}
         	   }
			}
			
			mHandler.sendEmptyMessage(104);

		}	
	};*/
	
	public class ThreadUL implements Runnable
	{
		private int mid = 0;
		ThreadUL(int id)
		{
			mid = id;
		}
		@Override
		public void run() {
			// TODO Auto-generated method stub

			// TODO Auto-generated method stub
	//		foucedown = false;  //race condition 会导致有时候STOP不工作
			SharedPreferences settings1 = getSharedPreferences(SAVESTRING_INFOS, 0);
			String address = settings1.getString("address", "");
			String user = settings1.getString("user", "");
			String password = settings1.getString("password", "");
			String tmpFtpLoopNum = settings1.getString("FtpLoopNum","");
			String tmpFtpLoopTime = settings1.getString("FtpLoopTime","");
			if(!((address!=null)&&(address.length()!=0)))
			{
				address = "172.24.169.64";
			}
		    if(!((user!=null)&&(user.length()!=0)))
		    {
		    	user = "test";
		    }
			if(!((password!=null)&&(password.length()!=0)))
			{
				password = "test";
			}
			if(!((tmpFtpLoopNum!=null)&&(tmpFtpLoopNum.length()!=0)))
			{
				tmpFtpLoopNum = "0";
			}
			if(!((tmpFtpLoopTime!=null)&&(tmpFtpLoopTime.length()!=0)))
			{
				tmpFtpLoopTime = "0";
			}
			FtpLoopNum = Integer.parseInt(tmpFtpLoopNum);
	        FtpLoopTime = Integer.parseInt(tmpFtpLoopTime);
			
			String Imei = ((TelephonyManager) getSystemService(TELEPHONY_SERVICE)).getDeviceId();
			
			if(FtpLoopNum != 0)
			{
			int fullLoop = FtpLoopNum;
			while((fullLoop > 0)&&(!foucedown))
			{
				fullLoop --;
		        String s = "Config data: \r\n"+" FTP loop:"+String.valueOf(FtpLoopNum) + " Ftp Time loop:"+String.valueOf(FtpLoopTime)+"\r\n";
		        String s1 = "current loop is "+String.valueOf(FtpLoopNum - fullLoop);
		    //    	showFtpConfig.setText(s+s1);
		        Message msg;
		        msg = Message.obtain();
		        msg.what = 0;
		        msg.obj = s+s1;
		        mHandler.sendMessage(msg);
			File localfile = new File("/data/data/com.MY.pingtest/upfiledemo"+Imei);
			MyUpTransferListener one = new MyUpTransferListener();
			FTPClient clientup = new FTPClient();
			ul[mid] = clientup;
			try {
				clientup.connect(address);
				clientup.login(user,password);
				clientup.upload(localfile,one);
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (FTPIllegalReplyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (FTPException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (FTPDataTransferException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (FTPAbortedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			}
			}
			else if(FtpLoopTime != 0)
			{				
		      SimpleDateFormat  formatter =   new SimpleDateFormat("yyyy.MM.dd    HH:mm:ss     ");     
        	  long endTime = System.currentTimeMillis()+FtpLoopTime*1000;//毫秒
     	      while((System.currentTimeMillis()<endTime)&&(!foucedown))
     	      {
     			String s = "Config data: \r\n"+" FTP loop:"+String.valueOf(FtpLoopNum) + " Ftp Time loop:"+String.valueOf(FtpLoopTime)+"\r\n";
     			Date  curDate = new Date(System.currentTimeMillis());//获取当前时间       
       			//	String str = formatter.format(curDate);   	
       				Date  curDate1 = new Date(endTime);//获取当前时间       
                   	String s1 = "current time is "+formatter.format(curDate)+" End time is "+ formatter.format(curDate1);
                   //	showFtpConfig.setText(s+s1);
                       Message msg;
                       msg = Message.obtain();
                       msg.what = 0;
                       msg.obj = s+s1;
                       mHandler.sendMessage(msg);
				File localfile = new File("/data/data/com.MY.pingtest/upfiledemo"+Imei);
				MyUpTransferListener one = new MyUpTransferListener();
				FTPClient clientup = new FTPClient();
				
				ul[mid] = clientup;
				try {
					clientup.connect(address);
					clientup.login(user,password);
					clientup.upload(localfile,one);
				} catch (IllegalStateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (FTPIllegalReplyException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (FTPException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (FTPDataTransferException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (FTPAbortedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
     	      }
			}
         mHandler.sendEmptyMessage(105);
		
		}
	}
/*	Runnable threadUL = new Runnable()
	{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			foucedown = false;
			SharedPreferences settings1 = getSharedPreferences(SAVESTRING_INFOS, 0);
			String address = settings1.getString("address", "");
			String user = settings1.getString("user", "");
			String password = settings1.getString("password", "");
			String tmpFtpLoopNum = settings1.getString("FtpLoopNum","");
			String tmpFtpLoopTime = settings1.getString("FtpLoopTime","");
			if(!((address!=null)&&(address.length()!=0)))
			{
				address = "172.24.169.64";
			}
		    if(!((user!=null)&&(user.length()!=0)))
		    {
		    	user = "test";
		    }
			if(!((password!=null)&&(password.length()!=0)))
			{
				password = "test";
			}
			if(!((tmpFtpLoopNum!=null)&&(tmpFtpLoopNum.length()!=0)))
			{
				tmpFtpLoopNum = "0";
			}
			if(!((tmpFtpLoopTime!=null)&&(tmpFtpLoopTime.length()!=0)))
			{
				tmpFtpLoopTime = "0";
			}
			FtpLoopNum = Integer.parseInt(tmpFtpLoopNum);
	        FtpLoopTime = Integer.parseInt(tmpFtpLoopTime);
			
			String Imei = ((TelephonyManager) getSystemService(TELEPHONY_SERVICE)).getDeviceId();
			
			if(FtpLoopNum != 0)
			{
			int fullLoop = FtpLoopNum;
			while((fullLoop > 0)&&(!foucedown))
			{
				fullLoop --;
		        String s = "Config data: \r\n"+" FTP loop:"+String.valueOf(FtpLoopNum) + " Ftp Time loop:"+String.valueOf(FtpLoopTime)+"\r\n";
		        String s1 = "current loop is "+String.valueOf(FtpLoopNum - fullLoop);
		    //    	showFtpConfig.setText(s+s1);
		        Message msg;
		        msg = Message.obtain();
		        msg.what = 0;
		        msg.obj = s+s1;
		        mHandler.sendMessage(msg);
			File localfile = new File("/data/data/com.MY.pingtest/upfiledemo"+Imei);
			MyUpTransferListener one = new MyUpTransferListener();
			FTPClient clientup = new FTPClient();
			ul = clientup;
			try {
				clientup.connect(address);
				clientup.login(user,password);
				clientup.upload(localfile,one);
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (FTPIllegalReplyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (FTPException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (FTPDataTransferException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (FTPAbortedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			}
			}
			else if(FtpLoopTime != 0)
			{				
		      SimpleDateFormat  formatter =   new SimpleDateFormat("yyyy.MM.dd    HH:mm:ss     ");     
        	  long endTime = System.currentTimeMillis()+FtpLoopTime*1000;//毫秒
     	      while((System.currentTimeMillis()<endTime)&&(!foucedown))
     	      {
     			String s = "Config data: \r\n"+" FTP loop:"+String.valueOf(FtpLoopNum) + " Ftp Time loop:"+String.valueOf(FtpLoopTime)+"\r\n";
     			Date  curDate = new Date(System.currentTimeMillis());//获取当前时间       
       			//	String str = formatter.format(curDate);   	
       				Date  curDate1 = new Date(endTime);//获取当前时间       
                   	String s1 = "current time is "+formatter.format(curDate)+" End time is "+ formatter.format(curDate1);
                   //	showFtpConfig.setText(s+s1);
                       Message msg;
                       msg = Message.obtain();
                       msg.what = 0;
                       msg.obj = s+s1;
                       mHandler.sendMessage(msg);
				File localfile = new File("/data/data/com.MY.pingtest/upfiledemo"+Imei);
				MyUpTransferListener one = new MyUpTransferListener();
				FTPClient clientup = new FTPClient();
				
				ul = clientup;
				try {
					clientup.connect(address);
					clientup.login(user,password);
					clientup.upload(localfile,one);
				} catch (IllegalStateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (FTPIllegalReplyException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (FTPException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (FTPDataTransferException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (FTPAbortedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
     	      }
			}
         mHandler.sendEmptyMessage(105);
		}
		
	};*/
	

    
    
   /*void threadPing()
   {
		pingIsWorking = true;
		SharedPreferences outsharedPreferences= getSharedPreferences("myConfig",
				Activity.MODE_PRIVATE); 
		String mPingPackageSize =outsharedPreferences.getString("mPingPackageSize", "");
		String mPingCount =outsharedPreferences.getString("mPingCount", "");
		String mPingTimer =outsharedPreferences.getString("mPingTimer", "");
		String mPingAddress =outsharedPreferences.getString("mPingAddress", "");
       int counter = Integer.parseInt(mPingCount.trim());
     //  int mtimer = Integer.parseInt(mPingTimer.trim());

       String cmdPing  = "/system/bin/ping -c" + mPingCount + " -s " +mPingPackageSize+" "+mPingAddress;
       String str;
       String temp = "";
		try {
			p = Runtime.getRuntime().exec(cmdPing);
           BufferedReader bufferReader = new BufferedReader(new InputStreamReader(p.getInputStream()));
           while ((str = bufferReader.readLine()) != null) {
    //       changePingPage(str, 0);
     //      temp = temp+str+"\r\n";
               Message msg;
               msg = Message.obtain();
               msg.what = 0;
               msg.obj = str;
               mHandler.sendMessage(msg);        
               }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}     		
		mHandler.sendEmptyMessage(97);
		pingIsWorking = false;
   }*/
	Runnable threadPing = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			
	        Message msg;
	        msg = Message.obtain();
	        msg.what = 0;
	        msg.obj = "Ping start";    
	        mHandler.sendMessage(msg);  
			pingIsWorking = true;
			SharedPreferences outsharedPreferences= getSharedPreferences("myConfig",
					Activity.MODE_PRIVATE); 
			String mPingPackageSize =outsharedPreferences.getString("mPingPackageSize", "");
			String mPingCount =outsharedPreferences.getString("mPingCount", "");
			String mPingTimer =outsharedPreferences.getString("mPingTimer", "");
			String mPingAddress =outsharedPreferences.getString("mPingAddress", "");
			
			if(!((mPingPackageSize!=null)&&(mPingPackageSize.length()!=0)))
			{
				mPingPackageSize = "32";
			}
			if(!((mPingAddress!=null)&&(mPingAddress.length()!=0)))
			{
				mPingAddress = "www.163.com";
			}

			int counter = 5;
			if(!((mPingCount!=null)&&(mPingCount.length()!=0)))
			{
			//	counter = "0";
			  counter = Integer.parseInt(mPingCount.trim());
			}
         //   int counter = Integer.parseInt(mPingCount.trim());
          //  int mtimer = Integer.parseInt(mPingTimer.trim());

            String cmdPing  = "/system/bin/ping -c" + mPingCount + " -s " +mPingPackageSize+" "+mPingAddress;
            String str;
            String temp = "";
			try {
				p = Runtime.getRuntime().exec(cmdPing);
	            BufferedReader bufferReader = new BufferedReader(new InputStreamReader(p.getInputStream()));
	            while ((str = bufferReader.readLine()) != null) {
	     //       changePingPage(str, 0);
	      //      temp = temp+str+"\r\n";
           //         Log.e("ping",str);
                    Message msg1;
                    msg1 = Message.obtain();
                    msg1.what = 0;
                    msg1.obj = str;
                    mHandler.sendMessage(msg1);              
	                }
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}     		
			mHandler.sendEmptyMessage(97);
			pingIsWorking = false;
		 }	
		
	
	};
 
    
	public Handler mHandler = new Handler() {
    	public void handleMessage(final Message msg) {
    		
   // 		Log.e("mHandler ",String.valueOf(msg.what));
    		switch (msg.what) {
			case 0:
			{
				String message;
				if(msg.obj != null)
				{
				message = msg.obj.toString();
				textview.append(message+"\r\n");
				}
			}
				break;
			case 1:  //DL only
				handleDL();
				foucedown = false;
				break;
			case 2:
				handleUL();
				foucedown = false;
				break;
			case 3:
				handleDLUL();
				foucedown = false;
				break;
			case 4:
				handlePing();
				foucedown = false;
				break;
			case 5:
				handleDLP();
				foucedown = false;
				break;
			case 6:
				handleULP();
				foucedown = false;
				break;
			case 7:
				handleAll();
				foucedown = false;
				break;
				 
			case 97:
				status = status - 4;
				if(!modeSynchronism)
				{
					synchronized(pvPing)
					{
						pvPing.notifyAll();
					}
				}
	//			Log.e("status",String.valueOf(status));
				if((status == 0)&&modeSynchronism)
				{
	//			Log.e("status == 0 ",String.valueOf(msg.what));
				 resetBtn();
				synchronized(pv)
				{
				  pv.notifyAll();
				}
				 
				}
				break;
			case 98:
		//		status = status - 2;
				if(!modeSynchronism)
				{
					synchronized(pvUL)
					{
						pvUL.notifyAll();
					}
				}
		//		Log.e("status",String.valueOf(status));
				if((status <= 0)&&modeSynchronism)
				{
		//			 resetBtn();
		//		  synchronized(pv)
		//		  {
		//		  pv.notifyAll();
		//		  }
			//	  resetBtn();
				}
				break;
			case 99:
			//	if(status != 0)
			//	{
		//		status = status - 1;
			//	}//如果status为0，说明触发了STOP，DL/UL线程在reset以后ABORT，不能再
				if(!modeSynchronism)
				{
					synchronized(pvDL)
					{
						pvDL.notifyAll();
					}
				}
	//			Log.e("status ",String.valueOf(status));
				if((status <= 0)&&modeSynchronism)
				{
		//			 resetBtn();
		//			synchronized(pv)
		//			{
			//	     pv.notifyAll();
					}
				//  resetBtn();
		//		}
				break;
			case 100:
				textview1.setText("");
			    break;
			case 104:
				status = status - 1;
				if((status <= 0)&&modeSynchronism)
				{
//				Log.e("status == 0 ",String.valueOf(msg.what));
				 resetBtn();
				synchronized(pv)
				{
				  pv.notifyAll();
				}
				}
				break;
			case 105:
				status = status - 2;
				if((status <= 0)&&modeSynchronism)
				{
//				Log.e("status == 0 ",String.valueOf(msg.what));
				 resetBtn();
				synchronized(pv)
				{
				  pv.notifyAll();
				}
				}
				break;
			default:
				Toast.makeText(MailActivity.this, "无选中项", Toast.LENGTH_LONG).show();
				resetBtn();
				break;
			}
    	}
    };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.net_tool_ftp);
		
        StrictMode.ThreadPolicy policy=new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        
		SharedPreferences settings1 = getSharedPreferences(SAVESTRING_INFOS, 0);

    	dlThread = Integer.parseInt(settings1.getString("FtpDlThread", ""));  
    	ulThread = Integer.parseInt(settings1.getString("FtpUlThread", ""));
    	pingThread  =Integer.parseInt(settings1.getString("PingThread", ""));
		
		list.add("FTP DL");
		list.add("FTP UL");
		list.add("ping  ");
		listview = (ListView) findViewById(R.id.listView1);
		numberPicker = (NumberPicker) findViewById(R.id.MutiTasknumberPicker);

		numberPicker.setMaxValue(60);
		numberPicker.setMinValue(0);
		numberPicker.setValue(1);
		
		btn = (Button) findViewById(R.id.connectFtp);
		stopbtn = (Button) findViewById(R.id.stopMutiTask);
		selectbtn = (Button) findViewById(R.id.MutiTaskSelect);
		deletebtn = (Button) findViewById(R.id.MutiTaskDelete);
		textview1 = (TextView) findViewById(R.id.MutiTasktextView1);
		textview2 = (TextView) findViewById(R.id.MutiTasktextView2);
		textview = (TextView) findViewById(R.id.MutiTaskShow);
		listview.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_multiple_choice,list));
		listview.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
		
		
		listview.setOnItemClickListener(new OnItemClickListener() {  
        	 

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				
				switch((int)id){
					case 0:
					{
						checked0 = !checked0;
						if(checked0)
						{
							status = status+1;
						}
						else
						{
							status = status-1;
						}
					}
						break;
					case 1:
					{
						checked1 = !checked1;
						if(checked1)
						{
							status = status+2;
						}
						else
						{
							status = status-2;
						}
					}
						break;
					case 2:
					{
						checked2 = !checked2;
						if(checked2)
						{
							status = status+4;
						}
						else
						{
							status = status-4;
						}
					}
						break;
					default:
					    break;
							
				}
		//		setTitle(String.valueOf(status));
			}  
        }); 
		
		selectbtn.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
			//	Toast.makeText(MailActivity.this, String.valueOf(numberPicker.getValue()), Toast.LENGTH_LONG).show();
				if(status != 0)
				{
				MutiTaskData data = new MutiTaskData(status,numberPicker.getValue());
				
				Textview1String = Textview1String+"("+data.describer+")"+"x"+String.valueOf(data.loop)+" ";
	//			textview1.append(Textview1String+"("+data.describer+")"+"x"+String.valueOf(data.loop)+" ");
				textview1.setText(Textview1String);
				thelist.add(data);
				}
				else
				{
					Toast.makeText(MailActivity.this, "invalid input", Toast.LENGTH_LONG).show();
				}
			}
			
		});
		
		deletebtn.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String s = "";
				String[] raw = Textview1String.split(" ");
				for(int i = 0;i<raw.length-2;i++)
				{
					s = s + raw[i]+" ";
				}
				textview1.setText(s);
				Textview1String = s;
				if((thelist.size()-1)>=0)
				thelist.remove(thelist.size()-1);
			}
			
		});
		btn.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				new Thread(new Runnable()
				{

					@Override
					public void run() {
						// TODO Auto-generated method stub
						Iterator<MutiTaskData> it = thelist.iterator();
						while(it.hasNext())
						{
							final MutiTaskData me = it.next();
							loopT = me.loop;
							if(modeSynchronism)
							{
							while(loopT>0)
							{
								status = me.status;
								mHandler.sendEmptyMessage(status);
								try {
									synchronized(pv)
								   {
									pv.wait();
									}
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								loopT--;
							}
					        Message msg;
					        msg = Message.obtain();
					        msg.what = 0;
					        msg.obj = "同步任务完成";    
					        mHandler.sendMessage(msg);  
							}
							else
							{
								if((me.status/4) == 1) //PING
								{
									new Thread(new Runnable()
									{

										@Override
										public void run() {
											// TODO Auto-generated method stub
											int loopPing = me.loop;
											while((loopPing>0)&&(loopT >0))
											{
												mHandler.sendEmptyMessage(4);
												try {
													synchronized(pvPing)
												   {
														pvPing.wait();
													}
												} catch (InterruptedException e) {
													// TODO Auto-generated catch block
													e.printStackTrace();
												}
												loopPing--;
											}
									        Message msg;
									        msg = Message.obtain();
									        msg.what = 0;
									        msg.obj = "异步PING任务完成";    
									        mHandler.sendMessage(msg);  
										}
										
									}).start();
								}
								if((me.status%4)/2 == 1)//UL
								{
									new Thread(new Runnable()
									{

										@Override
										public void run() {
											// TODO Auto-generated method stub
											int loopUl = me.loop;
											while((loopUl>0)&&(loopT >0))
											{
												mHandler.sendEmptyMessage(2);
												try {
													synchronized(pvUL)
												   {
														pvUL.wait();
													}
												} catch (InterruptedException e) {
													// TODO Auto-generated catch block
													e.printStackTrace();
												}
												loopUl--;
											}
									        Message msg;
									        msg = Message.obtain();
									        msg.what = 0;
									        msg.obj = "异步UL任务完成";    
									        mHandler.sendMessage(msg);  
										}
										
									}).start();
								}
								if((me.status%4)%2 == 1)//DL
								{
									new Thread(new Runnable()
									{

										@Override
										public void run() {
											// TODO Auto-generated method stub
											int loopDl = me.loop;
											while((loopDl>0)&&(loopT >0))
											{
												mHandler.sendEmptyMessage(1);
												try {
													synchronized(pvDL)
												   {
														pvDL.wait();
													}
												} catch (InterruptedException e) {
													// TODO Auto-generated catch block
													e.printStackTrace();
												}
												loopDl--;
											}							        
											Message msg;
									        msg = Message.obtain();
									        msg.what = 0;
									        msg.obj = "异步DL任务完成";    
									        mHandler.sendMessage(msg); 
										}	
									}).start();
								}	
							}
							
							it.remove(); 
						}
						mHandler.sendEmptyMessage(100);
					}
					
				}).start();
					


		//		mHandler.sendEmptyMessage(status);
		//		listview.clearChoices();
				btn.setEnabled(false);
				listview.setEnabled(false);
			}
			
		});
		
		stopbtn.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				foucedown = true;
				
				resetBtn();
				new Thread(new Runnable()
				{

					@Override
					public void run() {
						// TODO Auto-generated method stub
						for(int i = 0;i<dlThread;i++)
						{
						if(dl[i] != null)
						{
							try {
								dl[i].abortCurrentDataTransfer(true);
								dl[i].disconnect(true);
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (FTPIllegalReplyException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (IllegalStateException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (FTPException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}	
						}
						}
						for(int i = 0;i<ulThread;i++)
						{
						if(ul[i] != null)
						{
							try {
								ul[i].abortCurrentDataTransfer(true);
								ul[i].disconnect(true);
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (FTPIllegalReplyException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (IllegalStateException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (FTPException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}}
					}
					

				}).start();
				if(pingIsWorking)
				{
					pingIsWorking = false;
					p.destroy();
				}
//				resetBtn();
				
				loopT = -99;
				synchronized(pv)
			   {
				pv.notifyAll();
				}
				synchronized(pvPing)
				   {
					pvPing.notifyAll();
					}
				synchronized(pvDL)
				   {
					pvDL.notifyAll();
					}
				synchronized(pvUL)
				   {
					pvUL.notifyAll();
					}
	//			textview1.setText("");
				
			}
			
		});
		
}
protected String getProfile(int status2) {
		// TODO Auto-generated method stub
		return null;
	}
protected void resetBtn() {
		// TODO Auto-generated method stub
	btn.setEnabled(true);
	listview.setEnabled(true);
	listview.setItemChecked(0,true);
	listview.setItemChecked(1,true);
	listview.setItemChecked(2,true);
	listview.clearChoices();

	status = 0;

	checked0 = false;
	checked1 = false;
	checked2 = false;
	}
protected void handleAll() {
		// TODO Auto-generated method stub
	for(int i=0;i<ulThread;i++)
	new Thread(new ThreadUL(i)).start();
	for(int i=0;i<dlThread;i++)
	new Thread(new ThreadDL(i)).start();
	new Thread(threadPing).start();
	}
protected void handleULP() {
		// TODO Auto-generated method stub
	for(int i=0;i<ulThread;i++)
	new Thread(new ThreadUL(i)).start();
	new Thread(threadPing).start();
	}
protected void handleDLP() {
		// TODO Auto-generated method stub
	for(int i=0;i<dlThread;i++)
	new Thread(new ThreadDL(i)).start();
	new Thread(threadPing).start();
	}
protected void handlePing() {
		// TODO Auto-generated method stub
	new Thread(threadPing).start();
	}
protected void handleDLUL() {
		// TODO Auto-generated method stub
	for(int i=0;i<dlThread;i++)
	new Thread(new ThreadDL(i)).start();
	for(int i=0;i<ulThread;i++)
	new Thread(new ThreadUL(i)).start();	
	}
protected void handleUL() {
		// TODO Auto-generated method stub
	for(int i=0;i<ulThread;i++)
	new Thread(new ThreadUL(i)).start();
	}
protected void handleDL() {
	// TODO Auto-generated method stub
	for(int i=0;i<dlThread;i++)
	new Thread(new ThreadDL(i)).start();
}

public class MyDownTransferListener implements FTPDataTransferListener
{

	@Override
	public void aborted() {
		// TODO Auto-generated method stub
		mHandler.sendEmptyMessage(99);
        Message msg;
        msg = Message.obtain();
        msg.what = 0;
        msg.obj = "DL aborted";
        mHandler.sendMessage(msg);
	}

	@Override
	public void completed() {
		// TODO Auto-generated method stub
		mHandler.sendEmptyMessage(99);
        Message msg;
        msg = Message.obtain();
        msg.what = 0;
        msg.obj = "DL completed";
        mHandler.sendMessage(msg);
	}

	@Override
	public void failed() {
		// TODO Auto-generated method stub
		mHandler.sendEmptyMessage(99);
        Message msg;
        msg = Message.obtain();
        msg.what = 0;
        msg.obj = "DL failed";
        mHandler.sendMessage(msg);
	}

	@Override
	public void started() {
		// TODO Auto-generated method stub
//		Log.e("DL start","DL start");
        Message msg;
        msg = Message.obtain();
        msg.what = 0;
        msg.obj = "DL start";
        mHandler.sendMessage(msg);  
	}

	@Override
	public void transferred(int arg0) {
		// TODO Auto-generated method stub
		
	}
	
}

public class MyUpTransferListener implements FTPDataTransferListener
{

	@Override
	public void aborted() {
		// TODO Auto-generated method stub
		mHandler.sendEmptyMessage(98);
        Message msg;
        msg = Message.obtain();
        msg.what = 0;
        msg.obj = "UL aborted";
        mHandler.sendMessage(msg); 
	}

	@Override
	public void completed() {
		// TODO Auto-generated method stub
		mHandler.sendEmptyMessage(98);
        Message msg;
        msg = Message.obtain();
        msg.what = 0;
        msg.obj = "UL completed";
        mHandler.sendMessage(msg); 
	}

	@Override
	public void failed() {
		// TODO Auto-generated method stub
		mHandler.sendEmptyMessage(98);
        Message msg;
        msg = Message.obtain();
        msg.what = 0;
        msg.obj = "UL failed";
        mHandler.sendMessage(msg); 
	}

	@Override
	public void started() {
		// TODO Auto-generated method stub
		Log.e("UL start","UL start");
        Message msg;
        msg = Message.obtain();
        msg.what = 0;
        msg.obj = "UL start";
        mHandler.sendMessage(msg);  
	}

	@Override
	public void transferred(int arg0) {
		// TODO Auto-generated method stub
		
	}
	
}

}
