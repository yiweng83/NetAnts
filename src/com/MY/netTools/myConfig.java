package com.MY.netTools;

import java.util.Map;

import com.MY.pingtest.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class myConfig extends Activity {
	private Button MailConfig;
	private Button pingConfig;
	private Button FtpConfig;
	private Button AttachConfig;
	private Button AutoTestConfig;
	private Button MultiTaskConfig;
	private myConfig activity;  
    private Button showConfig;
    private TextView showAllConfig;
	private TextView usr;
	private String str = new String();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.net_tool_config);
		activity = this;
		final SharedPreferences sharedPreferences = getSharedPreferences("myConfig", MODE_PRIVATE); //配置文件
		MailConfig = (Button) findViewById(R.id.ConfigMail);
		pingConfig = (Button)findViewById(R.id.ConfigPing);
		FtpConfig = (Button)findViewById(R.id.configFtp);
		AttachConfig = (Button)findViewById(R.id.ConfigAttach);
		showConfig = (Button) findViewById(R.id.ConfigShow);
		showAllConfig = (TextView) findViewById(R.id.showAllConfig);
		AutoTestConfig = (Button)findViewById(R.id.ConfigAutotest);
		MultiTaskConfig = (Button)findViewById(R.id.ConfigMulti);
		
		MailConfig.setEnabled(false);
		usr = (TextView) findViewById(R.id.showPingConfig);
		final LinearLayout layout=new LinearLayout(this);
		layout.setOrientation(LinearLayout.VERTICAL);
		LayoutInflater flater = LayoutInflater.from(activity);
		final View view = flater.inflate(R.layout.popmailconfig, null);
		
	//	final LinearLayout layout2=new LinearLayout(this);
		layout.setOrientation(LinearLayout.VERTICAL);
		LayoutInflater flater2 = LayoutInflater.from(activity);
		final View view2 = flater2.inflate(R.layout.poppingconfig, null);
		
		
//		final LinearLayout layout3=new LinearLayout(this);
		layout.setOrientation(LinearLayout.VERTICAL);
		LayoutInflater flater3 = LayoutInflater.from(activity);
		final View view3 = flater3.inflate(R.layout.popftpconfig, null);
		
//		final LinearLayout layout4=new LinearLayout(this);
		layout.setOrientation(LinearLayout.VERTICAL);
		LayoutInflater flater4 = LayoutInflater.from(activity);
		final View view4 = flater4.inflate(R.layout.popattachconfig, null);
		
//		final LinearLayout layout5=new LinearLayout(this);
		layout.setOrientation(LinearLayout.VERTICAL);
		LayoutInflater flater5 = LayoutInflater.from(activity);
		final View view5 = flater5.inflate(R.layout.popautotestconfig, null);
		
		layout.setOrientation(LinearLayout.VERTICAL);
		LayoutInflater flater6 = LayoutInflater.from(activity);
		final View view6 = flater5.inflate(R.layout.popmultitaskconfig, null);
		
//		layout.addView(view);   
		//========= for mail
        final EditText mUserName = (EditText)view.findViewById(R.id.edit_username);  
        final EditText mPassword = (EditText)view.findViewById(R.id.edit_password); 
        final EditText Mailto = (EditText) view.findViewById(R.id.defaultTo);
        final EditText MailFrom = (EditText) view.findViewById(R.id.defaulMailFrom);
        final EditText MailSMTP = (EditText) view.findViewById(R.id.defaultMailSMTP);
        final EditText Mailport = (EditText) view.findViewById(R.id.defaultPort);
    //    final TextView Pass =  (TextView) view.findViewById(R.id.text_password);
        //========for ping
        final EditText mPingPackageSize = (EditText)view2.findViewById(R.id.PingPackageSize);
        final EditText mPingCount = (EditText)view2.findViewById(R.id.PingCount);
