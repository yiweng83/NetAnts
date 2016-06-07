package com.MY.netTools.business;

import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class httpKpi {
	public int totalHttpGet = 0;
	public int partHttpGet = 0;
	public float totalHttp200 = 0;
	public float partHttp200 = 0;
	public int totalHttpDlOK= 0;
	public int partHttpDlOK= 0;
	public int lastindex = 0;
	public String avgspeed1;
	public List<String> speed = new LinkedList<String>();
	public List<String> time200 = new LinkedList<String>();
	public List<String> timeD = new LinkedList<String>();
	public Queue<String> Speedcalc = new LinkedList<String>();
    public httpKpi()
	{
		
	}
    public String calcSpeedD()
    {
    	float Speed = 0;
    	
    	
    	int len = Speedcalc.size();
    	Iterator it = Speedcalc.iterator();
        while(it.hasNext())
        {
        	Speed = Speed + Float.parseFloat((String) it.next());
        }
        speed.add(String.valueOf(Speed/len));	
//        String rtn  = String.valueOf(Speed/len);
        DecimalFormat fnum = new DecimalFormat("##0.00"); 
        String rtn=fnum.format(Speed/len);
  //      String rtn = String.format("%2d", Long.parseLong(Speed/len));
        Speedcalc.clear();
        return rtn;
    }
    
    public String calcSpeed()
    {
    	long Speed = 0;
    	int len = speed.size();
    	Iterator it = speed.iterator();
        while(it.hasNext())
        {
        	Speed = Speed + Long.parseLong((String) it.next());
        }
       return String.valueOf(Speed/len);		   
    }
    public String calctime200()
    {
    	long Speed = 0;
    	int len = time200.size();
    	Iterator it = time200.iterator();
        while(it.hasNext())
        {
        	Speed = Speed + Long.parseLong((String) it.next());
        }
       return String.valueOf(Speed/len);		   
    }
    public String calctimeD()
    {
    	long Speed = 0;
    	int len = timeD.size();
    	Iterator it = timeD.iterator();
        while(it.hasNext())
        {
        	Speed = Speed + Long.parseLong((String) it.next());
        }
       return String.valueOf(Speed/len);		   
    }
    
    public void clean()
    {
    	totalHttpGet = 0;
    	partHttpGet = 0;
    	totalHttp200 = 0;
    	partHttp200 = 0;
    	totalHttpDlOK = 0;
    	speed.clear();
    	time200.clear();
    	timeD.clear();
    	Speedcalc.clear();
    	
    }
	public String result() {
		// TODO Auto-generated method stub
		String kpi200 = String.valueOf(totalHttp200/totalHttpGet);
		
		String data = kpi200+" "+calctime200()+" "+" "+" "+calctimeD()+" "+calcSpeed();
		
		return data;
	}
	public String partResult(int index)
	{
		String kpi200part = String.valueOf((partHttp200/partHttpGet)*100)+"%"; 
		String kpiHttppart = String.valueOf(((partHttpGet+totalHttpDlOK)/partHttpGet)*100)+"%";
		
		String avgspeed = calcSpeedD();
		long parttime200 = 0;
		long parttimeD = 0;
		
		for(int i = lastindex;i<index;i++)
		{
			parttime200 = parttime200+Long.parseLong(time200.get(i));
			parttimeD = parttimeD + Long.parseLong(timeD.get(i));
		}
		String data = "HTTP connect :"+kpi200part+" "+"avg responsed time:"+parttime200/(index-lastindex)+"ms "+"HTTP download data :"+kpiHttppart+" "+"HTTP download time:"+parttimeD/(index-lastindex)+"ms "+"HTTP download avg speed:"+avgspeed;
												
		lastindex = index;
		return data;
		
	}
}
