package com.MY.netTools.business;
import java.util.LinkedList;  
import java.util.List;  
  
import android.app.Activity;  
import android.app.Service;
	public class ExitAppUtils {  
	    /** 
	     * ת��Activity������ 
	     */  
	    private List<Activity> mActivityList = new LinkedList<Activity>();  
	    private List<Service> mServiceList = new LinkedList<Service>();  
	    private static ExitAppUtils instance = new ExitAppUtils();  
	      
	    /** 
	     * �����캯��˽�л� 
	     */  
	    ExitAppUtils(){};  
	      
	    /** 
	     * ��ȡExitAppUtils��ʵ������ֻ֤��һ��ExitAppUtilsʵ������ 
	     * @return 
	     */  
	    public static ExitAppUtils getInstance(){  
	        return instance;  
	    }  
	  
	      
	    /** 
	     * ���Activityʵ����mActivityList�У���onCreate()�е��� 
	     * @param activity 
	     */  
	    public void addActivity(Activity activity){  
	        mActivityList.add(activity);  
	    }  
	    
	    public void addService(Service service){  
	    	mServiceList.add(service);  
	    }  
	      
	    /** 
	     * ��������ɾ�������Activityʵ������onDestroy()�е��� 
	     * @param activity 
	     */  
	    public void delActivity(Activity activity){  
	        mActivityList.remove(activity);  
	    }  
	    
	    public void delService(Service service){  
	    	mServiceList.remove(service);  
	    }  
	      
	      
	    /** 
	     * �˳�����ķ��� 
	     */  
	    public void exit(){  
	        for(Activity activity : mActivityList){  
	            activity.finish();  
	        }  
	        for(Service service : mServiceList){  
	          service.stopSelf();
	        }  
	        System.exit(0);  
	    }  
}
