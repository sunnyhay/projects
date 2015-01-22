package org.lvyouzaike.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;

//send welcome email to those new subscribers. combine freemarker and smtp functions
public class ProcessWelcome {
	Map<String, Object> root;              //the root
	Map<String, String> welcome;           //welcome content
	Configuration cfg;                     //the unique configuration
	Template temp;                         //the welcome template
	ByteArrayOutputStream out;             //the only output
	Writer writer;                         //the writer
	
	public ProcessWelcome(String tempPath) throws IOException{
		root = new HashMap<String, Object>();
		welcome = new HashMap<String, String>();
								
		//1. create a configuration, set the directory where templates reside.
		cfg = new Configuration();
		cfg.setDefaultEncoding("UTF-8");
		cfg.setDirectoryForTemplateLoading(new File(tempPath));
		cfg.setObjectWrapper(new DefaultObjectWrapper());
		
		//2. construct welcome template
		temp = cfg.getTemplate("welcome.ftl");      //the filename is fixed!!!
		temp.setEncoding("UTF-8");
	}
	
	public void buildDataModel(Date sub_date, String email, String resorts) throws FileNotFoundException, IOException{
		// 3. build the welcome data model
		root.put("welcome", welcome);
		
		DateFormat df = DateFormat.getDateInstance(DateFormat.FULL,
				Locale.CHINA);
		welcome.put("sub_date", "" + df.format(sub_date));
		welcome.put("email", email);
		if(resorts.equals("") || null == resorts)
			resorts = "您目前没有订阅任何旅游目的地";
		welcome.put("resortlist", "" + resorts);
	}
	
	public String produce() throws TemplateException, IOException{
		//4. construct the standard freemarker-based output
		out = new ByteArrayOutputStream();
		writer = new OutputStreamWriter(out);
		temp.process(root, writer);
		writer.flush();
		
		return out.toString();
	}
	
	public void closeAll() throws IOException{
		out.close();
		writer.close();
	}	
}
