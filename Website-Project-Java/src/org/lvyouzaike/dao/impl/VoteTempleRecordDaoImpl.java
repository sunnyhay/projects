package org.lvyouzaike.dao.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.lvyouzaike.dao.VoteTempleRecordDao;
import org.lvyouzaike.model.VoteTempleRecord;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Component;

@Component("voteTempleRecordDao")
public class VoteTempleRecordDaoImpl implements VoteTempleRecordDao {
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
		List<VoteTempleRecord> records = ht.find("from VoteTempleRecord r where r.ip = '" + ip + "'");
		if(records != null && records.size() > 0)
			return true;
		return false;
	}

	@Override
	public boolean add(VoteTempleRecord record) {
		ht.merge(record);
		return true;
	}

	@Override
	public boolean delete(VoteTempleRecord record) {
		ht.delete(record);
		return true;
	}

	@Override
	public boolean update(VoteTempleRecord record) {
		ht.update(record);
		return true;
	}

	@Override
	public VoteTempleRecord getById(int id) {
		return ht.get(VoteTempleRecord.class, id);
	}

	@Override
	public VoteTempleRecord getByIp(String ip) {
		@SuppressWarnings("unchecked")
		List<VoteTempleRecord> records = ht.find("from VoteTempleRecord r where r.ip = '" + ip + "'");
		if(records != null && records.size() > 0){
			return records.get(0);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<VoteTempleRecord> getAll() {
		return ht.find("from VoteTempleRecord");
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<VoteTempleRecord> getByDate(Date from, Date to) {
		return (List<VoteTempleRecord>) ht.find("from VoteTempleRecord r where r.voteDate > ? and r.voteDate < ?", from, to);
	}

}
