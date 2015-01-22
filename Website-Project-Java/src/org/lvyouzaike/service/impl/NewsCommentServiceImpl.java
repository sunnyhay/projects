package org.lvyouzaike.service.impl;

import java.util.Date;

import javax.annotation.Resource;

import org.lvyouzaike.dao.NewsCommentDao;
import org.lvyouzaike.model.NewsComment;
import org.lvyouzaike.service.NewsCommentService;
import org.springframework.stereotype.Component;

@Component("newsCommentService")
public class NewsCommentServiceImpl implements NewsCommentService {
	private NewsCommentDao dao;	

	public NewsCommentDao getDao() {
		return dao;
	}

	@Resource
	public void setDao(NewsCommentDao dao) {
		this.dao = dao;
	}

	@Override
	public boolean addNewsComment(String name, String ip, String nickname,
			String comment) {
		NewsComment c = new NewsComment();
		
		c.setName(name);
		c.setIp(ip);
		c.setNickname(nickname);
		c.setComment(comment);
		c.setCommentDate(new Date());
		
		return dao.add(c);
	}

}
