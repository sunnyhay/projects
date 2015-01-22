package org.lvyouzaike.util;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.lvyouzaike.freemarker.Entity;
import org.lvyouzaike.freemarker.Lst;
import org.lvyouzaike.model.Mail;
import org.lvyouzaike.model.Resort;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/*
 * to process two freemarker templates and finally get a string representing all the stuff done. 
 */
public class ProcessFM {
	private static final String NOCONTENT = "本期无更新内容";    //the default declaration for no update content this time
	private static final String NOCUSTOMSUB = "您没有订阅任何旅游目的地";    //the default declaration for no custom subscription
	private static final String NOCUSTOMCONTENT = "当前没有任何旅游目的地信息";   //the default declaration for no custom content
	
	Map<String, Object> root;              //the root
	Map<String, String> standard;          //standard content
	Map<String, String> custom;            //custom content
	private Map<String, String> picMap;            //store pictures' location-id pairs parsed from standard.properties
	
	Configuration cfg;                     //the unique configuration
	Template sTemp;                        //the standard template
	Template cTemp;                        //the custom template
	ByteArrayOutputStream sout;            //standard output
	ByteArrayOutputStream cout;            //custom output
	Writer writer;                         //the writer
	
	public Map<String, String> getPicMap() {
		return picMap;
	}
	
	public ProcessFM(String tempPath) throws IOException{
		root = new HashMap<String, Object>();
		standard = new HashMap<String, String>();
		custom = new HashMap<String, String>();
		picMap = new HashMap<String, String>();
								
		//1. create a configuration, set the directory where templates reside.
		cfg = new Configuration();
		cfg.setDefaultEncoding("UTF-8");
		cfg.setDirectoryForTemplateLoading(new File(tempPath));
		cfg.setObjectWrapper(new DefaultObjectWrapper());
		
		//2. construct templates
		sTemp = cfg.getTemplate("standard.ftl");     //the filename is fixed!!!
		cTemp = cfg.getTemplate("custom.ftl");       //the filename is fixed!!!
		sTemp.setEncoding("UTF-8");
		cTemp.setEncoding("UTF-8");
	}
	
