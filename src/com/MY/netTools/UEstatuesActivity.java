package com.MY.netTools;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.MY.netTools.business.RawData;
import com.MY.pingtest.R;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.telephony.CellIdentityGsm;
import android.telephony.CellIdentityLte;
import android.telephony.CellInfo;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellLocation;
import android.telephony.CellSignalStrength;
import android.telephony.CellSignalStrengthLte;
import android.telephony.NeighboringCellInfo;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class UEstatuesActivity extends Activity {
	
	protected static FileOutputStream fout = null;
	
	private Button btnClean;
	private Button btnUeFlash;
	TextView show;
	String fileName = "UEstautsreport";
	private  TelephonyManager tm;
	File file = new File(Environment.getExternalStorageDirectory()+"/NetAnts", fileName);
	
//	String SignalStrengths = " ";
	public void setStatusChange(String data)
	{
		RawData.SignalStrengths = data;
	}
	
	public static String getStatusChange()
	{
		return RawData.SignalStrengths.toString()+" ";
	}
	public BroadcastReceiver mReceiver1 = new BroadcastReceiver()
	{

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			if(action.equals("SignalStrengthsChanged"))
			{
				
            	String result = intent.getStringExtra("data"); 
       //      	Log.e("SignalStrengthsChanged",result);
            	setStatusChange(result);
       //     	Log.e("SignalStrengthsChanged1",getStatusChange());
      //      	Log.e("SignalStrengthsChanged",getStatusChange());	
            	show.setText(getStatus(tm));
			}
		
		}
		
	};
	
	
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1) @Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.uestatus);
		btnClean = (Button) findViewById(R.id.cleanuestatus);
		btnUeFlash = (Button) findViewById(R.id.ueFlash);
		tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		
		
        IntentFilter mFilter1 = new IntentFilter();
        mFilter1.addAction("SignalStrengthsChanged");
        registerReceiver(mReceiver1, mFilter1);
        
		show = (TextView) findViewById(R.id.showUEstatus);
	       try {
	        //  	fout =openFileOutput(fileName , MODE_APPEND);//不覆盖
	    	   fout= new FileOutputStream(file,true);
	        //   	fout =openFileOutput(fileName,MODE_PRIVATE);
	   		} catch (FileNotFoundException e) {
	   			// TODO Auto-generated catch block
	   			e.printStackTrace();
	   		}  
		// TODO Auto-generated method stub

	    show.setText(getStatus(tm));
       
	    btnClean.setOnClickListener(new OnClickListener()
        {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// TODO Auto-generated method stub
			      try {
				//	fout =openFileOutput("UEstautsreport" , MODE_PRIVATE);
					 fout= new FileOutputStream(file,false);
					 
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	
			}		       	
        });
        btnUeFlash.setOnClickListener(new OnClickListener()
        {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				show.setText(getStatus(tm));
			}
        	
        });
	}
	
	
	

	
	
	public static String getStatus(TelephonyManager tmm)
	{
		SimpleDateFormat  formatter =   new SimpleDateFormat("yyyy.MM.dd    HH:mm:ss     ");
		Date  curDate = new Date(System.currentTimeMillis());//获取当前时间       
		String str = "new IP report start time : "+formatter.format(curDate) + "\r\n";   	
		try {
			fout.write(str.getBytes());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// String ff= " 65535]";
	   //  String st = ff.replace("]","");
	  //   String s = st.replace(" ", "");
	 //    int kf= Integer.parseInt(s) / 256;
    //     Log.e("ffff",s);
    ///      Log.e("ffff","k "+kf);
		
		// 1 get UE IMEI
		String IMEI = tmm.getDeviceId();
		
		String IMSI = tmm.getSubscriberId();
// 2 get network type
		int netType = tmm.getNetworkType();
// 3 get IP
	    String IP = MainActivity.getPsdnIp();
//4 
	    String s1 = " ";
	    String s2 = " ";
	    int i4 = 2147483647;
	    
	    
		List<CellInfo> cellInfoList = tmm.getAllCellInfo();
       if((cellInfoList != null)&&(!(cellInfoList.isEmpty())))
        {
        for(CellInfo info:cellInfoList){
            //获取邻居小区号
            if (info instanceof CellInfoLte)
            {
                // cast to CellInfoLte and call all the CellInfoLte methods you need
            	CellIdentityLte lte_info = ((CellInfoLte) info).getCellIdentity();

                int i = lte_info.getMcc();
                int j = lte_info.getMnc();
                int k = lte_info.getTac();
                int m = lte_info.getPci();
                int n = lte_info.getCi();
                int i1 = n / 256;
                int i2 = n % 256;
                s1 = "LTEID:" + i + "-" + j + " LTETac:" + k + " LTEPci:" + m +" Cell Id "+n +" eNodeB:" + i1 + " CIinSite:" + i2 +"\n";
           // 	int a = lte_info.getTac();
           // 	int b = lte_info.getPci();
           // 	int c = lte_info.getCi();
           // 	int d = lte_info.getPci();
            //	 String s = "Tac is "+String.valueOf(a)+"Pci is "+String.valueOf(b)+"Ci is "+String.valueOf(c)+"Pci is "+String.valueOf(d);
         //   	s1 = lte_info.toString();
                CellSignalStrengthLte Lte_strength = ((CellInfoLte) info).getCellSignalStrength();
            	
                int i3 = Lte_strength.getAsuLevel();
                i4 = Lte_strength.getDbm();
                int i5 = Lte_strength.getTimingAdvance();
           //     i4 = 2147483647;
                if(i4 != 2147483647)
                {
                s1 = s1 + "\n" + "LTEsig AsuLevel=" + i3 + " dbm=" + i4 + "  TA=" + i5+"\n";
                }
            //	int e = Lte_strength.getAsuLevel();
            ///	int f = Lte_strength.getLevel();
          //  	s1 = s1+Lte_strength.toString();
   //         	 Toast.makeText(UEstatuesActivity.this, s1, Toast.LENGTH_LONG).show();
            }
   //         if(info instanceof CellInfoGsm)
    //       {
    //        	CellIdentityGsm Gsm_info = ((CellInfoGsm) info).getCellIdentity();
    //        	s2 = Gsm_info.toString();
    //        	CellSignalStrength Gsm_strength = ((CellInfoGsm) info).getCellSignalStrength();
    //        	s2 = s2+Gsm_strength.toString();
    //        	 Toast.makeText(UEstatuesActivity.this, s2, Toast.LENGTH_LONG).show();
    //        }
        }//end of for	
        }
        else
        {
        	s1 = "call getAllCellInfo fail ";
        	
       // 	GsmCellLocation cellLoca = (GsmCellLocation) tm.getCellLocation();
        	CellLocation cellLoca = tmm.getCellLocation();
        	 if(cellLoca!=null)
        	{
        //		 String[] raw = cellLoca.toString().split(",");
        //		 int k = Integer.parseInt(raw[1]) / 256;
        //		 int m = Integer.parseInt(raw[1]) % 256;
        //		 s1 = s1 + "\n" + cellLoca.toString() + " eNodeB:" + k + " Cell:" + m;
        	//	 if (cellLoca instanceof GsmCellLocation)
        	//	 {
        	 //      	 int i = ((GsmCellLocation) cellLoca).getLac();
        	 //       	 int j = ((GsmCellLocation) cellLoca).getCid();
        	 //       		 int k = j / 256;
        	 //       		 int m = j % 256;
        	   //     		 s1 = s1 + "\n" + "eNodeB:" + k + " Cell:" + m;
        	        //	 s1 = s1 + " Cid@cellid "+cellLoca.toString();
        		// }
        	///	 int i = cellLoca.getLac();
        	///	 int j = cellLoca.getCid();
        	//	 else
        	//	 {
        			 
            		 String[] raw = cellLoca.toString().split(",");
            		 if(raw.length>4)
            		 {
    //        			 raw[4] = "65535]";
            	     String st = raw[4].replace("]","");
            	     String s = st.replace(" ", "");
   //         	     Log.e("ffff",s);
            		 int k = Integer.parseInt(s) / 256;
            		 int m = Integer.parseInt(s) % 256;
            		 s1 = s1 + " "+cellLoca.toString()+" " + "eNodeB:" + k + " Cell:" + m;
            		 }
            		 else
            		 {
            	       int k = Integer.parseInt(raw[1]) / 256;
            	       int m = Integer.parseInt(raw[1]) % 256;
            	       s1 = s1 +": " + cellLoca.toString()+"eNodeB:" + k + " Cell:" + m;
            		 }
            		 
        		//	 String s = "1,65535,1,2";
        		//	 String s = cellLoca.toString();
            	//	 String[] raw = s.split(",");
      //  		//	 String[] raw = s.split(",");
            	//	 int k = Integer.parseInt(raw[1]) / 256;
            	//	 int m = Integer.parseInt(raw[1]) % 256;
            	//	 s1 = s1 +"\n" + "eNodeB:" + k + " Cell:" + m;
        		// }

        //	 s1 = s1 + " Cid@cellid "+cellLoca.toString();
        	}
        	List<NeighboringCellInfo> list = tmm.getNeighboringCellInfo();

        	if((list!=null)&&(!(list.isEmpty()))) {
        	            for (NeighboringCellInfo info : list) {
        	                int cid = info.getCid();
        	                // 获取邻居小区LAC，LAC:
        	                // 位置区域码。为了确定移动台的位置，每个GSM/PLMN的覆盖区都被划分成许多位置区，LAC则用于标识不同的位置区。
        	                int lac = info.getLac();            
        	                // 获取邻居小区信号强度
        	                int ss = -131 + 2 * info.getRssi();
        	                s1 = s1+info.toString();
        	            }
        	 }
       // 	else
       // 	{
       // 		s1 = s1+"\r\n"+" "+getStatusChange();
      //  	}
        }
        if(i4 == 2147483647)
        {
        s1 = s1+"\r\n"+" "+getStatusChange();
        }
        String report = " UE Statues: \r\n IMEI : "+IMEI+"\r\n"+" IMSI :"+IMSI+"\r\n"
        		+" netType : "+netType+"\r\n"+" IP : "+IP+"\r\n"+" UE Model :"+Build.MODEL+"\r\n"+" Status: "+s1+"\r\n"+" "+s2+"\r\n"+"\r\n";
 //       show.append(report);
  //      Toast.makeText(UEstatuesActivity.this, report,Toast.LENGTH_LONG).show();
        try {
			fout.write(report.getBytes());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return report;
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		unregisterReceiver(mReceiver1);
		super.onDestroy();
	
	}

}
