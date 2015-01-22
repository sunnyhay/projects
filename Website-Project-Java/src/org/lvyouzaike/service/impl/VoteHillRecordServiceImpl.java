package org.lvyouzaike.service.impl;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Resource;

import org.lvyouzaike.dao.VoteHillDao;
import org.lvyouzaike.dao.VoteHillRecordDao;
import org.lvyouzaike.model.VoteHill;
import org.lvyouzaike.model.VoteHillRecord;
import org.lvyouzaike.service.VoteHillRecordService;
import org.springframework.stereotype.Component;

@Component("voteHillRecordService")
public class VoteHillRecordServiceImpl implements VoteHillRecordService {
	private VoteHillRecordDao dao;
	private VoteHillDao voteHillDao;
	
	public VoteHillRecordDao getDao() {
		return dao;
	}

	@Resource
	public void setDao(VoteHillRecordDao dao) {
		this.dao = dao;
	}

	public VoteHillDao getVoteHillDao() {
		return voteHillDao;
	}

	@Resource(name="voteHillDao")
	public void setVoteHillDao(VoteHillDao voteHillDao) {
		this.voteHillDao = voteHillDao;
	}

	@Override
	public boolean add(String ip, String hills) {
		VoteHillRecord record = dao.getByIp(ip);
		
		if(record == null){//a new vote hill record
			record = new VoteHillRecord();
			Set<VoteHill> hSet = new HashSet<VoteHill>();
			
			record.setIp(ip);
			record.setVoteDate(new Date());
			
			if(hills == null){//must be impossible
				return false;
			}else{
				String h[] = hills.split(",");
				for(String hill: h){
					VoteHill voteHill = voteHillDao.getByName(hill);
					hSet.add(voteHill);
					voteHill.getVoteHillRecord().add(record);
				}
				record.setVoteHill(hSet);
			}		
			return dao.add(record);
		}else{//update the user's vote hill record if this is not the voter's first vote for the hill list
			Set<VoteHill> hSet = record.getVoteHill();
			if(!hSet.isEmpty()){//clear previous non-empty vote-related records
				for(VoteHill voteHill: hSet){
					voteHill.getVoteHillRecord().remove(record);  //delete each hill's this vote record
				}
				record.getVoteHill().clear();  //clear this vote record's voted hills
			}
			if(hills == null){//impossible
				return false;
			}else{
				Set<VoteHill> hiSet = new HashSet<VoteHill>();
				String h[] = hills.split(",");
				for(String hill: h){
					VoteHill voteHill = voteHillDao.getByName(hill);
					hiSet.add(voteHill);
					voteHill.getVoteHillRecord().add(record);
				}
				record.setVoteHill(hiSet);
			}
		}	
		return dao.update(record);

	}

}
