package org.lvyouzaike.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.lvyouzaike.model.ExpressZaike;
import org.lvyouzaike.service.ExpressService;
import org.lvyouzaike.service.admin.ExpressAdminService;
import org.lvyouzaike.service.admin.impl.ExpressZaikeAdminServiceImpl;
import org.lvyouzaike.service.impl.ExpressZaikeServiceImpl;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

/*
 * one method: zaike() for yes or no poll zaike express system
 */

@Component("pollzaikeAction")
@Scope("prototype")
public class PollzaikeAction extends ActionSupport {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String zaike;   //the express zaike vote: yes or no
	
	public String getZaike() {
		return zaike;
	}

	public void setZaike(String zaike) {
		this.zaike = zaike;
	}
	
	//when the page is loaded, fetch the total count of total poll zaike express for index.html 
	public String getExpressCount() throws IOException{
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("beans.xml");
		ExpressAdminService expressZaikeAdminService = (ExpressZaikeAdminServiceImpl) applicationContext.getBean("expressZaikeAdminService");
		
		String name = "pollzaike";  //name of the item in express zaike table
		int total = 0;
		
		ActionContext context = ActionContext.getContext();
		Map<String, Object> map = context.getApplication();
		
		if(null == map.get(name)){//first time, need to fetch the total support count out of db
			total = expressZaikeAdminService.getExpressTotalCount(name);
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

	public String zaike() throws IOException{
		/*System.out.println();
		System.out.println("pollzaike is: " + zaike);
		System.out.println();*/
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("beans.xml");
		ExpressService expressZaikeService = (ExpressZaikeServiceImpl) applicationContext.getBean("expressZaikeService");
		ExpressAdminService expressZaikeAdminService = (ExpressZaikeAdminServiceImpl) applicationContext.getBean("expressZaikeAdminService");
		
		List<Integer> polls = new ArrayList<Integer>();
		ActionContext context = ActionContext.getContext();
		Map<String, Object> map = context.getApplication();
		
		String name = "pollzaike";
		int item = Integer.parseInt(zaike.substring(zaike.length()-1));  //item's index
		
		//1. increment the current pollzaike into db
		if(!expressZaikeService.increExpressItem(name, item))
			System.out.println("failure of update db");		
		
		if(null == map.get(zaike)){//the first time, need to fetch pollzaike express data out of db
			//2. fetch the updated pollzaike from db
			ExpressZaike ez = (ExpressZaike) expressZaikeAdminService.getExpressByName(name);
			//3. put the values of pollzaike express
			map.put("pollzaike1", ez.getItem1());
			map.put("pollzaike2", ez.getItem2());
		}else{
			map.put(zaike, (Integer)map.get(zaike) + 1);  //increment corresponding pollzaike firstly
			
		}
		//4.increment the total count in the application
		map.put(name, (Integer)map.get(name) + 1);

		polls.add((Integer)map.get("pollzaike1"));  //prepare the result to be sent back to ajax callback method
		polls.add((Integer)map.get("pollzaike2"));
		
		Gson gson = new Gson();
		String result = gson.toJson(polls);
		
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
