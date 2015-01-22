package org.lvyouzaike.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;

import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.lvyouzaike.service.MailCommentService;
import org.lvyouzaike.service.MailService;
import org.lvyouzaike.service.impl.MailCommentServiceImpl;
import org.lvyouzaike.service.impl.MailServiceImpl;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.opensymphony.xwork2.ActionSupport;

/*
 * one methods: unsub() to unsubscribe  a given email subscription
 */

@Component("mailUnsubAction")
@Scope("prototype")
public class MailUnsubAction extends ActionSupport {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String unsubAdr;  //the given mail address
	private String formatreason;  //format reason
	private String comment;  //customized reason
	private String nickname;  //nickname for comment
		
	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getFormatreason() {
		return formatreason;
	}

	public void setFormatreason(String formatreason) {
		this.formatreason = formatreason;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getUnsubAdr() {
		return unsubAdr;
	}

	public void setUnsubAdr(String unsubAdr) {
		this.unsubAdr = unsubAdr;
	}

	public String unsub() throws IOException{
		unsubAdr = URLDecoder.decode(unsubAdr,"UTF-8");
		formatreason = URLDecoder.decode(formatreason,"UTF-8");
		comment = URLDecoder.decode(comment,"UTF-8");
		nickname = URLDecoder.decode(nickname,"UTF-8");
		
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("beans.xml");
		MailService mailService = (MailServiceImpl) applicationContext.getBean("mailService");
		MailCommentService mailCommentService = (MailCommentServiceImpl) applicationContext.getBean("mailCommentService");
		
		//System.out.println("email subscription is cancelled!");
		String flag = "";
		boolean unsubSuccess = mailService.unsubMail(unsubAdr);
		if(!unsubSuccess)
			//System.out.println("wrong email address");  //here should tell the user wrong email subscription
			flag = "no";  //the mail subscription to be unsubscribed does not exist
		else{
			flag = "yes";  //unsub success!
			boolean unsubCommentSuccess = mailCommentService.addMailComment(unsubAdr, formatreason, comment);
			if(!unsubCommentSuccess)
				System.out.println("insert mail comment error!");
		}
		
		Gson gson = new Gson();
		String result = gson.toJson(flag);
		
		HttpServletResponse resp = ServletActionContext.getResponse();
		resp.setContentType("application/json;charset=utf-8");
		resp.setHeader("Cache-Control", "no-cache");  //cancel cache when ajax works
		
		PrintWriter out = resp.getWriter();
		
		OutputFormat format = OutputFormat.createPrettyPrint();
		format.setEncoding("utf-8");
		
		XMLWriter writer = new XMLWriter(out, format);
		writer.write(result);
		
		out.flush();
		out.close();
						
		return null;
	}
	
}
