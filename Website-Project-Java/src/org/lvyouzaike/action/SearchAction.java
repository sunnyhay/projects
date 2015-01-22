package org.lvyouzaike.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;

import org.apache.lucene.document.Document;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.apache.struts2.ServletActionContext;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.lvyouzaike.util.LuceneWithMMAnalyzer;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.opensymphony.xwork2.ActionSupport;

/*
 * one method: search() to query a given input for any searched results in local files
 */

@Component("searchAction")
@Scope("prototype")
public class SearchAction extends ActionSupport{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String query;  //the given input string
	private static String INDEXPATH = "luceneindex";
	//name and content are current two parameters to be fetched from each indiced document. more may be added later.
	private static final String DOCNAME = "name";            
	private static final String DOCCONTENT = "content";
	
	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}
	
	private String getRealPath(String url, String filename){
		final String[] DIRS = new String[]{"box" ,"commonlist", "download", "files", "individual", "mail", "news", "special"};
		
		/*System.out.println("url is: " + url);
		System.out.println("filename is: " + filename);*/
		String tail = "";
		for(String dir: DIRS){
			if(filename.contains(dir)){
				tail = filename.substring(filename.indexOf(dir));
				//System.out.println(name.substring(name.indexOf(dir)));
				break;
			}
		}
		
		if("".equals(tail))
			tail = filename.substring(filename.lastIndexOf("/")+1);
		
		return url.substring(0, url.lastIndexOf("/")+1) + tail;
	}

	public String search() throws IOException, ParseException, InvalidTokenOffsetsException{
		query = URLDecoder.decode(query,"UTF-8");	
		//System.out.println("query string: " + query);
		ServletContext sc = ServletActionContext.getRequest().getServletContext();
		List<Document> documents = LuceneWithMMAnalyzer.search(sc.getRealPath(INDEXPATH), query);
		List<String> results = new ArrayList<String>();
		
		//System.out.println(ServletActionContext.getRequest().getServerName());
		//System.out.println(ServletActionContext.getRequest().getServerPort());
		//System.out.println(ServletActionContext.getRequest().getRequestURL().toString());
				
		for(Document doc: documents){
			results.add(doc.get(DOCNAME));
			results.add(doc.get(DOCCONTENT));
			//System.out.println(doc.get("path"));
			results.add(getRealPath(ServletActionContext.getRequest().getRequestURL().toString(),doc.get("path")));
		}
		
		Gson gson = new Gson();
		String result = gson.toJson(results);
		
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
