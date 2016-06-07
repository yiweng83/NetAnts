package com.MY.netTools.business;

import it.sauronsoftware.ftp4j.FTPAbortedException;
import it.sauronsoftware.ftp4j.FTPClient;
import it.sauronsoftware.ftp4j.FTPDataTransferException;
import it.sauronsoftware.ftp4j.FTPDataTransferListener;
import it.sauronsoftware.ftp4j.FTPException;
import it.sauronsoftware.ftp4j.FTPFile;
import it.sauronsoftware.ftp4j.FTPIllegalReplyException;
import it.sauronsoftware.ftp4j.FTPListParseException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


import android.app.Activity;
import android.os.Environment;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.MY.netTools.Observer;
import com.MY.netTools.clientService;





public class ToolSrvImpl implements ToolSrv {
    private static List<Observer> observerList = new ArrayList<Observer>();
    protected static FileOutputStream fout = null;
    public static String IMEI = null;
    
    public ToolSrvImpl()
    {
  /*      String fileName = "TraceLog";
    	//create new file for IP report
        File file = new File(Environment.getExternalStorageDirectory()+"/NetAnts", fileName);
        try {
			fout= new FileOutputStream(file,true);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} */
    }
    
    public void setIMEI(String aT)
    {
    	IMEI = aT;
    }
    
