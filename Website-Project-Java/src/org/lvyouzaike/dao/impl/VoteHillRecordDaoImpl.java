package org.lvyouzaike.dao.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.lvyouzaike.dao.VoteHillRecordDao;
import org.lvyouzaike.model.VoteHillRecord;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Component;

@Component("voteHillRecordDao")
public class VoteHillRecordDaoImpl implements VoteHillRecordDao {
	private HibernateTemplate ht;

	public HibernateTemplate getHt() {
		return ht;
	}

	@Resource
	public void setHt(HibernateTemplate ht) {
		this.ht = ht;
	}

	@Override
	public boolean check(String ip) {
		@SuppressWarnings("unchecked")
		List<VoteHillRecord> records = ht.find("from VoteHillRecord r where r.ip = '" + ip + "'");
		if(records != null && records.size() > 0)
			return true;
		return false;
	}

	@Override
	public boolean add(VoteHillRecord record) {
		ht.merge(record);
		return true;
	}

	@Override
	public boolean delete(VoteHillRecord record) {
		ht.delete(record);
		return true;
	}

	@Override
	public boolean update(VoteHillRecord record) {
		ht.update(record);
		return true;
	}

	@Override
	public VoteHillRecord getById(int id) {
		return ht.get(VoteHillRecord.class, id);
	}

	@Override
	public VoteHillRecord getByIp(String ip) {
		@SuppressWarnings("unchecked")
		List<VoteHillRecord> records = ht.find("from VoteHillRecord r where r.ip = '" + ip + "'");
		if(records != null && records.size() > 0){
			return records.get(0);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<VoteHillRecord> getAll() {
		return ht.find("from VoteHillRecord");
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<VoteHillRecord> getByDate(Date from, Date to) {
		return (List<VoteHillRecord>) ht.find("from VoteHillRecord r where r.voteDate > ? and r.voteDate < ?", from, to);
	}

}
