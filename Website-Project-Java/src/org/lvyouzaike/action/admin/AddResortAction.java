package org.lvyouzaike.action.admin;

import java.io.PrintWriter;
import java.net.URLDecoder;

import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.lvyouzaike.service.admin.ResortAdminService;
import org.lvyouzaike.service.admin.impl.ResortAdminServiceImpl;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.opensymphony.xwork2.ActionSupport;

/*
 * one method: addResort() to add a new area.
 */

@Component("addResortAction")
@Scope("prototype")
public class AddResortAction extends ActionSupport {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String name;  //the name of the resort
	private String description;  //the description of the resort
	private String type;  //the type of the resort
	private String area;  //the area to which the resort belongs
		
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}	
	
	public String addResort() throws Exception{
		name = URLDecoder.decode(name, "UTF-8");
		description = URLDecoder.decode(description, "UTF-8");
		type = URLDecoder.decode(type, "UTF-8");
		area = URLDecoder.decode(area, "UTF-8");
				
		/*System.out.println("resort name is: " + name);
		System.out.println("resort description is: " + description);
		System.out.println("resort type is: " + type);
		System.out.println("resort area is: " + area);*/
		
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("beans.xml");
		ResortAdminService resortAdminService = (ResortAdminServiceImpl) applicationContext.getBean("resortAdminService");
		
		byte[] img = null;
		String identity = "";
		if("resort".equals(type))
			identity = "domesticresort";
		else if("city".equals(type))
			identity = "domesticcity";
		else if("interresort".equals(type))
			identity = "interresort";
		else if("intercity".equals(type))
			identity = "intercity";
		else
			System.out.println("impossible!");
		
		boolean add = resortAdminService.addResort(name, description, img, identity, area);
		
		Gson gson = new Gson();
		String result = gson.toJson(add);  //send back a boolean object to show whether adding an area is done without error.
		
		//System.out.println("result is: " + result);
		
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
