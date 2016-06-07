package com.MY.netTools;

import it.sauronsoftware.ftp4j.FTPAbortedException;
import it.sauronsoftware.ftp4j.FTPClient;
import it.sauronsoftware.ftp4j.FTPDataTransferException;
import it.sauronsoftware.ftp4j.FTPDataTransferListener;
import it.sauronsoftware.ftp4j.FTPException;
import it.sauronsoftware.ftp4j.FTPIllegalReplyException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.os.RemoteException;
import android.os.StrictMode;
import android.telephony.CellIdentityLte;
import android.telephony.CellInfo;
import android.telephony.CellInfoLte;
import android.telephony.CellLocation;
import android.telephony.CellSignalStrengthLte;
import android.telephony.NeighboringCellInfo;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.MY.netTools.business.HTTPCommand;
import com.MY.netTools.business.MultiTaskCommand;
import com.MY.netTools.business.MultiTaskCommand.DL;
import com.MY.netTools.business.MultiTaskCommand.PING;
import com.MY.netTools.business.MultiTaskCommand.UL;
import com.MY.netTools.business.OTCALLCommand;
import com.MY.netTools.business.RawData;
import com.MY.netTools.business.SWECHATCommand;
import com.MY.netTools.business.ToolSrvImpl;
import com.MY.netTools.business.httpKpi;
import com.MY.netTools.business.myMessage;
import com.MY.netTools.business.myMessage.DFDCommand;
import com.MY.netTools.business.myMessage.DFUCommand;
import com.MY.netTools.business.myMessage.FtpDLCommand;
import com.MY.netTools.business.myMessage.FtpULCommand;
import com.MY.netTools.business.myMessage.PingCommand;
import com.MY.netTools.business.myMessage.UrlCommand;
import com.android.internal.telephony.ITelephony;

public class clientService extends Service {

	public static Socket mSocket = null;
//	public static SocketChannel socketChannel = null;
//	Selector selector;  
	
 //   private static final String HOST = "10.0.2.2";  //10.0.3.2?
    private static final int PORT = 8189;  
 //   private final String ACTION_NAME1 = "发送数据";
    private static String LogServerAddress = "127.0.0.1";
    private static String LogServerUsr = "root";
    private static String LogServerPass = "123456";
    
    
    private int speedType = 0;
    private int DFstopTimer = 0;
    private boolean isTaskDF = false;
    private boolean startstop= false;
 //   private boolean rebootServiceForTaskDF = false;
 //   public static boolean rebootServiceForServerDown = false;
    private final StringBuffer report2file = new StringBuffer();
	private long startTime;
	private long endTime;
	private int isAlive = 0;
	float inStart;
	float outStart;
	float elaspetInSize;
	float elaspetOutSize;
	double avgInSpeed = 0.0F;
	double avgOutSpeed= 0.0F;
	public String[] pinjie=new String[4];
	
	private float Inmin=0;
	private float Inmax=0;
	private float outmin=0;
	private float outmax=0;
	private double intotal=0;
    private double outtotal=0;
	private int i=1;
	private long sec=0;
	//===============
    
    
    
    public static BufferedReader in = null;  
    public static PrintWriter out = null;  
//    private String content = ""; 
    boolean TaskIsWorking = false;
	boolean timeup = true;
    private static Object lock=new Object();
    private static Object lockliq=new Object();
    private static Object pvlock=new Object();
    public static Object socketLock=new Object();
    private static Object SocketKAlock=new Object();
    
//    private static Object[] MultiTaskDLlock = new Object[3];
//    private static Object[] MultiTaskULlock = new Object[3];
//    private static Object[] MultiTaskPINGlock = new Object[3];
    private static Object tasklock = new Object();
    private static Object fulltasklock = new Object();
    private static Object AUlock = new Object();

    
    public boolean rebootServiceForTaskDF = false;
    public boolean rebootServiceForServerDown = false;
    public boolean reconnectServer = false;
    
    private long NextStartTime = 0;//next start time
	private static int socketStat = 0;
	private boolean foucedown = false;
	public RawData app; 
	public static long systemTimeOffset = 0;//serverTime - clientTime;
	
	private boolean Urlidle = true;
	private boolean cleanFtp = false;
    String fileName = "AUTOTESTLOG";
	//create new file for IP report
    File file = new File(Environment.getExternalStorageDirectory()+"/NetAnts", fileName);
    public TelephonyManager tm;
	String fileNameFTP = "FTPreport";
    String fileNameIP = "IPreport";
    File fileIP;
    
    File fileFTP;
	//create new file for IP report
	 
	public long ftpdlDoneTime;
	public long ftpulDoneTime;
	FTPClient clientdown;
	FTPClient clientUp;
	FTPClient TraceUp;
	FTPClient AU;
	
	
	FTPClient[] clientdownThread = new FTPClient[3];
	FTPClient[] clientupThread = new FTPClient[3];
	
	private static Queue<Object> lockqueue = new LinkedList<Object>();
	
    protected static FileOutputStream fout = null;
    protected static FileOutputStream foutFTP = null;
    protected static FileOutputStream foutPing = null;
    
    private static Set<myMessage> CommandList = new TreeSet<myMessage>();
    
    private static Queue<String> liq = new LinkedList<String>();
    
    public static int clientStat = 0;
    
    public String IMEI;
    
	File f ;
	FileWriter fw;

	protected int TaskLength = -1;
    
	PowerManager.WakeLock wakeLock;
	
	public RawData theapp;

	public static boolean stoprenconnect = false;
	public boolean isAttch_S = false;
	
	
//	public String MutiTaskDLAddress;
//	public String MutiTaskDLUser;
//	public String MutiTaskDLPassWord;
//	public String MutiTaskDLfileName;
//	public String MutiTaskDLDurationTime;
//	public String MutiTaskULAddress;
//	public String MutiTaskULUser;
///	public String MutiTaskULPassWord;
//	public String MutiTaskULDurationTime;
//	public String MutiTaskPINGAddress;
//	public String MutiTaskPINGmM;
//	public String MutiTaskPINGDurationTime;
//	public String MutiTaskPINGPackageSizs;
	private Process[] p = new Process[3];
	
	public int haveMultiDL = 0;
	public int haveMultiUL = 0;
	public int haveMultiPING = 0;
	
	public boolean[] MultiTaskDLIsWorking = new boolean[3];
	public boolean[] MultiTaskULIsWorking = new boolean[3];
	
	private static String SignalStrengths = "";
	private int SocketServiceKA = 0;
	private SharedPreferences settings;
	
	
	private double lastVersion = 0;
	static double currentVersion = 2.8;
	static boolean AUDownload = false;

	static httpKpi kpi = new httpKpi();
	static boolean HttpSpeed = false;
	static Object lockHttpSpeed = new Object();
	static Object lockHttpInterval = new Object();
	static Object lockNotifyNextHttp = new Object();
	
	static Object lockwechat = new Object();
	static boolean taskwechat = false;
	static boolean intervalKA = false;
	
	public static String getPartStatus(TelephonyManager tmm)
	{
		//cell id 
		//RSRP
		//RSSNR
		String s = "";
		String s2 = "";
		int i4 = 2147483647;
		
		List<CellInfo> cellInfoList = tmm.getAllCellInfo();
        if((cellInfoList != null)&&(!(cellInfoList.isEmpty())))
        {
        for(CellInfo info:cellInfoList){
            //获取邻居小区号
            if (info instanceof CellInfoLte)
            {
                // cast to CellInfoLte and call all the CellInfoLte methods you need
            	CellIdentityLte lte_info = ((CellInfoLte) info).getCellIdentity();

                int n = lte_info.getCi();
                int i = lte_info.getMcc();
                int j = lte_info.getMnc();
                int k = lte_info.getTac();
                int m = lte_info.getPci();
                int i1 = n / 256;
                int i2 = n % 256;
                s ="LTEID:" + i + "-" + j + " LTETac:" + k + " LTEPci:" + m +" Cell Id "+n +" eNodeB:" + i1 + " CIinSite:" + i2 +" ";
                CellSignalStrengthLte Lte_strength = ((CellInfoLte) info).getCellSignalStrength();
            	
                int i3 = Lte_strength.getAsuLevel();
                i4 = Lte_strength.getDbm();
                int i5 = Lte_strength.getTimingAdvance();
                
  //              i4 = 2147483647;
                if(i4 != 2147483647)
                {
                s = s + "AsuLevel=" + i3 + " rsrp=" + i4 + "  rssnr=" + i5+" ";
                }

            }
          //  if(info instanceof CellInfoGsm)
          // // {
           // 	CellIdentityGsm Gsm_info = ((CellInfoGsm) info).getCellIdentity();
          //  	s2 = "Gsm CellId = "+Gsm_info.getCid()+" ";
          //  	CellSignalStrength Gsm_strength = ((CellInfoGsm) info).getCellSignalStrength();
         //   	s2 = s2+Gsm_strength.toString();
         //   }
        }//end of for	
        }
        else
        {
        	CellLocation cellLoca = tmm.getCellLocation();
        	 if(cellLoca!=null)
        	{
        	//	 if (cellLoca instanceof GsmCellLocation)
        	//	 {
        	//        	 int i = ((GsmCellLocation) cellLoca).getLac();
        	 //       	 int j = ((GsmCellLocation) cellLoca).getCid();
        	//        		 int k = j / 256;
        	//        		 int m = j % 256;
        //	        		 s = s + " " + "eNodeB:" + k + " Cell:" + m;
        	        //	 s1 = s1 + " Cid@cellid "+cellLoca.toString();
        	//	 }
        	///	 int i = cellLoca.getLac();
        	///	 int j = cellLoca.getCid();
        	//	 else
        	//	 {
            //		 String[] raw = cellLoca.toString().split(",");
            //		 int k = Integer.parseInt(raw[1]) / 256;
            //		 int m = Integer.parseInt(raw[1]) % 256;
            //		 s = s +" " + "eNodeB:" + k + " Cell:" + m;
        		 String[] raw = cellLoca.toString().split(",");
        		 if(raw.length>4)
        		 {
        //			 raw[4] = "65535]";
        	     String st = raw[4].replace("]","");
        	     String sw = st.replace(" ", "");
//         	     Log.e("ffff",s);
        		 int k = Integer.parseInt(sw) / 256;
        		 int m = Integer.parseInt(sw) % 256;
        		 s = s + " "+cellLoca.toString()+" " + "eNodeB:" + k + " Cell:" + m;
        		 }
        		 else
        		 {
        	       int k = Integer.parseInt(raw[1]) / 256;
        	       int m = Integer.parseInt(raw[1]) % 256;
        	       s = s +": "+cellLoca.toString() + "eNodeB:" + k + " Cell:" + m;
        		 }
        	//	 }

        	}
        	List<NeighboringCellInfo> list = tmm.getNeighboringCellInfo();

        	if((list!=null)&&(!(list.isEmpty()))) {
        	            for (NeighboringCellInfo info : list) {
        	                int cid = info.getCid();
        	                // 获取邻居小区LAC，LAC:
        	                // 位置区域码。为了确定移动台的位置，每个GSM/PLMN的覆盖区都被划分成许多位置区，LAC则用于标识不同的位置区。
        	                int lac = info.getLac();            
        	                // 获取邻居小区信号强度
        	                int ss = -131 + 2 * info.getRssi();
        	                s = s+info.toString();
        	            }
        	 }
        //	else
        //	{
       // 		s = s+" "+getStatusChange();
        //	}
        
        }
        if(i4 == 2147483647)
        {
        s = s+" "+getStatusChange();
        }
		return s+s2;
		//
	}
	
	
	public void setStatusChange(String data)
	{
		SignalStrengths = data;
	}
	
	public static String getStatusChange()
	{
		return SignalStrengths;
	}
	
