package org.lvyouzaike.action.admin;

import java.io.PrintWriter;
import java.net.URLDecoder;

import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.lvyouzaike.service.admin.AreaAdminService;
import org.lvyouzaike.service.admin.impl.AreaAdminServiceImpl;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.opensymphony.xwork2.ActionSupport;

/*
 * one method: addArea() to add a new area.
 */

@Component("addAreaAction")
@Scope("prototype")
public class AddAreaAction extends ActionSupport {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String name;  //the name of the area
		
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}	
	
	public String addArea() throws Exception{
		name = URLDecoder.decode(name, "UTF-8");
				
		//System.out.println("area name is: " + name);
		
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("beans.xml");
		AreaAdminService areaAdminService = (AreaAdminServiceImpl) applicationContext.getBean("areaAdminService");
		
		boolean add = areaAdminService.addArea(name);
		
		Gson gson = new Gson();
		String result = gson.toJson(add);  //send back a boolean object to show whether adding an area is done without error.
		
		System.out.println("result is: " + result);
		
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
