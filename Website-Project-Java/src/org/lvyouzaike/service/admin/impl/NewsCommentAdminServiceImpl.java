package org.lvyouzaike.service.admin.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.lvyouzaike.dao.NewsCommentDao;
import org.lvyouzaike.model.NewsComment;
import org.lvyouzaike.service.admin.NewsCommentAdminService;
import org.springframework.stereotype.Component;

@Component("newsCommentAdminService")
public class NewsCommentAdminServiceImpl implements NewsCommentAdminService {
	private NewsCommentDao dao;	

	public NewsCommentDao getDao() {
		return dao;
	}

	@Resource
	public void setDao(NewsCommentDao dao) {
		this.dao = dao;
	}
	
	@Override
	public boolean deleteNewsComment(int id) {
		return dao.delete(id);
	}

	@Override
	public List<NewsComment> getAllNewsComments() {
		return dao.getAll();
	}

	@Override
	public List<NewsComment> getNewsCommentsForName(String name) {
		return dao.getForSameName(name);
	}

	@Override
	public List<NewsComment> getNewsCommentsForNameByDate(String name,
			Date from, Date to) {
		return dao.getManyForSameName(name, from, to);
	}

	@Override
	public List<NewsComment> getNewsCommentsForIp(String ip) {
		return dao.getForSameIp(ip);
	}

}
