package com.MY.netTools.business;

import java.io.DataOutputStream;
import java.io.IOException;

import com.MY.netTools.runFtp;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

public class AirplaneModeService {
	
        public boolean run(Context context) throws Exception {
        boolean isEnabled = isAirplaneModeOn(context);
        
        // Toggle airplane mode.
        setSettings(context, isEnabled?0:1);
        // Post an intent to reload.
        
  //      intent.putExtra("state", !isEnabled);
//        context.sendBroadcast(intent);
        
        return true;
    }
        public boolean reset(Context context) throws Exception
        {
        	setSettings(context, 0);
			return true;
        }
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1) public static boolean isAirplaneModeOn(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return Settings.System.getInt(context.getContentResolver(), 
                    Settings.System.AIRPLANE_MODE_ON, 0) != 0;          
        } else {
            return Settings.Global.getInt(context.getContentResolver(), 
                    Settings.Global.AIRPLANE_MODE_ON, 0) != 0;
        }       
    }
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1) public static void setSettings(Context context, int value) throws Exception {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            Settings.System.putInt(
                      context.getContentResolver(),
                      Settings.System.AIRPLANE_MODE_ON, value);
            Intent intent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
            intent.putExtra("state", value);
            context.sendBroadcast(intent);
        } else {
            String command1 = "settings put global airplane_mode_on "+String.valueOf(value);
            String command2 = "am broadcast -a android.intent.action.AIRPLANE_MODE --ez state ";
            if(value == 1)
            {
            	command2 = command2+"true";
            	
            }
            else
            {
            	command2 = command2+"false";
            }
            Log.i("check 1",command1+" "+command2);
            execRootCmdSilent(command1);
            execRootCmdSilent(command2);
            
        }       
    }
    public static int execRootCmdSilent(String cmd) {
        int result = -1;
        DataOutputStream dos = null;

        try {
            Process p = Runtime.getRuntime().exec("su");
            dos = new DataOutputStream(p.getOutputStream());

            dos.writeBytes(cmd + "\n");
            dos.flush();
            dos.writeBytes("exit\n");
            dos.flush();
            p.waitFor();
            result = p.exitValue();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (dos != null) {
                try {
                    dos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }
}