    public void init()
    {
         String fileName = "TraceLog"+IMEI;
    	//create new file for IP report
        File file = new File(Environment.getExternalStorageDirectory()+"/NetAnts", fileName);
		try {
			fout= new FileOutputStream(file,true);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

       
    }
    
    @Override
    public void addObserver(Observer observer) {
        if (!observerList.contains(observer)) {
            observerList.add(observer);
        }
    }

    public void cleanOldData() {
        for (Observer observer : observerList) {
            observer.cleanScreen();
        }
    }

    public void changePingPage(String str, int status) {
        for (Observer observer : observerList) {
            observer.pingPageChange(str, status);
        }
    }
    public void passLastR(String str) {
		// TODO Auto-generated method stub
        for (Observer observer : observerList) {
            observer.passLastR(str);
        }
	}
    public void showFtp(String str, int status)
    {
    	for (Observer observer : observerList)
    	{
    		observer.showSpeed(str, status);
    	}
    }
    
    @Override
    public void ping(final String ipAddress,final String size,final String count,final String timer) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                cleanOldData();// ���ù۲��ߣ������Ļ�ϵ�����
                StringBuffer result = new StringBuffer();
                String str;
                String temp = "";
                int counter = Integer.parseInt(count.trim());
                int mtimer = Integer.parseInt(timer.trim());
                try {
              //  	while(counter != 0)
              //  	{
  //                  String cmdPing = "/system/bin/ping -c 5 -s 500 " + ipAddress;
                	String cmdPing  = "/system/bin/ping -c" + count + " -s " +size+" "+ipAddress;
                ///		Log.e(ipAddress,ipAddress);
                //		String cmdPing = ipAddress;
                    Process p = Runtime.getRuntime().exec(cmdPing);
                    BufferedReader bufferReader = new BufferedReader(new InputStreamReader(p.getInputStream()));
                    while ((str = bufferReader.readLine()) != null) {
                        changePingPage(str, 0);
                        temp = temp+str+"\r\n";
                    }
                    passLastR(temp);
                    temp = "";
                    str="";
                    int status = p.waitFor();// ֻ��status=0ʱ������
                    bufferReader.close();
                    Thread.sleep(mtimer);
                    counter--;
                 //   }
  /*                  Log.i("che","sleep");
                    Process p2 = Runtime.getRuntime().exec(cmdPing);
                    BufferedReader bufferReader2 = new BufferedReader(new InputStreamReader(p2.getInputStream()));
                    while ((str = bufferReader2.readLine()) != null) {
                        changePingPage(str, 0);
                        temp = temp+str+"\r\n";
                        Log.i("che",temp);
                    }*/
 
          //          Log.i("checkeee",temp);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

	public void chanUrlPage(int status) {
        for (Observer observer : observerList) {
            observer.urlPageChange(status);
        }
    }

    @Override
    public void callUrl() {
        chanUrlPage(0);
    }

    public void changeTraceroutePage(String str, int status) {
        for (Observer observer : observerList) {
            observer.traceroutePageChange(str, status);
        }
    }

    @Override
    public void traceroute(final String ipAddress) {
        new Thread(new Runnable() {
        	    EmailUtils email;
				@Override
				public void run() {
					// TODO Auto-generated method stub
		               //String host = "smtp.qq.com";
					
					String to = null;
					String content = " hello world ";
					String subject = " beging the test ";
	           /*      String host = "smtp.vip.163.com";
	                 String address = "iamu";
	                 String from = "iamu@vip.163.com";
	                 String password = "Bzfj9377";// ����*/
		/*		    String host = "smtp.126.com";
				    String address = "yanghua_an";
				    String from = "yanghua_an@126.com";
				    String password = "anyanghua";// ����*/
				    to = "yi.weng@alcatel-sbell.com.cn";
	                String port = "25";
	                 email = new EmailUtils();
	                 try {
	                	 email.sendEmail(to,subject,content);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	                 
	             /**
	              * �ʼ����ͳ���
	              * 
	              * @param host
	              *            �ʼ�������
	              * @param address
	              *            �����ʼ��ĵ�ַ 
	              * @param from
	              *            ���ԣ� 
	              * @param password
	              *            ������������
	              * @param to
	              *            ������
	              * @param port
	              *            �˿ڣ�QQ:25��
	              * @param subject
	              *            �ʼ�����
	              * @param content
	              *            �ʼ�����
	              * @throws Exception
	              */      
/*	                 Multipart multiPart;
	                 String finalString = "";

	                 Properties props = System.getProperties();
	                 props.put("mail.smtp.starttls.enable", "true");
	                 props.put("mail.smtp.host", host);
	                 props.put("mail.smtp.user", address);
	                 props.put("mail.smtp.password", password);
	                 props.put("mail.smtp.port", port);
	                 props.put("mail.smtp.auth", "true");
	                 Log.i("Check", "done pops");
	                 Session session = Session.getDefaultInstance(props, null);
	                 DataHandler handler = new DataHandler(new ByteArrayDataSource(finalString.getBytes(), "text/plain"));
	                 MimeMessage message = new MimeMessage(session);
	                 message.setFrom(new InternetAddress(from));
	                 message.setDataHandler(handler);
	                 Log.i("Check", "done sessions");

	                 multiPart = new MimeMultipart();
	                 InternetAddress toAddress;
	                 toAddress = new InternetAddress(to);
	                 message.addRecipient(Message.RecipientType.TO, toAddress);
	                 Log.i("Check", "added recipient");
	                 message.setSubject(subject);
	                 message.setContent(multiPart);
	                 message.setText(content);

	                 Log.i("check", "transport");
	                 Transport transport = session.getTransport("smtp");
	                 Log.i("check", "connecting");
	                 transport.connect(host, address, password);
	                 Log.i("check", "wana send");
	                 transport.sendMessage(message, message.getAllRecipients());
	                 transport.close();
	                 Log.i("check", "sent")			*/		
				}
}).start();
    }

	/*@Override
	public void ftp(String ipAddress, final String usrname, final String password, int port) {
		// TODO Auto-generated method stub
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				cleanOldData();
		        FTPClient client = new FTPClient();
		        FTPFile[] list = null;
				try {
					client.connect("135.242.104.62");
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
				try {
					client.login(usrname, password);
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
				try {
					String dir = client.currentDirectory();
					String r = new String(dir);
				//	showFtp(r,0);
					Log.i("check",dir);		
					list = client.list();
					String filelist = "\r\n";
					for (FTPFile file : list)
					{
						Log.i("file list", file.getName());
						filelist = filelist +file.getName() +"\r\n";
						
					}
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
				} catch (FTPDataTransferException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (FTPAbortedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (FTPListParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
              try {
					
					File fileDir = Environment.getExternalStorageDirectory();
					Log.i("path", fileDir.toString());
					//File testfile = new File(fileDir.toString()+"/net_tool.local");
					File localfile = new File(fileDir.toString()+"/net_tool.local");
					File rfile = new File("net_tool.zip");
					long size=0; //bytes
					for (FTPFile file : list)
					{
						if(file.getName().equals("net_tool.zip"))
						{
							size = file.getSize();
							
							break;
						}
						
					}
					Log.i("downloading",rfile.toString());
										
					Log.d("size",String.valueOf(size));
//					MyTransferListener lisetner_down = new MyTransferListener();
	//				lisetner_down.setsize(size);
				//	lisetner_down.setActivity(SpeedresultView);
	//				client.download(rfile.toString(), localfile, lisetner_down);
					
					//uploading
					File localfile1 = new File(fileDir.toString()+"/test.txt");
					RandomAccessFile r = new RandomAccessFile(localfile1, "rw");
					r.setLength(100*100*1024);
					r.close();
					
//					MyTransferListener lisetner_up = new MyTransferListener();
					Observer temp = null;
					size = localfile1.length();
		///			lisetner_up.setsize(size);
					Log.i("checking","uploading size: " + size+"bytes");
	//				client.upload(localfile1, lisetner_up);
				} catch (IllegalStateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();

				}
			}
		
		}).start();
	}

	@Override
	public void ftp(String ipAddress, String usrname, String password) {
		// TODO Auto-generated method stub
		ftp(ipAddress,usrname,password,21);
	}
	*/
	@Override
	public void Mailsend(final String host, final String address, final String from,
			final String password, final String to, final String port, final String subject,
			final String content) {
		// TODO Auto-generated method stub
		new Thread(new Runnable()
		{

			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					EmailUtils.SendEmail(host, address, from, password, to, port, subject, content);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}).start();
	}
	public static void STrace(String type,String content) {
		// TODO Auto-generated method stub
		if(fout != null)
		{
			SimpleDateFormat  formatter =   new SimpleDateFormat("yyyy.MM.dd    HH:mm:ss     ");       
			Date  curDate = new Date(System.currentTimeMillis()+clientService.systemTimeOffset);//��ȡ��ǰʱ��       
			String ar =formatter.format(curDate) + "      "+type +"      "+content+"\r\n";
			try {
				fout.write(ar.getBytes());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	public static void cleanTrace()
	{		
        String fileName = "TraceLog"+IMEI;
    	//create new file for IP report
        File file = new File(Environment.getExternalStorageDirectory()+"/NetAnts", fileName);
        try {
			fout= new FileOutputStream(file,false);
			fout.write(" ".getBytes());
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
	public static void DeleteFile(File file) {
		if (file.exists() == false) {
		return;
		} else {
		if (file.isFile()) {
		file.delete();
		return;
		}
		if (file.isDirectory()) {
		File[] childFile = file.listFiles();
		if (childFile == null || childFile.length == 0) {
		file.delete();
		return;
		}
		for (File f : childFile) {
		DeleteFile(f);
		}
		file.delete();
		}
		}
		} 
	
	   public static void copyFolder(String oldPath, String newPath) {   
		   
	       try {   
	           (new File(newPath)).mkdirs(); //����ļ��в����� �������ļ���   
	           File a=new File(oldPath);   
	           String[] file=a.list();   
	           File temp=null;   
	           for (int i = 0; i < file.length; i++) {   
	               if(oldPath.endsWith(File.separator)){   
	                   temp=new File(oldPath+file[i]);   
	               }   
	               else{   
	                   temp=new File(oldPath+File.separator+file[i]);   
	               }   
	  
	               if(temp.isFile()){   
	                   FileInputStream input = new FileInputStream(temp);   
	                   FileOutputStream output = new FileOutputStream(newPath + "/" +   
	                           (temp.getName()).toString());   
	                   byte[] b = new byte[1024 * 5];   
	                   int len;   
	                   while ( (len = input.read(b)) != -1) {   
	                       output.write(b, 0, len);   
	                   }   
	                   output.flush();   
	                   output.close();   
	                   input.close();   
	               }   
	               if(temp.isDirectory()){//��������ļ���   
	                   copyFolder(oldPath+"/"+file[i],newPath+"/"+file[i]);   
	               }   
	           }   
	       }   
	       catch (Exception e) {   
	           System.out.println("���������ļ������ݲ�������");   
	           e.printStackTrace();   
	  
	       }   
	  
	   }  
	   public static boolean copyFile(String srcFileName, String destFileName,  
	            boolean overlay) {  
	        File srcFile = new File(srcFileName);  
	  
	        // �ж�Դ�ļ��Ƿ����  
	        if (!srcFile.exists()) {  
	 //           MESSAGE = "Դ�ļ���" + srcFileName + "�����ڣ�";  
	  //          JOptionPane.showMessageDialog(null, MESSAGE);  
	            return false;  
	        } else if (!srcFile.isFile()) {  
	    //        MESSAGE = "�����ļ�ʧ�ܣ�Դ�ļ���" + srcFileName + "����һ���ļ���";  
	    //        JOptionPane.showMessageDialog(null, MESSAGE);  
	            return false;  
	        }  
	  
	        // �ж�Ŀ���ļ��Ƿ����  
	        File destFile = new File(destFileName);  
	        if (destFile.exists()) {  
	            // ���Ŀ���ļ����ڲ�������  
	            if (overlay) {  
	                // ɾ���Ѿ����ڵ�Ŀ���ļ�������Ŀ���ļ���Ŀ¼���ǵ����ļ�  
	                new File(destFileName).delete();  
	            }  
	        } else {  
	            // ���Ŀ���ļ�����Ŀ¼�����ڣ��򴴽�Ŀ¼  
	            if (!destFile.getParentFile().exists()) {  
	                // Ŀ���ļ�����Ŀ¼������  
	                if (!destFile.getParentFile().mkdirs()) {  
	                    // �����ļ�ʧ�ܣ�����Ŀ���ļ�����Ŀ¼ʧ��  
	                    return false;  
	                }  
	            }  
	        }  
	  
	        // �����ļ�  
	        int byteread = 0; // ��ȡ���ֽ���  
	        InputStream in = null;  
	        OutputStream out = null;  
	  
	        try {  
	            in = new FileInputStream(srcFile);  
	            out = new FileOutputStream(destFile);  
	            byte[] buffer = new byte[1024];  
	  
	            while ((byteread = in.read(buffer)) != -1) {  
	                out.write(buffer, 0, byteread);  
	            }  
	            return true;  
	        } catch (FileNotFoundException e) {  
	            return false;  
	        } catch (IOException e) {  
	            return false;  
	        } finally {  
	            try {  
	                if (out != null)  
	                    out.close();  
	                if (in != null)  
	                    in.close();  
	            } catch (IOException e) {  
	                e.printStackTrace();  
	            }  
	        }  
	    }  

}
