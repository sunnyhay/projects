package org.lvyouzaike.dao;

import java.util.Date;
import java.util.List;

import org.lvyouzaike.model.VoteHillRecord;

public interface VoteHillRecordDao {
	public boolean check(String ip);  //check if a vote record exists with this ip in List Hill
	public boolean add(VoteHillRecord record);  //add a vote record
	public boolean delete(VoteHillRecord record);  //delete a vote record
	public boolean update(VoteHillRecord record);  //update a vote record
	public VoteHillRecord getById(int id);  //get by id
	public VoteHillRecord getByIp(String ip);  //get by ip
	public List<VoteHillRecord> getAll();  //get all
	public List<VoteHillRecord> getByDate(Date from, Date to);
}