	public class dlThread implements Runnable
	{
		 public int mIndex;
		 private String  mutiTaskDLAddress ;
		 private String  mutiTaskDLUser;
		 private String  mutiTaskDLPass;
		 private String   mutiTaskDLDurationTime;   
		 private String  mutiTaskDLfileName;
		dlThread(String address,String usr,String pass,String time,String fileName,int index)
		{
			mutiTaskDLAddress = address;
			mutiTaskDLUser = usr;
			mutiTaskDLPass = pass;
			mutiTaskDLDurationTime = time;
			mIndex = index;
			mutiTaskDLfileName = fileName;
		}
		@Override
		public void run() {

			// TODO Auto-generated method stub			
			long repeatTime = Long.parseLong(mutiTaskDLDurationTime);
			long currentServerTime = System.currentTimeMillis()+systemTimeOffset;
			long endTime = currentServerTime+repeatTime;
			while(((endTime - currentServerTime)>=0)&&(!foucedown))
			{	
			if((!MultiTaskDLIsWorking[mIndex]))
			{
			MultiTaskDLIsWorking[mIndex] = true;
			MyDlTransferListenerForMultiThread one = new MyDlTransferListenerForMultiThread(mIndex);
			File localfile = new File("/data/data/com.MY.pingtest/files/net-tool.zip");
			clientdownThread[mIndex] = new FTPClient();
			try {
				clientdownThread[mIndex].connect(mutiTaskDLAddress);
				clientdownThread[mIndex].login(mutiTaskDLUser,mutiTaskDLPass);
				clientdownThread[mIndex].download(mutiTaskDLfileName, localfile,one);
	//			clientdownThread[mIndex] = myclientdown;
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block;
				MultiTaskDLIsWorking[mIndex] = false;
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				MultiTaskDLIsWorking[mIndex] = false;
				e.printStackTrace();
			} catch (FTPIllegalReplyException e) {
				// TODO Auto-generated catch block
				MultiTaskDLIsWorking[mIndex] = false;
				e.printStackTrace();
			} catch (FTPException e) {
				// TODO Auto-generated catch block
				MultiTaskDLIsWorking[mIndex] = false;
				e.printStackTrace();
			} catch (FTPDataTransferException e) {
				// TODO Auto-generated catch block
				MultiTaskDLIsWorking[mIndex] = false;
				e.printStackTrace();
			} catch (FTPAbortedException e) {
				// TODO Auto-generated catch block
				MultiTaskDLIsWorking[mIndex] = false;
				e.printStackTrace();
			}
			currentServerTime = System.currentTimeMillis()+systemTimeOffset;
			}
			}
			
			ToolSrvImpl.STrace("dlThread", "dlThread end "+ lockqueue.size());
			synchronized(fulltasklock)
			{
			lockqueue.poll();
			if(lockqueue.size() == 0)
			{
				synchronized(fulltasklock)
				{
				fulltasklock.notify();
				}
			}
			}
		
		}
		
	}
	public class ulThread implements Runnable
	{
         public int mIndex;
		 private String  mutiTaskULAddress ;
		 private String  mutiTaskULUser;
		 private String  mutiTaskULPass;
		 private String   mutiTaskULDurationTime;
		ulThread(String address,String usr,String pass,String time,int index)
		{
			mutiTaskULAddress = address;
			mutiTaskULUser = usr;
			mutiTaskULPass = pass;
			mutiTaskULDurationTime = time;
			mIndex = index;
		}
		@Override
		public void run() {

			// TODO Auto-generated method stub			
			long repeatTime = Long.parseLong(mutiTaskULDurationTime);
			long currentServerTime = System.currentTimeMillis()+systemTimeOffset;
			long endTime = currentServerTime+repeatTime;
			while(((endTime - currentServerTime)>=0)&&(!foucedown))
			{	
			if((!MultiTaskULIsWorking[mIndex]))
			{
			MultiTaskULIsWorking[mIndex] = true;
			MyUlTransferListenerForMultiThread one = new MyUlTransferListenerForMultiThread(mIndex);
			File localfile = new File("/data/data/com.MY.pingtest/upfiledemo"+IMEI);
			clientupThread[mIndex] = new FTPClient();
			try {
				clientupThread[mIndex].connect(mutiTaskULAddress);
				clientupThread[mIndex].login(mutiTaskULUser,mutiTaskULPass);
				clientupThread[mIndex].upload(localfile,one);
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block;
				MultiTaskULIsWorking[mIndex] = false;
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				MultiTaskULIsWorking[mIndex] = false;
				e.printStackTrace();
			} catch (FTPIllegalReplyException e) {
				// TODO Auto-generated catch block
				MultiTaskULIsWorking[mIndex] = false;
				e.printStackTrace();
			} catch (FTPException e) {
				// TODO Auto-generated catch block
				MultiTaskULIsWorking[mIndex] = false;
				e.printStackTrace();
			} catch (FTPDataTransferException e) {
				// TODO Auto-generated catch block
				MultiTaskULIsWorking[mIndex] = false;
				e.printStackTrace();
			} catch (FTPAbortedException e) {
				// TODO Auto-generated catch block
				MultiTaskULIsWorking[mIndex] = false;
				e.printStackTrace();
			}
			}
			currentServerTime = System.currentTimeMillis()+systemTimeOffset;
			}
			
			ToolSrvImpl.STrace("ulThread", "ulThread end "+ lockqueue.size());
			synchronized(fulltasklock)
			{
			lockqueue.poll();
			if(lockqueue.size() == 0)
			{
				synchronized(fulltasklock)
				{
				fulltasklock.notify();
				}
			}
			}
		
		}
		
	}
/*	public Runnable threadDL = new Runnable() {
	     
			@Override
			public void run() {
				// TODO Auto-generated method stub			
				long repeatTime = Long.parseLong(MutiTaskDLDurationTime);
				long currentServerTime = System.currentTimeMillis()+systemTimeOffset;
				long endTime = currentServerTime+repeatTime;
				while(((endTime - currentServerTime)>=0)&&(!foucedown))
				{	
				if((!MultiTaskDLIsWorking))
				{
				MultiTaskDLIsWorking = true;
				MyDownTransferListener one = new MyDownTransferListener();
				File fileDir =getFilesDir();
				File localfile = new File("/data/data/com.MY.pingtest/files/net-tool.zip");
				FTPClient myclientdown = new FTPClient();
				try {
					myclientdown.connect(MutiTaskDLAddress);
					myclientdown.login(MutiTaskDLUser,MutiTaskDLPassWord);
					myclientdown.download(MutiTaskDLfileName, localfile,one);
					clientdown = myclientdown;
				} catch (IllegalStateException e) {
					// TODO Auto-generated catch block;
					MultiTaskDLIsWorking = false;
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					MultiTaskDLIsWorking = false;
					e.printStackTrace();
				} catch (FTPIllegalReplyException e) {
					// TODO Auto-generated catch block
					MultiTaskDLIsWorking = false;
					e.printStackTrace();
				} catch (FTPException e) {
					// TODO Auto-generated catch block
					MultiTaskDLIsWorking = false;
					e.printStackTrace();
				} catch (FTPDataTransferException e) {
					// TODO Auto-generated catch block
					MultiTaskDLIsWorking = false;
					e.printStackTrace();
				} catch (FTPAbortedException e) {
					// TODO Auto-generated catch block
					MultiTaskDLIsWorking = false;
					e.printStackTrace();
				}
				}
				}
				synchronized(MultiTaskDLlock)
				{
				MultiTaskDLlock.notify();
				}
			}	
		};*/
		
/*		public Runnable threadUL = new Runnable() {
		     
			@Override
			public void run() {
				// TODO Auto-generated method stub	
				
				long repeatTime = Long.parseLong(MutiTaskULDurationTime);
				long currentServerTime = System.currentTimeMillis()+systemTimeOffset;
				long endTime = currentServerTime+repeatTime;
				while(((endTime - currentServerTime)>=0)&&(!foucedown))
				{	
				if((!MultiTaskULIsWorking))
				{
					MultiTaskULIsWorking = true;
				String Imei = ((TelephonyManager) getSystemService(TELEPHONY_SERVICE)).getDeviceId();
				File localfile = new File("/data/data/com.MY.pingtest/upfiledemo"+Imei);
				MyUpTransferListener one = new MyUpTransferListener();
				FTPClient myclientup = new FTPClient();
				try {
					myclientup.connect(MutiTaskULAddress);
					myclientup.login(MutiTaskULUser,MutiTaskULPassWord);
					myclientup.upload(localfile,one);
					clientUp = myclientup;
				} catch (IllegalStateException e) {
					// TODO Auto-generated catch block
					MultiTaskULIsWorking = false;
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					MultiTaskULIsWorking = false;
					e.printStackTrace();
				} catch (FTPIllegalReplyException e) {
					// TODO Auto-generated catch block
					MultiTaskULIsWorking = false;
					e.printStackTrace();
				} catch (FTPException e) {
					// TODO Auto-generated catch block
					MultiTaskULIsWorking = false;
					e.printStackTrace();
				} catch (FTPDataTransferException e) {
					// TODO Auto-generated catch block
					MultiTaskULIsWorking = false;
					e.printStackTrace();
				} catch (FTPAbortedException e) {
					// TODO Auto-generated catch block
					MultiTaskULIsWorking = false;
					e.printStackTrace();
				}
				}
			    }	
				synchronized(MultiTaskULlock)
				{
				MultiTaskULlock.notify();
				}
				}
		};*/
	
	
	    public class pingThread implements Runnable
	    {
	        public int mIndex;
	    	private String mutiTaskPINGmM;
	    	private String mutiTaskPINGPackageSizs;
	    	private String mutiTaskPINGAddress;
	    	pingThread(String mM,String packageSize,String pingAddress,int i)
	    	{
	    		mutiTaskPINGmM = mM;
	    		mutiTaskPINGPackageSizs = packageSize;
	    		mutiTaskPINGAddress = pingAddress;
	    		mIndex = i;
	    	}
			@Override
			public void run() {
				// TODO Auto-generated method stub
	            String cmdPing  = "/system/bin/ping -c" + mutiTaskPINGmM + " -s " +mutiTaskPINGPackageSizs+" "+mutiTaskPINGAddress;
	            String str;
	   //         String temp = "";
				ToolSrvImpl.STrace("PING","PING thread ["+mIndex+"] start");
				// TODO Auto-generated method stub
				postToSreen("PING"+" PING thread ["+mIndex+"] start");
				NIOsendOut("TASK;PINGACK;"+"123456789"+";start;"+IMEI+";");
				try {
					p[mIndex] = Runtime.getRuntime().exec(cmdPing);
		            BufferedReader bufferReader = new BufferedReader(new InputStreamReader(p[mIndex].getInputStream()));
		            while ((str = bufferReader.readLine()) != null) {
		     //       changePingPage(str, 0);
		      //      temp = temp+str+"\r\n";
	           //         Log.e("ping",str);
	         //           Message msg1;
	        //            msg1 = Message.obtain();
	        //            msg1.what = 0;
	        //            msg1.obj = str;
	        //            mHandler.sendMessage(msg1);              
		                }
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}     
//				ToolSrvImpl.STrace("ping", "ping dddddend "+ lockqueue.size());
				NIOsendOut("TASK;PINGACK;"+"123456789"+";end;"+IMEI+";");
				synchronized(fulltasklock)
				{
					
					lockqueue.poll();
					if(lockqueue.size() == 0)
					{
					  synchronized(fulltasklock)
					 {
					 fulltasklock.notify();
					 }
					}
					 
				}
			}
	    	
	    }
/*		public Runnable threadPing = new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				
	//	        Message msg;
	//	        msg = Message.obtain();
	//	        msg.what = 0;
	//	        msg.obj = "Ping start";    
	//	        mHandler.sendMessage(msg);  

	            String cmdPing  = "/system/bin/ping -c" + MutiTaskPINGmM + " -s " +MutiTaskPINGPackageSizs+" "+MutiTaskPINGAddress;
	            String str;
	            String temp = "";
				try {
					p = Runtime.getRuntime().exec(cmdPing);
		            BufferedReader bufferReader = new BufferedReader(new InputStreamReader(p.getInputStream()));
		            while ((str = bufferReader.readLine()) != null) {
		     //       changePingPage(str, 0);
		      //      temp = temp+str+"\r\n";
	           //         Log.e("ping",str);
	         //           Message msg1;
	        //            msg1 = Message.obtain();
	        //            msg1.what = 0;
	        //            msg1.obj = str;
	        //            mHandler.sendMessage(msg1);              
		                }
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}     
				synchronized(MultiTaskPINGlock)
				{
					MultiTaskPINGlock.notify();
				}
			 }	
			
		
		};*/
	
    public static void setMobileData(Context pContext, boolean pBoolean) {  
  	  
        try {  
      
            ConnectivityManager mConnectivityManager = (ConnectivityManager) pContext.getSystemService(Context.CONNECTIVITY_SERVICE);  
      
            Class ownerClass = mConnectivityManager.getClass();  
      
            Class[] argsClass = new Class[1];  
            argsClass[0] = boolean.class;  
      
            Method method = ownerClass.getMethod("setMobileDataEnabled", argsClass);  
      
            method.invoke(mConnectivityManager, pBoolean);  
      
        } catch (Exception e) {  
            // TODO Auto-generated catch block  
            e.printStackTrace();  
            System.out.println("移动数据设置错误: " + e.toString());  
        }  
    }  
    public void connectServer()
    {
    	if(stoprenconnect)
    	{
    		return;
    	}
		SharedPreferences settings = this.getSharedPreferences("myConfig", 0);
		String ServierAddress = settings.getString("ServierAddress", "");
		try {		
			socketStat = 0;
			mSocket = new Socket(ServierAddress,PORT);
		//	mSocket = new Socket(HOST, PORT);			
		}catch (UnknownHostException e1) {
			// TODO Auto-generated catch block			
			socketStat = 1;
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
		    socketStat = 2;
			e1.printStackTrace();
		}
		if(mSocket != null)
		{
		if(!mSocket.isConnected()||mSocket.isInputShutdown()||(socketStat!=0))
		{
			ToolSrvImpl.STrace("reconnect", "reconnecting after 10s");
			Intent mIntent = new Intent("client");
	        mIntent.putExtra("result", "Socket can't start "+ String.valueOf(socketStat));    
	        sendBroadcast(mIntent);
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}	
		if(mSocket.isConnected()&&(!mSocket.isInputShutdown())&&(!mSocket.isOutputShutdown()))
		{
		try {
			out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(     					
					mSocket.getOutputStream())), true);
			in = new BufferedReader(new InputStreamReader(mSocket.getInputStream())); 
			
		//	InputStream = mSocket.getInputStream();
//	          //  ATTACH
			if(isAttch_S)
			{
			NIOsendOut("SYSTEM;ATTACH_S;"+IMEI+";");	
			}
			else
			{
			NIOsendOut("SYSTEM;ATTACH;"+IMEI+";");			
			}
			reconnectServer = false;
	           //ATTACH
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		}
		}
    }
 
/*    public void NIOconnectServer()
    { 
     	if(stoprenconnect)
     	{
     		return;
     	}
 		SharedPreferences settings = this.getSharedPreferences("myConfig", 0);
 		String ServierAddress = settings.getString("ServierAddress", "");
     	try {
     		synchronized(socketLock)
     		{
     		selector=SelectorProvider.provider().openSelector();  
 			socketChannel = SocketChannel.open();
 			socketChannel.configureBlocking(false);  
 	        SocketAddress socketAddress = new InetSocketAddress(ServierAddress,PORT);
 	        socketChannel.connect(socketAddress);
 	        socketChannel.register(selector, SelectionKey.OP_CONNECT);  
                
 	             selector.select(5000);  
 	            ToolSrvImpl.STrace("2","2");
                 Iterator<SelectionKey> selectedKeys = selector.selectedKeys().iterator();  
                 if (selectedKeys.hasNext())  
                 {  
                     SelectionKey key = (SelectionKey) selectedKeys.next();  
                     selectedKeys.remove();  
                     if (key.isConnectable())   
                     {  
                        SocketChannel socketChannel = (SocketChannel) key.channel();  
                        socketChannel.finishConnect();    
                        ToolSrvImpl.STrace("3","3");
                     }
                 }
                 else
                 {
                	 ToolSrvImpl.STrace("4","4");
          	        socketChannel.finishConnect();
          	        selector.close();
          	    //    socketChannel.socket().shutdownInput();
          	        socketChannel.close();
          	        socketChannel = null;
                 }
                 ToolSrvImpl.STrace("5","5");
     		}
 	              	
 		} catch (IOException e) {
 			// TODO Auto-generated catch block
 			ToolSrvImpl.STrace("6","6");
 			e.printStackTrace();
 		}

 		if(isAttch_S)
 		{
 		NIOsendOut("SYSTEM;ATTACH_S;"+IMEI+";");	
 		}
 		else
 		{
 		NIOsendOut("SYSTEM;ATTACH;"+IMEI+";");			
 		}
 		reconnectServer = false;
     }
    */
    public static void listAll()
    {
    	synchronized(lock)//花时间太多？
    	{
		myMessage me = null;
    	Iterator<myMessage> it = CommandList.iterator();
    	while(it.hasNext())
    	{
    		me = it.next();
			SimpleDateFormat  formatter =   new SimpleDateFormat("yyyy.MM.dd    HH:mm:ss     ");       
			Date  curDate = new Date(Long.parseLong(me.startTime));//获取当前时间       
			
			Date  curDate1 = new Date(System.currentTimeMillis()+systemTimeOffset);//获取当前时间       
			
			String str1 = "wanted start time : "+formatter.format(curDate) + "\r\n"+"current time is "+formatter.format(curDate1);   
			
			ToolSrvImpl.STrace("message list",str1);
    	}
        }
    }
    
    class TimeThread implements Runnable
    {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			while(true)
			{
                mHandler.sendEmptyMessage(10);
         //   }			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			}
		}
    	
    }
    
    
