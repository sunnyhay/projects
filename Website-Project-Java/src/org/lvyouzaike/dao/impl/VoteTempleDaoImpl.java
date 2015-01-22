package org.lvyouzaike.dao.impl;

import java.util.List;

import javax.annotation.Resource;

import org.lvyouzaike.dao.VoteTempleDao;
import org.lvyouzaike.model.VoteTemple;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Component;

@Component("voteTempleDao")
public class VoteTempleDaoImpl implements VoteTempleDao {
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
		List<VoteTemple> temples = ht.find("from VoteTemple t where t.name = '" + name + "'");
		if(temples != null && temples.size() > 0)
			return true;
		return false;
	}

	@Override
	public boolean add(VoteTemple temple) {
		if(!check(temple.getName())){
			ht.merge(temple);
			return true;
		}			
		return false;
	}

	@Override
	public boolean update(VoteTemple temple) {
		ht.update(temple);
		return true;
	}

	@Override
	public VoteTemple getById(int id) {
		return ht.get(VoteTemple.class, id);
	}

	@Override
	public VoteTemple getByName(String name) {
		@SuppressWarnings("unchecked")
		List<VoteTemple> temples = ht.find("from VoteTemple t where t.name = '" + name + "'");
		if(temples != null && temples.size() > 0){
			return temples.get(0);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<VoteTemple> getAll() {
		return ht.find("from VoteTemple");
	}

}
