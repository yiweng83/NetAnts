package com.MY.netTools.business;

public class myMessage implements Comparable<myMessage>
{
	public String refNum;
	public String tag;
	public String startTime;
	public String endTime;  	
	public String command = null;
	public CommandBase realCommand = null;
	public myMessage(String obj)
	{
		String[] resultarray=obj.split(";");
		if(resultarray.length!=0)
		{
        if((resultarray[1]!=null)&&(resultarray[1].length()!=0))
	    {
        refNum = resultarray[1];
		}
		if((resultarray[2]!=null)&&(resultarray[2].length()!=0))
		{
		tag = resultarray[2];
		}
		if((resultarray[3]!=null)&&(resultarray[3].length()!=0))
		{
		startTime = resultarray[3];
		}
		if((resultarray[4]!=null)&&(resultarray[4].length()!=0))
		{
		endTime = resultarray[4];
		}
		if((resultarray[5]!=null)&&(resultarray[5].length()!=0))
		{
		command = resultarray[5];
		}
		}
		normalizationcommand();
	}
	public String toString()
	{
		return tag+startTime+endTime+command;
	}
	@Override
	public int compareTo(myMessage another) {
		// TODO Auto-generated method stub
		if(Long.parseLong(startTime) == Long.parseLong(another.startTime))
		{
			return -1;
		}
		if(Long.parseLong(startTime) > Long.parseLong(another.startTime))
		{
			return 1;
		}
		if(Long.parseLong(startTime) < Long.parseLong(another.startTime))
		{
			return -1;
		}
		return 0;
	}
    public int convertTag()
    {
       int rtn  = -1;
       if(tag.equals("FTPDL"))
    	   rtn = 1;
       if(tag.equals("PING"))
    	   rtn = 2;
       if(tag.equals("URL"))
    	   rtn = 3;
       if(tag.equals("FTPUL"))
    	   rtn = 4;
       if(tag.equals("DFU"))
    	   rtn = 5;       
       if(tag.equals("DFD"))
    	   rtn = 6;
       if(tag.equals("MULTI"))
    	   rtn = 7;
       if(tag.equals("OTCALL"))
           rtn = 8;
       if(tag.equals("HTTP"))
    	   rtn = 9;
       if(tag.equals("SWECHAT"))
    	   rtn = 10;
       
       return rtn;
    }
    public void normalizationcommand()
    {
    	int tag = convertTag();
    	switch (tag)
    	{
    		case 1:
    		normalizationFTPDLcommand();
    		break;
    		case 2:
    		normalizationPINGcommand();
    		break;
    		case 3:
    	    normalizationURLcommand();
    	    break;
    		case 4:
    		normalizationFTPULcommand();
    		break;
    		case 5:
    		normalizationDFUcommand();
    		break;
    		case 6:
    		normalizationDFDcommand();
    		break;
    		case 7:
    		normalizationMultiTaskcommand();
    		break;
    		case 8:
    	    normalizationOTCALLTaskcommand();
    	    break;
    		case 9:
    		normalizationHTTPcommand();
    		break;
    		case 10:
    	    normalizationSWECHATcommand();
    	    break;
    		
    	    default:
    	    break;
    	}
    }
	private void normalizationSWECHATcommand() {
		// TODO Auto-generated method stub
		realCommand = new SWECHATCommand(command);
	}
	private void normalizationOTCALLTaskcommand() {
		// TODO Auto-generated method stub
		realCommand = new OTCALLCommand(command);
	}
	private void normalizationMultiTaskcommand() {
		// TODO Auto-generated method stub
		realCommand = new MultiTaskCommand(command);
	}
	private void normalizationDFDcommand() {
		// TODO Auto-generated method stub
		realCommand = new DFDCommand(command);
	}
	private void normalizationDFUcommand() {
		// TODO Auto-generated method stub
		realCommand = new DFUCommand(command);
	}
	private void normalizationFTPULcommand() {
		// TODO Auto-generated method stub
		realCommand = new FtpULCommand(command);
	}
	private void normalizationURLcommand() {
		// TODO Auto-generated method stub
		realCommand = new UrlCommand(command);
	}
	private void normalizationPINGcommand() {
		// TODO Auto-generated method stub
		realCommand = new PingCommand(command);
	}
	private void normalizationFTPDLcommand() {
		// TODO Auto-generated method stub
		realCommand = new FtpDLCommand(command);
		
	}
	private void normalizationHTTPcommand() {
		// TODO Auto-generated method stub
		realCommand = new HTTPCommand(command);
		
	}
	public class FtpDLCommand extends CommandBase
	{
        public String Address;
        public String Usr;
        public String Password;  
        public String filename;
		public FtpDLCommand(String command) {
			// TODO Auto-generated constructor stub
			String[] resultarray=command.split(":");
			if(resultarray.length==4)
			{
			if((resultarray[0]!=null)&&(resultarray[0].length()!=0))
			{
			Address = resultarray[0];
			}
			if((resultarray[1]!=null)&&(resultarray[1].length()!=0))
			{
			Usr = resultarray[1];
			}
			if((resultarray[2]!=null)&&(resultarray[2].length()!=0))
			{
			Password = resultarray[2];
			}
			if((resultarray[3]!=null)&&(resultarray[3].length()!=0))
			{
			filename = resultarray[3];
			}
			}
			else
			{
				RawData.tool.STrace("ERROR", "invalid incoming FTP message,"+command);
			}
		}		
	}
	
