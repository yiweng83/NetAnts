package com.MY.netTools;

import it.sauronsoftware.ftp4j.FTPClient;
import it.sauronsoftware.ftp4j.FTPException;
import it.sauronsoftware.ftp4j.FTPFile;
import it.sauronsoftware.ftp4j.FTPIllegalReplyException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.MY.netTools.business.RawData;
import com.MY.pingtest.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class FtpLogin extends Activity{
	private EditText edittext_address;
	private EditText edittext_user;
	private EditText edittext_password;
	private Button button_login;
	private CheckBox saveuserinfo;
	private CheckBox usingnewfile;
	private CheckBox disablemomentaryrate;
	private String address;
	private String user;
	private String password;
	private static final String SAVESTRING_INFOS = "myConfig";
	private FTPClient client;
	String fileName = "FTPreport";
	File file = new File(Environment.getExternalStorageDirectory()+"/NetAnts", fileName);
	
	public  RawData app;
	protected static FileOutputStream fout = null;
    @SuppressLint("NewApi") @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ftplogin);
        StrictMode.ThreadPolicy policy=new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        findviews();
    }
    
    private void findviews() {
		// TODO Auto-generated method stub
		Context ctx = FtpLogin.this;
		SharedPreferences settings1 = ctx.getSharedPreferences(SAVESTRING_INFOS, 0);
		address = settings1.getString("address", "");
		user = settings1.getString("user", "");
		password = settings1.getString("password", "");
		edittext_address=(EditText)findViewById(R.id.editText_address);
		edittext_user=(EditText)findViewById(R.id.editText_user);
		edittext_password=(EditText)findViewById(R.id.editText_password);
		button_login=(Button)findViewById(R.id.button_login);
		saveuserinfo=(CheckBox)findViewById(R.id.checkBox_saveuserinfo);
		usingnewfile =(CheckBox)findViewById(R.id.checkBox_cleanrecord);
		disablemomentaryrate = (CheckBox) findViewById(R.id.checkBox_disablemomentaryrate);
		edittext_address.setText(address);
		edittext_user.setText(user);
		edittext_password.setText(password);
				
		button_login.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
			if(isOpenNetwork()){
				Intent myintent=new Intent(FtpLogin.this,runFtp.class);
				address=edittext_address.getText().toString().trim();
				user=edittext_user.getText().toString().trim();
				password=edittext_password.getText().toString().trim();
				String[] keys={"address","user","password"};
				String[] values={address,user,password};
				if(saveuserinfo.isChecked()){
				saveinfos(keys,values);}
				if(usingnewfile.isChecked())
				{				 
				      try {
				    	fout= new FileOutputStream(file,false);
					//	fout =openFileOutput("FTPreport" , MODE_PRIVATE);
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}	
				}
				if(disablemomentaryrate.isChecked())
				{
					app = (RawData)getApplication();
				    app.setisenablemomentaryrate(false);
				}
				else
				{
					app = (RawData)getApplication();
				    app.setisenablemomentaryrate(true);
				}
				myintent.putExtra("address", address);
				myintent.putExtra("user", user);
				myintent.putExtra("password", password);
				if(saveuserinfo.isChecked()){
				saveinfos(keys,values);}
				client = new FTPClient();
                boolean isconnect = true;
                String errorlog="";
			    try {
					client.connect(address);
					client.login(user,password);
				} catch (IllegalStateException e) {
					// TODO Auto-generated catch block
					isconnect = false;
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					isconnect = false;
					e.printStackTrace();
				} catch (FTPIllegalReplyException e) {
					// TODO Auto-generated catch block
					isconnect = false;
					e.printStackTrace();
				} catch (FTPException e) {
					// TODO Auto-generated catch block
					isconnect = false;
					e.printStackTrace();
				}
			    if(isconnect)
			    {
			    try {
					client.disconnect(true);
				} catch (IllegalStateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (FTPIllegalReplyException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (FTPException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				startActivity(myintent);
				finish();
			    }
			    else
			    {
			     Toast.makeText(FtpLogin.this, "Can not connect the Ftp server,please check the configration or Ftp server", Toast.LENGTH_LONG).show();
			    }
				}
			else{
				Toast.makeText(FtpLogin.this, "Please enable network access", Toast.LENGTH_SHORT).show();
			}		
			}
		});
	}

    private void saveinfos(String[] key,String[] save_str)
	{
	      Context ctx = FtpLogin.this;
	      SharedPreferences settings = ctx.getSharedPreferences(SAVESTRING_INFOS,MODE_PRIVATE); 
	      SharedPreferences.Editor editor = settings.edit();
	      editor.putString(key[0] , save_str[0]);
	      editor.putString(key[1] , save_str[1]);
	      editor.putString(key[2] , save_str[2]);
	      editor.commit();
	 }
    
    private boolean isOpenNetwork() {  
	    ConnectivityManager connManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);  
	    if(connManager.getActiveNetworkInfo() != null) {  
	        return connManager.getActiveNetworkInfo().isAvailable();  
	    }  
	    return false;  
	}   
}
