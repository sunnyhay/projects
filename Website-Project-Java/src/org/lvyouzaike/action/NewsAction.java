package org.lvyouzaike.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.lvyouzaike.model.ExpressAttitude;
import org.lvyouzaike.model.ExpressSupport;
import org.lvyouzaike.model.ExpressZaike;
import org.lvyouzaike.model.NewsComment;
import org.lvyouzaike.service.ExpressService;
import org.lvyouzaike.service.NewsCommentService;
import org.lvyouzaike.service.admin.ExpressAdminService;
import org.lvyouzaike.service.admin.NewsCommentAdminService;
import org.lvyouzaike.service.admin.impl.ExpressAttitudeAdminServiceImpl;
import org.lvyouzaike.service.admin.impl.ExpressSupportAdminServiceImpl;
import org.lvyouzaike.service.admin.impl.ExpressZaikeAdminServiceImpl;
import org.lvyouzaike.service.admin.impl.NewsCommentAdminServiceImpl;
import org.lvyouzaike.service.impl.ExpressAttitudeServiceImpl;
import org.lvyouzaike.service.impl.ExpressSupportServiceImpl;
import org.lvyouzaike.service.impl.ExpressZaikeServiceImpl;
import org.lvyouzaike.service.impl.NewsCommentServiceImpl;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

/*
 * three methods: express() for support express system; addParkComment() to add a new comment on park list; getParkComments() to get all.
 */

@Component("newsAction")
@Scope("prototype")
public class NewsAction extends ActionSupport {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String name;      //the name of the news
	private String nickname;  //the nickname input on this news
	private String comment;   //the comment input on this news
	private String zaike;   //the express zaike vote
	private String attitude;  //the express attitude vote
	private String support;  //the express support vote
	
	public String getSupport() {
		return support;
	}

	public void setSupport(String support) {
		this.support = support;
	}
	
	public String getAttitude() {
		return attitude;
	}