/*	class mySocket implements Runnable
	{
		@Override
		public void run() {
			// TODO Auto-generated method stub
	//		ToolSrvImpl.STrace("process","read thread begins");
	        try {  
	            while (true) {  
	                if ((mSocket != null) && (mSocket.isConnected())) {  
	                    if (!mSocket.isInputShutdown()) {  
	                        if ((content = in.readLine()) != null) {  
	                            content += "\n";
	                            if(content.contains("OK"))
	                            {
	                          
	                            }
	                            else
	                            {
	                            Message msg;
	                            msg = Message.obtain();
	                            msg.what = 0;
	                            msg.obj = content;
	                            mHandler.sendMessage(msg);  
	                            }
	                        }
	                    }
	                } 
	                if((socketChannel != null))
	                {
	                	 ByteBuffer readBuffer = ByteBuffer.allocate(8192);
	                	 int ssize = 0;
	                	 if(( ssize= socketChannel.read(readBuffer))>0)
	                	 {
	                		 String NIOcontent = new String(readBuffer.array(),0, ssize);	  
	            //    		 ToolSrvImpl.STrace("process",NIOcontent);
	                		 NIOcontent +=  "\n";
	                         Message msg;
	                         msg = Message.obtain();
	                         msg.what = 0;
	                         msg.obj = NIOcontent;
	                         mHandler.sendMessage(msg);  
	                	 }
	                }
	                
	                Thread.sleep(1000);
	            }  
	        } catch (Exception e) {  
	            e.printStackTrace();  
	        }  
		}
		
	}*/
	
	
	class MessageHandler implements Runnable
	{
		@Override
		public void run() {
			// TODO Auto-generated method stub
				while(true)
				{
					boolean o = false;
					if((!TaskIsWorking)&&(timeup))
					{
					myMessage me = null;
		        	synchronized(lock)//花时间太多？
		        	{
					Iterator<myMessage> it = CommandList.iterator();
					if(it.hasNext())
					{
						me = it.next();
						//check the time,if not current time,set next start time ,change Taskidle to true,then TimeThread will post meassage 10 to trigger circle check
						if(Long.parseLong(me.startTime)>=(System.currentTimeMillis()+systemTimeOffset))
						{
							SimpleDateFormat  formatter =   new SimpleDateFormat("yyyy.MM.dd    HH:mm:ss     ");       
							Date  curDate = new Date(Long.parseLong(me.startTime));//获取当前时间       							
							Date  curDate1 = new Date(System.currentTimeMillis()+systemTimeOffset);//获取当前时间       							
							String str1 = "wanted start time : "+formatter.format(curDate) + "\r\n"+"current time is "+formatter.format(curDate1);						
							ToolSrvImpl.STrace("waiting",str1);
							NextStartTime = Long.parseLong(me.startTime);
							timeup = false;	
						}
						else
						{
						it.remove(); //should have this line
						o = true;
						}
					}
		        	}
		        	if(o)
		        	{
					try {
						postCommand(me);
					//	Thread.sleep(2000);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					}
					}
			    }
			
		}

		private void postCommand(myMessage next) throws IOException, InterruptedException {
			// TODO Auto-generated method stub		
			
			Intent mIntent1 = new Intent("AUTOTEST");
	        mIntent1.putExtra("show", "InServerCommand");    
	        sendBroadcast(mIntent1);	
			switch(next.convertTag())
			{
			case 1:
				handleFTPDL(next);
			break;
			case 2:
				handlePING(next);
			break;
			case 3:
				handleURL(next);//发广播 模拟点击实现？button.performClick
			break;
			case 4:
				handleFTPUL(next);
				break;
			case 5:
				handleDFU(next);
				break;
			case 6:
				handleDFD(next);
				break;
			case 7:
				handleMultiTask(next);
				break;
			case 8:
				handleOTCALL(next);
				break;
			case 9:
				handleHTTP(next);
				break;
			case 10:
				handleSWECHAT(next);
				break;
			default:
				break;
			}
			Intent mIntent = new Intent("AUTOTEST");
	        mIntent.putExtra("show", "OutServerCommand");    
	        sendBroadcast(mIntent);
	        if(foucedown)
	        {
	        	upDataTrcaeFileToServer(0);
	        }
	//		Taskidle = true;
		}
		
		private void handleMultiTask(myMessage next) {
			// TODO Auto-generated method stub
			ToolSrvImpl.STrace("process","handleMultiTask");
			Intent mIntent1 = new Intent("AUTOMESSAGE");
	        mIntent1.putExtra("show", "MultiTask start"); 
	        sendBroadcast(mIntent1);
	        
	        final MultiTaskCommand t = (MultiTaskCommand) next.realCommand;
			int loop =  t.loop;
			int fullloop = loop;
		//	sendOut("TASK;DFDACK;"+aT.refNum+";start;"+IMEI+";");
			
			Log.e("MUTILACK",t.toString());
			NIOsendOut("TASK;MUTILACK;"+next.refNum+";start;"+IMEI+";");
			
			while((loop>0)&&(!foucedown))
			{   
					loop--;
					postToSreen("current loop:"+String.valueOf(fullloop-loop));
					
					if(MultiTaskCommand.dllist.size() != 0)
					{		
						haveMultiDL = MultiTaskCommand.dllist.size();
						int j = 0;
						for(DL i : MultiTaskCommand.dllist)
						{ 
							 
					//		 MutiTaskDLAddress = i.Address;
					//		 MutiTaskDLUser = i.Usr;
					//		 MutiTaskDLPassWord = i.Password;
					//		 MutiTaskDLfileName = i.filename;
					//		 MutiTaskDLDurationTime = i.DurationTime;
							 dlThread th = new dlThread(i.Address,i.Usr,i.Password,i.DurationTime,i.filename,j);
							 j++;
							 new Thread(th).start();			
						}	
			//			MultiTaskCommand.dllist.clear();
								 //wait() here
					}
					if(MultiTaskCommand.ullist.size() != 0)
					{
						haveMultiUL = MultiTaskCommand.ullist.size();
						int j = 0;
                  //     haveMultiUL = true;
				//	   MutiTaskULAddress = t.ul.Address;
				//	   MutiTaskULUser = t.ul.Usr;
				//	   MutiTaskULPassWord = t.ul.Password;
				//	   MutiTaskULDurationTime = t.ul.DurationTime;
				//	   new Thread(threadUL).start();
						for(UL i : MultiTaskCommand.ullist)
						{ 
						 ulThread th = new ulThread(i.Address,i.Usr,i.Password,i.DurationTime,j);
						 j++;
						 new Thread(th).start();	
						}
				//		MultiTaskCommand.ullist.clear();
								 //wait() here
					}
					if(MultiTaskCommand.pinglist.size() != 0)
					{
				//		haveMultiPING = true;
				//		MutiTaskPINGAddress = t.ping.Address;
				//		MutiTaskPINGDurationTime = t.ping.DurationTime;
				//		MutiTaskPINGmM = t.ping.mM;
				//		MutiTaskPINGPackageSizs = t.ping.PackageSize;
						
					//	new Thread(threadPing).start();
						haveMultiPING = MultiTaskCommand.pinglist.size();
						int j = 0;
						for(PING i : MultiTaskCommand.pinglist)
						{ 
							pingThread th = new pingThread(i.mM,i.PackageSize,i.Address,j);
							j++;
							new Thread(th).start();
						}
						MultiTaskCommand.pinglist.clear();
				  } 
					
				         if(haveMultiDL != 0)
				         {
				         for(int i=0;i<haveMultiDL;i++)
				         {
						 synchronized(fulltasklock)
						 {
                            lockqueue.offer("1");
						 }
				         }
				         }
					     if(haveMultiUL != 0)
					     {
//					        Log.e("haveMultiUL","haveMultiUL = "+haveMultiUL);
						    for(int i=0;i<haveMultiUL;i++)
						     {
							 synchronized(fulltasklock)
							 {
								 
							 }
							 lockqueue.offer("1");
						     }
					     }
				
					         if(haveMultiPING !=0)
					         {
				//	        	 Log.e("haveMultiPING","haveMultiPING = "+haveMultiPING);
					        for(int i=0;i<haveMultiPING;i++)
					        {
							 synchronized(fulltasklock)
							 {

								 lockqueue.offer("1");
							 }
					         }
					       }
			//		  Log.e("lockqueue","lockqueue = "+lockqueue.size());
						if((haveMultiDL!=0)||(haveMultiUL!=0))
						{
							speedType = 1;
						}
					synchronized(fulltasklock)
					{
					try {
						fulltasklock.wait();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					}
				}
			NIOsendOut("TASK;MUTILACK;"+next.refNum+";end;"+IMEI+";");

	
			haveMultiDL = 0;
			haveMultiUL = 0;
			haveMultiPING = 0;
			
			if((haveMultiDL !=0) || (haveMultiUL!=0))
			{
		         cleanFtp = true;
			}
			MultiTaskCommand.ullist.clear();
			MultiTaskCommand.dllist.clear();
			MultiTaskCommand.pinglist.clear();
			
			TaskLength--;
			Intent mIntent2 = new Intent("AUTOMESSAGE");
	        mIntent2.putExtra("show", "MultiTask end");    
	        sendBroadcast(mIntent2);	
			
		}
		
		private void handleOTCALL(myMessage next)
		{
			ToolSrvImpl.STrace("process","handleOTCALL");
			Intent mIntent1 = new Intent("AUTOMESSAGE");
	        mIntent1.putExtra("show", "OTCALL start"); 
	        sendBroadcast(mIntent1);
	        
	        final OTCALLCommand t = (OTCALLCommand)next.realCommand;
			int loop =  Integer.parseInt(t.loop);
			Method method;
		//	sendOut("TASK;DFDACK;"+aT.refNum+";start;"+IMEI+";");
			NIOsendOut("TASK;OTACALLACK;"+next.refNum+";start;"+IMEI+";");
		//	
			while((loop>0)&&(!foucedown))
			{   
				if(!TaskIsWorking)
				{
				TaskIsWorking = true;	
				Log.e("loop = "," "+loop);
				loop--;
				
				//call
	        	synchronized(tasklock)
	        	{
	       // 	tasklock.wait(5000);
	        		try {
						tasklock.wait(5000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	        	}
	        	
		        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+ t.callingnumber));
		        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		        startActivity(intent); 
		        ToolSrvImpl.STrace("process","start one OTCALL");
		        try {
		        	Log.e("existingtime = "," "+t.existingtime);
		        	synchronized(tasklock)
		        	{
		       // 	tasklock.wait(5000);
		        		tasklock.wait(Long.parseLong(t.existingtime));
		        	}
				//	tasklock.wait(Long.parseLong(t.existingtime));
					method = Class.forName("android.os.ServiceManager")
					        .getMethod("getService", String.class);
			         IBinder binder = (IBinder) method.invoke(null, new Object[]{TELEPHONY_SERVICE});

			         //转换为具体的服务类(ITelephony)接口对象
	//		     ToolSrvImpl.STrace("process","time out,end ongonign call");
			         ITelephony telephony = ITelephony.Stub.asInterface(binder);
			         telephony.endCall();
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NoSuchMethodException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	        
				TaskIsWorking = false;
				}
			}
			
			
			
			
			
			 NIOsendOut("TASK;OTACALLACK;"+next.refNum+";end;"+IMEI+";");
			 speedType = 0;
			 TaskLength--;
		//	 upDataTrcaeFileToServer(0);
			 Intent mIntent2 = new Intent("AUTOMESSAGE");
		     mIntent2.putExtra("show", "OTCALL end");    
		     sendBroadcast(mIntent2);
		}

		private void handleFTPUL(myMessage next) {
			// TODO Auto-generated method stub
			ToolSrvImpl.STrace("process","handleFTPUL");
			Intent mIntent1 = new Intent("AUTOMESSAGE");
	        mIntent1.putExtra("show", "FTPUL start");    
	        sendBroadcast(mIntent1);	
			try {
				foutFTP= new FileOutputStream(fileFTP,false);
				SimpleDateFormat  formatter =   new SimpleDateFormat("yyyy.MM.dd    HH:mm:ss     ");       
				Date  curDate = new Date(System.currentTimeMillis());//获取当前时间       
				String str1 = "new FTP report start time : "+formatter.format(curDate) + "\r\n";   	
				try {
				if(foutFTP != null)
					foutFTP.write(str1.getBytes());
				} catch (IOException e) {
					// TODO Auto-generated catch block
				e.printStackTrace();
				}
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} 
			String Imei = ((TelephonyManager) getSystemService(TELEPHONY_SERVICE)).getDeviceId();
			NIOsendOut("TASK;FTPULACK;"+next.refNum+";start;"+Imei+";");
			speedType = 2;
	//		clientUp = new FTPClient();
			long repeatTime = Long.parseLong(next.endTime);
			long currentServerTime = System.currentTimeMillis()+systemTimeOffset;
			long endTime = currentServerTime+repeatTime;
			
			startTime = System.currentTimeMillis();
            startstop = true;
	//		calculateFtpStartSpeed();
			while(((endTime - currentServerTime)>=0)&&(!foucedown))
			{
             if((!TaskIsWorking)&&(!reconnectServer))
             {
               TaskIsWorking = true;
               MyUpTransferListener UpLoaderListener = new MyUpTransferListener();
               File localfile = new File("/data/data/com.MY.pingtest/upfiledemo"+Imei);
			   clientUp = new FTPClient();
			   FtpULCommand t = (FtpULCommand) next.realCommand;
			   try {
				 if(clientUp != null)
				 {
				clientUp.connect(t.Address);
				clientUp.login(t.Usr,t.Password);
			    clientUp.upload(localfile,UpLoaderListener);
				 }
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
			//   currentServerTime = System.currentTimeMillis()+systemTimeOffset;
           //    currentServerTime = ftpulDoneTime+systemTimeOffset;  //611
            }
             currentServerTime = System.currentTimeMillis()+systemTimeOffset;
			}
//			 calculateFtpEndSpeed();
			NIOsendOut("TASK;FTPULACK;"+next.refNum+";end;"+IMEI+";");
			speedType = 0;
			 TaskLength--;
			 cleanFtp = true;
		//	 upDataTrcaeFileToServer(0);
			 Intent mIntent2 = new Intent("AUTOMESSAGE");
		     mIntent2.putExtra("show", "FTPUL end");    
		     sendBroadcast(mIntent2);	
	//		 startstop = false;
		}

		private void handleURL(myMessage next) throws InterruptedException {
			// TODO Auto-generated method stub
			ToolSrvImpl.STrace("process","handleURL");
			Intent mIntent = new Intent("AUTOMESSAGE");
	        mIntent.putExtra("show", "URL start");    
	        sendBroadcast(mIntent);	
			String Imei = ((TelephonyManager) getSystemService(TELEPHONY_SERVICE)).getDeviceId();
			NIOsendOut("TASK;URLACK;"+next.refNum+";start;"+Imei+";");
			TaskIsWorking = true;

			UrlCommand t = (UrlCommand) next.realCommand;
			Intent mIntent1 = new Intent("AUTOMESSAGE");
	        mIntent1.putExtra("result", "URLUP;"+t.Address);    
	        sendBroadcast(mIntent1);	
	        
	        Thread.sleep(5000);
	        
			long repeatTime = Integer.parseInt(next.endTime);
			long currentServerTime = System.currentTimeMillis()+systemTimeOffset;
			long endTime = currentServerTime+repeatTime;
			while(((endTime - currentServerTime)>=0)&&(!foucedown))
			{
				if(Urlidle)
				{
			//		Log.i("Urlidle","Urlidle");
					currentServerTime = System.currentTimeMillis()+systemTimeOffset;
			        Urlidle = false;
				}
			}
			if(foucedown)
			{
                Message msg;
                msg = Message.obtain();
                msg.what = 5;
                msg.obj = "ongoing URL foucedown\n";
                mHandler.sendMessage(msg);  
			}
			Intent mIntent3 = new Intent("AUTOMESSAGE");
		    mIntent3.putExtra("result","URLDONE");    
			sendBroadcast(mIntent3);	
			TaskIsWorking = false;
			NIOsendOut("TASK;URLACK;"+next.refNum+";end;"+Imei+";");
			TaskLength--;
		}

		private void handlePING(myMessage next) throws IOException, InterruptedException {
			// TODO Auto-generated method stub
			ToolSrvImpl.STrace("process","handlePING");
			Intent mIntent1 = new Intent("AUTOMESSAGE");
	        mIntent1.putExtra("show", "PING start");    
	        sendBroadcast(mIntent1);	
			foutPing= new FileOutputStream(fileIP,false); 
			NIOsendOut("TASK;PINGACK;"+next.refNum+";start;"+IMEI+";");
			TaskIsWorking = true;
			String Imei = ((TelephonyManager) getSystemService(TELEPHONY_SERVICE)).getDeviceId();
			long repeatTime = Long.parseLong(next.endTime);
			long currentServerTime = System.currentTimeMillis()+systemTimeOffset;
			long endTime = currentServerTime+repeatTime;
			while(((endTime - currentServerTime)>=0)&&(!foucedown))
			{
			SimpleDateFormat  formatter =   new SimpleDateFormat("yyyy.MM.dd    HH:mm:ss     ");       
			Date  curDate = new Date(System.currentTimeMillis());//获取当前时间       
			String str1 = "new IP report start time : "+formatter.format(curDate) + "\r\n";   	
			try {
			if(foutPing != null)
			   foutPing.write(str1.getBytes());
			} catch (IOException e) {
				// TODO Auto-generated catch block
			e.printStackTrace();
			}
			String str;
			PingCommand t = (PingCommand) next.realCommand;	
        	String cmdPing  = "/system/bin/ping -c "+ t.mM +" -s " + t.PackageSize+" "+t.Address;
            Process p = Runtime.getRuntime().exec(cmdPing);
            BufferedReader bufferReader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            while ((str = bufferReader.readLine()) != null) {
                Message msg;
                msg = Message.obtain();
                msg.what = 4;
                msg.obj = str;
                mHandler.sendMessage(msg);     
//            	Log.i("PING",str);
            }
            str="";
//            bufferReader.close();
            Thread.sleep(2000);
            currentServerTime = System.currentTimeMillis()+systemTimeOffset;
			}
			NIOsendOut("TASK;PINGACK;"+next.refNum+";end;"+Imei+";");
			TaskLength--;
	//		upDataTrcaeFileToServer(1);
			Intent mIntent2 = new Intent("AUTOMESSAGE");
	        mIntent2.putExtra("show", "PING end");    
	        sendBroadcast(mIntent2);	
			if(foucedown)
			{
                Message msg;
                msg = Message.obtain();
                msg.what = 4;
                msg.obj = "ongoing PING foucedown\n";
                mHandler.sendMessage(msg);  
			}
			TaskIsWorking = false;
		}

		private void handleFTPDL(final myMessage aT)
		{
			ToolSrvImpl.STrace("process","handleFTPDL");
			Intent mIntent1 = new Intent("AUTOMESSAGE");
	        mIntent1.putExtra("show", "FTPDL start");    
	        sendBroadcast(mIntent1);	
			try {
				foutFTP= new FileOutputStream(fileFTP,false);
				SimpleDateFormat  formatter =   new SimpleDateFormat("yyyy.MM.dd    HH:mm:ss     ");       
				Date  curDate = new Date(System.currentTimeMillis());//获取当前时间       
				String str1 = "new FTP report start time : "+formatter.format(curDate) + "\r\n";   	
				try {
				if(foutFTP != null)
					foutFTP.write(str1.getBytes());
				} catch (IOException e) {
					// TODO Auto-generated catch block
				e.printStackTrace();
				}
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} 
			String Imei = ((TelephonyManager) getSystemService(TELEPHONY_SERVICE)).getDeviceId();
			NIOsendOut("TASK;FTPDLACK;"+aT.refNum+";start;"+Imei+";");
			speedType = 1;
		//	cleanFtp = true;
		//	clientdown = new FTPClient();
			long repeatTime = Long.parseLong(aT.endTime);
			long currentServerTime = System.currentTimeMillis()+systemTimeOffset;
			long endTime = currentServerTime+repeatTime;
			startTime = System.currentTimeMillis();
      //      startstop = true;
	//		calculateFtpStartSpeed();
			while(((endTime - currentServerTime)>=0)&&(!foucedown))
			{
			if((!TaskIsWorking)&&(!reconnectServer))
			{
				TaskIsWorking = true;
	//		new Thread(new Runnable()   //611
		//			{
		//				@Override
		//				public void run() {
							// TODO Auto-generated method stub
						//	TaskIsWorking = true;
							FtpDLCommand t = (FtpDLCommand) aT.realCommand;
							File localfile = new File("/data/data/com.MY.pingtest/files/net-tool.zip");
							MyDownTransferListener DownLoaderListener = new MyDownTransferListener();
							clientdown = new FTPClient();
						    try {
						    	if(clientdown != null)
						    	{
								clientdown.connect(t.Address);
								clientdown.login(t.Usr,t.Password);
								clientdown.download(t.filename, localfile,DownLoaderListener);
						    	}
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
			//			}
			//		}).start();
		 //    currentServerTime = System.currentTimeMillis()+systemTimeOffset;
		//	 currentServerTime = ftpdlDoneTime + systemTimeOffset;	
			}
			 currentServerTime = System.currentTimeMillis()+systemTimeOffset;
			}
	//		calculateFtpEndSpeed();
			NIOsendOut("TASK;FTPDLACK;"+aT.refNum+";end;"+IMEI+";");
			speedType = 0;
			TaskLength--;
			cleanFtp = true;
	//		Log.e("sockedt",String.valueOf(TaskLength));
	//		upDataTrcaeFileToServer(0);
			Intent mIntent2 = new Intent("AUTOMESSAGE");
	        mIntent2.putExtra("show", "FTPDL end");    
	        sendBroadcast(mIntent2);	
	//		startstop = false;
		}
		
		private void handleDFU(final myMessage aT)
		{
			synchronized(RawData.Tlock)
			{
				rebootServiceForTaskDF = true;
			}
			ToolSrvImpl.STrace("process","handleDFU");
			Intent mIntent1 = new Intent("AUTOMESSAGE");
	        mIntent1.putExtra("show", "DFU start");    
	        sendBroadcast(mIntent1);	
	        NIOsendOut("TASK;DFUACK;"+aT.refNum+";start;"+IMEI+";");
	        speedType = 2;
			DFUCommand t = (DFUCommand) aT.realCommand;
			int loop =  Integer.parseInt(t.loop);
			int fullloop = loop;
			while((loop>0)&&(!foucedown))
			{   
				if(!TaskIsWorking)
				{
				TaskIsWorking = true;	
				loop--;
				//1 close socket
				try {
					stoprenconnect = true;
					if(mSocket != null)
					{
				//    mSocket.shutdownOutput();
				//	mSocket.getInputStream().close();
				    mSocket.shutdownInput();
					mSocket.close();
				//	interrupt = false;
					mSocket = null;
					}
					if((loop+1) != fullloop)
					DisconnnetServer();
				//	SocketService.disconnectServer();
				//	if(out != null)
				///	{
				//	out.close();
				//	out = null;
			//		}
			//		if(in != null)
			//		{
			//		in.close();
			//		in.reset();
			//		in = null;
		      //   	}
					postToSreen("current loop:"+String.valueOf(fullloop-loop));	
				} catch (IOException e) {
					// TODO Auto-generated catch block
					TaskIsWorking = false;	
					ToolSrvImpl.STrace("processDFU","IOException");
					e.printStackTrace();
				}
				if((loop+1) != fullloop)
				{
				synchronized(pvlock)
				{
				ToolSrvImpl.STrace("process","pvlock.wait start");
				postToSreen("pvlock.wait start");
				try {
					pvlock.wait(Integer.parseInt(t.DetachTimer));
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					ToolSrvImpl.STrace("processDFU","NumberFormatException");
					e.printStackTrace();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					TaskIsWorking = false;	
					ToolSrvImpl.STrace("processDFU","InterruptedException");
					e.printStackTrace();
				}
				ToolSrvImpl.STrace("process","pvlock.wait end");
				postToSreen("pvlock.wait end");
				}	
				}
				//2 reconnnect server
				stoprenconnect = false;
				
				ConnnetServer();
			//	isAttch_S = true;
			//	do
			//	{
			//	if(!SocketService.isSocketWokring())
			//		SocketService.NIOconnectServer();
			//	}while((socketChannel == null)|| !socketChannel.isConnected());
			//	do{
			//		connectServer();
		    // 	}while((mSocket==null)||(!mSocket.isConnected()||mSocket.isInputShutdown()||(socketStat!=0)));
				isAttch_S = false;
				// 3 send out ATTACH_S
				
				//4 do FTPUL
	               MyUpTransferListener UpLoaderListener = new MyUpTransferListener();
	               File localfile = new File("/data/data/com.MY.pingtest/upfiledemo"+IMEI);
				    clientUp = new FTPClient();
					try {
						clientUp.connect(t.Address);
						clientUp.login(t.Usr,t.Password);
					    DFstopTimer = Integer.parseInt(t.DetachTimer)/1000;
					    isTaskDF = true;
					    ToolSrvImpl.STrace("process","handleDFU UL start");
					    clientUp.upload(localfile,UpLoaderListener);
					} catch (IllegalStateException e) {
						// TODO Auto-generated catch block
						TaskIsWorking = false;
						isTaskDF = false;
						ToolSrvImpl.STrace("processDFU","IllegalStateException");
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						TaskIsWorking = false;
						isTaskDF = false;
						ToolSrvImpl.STrace("processDFU","IOException");
						e.printStackTrace();
					} catch (FTPIllegalReplyException e) {
						// TODO Auto-generated catch block
						TaskIsWorking = false;
						isTaskDF = false;
						ToolSrvImpl.STrace("processDFU","FTPIllegalReplyException");
						e.printStackTrace();
					} catch (FTPException e) {
						// TODO Auto-generated catch block
						TaskIsWorking = false;
						isTaskDF = false;
						ToolSrvImpl.STrace("processDFU","FTPException");
						e.printStackTrace();
					} catch (FTPDataTransferException e) {
						// TODO Auto-generated catch block
						TaskIsWorking = false;
						isTaskDF = false;
						ToolSrvImpl.STrace("processDFU","FTPDataTransferException");
						e.printStackTrace();
					} catch (FTPAbortedException e) {
						// TODO Auto-generated catch block
						TaskIsWorking = false;
						isTaskDF = false;
						ToolSrvImpl.STrace("processDFU","FTPAbortedException");
						e.printStackTrace();
					} catch (NumberFormatException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			}
			}
			NIOsendOut("TASK;DFUACK;"+aT.refNum+";end;"+IMEI+";");
			speedType = 0;
			cleanFtp = true;
			TaskLength--;
		//	TaskIsWorking = false;
			Intent mIntent2 = new Intent("AUTOMESSAGE");
	        mIntent2.putExtra("show", "DFU end");    
	        sendBroadcast(mIntent2);	
	        isTaskDF = false;
		}
		

		private void handleDFD(final myMessage aT)
		{
			synchronized(RawData.Tlock)
			{
				rebootServiceForTaskDF = true;
			}
			ToolSrvImpl.STrace("process","handleDFD");
			Intent mIntent1 = new Intent("AUTOMESSAGE");
	        mIntent1.putExtra("show", "DFD start");    
	        sendBroadcast(mIntent1);	
	        
			DFDCommand t = (DFDCommand) aT.realCommand;
			int loop =  Integer.parseInt(t.loop);
			int fullloop = loop;
		//	sendOut("TASK;DFDACK;"+aT.refNum+";start;"+IMEI+";");
			NIOsendOut("TASK;DFDACK;"+aT.refNum+";start;"+IMEI+";");
			speedType = 1;
	//		Log.e("1"," "+t.DetachTimer + foucedown + TaskIsWorking);
			while((loop>0)&&(!foucedown))
			{   
				if(!TaskIsWorking)
				{
				TaskIsWorking = true;	
				loop--;
				//1 close socket
				try {
					stoprenconnect = true;
					if(out != null)
					out.close();
					if(in != null)
					{
					in.close();
				//	in.reset();
					in  = null;
					}
					if(mSocket != null)
					{
			    //    mSocket.shutdownOutput();
				//	mSocket.shutdownInput();
					mSocket.close();
			//		interrupt = false;
					}
					//SocketService.disconnectServer();
					if((loop+1) != fullloop)
					DisconnnetServer();
					postToSreen("current loop:"+String.valueOf(fullloop-loop));

				} catch (IOException e) {
					// TODO Auto-generated catch block
					TaskIsWorking = false;
					ToolSrvImpl.STrace("processDFD","IOException");
					e.printStackTrace();
				}
				
				if(((loop+1) != fullloop)||(fullloop == 1))
				{
				synchronized(pvlock)
				{
				ToolSrvImpl.STrace("process","pvlock.wait start");
				postToSreen("pvlock.wait start");
				try {
					pvlock.wait(Integer.parseInt(t.DetachTimer));
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					TaskIsWorking = false;
					ToolSrvImpl.STrace("processDFD","NumberFormatException");
					e.printStackTrace();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					TaskIsWorking = false;
					ToolSrvImpl.STrace("processDFD","InterruptedException");
					e.printStackTrace();
				}
				ToolSrvImpl.STrace("process","pvlock.wait end");
				postToSreen("pvlock.wait end");
				}
				}
				//2 reconnnect server
				stoprenconnect = false;
				ConnnetServer();
				
		//		isAttch_S = true;
		//		do{
		//			connectServer();
		//     	}while((mSocket==null)||(!mSocket.isConnected()||mSocket.isInputShutdown()||(socketStat!=0)));
				
			//	do
			//	{
			//	if(!SocketService.isSocketWokring())
			//		SocketService.NIOconnectServer();
			//	}while((socketChannel == null)|| !socketChannel.isConnected());
				isAttch_S = false;
				// 3 send out ATTACH_S
				//4 do FTPUL
	//			long currentServerTime = System.currentTimeMillis()+systemTimeOffset;
	//			long endTime = currentServerTime+Integer.parseInt(t.DetachTimer);
				//需要一个标志位来，只有DL完成或失败以后再做下一次操作，这里是异步的
	//			long currentServerTime = System.currentTimeMillis()+systemTimeOffset;
		
			      //      startstop = true;
				//		calculateFtpStartSpeed();
		//		while(((endTime - (System.currentTimeMillis()+systemTimeOffset))>=0)&&(!foucedown))
	//			{
				File localfile = new File("/data/data/com.MY.pingtest/files/net-tool.zip");
				MyDownTransferListener DownLoaderListener = new MyDownTransferListener();
				clientdown = new FTPClient();
			    try {
					clientdown.connect(t.Address);
					clientdown.login(t.Usr,t.Password);
					DFstopTimer = Integer.parseInt(t.DetachTimer)/1000;
					isTaskDF = true;
					ToolSrvImpl.STrace("process","handleDFU DL start");
					clientdown.download(t.filename, localfile,DownLoaderListener);					
					
					} catch (IllegalStateException e) {
						// TODO Auto-generated catch block
						TaskIsWorking = false;
						isTaskDF = false;
						ToolSrvImpl.STrace("processDFD","IllegalStateException");
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						TaskIsWorking = false;
						isTaskDF = false;
						ToolSrvImpl.STrace("processDFD","IOException");
						e.printStackTrace();
					} catch (FTPIllegalReplyException e) {
						// TODO Auto-generated catch block
						TaskIsWorking = false;
						isTaskDF = false;
						ToolSrvImpl.STrace("processDFD","FTPIllegalReplyException");
						e.printStackTrace();
					} catch (FTPException e) {
						// TODO Auto-generated catch block
						TaskIsWorking = false;
						isTaskDF = false;
						ToolSrvImpl.STrace("processDFD","FTPException");
						e.printStackTrace();
					} catch (FTPDataTransferException e) {
						// TODO Auto-generated catch block
						TaskIsWorking = false;
						isTaskDF = false;
						ToolSrvImpl.STrace("processDFD","FTPDataTransferException");
						e.printStackTrace();
					} catch (FTPAbortedException e) {
						// TODO Auto-generated catch block
						ToolSrvImpl.STrace("processDFD","FTPAbortedException");
						TaskIsWorking = false;
						isTaskDF = false;
						e.printStackTrace();
					}
				 }
			//	}
		//	TaskIsWorking = false;
				isTaskDF = false;
			}
			NIOsendOut("TASK;DFDACK;"+aT.refNum+";end;"+IMEI+";");
			speedType = 0;
		//	sendOut("TASK;DFDACK;"+aT.refNum+";end;"+IMEI+";");
			cleanFtp = true;
			TaskLength--;
			Intent mIntent2 = new Intent("AUTOMESSAGE");
	        mIntent2.putExtra("show", "DFD end");    
	        sendBroadcast(mIntent2);	
		}
		private void handleHTTP(myMessage next){
			ToolSrvImpl.STrace("process","handleHTTP");
			Intent mIntent1 = new Intent("AUTOMESSAGE");
	        mIntent1.putExtra("show", "HTTP start");    
	        sendBroadcast(mIntent1);	
	        
			HTTPCommand t = (HTTPCommand) next.realCommand;
		//	int loop =  Integer.parseInt(t.loop);
		//	int fullloop = loop;
	//		sendOut("TASK;HTTPACK;"+next.refNum+";start;"+IMEI+";");
			Log.e(" ",String.valueOf(next.realCommand));
			String address = t.httpAddress;
			String interval = t.interavl;
			long t1 = 0;
			long t2 = 0;
			 if(!address.contains("http://"))
				 address = "http://"+address;
			 int  loop = 0;
			 
             while(!foucedown)
             {
	//		webView1.loadUrl("http://www.163.com");  //work
			 
			try {
				//step 1 test 200 OK
				loop++;
				URL uri = new URL(address);
				kpi.totalHttpGet++;
				kpi.partHttpGet++;
				HttpURLConnection conn;
				InputStream is;
				conn = (HttpURLConnection) uri.openConnection();
				t1 = System.currentTimeMillis();
				is = conn.getInputStream();
				t2 = System.currentTimeMillis();
				int code = conn.getResponseCode();
				if(code == 200)
				{
					kpi.totalHttp200++;
					kpi.partHttp200++;
					kpi.time200.add(String.valueOf(t2-t1));
				}
			
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			//step 2 
   		    Intent mIntent = new Intent("URL");
            mIntent.putExtra("data",address);    
            sendBroadcast(mIntent);
            HttpSpeed = true;
            
            synchronized(lockNotifyNextHttp)
            {
            	try {
            		lockNotifyNextHttp.wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
             String valuep =  kpi.partResult(loop);
             Log.e("kpi.avgspeed1"," "+kpi.avgspeed1);
             kpi.avgspeed1="";
			 Intent mIntent2 = new Intent("AUTOMESSAGE");
		     mIntent2.putExtra("show", "HTTP report");    
		     sendBroadcast(mIntent2);
			NIOsendOut("TASK;HTTPACK;"+next.refNum+";start;"+IMEI+";"+valuep+";");
    //        kpi.calcSpeedD();
	//		kpi.clean();
            synchronized(lockHttpInterval)
            {
            try {
            	lockHttpInterval.wait(Long.parseLong(interval));
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            }
            }//end while
             
      //      String value = kpi.result();
            TaskLength--;
            
			 Intent mIntent2 = new Intent("AUTOMESSAGE");
		     mIntent2.putExtra("show", "HTTP end");    
		     sendBroadcast(mIntent2);
			NIOsendOut("TASK;HTTPACK;"+next.refNum+";end;"+IMEI+";");
			kpi.clean();
			speedType = 0;
		}
		private void handleSWECHAT(myMessage next)
		{
			ToolSrvImpl.STrace("process","handleWECHAT");
			Intent mIntent1 = new Intent("AUTOMESSAGE");
	        mIntent1.putExtra("show", "WECHAT start");    
	        sendBroadcast(mIntent1);	
	        
	        SWECHATCommand t = (SWECHATCommand) next.realCommand;
			
	        long interval = Long.parseLong(t.interavl);
	        //step 1 stop the KA message 
	        NIOsendOut("TASK;SWECHATACK;"+next.refNum+";start;"+IMEI+";");
	        intervalKA = true;
	        while(!foucedown)
	        {
	        synchronized(lockwechat)
	        {
	        	try {
					lockwechat.wait(interval);
					mHandler.sendEmptyMessage(40);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					
					e.printStackTrace();
				}
	        }
	        }
	       /* while(!foucedown)
	        {
			if(!TaskIsWorking)
			{
			TaskIsWorking = true;	
	        stoprenconnect = true;
	        DisconnnetServer();
			ToolSrvImpl.STrace("process","pvlock.wait start");
			postToSreen("pvlock.wait start");
	        
	        synchronized(lockwechat)
	        {
	        	try {
					lockwechat.wait(interval);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					
					e.printStackTrace();
				}
	        }
	        
			ToolSrvImpl.STrace("process","pvlock.wait end");
			postToSreen("pvlock.wait end");
			stoprenconnect = false;
			ConnnetServer();
	        }
	        }*/
	        TaskIsWorking = false;
	        
	        NIOsendOut("TASK;SWECHAT ACK;"+next.refNum+";end;"+IMEI+";");
			 speedType = 0;
			 TaskLength--;
		//	 upDataTrcaeFileToServer(0);
			 Intent mIntent2 = new Intent("AUTOMESSAGE");
		     mIntent2.putExtra("show", "WECHAT end");    
		     intervalKA = false;
		     sendBroadcast(mIntent2);
		}
		
	}
	public class MyDownTransferListener implements FTPDataTransferListener
	{

		@Override
		public void aborted() {
			// TODO Auto-generated method stub
			ToolSrvImpl.STrace("FTPDL","DL abort");
			postToSreen("FTPDL DL abort");
			TaskIsWorking = false;
	//		if(haveMultiDL)
	//		MultiTaskDLIsWorking = false;
			isTaskDF = false;
			ftpdlDoneTime = System.currentTimeMillis();
			mHandler.sendEmptyMessage(22);
			startstop = false;
		}

		@Override
		public void completed() {
			// TODO Auto-generated method stub
			ToolSrvImpl.STrace("FTPDL","DL done");
			postToSreen("FTPDL DL done");
			TaskIsWorking = false;
	//		if(haveMultiDL)
	//		MultiTaskDLIsWorking = false;
			isTaskDF = false;
			ftpdlDoneTime = System.currentTimeMillis();
			mHandler.sendEmptyMessage(24);
			startstop = false;
		}

		@Override
		public void failed() {
			// TODO Auto-generated method stub
			ToolSrvImpl.STrace("FTPDL","DL failed");
			postToSreen("FTPDL DL failed");
			TaskIsWorking = false;
	//		if(haveMultiDL)
	//		MultiTaskDLIsWorking = false;
			isTaskDF = false;
			ftpdlDoneTime = System.currentTimeMillis();
			mHandler.sendEmptyMessage(22);
			startstop = false;
		}

		@Override
		public void started() {
			ToolSrvImpl.STrace("FTPDL","DL start");
			// TODO Auto-generated method stub
			postToSreen("FTPDL DL start");
			mHandler.sendEmptyMessage(23);
			 startstop = true;
		}

		@Override
		public void transferred(int arg0) {
			// TODO Auto-generated method stub
            mHandler.sendEmptyMessage(21);
		}
		
	}
	
	public class MyDlTransferListenerForMultiThread implements FTPDataTransferListener
	{
		public int mindex;
		MyDlTransferListenerForMultiThread(int index)
		{
			mindex = index;
		}
		@Override
		public void aborted() {
			// TODO Auto-generated method stub
			ToolSrvImpl.STrace("FTPDL","DL thread ["+mindex+"] aborted");
			// TODO Auto-generated method stub
			postToSreen("FTPDL"+" DL thread ["+mindex+"] aborted");
			MultiTaskDLIsWorking[mindex] = false;
			NIOsendOut("TASK;FTPDLACK;"+"123456789"+";aborted;"+IMEI+";");
		}

		@Override
		public void completed() {
			// TODO Auto-generated method stub
			ToolSrvImpl.STrace("FTPDL","DL thread ["+mindex+"] completed");
			// TODO Auto-generated method stub
			postToSreen("FTPDL"+" DL thread ["+mindex+"] completed");
			MultiTaskDLIsWorking[mindex] = false;
			NIOsendOut("TASK;FTPDLACK;"+"123456789"+";completed;"+IMEI+";");
			
		}

		@Override
		public void failed() {
			// TODO Auto-generated method stub
			ToolSrvImpl.STrace("FTPDL","DL thread ["+mindex+"] failed");
			// TODO Auto-generated method stub
			postToSreen("FTPDL"+" DL thread ["+mindex+"] failed");
			MultiTaskDLIsWorking[mindex] = false;
			NIOsendOut("TASK;FTPDLACK;"+"123456789"+";failed;"+IMEI+";");
		}

		@Override
		public void started() {
			// TODO Auto-generated method stub
			ToolSrvImpl.STrace("FTPDL","DL thread ["+mindex+"] start");
			// TODO Auto-generated method stub
			postToSreen("FTPDL"+" DL thread ["+mindex+"] start");
			NIOsendOut("TASK;FTPDLACK;"+"123456789"+";start;"+IMEI+";");
		}

		@Override
		public void transferred(int arg0) {
			// TODO Auto-generated method stub
			
		}
		
	}
	public class MyUlTransferListenerForMultiThread implements FTPDataTransferListener
	{
		public int mIndex;
		MyUlTransferListenerForMultiThread(int i)
		{
			mIndex = i;
		}
		@Override
		public void aborted() {
			// TODO Auto-generated method stub
			ToolSrvImpl.STrace("FTPUL","UL thread ["+mIndex+"] aborted");
			// TODO Auto-generated method stub
			postToSreen("FTPUL"+" UL thread ["+mIndex+"] aborted");
			MultiTaskULIsWorking[mIndex] = false;
			NIOsendOut("TASK;FTPULACK;"+"123456789"+";aborted;"+IMEI+";");
		}

		@Override
		public void completed() {
			// TODO Auto-generated method stub
			ToolSrvImpl.STrace("FTPUL","UL thread ["+mIndex+"] completed");
			// TODO Auto-generated method stub
			postToSreen("FTPUL"+" UL thread ["+mIndex+"] completed");
			MultiTaskULIsWorking[mIndex] = false;
			NIOsendOut("TASK;FTPULACK;"+"123456789"+";completed;"+IMEI+";");
		}

		@Override
		public void failed() {
			// TODO Auto-generated method stub
			// TODO Auto-generated method stub
			ToolSrvImpl.STrace("FTPUL","UL thread ["+mIndex+"] failed");
			// TODO Auto-generated method stub
			postToSreen("FTPUL"+" UL thread ["+mIndex+"] failed");
			MultiTaskULIsWorking[mIndex] = false;
			NIOsendOut("TASK;FTPULACK;"+"123456789"+";failed;"+IMEI+";");
		}

		@Override
		public void started() {
			// TODO Auto-generated method stub
			ToolSrvImpl.STrace("FTPDL","UL thread ["+mIndex+"] start");
			// TODO Auto-generated method stub
			postToSreen("FTPDL"+" UL thread ["+mIndex+"] start");
			NIOsendOut("TASK;FTPULACK;"+"123456789"+";start;"+IMEI+";");
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
			ToolSrvImpl.STrace("FTPUL","UL abort");
			postToSreen("FTPUL UL abort");
			TaskIsWorking = false;
	//		if(haveMultiUL)
	//		MultiTaskULIsWorking = false;
			isTaskDF = false;
			ftpulDoneTime = System.currentTimeMillis();
			mHandler.sendEmptyMessage(32);
			startstop = false;
		}

		@Override
		public void completed() {
			// TODO Auto-generated method stub
			ToolSrvImpl.STrace("FTPUL","UL done");
			postToSreen("FTPUL UL done");
	//		Log.e("Up done","Up done");
			TaskIsWorking = false;
	//		if(haveMultiUL)
	//		MultiTaskULIsWorking = false;
			isTaskDF = false;
			ftpulDoneTime = System.currentTimeMillis();
			mHandler.sendEmptyMessage(34);
			startstop = false;
		}

		@Override
		public void failed() {
			// TODO Auto-generated method stub
			Log.e("Up failed","Up failed");
			ToolSrvImpl.STrace("FTPUL","UL failed");
			postToSreen("FTPUL UL failed");
			TaskIsWorking = false;
	//		if(haveMultiUL)
	//		MultiTaskULIsWorking = false;
			isTaskDF = false;
			ftpulDoneTime = System.currentTimeMillis();
			mHandler.sendEmptyMessage(32);
			startstop = false;
		}

		@Override
		public void started() {
			// TODO Auto-generated method stub
			ToolSrvImpl.STrace("FTPUL","UL start");
			postToSreen("FTPUL UL start");
			mHandler.sendEmptyMessage(33);
			 startstop = true;
		}

		@Override
		public void transferred(int arg0) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	public class AUTransferListener implements FTPDataTransferListener
	{

		@Override
		public void aborted() {
			// TODO Auto-generated method stub
			NIOsendOut("INSTANCE;AU;"+"aborted;"+IMEI+";");
        	synchronized(AUlock)
        	{
			AUlock.notify();
        	}
		}

		@Override
		public void completed() {
			// TODO Auto-generated method stub
			AUDownload = true;
	//		Log.e("AU completed","AU completed");
			NIOsendOut("INSTANCE;AU;"+"download complete;"+IMEI+";");
        	synchronized(AUlock)
        	{
			AUlock.notify();
        	}
		}

		@Override
		public void failed() {
	//		// TODO Auto-generated method stub
			NIOsendOut("INSTANCE;AU;"+"failed;"+IMEI+";");
        	synchronized(AUlock)
        	{
			AUlock.notify();
        	}
		}

		@Override
		public void started() {
			// TODO Auto-generated method stub
			NIOsendOut("INSTANCE;AU;"+"start;"+IMEI+";");
	//		Log.e("AU start","AU start");
		}

		@Override
		public void transferred(int arg0) {
			// TODO Auto-generated method stub
		
		}
		
	}
	
    public Handler mHandler = new Handler() {  
        public void handleMessage(Message msg) {  
        	switch (msg.what) {
        	case 0:
        	{   
//        		TaskIsWorking = false;
        		String Command = msg.obj.toString();
   //     		Log.e("received",Command);
         		ToolSrvImpl.STrace("RECEIVED",Command);
        		if(Command.contains("INTRA"));
        		{
        			postINTRACommand(Command);
        		}
        		if(Command.contains("SYSTEM"))
        		{
        			postSystemCommand(Command);
        		}
        		else if(Command.contains("INSTANCE"))
        		{
        		foucedown = false;
        	 	synchronized(lock)
            	{
            	//分解instance 
            	String[] rt = Command.split("!");
            	int TaskLen = rt.length-2;
            	TaskLength = TaskLen;
       //     	Log.e("socket",String.valueOf(TaskLen));
            	while(TaskLen>0)
            	{
           // 		Log.i("socket",rt[TaskLen]);
            		myMessage mMessage = new myMessage(rt[TaskLen]);
          //  		Log.e("cccc","0"+mMessage.tag+"1"+mMessage.refNum+"2"+mMessage.startTime+"3"+mMessage.endTime+"4"+mMessage.command);
            		CommandList.add(mMessage);           		
            	    TaskLen--;
            	}
            //	listAll();
            	String Imei = ((TelephonyManager) getSystemService(TELEPHONY_SERVICE)).getDeviceId();
            	NIOsendOut("INSTANCE;INSTANCEACK;start;"+Imei+";");
            	//send a broadcast to MainActivity

        //		foucedown = false;
            	}
            	}        	
        	}
        	break;
        	case 4:
        	{
        		Intent mIntent1 = new Intent("AUTOMESSAGE");
    	        mIntent1.putExtra("result", "PINGR;"+msg.obj.toString());    
    	        sendBroadcast(mIntent1);	
        	}
        		break;
        	
        	case 10:
        		//1 step one,check the socket connectio   
        		
        	///	theapp = (RawData)getApplication();
       ////     Log.e("check","RawData.rebootServiceForServerDown : "+String.valueOf(rebootServiceForServerDown)+
        ////    				"  reconnectServer: "+String.valueOf(reconnectServer)
         //   				);
        		if(isAlive >= 60)
        		{
        			ToolSrvImpl.STrace("KA","clinet is alive "+String.valueOf(foucedown));
        			isAlive = 0;
        		}
        		synchronized(SocketKAlock)
        		{
        		if(SocketServiceKA >=60)
        		{
        		    String DisabelAutoTest = settings.getString("DisableAutoTest", "");
        		    if(!DisabelAutoTest.equals("Yes"))
        		    {
        			ToolSrvImpl.STrace("KA","SocketService miss KA in 60 sec, reboot SocketService");
        			Intent mIntent = new Intent("SocketQeustReStart");
        	        mIntent.putExtra("data","restart");    
        	        sendBroadcast(mIntent);	
        	  //      synchronized(SocketKAlock)
        	 //       {
        	        SocketServiceKA = 0;
        	  //      }
        		    }
        		}
  //      		synchronized(SocketKAlock)
 //       		{
 //       		ToolSrvImpl.STrace("KA","SocketService: "+SocketServiceKA);
        		SocketServiceKA++;
        		}
        		isAlive++;
        		if(isTaskDF)
        		{
        		DFstopTimer--;
        		if(DFstopTimer <=0 )
        		{
        			ToolSrvImpl.STrace("process","time out,stop DF DL/UL task");
        			if(clientUp !=null)
        			{
					  try {
						clientUp.abortCurrentDataTransfer(true);
						clientUp.disconnect(true);
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
        			if(clientdown !=null)
        			{
					  try {
						  clientdown.abortCurrentDataTransfer(true);
						  clientdown.disconnect(true);
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
        		}
        		
        		if((RawData.socketChannel == null)&&((mSocket== null)||!mSocket.isConnected()||mSocket.isInputShutdown()||mSocket.isOutputShutdown()||mSocket.isClosed()))
        		{
        //			Log.i("reconnect1","reconnet1");
        			//re-connect?
        	//		do{
        			
        			if(!stoprenconnect)
        			{
        	//		ToolSrvImpl.STrace("reconnect","reconnet");
        			if(!rebootServiceForTaskDF)
        			{
        			TaskIsWorking = false;
        			timeup = true;
        			}
        			}
        			//	Log.i("reconnect","reconnet");
        	//		reconnectServer = true;
        		//	connectServer();
        		//	}while((mSocket== null)||!mSocket.isConnected()||mSocket.isInputShutdown()||(socketStat!=0)||mSocket.isClosed());
        		}
        /*       if((socketChannel != null)&&(reconnectServer))
               {
            	   if(!stoprenconnect)
            	   {
           			ToolSrvImpl.STrace("WARNING","server socket is down,reconnect");
           			stopInstance();
       			    TaskIsWorking = false;
       				timeup = true;
       				
       				try {
						socketChannel.close();
						socketChannel = null;
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
            	   }
            	   
               }      */		
        		        		
      /*  		if(RawData.socketChannel != null)
        		{
        			if(reconnectNIOServer)
        			{
            			if(!stoprenconnect)
            			{
            			ToolSrvImpl.STrace("WARNING","server socket is down,reconnect");
            			if(!rebootServiceForTaskDF)
            			{
            			stopInstance();
        			    TaskIsWorking = false;
        				timeup = true;
            			}
            			
         		      synchronized(socketLock)
						{
					//	SocketService.disconnectServer();
         		    	 DisconnnetServer();
						}  	
         		     reconnectServer = true;
            			}
        			}
        			
        		}*/
        		
        		long currentTime = System.currentTimeMillis()+systemTimeOffset;
        	//	Log.i("time",String.valueOf(currentTime));
        	//	listAll();
        		if(TaskLength == 0)
        		{
        			NIOsendOut("INSTANCE;INSTANCEACK;end;"+IMEI+";");
        			TaskLength = -1;
        			if(rebootServiceForTaskDF)
        			{
        				synchronized(RawData.Tlock)
        				{
        					rebootServiceForTaskDF = false;
        				}
        			Intent mIntent1 = new Intent("AUTOMESSAGE");
        	        mIntent1.putExtra("show", "DF restart");    
        	        sendBroadcast(mIntent1);
        	        
        //        	String cmd  = "mv "+Environment.getExternalStorageDirectory().toString()+File.separator + "crash"
        //        			+File.separator+"* "+Environment.getExternalStorageDirectory().toString()+File.separator+"crash2/";
         //           try {
		//				Process p = Runtime.getRuntime().exec(cmd);
		//			} catch (IOException e) {
						// TODO Auto-generated catch block
		//				e.printStackTrace();
		//			}
        		//	unbindService(theapp.getServiceConnection());
                   //should reinit all var here;
                    
            //       upDataTrcaeFileToServer(0);
          //          upDataTrcaeFileToServer(1);  
        			}
        			 upDataTrcaeFileToServer(0);
        		}
        //		Log.i("time Next",String.valueOf(NextStartTime));
        		if(currentTime >= NextStartTime)
        		{
        //			Log.i("timeup","timeup");
        			timeup = true;
        		}
        		String tmp = "empty;empty";
        		String tminfo = "empty";
        		synchronized(lockliq)
        		{
        			tmp = liq.poll();
        		}
        		if(tmp == null)
        		{
        			// to be continue
        			tmp = "empty;empty";
        		}
        		if((isAlive % 15) == 0)
        		{
        			tminfo = getPartStatus(tm);
        		//	tminfo = tminfo+" "+SignalStrengths;
       // 			Log.e("tminfo",tminfo);
        		}
        		if(tminfo == null)
        		{
        			// to be continue
        			tminfo = "empty";
        		}

        		if(report2file!=null)
        		{
        	//		sendOut("SYSTEM;KA;"+IMEI+";");
        			if(intervalKA)
        			{
        				
        			}
        			else
        			{
        			if(speedType == 1)
        			{
        			NIOsendOut("SYSTEM;KA;"+IMEI+";"+tmp+";"+tminfo+";"+"in;");
        			}
        			else if(speedType == 2)
        			{
        			NIOsendOut("SYSTEM;KA;"+IMEI+";"+tmp+";"+tminfo+";"+"out;");	
        			}
        			else
        			{
        			NIOsendOut("SYSTEM;KA;"+IMEI+";"+tmp+";"+tminfo+";");
        			}
        			}
        			report2file.delete(0, report2file.length());
        		}
        		else
        		{
        	//		sendOut("SYSTEM;KA;"+IMEI+";");
        	//		if(speedType == 1)
        	//		{
        	//		NIOsendOut("SYSTEM;KA;"+IMEI+";"+tmp+";"+"in;");
        	//		}
        	//		else if(speedType == 2)
        	//		{
        	//		NIOsendOut("SYSTEM;KA;"+IMEI+";"+tmp+";"+"out;");	
        	////		}else
        	///		{
        	//		NIOsendOut("SYSTEM;KA;"+IMEI+";"+tmp+";");
        	//		}
        			
        		}
        		if((foucedown)||(cleanFtp))
        		{
        		//	ToolSrvImpl.STrace("WARNING","foucedown is true;");
        			if(clientdown!=null)
        			{
							try {
								clientdown.abortCurrentDataTransfer(true);
					//			clientdown.abortCurrentDataTransfer(false);
								clientdown.disconnect(true);
						//		clientdown = null;   //race condition
								} catch (IOException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									} catch (FTPIllegalReplyException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									} catch (IllegalStateException e) {
										// TODO Auto-generated catch block
								//		clientdown = null;
										e.printStackTrace();
									} catch (FTPException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
        			}
        			if(clientUp !=null)
        			{
					try {
						clientUp.abortCurrentDataTransfer(true);
						clientUp.disconnect(true);
					 //   clientUp = null;
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (FTPIllegalReplyException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalStateException e) {
						// TODO Auto-generated catch block
				//		clientUp = null;
						e.printStackTrace();
					} catch (FTPException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
;
        		    }
        			
        			for(int i=0;i<3;i++)
        			{
        				if(clientdownThread[i] != null)
        				{
        					
        		//			ToolSrvImpl.STrace("clientdownThread","clientdownThread["+i+"] is true;");
        					try {
								clientdownThread[i].abortCurrentDataTransfer(true);
								clientdownThread[i].disconnect(true);
								
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (FTPIllegalReplyException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
				//			clientdown.abortCurrentDataTransfer(false);
                              catch (IllegalStateException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (FTPException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
        					clientdownThread[i] = null;
        					
        				}
        				if(clientupThread[i] != null)
        				{	
//        					Log.e("clientupThread","clientupThread["+i+"] is true;");					
        					try {
								clientupThread[i].abortCurrentDataTransfer(true);
								clientupThread[i].disconnect(true);
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
        					clientupThread[i] = null;
        					
        					
        				}
        			}
        		   Urlidle = true;//in case URL hung in URL process 
        		   
       		       cleanFtp = false;
       		
       		//	foucedown = false;
        		}
        		if(reconnectServer)
        		{
             	   if(!stoprenconnect)
             	   {
            //			ToolSrvImpl.STrace("WARNING","start reconnect");
            //			if(!rebootServiceForTaskDF)
            //			{
            //			stopInstance();
        	//		    TaskIsWorking = false;
        	//			timeup = true;
            //			}
        			//	synchronized(socketLock)
					//	{
   			//	selector.close();
   			//	socketChannel.socket().shutdownInput();
						//	SocketService.disconnectServer();
        					DisconnnetServer();
					//	}
             	   }          	  
             		if((rebootServiceForServerDown)&&(!rebootServiceForTaskDF))
             		{
             			synchronized(RawData.Tlock)
             			{
             				rebootServiceForServerDown = false;
             			}
             			//trigger a core dump
                    	String cmd  = "rm"+Environment.getExternalStorageDirectory().toString()+File.separator + "crash/* "+Environment.getExternalStorageDirectory().toString()+File.separator+"/crash2/";
                        try {
            				Runtime.getRuntime().exec(cmd);
            			} catch (IOException e) {
            				// TODO Auto-generated catch block
            				e.printStackTrace();
            			}
             		//	unbindService(theapp.getServiceConnection());
                        
                        //should reboot SocketService
                     //   ConnnetServer();
             		}
           	//	do
        	//	{
             	//   if(!SocketService.isSocketWokring())
             	//   {
           		//	ToolSrvImpl.STrace("0","0");
           			ConnnetServer();
           		///	SocketService.NIOconnectServer();
             	//   }
        	//	}while((socketChannel == null)|| !socketChannel.isConnected());
        		}
        		break;
        		
        	case 21:
				f = new File("/data/data/com.MY.pingtest/files/net-tool.zip");
                try {
					fw = new FileWriter(f);
					fw.write(" ");
					fw.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}		
                break;
                
        	case 22:
        	  {
        		NIOsendOut("TASK;FTPDLACK;"+"123456789"+";Failed;"+IMEI+";");
    			Intent mIntent1 = new Intent("AUTOMESSAGE");
    	        mIntent1.putExtra("show", "FTPDL abort/Failed");    
    	        sendBroadcast(mIntent1);
        	   }	
        	break;
        	
        	case 23:
        		NIOsendOut("TASK;FTPDLACK;"+"123456789"+";start;"+IMEI+";");
        		break;
        	case 24:
        		NIOsendOut("TASK;FTPDLACK;"+"123456789"+";end;"+IMEI+";");
        		break;
        		
        	case 32:
        	{
        		NIOsendOut("TASK;FTPULACK;"+"123456789"+";Failed;"+IMEI+";");
    			Intent mIntent1 = new Intent("AUTOMESSAGE");
    	        mIntent1.putExtra("show", "FTPUL Failed");  
    	        sendBroadcast(mIntent1);
        	}
        	break;
        	
        	case 33:
        		NIOsendOut("TASK;FTPULACK;"+"123456789"+";start;"+IMEI+";");
        		break;
        	case 34:
        		NIOsendOut("TASK;FTPULACK;"+"123456789"+";end;"+IMEI+";");
        		break;
        	case 40:
        		String tminfo1 = getPartStatus(tm);
        		String tmp1="empty;empty";
        		synchronized(lockliq)
        		{
        			tmp1 = liq.poll();
        		}
        		if(tmp1 == null)
        		{
        			// to be continue
        			tmp1 = "empty;empty";
        		}
        		if(tminfo1 == null)
        		{
        			// to be continue
        			tminfo1 = "empty";
        		}
        		if(report2file!=null)
        		{
        	//		sendOut("SYSTEM;KA;"+IMEI+";");
        			if(speedType == 1)
        			{
        			NIOsendOut("SYSTEM;KA;"+IMEI+";"+tmp1+";"+tminfo1+";"+"in;");
        			}
        			else if(speedType == 2)
        			{
        			NIOsendOut("SYSTEM;KA;"+IMEI+";"+tmp1+";"+tminfo1+";"+"out;");	
        			}
        			else
        			{
        			NIOsendOut("SYSTEM;KA;"+IMEI+";"+tmp1+";"+tminfo1+";");
        			}
        			}
        			report2file.delete(0, report2file.length());
        		break;
        	default:
        		break;
        	}
        }

		private void postINTRACommand(String command) {
			// TODO Auto-generated method stub
			if(command.contains("TASKDF+ServerDownT"))
			{
			 	reconnectServer = true;
			 	rebootServiceForServerDown = true;
			}
			if(command.contains("TASKDF+ServerDownF"))
			{
			 	reconnectServer = false;
			 	rebootServiceForServerDown = false;
			}
		}  
    };  
	
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	public void postToSreen(String string) {
		// TODO Auto-generated method stub
		SharedPreferences settings = this.getSharedPreferences("myConfig",MODE_PRIVATE);
		String DisableUEScreenTrace = settings.getString("DisableUEScreenTrace", "");
		if(DisableUEScreenTrace.equals("No"))
		{
		Intent mIntent = new Intent("AUTOMESSAGE");
        mIntent.putExtra("show", string);    
        sendBroadcast(mIntent);
		}
	}
	public void upDataTrcaeFileToServer(final int j) {
		// TODO Auto-generated method stub
		new Thread(new Runnable()
		{

			@Override
			public void run() {
				// TODO Auto-generated method stub
				String mFilename = "";
				switch(j)
				{
				case 0:
					mFilename = fileNameFTP;
					break;
				case 1:
					mFilename = fileNameIP;
					break;
				default:
					break;
				}			
		//		NIOsendOut("SYSTEM;STOPALL_ACK;"+"success"+";"+IMEI+";"+"uploadTrace start");  
			//	String filename = Environment.getExternalStorageDirectory()+"/NetAnts/TraceLog"+IMEI;
		//    	String cmd  = "/system/bin/cp "+ Environment.getExternalStorageDirectory()+"/NetAnts/TraceLog"+IMEI +" " + Environment.getExternalStorageDirectory()+"/NetAnts/traceLog"+IMEI;
	//	 //   	Log.e("resetTraceBack",cmd);
		//			try {
		//				Process p = Runtime.getRuntime().exec(cmd);
			//		} catch (IOException e1) {
						// TODO Auto-generated catch block
		//				e1.printStackTrace();
			//		}
	//			Log.e("cpFile","cpFile");
	//			ToolSrvImpl.copyFile(Environment.getExternalStorageDirectory()+"/NetAnts/TraceLog"+IMEI,Environment.getExternalStorageDirectory()+"/NetAnts/traceLog"+IMEI,false);
		     
				TraceUp = new FTPClient();
				SharedPreferences settings = clientService.this.getSharedPreferences("myConfig", 0);
		//		String ServierAddress = settings.getString("ServierAddress", "");
				try {
			//		File localfile = new File(Environment.getExternalStorageDirectory()+"/NetAnts/"+mFilename);
					TraceUp.connect(LogServerAddress);
					TraceUp.login(LogServerUsr, LogServerPass);
			//		TraceUp.login("test", "test");
			//		TraceUp.upload(localfile);
					File localfile1 = new File(Environment.getExternalStorageDirectory()+"/NetAnts/TraceLog"+IMEI);
					TraceUp.upload(localfile1);
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
				}
			//	TraceUp.login(t.Usr,t.Password);
                catch (FTPDataTransferException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (FTPAbortedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		//		NIOsendOut("SYSTEM;STOPALL_ACK;"+"success"+";"+IMEI+";"+"uploadTrace end"); 
			}
	//		NIOsendOut("SYSTEM;STOPALL_ACK;"+rtn+";"+IMEI+";");  
			
		}).start();
		
		
	}

	protected void postSystemCommand(String command) {
		// TODO Auto-generated method stub
		
//		Log.i("Socket","post SYSTEM"+command);
		String tag = null;
	    if(command.contains("ATTACH_ACK"))
	    {
	    	String[] rt = command.split(";");
			if((rt[3]!=null)&&(rt[3].length()!=0))
			{
				//Timeioff+本机时间=服务器时间
			systemTimeOffset = Long.parseLong(rt[3])-System.currentTimeMillis();
            socketStat = 10;
            clientStat = 0;
            ToolSrvImpl.STrace("TimeOffset", "time offset "+ String.valueOf(systemTimeOffset));
		//	Log.i("time offset",String.valueOf(systemTimeOffset));
			}
			if((rt[4]!=null)&&(rt[4].length()!=0))
			{
				LogServerAddress = rt[4];
			}
			if((rt[5]!=null)&&(rt[5].length()!=0))
			{
				LogServerUsr = rt[5];
			}
			if((rt[6]!=null)&&(rt[6].length()!=0))
			{
				LogServerPass = rt[6];
			}
			//start auto upgrated function 
			if((rt[7]!=null)&&(rt[7].length()!=0))
			{
				lastVersion = Double.parseDouble(rt[7]);
				checkAndupGrade();
			}
			//end AU
	    }
	    else if(command.contains("STOPALL"))
	    {
	//    	Log.i("STOPALL","STOPALL");
	    	String rtn = "failed";
	    	foucedown = true;
	    	if(stopInstance())
	    	{
	    		rtn = "success";
	    	}
	    	NIOsendOut("SYSTEM;STOPALL_ACK;"+rtn+";"+IMEI+";");  
			Intent mIntent1 = new Intent("AUTOMESSAGE");
	        mIntent1.putExtra("show", "STOPALL");    
	        sendBroadcast(mIntent1);	
	  //  	listAll();
	    }
	    else if(command.contains("PULLTRACE"))
	    {
	    	upDataTrcaeFileToServer(0);	    	
	    }
	    else if(command.contains("CLEANTRACE"))
	    {  
	    	ToolSrvImpl.cleanTrace();
	    }

	}


//begin AU function
	private void checkAndupGrade() {
		// TODO Auto-generated method stub
		if(lastVersion > currentVersion)
		{
		//	Log.e("checkAndupGrade ","checkAndupGrade ");
			// step 1 download package from server,no need Thread.
			//beacause no root UE need manual opreation to click button,so need to send a notice to server when the download is done.
			
		//	NIOsendOut("INSTANCE;AU;"+"start;"+IMEI+";");
			AUDownload = false;
			new Thread(new Runnable()
			{

				@Override
				public void run() {
					// TODO Auto-generated method stub
			//		Log.e("checkAndupGrade11 ","checkAndupGrade ");
					AU = new FTPClient();
					SharedPreferences settings = clientService.this.getSharedPreferences("myConfig", 0);
					try {					
						AU.connect(LogServerAddress);
						AU.login(LogServerUsr, LogServerPass);
						AUTransferListener one = new AUTransferListener();
						File localfile = new File(Environment.getExternalStorageDirectory()+"/NetAnts/NetAnts_temp.apk");			
						AU.download("net_tool_v"+lastVersion+".apk",localfile,one);
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
					}
	                catch (FTPDataTransferException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (FTPAbortedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
			}).start();
		
        	synchronized(AUlock)
        	{
       // 	tasklock.wait(5000);
        		try {
        			AUlock.wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        	}
		    
			//step 2, install package
        	if(AUDownload)
        	{
    //    		NIOsendOut("INSTANCE;INSTANCEACK;start;"+Imei+";");
        	 String fileName = Environment.getExternalStorageDirectory() + "/NetAnts/NetAnts_temp.apk";
        	 Log.e("fileName ",fileName);
        	 Uri uri = Uri.fromFile(new File(fileName));
        	 Intent intent = new Intent(Intent.ACTION_VIEW);
        	 intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        	 intent.setDataAndType(uri, "application/vnd.android.package-archive");
        	 startActivity(intent);
        	}
        	else
        	{
    //    		NIOsendOut("INSTANCE;INSTANCEACK;start;"+Imei+";");
        	}
		}
	}
//end AU funciton

	private boolean stopInstance() {
		// TODO Auto-generated method stub		
//		foucedown = true;
		synchronized(lock)
		{
			TaskIsWorking = false;
			
			
			timeup = true;
//			foucedown = true;
			NextStartTime = 0;
			CommandList.clear();
		}
		
    	synchronized(tasklock)
    	{
   // 	tasklock.wait(5000);
    		tasklock.notify();
    	}
		for(int i=0;i<haveMultiPING;i++)
		{
		 if(p[i] != null)
		 p[i].destroy();
		 
	//	 ToolSrvImpl.STrace("ping", "ping end "+ lockqueue.size());
	//	 synchronized(lockqueue)
	//	 {
	//		lockqueue.poll();
	//		if(lockqueue.size() == 0)
	//		{
	//			synchronized(fulltasklock)
	//			{
	//			fulltasklock.notify();
	//			}
	//		}
	//	 }
		}
        synchronized(lockwechat)
        {
				lockwechat.notify();       	
        }
		return true;
	}

	@TargetApi(Build.VERSION_CODES.GINGERBREAD) @Override
	public void onCreate() {
		// TODO Auto-generated method stub
		theapp = (RawData)getApplication();
		theapp.exitAppUtils.addService(clientService.this);
		
		for(int i=0;i<3;i++)
		{
	//		MultiTaskDLlock[i] = new Object();
	//		MultiTaskULlock[i] = new Object();
	//		MultiTaskPINGlock[i] = new Object();
		}
	//	addActivity(clientService.this);
        StrictMode.ThreadPolicy policy=new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
     //   stopSelf();  //结束自己
        tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        settings = clientService.this.getSharedPreferences("myConfig", 0);
        cleanFtp = false;
        if (wakeLock == null) {
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, this.getClass().getCanonicalName());
            wakeLock.acquire();
        }
        
        IntentFilter mFilter1 = new IntentFilter();
        mFilter1.addAction("URLACK");
        registerReceiver(mReceiver, mFilter1);
        
        IntentFilter mFilter3 = new IntentFilter();
        mFilter3.addAction("SignalStrengthsChanged");
        registerReceiver(mReceiver1, mFilter3);
        
        IntentFilter mFilter2 = new IntentFilter();
        mFilter2.addAction("INCOMINGDATA");
        registerReceiver(mReceiver2, mFilter2);
        
        IntentFilter mFilter4 = new IntentFilter();
        mFilter4.addAction("SocketKA");
        registerReceiver(mReceiver3, mFilter4);
        
        IntentFilter mFilter5 = new IntentFilter();
        mFilter5.addAction("URLACKDATA");
        registerReceiver(mReceiver4, mFilter5);
        
        
        IntentFilter mFilter6 = new IntentFilter();
        mFilter6.addAction("URLACKDATA1");
        registerReceiver(mReceiver5, mFilter6);
        
  	   app = (RawData)getApplication();
  		IMEI = tm.getDeviceId();
  		// 3 get IP
  	//    String IP = MainActivity.getPsdnIp();
  	  // app.mobNetInfo
  	    
  	    fileNameFTP =  fileNameFTP+IMEI;
  	    fileNameIP = fileNameIP+IMEI;
        fileIP = new File(Environment.getExternalStorageDirectory()+"/NetAnts", fileNameIP);
        
        fileFTP = new File(Environment.getExternalStorageDirectory()+"/NetAnts", fileNameFTP);
        
		try {
			foutFTP= new FileOutputStream(fileFTP,false);
			SimpleDateFormat  formatter =   new SimpleDateFormat("yyyy.MM.dd    HH:mm:ss     ");       
			Date  curDate = new Date(System.currentTimeMillis());//获取当前时间       
			String str1 = "new FTP report start time : "+formatter.format(curDate) + "\r\n";   	
			try {
			if(foutFTP != null)
				foutFTP.write(str1.getBytes());
			} catch (IOException e) {
				// TODO Auto-generated catch block
			e.printStackTrace();
			}
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} 
		
   //     IntentFilter mFilter = new IntentFilter();
   //     mFilter.addAction("com.MY.pingtest.AutoTestspeed");
   //     registerReceiver(mBroadcastReceiver1, mFilter);
        	    
 	   try {
		fout= new FileOutputStream(file,true);
		foutPing = new FileOutputStream(fileIP,true);
	} catch (FileNotFoundException e2) {
		// TODO Auto-generated catch block
		e2.printStackTrace();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} 
		Intent mIntent2 = new Intent("AUTOTEST");
	    mIntent2.putExtra("result", "START");    
	    sendBroadcast(mIntent2);	
	    ToolSrvImpl.STrace("service start","service start");
	    
	 //   resetTraceBack();
	    //force SocketService reboot
		 Intent mIntent = new Intent("SocketQeustReStart");
         mIntent.putExtra("data","restart");    
         sendBroadcast(mIntent);	
		super.onCreate();
	//	do{
	//		connectServer();
     	//}while((mSocket==null)||(!mSocket.isConnected()||mSocket.isInputShutdown()||(socketStat!=0)));
		
	//	do
	//	{
	//	if((socketChannel == null)|| !socketChannel.isConnected())
	//	{
	//		NIOconnectServer();
	//	}
	//	}while((socketChannel == null)|| !socketChannel.isConnected());
		
	//	Log.e("start","start");
// get IMEI IP mobileNet
		
 //      Thread a = new Thread(new mySocket());
 //      a.start();
//       try {
//		a.join();
//	    } catch (InterruptedException e) {
//		// TODO Auto-generated catch block
//     	 e.printStackTrace();
//	    }
       Thread b = new Thread(new MessageHandler());
      b.start();
       
       Thread c = new Thread(new TimeThread());
       c.start();
        
	}
	private void resetTraceBack() {
		// TODO Auto-generated method stub

    	String cmd  = "/system/bin/rm -r -f /storage/emulated/0/crash/";
    	Log.e("resetTraceBack",cmd);
        try {
			Process p = Runtime.getRuntime().exec(cmd);
			String str;
            BufferedReader bufferReader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            while ((str = bufferReader.readLine()) != null) {
            	Log.e("resetTraceBack",str);
            }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
    	String cmd2  = "/system/bin/mv "+Environment.getExternalStorageDirectory().toString()+File.separator + "crash2"
    			+File.separator+" "+Environment.getExternalStorageDirectory().toString()+File.separator+"crash/";
    	Log.e("resetTraceBack2",cmd2);
        try {
			Process p = Runtime.getRuntime().exec(cmd2);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@SuppressWarnings("unused")
	private void sendOut(String string) {
		// TODO Auto-generated method stub
      if(mSocket ==null || mSocket.isClosed())
			return;
		if(out!=null)
		{
//	    Log.e("sendOut",string);
             out.println(string);
             out.flush();
 			if(out.checkError())
 			{
 				ToolSrvImpl.STrace("SENDOUT","IOException has been thrown previously"+"  "+string);
 			}
 			else
 			{
 				if(!string.contains("KA"))//617
 				ToolSrvImpl.STrace("SENDOUT",string);
 			}
		}
		else
		{
			ToolSrvImpl.STrace("SENDOUT","out==null");
		}
			
	}
	private void NIOsendOut(String string)
	{
		
		 Intent mIntent = new Intent("SocketQeust");
         mIntent.putExtra("data",string);    
         sendBroadcast(mIntent);	
		
	//	SocketService.NIOsendOut(string);
	/*	  if((socketChannel ==null) || (!socketChannel.isConnected()))
				return;
		  byte[] bytes = string.getBytes();
		  ByteBuffer buffer = ByteBuffer.wrap(bytes);
		  try {
			socketChannel.write(buffer);
			if(!string.contains("KA"))//617
			ToolSrvImpl.STrace("SENDOUT",string);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			ToolSrvImpl.STrace("SENDOUT","IOException has been thrown previously"+"  "+string);
			rebootServiceForServerDown = true;
			reconnectServer = true;
			e.printStackTrace();
		}*/
	}
	private BroadcastReceiver mReceiver = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			if(action.equals("URLACK"))
			{
            	String result = intent.getStringExtra("result"); 
            	if(result.contains("Speed"))
            	{
            		String s = result.replace("Speed", "");
            		processFtpSpeed(s);
            	}
            	else
            	{
				Urlidle = true;
            	}         	
			}
		}
		
	};
	public BroadcastReceiver mReceiver1 = new BroadcastReceiver()
	{

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			if(action.equals("SignalStrengthsChanged"))
			{
				
            	String result = intent.getStringExtra("data"); 
       //      	Log.e("SignalStrengthsChanged",result);
            	setStatusChange(result);
       //     	Log.e("SignalStrengthsChanged1",getStatusChange());
     //       	Log.e("SignalStrengthsChanged1",getStatusChange());	
     //       	show.setText(getStatus(tm));
			}
		
		}
		
	};
	
	private BroadcastReceiver mReceiver2 = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			if(action.equals("INCOMINGDATA"))
			{
            	String result = intent.getStringExtra("data"); 
	            Message msg;
	            msg = Message.obtain();
	            msg.what = 0;
	            msg.obj = result;
	            mHandler.sendMessage(msg);           	
			}
		}
		
	};
	
	private BroadcastReceiver mReceiver3 = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			if(action.equals("SocketKA"))
			{
		//		ToolSrvImpl.STrace("SocketKA","SocketKA");
		//		Log.e("SocketQeustReStart","SocketQeustReStart");
				synchronized(SocketKAlock)
				{
				SocketServiceKA = 0;
				}
			}
		}	
	};
	
	private BroadcastReceiver mReceiver4 = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			String data = intent.getStringExtra("data"); 
			if(action.equals("URLACKDATA"))
			{
             HttpSpeed = false;
             String data0[] = data.split(";");
             kpi.timeD.add(data0[0]);
             kpi.avgspeed1 = String.format("%2d", Long.parseLong(data0[1])/Long.parseLong(data0[0]));
             synchronized(lockNotifyNextHttp)
             {
             lockNotifyNextHttp.notify();
             }
			}
		}	
	};
	private BroadcastReceiver mReceiver5 = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			String data = intent.getStringExtra("data"); 
			if(action.equals("URLACKDATA1"))
			{
            kpi.totalHttpDlOK--;
			}
		}	
	};
	private void processFtpSpeed(String data)
	{
        String str = runFtp.normalizationgSriptsInput(data);
   //     str="rmnet0:;52575007;90059;0;0;0;0;0;0;12907064;111737;0;0;0;0;0;0;rmnet0:;52579369;90070;0;0;0;0;0;0;12908792;111751;0;0;0;0;0;0";//for sony with out grep
        String[] resultarray=str.split(";");
//rmnet0:;52575007;90059;0;0;0;0;0;0;12907064;111737;0;0;0;0;0;0;rmnet0:;52579369;90070;0;0;0;0;0;0;12908792;111751;0;0;0;0;0;0  
//        52649816 12962812 52651163 12965072
        float in = 0;
        float out = 0;
        String sin = resultarray[4];
        String sout = resultarray[5];
        in = Float.parseFloat(sin);
        out = Float.parseFloat(sout);		
        synchronized(lockliq)
        {
        liq.offer(sin+";"+sout);
        }
        
        if(HttpSpeed)
        {
        	synchronized(lockHttpSpeed)
        	{
        		kpi.Speedcalc.add(sin);
        	}
        }
        
        String averagei=null;
        String averageo=null;
        sec=(System.currentTimeMillis() - startTime)/1000;
        intotal = intotal +  in*1024;
        outtotal = outtotal + out*1024;
        if(i==1){
        	Inmax=in;
        	Inmin=in;
        	outmax=out;
        	outmin=out;
        	i=0;
        }
        if(i!=1){
              if(in>Inmax){
            	Inmax=in;
            }else if(in<Inmin){
            	Inmin=in;
            }
            
            if(out>outmax){
            	outmax=out;
            }else if(out<outmin){
            	outmin=out;
            }
           }
        
     //   if((intotal*8/sec)>1024){	
          //  float averagein=(float) (intotal*8/(1024*sec));
        //    if((!Float.isNaN(averagein))&&(!Float.isInfinite(averagein)))
         //   		{
         //   BigDecimal c = new BigDecimal(averagein);               
        //    float K1 = c.setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();
        ///   		}
        	//}else{
        		float averagein=(float) (intotal*8/sec);
        		averagei="AverageIN="+String.format("%.2f", averagein)+"Kb/s"+"\n";
       // 	}
        	
      //      if((outtotal*8/sec)>1024){
          //  float averageout=(float) (outtotal*8/(1024*sec));                
         //   if((!Float.isNaN(averageout))&&(!Float.isInfinite(averageout)))
         //   {
         //   BigDecimal d = new BigDecimal(averageout);
         //   float K2 = d.setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();
        //	averageo="AverageOUT="+K2+"Mb/s"+"\n";
         //   }
         //   }else{
            	float averageout=(float) (outtotal*8/sec);
        		averageo="AverageOUT="+String.format("%.2f", averageout)+"Kb/s"+"\n";
        //    }
            
        if(Inmax*8>1024)
        {   float f = Inmax*8/1024;
            BigDecimal b = new BigDecimal(f);
            float f1 = b.setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();
        	pinjie[0]="INMAX="+f1+"Mb/s"+"\n";}
        else{
        pinjie[0]="INMAX="+Inmax*8+"Kb/s"+"\n";}
        
        if(Inmin*8>1024){
        	 float f = Inmin*8/1024;
             BigDecimal b = new BigDecimal(f);
             float f1 = b.setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();
             pinjie[1]="INMIN="+f1+"Mb/s"+"\n";
        }else{
        pinjie[1]="INMIN="+Inmin*8+"Kb/s"+"\n";}
        
        if(outmax*8>1024){
        	 float f = outmax*8/1024;
             BigDecimal b = new BigDecimal(f);
             float f1 = b.setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();
             pinjie[2]="OUTMAX="+f1+"Mb/s"+"\n";
        }else{
        pinjie[2]="OUTMAX="+outmax*8+"Kb/s"+"\n";
        }
        
        if(outmin*8>1024){
        	 float f = outmin*8/1024;
             BigDecimal b = new BigDecimal(f);
             float f1 = b.setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();
             pinjie[3]="OUTMIN="+f1+"Mb/s"+"\n";
        }
        else{
        pinjie[3]="OUTMIN="+outmin*8+"Kb/s"+"\n";}
        
        String str1 = null;
        if(in*8<1024&&out*8<1024){
        str1="IN :"+in*8+"Kb/s  OUT:"+out*8+"kb/s";
        }else if(in*8<1024&&out*8>=1024)
        {   float f = out*8/1024;
         BigDecimal b = new BigDecimal(f);
         float f1 = b.setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();
        	str1="IN:"+in*8+"Kb/s  OUT:"+f1+"Mb/s";}
        else if(in*8>=1024&&out*8<1024)
        {   float f = in*8/1024;
         BigDecimal b = new BigDecimal(f);
         float f1 = b.setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();
        	str1="IN :"+f1+"Mb/s  OUT:"+out*8+"Kb/s";}
        else if(in*8>=1024&&out*8>=1024)
        {   float f1 = in*8/1024;
            float f2 = out*8/1024;
         BigDecimal b1 = new BigDecimal(f1);
         BigDecimal b2 = new BigDecimal(f2);
         float F1 = b1.setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();
         float F2 = b2.setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();
        	str1="IN :"+F1+"Mb/s  OUT:"+F2+"Mb/s";}
        
       if(app.getisenablemomentaryrate()&&startstop)
       {
        if(((pinjie!=null)&&(pinjie.length!=0)))
        {
        String s = str1+pinjie[0]+pinjie[1]+averagei+pinjie[2]+pinjie[3]+averageo;
        report2file.append(str).append("\r\n").append(s).append("\r\n");    
	    }
  //      try {
  //      	if(foutFTP == null)
  //      	{
 //       		foutFTP= new FileOutputStream(file,true); 
 //           }
//        	foutFTP.write(report2file.toString().getBytes());
//		} catch (IOException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
       }
         
	};
	
	@SuppressWarnings("unused")
	private void calculateFtpEndSpeed()
	{
		if(startstop)
		{
	    startstop= false;
	    String s;
	    String cmd = "sh /data/data/com.MY.pingtest/netspeed rmnet0";
        Process p;
		try {
			p = Runtime.getRuntime().exec(cmd);
			endTime = System.currentTimeMillis();
            BufferedReader bufferReader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            while ((s = bufferReader.readLine()) != null) {
            	String sk = runFtp.normalizationNetSpeed(s);
            	String oldin = null;
            	String oldout = null;
	       	     //rmnet0:;52575007;90059;0;0;0;0;0;0;12907064;111737;0;0;0;0;0;0;rmnet0:;52579369;90070;0;0;0;0;0;0;12908792;111751;0;0;0;0;0;0  
//	                52649816 12962812 52651163 12965072
            	
	            	if((sk!=null)&&(sk.length()!=0))
	            	{
	            	String[] rt=sk.split(";");
	            	if((rt!=null)&&(rt.length!=0))
	            	{
	            	if((rt[1]!=null)&&(!rt[1].isEmpty()))
	            	{
	                oldin = rt[1].trim();
	            	}
	            	if((rt[9]!=null)&&(!rt[9].isEmpty()))
	            	{
	                oldout= rt[9].trim();
	            	}	
	            	}
	            	}
            
            float in = Long.parseLong(oldin);
            float out= Long.parseLong(oldout);
            
            long elaspetime = (endTime-startTime)/1000;
            
            elaspetInSize = in - inStart;
            elaspetOutSize = out - outStart;
            
            avgInSpeed = elaspetInSize / elaspetime/1024.0;
            avgOutSpeed = elaspetOutSize / elaspetime/1024.0;	            
            String sd = "\r\n"+"Completed"+" Elaspe time: "+ elaspetime + " Average In speed: "+String.format("%.2f", avgInSpeed)+"KB" + " Average out speed :"+String.format("%.2f", avgOutSpeed)+"k"+" ElaspetInSize "
            +elaspetInSize+" Byte "+" ElaspetOutSize "+elaspetOutSize+" Byte "+"\r\n"+"\r\n";
            if(!app.getisenablemomentaryrate())
            	sd=sd+pinjie[0]+pinjie[1]+pinjie[2]+pinjie[3]+"\r\n"+"\r\n";
            
            
            foutFTP.write(sd.getBytes());
             	   }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//calculate the avg speed
		}
	}
	
	@SuppressWarnings("unused")
	private void calculateFtpStartSpeed()
	{
	    String s;
	    String cmd = "sh /data/data/com.MY.pingtest/netspeed rmnet0 ";
        Process p;
		try {
			p = Runtime.getRuntime().exec(cmd);
			startTime = System.currentTimeMillis();
            BufferedReader bufferReader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            while ((s = bufferReader.readLine()) != null) {
            	String sk = runFtp.normalizationNetSpeed(s);
            	
	       	     //rmnet0:;52575007;90059;0;0;0;0;0;0;12907064;111737;0;0;0;0;0;0;rmnet0:;52579369;90070;0;0;0;0;0;0;12908792;111751;0;0;0;0;0;0  
//	                52649816 12962812 52651163 12965072
            	Log.e("calculateFtpStartSpeed",sk);
	            	if((sk!=null)&&(sk.length()!=0))
	            	{
	            	String[] rt=sk.split(";");
	            	String oldin = null;
	            	String oldout = null;
	            	if((rt!=null)&&(rt.length!=0))
	            	{
	            	if((rt[1]!=null)&&(!rt[1].isEmpty()))
	            	{
	                oldin = rt[1].trim();
	            	}
	            	if((rt[9]!=null)&&(!rt[9].isEmpty()))
	            	{
	                oldout= rt[9].trim();
	            	}			                
	                inStart = Long.parseLong(oldin);
	                outStart = Long.parseLong(oldout);			            	
	            	}
	            	}		            	
            }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void DisconnnetServer() {
		// TODO Auto-generated method stub
		 Intent mIntent = new Intent("serverCommand");
         mIntent.putExtra("data","DisconnnetServer");    
         sendBroadcast(mIntent);	
	}
	
	public void ConnnetServer() {
		// TODO Auto-generated method stub
		 Intent mIntent = new Intent("serverCommand");
         mIntent.putExtra("data","connnetServer");    
         sendBroadcast(mIntent);
	}
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		ToolSrvImpl.STrace("process","ClientService onDestroy");
		unregisterReceiver(mReceiver);
		unregisterReceiver(mReceiver1);
		unregisterReceiver(mReceiver2);
		unregisterReceiver(mReceiver3);
		theapp.exitAppUtils.delService(clientService.this);
	    if (wakeLock !=null&& wakeLock.isHeld()) {
	        wakeLock.release();
	        wakeLock =null;
	    }
		super.onDestroy();
	}
	
}
