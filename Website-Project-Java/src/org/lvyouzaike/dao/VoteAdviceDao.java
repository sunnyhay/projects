package org.lvyouzaike.dao;

import java.util.Date;
import java.util.List;

import org.lvyouzaike.model.VoteAdvice;

public interface VoteAdviceDao {
	public boolean add(VoteAdvice advice);  //add a new vote advice
	public boolean delete(int id);  //delete a vote advice by id
	public List<VoteAdvice> getAll();  //get all the advices
	public List<VoteAdvice> getAfter(Date current);  //get all the advices submitted after current time
}
