package org.lvyouzaike.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.lvyouzaike.service.admin.DownloadAdminService;
import org.lvyouzaike.service.admin.impl.DownloadAdminServiceImpl;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

/*
 * two methods: incrementCount() to increment a download count on some download item;
 * getCounts() to get all the counts when the page is loaded.
 */

@Component("downloadCountAction")
@Scope("prototype")
public class DownloadCountAction extends ActionSupport {
		
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public String getCounts() throws IOException{
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("beans.xml");
		DownloadAdminService downloadAdminService = (DownloadAdminServiceImpl) applicationContext.getBean("downloadAdminService");
		Map<Integer, Integer> counts = new HashMap<Integer, Integer>();
		
		//put all the download counts into application with id "download#" if first time, here # is the key of Map counts
		//if application has held those counts, fetch them out directly.
		ActionContext context = ActionContext.getContext();
		Map<String, Object> map = context.getApplication();
		int total = 10;  //attention!!!this amount is the number of download items in that table!! should be set manually!!!
		String download = "download";
		
		//use "download1" as the mark
		if(null == map.get("download1")){//the first time, need to fetch counts of download items out of db
			 counts = downloadAdminService.getItemCounts();
			 			 
			 for(int key: counts.keySet()){
				 map.put(download + key, counts.get(key));
			 }
		}else{
			for(int i = 1; i <= total; i++){
				counts.put(i, (Integer) map.get(download + i));
			}
		}
		
		Gson gson = new Gson();
		String result = gson.toJson(counts);
		
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
