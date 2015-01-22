package org.lvyouzaike.action;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.Date;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.lvyouzaike.service.MailService;
import org.lvyouzaike.util.ProcessWelcome;
import org.lvyouzaike.util.SendWelcome;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.opensymphony.xwork2.ActionSupport;

import freemarker.template.TemplateException;

/*
 * respond to the submit request from mail-subscription page when the user wants to subscribe mail service
 * Query method: qAdr for mail address; qSendDay for last send date; qSubDay for subscription date; qSub for customized status;
 *               qValid for current subscription status; qResorts for the list of resort names the mail subscribes (not null if qSub is true). 
 */

@Component("mailAddAction")
@Scope("prototype")
public class MailAddAction extends ActionSupport implements ServletRequestAware{
	private static final long serialVersionUID = 1L;
	private static final String CONFIGPROP = "templates/properties/config.properties";
	private static final String FTLPATH = "templates/ftl";
	public HttpServletRequest request;
	private MailService mailService;
		
	private String subAdr;                          //subscription email
	private String subhidden;                       //actually passes the real choices of the user when user submits subscription request
			
	public String getSubhidden() {
		return subhidden;
	}

	public void setSubhidden(String subhidden) {
		this.subhidden = subhidden;
	}
	
	public String getSubAdr() {
		return subAdr;
	}

	public void setSubAdr(String subAdr) {
		this.subAdr = subAdr;
	}

	public MailService getMailService() {
		return mailService;
	}

	@Resource
	public void setMailService(MailService mailService) {
		this.mailService = mailService;
	}	
	
	public String add() throws FileNotFoundException, IOException, TemplateException, MessagingException{
		subhidden = URLDecoder.decode(subhidden,"UTF-8");
		//if no custom content is chosen, the returned subhidden is ""
		mailService.addMailSub(subAdr, subhidden);
		
		//even the notification mail sending fails, still store the mail subscription into db.
		ServletContext sc = request.getServletContext();
		ProcessWelcome pw = new ProcessWelcome(sc.getRealPath(FTLPATH));
		pw.buildDataModel(new Date(), subAdr, subhidden);
		String bodyText = pw.produce();
		pw.closeAll();
		System.out.println(bodyText);
		
		SendWelcome sw = new SendWelcome(bodyText, sc.getRealPath(CONFIGPROP));
		sw.prepare();
		sw.send(subAdr);		
		
		return SUCCESS;
	}

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}
}
