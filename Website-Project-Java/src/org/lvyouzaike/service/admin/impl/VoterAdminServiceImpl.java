package org.lvyouzaike.service.admin.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.lvyouzaike.dao.VoterDao;
import org.lvyouzaike.model.Voter;
import org.lvyouzaike.service.admin.VoterAdminService;
import org.springframework.stereotype.Component;

@Component("voterAdminService")
public class VoterAdminServiceImpl implements VoterAdminService {
	private VoterDao dao;

	public VoterDao getDao() {
		return dao;
	}

	@Resource
	public void setDao(VoterDao dao) {
		this.dao = dao;
	}

	@Override
	public Voter getByIp(String ip) {
		return dao.getByIp(ip);
	}
	
	@Override
	public List<Voter> getAll() {
		return dao.getAll();
	}

	@Override
	public List<Voter> getAfter(Date from, Date to) {
		return dao.getAfter(from, to);
	}

	@Override
	public boolean delete(String ip) {
		return dao.delete(dao.getByIp(ip));
	}

}
