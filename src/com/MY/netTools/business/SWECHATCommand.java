package com.MY.netTools.business;

public class SWECHATCommand extends CommandBase {
	public String interavl;
	SWECHATCommand(String command)
	{
		String[] resultarray=command.split(":");
		if(resultarray.length==1)
    	{
			if((resultarray[0]!=null)&&(resultarray[0].length()!=0))
			{
				interavl = resultarray[0];
			}				
    	}
		else
		{
			RawData.tool.STrace("ERROR", "invalid incoming HTTP message,"+command);
		}
	}
}
