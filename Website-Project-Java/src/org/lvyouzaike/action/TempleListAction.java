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
import org.lvyouzaike.model.VoteTemple;
import org.lvyouzaike.service.CommentService;
import org.lvyouzaike.service.ExpressService;
import org.lvyouzaike.service.VoteTempleRecordService;
import org.lvyouzaike.service.admin.CommentAdminService;
import org.lvyouzaike.service.admin.ExpressAdminService;
import org.lvyouzaike.service.admin.VoteTempleAdminService;
import org.lvyouzaike.service.admin.impl.CommentTempleAdminServiceImpl;
import org.lvyouzaike.service.admin.impl.ExpressSupportAdminServiceImpl;
import org.lvyouzaike.service.admin.impl.VoteTempleAdminServiceImpl;
import org.lvyouzaike.service.impl.CommentTempleServiceImpl;
import org.lvyouzaike.service.impl.ExpressSupportServiceImpl;
import org.lvyouzaike.service.impl.VoteTempleRecordServiceImpl;
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

@Component("templeListAction")
@Scope("prototype")
public class TempleListAction extends ActionSupport {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String nickname;  //the nickname input on temple comment area
	private String comment;   //the comment input on temple comment area
	private String support;   //the express support vote
	private String templeCheck; //the list to hold all the votes on temple list
	
	public String getTempleCheck() {
		return templeCheck;
	}

	public void setTempleCheck(String templeCheck) {
		this.templeCheck = templeCheck;
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

	public String getSupport() {
		return support;
	}

	public void setSupport(String support) {
		this.support = support;
	}
	
	// when the page is loaded, fetch the total count of support express for temple list
	public String getExpressCount() throws IOException {
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("beans.xml");
		ExpressAdminService expressSupportAdminService = (ExpressSupportAdminServiceImpl) applicationContext.getBean("expressSupportAdminService");

		String name = "templeSupport"; // name of the item in express support table for this temple list
		int total = 0;

		ActionContext context = ActionContext.getContext();
		Map<String, Object> map = context.getApplication();

		if (null == map.get(name)) {// first time, need to fetch the total support count out of db
			total = expressSupportAdminService.getExpressTotalCount(name);
			map.put(name, total);
		} else {
			total = (Integer) map.get(name);
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

	public String express() throws IOException{
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("beans.xml");
		ExpressService expressSupportService = (ExpressSupportServiceImpl) applicationContext.getBean("expressSupportService");
		ExpressAdminService expressSupportAdminService = (ExpressSupportAdminServiceImpl) applicationContext.getBean("expressSupportAdminService");
		
		List<Integer> templeSupports = new ArrayList<Integer>();
		ActionContext context = ActionContext.getContext();
		Map<String, Object> map = context.getApplication();
		
		String name = "templeSupport";
		int item = Integer.parseInt(support.substring(support.length()-1));  //item's index
		
		//1. increment the current support into db
		if(!expressSupportService.increExpressItem(name, item))
			System.out.println("failure of update db");		
		
		if(null == map.get(support)){//the first time, need to fetch support express data of temple list out of db
			//2. fetch the updated support from db
			ExpressSupport es = (ExpressSupport) expressSupportAdminService.getExpressByName(name);
			//3. put the values of support express
			map.put("temple1", es.getItem1());
			map.put("temple2", es.getItem2());
			map.put("temple3", es.getItem3());
			map.put("temple4", es.getItem4());
			map.put("temple5", es.getItem5());
		}else{
			map.put(support, (Integer)map.get(support) + 1);  //increment corresponding support firstly
			
		}
		//4.increment the total count in the application
		map.put(name, (Integer)map.get(name) + 1);
		
		templeSupports.add((Integer)map.get("temple1"));  //prepare the result to be sent back to ajax callback method
		templeSupports.add((Integer)map.get("temple2"));
		templeSupports.add((Integer)map.get("temple3"));
		templeSupports.add((Integer)map.get("temple4"));
		templeSupports.add((Integer)map.get("temple5"));
		
		Gson gson = new Gson();
		String result = gson.toJson(templeSupports);
		
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
	
	public String getTempleComments() throws IOException{
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("beans.xml");
		CommentAdminService commentTempleAdminService = (CommentTempleAdminServiceImpl) applicationContext.getBean("commentTempleAdminService");
		List<CommentModel> comments = commentTempleAdminService.getAll();
		
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
	
	public String addTempleComment() throws Exception{
		nickname = URLDecoder.decode(nickname, "UTF-8");
		comment = URLDecoder.decode(comment, "UTF-8");
				
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("beans.xml");
		CommentService commentTempleService = (CommentTempleServiceImpl) applicationContext.getBean("commentTempleService");
		
		boolean add = commentTempleService.addComment(nickname, comment);
		
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

	//when the page is loaded, fetch the total votes for each item in the temple list
	public String getTempleVotes() throws IOException{
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("beans.xml");
		VoteTempleAdminService voteTempleAdminService = (VoteTempleAdminServiceImpl) applicationContext.getBean("voteTempleAdminService");
		
		List<VoteTemple> temples = voteTempleAdminService.getAllTemples();
		List<Integer> votes = new ArrayList<Integer>();
		
		//put all the votes against the temples in the list
		for(VoteTemple t: temples){
			votes.add(t.getVoteTempleRecord().size());
		}
		
		Gson gson = new Gson();
		String result = gson.toJson(votes);
		
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
	
	public String vote() throws IOException{
		templeCheck = URLDecoder.decode(templeCheck, "UTF-8");
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("beans.xml");
		VoteTempleRecordService voteTempleRecordService = (VoteTempleRecordServiceImpl) applicationContext.getBean("voteTempleRecordService");
		VoteTempleAdminService voteTempleAdminService = (VoteTempleAdminServiceImpl) applicationContext.getBean("voteTempleAdminService");
		
		/*HttpServletRequest req = ServletActionContext.getRequest();
		System.out.println("request ip is: " + req.getRemoteAddr());*/
		
		String ip = ServletActionContext.getRequest().getRemoteAddr();  
		
		boolean addVote = voteTempleRecordService.add(ip, templeCheck);  //add the new vote 
		if(!addVote)
			System.out.println("something is wrong");
		
		//this time get the new votes after this vote
		List<VoteTemple> temples = voteTempleAdminService.getAllTemples();
		List<Integer> votes = new ArrayList<Integer>();
		
		//put all the votes against the hills in the list
		for(VoteTemple t: temples){
			votes.add(t.getVoteTempleRecord().size());
		}
				
		Gson gson = new Gson();
		String result = gson.toJson(votes);  //send back new votes data.
		
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
