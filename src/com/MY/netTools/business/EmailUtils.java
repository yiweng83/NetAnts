package com.MY.netTools.business;

import java.util.Properties;

import javax.activation.DataHandler;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;



import android.util.Log;

public class EmailUtils {
	  public static void sendEmail(String to, String subject, String content) throws Exception, MessagingException {
	        //String host = "smtp.qq.com";
	        String host = "smtp.126.com";
	        String address = "yanghua_an";
	        String from = "yanghua_an@126.com";
	        String password = "anyanghua";// ����
	        String port = "25";
	        SendEmail(host, address, from, password, to, port, subject, content);
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
	    public static void SendEmail(String host, String address, String from, String password, String to, String port, String subject, String content) throws Exception {
	        Multipart multiPart;
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
	        Log.i("check", "sent");
	    }
	}

