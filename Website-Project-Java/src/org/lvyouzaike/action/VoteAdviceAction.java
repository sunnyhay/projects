package org.lvyouzaike.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.lvyouzaike.model.VoteAdvice;
import org.lvyouzaike.service.VoteAdviceService;
import org.lvyouzaike.service.admin.VoteAdviceAdminService;
import org.lvyouzaike.service.admin.impl.VoteAdviceAdminServiceImpl;
import org.lvyouzaike.service.impl.VoteAdviceServiceImpl;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.opensymphony.xwork2.ActionSupport;

/*
 * three methods: express() for support express system; addParkComment() to add a new comment on park list; getParkComments() to get all.
 */

@Component("voteAdviceAction")
@Scope("prototype")
public class VoteAdviceAction extends ActionSupport {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String nickname;  //the nickname input on vote advice
	private String comment;   //the comment input on vote advice
	
	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	
	
	public String getVoteAdvice() throws IOException{
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("beans.xml");
		VoteAdviceAdminService voteAdviceAdminService = (VoteAdviceAdminServiceImpl) applicationContext.getBean("voteAdviceAdminService");
		List<VoteAdvice> comments = voteAdviceAdminService.getAll();
						
		Gson gson = new Gson();
		String result = gson.toJson(comments);
		
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
	
	public String addVoteAdvice() throws Exception{
		nickname = URLDecoder.decode(nickname, "UTF-8");
		comment = URLDecoder.decode(comment, "UTF-8");
				
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("beans.xml");
		VoteAdviceService voteAdviceService = (VoteAdviceServiceImpl) applicationContext.getBean("voteAdviceService");
		
		boolean add = voteAdviceService.addVoteAdvice(nickname, comment);
		
		Gson gson = new Gson();
		String result = gson.toJson(add);  //send back a boolean object to show whether adding a city comment is done without error.
		
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
