package com.MY.netTools.business;

import android.app.Activity;
import android.widget.TextView;

import com.MY.netTools.Observer;

public interface ToolSrv {
	
//	public void STrace(String type,String content);
	
	public void addObserver(Observer observer);

	public void ping(String ipAddress,String size,String count,String timer);

	public void callUrl();

	public void traceroute(String ipAddress);
	
	public void Mailsend(String host, String address, String from, String password, String to, String port, String subject, String content );
	
//	public void ftp(String ipAddress,String usrname,String password,int port);
//	public void ftp(String ipAddress,String usrname,String password);
}
