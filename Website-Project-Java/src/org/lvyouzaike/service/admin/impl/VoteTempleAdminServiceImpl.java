package org.lvyouzaike.service.admin.impl;

import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import org.lvyouzaike.dao.VoteTempleDao;
import org.lvyouzaike.model.VoteTemple;
import org.lvyouzaike.service.admin.VoteTempleAdminService;
import org.lvyouzaike.util.VoteTempleComparator;
import org.springframework.stereotype.Component;

@Component("voteTempleAdminService")
public class VoteTempleAdminServiceImpl implements VoteTempleAdminService {
	private VoteTempleDao dao;

	public VoteTempleDao getDao() {
		return dao;
	}

	@Resource
	public void setDao(VoteTempleDao dao) {
		this.dao = dao;
	}

	@Override
	public boolean checkTemple(String name) {
		return dao.check(name);
	}

	@Override
	public boolean addTemple(String name, String description) {
		VoteTemple t = new VoteTemple();
		
		t.setName(name);
		t.setDescription(description);
		
		return dao.add(t);
	}

	@Override
	public boolean updateTempleDesc(String name, String description) {
		VoteTemple t = dao.getByName(name);
		
		t.setDescription(description);
		
		return dao.update(t);
	}

	@Override
	public VoteTemple getById(int id) {
		return dao.getById(id);
	}

	@Override
	public VoteTemple getByName(String name) {
		return dao.getByName(name);
	}

	@Override
	public List<VoteTemple> getTemplesOrderByVotes() {
		List<VoteTemple> temples = dao.getAll();
		
		Collections.sort(temples, new VoteTempleComparator());
		
		return temples;
	}

	@Override
	public List<VoteTemple> getAllTemples() {
		return dao.getAll();
	}

}