	public void buildDataModel(String standardPath, String customPath, Mail mail) throws FileNotFoundException, IOException{
		Entity e = new Entity();  //common entity object
		int num;                  //common integer counter
		
		// 3. build the standard data model
		root.put("std", standard);
		
		// read from standard.properties and set order 
		Properties stdProp = new Properties();
		stdProp.load(new BufferedInputStream(new FileInputStream(standardPath)));
		standard.put("order", stdProp.getProperty("order"));
		DateFormat df = DateFormat.getDateInstance(DateFormat.FULL, Locale.CHINA);
		standard.put("time", "" + df.format(new Date()));
		
		//read pictures' information into picmap
		for(int i = 1; i <= Integer.parseInt(stdProp.getProperty("pic_num")); i++){
			picMap.put(stdProp.getProperty("pic" + i + "_id"), stdProp.getProperty("pic" + i + "_location"));
		}
		
		//set news list
		List<Entity> newsList = new ArrayList<Entity>();
		num = Integer.parseInt(stdProp.getProperty("news_num"));
		if(num > 0){
			for(int i = 1; i <= num; i++){
				e.setTitle(stdProp.getProperty("news" + i + "_title"));
				e.setSummary(stdProp.getProperty("news" + i + "_summary"));
				newsList.add(e);
				e = new Entity();
			}
		}else{
			e.setTitle(NOCONTENT);
			e.setSummary(NOCONTENT);
			newsList.add(e);
			e = new Entity();
		}
		root.put("newslist", newsList);
		
		// policy list
		List<Entity> policyList = new ArrayList<Entity>();
		num = Integer.parseInt(stdProp.getProperty("policy_num"));
		if(num > 0){
			for(int i = 1; i <= num; i++){
				e.setTitle(stdProp.getProperty("policy" + i + "_title"));
				e.setSummary(stdProp.getProperty("policy" + i + "_summary"));
				policyList.add(e);
				e = new Entity();
			}
		}else{
			e.setTitle(NOCONTENT);
			e.setSummary(NOCONTENT);
			policyList.add(e);
			e = new Entity();
		}
		root.put("policylist", policyList);
		
		// box list
		List<Entity> boxList = new ArrayList<Entity>();
		num = Integer.parseInt(stdProp.getProperty("box_num"));
		if(num > 0){
			for(int i = 1; i <= num; i++){
				e.setTitle(stdProp.getProperty("box" + i + "_title"));
				e.setSummary(stdProp.getProperty("box" + i + "_summary"));
				boxList.add(e);
				e = new Entity();
			}
		}else{
			e.setTitle(NOCONTENT);
			e.setSummary(NOCONTENT);
			boxList.add(e);
			e = new Entity();
		}
		root.put("boxlist", boxList);
		
		//lst object
		Lst lst = new Lst();
		lst.setCityFirstAmount(Integer.parseInt(stdProp.getProperty("cityFirstAmount")));
		lst.setCityFirstName(stdProp.getProperty("cityFirstName"));
		lst.setCitySecondAmount(Integer.parseInt(stdProp.getProperty("citySecondAmount")));
		lst.setCitySecondName(stdProp.getProperty("citySecondName"));
		lst.setCityThirdAmount(Integer.parseInt(stdProp.getProperty("cityThirdAmount")));
		lst.setCityThirdName(stdProp.getProperty("cityThirdName"));
		lst.setHillFirstAmount(Integer.parseInt(stdProp.getProperty("hillFirstAmount")));
		lst.setHillFirstName(stdProp.getProperty("hillFirstName"));
		lst.setHillSecondAmount(Integer.parseInt(stdProp.getProperty("hillSecondAmount")));
		lst.setHillSecondName(stdProp.getProperty("hillSecondName"));
		lst.setHillThirdAmount(Integer.parseInt(stdProp.getProperty("hillThirdAmount")));
		lst.setHillThirdName(stdProp.getProperty("hillThirdName"));
		lst.setNumOfFirstBeizai(Integer.parseInt(stdProp.getProperty("numOfFirstBeizai")));
		lst.setNumOfFirstMeizai(Integer.parseInt(stdProp.getProperty("numOfFirstMeizai")));
		lst.setNumOfFirstRatio(Integer.parseInt(stdProp.getProperty("numOfFirstRatio")));
		lst.setNumOfFirstTotal(Integer.parseInt(stdProp.getProperty("numOfFirstTotal")));
		lst.setNumOfSecondBig(Integer.parseInt(stdProp.getProperty("numOfSecondBig")));
		lst.setNumOfSecondMedium(Integer.parseInt(stdProp.getProperty("numOfSecondMedium")));
		lst.setNumOfSecondMost(Integer.parseInt(stdProp.getProperty("numOfSecondMost")));
		lst.setNumOfSecondNone(Integer.parseInt(stdProp.getProperty("numOfSecondNone")));
		lst.setNumOfSecondSmall(Integer.parseInt(stdProp.getProperty("numOfSecondSmall")));
		lst.setNumOfSecondTotal(Integer.parseInt(stdProp.getProperty("numOfSecondTotal")));
		lst.setOuterNationFirstAmount(Integer.parseInt(stdProp.getProperty("outerNationFirstAmount")));
		lst.setOuterNationFirstName(stdProp.getProperty("outerNationFirstName"));
		lst.setOuterNationSecondAmount(Integer.parseInt(stdProp.getProperty("outerNationSecondAmount")));
		lst.setOuterNationSecondName(stdProp.getProperty("outerNationSecondName"));
		lst.setOuterNationThirdAmount(Integer.parseInt(stdProp.getProperty("outerNationThirdAmount")));
		lst.setOuterNationThirdName(stdProp.getProperty("outerNationThirdName"));
		lst.setOuterResortFirstAmount(Integer.parseInt(stdProp.getProperty("outerResortFirstAmount")));
		lst.setOuterResortFirstName(stdProp.getProperty("outerResortFirstName"));
		lst.setOuterResortSecondAmount(Integer.parseInt(stdProp.getProperty("outerResortSecondAmount")));
		lst.setOuterResortSecondName(stdProp.getProperty("outerResortSecondName"));
		lst.setOuterResortThirdAmount(Integer.parseInt(stdProp.getProperty("outerResortThirdAmount")));
		lst.setOuterResortThirdName(stdProp.getProperty("outerResortThirdName"));
		lst.setResortFirstAmount(Integer.parseInt(stdProp.getProperty("resortFirstAmount")));
		lst.setResortFirstName(stdProp.getProperty("resortFirstName"));
		lst.setResortSecondAmount(Integer.parseInt(stdProp.getProperty("resortSecondAmount")));
		lst.setResortSecondName(stdProp.getProperty("resortSecondName"));
		lst.setResortThirdAmount(Integer.parseInt(stdProp.getProperty("resortThirdAmount")));
		lst.setResortThirdName(stdProp.getProperty("resortThirdName"));
		lst.setTempleFirstAmount(Integer.parseInt(stdProp.getProperty("templeFirstAmount")));
		lst.setTempleFirstName(stdProp.getProperty("templeFirstName"));
		lst.setTempleSecondAmount(Integer.parseInt(stdProp.getProperty("templeSecondAmount")));
		lst.setTempleSecondName(stdProp.getProperty("templeSecondName"));
		lst.setTempleThirdAmount(Integer.parseInt(stdProp.getProperty("templeThirdAmount")));
		lst.setTempleThirdName(stdProp.getProperty("templeThirdName"));
		
		root.put("lst", lst);
		
		// 4. build the custom data model
		root.put("ctm", custom);
		
		//get mail subscription information 
		Set<Resort> resorts = mail.getResorts();
		
				
		custom.put("email", mail.getAddress());
		custom.put("sub_date", "" + mail.getSubDate());		
		custom.put("send_date", "" + mail.getLastSendDate());
		
		// read from custom.properties 
		Properties ctmProp = new Properties();
		ctmProp.load(new BufferedInputStream(new FileInputStream(customPath)));
		Map<String, String> customMap = new HashMap<String, String>();
		int amount = Integer.parseInt(ctmProp.getProperty("num"));
		for(int i = 1; i <= amount; i++){
			customMap.put(ctmProp.getProperty("resort" + i + "_title"), ctmProp.getProperty("resort" + i + "_summary"));
		}
		
		// custom resort list
		List<Entity> customList = new ArrayList<Entity>();
		String resortlist = "";
		for(Resort r: resorts){
			resortlist += r.getName() + " ";
			if(customMap.containsKey(r.getName())){
				e = new Entity();
				e.setTitle(r.getName());
				e.setSummary(customMap.get(r.getName()));
				customList.add(e);
			}
				
		}
		custom.put("resortlist", resortlist);
		
		if(resorts.isEmpty()){
			e = new Entity();
			e.setTitle(NOCUSTOMSUB);
			e.setSummary("");
			customList.add(e);
		}
		if(customList.isEmpty()){
			e = new Entity();
			e.setTitle(NOCUSTOMCONTENT);
			e.setSummary("");
			customList.add(e);
		}
		root.put("customlist", customList);
	}
	
	public String produce() throws TemplateException, IOException{
		//5. construct the standard freemarker-based output
		sout = new ByteArrayOutputStream();
		writer = new OutputStreamWriter(sout);
		sTemp.process(root, writer);
		writer.flush();
		
		//6. construct the custom freemarker-based output
		cout = new ByteArrayOutputStream();
		writer = new OutputStreamWriter(cout);
		cTemp.process(root, writer);
		writer.flush();
		
		return sout.toString() + cout.toString();
	}
	
	public void closeAll() throws IOException{
		sout.close();
		cout.close();
		writer.close();
	}
}
