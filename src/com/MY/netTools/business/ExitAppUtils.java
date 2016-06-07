package com.MY.netTools.business;
import java.util.LinkedList;  
import java.util.List;  
  
import android.app.Activity;  
import android.app.Service;
	public class ExitAppUtils {  
	    /** 
	     * 转载Activity的容器 
	     */  
	    private List<Activity> mActivityList = new LinkedList<Activity>();  
	    private List<Service> mServiceList = new LinkedList<Service>();  
	    private static ExitAppUtils instance = new ExitAppUtils();  
	      
	    /** 
	     * 将构造函数私有化 
	     */  
	    ExitAppUtils(){};  
	      
	    /** 
	     * 获取ExitAppUtils的实例，保证只有一个ExitAppUtils实例存在 
	     * @return 
	     */  
	    public static ExitAppUtils getInstance(){  
	        return instance;  
	    }  
	  
	      
	    /** 
	     * 添加Activity实例到mActivityList中，在onCreate()中调用 
	     * @param activity 
	     */  
	    public void addActivity(Activity activity){  
	        mActivityList.add(activity);  
	    }  
	    
	    public void addService(Service service){  
	    	mServiceList.add(service);  
	    }  
	      
	    /** 
	     * 从容器中删除多余的Activity实例，在onDestroy()中调用 
	     * @param activity 
	     */  
	    public void delActivity(Activity activity){  
	        mActivityList.remove(activity);  
	    }  
	    
	    public void delService(Service service){  
	    	mServiceList.remove(service);  
	    }  
	      
	      
	    /** 
	     * 退出程序的方法 
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
