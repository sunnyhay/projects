package org.lvyouzaike.service.admin;

import java.util.Date;
import java.util.List;

import org.lvyouzaike.model.VoteAdvice;

public interface VoteAdviceAdminService {
	public boolean deleteVoteAdvice(int id);  //delete a vote advice by id
	public List<VoteAdvice> getAll();  //get all the advices
	public List<VoteAdvice> getAfter(Date current);  //get all the advices submitted after current time
}
