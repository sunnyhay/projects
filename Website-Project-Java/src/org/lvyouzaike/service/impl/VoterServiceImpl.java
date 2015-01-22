package org.lvyouzaike.service.impl;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Resource;

import org.lvyouzaike.dao.ResortDao;
import org.lvyouzaike.dao.VoterDao;
import org.lvyouzaike.model.Resort;
import org.lvyouzaike.model.Voter;
import org.lvyouzaike.service.VoterService;
import org.springframework.stereotype.Component;

@Component("voterService")
public class VoterServiceImpl implements VoterService {
	private VoterDao dao;
	private ResortDao resortDao;
	
	public VoterDao getDao() {
		return dao;
	}

	@Resource
	public void setDao(VoterDao dao) {
		this.dao = dao;
	}

	public ResortDao getResortDao() {
		return resortDao;
	}

	@Resource(name="resortDao")
	public void setResortDao(ResortDao resortDao) {
		this.resortDao = resortDao;
	}

	@Override
	public boolean addVote(String ip, String resorts) {
		Voter v = dao.getByIp(ip);
		
		if(v == null){//a new vote record
			v = new Voter();
			Set<Resort> rSet = new HashSet<Resort>();
			
			v.setIp(ip);
			v.setVoteDate(new Date());
			
			if(resorts == null){//must be impossible
				return false;
			}else{
				String r[] = resorts.split(",");
				for(String resort: r){
					Resort rt = resortDao.getByName(resort);
					rSet.add(rt);
					rt.getVoters().add(v);
				}
				v.setResorts(rSet);
			}		
			return dao.add(v);
		}else{//update the user's vote record with new resorts if this is not the voter's first vote
			Set<Resort> rSet = v.getResorts();
			if(!rSet.isEmpty()){//clear previous non-empty vote-related resorts
				for(Resort r: rSet){
					r.getVoters().remove(v);  //delete each resort's this vote record
				}
				v.getResorts().clear();  //clear this vote record's customized resorts
			}
			if(resorts == null){//impossible
				return false;
			}else{
				Set<Resort> reSet = new HashSet<Resort>();
				String r[] = resorts.split(",");
				for(String resort: r){
					Resort rt = resortDao.getByName(resort);
					reSet.add(rt);
					rt.getVoters().add(v);
				}
				v.setResorts(reSet);
			}
		}	
		return dao.update(v);
	}
}
