package com.MY.netTools.business;

import java.util.LinkedList;
import java.util.List;

import android.util.Log;



public class MultiTaskCommand extends CommandBase {
	
	public int loop;
	public int len;
	
//	public DL dl;
//	public UL ul;
//	public PING ping;
	
	
	public static List<DL> dllist = new LinkedList<DL>();
	public static List<UL> ullist = new LinkedList<UL>();
	public static List<PING> pinglist = new LinkedList<PING>();
	
	public class DL
	{
		public String DurationTime;
        public String Address;
        public String Usr;
        public String Password;  
        public String filename;
		public DL(String command) {
			// TODO Auto-generated constructor stub
			String[] resultarray=command.split(" ");
			if(resultarray.length==6)
			{
			if((resultarray[1]!=null)&&(resultarray[1].length()!=0))
			{
				DurationTime = resultarray[1];
			}
			if((resultarray[2]!=null)&&(resultarray[2].length()!=0))
			{
			Address = resultarray[2];
			}
			if((resultarray[3]!=null)&&(resultarray[3].length()!=0))
			{
			Usr = resultarray[3];
			}
			if((resultarray[4]!=null)&&(resultarray[4].length()!=0))
			{
			Password = resultarray[4];
			}
			if((resultarray[5]!=null)&&(resultarray[5].length()!=0))
			{
			filename = resultarray[5];
			}
			}
			else
			{
				RawData.tool.STrace("ERROR", "invalid incoming FTP message for multiTask,"+command);
			}
		}		
	}
	
	public class UL
	{
		public String DurationTime;
        public String Address;
        public String Usr;
        public String Password;  

		public UL(String command) {
			// TODO Auto-generated constructor stub
			String[] resultarray=command.split(" ");
		//	Log.e("resultarray"," "+resultarray.length);
			if(resultarray.length==5)
			{
			if((resultarray[1]!=null)&&(resultarray[1].length()!=0))
			{
			DurationTime = resultarray[1];
			}
			if((resultarray[2]!=null)&&(resultarray[2].length()!=0))
			{
			Address = resultarray[2];
			}
			if((resultarray[3]!=null)&&(resultarray[3].length()!=0))
			{
			Usr = resultarray[3];
			}
			if((resultarray[4]!=null)&&(resultarray[4].length()!=0))
			{
			Password = resultarray[4];
			}
			}
			else
			{
				RawData.tool.STrace("ERROR", "invalid incoming FTP message,"+command);
			}
		}
		
	}
	public class PING
	{
		public String DurationTime;
        public String Address;
        public String PackageSize;
        public String mM;
		public PING(String command) {
			// TODO Auto-generated constructor stub
			String[] resultarray=command.split(" ");
			if(resultarray.length==5)
			{
			if((resultarray[1]!=null)&&(resultarray[1].length()!=0))
			{
				DurationTime = resultarray[1];
			}
			if((resultarray[2]!=null)&&(resultarray[2].length()!=0))
			{
				Address = resultarray[2];
			}
			if((resultarray[3]!=null)&&(resultarray[3].length()!=0))
			{
				PackageSize = resultarray[3];
			}
			if((resultarray[4]!=null)&&(resultarray[4].length()!=0))
			{
				mM = resultarray[4];
			}
			}
		}
		
	}
	
	public MultiTaskCommand(String obj) {
		
		// TODO Auto-generated constructor stub
		String[] resultarray=obj.split(":");
		{
			if(resultarray.length > 3)
			{
			if((resultarray[0]!=null)&&(resultarray[0].length()!=0))
			{
				loop = Integer.parseInt(resultarray[0]);
			}
			if((resultarray[1]!=null)&&(resultarray[1].length()!=0))
			{
				len = Integer.parseInt(resultarray[1]);
			}
			}
			for(int i = 0;i<len;i++)
			{
				String[] rt=resultarray[i+2].split(" ");
				switch (convertTag(rt[0]))
				{
					case 1:
					{
						DL dl = new DL(resultarray[i+2]);
						dllist.add(dl);
					}
					break;
					case 2:
					{
						UL ul = new UL(resultarray[i+2]);
						ullist.add(ul);
					}
					break;
					case 3:
					{
						PING ping = new PING(resultarray[i+2]);
						pinglist.add(ping);
					}
					break;
					default:
						break;
				}
			}
		}
	}

	private int convertTag(String string) {
		// TODO Auto-generated method stub
		int rt = -1;
		if(string.equals("FTPDL"))
			rt = 1;
		else if(string.equals("FTPUL"))
			rt = 2;
		else if(string.equals("PING"))
			rt = 3;
				
		return rt;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		
		String rtn = " loop ="+loop+" len = "+len;
		if(dllist.size() != 0)
		{
			rtn = rtn +" "+" DL "+dllist.toString();
		}
		if(ullist.size() != 0)
		{
			rtn = rtn +" "+" ul "+ullist.toString();
		}
		if(pinglist.size() != 0)
		{
			rtn = rtn+" "+" ping "+pinglist.toString();
		}		
		return rtn;
		
	}
    
}
