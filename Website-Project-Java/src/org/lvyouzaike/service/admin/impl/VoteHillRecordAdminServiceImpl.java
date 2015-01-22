package org.lvyouzaike.service.admin.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.lvyouzaike.dao.VoteHillRecordDao;
import org.lvyouzaike.model.VoteHillRecord;
import org.lvyouzaike.service.admin.VoteHillRecordAdminService;
import org.springframework.stereotype.Component;

@Component("voteHillRecordAdminService")
public class VoteHillRecordAdminServiceImpl implements
		VoteHillRecordAdminService {
	private VoteHillRecordDao dao;

	public VoteHillRecordDao getDao() {
		return dao;
	}

	@Resource
	public void setDao(VoteHillRecordDao dao) {
		this.dao = dao;
	}

	@Override
	public boolean checkVoteHillRecord(String ip) {
		return dao.check(ip);
	}

	@Override
	public boolean deleteVoteHillRecord(String ip) {
		VoteHillRecord record = dao.getByIp(ip);
		return dao.delete(record);
	}

	@Override
	public VoteHillRecord getById(int id) {
		return dao.getById(id);
	}

	@Override
	public VoteHillRecord getByIp(String ip) {
		return dao.getByIp(ip);
	}

	@Override
	public List<VoteHillRecord> getAll() {
		return dao.getAll();
	}

	@Override
	public List<VoteHillRecord> getByDate(Date from, Date to) {
		return dao.getByDate(from, to);
	}

}
