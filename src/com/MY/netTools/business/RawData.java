package com.MY.netTools.business;

import java.nio.channels.SocketChannel;

import android.app.Application;
import android.content.ServiceConnection;
import android.telephony.TelephonyManager;


public class RawData extends Application {

	private String RawIpData;
	private String FtpData;
	private boolean isTriggered = false;
	private int repeattime = 0;
    private boolean isCleanPerformance = false;
    private boolean isenablemomentaryrate = true;
	private ServiceConnection mServiceConnection;
    
    public String activeNetInfo = null;
    public String mobNetInfo = null;
    public ExitAppUtils exitAppUtils = new ExitAppUtils();
    public static ToolSrvImpl tool = new ToolSrvImpl();
    
    //for autotest
    public static SocketChannel socketChannel;
  //  public boolean rebootServiceForTaskDF = false;
 //   public boolean rebootServiceForServerDown = false;
 //   public boolean reconnectServer = false;
    public static Object Tlock=new Object();
    
    public static String SignalStrengths = " ";
    public static boolean ServerHaveCommand = false;
 
    
	public void setServiceConnection(ServiceConnection data)
	{
		this.mServiceConnection = data;
	}
    
    public ServiceConnection getServiceConnection()
    {
    	return this.mServiceConnection;
    }
    
	public void setCleanPerformance(boolean data)
	{
		this.isCleanPerformance = data;
	}

	public boolean getCleanPerformance()
	{
		return this.isCleanPerformance;		
	}
	
	public void setisenablemomentaryrate(boolean data)
	{
		this.isenablemomentaryrate = data;
	}
	
	public boolean getisenablemomentaryrate()
	{
		return this.isenablemomentaryrate;		
	}
	
	public void setrepeattime(int data)
	{
		this.repeattime = data;
	}
	
	public int getrepeattime()
	{
		return this.repeattime;		
	}
	public void setIpData(String data)
	{
		this.RawIpData = new String(data);
	}
	
	public void setFtpData(String data)
	{
		this.FtpData = new String(data);
	}
	
	public String getIpData()
	{
		return this.RawIpData;		
	}
	
	public String getFtpData()
	{
		return this.FtpData;		
	}

	public void setIsTriggered(boolean data)
	{
		this.isTriggered = data;
	}
	
	public boolean getIsTriggered()
	{
		return this.isTriggered;
	}
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		String Imei = ((TelephonyManager) getSystemService(TELEPHONY_SERVICE)).getDeviceId();
		tool.setIMEI(Imei);
		tool.init();
        CrashHandler mCustomCrashHandler = CrashHandler.getInstance();  
        mCustomCrashHandler.setCustomCrashHanler(getApplicationContext());  
		super.onCreate();
	}
	
}