	public class FtpULCommand extends CommandBase
	{
        public String Address;
        public String Usr;
        public String Password;  

		public FtpULCommand(String command) {
			// TODO Auto-generated constructor stub
			String[] resultarray=command.split(":");
			if(resultarray.length==3)
			{
			if((resultarray[0]!=null)&&(resultarray[0].length()!=0))
			{
			Address = resultarray[0];
			}
			if((resultarray[1]!=null)&&(resultarray[1].length()!=0))
			{
			Usr = resultarray[1];
			}
			if((resultarray[2]!=null)&&(resultarray[2].length()!=0))
			{
			Password = resultarray[2];
			}
			}
			else
			{
				RawData.tool.STrace("ERROR", "invalid incoming FTP message,"+command);
			}
		}
		
	}
	public class PingCommand extends CommandBase
	{
        public String Address;
        public String PackageSize;
        public String mM;
		public PingCommand(String command) {
			// TODO Auto-generated constructor stub
			String[] resultarray=command.split(":");
			if(resultarray.length!=0)
			{
			if((resultarray[0]!=null)&&(resultarray[0].length()!=0))
			{
				Address = resultarray[0];
			}
			if((resultarray[1]!=null)&&(resultarray[1].length()!=0))
			{
				PackageSize = resultarray[1];
			}
			if((resultarray[2]!=null)&&(resultarray[2].length()!=0))
			{
				mM = resultarray[2];
			}
			}
		}
		
	}
	public class UrlCommand extends CommandBase
	{
		public String Address;
		public UrlCommand(String command) {
			// TODO Auto-generated constructor stub
			if(command.length()!=0)
			{
			Address = command;
			}
		}
	}
	
	public class DFUCommand extends CommandBase
	{
		public String DetachTimer;
		public String loop;
        public String Address;
        public String Usr;
        public String Password; 
        public DFUCommand(String command)
        {
			String[] resultarray=command.split(":");
			if(resultarray.length==5)
			{
			if((resultarray[0]!=null)&&(resultarray[0].length()!=0))
			{
				DetachTimer = resultarray[0];
			}
			if((resultarray[1]!=null)&&(resultarray[1].length()!=0))
			{
				loop = resultarray[1];
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
				RawData.tool.STrace("ERROR", "invalid incoming DFU message,"+command);
			}	
        }
	}
	
	public class DFDCommand extends CommandBase
	{
		public String DetachTimer;
		public String loop;
        public String Address;
        public String Usr;
        public String Password;
        public String filename;
        public DFDCommand(String command)
        {
			String[] resultarray=command.split(":");
			if(resultarray.length==6)
			{
			if((resultarray[0]!=null)&&(resultarray[0].length()!=0))
			{
				DetachTimer = resultarray[0];
			}
			if((resultarray[1]!=null)&&(resultarray[1].length()!=0))
			{
				loop = resultarray[1];
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
				RawData.tool.STrace("ERROR", "invalid incoming DFD message,"+command);
			}	
        }
	}
}