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
import org.lvyouzaike.model.CommentModel;
import org.lvyouzaike.model.ExpressSupport;
import org.lvyouzaike.service.CommentService;
import org.lvyouzaike.service.ExpressService;
import org.lvyouzaike.service.admin.CommentAdminService;
import org.lvyouzaike.service.admin.ExpressAdminService;
import org.lvyouzaike.service.admin.impl.CommentInterResortAdminServiceImpl;
import org.lvyouzaike.service.admin.impl.ExpressSupportAdminServiceImpl;
import org.lvyouzaike.service.impl.CommentInterResortServiceImpl;
import org.lvyouzaike.service.impl.ExpressSupportServiceImpl;
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

@Component("interresortListAction")
@Scope("prototype")
public class InterresortListAction extends ActionSupport {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String nickname;  //the nickname input on interresort comment area
	private String comment;   //the comment input on interresort comment area
	private String support;   //the express support vote
	
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

	public String getSupport() {
		return support;
	}

	public void setSupport(String support) {
		this.support = support;
	}
	
	//when the page is loaded, fetch the total count of support express for interresort list 
	public String getExpressCount() throws IOException{
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("beans.xml");
		ExpressAdminService expressSupportAdminService = (ExpressSupportAdminServiceImpl) applicationContext.getBean("expressSupportAdminService");
		
		String name = "interresortSupport";  //name of the item in express support table for this interresort list
		int total = 0;
		
		ActionContext context = ActionContext.getContext();
		Map<String, Object> map = context.getApplication();
		
		if(null == map.get(name)){//first time, need to fetch the total support count out of db
			total = expressSupportAdminService.getExpressTotalCount(name);
			map.put(name, total);
		}else{
			total = (Integer) map.get(name);
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

	public String express() throws IOException{
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("beans.xml");
		ExpressService expressSupportService = (ExpressSupportServiceImpl) applicationContext.getBean("expressSupportService");
		ExpressAdminService expressSupportAdminService = (ExpressSupportAdminServiceImpl) applicationContext.getBean("expressSupportAdminService");
		
		List<Integer> interresortSupports = new ArrayList<Integer>();
		ActionContext context = ActionContext.getContext();
		Map<String, Object> map = context.getApplication();
		
		String name = "interresortSupport";
		int item = Integer.parseInt(support.substring(support.length()-1));  //item's index
		
		//1. increment the current support into db
		if(!expressSupportService.increExpressItem(name, item))
			System.out.println("failure of update db");		
		
		if(null == map.get(support)){//the first time, need to fetch support express data of interresort list out of db
			//2. fetch the updated support from db
			ExpressSupport es = (ExpressSupport) expressSupportAdminService.getExpressByName(name);
			//3. put the values of support express
			map.put("interresort1", es.getItem1());
			map.put("interresort2", es.getItem2());
			map.put("interresort3", es.getItem3());
			map.put("interresort4", es.getItem4());
			map.put("interresort5", es.getItem5());
		}else{
			map.put(support, (Integer)map.get(support) + 1);  //increment corresponding support firstly
			
		}
		//4.increment the total count in the application
		map.put(name, (Integer)map.get(name) + 1);

		interresortSupports.add((Integer)map.get("interresort1"));  //prepare the result to be sent back to ajax callback method
		interresortSupports.add((Integer)map.get("interresort2"));
		interresortSupports.add((Integer)map.get("interresort3"));
		interresortSupports.add((Integer)map.get("interresort4"));
		interresortSupports.add((Integer)map.get("interresort5"));
		
		Gson gson = new Gson();
		String result = gson.toJson(interresortSupports);
		
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
	
	public String getInterResortComments() throws IOException{
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("beans.xml");
		CommentAdminService commentInterResortAdminService = (CommentInterResortAdminServiceImpl) applicationContext.getBean("commentInterResortAdminService");
		List<CommentModel> comments = commentInterResortAdminService.getAll();
		
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
	
	public String addInterResortComment() throws Exception{
		nickname = URLDecoder.decode(nickname, "UTF-8");
		comment = URLDecoder.decode(comment, "UTF-8");
				
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("beans.xml");
		CommentService commentInterResortService = (CommentInterResortServiceImpl) applicationContext.getBean("commentInterResortService");
		
		boolean add = commentInterResortService.addComment(nickname, comment);
		
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
