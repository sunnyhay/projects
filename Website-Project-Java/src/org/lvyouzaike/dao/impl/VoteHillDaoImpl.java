package org.lvyouzaike.dao.impl;

import java.util.List;

import javax.annotation.Resource;

import org.lvyouzaike.dao.VoteHillDao;
import org.lvyouzaike.model.VoteHill;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Component;

@Component("voteHillDao")
public class VoteHillDaoImpl implements VoteHillDao {
	private HibernateTemplate ht;

	public HibernateTemplate getHt() {
		return ht;
	}

	@Resource
	public void setHt(HibernateTemplate ht) {
		this.ht = ht;
	}
	
	@Override
	public boolean check(String name) {
		@SuppressWarnings("unchecked")
		List<VoteHill> hills = ht.find("from VoteHill h where h.name = '" + name + "'");
		if(hills != null && hills.size() > 0)
			return true;
		return false;
	}

	@Override
	public boolean add(VoteHill hill) {
		if(!check(hill.getName())){
			ht.merge(hill);
			return true;
		}			
		return false;
	}

	@Override
	public boolean update(VoteHill hill) {
		ht.update(hill);
		return true;
	}

	@Override
	public VoteHill getById(int id) {
		return ht.get(VoteHill.class, id);
	}

	@Override
	public VoteHill getByName(String name) {
		@SuppressWarnings("unchecked")
		List<VoteHill> hills = ht.find("from VoteHill h where h.name = '" + name + "'");
		if(hills != null && hills.size() > 0){
			return hills.get(0);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<VoteHill> getAll() {
		return ht.find("from VoteHill");
	}

}
