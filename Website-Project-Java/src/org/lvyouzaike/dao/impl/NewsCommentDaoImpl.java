package org.lvyouzaike.dao.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.lvyouzaike.dao.NewsCommentDao;
import org.lvyouzaike.model.NewsComment;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Component;

@Component("newsCommentDao")
public class NewsCommentDaoImpl implements NewsCommentDao {
	private HibernateTemplate ht;

	public HibernateTemplate getHt() {
		return ht;
	}

	@Resource
	public void setHt(HibernateTemplate ht) {
		this.ht = ht;
	}
	
	@Override
	public boolean add(NewsComment comment) {
		ht.save(comment);
		return true;
	}

	@Override
	public boolean delete(int id) {
		NewsComment c = ht.get(NewsComment.class, id);
		
		if(c != null){
			c.setId(id);
			ht.delete(c);
			return true;
		}		
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<NewsComment> getAll() {
		return ht.find("from NewsComment");
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<NewsComment> getForSameName(String name) {
		return ht.find("from NewsComment c where c.name = '" + name + "' order by c.commentDate");
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<NewsComment> getManyForSameName(String name, Date from, Date to) {
		return ht.find("from NewsComment c where c.name = ? and c.commentDate < ? and c.commentDate > ? order by c.commentDate", name, from, to);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<NewsComment> getForSameIp(String ip) {
		return ht.find("from NewsComment c where c.ip = '" + ip + "'");
	}

}
