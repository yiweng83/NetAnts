package com.MY.netTools.business;

import java.util.LinkedList;
import java.util.List;

import android.util.Log;

public class HTTPCommand extends CommandBase {

	    public String httpAddress;
	    public String interavl;
	    public String reportTime;
	    
	    HTTPCommand(String command)
	    {
	    	String[] resultarray=command.split(":");
	    	if(resultarray.length==3)
	    	{
				if((resultarray[0]!=null)&&(resultarray[0].length()!=0))
				{
					httpAddress = resultarray[0];
				}
				if((resultarray[1]!=null)&&(resultarray[1].length()!=0))
				{
					interavl = resultarray[1];
				}
				if((resultarray[2]!=null)&&(resultarray[2].length()!=0))
				{
					reportTime = resultarray[2];
				}	
	    	}
			else
			{
				RawData.tool.STrace("ERROR", "invalid incoming HTTP message,"+command);
			}
	    }
}


