package org.lvyouzaike.action;

import java.net.URLDecoder;

import javax.annotation.Resource;

import org.apache.struts2.ServletActionContext;
import org.lvyouzaike.service.VoterService;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.opensymphony.xwork2.ActionSupport;

/*
 * vote(): to vote for a list of notorious resorts. 
 */

@Component("voteAction")
@Scope("prototype")
public class VoteAction extends ActionSupport {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private VoterService voterService;
	private String votehidden;          //actually passes the real choices of the user when user votes
	
	public String getVotehidden() {
		return votehidden;
	}

	public void setVotehidden(String votehidden) {
		this.votehidden = votehidden;
	}

	public VoterService getVoterService() {
		return voterService;
	}

	@Resource
	public void setVoterService(VoterService voterService) {
		this.voterService = voterService;
	}
	
	public String vote() throws Exception{
		/*System.out.println("remote IP address: " + ServletActionContext.getRequest().getRemoteAddr());
		System.out.println("request URL: " + ServletActionContext.getRequest().getRequestURL());
		System.out.println("local IP address: " + ServletActionContext.getRequest().getLocalAddr());
		System.out.println("request URI: " + ServletActionContext.getRequest().getRequestURI());*/
		 
		
		String ip = ServletActionContext.getRequest().getRemoteAddr();  
		votehidden = URLDecoder.decode(votehidden,"UTF-8");
		//System.out.println("vote hidden string: " + votehidden + " with ip: " + ip);
		
		boolean result = voterService.addVote(ip, votehidden);
		if(result)
			return SUCCESS;
		else
			return ERROR;
	}	
}