//        final EditText mPingTimer = (EditText)view2.findViewById(R.id.PingTimer);
        final EditText mPingAddress = (EditText)view2.findViewById(R.id.PingDefaultAddress);
        final EditText mUrlAddress = (EditText) view2.findViewById(R.id.UrlDefaultAddress);
        
        
        
        //========for FTP
 //       final EditText FtpThread = (EditText)view3.findViewById(R.id.FtpThread);
 //       final EditText FtpKilling = (EditText)view3.findViewById(R.id.FtpKilling);
        final EditText FtpFileName = (EditText)view3.findViewById(R.id.FtpFileName);
        final EditText FtpLoopNum = (EditText) view3.findViewById(R.id.FtpLoopNum);
        final EditText FtpLoopTime = (EditText)view3.findViewById(R.id.FtpLoopTime);
        //========for Attach/detach
        final EditText modeChangeDurationTimer = (EditText) view4.findViewById(R.id.modeDurationTimer);
        final EditText modeChangeTime = (EditText) view4.findViewById(R.id.modeChangeTime);
        
        
        final CheckBox DisableAutoTest = (CheckBox)view5.findViewById(R.id.DisableAutoTest);
        final CheckBox DisableUEScreenTrace = (CheckBox) view5.findViewById(R.id.DisableUEScreenTrace);
        final EditText ServierAddress  = (EditText)view5.findViewById(R.id.ServerAddress);
       // for MultiTask 
        final EditText FtpDlThread = (EditText)view6.findViewById(R.id.MultiDLThread);
        final EditText FtpUlThread = (EditText) view6.findViewById(R.id.MultiULThread);
        final EditText PingThread = (EditText)view6.findViewById(R.id.MultiPingThread);
        
		String name1 =sharedPreferences.getString("name", "");
		String habi1t =sharedPreferences.getString("password", "");
		String s1 =sharedPreferences.getString("Mailto", "");
		String d1 =sharedPreferences.getString("MailFrom", "");
		String f1 =sharedPreferences.getString("MailSMTP", "");
		String g1 =sharedPreferences.getString("Mailport", "");
		
		mUserName.setText(name1);
		mPassword.setText(habi1t);
		Mailto.setText(s1);
		MailFrom.setText(d1);
		MailSMTP.setText(f1);
		Mailport.setText(g1);
		
		mPingPackageSize.setText(sharedPreferences.getString("mPingPackageSize",""));
		mPingCount.setText(sharedPreferences.getString("mPingCount",""));
//		mPingTimer.setText(sharedPreferences.getString("mPingTimer",""));
		mPingAddress.setText(sharedPreferences.getString("mPingAddress",""));
		mUrlAddress.setText(sharedPreferences.getString("mUrlAddress",""));
		
		
