package org.lvyouzaike.util;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;
import java.util.Set;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
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

//send timely email to those subscriptions
public class SendMail {
	private String bodyText;           //body text of the email
	Properties prop;                   //configuration properties for the session
	Session session;                   //session object
	 
	
	public SendMail(String bodyText, String filename) throws FileNotFoundException, IOException{
		this.bodyText = bodyText;
		prop = new Properties();
		prop.load(new BufferedInputStream(new FileInputStream(filename)));
	}
	
	public void prepare(final String password) throws FileNotFoundException, IOException{
		//load all the parameters and put session done
		session = Session.getInstance(prop,
				new javax.mail.Authenticator() {
			protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(prop.getProperty("username"), password);
			}
		});
	}
	
	public void send(String to, HashMap<String, String> pics) throws UnsupportedEncodingException, MessagingException{
		MimeMessage message = new MimeMessage(session);
		message.setFrom(new InternetAddress(prop.getProperty("from")));
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
        message.setSubject(MimeUtility.encodeText(prop.getProperty("subject"), "UTF-8", "Q"), "UTF-8");
        message.setSentDate(new Date());
                
        //first part (html)
        Multipart multiPart = new MimeMultipart();
        BodyPart bodyPart = new MimeBodyPart();
        
        bodyPart.setContent(bodyText, "text/html;charset=utf-8");
        multiPart.addBodyPart(bodyPart);
        
        //pics
        DataSource fds;
        Set<String> picIds = pics.keySet();
        for(String id: picIds){
        	bodyPart = new MimeBodyPart();
        	//System.out.println("location: " + pics.get(id));
            fds = new FileDataSource(pics.get(id));
            bodyPart.setDataHandler(new DataHandler(fds));
            bodyPart.setHeader("Content-ID",id);

            // add it
            multiPart.addBodyPart(bodyPart);
        }
        //put everything together
        message.setContent(multiPart);
        
        Transport.send(message);

		System.out.println("Done");
	}
}
