package org.lvyouzaike.dao.impl;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.lvyouzaike.dao.CommentDao;
import org.lvyouzaike.model.CommentCity;
import org.lvyouzaike.model.CommentModel;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Component;

/*
 * hibernate implementation of CommentDao using HibernateTemplate util
 * The generic CommentModel object should be materialized to CommentCity object
 */
@Component("commentCityDao")
public class CommentCityDaoImpl implements CommentDao{
	private HibernateTemplate ht;

	public HibernateTemplate getHt() {
		return ht;
	}

	@Resource
	public void setHt(HibernateTemplate ht) {
		this.ht = ht;
	}
	
	@Override
	public boolean add(CommentModel comment) {
		ht.save(comment);
		return true;
	}

	@Override
	public boolean delete(int id) {
		CommentCity comment = new CommentCity();
		comment.setId(id);
		ht.delete(comment);
		return true;		
	}

	/*
	 * (non-Javadoc)
	 * @see org.lvyouzaike.dao.CommentDao#deleteMany(int, int)
	 * valid only if any id in <fromId,toId> exists.
	 */
	@Override
	public boolean deleteMany(int fromId, int toId) {
		for(int i = fromId; i <= toId; i++){
			CommentCity comment = new CommentCity();
			comment.setId(i);
			ht.delete(comment);
		}
		return true;
	}

	@Override
	public List<CommentModel> getAll() {
		return (List<CommentModel>) getAllCityComments();
	}
	
	/*
	 * this getAllDownloadComments() refers to page 711 in spring reference book.
	 */
	@SuppressWarnings("unchecked")
	public Collection<CommentModel> getAllCityComments(){
		return this.ht.find("from CommentCity");
	}

	@Override
	public List<CommentModel> getMany(Date from, Date to) {
		return (List<CommentModel>) getManyByDateDiff(from, to);
	}
	
	@SuppressWarnings("unchecked")
	public Collection<CommentModel> getManyByDateDiff(Date from, Date to){
		return this.ht.find("from CommentCity c where c.commentDate > ? and c.commentDate < ?", from, to);
	}

	@Override
	public CommentModel get(int id) {
		return ht.get(CommentCity.class, id);
	}

}
