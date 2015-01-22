package org.lvyouzaike.service.admin.impl;

import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import org.lvyouzaike.dao.VoteHillDao;
import org.lvyouzaike.model.VoteHill;
import org.lvyouzaike.service.admin.VoteHillAdminService;
import org.lvyouzaike.util.VoteHillComparator;
import org.springframework.stereotype.Component;

@Component("voteHillAdminService")
public class VoteHillAdminServiceImpl implements VoteHillAdminService {
	private VoteHillDao dao;
	
	public VoteHillDao getDao() {
		return dao;
	}

	@Resource
	public void setDao(VoteHillDao dao) {
		this.dao = dao;
	}

	@Override
	public boolean checkHill(String name) {
		return dao.check(name);
	}

	@Override
	public boolean addHill(String name, String description) {
		VoteHill hill = new VoteHill();
		
		hill.setName(name);
		hill.setDescription(description);
		
		return dao.add(hill);
	}

	@Override
	public boolean updateHillDesc(String name, String description) {
		VoteHill hill = dao.getByName(name);
		
		hill.setDescription(description);
		
		return dao.update(hill);
	}

	@Override
	public VoteHill getById(int id) {
		return dao.getById(id);
	}

	@Override
	public VoteHill getByName(String name) {
		return dao.getByName(name);
	}

	@Override
	public List<VoteHill> getHillsOrderByVotes() {
		List<VoteHill> hills = dao.getAll();
		
		Collections.sort(hills, new VoteHillComparator());
		
		return hills;
	}

	@Override
	public List<VoteHill> getAllHills() {
		return dao.getAll();
	}

}
