package org.lvyouzaike.action.admin;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.lvyouzaike.util.LuceneWithMMAnalyzer;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.opensymphony.xwork2.ActionSupport;

/*
 * one method: create() to create new indice for lucene searching
 */

@Component("createIndexAdminAction")
@Scope("prototype")
public class CreateIndexAdminAction extends ActionSupport implements ServletRequestAware{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static String[] FILEDIRS = {"news", "commonlist", "download", "box", "files", "individual", "special", "mail"};
	private static String INDEXPATH = "luceneindex";
	
	public HttpServletRequest request;
	
	//this method is only for test on this page!!!
	public String query() throws IOException, ParseException, InvalidTokenOffsetsException{
		ServletContext sc = request.getServletContext();
		
		//System.out.println("real index path: " + sc.getRealPath(INDEXPATH));
				
		LuceneWithMMAnalyzer.search(sc.getRealPath(INDEXPATH), "there");
		
		Gson gson = new Gson();
		String result = gson.toJson(true);  //send back a boolean object for test
		
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
	
	public String create() throws Exception{
		ServletContext sc = request.getServletContext();
		List<String> fileDirs = new ArrayList<String>();
		for(String fileDir: FILEDIRS){
			fileDirs.add(sc.getRealPath(fileDir));			
		}
				
		LuceneWithMMAnalyzer.createIndice(sc.getRealPath(INDEXPATH), fileDirs);
		
		Gson gson = new Gson();
		String result = gson.toJson(true);  //send back a boolean object for test
		
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
}