//		FtpThread.setText(sharedPreferences.getString("FtpThread",""));
//		FtpKilling.setText(sharedPreferences.getString("FtpKilling",""));
		FtpFileName.setText(sharedPreferences.getString("FtpFileName",""));
		FtpLoopNum.setText(sharedPreferences.getString("FtpLoopNum",""));
		FtpLoopTime.setText(sharedPreferences.getString("FtpLoopTime",""));
		
		
		FtpDlThread.setText(sharedPreferences.getString("FtpDlThread",""));
		FtpUlThread.setText(sharedPreferences.getString("FtpUlThread",""));
		PingThread.setText(sharedPreferences.getString("PingThread",""));

		modeChangeDurationTimer.setText(sharedPreferences.getString("modeChangeDurationTimer",""));
		modeChangeTime.setText(sharedPreferences.getString("modeChangeTime",""));
		
		if(sharedPreferences.getString("DisableAutoTest","").equals("Yes"))
		{
			DisableAutoTest.setChecked(true);
		}
		if(sharedPreferences.getString("DisableUEScreenTrace","").equals("Yes"))
		{
			DisableUEScreenTrace.setChecked(true);
		}
		ServierAddress.setText(sharedPreferences.getString("ServierAddress",""));
		
		
		MailConfig.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (view.getParent() != null) { ((ViewGroup)view.getParent()).removeView(view);} //防止重复打开造成view重复create，引起crash
				new AlertDialog.Builder(activity).setTitle("mail") 
				 .setView(view) 
				 .setPositiveButton("Yes",new DialogInterface.OnClickListener()
				 {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub 
						str = mUserName.getText().toString();
						SharedPreferences.Editor editor = sharedPreferences.edit(); 
						editor.putString("name", str); 
						str = mPassword.getText().toString();
						editor.putString("password",str);
						str = Mailto.getText().toString();
						editor.putString("Mailto",str);
						str = MailFrom.getText().toString();
						editor.putString("MailFrom",str);
						str = MailSMTP.getText().toString();
						editor.putString("MailSMTP",str);
						str = Mailport.getText().toString();
						editor.putString("Mailport",str);
						editor.commit(); 
						usr.setText(mUserName.getText().toString());
			///			Pass.setText(mPassword.getText().toString());
						
					}
				 })
				 .setNeutralButton("Cancel", null)  
				 .show();
			}	
		});
		
		pingConfig.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (view2.getParent() != null) { ((ViewGroup)view2.getParent()).removeView(view2);} //防止重复打开造成view重复create，引起crash
				new AlertDialog.Builder(activity).setTitle("Ping")
				.setView(view2)
				.setPositiveButton("Yes",new DialogInterface.OnClickListener()
				{

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						SharedPreferences.Editor editor = sharedPreferences.edit(); 
						str = mPingPackageSize.getText().toString();
						editor.putString("mPingPackageSize",str);
						str = mPingCount.getText().toString();
						editor.putString("mPingCount",str);
//						str = mPingTimer.getText().toString();
						editor.putString("mPingTimer","1000");
						str = mPingAddress.getText().toString();
						editor.putString("mPingAddress",str);
						str = mUrlAddress.getText().toString();
						editor.putString("mUrlAddress",str);
						editor.commit(); 
					}
					
				})
				 .setNeutralButton("Cancel", null)  
				 .show();
			}
			
		});
		
		FtpConfig.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (view3.getParent() != null) { ((ViewGroup)view3.getParent()).removeView(view3);} //防止重复打开造成view重复create，引起crash
				new AlertDialog.Builder(activity).setTitle("Ftp")
				.setView(view3)
				.setPositiveButton("Yes",new DialogInterface.OnClickListener()
				{

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						SharedPreferences.Editor editor = sharedPreferences.edit(); 
//						str = FtpThread.getText().toString();
//						editor.putString("FtpThread",str);
//						str = FtpKilling.getText().toString();
//						editor.putString("FtpKilling",str);
						str = FtpFileName.getText().toString();
						editor.putString("FtpFileName",str);
						str = FtpLoopNum.getText().toString();
						editor.putString("FtpLoopNum",str);
						str = FtpLoopTime.getText().toString();
						editor.putString("FtpLoopTime",str);
						
						editor.commit(); 
					}
					
				})
				 .setNeutralButton("Cancel", null)  
				 .show();
			}
			
		});		
		
		
		AttachConfig.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (view4.getParent() != null) { ((ViewGroup)view4.getParent()).removeView(view4);} //防止重复打开造成view重复create，引起crash
				new AlertDialog.Builder(activity).setTitle("Attach")
				.setView(view4)
				.setPositiveButton("Yes",new DialogInterface.OnClickListener()
				{

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						SharedPreferences.Editor editor = sharedPreferences.edit(); 
						str = modeChangeDurationTimer.getText().toString();
						editor.putString("modeChangeDurationTimer",str);
						str = modeChangeTime.getText().toString();
						editor.putString("modeChangeTime",str);
						editor.commit(); 
					}
					
				})
				 .setNeutralButton("Cancel", null)  
				 .show();
			}
			
		});
		
		showConfig.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showAllConfig.setText("");
			        SharedPreferences outsharedPreferences= getSharedPreferences("myConfig",
						Activity.MODE_PRIVATE); 
		/*	        Map<String, ?> allMap = outsharedPreferences.getAll();
			        for(Object o : allMap.keySet())
			        {
			        	allMap.get(o);
			        	String name = o.toString();
			        	String value = outsharedPreferences.getString(o.toString(), "");
			        	showAllConfig.append("name : "+name+" value : "+value+"\r\n");
			        }
			        */
			      
			      String PingConfig = "Ping Configration :"+"\r\n"+"Ping Package Size: "+sharedPreferences.getString("mPingPackageSize","")
			    		  +"\r\n"+"Ping Count: "+sharedPreferences.getString("mPingCount","")+"\r\n"+"Ping Timer: "+sharedPreferences.getString("mPingTimer","")
			    		  +"\r\n"+"Ping Address: "+sharedPreferences.getString("mPingAddress","")+"\r\n"+"Url Address: "+sharedPreferences.getString("mUrlAddress","")+"\r\n"+"\r\n";
			      showAllConfig.append(PingConfig);
			      
			      String FtpConfig = "Ftp Configration :"+"\r\n"+"Ftp Address: "+sharedPreferences.getString("address", "")+"\r\n"+"Ftp User name: "+
			    		  sharedPreferences.getString("user", "") +"\r\n"+"Ftp Password: "+sharedPreferences.getString("password","")+"\r\n"
			    		  +"Ftp Down/Up Load Thread: "+sharedPreferences.getString("FtpThread","")+"\r\n"+"Ftp killing Thread per click: "+
			    		  sharedPreferences.getString("FtpKilling","")+"\r\n"+"Ftp DownLoad file: "+sharedPreferences.getString("FtpFileName","")+"\r\n"+
			    		  "Ftp Loop Number: "+sharedPreferences.getString("FtpLoopNum","")+"\r\n"+"Ftp Loop Time(second): "+sharedPreferences.getString("FtpLoopTime","")+"\r\n"+"\r\n";
			      showAllConfig.append(FtpConfig);
			      
			      String attachConfig="Attach/Detach Configration : "+"\r\n"+"Attach/Detach Change Duration Timer: "+sharedPreferences.getString("modeChangeDurationTimer","")+"\r\n"
			    		  +"Attach/Detach Change Times: "+sharedPreferences.getString("modeChangeTime","")+"\r\n"+"\r\n";
			      showAllConfig.append(attachConfig);
			      
			      String AutoTestConfig="AutoTest Configration : "+"\r\n"+"AutoTest Disable :  "+sharedPreferences.getString("DisableAutoTest","")+"\r\n"+"AutoTest Disable screen Trace:  "+sharedPreferences.getString("DisableUEScreenTrace","")+"\r\n"
			    		  +"ServierAddress: "+sharedPreferences.getString("ServierAddress","")+"\r\n"+"\r\n";
			      showAllConfig.append(AutoTestConfig);
			      
			      String MulTiTask="MulTiTask Configration : "+"\r\n"+"FtpDlThread  :  "+sharedPreferences.getString("FtpDlThread","")+"\r\n"+"FtpUlThread:  "+sharedPreferences.getString("FtpUlThread","")+"\r\n"
			    		  +"PingThread: "+sharedPreferences.getString("PingThread","")+"\r\n"+"\r\n";
			}
			
		});
		AutoTestConfig.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (view5.getParent() != null) { ((ViewGroup)view5.getParent()).removeView(view5);} //防止重复打开造成view重复create，引起crash
				new AlertDialog.Builder(activity).setTitle("Attach")
				.setView(view5)
				.setPositiveButton("Yes",new DialogInterface.OnClickListener()
				{

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						SharedPreferences.Editor editor = sharedPreferences.edit(); 						
						if(DisableAutoTest.isChecked())
						{
						editor.putString("DisableAutoTest","Yes");
						}
						else
						{
							editor.putString("DisableAutoTest","No");
						}
						if(DisableUEScreenTrace.isChecked())
						{
							editor.putString("DisableUEScreenTrace","Yes");
						}
						else
						{
							editor.putString("DisableUEScreenTrace","No");
						}
						str = ServierAddress.getText().toString();
						editor.putString("ServierAddress",str);
						editor.commit(); 
					}
				})
				 .setNeutralButton("Cancel", null)  
				 .show();
			}
			
		});
		MultiTaskConfig.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (view6.getParent() != null) { ((ViewGroup)view6.getParent()).removeView(view6);} //防止重复打开造成view重复create，引起crash
				new AlertDialog.Builder(activity).setTitle("Attach")
				.setView(view6)
				.setPositiveButton("Yes",new DialogInterface.OnClickListener()
				{

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						SharedPreferences.Editor editor = sharedPreferences.edit(); 
//						str = FtpThread.getText().toString();
//						editor.putString("FtpThread",str);
//						str = FtpKilling.getText().toString();
//						editor.putString("FtpKilling",str);
						str = FtpDlThread.getText().toString();
						editor.putString("FtpDlThread",str);
						str = FtpUlThread.getText().toString();
						editor.putString("FtpUlThread",str);
						str = PingThread.getText().toString();
						editor.putString("PingThread",str);
						
						editor.commit(); 
					}
					
				}
				)
				.setNeutralButton("Cancel", null)  
				.show();
			}
			
		});
	}
	

}