	public void setAttitude(String attitude) {
		this.attitude = attitude;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getZaike() {
		return zaike;
	}

	public void setZaike(String zaike) {
		this.zaike = zaike;
	}

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
	
	//when the page is loaded, fetch the total count of zaike express for news with this name 
	public String getZaikeCount() throws IOException{
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("beans.xml");
		ExpressAdminService expressZaikeAdminService = (ExpressZaikeAdminServiceImpl) applicationContext.getBean("expressZaikeAdminService");
		
		String zaikeName = "zaike" + name;
		int total = 0;
		
		ActionContext context = ActionContext.getContext();
		Map<String, Object> map = context.getApplication();
		
		if(null == map.get(zaikeName)){//first time, need to fetch the total zaike count out of db
			total = expressZaikeAdminService.getExpressTotalCount(zaikeName);
			map.put(zaikeName, total);
		}else{
			total = (Integer) map.get(zaikeName);
		}
		
		Gson gson = new Gson();
		String result = gson.toJson(total);
		
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
	
	//when the page is loaded, fetch the total count of attitude express for news with this name 
	public String getAttitudeCount() throws IOException{
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("beans.xml");
		ExpressAdminService expressAttitudeAdminService = (ExpressAttitudeAdminServiceImpl) applicationContext.getBean("expressAttitudeAdminService");
		
		String attitudeName = "attitude" + name;
		int total = 0;
		
		ActionContext context = ActionContext.getContext();
		Map<String, Object> map = context.getApplication();
		
		if(null == map.get(attitudeName)){//first time, need to fetch the total attitude count out of db
			total = expressAttitudeAdminService.getExpressTotalCount(attitudeName);
			map.put(attitudeName, total);
		}else{
			total = (Integer) map.get(attitudeName);
		}
		
		Gson gson = new Gson();
		String result = gson.toJson(total);
		
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
	
	//when the page is loaded, fetch the total count of support express for news with this name 
	public String getSupportCount() throws IOException {
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("beans.xml");
		ExpressAdminService expressSupportAdminService = (ExpressSupportAdminServiceImpl) applicationContext.getBean("expressSupportAdminService");

		String supportName = "support" + name;
		int total = 0;

		ActionContext context = ActionContext.getContext();
		Map<String, Object> map = context.getApplication();

		if (null == map.get(supportName)) {// first time, need to fetch the total support count out of db
			total = expressSupportAdminService.getExpressTotalCount(supportName);
			map.put(supportName, total);
		} else {
			total = (Integer) map.get(supportName);
		}

		Gson gson = new Gson();
		String result = gson.toJson(total);

		HttpServletResponse resp = ServletActionContext.getResponse();
		resp.setContentType("application/json;charset=utf-8");
		resp.setHeader("Cache-Control", "no-cache"); // cancel cache when ajax works

		PrintWriter out = resp.getWriter();

		OutputFormat format = OutputFormat.createPrettyPrint();
		format.setEncoding("utf-8");

		XMLWriter writer = new XMLWriter(out, format);
		writer.write(result);

		out.flush();
		out.close();

		return null;
	}

	public String expressZaike() throws IOException{
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("beans.xml");
		ExpressService expressZaikeService = (ExpressZaikeServiceImpl) applicationContext.getBean("expressZaikeService");
		ExpressAdminService expressZaikeAdminService = (ExpressZaikeAdminServiceImpl) applicationContext.getBean("expressZaikeAdminService");
		
		List<Integer> newsZaike = new ArrayList<Integer>();
		ActionContext context = ActionContext.getContext();
		Map<String, Object> map = context.getApplication();
		
		String zaikeName = zaike.substring(0, zaike.length()-1) + name;
		int item = Integer.parseInt(zaike.substring(zaike.length()-1));  //item's index
		
		//1. increment the current zaike into db
		if(!expressZaikeService.increExpressItem(zaikeName, item))
			System.out.println("failure of update db");		
		
		if(null == map.get(name)){//the first time, need to fetch zaike express data of this news with "name" out of db
			//2. fetch the updated zaike from db
			ExpressZaike ez = (ExpressZaike) expressZaikeAdminService.getExpressByName(zaikeName);
			//3. put the values of zaike express
			map.put(name+1, ez.getItem1());
			map.put(name+2, ez.getItem2());
			map.put(name+3, ez.getItem3());
			map.put(name+4, ez.getItem4());
			map.put(name+5, ez.getItem5());
		}else{
			map.put(name+item, (Integer)map.get(name+item) + 1);  //increment corresponding zaike firstly
			
		}
		//4.increment the total zaike count in the application
		map.put("zaike" + name, (Integer)map.get("zaike" + name) + 1);
		
		newsZaike.add((Integer)map.get(name+1));  //prepare the result to be sent back to ajax callback method
		newsZaike.add((Integer)map.get(name+2));
		newsZaike.add((Integer)map.get(name+3));
		newsZaike.add((Integer)map.get(name+4));
		newsZaike.add((Integer)map.get(name+5));
		
		Gson gson = new Gson();
		String result = gson.toJson(newsZaike);
		
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
	
	public String expressAttitude() throws IOException{
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("beans.xml");
		ExpressService expressAttitudeService = (ExpressAttitudeServiceImpl) applicationContext.getBean("expressAttitudeService");
		ExpressAdminService expressAttitudeAdminService = (ExpressAttitudeAdminServiceImpl) applicationContext.getBean("expressAttitudeAdminService");
		
		List<Integer> newsAttitude = new ArrayList<Integer>();
		ActionContext context = ActionContext.getContext();
		Map<String, Object> map = context.getApplication();
		
		String attitudeName = attitude.substring(0, attitude.length()-1) + name;
		int item = Integer.parseInt(attitude.substring(attitude.length()-1));  //item's index
		
		//1. increment the current attitude into db
		if(!expressAttitudeService.increExpressItem(attitudeName, item))
			System.out.println("failure of update db");		
		
		if(null == map.get(name)){//the first time, need to fetch attitude express data of this news with "name" out of db
			//2. fetch the updated attitude from db
			ExpressAttitude ea = (ExpressAttitude) expressAttitudeAdminService.getExpressByName(attitudeName);
			//3. put the values of attitude express
			map.put(name+1, ea.getItem1());
			map.put(name+2, ea.getItem2());
			map.put(name+3, ea.getItem3());
			map.put(name+4, ea.getItem4());
			map.put(name+5, ea.getItem5());
		}else{
			map.put(name+item, (Integer)map.get(name+item) + 1);  //increment corresponding attitude firstly
			
		}
		//4.increment the total attitude count in the application
		map.put("attitude" + name, (Integer)map.get("attitude" + name) + 1);
	
		newsAttitude.add((Integer)map.get(name+1));  //prepare the result to be sent back to ajax callback method
		newsAttitude.add((Integer)map.get(name+2));
		newsAttitude.add((Integer)map.get(name+3));
		newsAttitude.add((Integer)map.get(name+4));
		newsAttitude.add((Integer)map.get(name+5));
		
		Gson gson = new Gson();
		String result = gson.toJson(newsAttitude);
		
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
	
	public String expressSupport() throws IOException{
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("beans.xml");
		ExpressService expressSupportService = (ExpressSupportServiceImpl) applicationContext.getBean("expressSupportService");
		ExpressAdminService expressSupportAdminService = (ExpressSupportAdminServiceImpl) applicationContext.getBean("expressSupportAdminService");
		
		List<Integer> newsSupport = new ArrayList<Integer>();
		ActionContext context = ActionContext.getContext();
		Map<String, Object> map = context.getApplication();
		
		String supportName = support.substring(0, support.length()-1) + name;
		int item = Integer.parseInt(support.substring(support.length()-1));  //item's index
		
		//1. increment the current support into db
		if(!expressSupportService.increExpressItem(supportName, item))
			System.out.println("failure of update db");		
		
		if(null == map.get(name)){//the first time, need to fetch support express data of this news with "name" out of db
			//2. fetch the updated support from db
			ExpressSupport es = (ExpressSupport) expressSupportAdminService.getExpressByName(supportName);
			//3. put the values of attitude express
			map.put(name+1, es.getItem1());
			map.put(name+2, es.getItem2());
			map.put(name+3, es.getItem3());
			map.put(name+4, es.getItem4());
			map.put(name+5, es.getItem5());
		}else{
			map.put(name+item, (Integer)map.get(name+item) + 1);  //increment corresponding support firstly
			
		}
		//4.increment the total support count in the application
		map.put("support" + name, (Integer)map.get("support" + name) + 1);
	
		newsSupport.add((Integer)map.get(name+1));  //prepare the result to be sent back to ajax callback method
		newsSupport.add((Integer)map.get(name+2));
		newsSupport.add((Integer)map.get(name+3));
		newsSupport.add((Integer)map.get(name+4));
		newsSupport.add((Integer)map.get(name+5));
		
		Gson gson = new Gson();
		String result = gson.toJson(newsSupport);
		
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
	
	public String getNewsComments() throws IOException{
		String newsName = name;
		
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("beans.xml");
		NewsCommentAdminService newsCommentAdminService = (NewsCommentAdminServiceImpl) applicationContext.getBean("newsCommentAdminService");
		List<NewsComment> comments = newsCommentAdminService.getNewsCommentsForName(newsName);
		
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
	
	public String addNewsComment() throws Exception{
		nickname = URLDecoder.decode(nickname, "UTF-8");
		comment = URLDecoder.decode(comment, "UTF-8");
		String ip = ServletActionContext.getRequest().getRemoteAddr(); 
		String newsName = name;
				
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("beans.xml");
		NewsCommentService newsCommentService = (NewsCommentServiceImpl) applicationContext.getBean("newsCommentService");
		
		boolean add = newsCommentService.addNewsComment(newsName, ip, nickname, comment);
		
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
