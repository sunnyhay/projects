package org.lvyouzaike.service.admin;

import java.util.Date;
import java.util.List;

import org.lvyouzaike.model.VoteTempleRecord;

public interface VoteTempleRecordAdminService {
	public boolean checkVoteTempleRecord(String ip);  //check if a vote record exists with this ip in List Temple
	public boolean deleteVoteTempleRecord(String ip);  //delete a vote record by ip
	public VoteTempleRecord getById(int id);  //get by id
	public VoteTempleRecord getByIp(String ip);  //get by ip
	public List<VoteTempleRecord> getAll();  //get all
	public List<VoteTempleRecord> getByDate(Date from, Date to);
}
