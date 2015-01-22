package org.lvyouzaike.service.impl;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Resource;

import org.lvyouzaike.dao.VoteTempleDao;
import org.lvyouzaike.dao.VoteTempleRecordDao;
import org.lvyouzaike.model.VoteTemple;
import org.lvyouzaike.model.VoteTempleRecord;
import org.lvyouzaike.service.VoteTempleRecordService;
import org.springframework.stereotype.Component;

@Component("voteTempleRecordService")
public class VoteTempleRecordServiceImpl implements VoteTempleRecordService {
	private VoteTempleRecordDao dao;
	private VoteTempleDao voteTempleDao;
	
	public VoteTempleRecordDao getDao() {
		return dao;
	}

	@Resource
	public void setDao(VoteTempleRecordDao dao) {
		this.dao = dao;
	}

	public VoteTempleDao getVoteTempleDao() {
		return voteTempleDao;
	}

	@Resource(name="voteTempleDao")
	public void setVoteTempleDao(VoteTempleDao voteTempleDao) {
		this.voteTempleDao = voteTempleDao;
	}


	@Override
	public boolean add(String ip, String temples) {
		VoteTempleRecord record = dao.getByIp(ip);
		
		if(record == null){//a new vote temple record
			record = new VoteTempleRecord();
			Set<VoteTemple> tSet = new HashSet<VoteTemple>();
			
			record.setIp(ip);
			record.setVoteDate(new Date());
			
			if(temples == null){//must be impossible
				return false;
			}else{
				String t[] = temples.split(",");
				for(String temple: t){
					VoteTemple voteTemple = voteTempleDao.getByName(temple);
					tSet.add(voteTemple);
					voteTemple.getVoteTempleRecord().add(record);
				}
				record.setVoteTemple(tSet);
			}		
			return dao.add(record);
		}else{//update the user's vote temple record if this is not the voter's first vote for the temple list
			Set<VoteTemple> tSet = record.getVoteTemple();
			if(!tSet.isEmpty()){//clear previous non-empty vote-related records
				for(VoteTemple voteTemple: tSet){
					voteTemple.getVoteTempleRecord().remove(record);  //delete each temple's this vote record
				}
				record.getVoteTemple().clear();  //clear this vote record's voted temples
			}
			if(temples == null){//impossible
				return false;
			}else{
				Set<VoteTemple> teSet = new HashSet<VoteTemple>();
				String t[] = temples.split(",");
				for(String temple: t){
					VoteTemple voteTemple = voteTempleDao.getByName(temple);
					teSet.add(voteTemple);
					voteTemple.getVoteTempleRecord().add(record);
				}
				record.setVoteTemple(teSet);
			}
		}	
		return dao.update(record);

	}

}
