package org.lvyouzaike.dao;

import java.util.Date;
import java.util.List;

import org.lvyouzaike.model.VoteTempleRecord;

public interface VoteTempleRecordDao {
	public boolean check(String ip);  //check if a vote record exists with this ip in List Temple
	public boolean add(VoteTempleRecord record);  //add a vote record
	public boolean delete(VoteTempleRecord record);  //delete a vote record
	public boolean update(VoteTempleRecord record);  //update a vote record
	public VoteTempleRecord getById(int id);  //get by id
	public VoteTempleRecord getByIp(String ip);  //get by ip
	public List<VoteTempleRecord> getAll();  //get all
	public List<VoteTempleRecord> getByDate(Date from, Date to);
}
