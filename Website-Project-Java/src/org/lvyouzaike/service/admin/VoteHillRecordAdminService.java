package org.lvyouzaike.service.admin;

import java.util.Date;
import java.util.List;

import org.lvyouzaike.model.VoteHillRecord;

public interface VoteHillRecordAdminService {
	public boolean checkVoteHillRecord(String ip);  //check if a vote record exists with this ip in List Hill
	public boolean deleteVoteHillRecord(String ip);  //delete a vote record by ip
	public VoteHillRecord getById(int id);  //get by id
	public VoteHillRecord getByIp(String ip);  //get by ip
	public List<VoteHillRecord> getAll();  //get all
	public List<VoteHillRecord> getByDate(Date from, Date to);
}
