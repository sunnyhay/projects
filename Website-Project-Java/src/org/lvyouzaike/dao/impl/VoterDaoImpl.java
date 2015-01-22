package org.lvyouzaike.dao.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.lvyouzaike.dao.VoterDao;
import org.lvyouzaike.model.Voter;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Component;

@Component("voterDao")
public class VoterDaoImpl implements VoterDao {
	private HibernateTemplate ht;

	public HibernateTemplate getHt() {
		return ht;
	}

	@Resource
	public void setHt(HibernateTemplate ht) {
		this.ht = ht;
	}
	
	@Override
	public boolean add(Voter voter) {
		@SuppressWarnings("unchecked")
		List<Voter> voters = ht.find("from Voter v where v.ip = '" + voter.getIp() + "'");
		if(voters != null && voters.size() > 0)
			return false;
		ht.merge(voter);
		return true;
	}

	@Override
	public boolean update(Voter voter) {
		ht.update(voter);
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Voter> getAll() {
		return (List<Voter>) ht.find("from Voter");
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Voter> getAfter(Date from, Date to) {
		return (List<Voter>) ht.find("from Voter v where v.voteDate > ? and v.voteDate < ?", from, to);
	}
	
	@Override
	public Voter getByIp(String ip){
		@SuppressWarnings("unchecked")
		List<Voter> voters = ht.find("from Voter v where v.ip = '" + ip + "'");
		if(voters != null && voters.size() > 0)
			return voters.get(0);
		return null;
	}

	@Override
	public boolean delete(Voter voter) {
		ht.delete(voter);
		return true;
	}

}
