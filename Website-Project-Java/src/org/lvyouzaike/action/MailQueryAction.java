package org.lvyouzaike.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.lvyouzaike.model.Mail;
import org.lvyouzaike.model.Resort;
import org.lvyouzaike.service.MailService;
import org.lvyouzaike.service.impl.MailServiceImpl;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.opensymphony.xwork2.ActionSupport;

/*
 * one methods: query() to query a given email subscription
 */

@Component("mailQueryAction")
@Scope("prototype")
public class MailQueryAction extends ActionSupport {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String queryAdr;  //the given mail address
		
	public String getQueryAdr() {
		return queryAdr;
	}

	public void setQueryAdr(String queryAdr) {
		this.queryAdr = queryAdr;
	}

	public String query() throws IOException{
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("beans.xml");
		MailService mailService = (MailServiceImpl) applicationContext.getBean("mailService");
		
		List<String> mailInfo = new ArrayList<String>();
		Mail mail = mailService.getByAddress(queryAdr);
		
		if(null == mail){
			mailInfo.add("empty");  //if the mail subscription does not exist, return "empty".
		}else{
			mailInfo.add("" + mail.isValidOrNot());
			mailInfo.add("" + mail.isSubOrNot());
			mailInfo.add("" + mail.getSubDate());
			mailInfo.add("" + mail.getLastSendDate());			
			for(Resort r: mail.getResorts()){
				mailInfo.add(r.getName());
			}
		}		
		
		Gson gson = new Gson();
		String result = gson.toJson(mailInfo);
		
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
