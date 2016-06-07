package com.MY.netTools.business;

public class OTCALLCommand extends CommandBase {

	public String loop;
    public String callingnumber;
    public String existingtime;
   
	OTCALLCommand(String command)
	{
		String[] resultarray=command.split(":");
		if(resultarray.length==3)
		{
		if((resultarray[0]!=null)&&(resultarray[0].length()!=0))
		{
			loop = resultarray[0];
		}
		if((resultarray[1]!=null)&&(resultarray[1].length()!=0))
		{
			callingnumber = resultarray[1];
		}
		if((resultarray[2]!=null)&&(resultarray[2].length()!=0))
		{
			existingtime = resultarray[2];
		}
		}
		else
		{
			RawData.tool.STrace("ERROR", "invalid incoming DFD message,"+command);
		}	
	}
}
