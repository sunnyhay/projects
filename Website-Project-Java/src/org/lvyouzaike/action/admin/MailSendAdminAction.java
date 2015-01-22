package org.lvyouzaike.action.admin;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.lvyouzaike.model.Mail;
import org.lvyouzaike.service.admin.MailAdminService;
import org.lvyouzaike.service.admin.SMTPAccountAdminService;
import org.lvyouzaike.service.admin.impl.MailAdminServiceImpl;
import org.lvyouzaike.service.admin.impl.SMTPAccountAdminServiceImpl;
import org.lvyouzaike.util.GetCustomContent;
import org.lvyouzaike.util.ProcessFM;
import org.lvyouzaike.util.SendMail;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

/*
 * two methods: query() to fetch all the mail subscription information as well as prepared custom content.
 * send() to send the merged email to each subscriber.
 */

@Component("mailSendAdminAction")
@Scope("prototype")
public class MailSendAdminAction extends ActionSupport implements ServletRequestAware{
	private static final long serialVersionUID = 1L;
	private static final String STANDARDPROP = "templates/properties/standard.properties"; 
	private static final String CUSTOMPROP = "templates/properties/custom.properties";
	private static final String CONFIGPROP = "templates/properties/config.properties";
	private static final String FTLPATH = "templates/ftl";
	private static final String SMTPUSERNAME = "admin@lyzk.org";
	
	private List<Mail> mails;
	public HttpServletRequest request;
	private int num;             //current index of mails to be sent
	private String password;        //password of smtp account

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}
	
	private boolean pwdMatch(ApplicationContext applicationContext, String username, String password) throws Exception{
		SMTPAccountAdminService service = (SMTPAccountAdminServiceImpl) applicationContext.getBean("smtpAccountAdminService");
		return service.checkPwd(username, password);
	}
	
	@SuppressWarnings("unchecked")
	public String send() throws BeansException, Exception{
		System.out.println("this time: " + num + " with password: " + password);
		String sendbackString = "";
		
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("beans.xml");
				
		if(pwdMatch(applicationContext, SMTPUSERNAME, password)){
			ServletContext sc = request.getServletContext();
			ActionContext context = ActionContext.getContext();
			Map<String, Object> map = context.getApplication();
			List<Mail> mails = (List<Mail>) map.get("mailList");
			HashMap<String, String> picMap = new HashMap<String, String>();
			Mail currentMail = mails.get(num);
					
			//System.out.println(mails.get(num).getAddress());
			ProcessFM pfm = new ProcessFM(sc.getRealPath(FTLPATH));
			pfm.buildDataModel(sc.getRealPath(STANDARDPROP), sc.getRealPath(CUSTOMPROP), currentMail);
			picMap = (HashMap<String, String>) pfm.getPicMap();
			/*for(String id: picMap.keySet()){
				System.out.println("before id: " + id + " with location: " + picMap.get(id));
			}*/
			
			String bodyText = pfm.produce();
			pfm.closeAll();
			//System.out.println(bodyText);
			
			SendMail sm = new SendMail(bodyText, sc.getRealPath(CONFIGPROP));
			sm.prepare(password);
			//change the pic location
			for(String id: picMap.keySet()){
				picMap.put(id, sc.getRealPath(picMap.get(id)));
				//System.out.println("after id: " + id + " with location: " + picMap.get(id));
			}
			sm.send(mails.get(num).getAddress(), picMap);
			
			//after sending, update recent send date for each mail
			MailAdminService mailAdminService = (MailAdminServiceImpl) applicationContext.getBean("mailAdminService");
			
			currentMail.setLastSendDate(new Date());
			System.out.println(mailAdminService.updateMail(currentMail));
			sendbackString = currentMail.getAddress();			
		}else
			sendbackString = "INVALIDPWD";		
				
		Gson gson = new Gson();
		String result = gson.toJson(sendbackString);  //send back current email address with subscription
		
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

	public String query() throws Exception{
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("beans.xml");
		MailAdminService mailAdminService = (MailAdminServiceImpl) applicationContext.getBean("mailAdminService");
		ServletContext sc = request.getServletContext();
		
		mails = mailAdminService.getValidAll();
		
		int total = mails.size();
		int custom = mailAdminService.numOfCustomContent();
		String customContent = GetCustomContent.get(sc.getRealPath(CUSTOMPROP));
		
		List<String> queryResult = new ArrayList<String>();
		queryResult.add("" + total);
		queryResult.add("" + custom);
		queryResult.add(customContent);
		
		ActionContext context = ActionContext.getContext();
		Map<String, Object> map = context.getApplication();
		String name = "mailList";
		
		map.put(name, mails);
				
		Gson gson = new Gson();
		String result = gson.toJson(queryResult);  //send back for test
		
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

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
