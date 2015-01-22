package org.lvyouzaike.service.admin.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.lvyouzaike.dao.CommentDao;
import org.lvyouzaike.model.CommentModel;
import org.lvyouzaike.service.admin.CommentAdminService;
import org.springframework.stereotype.Component;

@Component("commentMuseumAdminService")
public class CommentMuseumAdminServiceImpl implements CommentAdminService {
	private CommentDao dao;

	public CommentDao getDao() {
		return dao;
	}

	@Resource(name="commentMuseumDao")
	public void setDao(CommentDao dao) {
		this.dao = dao;
	}
	
	@Override
	public boolean delete(int id) {
		dao.delete(id);
		return true;
	}

	@Override
	public boolean deleteMany(int fromId, int toId) {
		dao.deleteMany(fromId, toId);
		return true;
	}

	@Override
	public List<CommentModel> getAll() {
		return dao.getAll();
	}

	@Override
	public List<CommentModel> getMany(Date from, Date to) {
		return dao.getMany(from, to);
	}

	@Override
	public CommentModel getById(int id) {
		return dao.get(id);
	}

}
