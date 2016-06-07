package com.MY.netTools;

import it.sauronsoftware.ftp4j.FTPAbortedException;
import it.sauronsoftware.ftp4j.FTPClient;
import it.sauronsoftware.ftp4j.FTPDataTransferException;
import it.sauronsoftware.ftp4j.FTPDataTransferListener;
import it.sauronsoftware.ftp4j.FTPException;
import it.sauronsoftware.ftp4j.FTPIllegalReplyException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.MY.netTools.business.ExitAppUtils;
import com.MY.netTools.business.RawData;
import com.MY.pingtest.R;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class runFtp extends Activity {
    
	protected static FileOutputStream fout = null;
	protected FileOutputStream foutclean = null;
	private final StringBuffer report2file = new StringBuffer();
	private TextView start;
	private TextView stop;
	private TextView FTPdownload;
	private TextView FTPupload;
	private TextView resultText;
	private TextView stopupdown;
	private TextView stopup;
	private TextView FtpRuningThreadNum;
	private TextView FtpRuningThreadNumUp;
	private Button resetupdown;
//	private Button resetTrace;
	private TextView showFtpConfig;
	private Boolean k=true;
	private final String ACTION_NAME = "发送广播";
	private final String ACTION_NAME1 = "发送数据";
	private float Inmin=0;
	private float Inmax=0;
	private float outmin=0;
	private float outmax=0;
	private double intotal=0;
    private double outtotal=0;
	private int i=1;
	private long sec=0;
	private String rmnet=null;
	private Boolean networkstate=false;
	private boolean foucedown = false;
	private String address;
	private String user;
	private String password;
	private final StringBuffer result = new StringBuffer();
//	private int ThreadNum = -99;
//	private int killNum = -99;
	private String FtpFileName="1.txt";
	private int FtpLoopNum = 0;
	private int FtpLoopTime = 0;
	private boolean startstop= false;
    final ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
	
	private long startTime;
	private long endTime;
	private final StringBuffer tmp = new StringBuffer();
	float inStart;
	float outStart;
	float elaspetInSize;
	float elaspetOutSize;
	double avgInSpeed = 0.0F;
	double avgOutSpeed= 0.0F;
	public String[] pinjie=new String[4];
	public RawData app;
	public boolean isMinitorPid = false;
	public boolean isRuningPidTask = false;
	private FTPClient[] clientdown = new FTPClient[10];
    private FTPClient[] clientup = new FTPClient[10];
	
//	private FTPClient[] clientdown;
//	private FTPClient[] clientup;
	
	int workingDownload = 0;
	int workingUpload = 0;
	
	File[] f = new File[20];
	FileWriter fw;

	 String fileName = "FTPreport";
	 File file = new File(Environment.getExternalStorageDirectory()+"/NetAnts", fileName);
	
	
	MyDownTransferListener[] lisetner_down = new MyDownTransferListener[10];
	MyUpTransferListener[] lisetner_up = new MyUpTransferListener[10];
	
	public static String runOnly(String[] cmd,String workdirectory) throws IOException{ 
		try { 
		ProcessBuilder builder = new ProcessBuilder(cmd); 
		InputStream in = null; 
		if (workdirectory != null) { 
		builder.directory(new File(workdirectory)); 
		builder.redirectErrorStream(true); 
		Process process = builder.start(); 
		} 
		} catch (Exception ex) { 
		ex.printStackTrace(); 
		} 
		return null;
	}
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ftprun);
		
		 app = (RawData)getApplication();
	//	 app.exitAppUtils.addActivity(runFtp.this);
	       try {
	       //   	fout =openFileOutput(fileName , MODE_APPEND);//不覆盖
	    	   fout= new FileOutputStream(file,true); 
	        //   	fout =openFileOutput(fileName,MODE_PRIVATE);
	   		} catch (FileNotFoundException e) {
	   			// TODO Auto-generated catch block
	   			e.printStackTrace();
	   		}   
		//String[] sucmd={"su"};
		//String[] chcmdnet={"chmod","777","data/local/tmp/netspeed"};
		//String[] chcmdbbox={"chmod","777","data/local/tmp/busybox"};
	   //  String[] createupfile={"dd","if=/dev/zero","of=upfiledemo","bs=8192","count=100000"};
	       String Imei = ((TelephonyManager) getSystemService(TELEPHONY_SERVICE)).getDeviceId();
	//       String[] createupfile={"dd","if=/dev/zero","of=upfiledemo"+Imei,"bs=8192","count=1000"};
		//String[] chcmdupfiledemo={"chmod","777","/data/local/tmp/upfiledemo"};
		String path="/data/data/com.MY.pingtest/";
		Intent intent=getIntent();
		address=intent.getStringExtra("address");
		user=intent.getStringExtra("user");
		password=intent.getStringExtra("password");

		

		//chmod(sucmd);
		//chmod(chcmdupfiledemo);
		//chmod(chcmdnet);
		//chmod(chcmdbbox);
		networkstate=isOpenNetwork();
		if(!networkstate){
			Toast.makeText(runFtp.this, "Please ebable network access", Toast.LENGTH_LONG).show();
		}
		registerBoradcastReceiver();//注册广播接收
		findViews();//控件初始化
	//	intent1 = new Intent(runFtp.this, Fxservice.class);  //yiweng
        //启动FxService  
    //   bindService(intent1,conn,Context.BIND_AUTO_CREATE);//yiweng
	}
	
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK)
		{
			//need end all upload/down load?
			 try {
				 if(clientdown[0] !=null)
				clientdown[0].abortCurrentDataTransfer(true);
				 if(clientup[0] != null)
				clientup[0].abortCurrentDataTransfer(true);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (FTPIllegalReplyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			 
		}
		return super.onKeyDown(keyCode, event);
 
	}

	@Override
    protected void onDestroy() {
		unregisterReceiver(mBroadcastReceiver);
        super.onDestroy();
        try {
        	if(fw!=null)
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
   //     this.unbindService(conn);
  //      int nPid = android.os.Process.myPid();  
   //     android.os.Process.killProcess(nPid);
        // 或者下面这种方式
        // android.os.Process.killProcess(android.os.Process.myPid());
    }
	
/*	private ServiceConnection conn = new ServiceConnection() {
        // 获取服务对象时的操作 
        public void onServiceConnected(ComponentName name, IBinder service) {
            // TODO Auto-generated method stub
        }
        
        // 无法获取到服务对象时的操作 
        public void onServiceDisconnected(ComponentName name) {
            // TODO Auto-generated method stub
        }

    };*/
	
	 public synchronized String run(String[] cmd, String workdirectory) 
				throws IOException { 
				StringBuffer result = new StringBuffer(); 
				try { 
				ProcessBuilder builder = new ProcessBuilder(cmd); 
				InputStream in = null; 
				if (workdirectory != null) { 
				builder.directory(new File(workdirectory)); 
				builder.redirectErrorStream(true); 
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

	 private boolean isOpenNetwork() {  
	    ConnectivityManager connManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);  
	    if(connManager.getActiveNetworkInfo() != null) {  
	        return connManager.getActiveNetworkInfo().isAvailable();  
	    }  
	    return false;  
	}  

	private void registerBoradcastReceiver() {
		// TODO Auto-generated method stub
		IntentFilter myIntentFilter = new IntentFilter();  
        myIntentFilter.addAction(ACTION_NAME1);  
	//	myIntentFilter.addAction("发送数据");
        //注册广播        
        registerReceiver(mBroadcastReceiver, myIntentFilter);
        
	//	IntentFilter myIntentFilter1 = new IntentFilter();  
  //      myIntentFilter1.addAction("AUTOTESTFORFTP");  
	//	myIntentFilter.addAction("发送数据");
        //注册广播        
//        registerReceiver(mReceiver, myIntentFilter);
	}
	
	public static String normalizationNetSpeed(String aT) {
			
       int ssize = aT.length();
        boolean lastIsSpace = false;
        boolean haveFirstChartactor = false; //611 start
        ArrayList<String> k = new ArrayList<String>();
        for(int i=0;i<ssize;i++)
        {
        	if(aT.charAt(i)==' ')
        	{
        		if(lastIsSpace)
        		{
        			lastIsSpace = true;
        		}
        		else
        		{
        			if((i!=0)&&(haveFirstChartactor)) //611 start
        	//		if(i!=0)
        		    if((i!=0)&&(haveFirstChartactor)) //611 start
        			{
        			k.add(";");
        			lastIsSpace = true;
        			}
        		}
        	}
        	else
        	{
        		if(!haveFirstChartactor)     //611 start 
        		haveFirstChartactor = true;  //611 start
        		lastIsSpace = false;
        		k.add(String.valueOf(aT.charAt(i)));
        	}
        }
        String[] s = (String[])k.toArray(new String[0]);
        
        String str = "";
        for(String sk : s){
                str+=sk;
        }
		return str;
	}
	
	public static String normalizationgSriptsInput(String aT)
	{
		
		long oldIn = 0;
		long oldOut = 0;
		long newIn = 0;
		long newOut = 0;
		String str=" ";
		int sszie = aT.length();
//		Log.e("v1",aT+"\r\n");
		
		String[] bigTwo = aT.split(";");
		
		String[] oldOne = bigTwo[0].split("\r\n");
		String[] newOne = bigTwo[1].split("\r\n");
		
//		Log.e("v1",bigTwo[0]+"\r\n");
		
		int linesize = oldOne.length;
		int linesizeN = newOne.length;
		
		for(int i=0;i<linesize;i++)
		{
//Log.e("v",newOne[i]+"\r\n");		
			//ingore line 1,2 and lo interface
		 if(oldOne[i].contains("lo")||(i==0)||(i==1))
		 {
		 }
		 else
		 {
			String k = normalizationNetSpeed(oldOne[i]);	
	//		Log.e("check",k);
			String[] resultarray = k.split(";");
			oldIn = oldIn+Long.parseLong(resultarray[1].trim());
            oldOut= oldOut+Long.parseLong(resultarray[9].trim());
		 }
			
		}
		for(int i=0;i<linesizeN;i++)
		{
//Log.e("v",newOne[i]+"\r\n");		
			//ingore line 1,2 and lo interface
		 if(newOne[i].contains("lo")||(i==0)||(i==1))
		 {
		 }
		 else
		 {
			String k = normalizationNetSpeed(newOne[i]);	
	//		Log.e("check",k);
			String[] resultarray = k.split(";");
			newIn = newIn+Long.parseLong(resultarray[1].trim());
            newOut= newOut+Long.parseLong(resultarray[9].trim());
		 }
			
		}
        float in = (newIn - oldIn)*8/1024;
        float out = (newOut - oldOut)*8/1024;
		String inSpeed = String.format("%.2f", in)+"Kb/s";
		String outSpeed = String.format("%.2f", out)+"Kb/s";


	//	Log.e(inSpeed,outSpeed);
   //     Log.e("check","OldIn = "+String.valueOf(oldIn)+" OldOut = "+String.valueOf(oldOut)+" newIn = "+String.valueOf(newIn)+" newOut = "+String.valueOf(newOut));
		return newIn+";"+newOut+";"+inSpeed+";"+outSpeed+";"+String.valueOf(in)+";"+String.valueOf(out)+";"+oldIn+";"+oldOut+";";
	}
	
	public static String normalizationgSriptsInputForGetNetOutPut(String aT)
	{
		String[] one = aT.split("\r\n");
		long In = 0;
		long Out = 0;
		int linesize = one.length;
		
		for(int i=0;i<linesize;i++)
		{
//Log.e("v",newOne[i]+"\r\n");		
			//ingore line 1,2 and lo interface
		 if(one[i].contains("lo")||(i==0)||(i==1))
		 {
		 }
		 else
		 {
			String k = normalizationNetSpeed(one[i]);	
	//		Log.e("check",k);
			String[] resultarray = k.split(";");
			In = Long.parseLong(resultarray[1].trim());
            Out= Long.parseLong(resultarray[9].trim());
		 }		
		}		
		return String.valueOf(In)+";"+String.valueOf(Out);		
	}
	
	/*
	 * 接收广播，对数据进行处理
	 */
	 private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver(){  
	        @Override  
	        public void onReceive(Context context, Intent intent) {  
	            String action = intent.getAction(); 
	            if(action.equals(ACTION_NAME1)){
	            	String result = intent.getStringExtra("result"); 
            //        String str = normalizationNetSpeed(result);
                     String str = normalizationgSriptsInput(result);
               //      Log.e("speed",str);
	                String[] resultarray=str.split(";");
	                
	                float in = 0;
	                float out = 0;
	                String sin = resultarray[4];
	                String sout = resultarray[5];
	                in = Float.parseFloat(sin)/8;
	                out = Float.parseFloat(sout)/8;	
	                
	            //    Log.e("speed",str);
	     //rmnet0:;52575007;90059;0;0;0;0;0;0;12907064;111737;0;0;0;0;0;0;rmnet0:;52579369;90070;0;0;0;0;0;0;12908792;111751;0;0;0;0;0;0  
//	                52649816 12962812 52651163 12965072
	         //       String oldin = resultarray[1].trim();
	        //        String oldout=resultarray[9].trim();
	       //         String newin=resultarray[18].trim();
	        //        String newout=resultarray[26].trim();
	                
	  //              float oldin1 = Integer.parseInt(oldin);
	      //          float oldout1= Integer.parseInt(oldout);
	      //          float in = (Integer.parseInt(newin) - oldin1)*8/1024;
	     //           float out = (Integer.parseInt(newout) - oldout1)*8/1024;
	     //           String sin = String.format("%.2f", in)+"Kb/s";
	     //           String sout =String.format("%.2f", out)+"Kb/s";
	                                
	                String averagei=null;
	                String averageo=null;
	                rwl.writeLock().lock();
	                sec++;
	            //    sec=(System.currentTimeMillis() - startTime)/1000;
	           //     intotal = ((Float.parseFloat(resultarray[0])-Float.parseFloat(resultarray[6]))+intotal);
	         //       outtotal = ((Float.parseFloat(resultarray[1])-Float.parseFloat(resultarray[7]))+outtotal);
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
	                
	     //           if((intotal*8/sec)>1024){	
		                 float averagein=(float) (intotal*8/(1024*sec));
		/*                if((!Float.isNaN(averagein))&&(!Float.isInfinite(averagein)))
		                		{
		                BigDecimal c = new BigDecimal(averagein);               
	                    float K1 = c.setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();
	                	averagei="AverageIN="+K1+"Mb/s"+"\n";
		                		}
	               // 	}else{*/
	              //  		float averagein=(float) (intotal*8/sec);
	                		averagei="AverageIN="+String.format("%.2f", averagein)+"Kb/s"+"\n";
	      //          	}
	                	
		      //          if((outtotal*8/sec)>1024){
		      /*          float averageout=(float) (outtotal*8/(1024*sec));                
		                if((!Float.isNaN(averageout))&&(!Float.isInfinite(averageout)))
		                {
		                BigDecimal d = new BigDecimal(averageout);
	                    float K2 = d.setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();
	                	averageo="AverageOUT="+K2+"Mb/s"+"\n";
		                }
		                }else{*/
		                	float averageout=(float) (outtotal*8/(1024*sec));
	                		averageo="AverageOUT="+String.format("%.2f", averageout)+"Kb/s"+"\n";
		          //      }
		                
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
	                resultText.setText(str1);
	                resultText.append("\n");
	                if((pinjie[0]!=null)&&(pinjie[0].length()!=0))
	                {
	                resultText.append(pinjie[0]);
	                }
	                if((pinjie[1]!=null)&&(pinjie[1].length()!=0))
	                {
	                resultText.append(pinjie[1]);
	                }
	                if((averagei!=null)&&(averagei.length()!=0))
	                {
	                resultText.append(averagei);
	                }
	                if((pinjie[2]!=null)&&(pinjie[2].length()!=0))
	                {
	                resultText.append(pinjie[2]);
	                }
	                if((pinjie[3]!=null)&&(pinjie[3].length()!=0))
	                {
	                resultText.append(pinjie[3]);
	                }
	                if((averageo!=null)&&(averageo.length()!=0))
	                {
	                resultText.append(averageo);
	                }
	                String s = pinjie[0]+pinjie[1]+averagei+pinjie[2]+pinjie[3]+averageo;
	                report2file.append(str).append("\r\n").append(str1).append("\r\n").append(s).append("\r\n");    
	                }
	                try {
	                	if(fout == null)
	                	{
	                		fout= new FileOutputStream(file,true); 
	                    }
						fout.write(report2file.toString().getBytes());  
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	               }
	                  
	            } 
	            
	        }  
	          
	    };

	@SuppressLint("NewApi") private void findViews() {
		// TODO Auto-generated method stub
		start=(Button)findViewById(R.id.button_start);
		stop=(Button)findViewById(R.id.button_stop);
		FTPdownload=(Button)findViewById(R.id.button_download);
		FTPupload=(Button)findViewById(R.id.button_upload);
		resultText=(TextView)findViewById(R.id.textView_result);
		showFtpConfig = (TextView)findViewById(R.id.showFtpConfig);
		start.setEnabled(false);
		stop.setEnabled(false);
		DisplayMetrics dm = new DisplayMetrics();
		start.setWidth(dm.widthPixels/2);
		stop.setWidth(dm.widthPixels/2);
		FTPdownload.setWidth(dm.widthPixels/2);
		FTPupload.setWidth(dm.widthPixels/2);
		stopupdown=(Button)findViewById(R.id.button_stopftpserver);
		stopup = (Button)findViewById(R.id.button_stopftpupserver);
		stopupdown.setEnabled(false);
		stopup.setEnabled(false);
		
		resetupdown = (Button) findViewById(R.id.button_reset);
		resetupdown.setEnabled(false);
		

		FtpRuningThreadNum = (TextView) findViewById(R.id.ftpruningthread);
		FtpRuningThreadNumUp = (TextView)findViewById(R.id.ftpruningthreadup);
				
//		clientdown = null;
//		clientup = null;
		
		final SharedPreferences outsharedPreferences= getSharedPreferences("myConfig",
				Activity.MODE_PRIVATE); 
//		String FtpThread =outsharedPreferences.getString("FtpThread", "");
//		String FtpKilling =outsharedPreferences.getString("FtpKilling", "");
		FtpFileName = outsharedPreferences.getString("FtpFileName","");
		String tmpFtpLoopNum = outsharedPreferences.getString("FtpLoopNum","");
		String tmpFtpLoopTime = outsharedPreferences.getString("FtpLoopTime","");
		
//		if(!((FtpThread!=null)&&(FtpThread.length()!=0)))
//		{
//			FtpThread = "1";
//		}
//		if(!((FtpKilling!=null)&&(FtpKilling.length()!=0)))
//		{
//		    FtpKilling = "1";
//		}
		if(!((FtpFileName!=null)&&(FtpFileName.length()!=0)))
		{
			FtpFileName = "1.txt";
		}
		if(!((tmpFtpLoopNum!=null)&&(tmpFtpLoopNum.length()!=0)))
		{
			tmpFtpLoopNum = "0";
		}
		if(!((tmpFtpLoopTime!=null)&&(tmpFtpLoopTime.length()!=0)))
		{
			tmpFtpLoopTime = "0";
		}
				
		String s = "Config data: \r\n"+" Download Ftp file: "+FtpFileName+" FTP loop:"+tmpFtpLoopNum + " Ftp Time loop:"+tmpFtpLoopTime;
		showFtpConfig.setText(s);
//		ThreadNum = Integer.parseInt(FtpThread);
//		killNum = Integer.parseInt(FtpKilling);
		FtpLoopNum = Integer.parseInt(tmpFtpLoopNum);
        FtpLoopTime = Integer.parseInt(tmpFtpLoopTime);
        
        	
        
		stopupdown.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//String[] sucmd={"su"};
				foucedown = true;
				new Thread(new Runnable()
				{

					@Override
					public void run() {
						// TODO Auto-generated method stub
						int offset = 0;
						try {
							if(clientdown!=null)
							{
								if(offset>9)
									offset = 0;
					//		for(int i = 0;i<killNum;i++)
							for(int i = 0;i<1;i++)
							{
								if(clientdown[offset]!=null)
								{
								   clientdown[offset].abortCurrentDataTransfer(false);//必须用false
								   offset++;
								}
							}
							handler.sendEmptyMessage(21);
							}
							
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (FTPIllegalReplyException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IllegalStateException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					
				}).start();
			}
	
		});
		
		stopup.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				foucedown = true;
				new Thread(new Runnable()
				{

					@Override
					public void run() {
						// TODO Auto-generated method stub
							try {
								if(clientup[0]!=null)
								clientup[0].abortCurrentDataTransfer(true);
								handler.sendEmptyMessage(21);
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (FTPIllegalReplyException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}//必须用false
					}
					
				}).start();
			}
			
			
		});
		
		
		FTPdownload.setOnClickListener(new View.OnClickListener() {
			
			@SuppressWarnings("null")
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				foucedown = false;
				SimpleDateFormat  formatter =   new SimpleDateFormat("yyyy.MM.dd    HH:mm:ss     ");       
				Date  curDate = new Date(System.currentTimeMillis());//获取当前时间       
				String str = "new FTP report start time : "+formatter.format(curDate) + "\r\n";   	
				try {
					fout.write(str.getBytes());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				networkstate=isOpenNetwork();
				if(!networkstate){
					Toast.makeText(runFtp.this, "Please open network", Toast.LENGTH_LONG).show();
				}else{
				String FileName =outsharedPreferences.getString("FtpFileName", "");
			    FTPDownTask[] ftpdowntask = new FTPDownTask[10];
			//	for(int i = 0;i<ThreadNum;i++)
			    for(int i = 0;i<1;i++)
				{

				ftpdowntask[i]=new FTPDownTask(address,user,password,FtpFileName,i);

				ftpdowntask[i].execute(1000);
				}
		//		for(int i = 0;i<ThreadNum;i++)
	//			{
	//			
	//			ftpdowntask[i]=new FTPDownTask(address,user,password,String.valueOf(i+1)+".exe",i);

	//			ftpdowntask[i].execute(1000);
	//			}


				start.setEnabled(true);
				stop.setEnabled(true);
				resetupdown.setEnabled(true);
				stopupdown.setEnabled(true);}
				FTPdownload.setEnabled(false);
				FTPupload.setEnabled(false);
			}
		});
		
		FTPupload.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				foucedown = false;
				networkstate=isOpenNetwork();
				if(!networkstate){
					Toast.makeText(runFtp.this, "Please ebable network access", Toast.LENGTH_LONG).show();
				}else{

				FTPUpTask ftpuptask=new FTPUpTask(address,user,password,0);
				ftpuptask.execute(1000);

				
				start.setEnabled(true);
				stop.setEnabled(true);
				resetupdown.setEnabled(true);
				stopup.setEnabled(true);
				FTPdownload.setEnabled(false);
				FTPupload.setEnabled(false);}
			}
		});
		
		
		start.setOnClickListener(new View.OnClickListener() {		

			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				FTPdownload.setEnabled(false);
				FTPupload.setEnabled(false);
				start.setEnabled(false);
				stopupdown.setEnabled(false);
				stopup.setEnabled(false);
				resetupdown.setEnabled(true);
				startstop = true;
				intotal=0;
				outtotal=0;
				sec=0;
				Inmin=0;
				Inmax=0;
				outmin=0;
				outmax=0;
				i=1;
				k=true;
					rmnet = "rmnet0";//yiweng
				if(!rmnet.equals(null)){
			    //start.setVisibility(View.INVISIBLE);
			    //start.setEnabled(false);
					
		//	    MyTask task=new MyTask(resultText,start);//运行异步任务，执行对应脚本
		//	    task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,1000);
			    String s;
			    String cmd = "sh /data/data/com.MY.pingtest/netspeed rmnet0 ";
                Process p;
				try {
					p = Runtime.getRuntime().exec(cmd);
					startTime = System.currentTimeMillis();
					StringBuffer x = new StringBuffer();
		            BufferedReader bufferReader = new BufferedReader(new InputStreamReader(p.getInputStream()));
		            while ((s = bufferReader.readLine()) != null) {
		            	x.append(s.toString()).append("\r\n");
		            }
		         //   	String sk = normalizationNetSpeed(s);
		            	
	                     String sk = normalizationgSriptsInput(x.toString());
	                     //      Log.e("speed",str);
		            	
			       	     //rmnet0:;52575007;90059;0;0;0;0;0;0;12907064;111737;0;0;0;0;0;0;rmnet0:;52579369;90070;0;0;0;0;0;0;12908792;111751;0;0;0;0;0;0  
//			                52649816 12962812 52651163 12965072
			            	if((sk!=null)&&(sk.length()!=0))
			            	{
		      	                String[] resultarray=sk.split(";");
		      	                
		      	             //   float in = 0;
		      	             //   float out = 0;
		      	                String sin = resultarray[0];
		      	                String sout = resultarray[1];
		      	            inStart = Float.parseFloat(sin);
		      	            outStart = Float.parseFloat(sout);	
			        /*    	String[] rt=sk.split(";");
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
			                inStart = Integer.parseInt(oldin);
			                outStart = Integer.parseInt(oldout);			            	
			            	}*/
			            	}		            	
		            
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			    //end 
			    }else{
			    	Toast.makeText(runFtp.this, "No network signal", Toast.LENGTH_LONG).show();
			    }
			}	
		});
		
		
		stop.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				if(startstop)
				{
			    startstop= false;
			    String s;
			    String cmd = "sh /data/data/com.MY.pingtest/netspeed rmnet0";
                Process p;
				try {
					p = Runtime.getRuntime().exec(cmd);
					endTime = System.currentTimeMillis();
					StringBuffer x = new StringBuffer();
		            BufferedReader bufferReader = new BufferedReader(new InputStreamReader(p.getInputStream()));
		            while ((s = bufferReader.readLine()) != null) {
		            	x.append(s.toString()).append("\r\n");
		            }
		            	String sk = normalizationgSriptsInput(x.toString());
		            	String oldin = null;
		            	String oldout = null;
			       	     //rmnet0:;52575007;90059;0;0;0;0;0;0;12907064;111737;0;0;0;0;0;0;rmnet0:;52579369;90070;0;0;0;0;0;0;12908792;111751;0;0;0;0;0;0  
//			                52649816 12962812 52651163 12965072
			            	if((sk!=null)&&(sk.length()!=0))
			            	{
			            	}
			            	 String[] resultarray=sk.split(";");
	      	                String sin = resultarray[0];
	      	                String sout = resultarray[1];
		            float in = Float.parseFloat(sin);
		            float out= Float.parseFloat(sout);
		            
		            long elaspetime = (endTime-startTime)/1000;
		            
		            elaspetInSize = in - inStart;
		            elaspetOutSize = out - outStart;
		            
		            avgInSpeed = elaspetInSize*8 / elaspetime/1024.0;
		            avgOutSpeed = elaspetOutSize*8 / elaspetime/1024.0;	            
		            String sd = "\r\n"+"Completed"+" Elaspe time: "+ elaspetime + " Average In speed: "+String.format("%.2f", avgInSpeed)+"K" + " Average out speed :"+String.format("%.2f", avgOutSpeed)+"k"+" ElaspetInSize "
		            +elaspetInSize+" Byte "+" ElaspetOutSize "+elaspetOutSize+" Byte "+"\r\n"+"\r\n";
		            if(!app.getisenablemomentaryrate())
		            	sd=sd+pinjie[0]+pinjie[1]+pinjie[2]+pinjie[3]+"\r\n"+"\r\n";
		            resultText.append(sd);
		            fout.write(sd.getBytes());
		             	   
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				//calculate the avg speed
				}
				
				
				FTPdownload.setEnabled(true);
				FTPupload.setEnabled(true);
				start.setEnabled(true);
				resetupdown.setEnabled(true);
				stopupdown.setEnabled(true);
				stopup.setEnabled(true);
				k=false;
				
			}
		});
		
		resetupdown.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
	//			rwl.readLock().lock();
				rwl.writeLock().lock();
				startTime = System.currentTimeMillis();
				intotal=0;
				outtotal=0;
				sec=0;
				Inmin=0;
				Inmax=0;
				outmin=0;
				outmax=0;
				i=1;
				rwl.writeLock().unlock();
	//			rwl.readLock().unlock();
				resultText.setText("\r\n reset \r\n");
			}
			
		});
		
	}
	

	class FTPUpTask extends AsyncTask<Integer,String,String>{
		
		private String address;
		private String user;
		private String password;
		private int id;
		public FTPUpTask(String address, String user, String password, int i) {
			// TODO Auto-generated constructor stub
			this.address=address;
			this.user=user;
			this.password=password;
			this.id = i;
		}

		@Override
		protected String doInBackground(Integer... params) {
			// TODO Auto-generated method stub
			String Imei = ((TelephonyManager) getSystemService(TELEPHONY_SERVICE)).getDeviceId();
			
			if(FtpLoopNum != 0)
			{
			int fullLoop = FtpLoopNum;
		    while((fullLoop > 0)&&(!foucedown))
		    {
		    	fullLoop --;
	        String s = "Config data: \r\n"+" Download Ftp file: "+FtpFileName+" FTP loop:"+String.valueOf(FtpLoopNum) + " Ftp Time loop:"+String.valueOf(FtpLoopTime)+"\r\n";
	        String s1 = "current loop is "+String.valueOf(FtpLoopNum - fullLoop);
            Message msg;
            msg = Message.obtain();
            msg.what = 10;
            msg.obj = s+s1;
            handler.sendMessage(msg);
//			String[] aaa={"/data/local/tmp/busybox","ftpput",address, Imei+String.valueOf(random),"/data/local/tmp/upfiledemo","-u",user,"-p",password};
//			String[] aaa={"/data/local/tmp/busybox","ftpput",address, Imei+String.valueOf(random),"/data/local/tmp/upfiledemo","-u",user,"-p",password};
			lisetner_up[id] = new MyUpTransferListener(id);
			File localfile = new File("/data/data/com.MY.pingtest/upfiledemo");
			try {
				clientup[id] = new FTPClient();
				clientup[id].connect(this.address);
				clientup[id].login(this.user,this.password);
				clientup[id].upload(localfile,lisetner_up[id]);
			} catch (IllegalStateException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (FTPIllegalReplyException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (FTPException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
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
	//		   long endTime = System.currentTimeMillis()+FtpLoopTime*1000;//毫秒
        	   while((System.currentTimeMillis()<endTime)&&(!foucedown))
        	   {
               	String s = "Config data: \r\n"+" Download Ftp file: "+FtpFileName+" FTP loop:"+String.valueOf(FtpLoopNum) + " Ftp Time loop:"+String.valueOf(FtpLoopTime)+"\r\n";
   				Date  curDate = new Date(System.currentTimeMillis());//获取当前时间       
   			//	String str = formatter.format(curDate);   	
   				Date  curDate1 = new Date(endTime);//获取当前时间       
               	String s1 = "current time is "+formatter.format(curDate)+" End time is "+ formatter.format(curDate1);
               //	showFtpConfig.setText(s+s1);
                   Message msg;
                   msg = Message.obtain();
                   msg.what = 10;
                   msg.obj = s+s1;
                   handler.sendMessage(msg);
      // 			double random=Math.random();
//    			String[] aaa={"/data/local/tmp/busybox","ftpput",address, Imei+String.valueOf(random),"/data/local/tmp/upfiledemo","-u",user,"-p",password};
//    			String[] aaa={"/data/local/tmp/busybox","ftpput",address, Imei+String.valueOf(random),"/data/local/tmp/upfiledemo","-u",user,"-p",password};
    			lisetner_up[id] = new MyUpTransferListener(id);
    			File localfile = new File("/data/data/com.MY.pingtest/upfiledemo"+Imei);
    			try {
    				clientup[id] = new FTPClient();
    				clientup[id].connect(this.address);
    				clientup[id].login(this.user,this.password);
    				clientup[id].upload(localfile,lisetner_up[id]);
    			} catch (IllegalStateException e1) {
    				// TODO Auto-generated catch block
    				e1.printStackTrace();
    			} catch (IOException e1) {
    				// TODO Auto-generated catch block
    				e1.printStackTrace();
    			} catch (FTPIllegalReplyException e1) {
    				// TODO Auto-generated catch block
    				e1.printStackTrace();
    			} catch (FTPException e1) {
    				// TODO Auto-generated catch block
    				e1.printStackTrace();
    			} catch (FTPDataTransferException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			} catch (FTPAbortedException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			}
        	   }
				
			}
			return null;
		}
		
    }
	

	
	class FTPDownTask extends AsyncTask<Integer,String,String>{
		
		private String address;
		private String user;
		private String password;
		private String filename;
		private int id;
		String path="/data/local/tmp";
		
		public FTPDownTask(String address, String user, String password,String filename, int i) {
			// TODO Auto-generated constructor stub
			this.address=address;
			this.user=user;
			this.password=password;
			this.filename = filename;
			this.id = i;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
		}

		@Override
		protected String doInBackground(Integer... params) {
			// TODO Auto-generated method stub
			
            
            if(FtpLoopNum != 0)
            {
            int fullLoop = FtpLoopNum;
            while((fullLoop > 0)&&(!foucedown))
            {
            	fullLoop--;
        	String s = "Config data: \r\n"+" Download Ftp file: "+FtpFileName+" FTP loop:"+String.valueOf(FtpLoopNum) + " Ftp Time loop:"+String.valueOf(FtpLoopTime)+"\r\n";
        	String s1 = "current loop is "+String.valueOf(FtpLoopNum - fullLoop);
    //    	showFtpConfig.setText(s+s1);
            Message msg;
            msg = Message.obtain();
            msg.what = 10;
            msg.obj = s+s1;
            handler.sendMessage(msg);
			File fileDir =getFilesDir();
			File localfile = new File("/data/data/com.MY.pingtest/files/net-tool.zip"+String.valueOf(id));
			f[id] = new File("/data/data/com.MY.pingtest/files/net-tool.zip"+String.valueOf(id));
			lisetner_down[id] = new MyDownTransferListener(id);
			try {
				fw = new FileWriter(f[id]);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
                clientdown[this.id] = new FTPClient();
				clientdown[this.id].connect(this.address);
				clientdown[this.id].login(this.user,this.password);

				clientdown[this.id].download(this.filename, localfile,lisetner_down[id]);
 
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
			
			return null;
            }//if FtpLoopNum != 0
            else if(FtpLoopTime != 0)
            {
            	SimpleDateFormat  formatter =   new SimpleDateFormat("yyyy.MM.dd    HH:mm:ss     ");       
            	long endTime = System.currentTimeMillis()+FtpLoopTime*1000;//毫秒
            	while((System.currentTimeMillis()<endTime)&&(!foucedown))
            	{
                	String s = "Config data: \r\n"+" Download Ftp file: "+FtpFileName+" FTP loop:"+String.valueOf(FtpLoopNum) + " Ftp Time loop:"+String.valueOf(FtpLoopTime)+"\r\n";
    				Date  curDate = new Date(System.currentTimeMillis());//获取当前时间       
    			//	String str = formatter.format(curDate);   	
    				Date  curDate1 = new Date(endTime);//获取当前时间       
                	String s1 = "current time is "+formatter.format(curDate)+" End time is "+ formatter.format(curDate1);
                //	showFtpConfig.setText(s+s1);
                    Message msg;
                    msg = Message.obtain();
                    msg.what = 10;
                    msg.obj = s+s1;
                    handler.sendMessage(msg);
                    
        			File fileDir =getFilesDir();
        			File localfile = new File("/data/data/com.MY.pingtest/files/net-tool.zip"+String.valueOf(id));
        			f[id] = new File("/data/data/com.MY.pingtest/files/net-tool.zip"+String.valueOf(id));
        			lisetner_down[id] = new MyDownTransferListener(id);
        			try {
        				fw = new FileWriter(f[id]);
        			} catch (IOException e) {
        				// TODO Auto-generated catch block
        				e.printStackTrace();
        			}
        			try {
                        clientdown[this.id] = new FTPClient();
        				clientdown[this.id].connect(this.address);
        				clientdown[this.id].login(this.user,this.password);

        				clientdown[this.id].download(this.filename, localfile,lisetner_down[id]);
         
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
            	return null;
            }
	//		String[] aaa={"busybox","ftpget",address,"/dev/null","net_tool.zip","-u",user,"-p",password};
	/*		String localfilename = "/data/local/tmp/"+this.filename;
			Log.i("check",localfilename);
			File localfile = new File(localfilename);//yiweng
			Log.i("check ","Background in");
	//		String[] aaa={"/data/local/tmp/busybox","ftpget",address,localfilename,"net_tool.zip","-u",user,"-p",password};
	//		String[] aaa={"/data/local/tmp/busybox","ftpget",address,"/dev/null",this.filename,"-u",user,"-p",password};
			String[] aaa={"/data/local/tmp/busyboxdownup","ftpget",address,"/dev/null",this.filename,"-u",user,"-p",password};
			String str = null;
			try {
				runOnly(aaa,path);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
			
			
			return null;
		}
			
	}
	
	
	class MyTask extends AsyncTask<Integer,String,String>{
	
		private TextView resultText;
		private TextView start;
		public MyTask(TextView resultText, TextView start) {
			// TODO Auto-generated constructor stub
			this.resultText=resultText;
			this.start=start;
		}
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			resultText.setText("");
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			//resultText.setText(result);
		}

		@Override
		protected String doInBackground(Integer... params) {
			// TODO Auto-generated method stub
			String[] cmd={"sh","/data/local/tmp/netspeed", rmnet,"1"};
		    String workdirectory="/data/local/tmp";
		
		     // 循环运行netspeed脚本，直到用户停止
		   try {
			   while(k){
				   String runresult;
                   String cmdPing = "sh /data/local/tmp/netspeed rmnet0 1";
                   Process p = Runtime.getRuntime().exec(cmdPing);
                   BufferedReader bufferReader = new BufferedReader(new InputStreamReader(p.getInputStream()));
                   while ((runresult = bufferReader.readLine()) != null) {
                	   result.append(runresult.toString()).append("\r\n");
                	   }
		//		String runresult=run(cmd,workdirectory);
				
				Intent mIntent = new Intent(ACTION_NAME);
                mIntent.putExtra("result", result.toString());    
                sendBroadcast(mIntent);
                result.delete(0, result.length());
                
      //       	fout =openFileOutput("/data/data/com.MY.pingtest/files/net-tool.zip" , MODE_PRIVATE);//不覆盖
                Thread.sleep(1000);
		            }  
		       }
		   catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		   return null;
		}
		
	}
	
	   public class MyDownTransferListener implements FTPDataTransferListener
		{

		    int id;
			public MyDownTransferListener(int id) {
			// TODO Auto-generated constructor stub
				this.id = id;
		}

			@Override
			public void completed() {
				// TODO Auto-generated method stub
                Message msg;
                msg = Message.obtain();
                msg.what = 0;
                msg.obj = id;
                handler.sendMessage(msg);  
                try {
					fout.write("DL completed".getBytes());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			@Override
			public void failed() {
				// TODO Auto-generated method stub
		//		Log.i("transfering","file falied");
                Message msg;
                msg = Message.obtain();
                msg.what = 3;
                msg.obj = id;
                handler.sendMessage(msg);  
                try {
					fout.write("DL failed".getBytes());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			@Override
			public void started() {
				// TODO Auto-generated method stub
                Message msg;
                msg = Message.obtain();
                msg.what = 6;
                msg.obj = id;
                handler.sendMessage(msg);  
				
			}

			@Override
			public void aborted() {
				// TODO Auto-generated method stub
                Message msg;
                msg = Message.obtain();
                msg.what = 3;
                msg.obj = id;
                handler.sendMessage(msg);  
				
                try {
					fout.write("DL aborted".getBytes());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			@Override
			public void transferred(int arg0) {
				// TODO Auto-generated method stub
                Message msg;
                msg = Message.obtain();
                msg.what = 1;
                msg.obj = id;
                handler.sendMessage(msg);  
			}
		}
	   
	   public class MyUpTransferListener implements FTPDataTransferListener
	   {
        int id;
        public MyUpTransferListener(int id2)
        {
        	id = id2;
        }
		@Override
		public void aborted() {
			// TODO Auto-generated method stub
            Message msg;
            msg = Message.obtain();
            msg.what = 4;
            msg.obj = id;
            handler.sendMessage(msg);  
            try {
				fout.write("UL aborted".getBytes());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		@Override
		public void completed() {
			// TODO Auto-generated method stub
             Message msg;
             msg = Message.obtain();
             msg.what = 5;
             msg.obj = id;
             handler.sendMessage(msg);  
             try {
					fout.write("UL completed".getBytes());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}

		@Override
		public void failed() {
			// TODO Auto-generated method stub
            Message msg;
            msg = Message.obtain();
            msg.what = 4;
            msg.obj = id;
            handler.sendMessage(msg);  
            try {
				fout.write("UL failed".getBytes());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		@Override
		public void started() {
			// TODO Auto-generated method stub
			handler.sendEmptyMessage(7);
		}

		@Override
		public void transferred(int arg0) {
			// TODO Auto-generated method stub
			
		}
		   
	   }
	   
	   private final Handler handler = new Handler(){
	        @Override
	        public void handleMessage(final Message msg) {
	            switch (msg.what) {
	            case 0:		  
	            	Toast.makeText(runFtp.this, "Ftp download done", Toast.LENGTH_LONG).show();           	
	            	f[(Integer) msg.obj].delete();
	            	workingDownload--;
	            	FtpRuningThreadNum.setText(String.valueOf(workingDownload));
					FTPdownload.setEnabled(true);
					FTPupload.setEnabled(true);
	                break;
	            case 1:				
					try {
						f[(Integer) msg.obj] = new File("/data/data/com.MY.pingtest/files/net-tool.zip"+String.valueOf((Integer) msg.obj));
	                    fw = new FileWriter(f[(Integer) msg.obj]);		
						fw.write(" ");
						fw.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;
	            case 2:
	                break;
	            case 3:  
	            	Toast.makeText(runFtp.this, "Ftp download abort/failed", Toast.LENGTH_LONG).show();     
	            	workingDownload--;
	            	FtpRuningThreadNum.setText(String.valueOf(workingDownload));
	            	f[(Integer) msg.obj].delete();
					FTPdownload.setEnabled(true);
					FTPupload.setEnabled(true);
	            	break;
	            case 4:
	            	Toast.makeText(runFtp.this, "Ftp upload abort/failed", Toast.LENGTH_LONG).show(); 
	            	workingUpload--;
	            	FtpRuningThreadNumUp.setText(String.valueOf(workingUpload));
					FTPdownload.setEnabled(true);
					FTPupload.setEnabled(true);
	            	break;
	            case 5:
	            	Toast.makeText(runFtp.this, "Ftp upload down", Toast.LENGTH_LONG).show();
	            	workingUpload--;
	            	FtpRuningThreadNumUp.setText(String.valueOf(workingUpload));
					FTPdownload.setEnabled(true);
					FTPupload.setEnabled(true);
	            	break;
	            case 6:
	            	workingDownload++;
	            	FtpRuningThreadNum.setText(String.valueOf(workingDownload));
	            	break;
	            case 7:
	            	workingUpload++;
	            	FtpRuningThreadNumUp.setText(String.valueOf(workingUpload));
	                break;
	                
	            case 10:
	            	String data = msg.obj.toString();
	            	showFtpConfig.setText(data);        	
	            case 21:
					FTPdownload.setEnabled(true);
					FTPupload.setEnabled(true);
					break;
	            default:
	                break;
	            }
	        }
	    };
}
	
	



