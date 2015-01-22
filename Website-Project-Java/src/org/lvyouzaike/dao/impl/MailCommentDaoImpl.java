package org.lvyouzaike.dao.impl;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.lvyouzaike.dao.MailCommentDao;
import org.lvyouzaike.model.MailComment;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Component;

/*
 * hibernate implementation of CommentDao using HibernateTemplate util
 */
@Component("mailCommentDao")
public class MailCommentDaoImpl implements MailCommentDao{
	private HibernateTemplate ht;

	public HibernateTemplate getHt() {
		return ht;
	}

	@Resource
	public void setHt(HibernateTemplate ht) {
		this.ht = ht;
	}
	
	@Override
	public boolean add(MailComment comment) {
		ht.save(comment);
		return true;
	}

	@Override
	public boolean delete(int id) {
		MailComment comment = new MailComment();
		comment.setId(id);
		ht.delete(comment);
		return true;
	}

	@Override
	public List<MailComment> getAll() {
		return (List<MailComment>) getAllMailComments();
	}
	
	/*
	 * this refers to page 711 in spring reference book.
	 */
	@SuppressWarnings("unchecked")
	public Collection<MailComment> getAllMailComments(){
		return this.ht.find("from MailComment");
	}

	@Override
	public List<MailComment> get(Date current) {
		return (List<MailComment>) getByDateDiff(current);
	}
		
	@SuppressWarnings("unchecked")
	public Collection<MailComment> getByDateDiff(Date current){
		return this.ht.find("from MailComment c where c.unsubDate > ?", current);
	}

}
