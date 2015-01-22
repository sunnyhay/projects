package org.lvyouzaike.service;

public interface VoterService {
	//add a new vote record or update a given vote record, resorts is a string with resorts delimited by commas
	//same logic with addMailSub() method in MailService
	public boolean addVote(String ip, String resorts);  
}
