package org.lvyouzaike.service.admin.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.lvyouzaike.dao.VoteTempleRecordDao;
import org.lvyouzaike.model.VoteTempleRecord;
import org.lvyouzaike.service.admin.VoteTempleRecordAdminService;
import org.springframework.stereotype.Component;

@Component("voteTempleRecordAdminService")
public class VoteTempleRecordAdminServiceImpl implements
		VoteTempleRecordAdminService {
	private VoteTempleRecordDao dao;

	public VoteTempleRecordDao getDao() {
		return dao;
	}

	@Resource
	public void setDao(VoteTempleRecordDao dao) {
		this.dao = dao;
	}

	@Override
	public boolean checkVoteTempleRecord(String ip) {
		return dao.check(ip);
	}

	@Override
	public boolean deleteVoteTempleRecord(String ip) {
		VoteTempleRecord record = dao.getByIp(ip);
		return dao.delete(record);
	}

	@Override
	public VoteTempleRecord getById(int id) {
		return dao.getById(id);
	}

	@Override
	public VoteTempleRecord getByIp(String ip) {
		return dao.getByIp(ip);
	}

	@Override
	public List<VoteTempleRecord> getAll() {
		return dao.getAll();
	}

	@Override
	public List<VoteTempleRecord> getByDate(Date from, Date to) {
		return dao.getByDate(from, to);
	}

}
