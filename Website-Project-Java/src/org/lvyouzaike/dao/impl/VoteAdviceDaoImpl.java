package org.lvyouzaike.dao.impl;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.lvyouzaike.dao.VoteAdviceDao;
import org.lvyouzaike.model.VoteAdvice;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Component;

/*
 * hibernate implementation of CommentDao using HibernateTemplate util
 */
@Component("voteAdviceDao")
public class VoteAdviceDaoImpl implements VoteAdviceDao{
	private HibernateTemplate ht;

	public HibernateTemplate getHt() {
		return ht;
	}

	@Resource
	public void setHt(HibernateTemplate ht) {
		this.ht = ht;
	}
	
	@Override
	public boolean add(VoteAdvice advice) {
		ht.save(advice);
		return true;
	}

	@Override
	public boolean delete(int id) {
		VoteAdvice advice = new VoteAdvice();
		advice.setId(id);
		ht.delete(advice);
		return true;
	}

	@Override
	public List<VoteAdvice> getAll() {
		return (List<VoteAdvice>) getAllVoteAdvice();
	}
	
	/*
	 * this refers to page 711 in spring reference book.
	 */
	@SuppressWarnings("unchecked")
	public Collection<VoteAdvice> getAllVoteAdvice(){
		return this.ht.find("from VoteAdvice");
	}

	@Override
	public List<VoteAdvice> getAfter(Date current) {
		return (List<VoteAdvice>) getByDateDiff(current);
	}
		
	@SuppressWarnings("unchecked")
	public Collection<VoteAdvice> getByDateDiff(Date current){
		return this.ht.find("from VoteAdvice c where c.adviceDate > ?", current);
	}

}
