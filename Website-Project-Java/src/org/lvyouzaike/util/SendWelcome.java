package org.lvyouzaike.util;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Properties;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

public class SendWelcome {
	private static final String PASSWORD = "sh$77$1919";  //fixed password for lyzk.org@gmail.com account
	private String bodyText;           //body text of the welcome email
	Properties prop;                   //configuration properties for the session
	Session session;                   //session object
		
	public SendWelcome(String bodyText, String filename) throws FileNotFoundException, IOException{
		this.bodyText = bodyText;
		prop = new Properties();
		prop.load(new BufferedInputStream(new FileInputStream(filename)));
	}
	
	public void prepare() throws FileNotFoundException, IOException{
		//load all the parameters and put session done
		session = Session.getInstance(prop,
				new javax.mail.Authenticator() {
			protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(prop.getProperty("username"), PASSWORD);
			}
		});
	}
	
	public void send(String to) throws UnsupportedEncodingException, MessagingException{
		MimeMessage message = new MimeMessage(session);
		message.setFrom(new InternetAddress(prop.getProperty("from")));
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
        message.setSubject(MimeUtility.encodeText(prop.getProperty("subject"), "UTF-8", "Q"), "UTF-8");
        message.setSentDate(new Date());
                
        //body part
        Multipart multiPart = new MimeMultipart();
        BodyPart bodyPart = new MimeBodyPart();
        
        bodyPart.setContent(bodyText, "text/html;charset=utf-8");
        multiPart.addBodyPart(bodyPart);

        message.setContent(multiPart);
        
        Transport.send(message);

		System.out.println("Welcome Done");
	}
}
